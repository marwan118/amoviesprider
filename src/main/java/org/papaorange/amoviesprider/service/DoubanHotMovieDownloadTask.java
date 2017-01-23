package org.papaorange.amoviesprider.service;

import java.io.IOException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.papaorange.amoviesprider.model.DoubanHotMovieItem;
import org.papaorange.amoviesprider.model.DoubanHotMovieItemsCollection;
import org.papaorange.amoviesprider.utils.DBAgent;
import org.papaorange.amoviesprider.utils.Utils;

import com.alibaba.fastjson.JSON;

public class DoubanHotMovieDownloadTask
{

    public void downloadHotMovie()
    {
	DBAgent agent = new DBAgent("papaorange.org", 27017, "movie");
	agent.connect();
	Document document = null;

	try
	{
	    document = Utils.download(
		    "https://movie.douban.com/j/search_subjects?type=movie&tag=%E7%83%AD%E9%97%A8&sort=recommend&page_limit=20&page_start=0");
	}
	catch (IOException e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	Elements elems = document.getElementsByTag("body");
	String jsonString = elems.html();
	System.out.println(jsonString);
	DoubanHotMovieItemsCollection collection = JSON.parseObject(jsonString, DoubanHotMovieItemsCollection.class);

	/*
	 *
	 **/

	for (DoubanHotMovieItem item : collection.getSubjects())
	{
	    String url = item.getUrl();
	    new DoubanDownloaderBFS(url, agent).collectBFS();
	}
	agent.close();
    }

    public static void main(String[] args)
    {
	new DoubanHotMovieDownloadTask().downloadHotMovie();
    }

}
