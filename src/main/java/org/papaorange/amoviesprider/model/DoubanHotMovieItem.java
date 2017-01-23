package org.papaorange.amoviesprider.model;

import com.alibaba.fastjson.annotation.JSONField;

public class DoubanHotMovieItem
{
    @JSONField(name = "cover")
    private String cover;

    @JSONField(name = "cover_x")
    private int cover_x;

    @JSONField(name = "cover_y")
    private int cover_y;

    @JSONField(name = "id")
    private String id;

    @JSONField(name = "is_beetle_subject")
    private boolean is_beetle_subject;

    @JSONField(name = "is_new")
    private boolean is_new;

    @JSONField(name = "playable")
    private boolean playable;

    @JSONField(name = "rate")
    private String rate;

    @JSONField(name = "title")
    private String title;

    @JSONField(name = "url")
    private String url;

    public String getCover()
    {
	return cover;
    }

    public void setCover(String cover)
    {
	this.cover = cover;
    }

    public int getCover_x()
    {
	return cover_x;
    }

    public void setCover_x(int cover_x)
    {
	this.cover_x = cover_x;
    }

    public int getCover_y()
    {
	return cover_y;
    }

    public void setCover_y(int cover_y)
    {
	this.cover_y = cover_y;
    }

    public String getId()
    {
	return id;
    }

    public void setId(String id)
    {
	this.id = id;
    }

    public boolean isIs_beetle_subject()
    {
	return is_beetle_subject;
    }

    public void setIs_beetle_subject(boolean is_beetle_subject)
    {
	this.is_beetle_subject = is_beetle_subject;
    }

    public boolean isIs_new()
    {
	return is_new;
    }

    public void setIs_new(boolean is_new)
    {
	this.is_new = is_new;
    }

    public boolean isPlayable()
    {
	return playable;
    }

    public void setPlayable(boolean playable)
    {
	this.playable = playable;
    }

    public String getRate()
    {
	return rate;
    }

    public void setRate(String rate)
    {
	this.rate = rate;
    }

    public String getTitle()
    {
	return title;
    }

    public void setTitle(String title)
    {
	this.title = title;
    }

    public String getUrl()
    {
	return url;
    }

    public void setUrl(String url)
    {
	this.url = url;
    }

}
