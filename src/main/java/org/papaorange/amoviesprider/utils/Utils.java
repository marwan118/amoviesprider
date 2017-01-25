package org.papaorange.amoviesprider.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils
{

    private final static Logger log = LoggerFactory.getLogger(Utils.class);

    public static Document httpGet(String url) throws IOException
    {
	log.debug("Download URL:" + url);
	return Jsoup.connect(url).ignoreContentType(true).header("Accept", "text/html")
		.header("Accept-Charset", "utf-8").header("Accept-Encoding", "gzip")
		.header("Accept-Language", "en-US,en")
		.header("User-Agent",
			"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.22 (KHTML, like Gecko) Chrome/25.0.1364.160 Safari/537.22")
		.timeout(0).get();
    }

    public static Document httpPost(String url, Map<String, String> map, String cookie) throws IOException
    {
	// 获取请求连接
	Connection con = Jsoup.connect(url);
	con.ignoreContentType(true).header("Accept", "text/html").header("Accept-Charset", "utf-8")
		.header("Accept-Encoding", "gzip").header("Accept-Language", "en-US,en")
		.header("User-Agent",
			"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.22 (KHTML, like Gecko) Chrome/25.0.1364.160 Safari/537.22")
		.timeout(0);
	// 遍历生成参数
	if (map != null)
	{
	    for (Entry<String, String> entry : map.entrySet())
	    {
		// 添加参数
		con.data(entry.getKey(), entry.getValue());
	    }
	}
	// 插入cookie（头文件形式）
	if (cookie != null)
	{
	    con.header("Cookie", cookie);
	}
	Document doc = con.post();
	return doc;
    }

    public static InputStream httpPostBlob(String url, Map<String, String> map, String cookie) throws IOException
    {
	// 获取请求连接

	Connection con = Jsoup.connect(url);
	con.ignoreContentType(true).header("Accept", "text/html").header("Accept-Charset", "utf-8")
		.header("Accept-Encoding", "gzip").header("Accept-Language", "en-US,en")
		.header("User-Agent",
			"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.22 (KHTML, like Gecko) Chrome/25.0.1364.160 Safari/537.22")
		.timeout(0);
	// 遍历生成参数
	if (map != null)
	{
	    for (Entry<String, String> entry : map.entrySet())
	    {
		// 添加参数
		con.data(entry.getKey(), entry.getValue());
	    }
	}
	// 插入cookie（头文件形式）
	if (cookie != null)
	{
	    con.header("Cookie", cookie);
	}

	return null;
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

    // public static void main(String[] args)
    // {
    // System.out.println(matchYear("5652-12-12"));
    // }
}
