package org.papaorange.amoviesprider.db;

public class DBMgr
{

    private static DBAgent agent = null;

    public static DBAgent getDBAgent()
    {

	if (agent != null)
	{
	    return agent;
	}
	else
	{
	    agent = new DBAgent("papaorange.org", 27017, "movie");
	    agent.connect();
	    return agent;
	}
    }

}
