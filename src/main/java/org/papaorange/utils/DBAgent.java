package org.papaorange.utils;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class DBAgent
{
    public static void main(String args[])
    {
	try
	{
	    // 连接到 mongodb 服务
	    MongoClient mongoClient = new MongoClient("papaorange.org", 27017);

	    // 连接到数据库
	    MongoDatabase mongoDatabase = mongoClient.getDatabase("movie");
	    System.out.println("Connect to database successfully");

	    MongoCollection<Document> collection = mongoDatabase.getCollection("info");
	    FindIterable<Document> finds = collection.find();
	    Document document = new Document("name", "驴得水");
	    collection.insertOne(document);
	    for (Document doc : finds)
	    {
		System.out.println(doc);
	    }
	    mongoClient.close();
	    System.out.println("集合 info 选择成功");
	}
	catch (Exception e)
	{
	    System.err.println(e.getClass().getName() + ": " + e.getMessage());
	}
    }
}
