package org.papaorange.amoviesprider.control;

import java.io.IOException;
import java.util.List;

import org.bson.Document;
import org.papaorange.amoviesprider.db.DBAgent;
import org.papaorange.amoviesprider.db.DBConnectionFactory;
import org.papaorange.amoviesprider.service.TorrentDownloadTask;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.BasicDBObject;

@RestController
public class GetMovieTorrentController
{
    // @CrossOrigin
    // @RequestMapping("/torrent/imdbid/{imdbId}")
    // public List<TorrentItem> getMovieTorrentByImdbId(@PathVariable String
    // imdbId) throws IOException
    // {
    // List<TorrentItem> torrentItems = new ArrayList<>();
    //
    // List<BtttTorrentItem> btttItems =
    // BtttParser.getTorrentItemsByImdbId(imdbId);
    //
    // for (BtttTorrentItem btttItem : btttItems)
    // {
    // BtttParser.downloadTorrent(btttItem);
    // TorrentItem temp = new TorrentItem();
    // temp.setImdbId(imdbId);
    // temp.setUrl("http://papaorange.org:8889/" + btttItem.getName());
    // torrentItems.add(temp);
    // }
    //
    // return torrentItems;
    // }

    private DBAgent agent = null;

    public GetMovieTorrentController()
    {
	agent = DBConnectionFactory.getDBAgent("movie");
    }

    @CrossOrigin
    @RequestMapping("/torrent")
    public Document getMovieMagnetLinkByUrl(@RequestParam String dburl) throws IOException
    {

	Document ret = new Document();

	List<Document> rets = agent.getDocuments("torrents", new BasicDBObject().append("url", dburl));

	if (rets.size() > 0)
	{
	    ret = rets.get(0);
	}
	return ret;

    }

    @CrossOrigin
    @RequestMapping("/torrent/taskstart")
    public void startDownloadTorrentTask() throws IOException
    {
	new TorrentDownloadTask().downloadTorrentTask();
    }

}
