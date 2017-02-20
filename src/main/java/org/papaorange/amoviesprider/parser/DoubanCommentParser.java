package org.papaorange.amoviesprider.parser;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.papaorange.amoviesprider.model.douban.comment.DoubanCommentItem;
import org.papaorange.amoviesprider.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DoubanCommentParser
{
    private final static Logger log = LoggerFactory.getLogger(DoubanCommentParser.class);
    private boolean stop = false;
    private String movieURL = "";
    private LinkedList<DoubanCommentItem> comments = new LinkedList<>();

    public DoubanCommentParser(String movieUrl)
    {
	this.movieURL = movieUrl;
    }

    public void parse() throws ParseException, IOException
    {
	String nextPageParams = "";
	Document document = null;
	// 大循环
	while (true)
	{
	    try
	    {
		Thread.sleep(500);
	    }
	    catch (InterruptedException e)
	    {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }

	    document = Utils.httpGet(movieURL + "comments" + nextPageParams);

	    nextPageParams = document.select("#paginator > a.next").attr("href");

	    Elements commentItems = document.getElementsByClass("comment-item");
	    if (commentItems.size() == 0)
	    {
		log.error("致命错误，页面中未找到任何评论");
		return;
	    }

	    if (stop == true || nextPageParams.equals(""))
	    {
		break;
	    }

	    for (Element element : commentItems)
	    {
		DoubanCommentItem item = new DoubanCommentItem();
		this.stop = true;
		int usefulCount = Integer.parseInt(element.select(".votes.pr5").text());
		if (usefulCount < 10)
		{
		    continue;
		}
		else
		{
		    String userName = element.select("div.avatar > a").attr("title");
		    String userID = element.select("div.avatar > a").attr("href");
		    userID = userID.replace("https://www.douban.com/people/", "").replace("/", "");
		    int stars = -1;
		    String starsString = element.select("[class~=rating]").attr("class");
		    starsString = starsString.replace(" rating", "").replace("allstar", "").replace("0", "");
		    if (starsString.equals(""))
		    {
			stars = 0;
		    }
		    else
		    {
			stars = Integer.parseInt(starsString);
		    }
		    Date date = new Date();
		    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd");
		    String dateString = element.select(".comment-time ").text();

		    date = simpleDateFormat.parse(dateString);

		    String content = element.select("div.comment > p").text();

		    item.setDate(date);
		    item.setStars(stars);
		    item.setUsefulCount(usefulCount);
		    item.setUserId(userID);
		    item.setUserName(userName);
		    item.setContent(content);
		    output(item);
		    comments.add(item);
		    this.stop = false;
		}
	    }

	}

    }

    private void output(DoubanCommentItem item)
    {
	System.out.println("用户名/ID：" + item.getUserName() + "/" + item.getUserId());
	System.out.println("星星数/有用数" + item.getStars() + "/" + item.getUsefulCount());
	System.out.println("内容：" + item.getContent());
	System.out.println("");
    }

    public static void main(String[] args)
    {
	try
	{
	    new DoubanCommentParser("https://movie.douban.com/subject/26325320/").parse();
	}
	catch (ParseException | IOException e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }
}
