import crawler.Crawler;

public class Main {

    public static void main(String[] args) {
        System.out.println("Start ...");

        String link = "https://habrahabr.ru/post/319164/";
        String path = "d:\\Study\\2016_Android\\Java_final_lab_Crawler\\downloaled";
        Crawler crawler = new Crawler(link, path, 2, 5);
        crawler.start();
        crawler.stop();
    }
}
