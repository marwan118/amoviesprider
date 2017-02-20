package org.papaorange.amoviesprider.model.douban.comment;

import java.util.Date;

public class DoubanCommentItem
{
    // 用户id
    private String userId;
    // 用户名称
    private String userName;
    // 星数1-5
    private int stars;
    // 有用计数
    private int usefulCount;
    // 评论日期
    private Date date;

    // 评论内容
    private String content;

    public String getUserId()
    {
	return userId;
    }

    public void setUserId(String userId)
    {
	this.userId = userId;
    }

    public String getUserName()
    {
	return userName;
    }

    public void setUserName(String userName)
    {
	this.userName = userName;
    }

    public int getStars()
    {
	return stars;
    }

    public void setStars(int stars)
    {
	this.stars = stars;
    }

    public int getUsefulCount()
    {
	return usefulCount;
    }

    public void setUsefulCount(int usefulCount)
    {
	this.usefulCount = usefulCount;
    }

    public Date getDate()
    {
	return date;
    }

    public void setDate(Date date)
    {
	this.date = date;
    }

    public String getContent()
    {
	return content;
    }

    public void setContent(String content)
    {
	this.content = content;
    }

}
