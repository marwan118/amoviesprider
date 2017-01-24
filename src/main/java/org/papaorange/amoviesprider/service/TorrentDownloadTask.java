package org.papaorange.amoviesprider.service;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.papaorange.amoviesprider.db.DBMgr;
import org.papaorange.amoviesprider.model.BtttTorrentItem;
import org.papaorange.amoviesprider.parser.BtttParser;
import org.papaorange.amoviesprider.utils.DBAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBObject;

import be.christophedetroyer.torrent.TorrentParser;

@Component
public class TorrentDownloadTask
{
    private DBAgent agent = null;
    private final static Logger log = LoggerFactory.getLogger(TorrentDownloadTask.class);

    public TorrentDownloadTask()
    {
	try
	{
	    agent = DBMgr.getDBAgent();

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }

    // @Scheduled(cron = "0 6 21 ? * *")
    public void downloadTorrentTask()
    {
	log.info("下载种子任务开始....");
	List<Document> documents = agent.getDocuments("good",
		new BasicDBObject().append("magnets", new BasicDBObject().append("$exists", false)));

	for (Document document : documents)
	{

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
		    log.error("出现错误,忽略该Torrent:" + item.getName() + " ErrorMsg:" + e.getMessage());
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
