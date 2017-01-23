package org.papaorange.amoviesprider.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils
{
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
