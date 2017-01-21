package org.papaorange.amoviesprider.service;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.papaorange.utils.DBAgent;
import org.papaorange.utils.Utils;

import java.util.List;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by papaorange on 2016/11/22.
 */
public class DoubanDownloaderBFS
{
    private String seedUrl = "";
    private DBAgent agent = null;
    private LinkedList<String> bfsQ = new LinkedList<>();
    private Map<String, Object> existMvMap = null;
    private Map<String, Object> ignoreMvMap = new HashMap<String, Object>();
    private static final Logger log = Logger.getLogger(DoubanDownloaderBFS.class);
    private int newCollectCount = 0;

    public Document download(String url) throws IOException
    {
	log.debug("Download URL:" + url);
	return Jsoup.connect(url).header("Accept", "text/html").header("Accept-Charset", "utf-8")
		.header("Accept-Encoding", "gzip").header("Accept-Language", "en-US,en")
		.header("User-Agent",
			"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.22 (KHTML, like Gecko) Chrome/25.0.1364.160 Safari/537.22")
		.timeout(0).get();
    }

    DoubanDownloaderBFS(String seedUrl, DBAgent agent)
    {
	this.seedUrl = seedUrl;
	this.agent = agent;
	this.existMvMap = agent.getAllDocumentsKey("good", "url");
	this.ignoreMvMap = agent.getAllDocumentsKey("ignore", "url");
    }

    public void collectBFS()
    {
	bfsQ.add(seedUrl);
	List<String> childUrl = processOneMovie(seedUrl);
	while (true)
	{
	    for (String child : childUrl)
	    {
		if (!existMvMap.containsKey(child) && !ignoreMvMap.containsKey(child))
		{
		    bfsQ.add(child);
		    ignoreMvMap.put(child, "");
		    Hashtable<String, String> ignoreItem = new Hashtable<>();
		    ignoreItem.put("url", child);
		    agent.addOneDocument(ignoreItem, "ignore");
		}
	    }

	    String head = bfsQ.removeFirst();
	    if (!existMvMap.containsKey(head))
	    {
		childUrl = processOneMovie(head);
	    }

	    if (bfsQ.size() == 0)
	    {
		break;
	    }
	    log.info("bfsQ length:" + bfsQ.size());
	}
    }

    private List<String> processOneMovie(String url)
    {

	Document document = null;
	List<String> childs = new ArrayList<>();
	try
	{
	    Thread.sleep(500);
	    document = this.download(url);
	}
	catch (IOException e)
	{
	    // e.printStackTrace();
	    if (e.toString().contains("Status=403"))
	    {
		log.error("致命错误,页面禁止被访问,403!");
	    }
	    return childs;
	}
	catch (InterruptedException e)
	{
	    return childs;
	}
	if (!existMvMap.containsKey(url) && !ignoreMvMap.containsKey(url))
	{
	    String yearStr = null;
	    String rateNumberStr = null;
	    String rateValueStr = null;
	    if (document.getElementsByClass("year").size() > 0)
	    {
		yearStr = Utils.matchYear(document.getElementsByClass("year").get(0).text());
	    }
	    else
	    {
		yearStr = Utils.matchYear(
			document.getElementsByAttributeValue("property", "v:initialReleaseDate").text().trim());
	    }
	    int year = yearStr.equals("") ? 0 : Integer.parseInt(yearStr);

	    if (document.getElementsByAttributeValue("property", "v:votes").size() > 0)
	    {
		rateNumberStr = document.getElementsByAttributeValue("property", "v:votes").text();
		if (rateNumberStr.equals(""))
		{
		    rateNumberStr = "0";
		}
	    }
	    else
	    {
		rateNumberStr = "0";
	    }
	    int rateNumber = Integer.parseInt(rateNumberStr);

	    if (document.getElementsByAttributeValue("property", "v:average").size() > 0)
	    {
		rateValueStr = document.getElementsByAttributeValue("property", "v:average").text();
		if (rateValueStr.equals(""))
		{
		    rateValueStr = "0";
		}
	    }
	    else
	    {
		rateValueStr = "0";
	    }
	    float ratevalue = Float.parseFloat(rateValueStr);

	    if (year < 2000)
	    {
		log.info("2000年以前电影，忽略。。。");
		this.ignoreMvMap.put(url, "");
	    }
	    else if (rateNumber < 5000)
	    {
		log.info("投票人数少于5000，忽略。。。");
		this.ignoreMvMap.put(url, "");
	    }
	    else if (ratevalue < 7)
	    {
		log.info("评分低于7分，忽略。。。");
		this.ignoreMvMap.put(url, "");
	    }
	    else
	    {
		Hashtable<String, String> movieItem = new Hashtable<>();
		// v:itemreviewed
		String name = "";
		if (document.getElementsByAttributeValue("property", "v:itemreviewed").size() > 0)
		{
		    name = document.getElementsByAttributeValue("property", "v:itemreviewed").get(0).text();
		    if (name.contains(" "))
		    {
			name = name.substring(0, name.indexOf(" "));
		    }
		}
		else
		{
		    return childs;
		}

		movieItem.put("url", url);
		movieItem.put("name", name);
		movieItem.put("year", yearStr);
		movieItem.put("rateValue", rateValueStr);
		movieItem.put("rateNumber", rateNumberStr);
		newCollectCount++;
		this.existMvMap.put(url, "");
		this.agent.addOneDocument(movieItem, "good");

		log.info("抓取影片：" + name + " 年代：" + year + " 评分/评分人数:" + ratevalue + "/" + rateNumber + " url:" + url
			+ "\t 已抓取:" + newCollectCount);
	    }
	}
	else
	{
	    log.info("重复影片,忽略...抓取推荐影片");
	}

	Elements elements = document.getElementsByClass("recommendations-bd");
	if (elements.size() > 0)
	{
	    elements = elements.get(0).getElementsByTag("dl");
	}
	else
	{
	    return childs;
	}

	for (Element element : elements)
	{
	    String childUrl = element.getElementsByTag("a").get(0).attr("href");
	    if (existMvMap.containsKey(childUrl) || ignoreMvMap.containsKey(childUrl))
	    {
		continue;
	    }
	    childs.add(childUrl);
	}
	return childs;
    }

    public static void main(String[] args)
    {
	DBAgent agent = new DBAgent("localhost", 27017, "movie");
	agent.connect();
	new DoubanDownloaderBFS("https://movie.douban.com/subject/1485260/?from=subject-page", agent).collectBFS();
	agent.close();
    }
}
