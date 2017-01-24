package org.papaorange.amoviesprider.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.papaorange.amoviesprider.db.DBMgr;
import org.papaorange.amoviesprider.model.BtttTorrentItem;
import org.papaorange.amoviesprider.parser.BtttParser;
import org.papaorange.amoviesprider.utils.DBAgent;

import com.mongodb.BasicDBObject;

import be.christophedetroyer.torrent.TorrentParser;

public class TorrentDownloadTask
{
    private DBAgent agent = null;

    TorrentDownloadTask()
    {
	try
	{
	    agent = new DBMgr().getDBAgent();

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }

    public void downloadTorrentTask()
    {
	List<Document> documents = agent.getAllDocuments("good");

	for (Document document : documents)
	{
	    String imdbLink = (String) document.get("imdbLink");

	    String imdbId = imdbLink.replace("http://www.imdb.com/title/", "");

	    List<BtttTorrentItem> items = BtttParser.getTorrentItemsByImdbId(imdbId);
	    List<String> magents = new ArrayList<>();

	    for (BtttTorrentItem item : items)
	    {
		String filePath = BtttParser.downloadTorrent(item);

		try
		{
		    String magent = "magnet:?xt=urn:btih:" + TorrentParser.parseTorrent(filePath).getInfo_hash();
		    magents.add(magent);
		}
		catch (IOException e)
		{
		    e.printStackTrace();
		}

	    }

	    agent.updateOneDocument("good", document, "magnets", new BasicDBObject().append("link", magents));

	}
    }

    public static void main(String[] args)
    {
	new TorrentDownloadTask().downloadTorrentTask();
    }
}
