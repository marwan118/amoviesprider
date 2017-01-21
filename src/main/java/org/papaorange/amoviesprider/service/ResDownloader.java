package org.papaorange.amoviesprider.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

public class ResDownloader
{

    private static final Logger log = Logger.getLogger(ResDownloader.class);

    public static String download(String url, String filename) throws Exception
    {

	URL localURL = new URL(url);
	FileOutputStream fileOutputStream = null;
	URLConnection connection = localURL.openConnection();
	HttpURLConnection httpURLConnection = (HttpURLConnection) connection;

	httpURLConnection.setRequestProperty("Accept-Charset", "utf-8");
	httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

	InputStream inputStream = null;

	StringBuffer resultBuffer = new StringBuffer();

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

	return resultBuffer.toString();
    }
}
