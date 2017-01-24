package org.papaorange.amoviesprider.parser;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class DoubanParser
{
    public static String getMovieNameFromHtmlDocument(Document doc)
    {
	// v:itemreviewed

	String name = "";

	if (doc.getElementsByAttributeValue("property", "v:itemreviewed").size() > 0)
	{
	    name = doc.getElementsByAttributeValue("property", "v:itemreviewed").get(0).text();
	    if (name.contains(" "))
	    {
		name = name.substring(0, name.indexOf(" "));
	    }
	}
	return name;
    }

    public static String getMovieIMDBLinkFromHtmlDocument(Document doc)
    {
	String imdbLink = "";
	Elements elems = doc.getElementsByAttributeValueContaining("href", "http://www.imdb.com/title/");
	if (elems.size() > 0)
	{
	    imdbLink = elems.get(0).attr("href");
	}
	return imdbLink;
    }

    public static String getMoviePosterLinkFromHtmlDocument(Document doc)
    {
	// rel="v:image"
	String posterLink = "";
	Elements elems = doc.getElementsByAttributeValue("rel", "v:image");
	if (elems.size() > 0)
	{
	    posterLink = elems.get(0).attr("src");
	}
	return posterLink;
    }

    public static String getMovieSummaryFromHtmlDocument(Document doc)
    {
	// v:summary
	String summary = "";
	if (doc.getElementsByAttributeValue("property", "v:summary").size() > 0)
	{
	    summary = doc.getElementsByAttributeValue("property", "v:summary").get(0).text();
	}

	return summary;
    }
}
