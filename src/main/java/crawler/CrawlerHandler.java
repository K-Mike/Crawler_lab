package crawler;


import org.jsoup.nodes.Document;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Handler of Crawler.
 */
class CrawlerHandler {
    private final WeakReference<Crawler> crawlerWeak;


    CrawlerHandler(Crawler crawler) {
        this.crawlerWeak = new WeakReference<Crawler>(crawler);
    }

    Document loadPage(String url) {
        Crawler crawler = crawlerWeak.get();
        Document doc = null;
        if (crawler != null) {
            doc = crawler.loadPage(url);
        }

        return doc;
    }

    void savePage(Document doc, String fileName) {
        Crawler crawler = crawlerWeak.get();

        if (crawler != null) {
            crawler.savePage(doc, fileName);
        }
    }

    ArrayList<String> getAllLinks(Document doc) {
        Crawler crawler = crawlerWeak.get();

        ArrayList<String> arrayLinks = new ArrayList<String>();
        if (crawler != null) {
            arrayLinks = crawler.getAllLinks(doc);
        }

        return arrayLinks;
    }

    void addTasks(ArrayList<String> links, Integer depth) {
        Crawler crawler = crawlerWeak.get();

        if (crawler != null) {
            crawler.addTasks(links, depth);
        }
    }
}
