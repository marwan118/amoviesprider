package org.papaorange.amoviesprider.control;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.papaorange.amoviesprider.db.DBAgent;
import org.papaorange.amoviesprider.db.DBMgr;
import org.papaorange.amoviesprider.service.DoubanDownloaderBFS;
import org.papaorange.amoviesprider.service.DoubanHotMovieDownloadTask;
import org.papaorange.amoviesprider.service.TorrentDownloadTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.BasicDBObject;

@RestController
@RequestMapping(value = "movie")
public class GetMovieInfoController
{

    private DBAgent agent = null;
    private boolean sortByLatest = true;
    private final static Logger log = LoggerFactory.getLogger(GetMovieInfoController.class);

    public GetMovieInfoController()
    {
	agent = DBMgr.getDBAgent();
    }

    @CrossOrigin
    @RequestMapping(value = "/next/{count}/{sort}/ratevalue/{gteORlte}/{ratevalue}")
    public List<Document> getNextClusterByRateValue(@PathVariable("gteORlte") String gteORlte,
	    @PathVariable("ratevalue") int ratevalue, @PathVariable("sort") String sort,
	    @PathVariable("count") int count) throws IOException
    {
	List<Document> ret = new ArrayList<>();
	int sortOrder = 1;
	if (sort.equals("latest"))

	{
	    sortOrder = -1;
	}
	else if (sort.equals("oldest"))
	{
	    sortOrder = 1;
	}
	if (gteORlte.equals("gte"))
	{
	    ret = agent.findNextCluster(new BasicDBObject("rateValue", new BasicDBObject("$gte", ratevalue)), "good",
		    count, true, new BasicDBObject("_id", sortOrder));
	}
	else if (gteORlte.equals("lte"))
	{

	}

	return ret;
    }

    @CrossOrigin
    @RequestMapping(value = "/next/{count}/{sort}}")
    public List<Document> getNextCluster(@PathVariable("count") int count, @PathVariable("sort") String sort)
	    throws IOException
    {

	log.info("getNextCluster?sort=" + sort + "&count=" + count);
	List<Document> ret = new ArrayList<>();

	if (sort.equals("latest"))
	{

	    if (sortByLatest == false)
	    {
		sortByLatest = true;
		reset();
	    }
	    ret = agent.findNextCluster(new BasicDBObject(), "good", count, true, new BasicDBObject("_id", -1));
	}
	else if (sort.equals("oldest"))

	{
	    if (sortByLatest == true)
	    {
		sortByLatest = false;
		reset();
	    }
	    ret = agent.findNextCluster(new BasicDBObject(), "good", count, true, new BasicDBObject("_id", 1));
	}
	return ret;
    }

    @CrossOrigin
    @RequestMapping("/next/reset")
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
	    new DoubanDownloaderBFS(seeUrl, agent).collectBFS();
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