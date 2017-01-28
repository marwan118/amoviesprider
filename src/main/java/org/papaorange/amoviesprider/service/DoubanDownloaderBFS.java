package org.papaorange.amoviesprider.service;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.papaorange.amoviesprider.db.DBAgent;
import org.papaorange.amoviesprider.parser.DoubanParser;
import org.papaorange.amoviesprider.utils.ResDownloader;
import org.papaorange.amoviesprider.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private final static Logger log = LoggerFactory.getLogger(DoubanDownloaderBFS.class);

    private int newCollectCount = 0;

    DoubanDownloaderBFS(String seedUrl, DBAgent agent)
    {
	this.seedUrl = urlUnify(seedUrl);
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

    private static String urlUnify(String url)
    {
	url = url.contains("?") ? url.substring(0, url.indexOf("?") - 1) : url;
	if (!url.endsWith("/"))
	{
	    url = url + "/";
	}
	return url;
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
	log.info("开始解析电影:url=" + url);

	Document document = null;
	List<String> childs = new ArrayList<>();
	try
	{
	    Thread.sleep(500);
	    document = Utils.httpGet(url);
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
		ignoreItem.put("name", DoubanParser.getMovieNameFromHtmlDocument(document));
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
		ignoreItem.put("name", DoubanParser.getMovieNameFromHtmlDocument(document));
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
		ignoreItem.put("name", DoubanParser.getMovieNameFromHtmlDocument(document));
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
		String name = DoubanParser.getMovieNameFromHtmlDocument(document);
		if (name.equals(""))
		{
		    return childs;
		}

		movieItem.put("url", url);
		movieItem.put("name", name);
		movieItem.put("year", year);
		movieItem.put("rateValue", ratevalue);
		movieItem.put("rateNumber", rateNumber);
		movieItem.put("imdbLink", DoubanParser.getMovieIMDBLinkFromHtmlDocument(document));
		movieItem.put("summary", DoubanParser.getMovieSummaryFromHtmlDocument(document));
		String posterLink = DoubanParser.getMoviePosterLinkFromHtmlDocument(document);
		String path = "/data/db/poster/" + posterLink.substring(posterLink.lastIndexOf("/") + 1);
		movieItem.put("posterUrl", DoubanParser.getMoviePosterLinkFromHtmlDocument(document));
		movieItem.put("posterPath", path);
		movieItem.put("release", DoubanParser.getMovieReleaseInfoFromHtmlDoucument(document));
		movieItem.put("country", DoubanParser.getMovieProduceCountryFromHtmlDocument(document));
		movieItem.put("runtime", DoubanParser.getMovieRuntimeFromHtmlDocument(document));
		movieItem.put("category", DoubanParser.getMovieCategoryFromHtmlDocument(document));
		movieItem.put("language", DoubanParser.getMovieLanguageFromHtmlDocument(document));
		movieItem.put("alias", DoubanParser.getMovieAliasFromHtmlDocument(document));

		newCollectCount++;
		this.existMvMap.put(url, "");
		this.agent.addOneDocument(movieItem, "good");
		try
		{
		    ResDownloader.downloadMethodGet(posterLink, path);
		}
		catch (Exception e)
		{
		    e.printStackTrace();
		}
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
	    childUrl = urlUnify(childUrl);
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

    public static void main(String[] args)
    {
	DBAgent agent = new DBAgent("papaorange.org", 27017, "movie");
	agent.connect();
	new DoubanDownloaderBFS("http://movie.douban.com/subject/26051524", agent)
		.processOneMovie("http://movie.douban.com/subject/26051524");
	agent.close();
    }
}
