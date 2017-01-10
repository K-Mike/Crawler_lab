package crawler;


import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Crawler for downloading net of html pages.
 */
public class Crawler {

    private ArrayList<String> downloadedLinks;
    private final Executor<CrawlerTask> executor;
    private final String mainLink;
    private final String path;
    private final Integer depth;
    private final CrawlerHandler handler;

    /**
     * Constructor.
     * @param link Start link.
     * @param path Folder for downloaded pages.
     * @param depth Downloading depth.
     * @param threadNumber Number of executor's threads.
     */
    public Crawler(String link, String path, Integer depth, Integer threadNumber) {
        downloadedLinks = new ArrayList<String>();
        executor = new Executor<CrawlerTask>(threadNumber);
        this.mainLink = link;
        this.path = path;
        this.depth = depth;

        handler = new CrawlerHandler(this);
    }

    /**
     * Start executor from main link.
     */
    public void start() {
        CrawlerTask mainLinkTask = new CrawlerTask(mainLink, depth, handler);

        executor.start();
        executor.execute(mainLinkTask);
    }

    /**
     * Load html page from net.
     * @param url adress of the page
     * @return Doceument (jsoup format)
     */
    Document loadPage(String url) {
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            System.out.println("Cannot load " + url);
        }

        return doc;
    }

    /**
     * Get all links from the page.
     * @param doc Document of html page (jsoup format).
     * @return ArrayList<String> of link from current page.
     */
    ArrayList<String> getAllLinks(Document doc) {

        ArrayList<String> arrayLinks = new ArrayList<String>();

        Elements links = doc.select("a[href]");

        for (Element link : links) {
            arrayLinks.add(link.attr("abs:href"));
        }

        return arrayLinks;
    }

    /**
     * Save page to file.
     * @param doc Document of html page (jsoup format).
     * @param fileName Name of the file.
     */
    void savePage(Document doc, String fileName) {

        // Build file name path.
        StringBuilder sb = new StringBuilder();
        sb.append(path);
        sb.append("//");
        sb.append(fileName);
        sb.append(".html");
        String filePath = sb.toString();

        File newFile = new File(filePath);
        FileOutputStream fop = null;
        try {
            String html = doc.html();

            fop = new FileOutputStream(newFile);
            fop.write(html.getBytes());
            fop.flush();
            fop.close();
        } catch (NullPointerException ex) {
            System.out.println("Not correct loaded page ");
        } catch (IOException ex) {
            System.out.println("Cannot write file " + filePath);
        } finally {
            if (fop != null) {
                try {
                    fop.close();
                } catch (IOException ex) {
                    System.out.println("Problems with writing file " + filePath);
                }
            }
        }
    }

    /**
     * Add new task for executor from list if links.
     * @param links ArrayList<String>  of links
     * @param depth depth of downloading.
     */
    synchronized void addTasks(ArrayList<String> links, Integer depth) {

        if (depth <= 0) {
            return;
        }

        for (String link : links) {
            if ( !downloadedLinks.contains(link) ) {
                CrawlerTask task = new CrawlerTask(link, depth, handler);
                executor.execute(task);
                downloadedLinks.add(link);
            }
        }
    }

    /**
     * Soft stop of the executor.
     */
    public void stop() {
        executor.interruptSoft();
    }

}
