package org.papaorange.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;

public class DBAgent
{

    private String dbAddress = "";
    private int dbPort = -1;
    private String dbName = "";
    private MongoClient mongoClient = null;
    private MongoDatabase mongoDatabase = null;
    private static final Logger log = Logger.getLogger(DBAgent.class);

    public DBAgent(String dbAddress, int dbPort, String dbName)
    {
	this.dbAddress = dbAddress;
	this.dbPort = dbPort;
	this.dbName = dbName;
    }

    public void connect()
    {
	this.mongoClient = new MongoClient(dbAddress, dbPort);
	this.mongoDatabase = this.mongoClient.getDatabase(dbName);
	log.info("连接到数据库:" + dbAddress + ":" + dbPort + "/" + dbName);
    }

    public void close()
    {
	this.mongoClient.close();
    }

    public Map<String, Object> getAllDocumentsKey(String collectionName, String key)
    {
	Map<String, Object> ret = new HashMap<>();

	MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
	MongoCursor<Document> cur = collection.find().iterator();
	while (cur.hasNext())
	{
	    Document document = cur.next();
	    ret.put((String) document.get(key), "");
	}
	return ret;
    }

    public void removeDocument(Map<String, String> doc, String collectionName, String filterName, String filterValue)
    {
	try
	{
	    MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
	    // DeleteResult result =
	    // collection.deleteOne(Filters.eq(doc.get(filterName),
	    // filterValue));

	    BasicDBObject toDelete = new BasicDBObject();
	    toDelete.put(filterName, filterValue);
	    DeleteResult result = collection.deleteOne(toDelete);
	    System.out.println(result);
	}
	catch (Exception e)
	{
	    System.err.println(e.getClass().getName() + ": " + e.getMessage());
	}
    }

    public void addOneDocument(Map<String, String> doc, String collectionName)
    {
	try
	{
	    MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
	    Document document = new Document();

	    for (String key : doc.keySet())
	    {
		document.append(key, doc.get(key));
	    }
	    collection.insertOne(document);
	}
	catch (Exception e)
	{
	    System.err.println(e.getClass().getName() + ": " + e.getMessage());
	}
    }

    public boolean isDocumentExist(String collectionName, BasicDBObject object)
    {
	boolean ret = false;
	MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
	FindIterable<Document> find = collection.find(object);
	MongoCursor<Document> cursor = find.iterator();
	while (cursor.hasNext())
	{
	    System.out.println(cursor.next());
	    ret = true;
	}
	return ret;
    }
}
