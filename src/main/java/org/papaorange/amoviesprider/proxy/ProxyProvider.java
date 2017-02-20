package org.papaorange.amoviesprider.proxy;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.papaorange.amoviesprider.db.DBAgent;
import org.papaorange.amoviesprider.db.DBMgr;
import org.springframework.stereotype.Component;

@Component
public class ProxyProvider
{

    private static List<Document> proxys = new ArrayList<>();

    public ProxyProvider()
    {
	DBAgent agent = new DBMgr().getDBAgent("proxy");
	proxys = agent.getAllDocuments("valid");
    }

    public List<Document> getAllProxies()
    {
	return proxys;
    }

    public String getRandomProxy()
    {
	Document proxy = proxys.get((int) (Math.random() * proxys.size()));

	return proxy.getString("proxy");
    }

}
