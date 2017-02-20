package org.papaorange.amoviesprider.db;

public class DBMgr
{

    private DBAgent agent = null;

    public DBAgent getDBAgent(String dbName)
    {

	if (agent != null)
	{
	    return agent;
	}
	else
	{
	    agent = new DBAgent("papaorange.org", 27017, dbName);
	    agent.connect();
	    return agent;
	}
    }

}
