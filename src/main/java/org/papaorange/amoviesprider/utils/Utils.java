package org.papaorange.amoviesprider.utils;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Utils
{

    private static final Logger log = Logger.getLogger(Utils.class);

    public static Document download(String url) throws IOException
    {
	log.debug("Download URL:" + url);
	return Jsoup.connect(url).ignoreContentType(true).header("Accept", "text/html")
		.header("Accept-Charset", "utf-8").header("Accept-Encoding", "gzip")
		.header("Accept-Language", "en-US,en")
		.header("User-Agent",
			"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.22 (KHTML, like Gecko) Chrome/25.0.1364.160 Safari/537.22")
		.timeout(0).get();
    }

    public static String matchYear(String str)
    {
	String year = "";
	try
	{
	    Pattern p = Pattern.compile("[0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3}");
	    Matcher matcher = p.matcher(str);
	    while (matcher.find())
	    {
		year = matcher.group();
	    }
	}
	catch (Exception e)
	{
	}
	return year;
    }

    public static void main(String[] args)
    {
	System.out.println(matchYear("5652-12-12"));
    }
}
