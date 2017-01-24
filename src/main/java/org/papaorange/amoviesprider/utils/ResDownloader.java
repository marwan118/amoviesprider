package org.papaorange.amoviesprider.utils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

public class ResDownloader
{

    private static final Logger log = Logger.getLogger(ResDownloader.class);

    public static void downloadMethodGet(String url, String filename) throws Exception
    {

	URL localURL = new URL(url);
	FileOutputStream fileOutputStream = null;
	URLConnection connection = localURL.openConnection();
	HttpURLConnection httpURLConnection = (HttpURLConnection) connection;

	httpURLConnection.setRequestProperty("Accept-Charset", "utf-8");
	httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

	InputStream inputStream = null;

	log.debug("GET:" + url);

	if (httpURLConnection.getResponseCode() >= 300)
	{
	    log.warn("GET:" + url + "\n" + "HTTP Request is not success, Response code is "
		    + httpURLConnection.getResponseCode());

	    throw new Exception("HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());
	}

	try
	{
	    inputStream = httpURLConnection.getInputStream();

	    FileUtils.copyInputStreamToFile(inputStream, new File(filename));

	}
	finally
	{

	    if (fileOutputStream != null)
	    {
		fileOutputStream.close();
	    }

	    if (inputStream != null)
	    {
		inputStream.close();
	    }

	}
    }

    public static void downloadMethodPost(String url, Map<String, String> parms, String filename) throws Exception
    {

	URL localURL = new URL(url);
	URLConnection connection = localURL.openConnection();
	HttpURLConnection httpURLConnection = (HttpURLConnection) connection;

	/**
	 * header("Accept", "text/html").header("Accept-Charset", "utf-8")
	 * .header("Accept-Encoding", "gzip").header("Accept-Language",
	 * "en-US,en") .header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64)
	 * AppleWebKit/537.22 (KHTML, like Gecko) Chrome/25.0.1364.160
	 * Safari/537.22")
	 */
	httpURLConnection.setRequestProperty("Accept-Charset", "utf-8");
	httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	httpURLConnection.setRequestProperty("User-Agent",
		"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.22 (KHTML, like Gecko) Chrome/25.0.1364.160 Safari/537.22");
	// httpURLConnection.setRequestProperty("Connection", "Keep-Alive");//
	// 维持长连接
	httpURLConnection.setDoOutput(true);
	httpURLConnection.setDoInput(true);
	InputStream inputStream = null;
	String formData = "";
	for (String key : parms.keySet())
	{
	    formData += (key + "=" + parms.get(key) + "&");
	}
	formData = formData.substring(0, formData.lastIndexOf("&"));
	httpURLConnection.connect();
	// 建立输入流，向指向的URL传入参数
	DataOutputStream dos = new DataOutputStream(httpURLConnection.getOutputStream());
	dos.writeBytes(formData);
	dos.flush();
	dos.close();
	// 获得响应状态
	int resultCode = httpURLConnection.getResponseCode();
	log.debug("POST:" + url);

	if (HttpURLConnection.HTTP_OK == resultCode)
	{

	    try
	    {
		inputStream = httpURLConnection.getInputStream();

		FileUtils.copyInputStreamToFile(inputStream, new File(filename));

	    }
	    finally
	    {

		if (inputStream != null)
		{
		    inputStream.close();
		}

	    }
	}
	else
	{
	    log.warn("POST:" + url + "\n" + "HTTP Request is not success, Response code is "
		    + httpURLConnection.getResponseCode());

	    throw new Exception("HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());

	}
    }
}
