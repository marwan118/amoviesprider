package org.papaorange.amoviesprider.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.jmx.Agent;
import org.bson.Document;
import org.papaorange.amoviesprider.db.DBAgent;
import org.papaorange.amoviesprider.db.DBMgr;
import org.papaorange.amoviesprider.model.BtttTorrentItem;
import org.papaorange.amoviesprider.parser.BtttParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBObject;

import be.christophedetroyer.torrent.TorrentParser;

@Component
public class TorrentDownloadTask
{
    private final static Logger log = LoggerFactory.getLogger(TorrentDownloadTask.class);

    @Scheduled(cron = "0 1 3 ? * *")
    public void downloadTorrentTask()
    {
	DBAgent agent = null;
	agent = DBMgr.getDBAgent();
	log.info("下载种子任务开始....");
	List<Document> documents = agent.getDocuments("good",
		new BasicDBObject().append("magnets", new BasicDBObject().append("$exists", false)));
	int total = documents.size();
	log.info("总计" + total + "部电影待下载Torrent...");

	int i = 0;
	for (Document document : documents)
	{

	    log.info("第(" + (i++) + "/" + total + ")部电影 : " + document.get("name") + "(" + document.get("year") + ")"
		    + " URL: " + document.get("url"));
	    String imdbLink = (String) document.get("imdbLink");
	    String imdbId = imdbLink.replace("http://www.imdb.com/title/", "");

	    if (agent.getDocuments("torrents", new BasicDBObject().append("imdbId", imdbId)).size() > 0)
	    {
		log.info("种子存在,略过下载...");
		continue;
	    }
	    agent.addOneDocument(downlaodTorrentAndParseMagnetUsingMovieInfo(document), "torrents");
	}
    }

    public static Document downlaodTorrentAndParseMagnetUsingMovieInfo(Document document)
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
		String hashInfo = TorrentParser.parseTorrent(filePath).getInfo_hash();
		object.append("link", "magnet:?xt=urn:btih:" + hashInfo);
		magnets.add(object);

		log.info("	解析种子文件:" + item.getName());
		log.info("	magnet:" + "magnet:?xt=urn:btih:" + hashInfo);

		FileUtils.deleteQuietly(new File("/data/db/torrents/" + item.getName()));
	    }
	    catch (Exception e)
	    {
		log.error("	解析种子出现错误,忽略:" + item.getName() + " ErrorMsg:" + e.getMessage());
	    }

	}
	Document newDoc = new Document();
	newDoc.append("imdbId", imdbId);
	newDoc.append("url", document.getString("url"));
	newDoc.append("magnets", magnets);

	return newDoc;
    }
}
