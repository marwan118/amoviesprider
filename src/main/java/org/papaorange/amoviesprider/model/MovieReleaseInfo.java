package org.papaorange.amoviesprider.model;

import java.util.Date;

import com.mongodb.BasicDBObject;

public class MovieReleaseInfo
{
    private Date releaseDate;

    private String location;

    public Date getReleaseDate()
    {
	return releaseDate;
    }

    public void setReleaseDate(Date releaseDate)
    {
	this.releaseDate = releaseDate;
    }

    public String getLocation()
    {
	return location;
    }

    public void setLocation(String location)
    {
	this.location = location;
    }

    public BasicDBObject toBasicDBObject()
    {

	return new BasicDBObject().append("releaseDate", releaseDate).append("location", location);

    }
}
