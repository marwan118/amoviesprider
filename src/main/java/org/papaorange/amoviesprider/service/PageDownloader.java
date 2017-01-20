package org.papaorange.amoviesprider.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.papaorange.utils.DBAgent;
import org.papaorange.utils.Utils;

import com.mongodb.BasicDBObject;

import java.io.IOException;
import java.util.Hashtable;

/**
 * Created by papaorange on 2016/11/22.
 */
public class PageDownloader
{
    public Document download(String url) throws IOException
    {
	return Jsoup.connect(url).header("Accept", "text/html").header("Accept-Charset", "utf-8")
		.header("Accept-Encoding", "gzip").header("Accept-Language", "en-US,en")
		.header("User-Agent",
			"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.22 (KHTML, like Gecko) Chrome/25.0.1364.160 Safari/537.22")
		.timeout(0).get();
    }

    // private static Hashtable<String, String> toCollectUrls = new
    // Hashtable<>();

    public static void collectRecursively(String seedUrl, DBAgent agent)
    {

	Document document = null;
	try
	{
	    document = new PageDownloader().download(seedUrl);
	}
	catch (IOException e)
	{
	    // e.printStackTrace();
	    return;
	}

	String yearStr = null;
	String rateNumberStr = null;
	String rateValueStr = null;
	if (document.getElementsByClass("year").size() > 0)
	{
	    yearStr = document.getElementsByClass("year").get(0).text().replace("(", "").replace(")", "");
	}
	else
	{
	    yearStr = Utils
		    .matchYear(document.getElementsByAttributeValue("property", "v:initialReleaseDate").text().trim());
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
	    Hashtable<String, String> movieItem = new Hashtable<>();
	    try
	    {
		Thread.sleep(300);
	    }
	    catch (InterruptedException e)
	    {
		e.printStackTrace();
	    }
	    String url = element.getElementsByTag("a").get(0).attr("href");
	    if (agent.isDocumentExist("info", new BasicDBObject("url", url)))
	    {
		// System.out.println("重复，忽略。。。");
	    }
	    else if (year < 1990)
	    {
		// System.out.println("90年以前电影，忽略。。。");
	    }
	    else if (rateNumber < 1000)
	    {
		// System.out.println("投票人数少于1000，忽略。。。");
	    }
	    else if (ratevalue < 5)
	    {
		// System.out.println("评分低于5分，忽略。。。");
	    }
	    else
	    {
		String name = element.getElementsByTag("dd").get(0).text();
		movieItem.put("url", url);
		movieItem.put("name", name);
		movieItem.put("year", yearStr);
		movieItem.put("rateValue", rateValueStr);
		movieItem.put("rateNumber", rateNumberStr);

		agent.addOneDocument(movieItem, "info");

		System.out.println("抓取影片：" + name + " 年代：" + year + " 评分/评分人数:" + ratevalue + "/" + rateNumber + " url:"
			+ url + "\t 已抓取:" + movieItem.size());
		collectRecursively(url, agent);
	    }
	}
    }

    public static void main(String[] args)
    {
	DBAgent agent = new DBAgent("localhost", 27017, "movie");
	agent.connect();
	collectRecursively("https://movie.douban.com/subject/20278505/?from=subject-page", agent);
	agent.close();
    }
}
