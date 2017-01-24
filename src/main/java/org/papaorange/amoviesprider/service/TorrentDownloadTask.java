package org.papaorange.amoviesprider.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.papaorange.amoviesprider.db.DBMgr;
import org.papaorange.amoviesprider.model.BtttTorrentItem;
import org.papaorange.amoviesprider.model.MagnetItem;
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

	    if (document.containsKey("magnets"))
	    {
		continue;
	    }
	    String imdbLink = (String) document.get("imdbLink");

	    String imdbId = imdbLink.replace("http://www.imdb.com/title/", "");

	    List<BtttTorrentItem> items = BtttParser.getTorrentItemsByImdbId(imdbId);
	    List<BasicDBObject> magnets = new ArrayList<>();

	    for (BtttTorrentItem item : items)
	    {
		String filePath = BtttParser.downloadTorrent(item);

		try
		{
		    BasicDBObject object = new BasicDBObject();
		    object.append("name", item.getName());
		    object.append("link", "magnet:?xt=urn:btih:" + TorrentParser.parseTorrent(filePath).getInfo_hash());
		    magnets.add(object);
		}
		catch (Exception e)
		{
		    e.printStackTrace();
		}

	    }
	    if (magnets.size() > 0)
	    {
		agent.updateOneDocument("good", "url", document, "magnets", magnets);
	    }

	}
    }

    public static void main(String[] args)
    {
	new TorrentDownloadTask().downloadTorrentTask();
    }
}
