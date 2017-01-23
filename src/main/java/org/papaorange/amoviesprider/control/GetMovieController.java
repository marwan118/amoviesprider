package org.papaorange.amoviesprider.control;

import java.io.IOException;
import java.util.List;

import org.bson.Document;
import org.papaorange.utils.DBAgent;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.BasicDBObject;

@RestController
public class GetMovieController
{

    private DBAgent agent = new DBAgent("192.168.1.100", 27017, "movie");

    public GetMovieController()
    {
	agent.connect();
    }

    @CrossOrigin
    @RequestMapping("/nextcluster")
    public List<Document> getMovie() throws IOException
    {
	return agent.findNextCluster(new BasicDBObject(), "good", 48);
    }
    
    @CrossOrigin
    @RequestMapping("/reset")
    public void reset() throws IOException
    {
	agent.resetClusterCur();
    }

}