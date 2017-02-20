package org.papaorange.amoviesprider.db;

import java.util.Hashtable;

public class DBConnectionFactory
{

    private static Hashtable<String, DBAgent> agentMap = new Hashtable<>();

    public static DBAgent getDBAgent(String dbName)
    {

	if (agentMap.containsKey(dbName) == false)
	{
	    DBAgent agent = new DBAgent("papaorange.org", 27017, dbName);
	    agentMap.put(dbName, agent);
	    return agent;
	}
	else
	{
	    return agentMap.get(dbName);
	}
    }

}
