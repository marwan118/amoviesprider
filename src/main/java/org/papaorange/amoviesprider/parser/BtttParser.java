package org.papaorange.amoviesprider.parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.papaorange.amoviesprider.model.BtttTorrentItem;
import org.papaorange.amoviesprider.utils.ResDownloader;
import org.papaorange.amoviesprider.utils.Utils;

public class BtttParser
{
    private static Document getMovieSearchPageByImdbId(String id)
    {
	// http://www.bttt.la/s.php?q=tt1181840&sitesearch=www.bttt.la&domains=bttt.la&hl=zh-CN&ie=UTF-8&oe=UTF-8

	String url = "http://www.bttt.la/s.php?q=" + id
		+ "&sitesearch=www.bttt.la&domains=bttt.la&hl=zh-CN&ie=UTF-8&oe=UTF-8";

	try
	{
	    return Utils.httpGet(url);
	}
	catch (IOException e)
	{
	    e.printStackTrace();
	    return null;
	}
    }

    private static String getMovieSubjectPageUrlFromDocument(Document doc)
    {
	String ret = "";
	Elements elements = doc.getElementsByClass("item cl");
	if (elements.size() > 0)
	{
	    Elements temp = elements.get(0).getElementsByClass("tt cl");
	    if (temp.size() > 0)
	    {
		ret = temp.get(0).getElementsByTag("a").attr("href");
	    }

	}
	return "http://www.bttt.la" + ret;
    }

    public static List<BtttTorrentItem> getTorrentItemsByImdbId(String id)
    {
	List<BtttTorrentItem> items = new ArrayList<BtttTorrentItem>();

	Document document = BtttParser.getMovieSearchPageByImdbId(id);

	String subjectUrl = getMovieSubjectPageUrlFromDocument(document);

	try
	{
	    Document subjectDoc = Utils.httpGet(subjectUrl);

	    Elements elements = subjectDoc.getElementsByClass("tinfo");

	    for (Element element : elements)
	    {
		BtttTorrentItem item = new BtttTorrentItem();
		item.setTorrentDownloadPageUrl("http://www.bttt.la" + element.getElementsByTag("a").attr("href"));
		item.setName(element.getElementsByClass("torrent").text());
		item.setImdbId(id);
		items.add(item);
	    }
	}
	catch (IOException e)
	{
	    e.printStackTrace();
	}

	return items;
    }

    public static String downloadTorrent(BtttTorrentItem item)
    {
	String url = item.getTorrentDownloadPageUrl();
	String ret = "";
	Map<String, String> parms = item.parseQueryParamsFromDownloadUrl(url);
	parms.put("action", "download");
	String actionPage;
	try
	{
	    actionPage = Utils.httpGet(url).getElementsByTag("form").attr("action");

	    if (new File("/data/db/torrents/" + item.getName()).exists() == false)
	    {
		ResDownloader.downloadMethodPost("http://www.bttt.la" + actionPage, parms,
			"/data/db/torrents/" + item.getName());
	    }

	    ret = "/data/db/torrents/" + item.getName();
	    return ret;

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	    return ret;

	}

    }
    //
    // public static void main(String[] args)
    // {
    // List<BtttTorrentItem> items =
    // BtttParser.getTorrentItemsByImdbId("tt1603807");
    // for (BtttTorrentItem btttTorrentItem : items)
    // {
    // downloadTorrent(btttTorrentItem);
    // }
    // }
}
