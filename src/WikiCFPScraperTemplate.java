package src;

import java.net.*;
import java.io.*;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

public class WikiCFPScraperTemplate {
    public static int DELAY = 7;
    public static void main(String[] args) {

        try {
            String category = "data mining";
            //String category = "databases";
            //String category = "machine learning";
            //String category = "artificial intelligence";

            int numOfPages = 20;

            //create the output file
            File file = new File("wikicfp_crawl.txt");
            file.createNewFile();
            FileWriter writer = new FileWriter(file);

            //now start crawling the all 'numOfPages' pages
            for(int i = 1;i<=numOfPages;i++) {
                //Create the initial request to read the first page
                //and get the number of total results
                String linkToScrape = "http://www.wikicfp.com/cfp/call?conference="+
                        URLEncoder.encode(category, "UTF-8") +"&page=" + i;
                //parse or store the content of page 'i' here in 'content'
                String content = getPageFromUrl(linkToScrape);

                //YOUR CODE GOES HERE
                // parse HTML to find table table for conference_acronym, conference_name, and conference_location
                Document doc = Jsoup.parse(content);
                // put information from table table into tsv
                // Elements table_table = doc.select("table table tbody tr");
                Elements table_table = doc.select("table table tbody");
                int counter = 1;
                for(Element e: table_table) {
                    if (e.toString().contains("#e6e6e6") || e.toString().contains("#f6f6f6")) {
                        if (counter == 1) {
                            // System.out.println(e.select("table table tbody tr tr:nth-child(2) td:nth-child(1)").text()); // name
                            // System.out.println(e.select("table table tbody tr td a").text()); // acronym
                            System.out.println(e.select("tr:nth-child(2) td:nth-child(1)").text()); // name
                            System.out.println(e.select("tr:nth-child(1) td:nth-child(1) a").text()); // acronym
                            counter++;
                        }
                        if (counter == 2) {
                            // System.out.println(e.select("table table tbody tr:nth-child(3) td:nth-child(2)").text()); // location
                            System.out.println(e.select("tr:nth-child(3) td:nth-child(2)").text()); // location
                            counter--;
                        }
                    }
                }
            }

                //System.out.println(content);

                //IMPORTANT! Do not change the following:
                Thread.sleep(DELAY*1000); //rate-limit the queries

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * Given a string URL returns a string with the page contents
     * Adapted from example in
     * http://docs.oracle.com/javase/tutorial/networking/urls/readingWriting.html
     * @param link
     * @return
     * @throws IOException
     */
    public static String getPageFromUrl(String link) throws IOException {
        URL thePage = new URL(link);
        URLConnection yc = thePage.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                yc.getInputStream()));
        String inputLine;
        String output = "";
        while ((inputLine = in.readLine()) != null) {
            output += inputLine + "\n";
        }
        in.close();
        return output;
    }



}


