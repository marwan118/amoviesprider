package org.papaorange.amoviesprider.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

import org.bson.Document;
import org.papaorange.amoviesprider.db.DBAgent;
import org.papaorange.amoviesprider.db.DBConnectionFactory;
import org.papaorange.amoviesprider.model.douban.comment.DoubanCommentItem;
import org.papaorange.amoviesprider.parser.DoubanCommentParser;
import org.springframework.stereotype.Component;

@Component
public class DoubanCommentDownloadTask
{

    public static void download()
    {
	DBAgent agent = null;
	LinkedList<Document> bsonComments = new LinkedList<>();
	LinkedList<DoubanCommentItem> comments = new LinkedList<>();
	agent = DBConnectionFactory.getDBAgent("movie");

	List<Document> docs = agent.getAllDocuments("good");
	for (Document document : docs)
	{
	    try
	    {
		comments = new DoubanCommentParser(document.getString("url")).parse();
	    }
	    catch (ParseException | IOException e)
	    {
		e.printStackTrace();
	    }

	    for (DoubanCommentItem comment : comments)
	    {
		Document bsonDoc = new Document();
		bsonDoc.append("url", document.getString("url"));
		bsonDoc.append("content", comment.getContent());
		bsonDoc.append("date", comment.getDate());
		bsonDoc.append("stars", comment.getStars());
		bsonDoc.append("usefulcount", comment.getUsefulCount());
		bsonDoc.append("uid", comment.getUserId());
		bsonDoc.append("uname", comment.getUserName());
		bsonComments.add(bsonDoc);
	    }

	    if (bsonComments.size() > 0)
	    {
		agent.addManyDocuments(bsonComments, "comments");
		bsonComments.clear();
	    }

	}

    }

    public static void main(String[] args)
    {
	download();
    }
}
