package org.papaorange.amoviesprider.service;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.papaorange.utils.DBAgent;
import org.papaorange.utils.Utils;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by papaorange on 2016/11/22.
 */
public class DoubanDownloader
{
    private String seedUrl = "";
    private DBAgent agent = null;
    private Map<String, Object> existMvMap = null;
    private static final Logger log = Logger.getLogger(DoubanDownloader.class);

    public Document download(String url) throws IOException
    {
	log.info("Download URL:" + url);
	return Jsoup.connect(url).header("Accept", "text/html").header("Accept-Charset", "utf-8")
		.header("Accept-Encoding", "gzip").header("Accept-Language", "en-US,en")
		.header("User-Agent",
			"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.22 (KHTML, like Gecko) Chrome/25.0.1364.160 Safari/537.22")
		.timeout(0).get();
    }

    DoubanDownloader(String seedUrl, DBAgent agent)
    {
	this.seedUrl = seedUrl;
	this.agent = agent;
	existMvMap = agent.getAllDocumentsKey("info", "url");
    }

    private int newCollectCount = 0;

    public void collectRecursively()
    {

	Document document = null;
	try
	{
	    Thread.sleep(500);
	    document = this.download(this.seedUrl);
	}
	catch (IOException e)
	{
	    // e.printStackTrace();
	    if (e.toString().contains("Status=403"))
	    {
		log.error("致命错误,页面禁止被访问,403!");
	    }
	    return;
	}
	catch (InterruptedException e)
	{
	    return;
	}
	if (!existMvMap.containsKey(seedUrl))
	{
	    String yearStr = null;
	    String rateNumberStr = null;
	    String rateValueStr = null;
	    if (document.getElementsByClass("year").size() > 0)
	    {
		yearStr = document.getElementsByClass("year").get(0).text().replace("(", "").replace(")", "");
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
	    }
	    else
	    {
		rateNumberStr = "0";
	    }
	    int rateNumber = Integer.parseInt(rateNumberStr);

	    if (document.getElementsByAttributeValue("property", "v:average").size() > 0)
	    {
		rateValueStr = document.getElementsByAttributeValue("property", "v:average").text();
	    }
	    else
	    {
		rateValueStr = "0";
	    }
	    float ratevalue = Float.parseFloat(rateValueStr);

	    if (year < 1990)
	    {
		System.err.println("90年以前电影，忽略。。。");

	    }
	    else if (rateNumber < 1000)
	    {
		System.err.println("投票人数少于1000，忽略。。。");
	    }
	    else if (ratevalue < 5)
	    {
		System.err.println("评分低于5分，忽略。。。");
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
		    return;
		}

		movieItem.put("url", seedUrl);
		movieItem.put("name", name);
		movieItem.put("year", yearStr);
		movieItem.put("rateValue", rateValueStr);
		movieItem.put("rateNumber", rateNumberStr);
		newCollectCount++;
		this.existMvMap.put(seedUrl, "");
		this.agent.addOneDocument(movieItem, "info");

		System.out.println("抓取影片：" + name + " 年代：" + year + " 评分/评分人数:" + ratevalue + "/" + rateNumber + " url:"
			+ seedUrl + "\t 已抓取:" + newCollectCount);
	    }
	}
	else
	{
	    System.out.println("重复影片,忽略...抓取推荐影片");
	}

	Elements elements = document.getElementsByClass("recommendations-bd");
	if (elements.size() > 0)
	{
	    elements = elements.get(0).getElementsByTag("dl");
	}
	else
	{
	    return;
	}

	for (Element element : elements)
	{
	    String url = element.getElementsByTag("a").get(0).attr("href");
	    if (existMvMap.containsKey(url))
	    {
		continue;
	    }
	    seedUrl = url;
	    collectRecursively();
	}

    }

    public static void main(String[] args)
    {
	DBAgent agent = new DBAgent("localhost", 27017, "movie");
	agent.connect();
	new DoubanDownloader("https://movie.douban.com/subject/25934014/", agent).collectRecursively();
	agent.close();
    }
}
