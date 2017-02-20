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
import org.papaorange.amoviesprider.proxy.ProxyProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils
{

    private final static Logger log = LoggerFactory.getLogger(Utils.class);

    public static Document httpGet(String url) throws IOException
    {
	log.debug("Download URL:" + url);
	String proxy = new ProxyProvider().getRandomProxy();

	String ip = proxy.substring(0, proxy.indexOf(":"));
	Integer port = Integer.parseInt(proxy.substring(proxy.indexOf(":") + 1));
	return Jsoup.connect(url).ignoreContentType(true).header("Accept", "text/html").header("Accept-Charset", "utf-8").header("Accept-Encoding", "gzip").header("Accept-Language", "zh-cn")
		.header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.22 (KHTML, like Gecko) Chrome/25.0.1364.160 Safari/537.22").timeout(0).proxy(ip, port).get();
    }

    public static Document httpPost(String url, Map<String, String> map, String cookie) throws IOException
    {
	// 获取请求连接
	Connection con = Jsoup.connect(url);
	con.ignoreContentType(true).header("Accept", "text/html").header("Accept-Charset", "utf-8").header("Accept-Encoding", "gzip").header("Accept-Language", "en-US,en")
		.header("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:51.0) Gecko/20100101 Firefox/51.0").timeout(0);
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
	con.ignoreContentType(true).header("Accept", "text/html").header("Accept-Charset", "utf-8").header("Accept-Encoding", "gzip").header("Accept-Language", "en-US,en")
		.header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.22 (KHTML, like Gecko) Chrome/25.0.1364.160 Safari/537.22").timeout(0);
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

    // eg:129
    public static String matchRuntime(String str)
    {
	String runtime = "";
	try
	{
	    Pattern p = Pattern.compile("[0-9]+");
	    Matcher matcher = p.matcher(str);
	    while (matcher.find())
	    {
		runtime = matcher.group();
	    }
	}
	catch (Exception e)
	{
	}
	return runtime;
    }

    // eg:2016
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

    // eg:2016-01-23
    public static String matchDate(String dateStr)
    {
	String date = "";
	try
	{
	    Pattern p = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}");
	    Matcher matcher = p.matcher(dateStr);
	    while (matcher.find())
	    {
		date = matcher.group();
	    }
	}
	catch (Exception e)
	{
	}
	return date;
    }

    // public static void main(String[] args)
    // {
    // System.out.println(matchYear("5652-12-12"));
    // }
}
