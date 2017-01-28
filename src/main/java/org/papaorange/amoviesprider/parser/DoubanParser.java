package org.papaorange.amoviesprider.parser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.papaorange.amoviesprider.model.MovieReleaseInfo;
import org.papaorange.amoviesprider.utils.Utils;

import com.mongodb.BasicDBObject;

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

    public static List<BasicDBObject> getMovieReleaseInfoFromHtmlDoucument(Document doc)
    {
	List<BasicDBObject> ret = new ArrayList<>();

	// v:initialReleaseDate
	Elements elems = doc.getElementsByAttributeValue("property", "v:initialReleaseDate");

	for (Element elem : elems)
	{
	    String releaseInfoStr = elem.attr("content");

	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	    MovieReleaseInfo releaseInfo = new MovieReleaseInfo();

	    try
	    {
		releaseInfo.setReleaseDate(sdf.parse(Utils.matchDate(releaseInfoStr)));
	    }
	    catch (ParseException e)
	    {
		e.printStackTrace();
		continue;
	    }

	    String location = null;
	    
	    if(releaseInfoStr.contains("("))
	    {
		location = releaseInfoStr.substring(releaseInfoStr.indexOf("(") + 1, releaseInfoStr.indexOf(")"));
	    }
	    else
	    {
		location = "未知";
	    }

	    releaseInfo.setLocation(location);

	    ret.add(releaseInfo.toBasicDBObject());
	}

	return ret;
    }

    public static List<String> getMovieProduceCountryFromHtmlDocument(Document doc)
    {
	List<String> ret = new ArrayList<>();

	List<Node> countries = doc.getElementsByClass("pl").stream().filter(x -> x.html().equals("制片国家/地区:"))
		.map(x -> x.nextSibling()).collect(Collectors.toList());

	String countryString = countries.toString();

	if (countryString.contains("/"))
	{
	    for (String node : countryString.split("/"))
	    {
		ret.add(node.replace("[", "").replace("]", "").trim());
	    }
	}
	else
	{
	    ret.add(countryString.replace("[", "").replace("]", "").trim());
	}

	return ret;
    }

    public static List<String> getMovieLanguageFromHtmlDocument(Document doc)
    {
	List<String> ret = new ArrayList<>();

	List<Node> languages = doc.getElementsByClass("pl").stream().filter(x -> x.html().equals("语言:"))
		.map(x -> x.nextSibling()).collect(Collectors.toList());

	String languagesString = languages.toString();

	if (languagesString.contains("/"))
	{
	    for (String node : languagesString.split("/"))
	    {
		ret.add(node.replace("[", "").replace("]", "").trim());
	    }
	}
	else
	{

	    ret.add(languagesString.replace("[", "").replace("]", "").trim());
	}

	return ret;
    }

    public static List<String> getMovieCategoryFromHtmlDocument(Document doc)
    {

	// v:genre

	List<String> categorys = doc.getElementsByAttributeValue("property", "v:genre").stream().map(x -> x.text())
		.collect(Collectors.toList());

	return categorys;
    }

    public static String getMovieRuntimeFromHtmlDocument(Document doc)
    {

	// v:runtime

	String runtime = doc.getElementsByAttributeValue("property", "v:runtime").stream().map(x -> x.attr("content"))
		.collect(Collectors.toList()).iterator().next();

	return runtime;
    }

    public static List<String> getMovieAliasFromHtmlDocument(Document doc)
    {

	List<String> ret = new ArrayList<>();

	List<Node> alias = doc.getElementsByClass("pl").stream().filter(x -> x.html().equals("又名:"))
		.map(x -> x.nextSibling()).collect(Collectors.toList());

	String aliasString = alias.toString();

	if (aliasString.contains("/"))
	{
	    for (String node : aliasString.split("/"))
	    {
		ret.add(node.replace("[", "").replace("]", "").trim());
	    }
	}
	else {
	    ret.add(aliasString.replace("[", "").replace("]", "").trim());
	}

	return ret;
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
