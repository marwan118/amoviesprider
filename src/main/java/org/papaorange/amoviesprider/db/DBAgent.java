package org.papaorange.amoviesprider.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private int clusterCur = 0;
    private final static Logger log = LoggerFactory.getLogger(DBAgent.class);

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

    public List<Document> getDocuments(String collectionName, Bson filter)
    {
	List<Document> ret = new ArrayList<>();
	MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
	MongoCursor<Document> cur = collection.find(filter).iterator();
	while (cur.hasNext())
	{
	    Document document = cur.next();
	    ret.add(document);
	}
	return ret;

    }

    public List<Document> getAllDocuments(String collectionName)
    {
	List<Document> ret = new ArrayList<>();
	MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
	MongoCursor<Document> cur = collection.find().iterator();
	while (cur.hasNext())
	{
	    Document document = cur.next();
	    ret.add(document);
	}
	return ret;
    }

    public int updateOneDocument(String collectionName, String key, Document ori, String toInsertKey,
	    Object toInsertObj)
    {
	int ret = 0;
	MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
	// System.out.println(collection.find(new BasicDBObject().append(key,
	// ori.getString(key))).iterator().next());

	BasicDBObject update = new BasicDBObject();
	update.put("$set", new BasicDBObject(toInsertKey, toInsertObj));
	collection.findOneAndUpdate(new BasicDBObject().append(key, ori.getString(key)), update);

	return ret;
    }

    public void removeDocument(Map<String, Object> doc, String collectionName, String filterName, String filterValue)
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

    public void addManyDocuments(List<? extends Document> docs, String collectionName)
    {
	MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);

	collection.insertMany(docs);

    }

    public void addOneDocument(Map<String, Object> doc, String collectionName)
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

    public List<Document> findNextCluster(BasicDBObject condiction, String collectionName, int clusterSize,
	    boolean sort, BasicDBObject sortby)
    {
	List<Document> result = new ArrayList<>();
	try
	{
	    MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
	    MongoCursor<Document> cursor = null;
	    if (sort == true)
	    {
		cursor = collection.find(condiction).skip(this.clusterCur).limit(clusterSize).sort(sortby).iterator();
	    }
	    else
	    {
		cursor = collection.find(condiction).skip(this.clusterCur).limit(clusterSize).iterator();
	    }
	    int i = 0;
	    while (cursor.hasNext())
	    {
		i++;
		Document temp = cursor.next();
		temp.append("imdbId", temp.get("imdbLink").toString().replaceAll("http://www.imdb.com/title/", ""));
		result.add(temp);
	    }
	    this.clusterCur += i;
	}
	catch (Exception e)
	{
	    System.err.println(e.getClass().getName() + ": " + e.getMessage());
	}
	return result;
    }

    public void resetClusterCur()
    {
	this.clusterCur = 0;
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

    // public static void main(String[] args)
    // {
    // DBAgent agent = new DBAgent("192.168.1.100", 27017, "movie");
    // agent.connect();
    // int i = 0;
    // while (true)
    // {
    // List<Document> documents = agent.findNextCluster(
    // new BasicDBObject().append("rateValue", new
    // BasicDBObject().append("$gte", 8)), "good", 50);
    // if (documents.size() == 0)
    // {
    // break;
    // }
    // for (Document document : documents)
    // {
    // System.out.println(i++ + ":" + document);
    // }
    // System.out.println();
    // }
    // agent.close();
    // }
}
