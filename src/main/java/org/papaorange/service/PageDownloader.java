package org.papaorange.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Iterator;


import java.io.IOException;

/**
 * Created by papaorange on 2016/11/22.
 */
public class PageDownloader {
    public Document download(String url) throws IOException {
        return Jsoup.connect(url).header("User-Agent", "User-Agent,Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.98 Safari/537.36").get();
    }

    public static void main(String[] args) {
        try {
            for (int i = 1; i < 10; i++) {
                try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
                System.err.println("Downloading Page:" + i + "...");
                Document doc = new PageDownloader().download("https://rarbg.to/torrents.php?category=42;44;45;46;47;48&page=" + i);
                Elements elements = doc.getElementsByClass("lista2");
                if(elements.size()==0)
                {
                	System.out.println(doc);
                	System.exit(-1);
                }
                Iterator<Element> iter = doc.getElementsByClass("lista2").iterator();
                while (iter.hasNext()) {
                    String str = (String) iter.next().toString();
                    System.out.println(str);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
