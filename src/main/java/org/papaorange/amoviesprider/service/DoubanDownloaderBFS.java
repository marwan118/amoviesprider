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
    private Map<String, Object> ignoreMvMap = null;
    private Map<String, Object> lastTimeRemainMvMap = null;
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
	this.lastTimeRemainMvMap = agent.getAllDocumentsKey("remain", "url");
	if (this.lastTimeRemainMvMap.size() > 0)
	{
	    for (String url : this.lastTimeRemainMvMap.keySet())
	    {
		bfsQ.add(url);
	    }
	}
    }

    public void collectBFS()
    {
	List<String> childUrl = new ArrayList<String>();
	if (lastTimeRemainMvMap.size() == 0)
	{
	    bfsQ.add(seedUrl);
	    Hashtable<String, Object> ignoreItem = new Hashtable<>();
	    ignoreItem.put("url", seedUrl);
	    agent.addOneDocument(ignoreItem, "remain");
	    childUrl = processOneMovie(seedUrl);
	}

	while (true)
	{
	    for (String child : childUrl)
	    {
		if (!existMvMap.containsKey(child) && !ignoreMvMap.containsKey(child))
		{
		    if (!bfsQ.contains(child))
		    {
			bfsQ.add(child);
			Hashtable<String, Object> ignoreItem = new Hashtable<>();
			ignoreItem.put("url", child);
			agent.addOneDocument(ignoreItem, "remain");
			// agent.removeDocument(ignoreItem, "remain", "url",
			// child);
			log.info("bfsQ加入待获取影片url:" + child);
		    }
		}
	    }

	    String head = bfsQ.removeFirst();
	    Hashtable<String, Object> ignoreItem = new Hashtable<>();
	    ignoreItem.put("url", head);
	    agent.removeDocument(ignoreItem, "remain", "url", head);
	    if (!existMvMap.containsKey(head))
	    {
		childUrl = processOneMovie(head);
	    }
	    else
	    {
		childUrl.clear();
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
	if (!existMvMap.containsKey(url))
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

	    if (year < 1990)
	    {
		log.info("1990年以前电影，忽略。。。" + url);
		this.ignoreMvMap.put(url, "");
		Hashtable<String, Object> ignoreItem = new Hashtable<>();
		ignoreItem.put("name", getMovieNameFromHtmlDocument(document));
		ignoreItem.put("url", url);
		ignoreItem.put("ignoreBy", "year");
		ignoreItem.put("year", year);
		ignoreItem.put("rateNumber", rateNumber);
		ignoreItem.put("rateValue", ratevalue);
		agent.addOneDocument(ignoreItem, "ignore");
		return childs;
	    }
	    else if (rateNumber < 5000)
	    {
		log.info("投票人数少于5000，忽略。。。" + url);
		this.ignoreMvMap.put(url, "");
		Hashtable<String, Object> ignoreItem = new Hashtable<>();
		ignoreItem.put("name", getMovieNameFromHtmlDocument(document));
		ignoreItem.put("url", url);
		ignoreItem.put("ignoreBy", "rateNumber");
		ignoreItem.put("year", year);
		ignoreItem.put("rateNumber", rateNumber);
		ignoreItem.put("rateValue", ratevalue);
		agent.addOneDocument(ignoreItem, "ignore");
		return childs;
	    }
	    else if (ratevalue < 6)
	    {
		log.info("评分低于6分，忽略。。。" + url);
		this.ignoreMvMap.put(url, "");
		Hashtable<String, Object> ignoreItem = new Hashtable<>();
		ignoreItem.put("name", getMovieNameFromHtmlDocument(document));
		ignoreItem.put("url", url);
		ignoreItem.put("ignoreBy", "ratevalue");
		ignoreItem.put("year", year);
		ignoreItem.put("rateNumber", rateNumber);
		ignoreItem.put("rateValue", ratevalue);
		agent.addOneDocument(ignoreItem, "ignore");
		return childs;
	    }
	    else
	    {
		Hashtable<String, Object> movieItem = new Hashtable<>();
		String name = getMovieNameFromHtmlDocument(document);
		if (name.equals(""))
		{
		    return childs;
		}

		movieItem.put("url", url);
		movieItem.put("name", name);
		movieItem.put("year", year);
		movieItem.put("rateValue", ratevalue);
		movieItem.put("rateNumber", rateNumber);
		movieItem.put("imdbLink", getMovieIMDBLinkFromHtmlDocument(document));
		movieItem.put("summary", getMovieSummaryFromHtmlDocument(document));
		newCollectCount++;
		this.existMvMap.put(url, "");
		this.agent.addOneDocument(movieItem, "good");

		log.info("抓取影片：" + name + "(" + year + ") " + ratevalue + "/" + rateNumber + "\t 已抓取:" + newCollectCount
			+ " url:" + url);
	    }
	}
	else
	{
	    log.info("重复影片,忽略...");
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
	    if (existMvMap.containsKey(childUrl))
	    {
		log.info("影片已存在影片库中... ");
		continue;
	    }
	    if (ignoreMvMap.containsKey(childUrl))
	    {
		log.info("影片已存在忽略库中...");
		continue;
	    }
	    childs.add(childUrl);
	}
	return childs;
    }

    private String getMovieNameFromHtmlDocument(Document doc)
    {
	// v:itemreviewed

	String name = "";

	if (doc.getElementsByAttributeValue("property", "v:itemreviewed").size() > 0)
	{
	    name = doc.getElementsByAttributeValue("property", "v:itemreviewed").get(0).text();
	    if (name.contains(" "))
	    {
		name = name.substring(0, name.indexOf(" "));
	    }
	}
	return name;
    }

    private String getMovieIMDBLinkFromHtmlDocument(Document doc)
    {
	String imdbLink = "";
	Elements elems = doc.getElementsByAttributeValueContaining("href", "http://www.imdb.com/title/");
	if (elems.size() > 0)
	{
	    imdbLink = elems.get(0).attr("href");
	}
	return imdbLink;
    }

    private String getMovieSummaryFromHtmlDocument(Document doc)
    {
	// v:summary
	String summary = "";
	if (doc.getElementsByAttributeValue("property", "v:summary").size() > 0)
	{
	    summary = doc.getElementsByAttributeValue("property", "v:summary").get(0).text();
	}

	return summary;
    }

    public static void main(String[] args)
    {
	DBAgent agent = new DBAgent("localhost", 27017, "movie");
	agent.connect();
	new DoubanDownloaderBFS("https://movie.douban.com/subject/1292720/?from=subject-page", agent).collectBFS();
	agent.close();
    }
}
