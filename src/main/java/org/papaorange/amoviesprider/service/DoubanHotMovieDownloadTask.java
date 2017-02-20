package org.papaorange.amoviesprider.service;

import java.io.IOException;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.papaorange.amoviesprider.db.DBAgent;
import org.papaorange.amoviesprider.db.DBConnectionFactory;
import org.papaorange.amoviesprider.model.DoubanHotMovieItem;
import org.papaorange.amoviesprider.model.DoubanHotMovieItemsCollection;
import org.papaorange.amoviesprider.utils.Utils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

@Component
public class DoubanHotMovieDownloadTask
{

    public void downloadMovieWithSeed(String url)
    {
	DBAgent agent = null;

	agent = DBConnectionFactory.getDBAgent("movie");

	new DoubanDownloaderBFS(url, agent).collectBFS();
    }

    @Scheduled(cron = "0 14 10 ? * *")
    public void downloadHotMovie()
    {
	DBAgent agent = null;

	agent = DBConnectionFactory.getDBAgent("movie");

	Document document = null;

	try
	{
	    document = Utils.httpGet(
		    "https://movie.douban.com/j/search_subjects?type=movie&tag=%E7%83%AD%E9%97%A8&sort=recommend&page_limit=20&page_start=0");
	}
	catch (IOException e)
	{
	    e.printStackTrace();
	}

	Elements elems = document.getElementsByTag("body");
	String jsonString = elems.html();
	// System.out.println(jsonString);
	DoubanHotMovieItemsCollection collection = JSON.parseObject(jsonString, DoubanHotMovieItemsCollection.class);

	/*
	 *
	 **/

	for (DoubanHotMovieItem item : collection.getSubjects())
	{
	    String url = item.getUrl();
	    new DoubanDownloaderBFS(url, agent).collectBFS();
	}
    }

    // public static void main(String[] args)
    // {
    // new DoubanHotMovieDownloadTask().downloadHotMovie();
    // }

}
