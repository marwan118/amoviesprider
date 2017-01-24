package org.papaorange.amoviesprider.model;

import java.util.HashMap;
import java.util.Map;

public class BtttTorrentItem
{
    private String name;
    private String torrentDownloadPageUrl;
    private String imdbId;

    public String getName()
    {
	return name.replace("/", "");
    }

    public void setName(String name)
    {
	this.name = name;
    }

    public String getTorrentDownloadPageUrl()
    {
	return torrentDownloadPageUrl;
    }

    public void setTorrentDownloadPageUrl(String torrentDownloadPageUrl)
    {
	this.torrentDownloadPageUrl = torrentDownloadPageUrl;
    }

    public String getImdbId()
    {
	return imdbId;
    }

    public void setImdbId(String imdbId)
    {
	this.imdbId = imdbId;
    }

    public Map<String, String> parseQueryParamsFromDownloadUrl(String url)
    {
	Map<String, String> params = new HashMap<String, String>();

	String[] segments = url.split("&");

	for (String seg : segments)
	{
	    if (seg.contains("id="))
	    {
		params.put("id", seg.replace("id=", ""));
	    }
	    if (seg.contains("uhash"))
	    {
		params.put("uhash", seg.replace("uhash=", ""));
	    }
	}

	return params;
    }

}
