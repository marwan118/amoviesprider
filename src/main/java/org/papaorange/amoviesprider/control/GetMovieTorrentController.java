package org.papaorange.amoviesprider.control;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.papaorange.amoviesprider.model.BtttTorrentItem;
import org.papaorange.amoviesprider.model.TorrentItem;
import org.papaorange.amoviesprider.parser.BtttParser;
import org.papaorange.amoviesprider.service.TorrentDownloadTask;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GetMovieTorrentController
{
    @CrossOrigin
    @RequestMapping("/torrent/{imdbId}")
    public List<TorrentItem> getMovieTorrentByImdbId(@PathVariable String imdbId) throws IOException
    {
	List<TorrentItem> torrentItems = new ArrayList<>();

	List<BtttTorrentItem> btttItems = BtttParser.getTorrentItemsByImdbId(imdbId);

	for (BtttTorrentItem btttItem : btttItems)
	{
	    BtttParser.downloadTorrent(btttItem);
	    TorrentItem temp = new TorrentItem();
	    temp.setImdbId(imdbId);
	    temp.setUrl("http://papaorange.org:8889/" + btttItem.getName());
	    torrentItems.add(temp);
	}

	return torrentItems;
    }

    @CrossOrigin
    @RequestMapping("/torrent/taskstart")
    public void startDownloadTorrentTask() throws IOException
    {
	new TorrentDownloadTask().downloadTorrentTask();
    }

}
