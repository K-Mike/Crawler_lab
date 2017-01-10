package crawler;


import org.jsoup.nodes.Document;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Task for downloading html pages.
 */
class CrawlerTask implements Task {
    final private String url;
    final private Integer depth;
    final private CrawlerHandler crawlerHandler;

    /**
     * Constructor.
     * @param url Adress of the page.
     * @param depth Depth of downloading.
     * @param crawlerHandler Handler of the current crawler.
     */
    CrawlerTask(String url, Integer depth, CrawlerHandler crawlerHandler) {
        this.url = url;
        this.depth = depth;
        this.crawlerHandler = crawlerHandler;
    }

    public void doWork() {
        System.out.println(url);
        Document doc = crawlerHandler.loadPage(url);

        if (doc != null) {
            String filename = null;
            try {
                filename = URLEncoder.encode(url, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                StringBuilder sb = new StringBuilder();
                sb.append("");
                sb.append(url.hashCode());
                filename = sb.toString();
            }
            crawlerHandler.savePage(doc, filename);
            crawlerHandler.addTasks(crawlerHandler.getAllLinks(doc), depth - 1);
        }
    }
}
