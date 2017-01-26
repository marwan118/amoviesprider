package org.papaorange.amoviesprider.control;

import java.io.IOException;
import java.util.List;

import org.bson.Document;
import org.papaorange.amoviesprider.db.DBAgent;
import org.papaorange.amoviesprider.db.DBMgr;
import org.papaorange.amoviesprider.service.DoubanHotMovieDownloadTask;
import org.papaorange.amoviesprider.service.TorrentDownloadTask;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.BasicDBObject;

@RestController
public class GetMovieInfoController
{

    private DBAgent agent = null;

    public GetMovieInfoController()
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

    @CrossOrigin
    @RequestMapping("/nextcluster")
    public List<Document> getMovie() throws IOException
    {
	return agent.findNextCluster(new BasicDBObject(), "good", 48);
    }

    @CrossOrigin
    @RequestMapping("/reset")
    public void reset() throws IOException
    {
	agent.resetClusterCur();
    }

    @CrossOrigin
    @RequestMapping("/updatemovie")
    public void downloadDoubanMovie(@RequestParam("seedurl") String seeUrl)
    {
	if (seeUrl != null)
	{
	    new DoubanHotMovieDownloadTask().downloadHotMovie();
	}
    }

    @CrossOrigin
    @RequestMapping("/updatehotmovie")
    public void startDownloadDoubanHotMovie(String seeUrl)
    {
	new DoubanHotMovieDownloadTask().downloadHotMovie();
    }

    @CrossOrigin
    @RequestMapping("/updatetorrent")
    public void startDownloadBtttTorrent()
    {
	new TorrentDownloadTask().downloadTorrentTask();
    }

}