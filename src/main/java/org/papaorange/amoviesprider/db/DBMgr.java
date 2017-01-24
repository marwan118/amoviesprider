package org.papaorange.amoviesprider.db;

public class DBMgr
{

    private static DBAgent agent = null;

    public static DBAgent getDBAgent() throws Exception
    {

	if (agent != null)
	{
	    return agent;
	}
	else
	{
	    agent = new DBAgent("192.168.1.100", 27017, "movie");
	    agent.connect();
	    if (agent == null)
	    {
		throw new Exception("DateBase Broken!");
	    }
	    return agent;
	}
    }

}
