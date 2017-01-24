package org.papaorange.amoviesprider.db;

import org.papaorange.amoviesprider.utils.DBAgent;
import org.springframework.stereotype.Component;

@Component
public class DBMgr
{

    private static DBAgent agent = null;

    public DBMgr()
    {
	agent = new DBAgent("192.168.1.100", 27017, "movie");
	agent.connect();
    }

    public static DBAgent getDBAgent() throws Exception
    {
	if (agent != null)
	{
	    return agent;
	}
	else
	{
	    throw new Exception("DateBase Broken!");
	}
    }

}
