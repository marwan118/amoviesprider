package org.papaorange.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.Iterator;
import java.io.IOException;

/**
 * Created by papaorange on 2016/11/22.
 */
public class PageDownloader {
    public Document download(String url) throws IOException {
        return Jsoup.connect(url).header("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2").get();
    }

    public static void main(String[] args) {
        try {
            for (int i = 1; i < 5626; i++) {
                try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
                System.err.println("Downloading Page:" + i + "...");
                Document doc = new PageDownloader().download("https://rarbg.to/torrents.php?category=14;17;42;44;45;46;47;48&page=" + i);
                Iterator<Element> iter = doc.getElementsByClass("lista2").iterator();
                while (iter.hasNext()) {
                    String str = (String) iter.next().toString();
                    System.out.println(str);
                    System.exit(-1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
