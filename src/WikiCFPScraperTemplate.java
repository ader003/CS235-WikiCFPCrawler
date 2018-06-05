package src;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

public class WikiCFPScraperTemplate {
    public static int DELAY = 7;
    public static void main(String[] args) {
        prep_text_file();
    }

    public static void prep_text_file() {
        List<String> categories = Arrays.asList("data mining", "databases", "machine learning", "artificial intelligence");
        List<Element> rows = new ArrayList<>();
//        for (String c : categories) {

        try {
            //create the output file
            File file = new File("wikicfp_crawl.txt");
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            for (String c : categories) {
                int numOfPages = 20;

                //now start crawling the all 'numOfPages' pages
                for (int i = 1; i <= numOfPages; i++) {
                    //Create the initial request to read the first page
                    //and get the number of total results
                    String linkToScrape = "http://www.wikicfp.com/cfp/call?conference=" +
                            URLEncoder.encode(c, "UTF-8") + "&page=" + i;
                    //parse or store the content of page 'i' here in 'content'
                    String content = getPageFromUrl(linkToScrape);

                    Document doc = Jsoup.parse(content);
                    // put information from table table into tsv
                    Elements table_table = doc.select("table table tbody tr");
                    for (Element e : table_table) {
                        if (e.toString().contains("#e6e6e6") || e.toString().contains("#f6f6f6")) {
                            rows.add(e);
                            //                            System.out.println(e.select("tr:nth-child(2) td:nth-child(1)").text()); // name
                            //                            System.out.println(e.select("tr:nth-child(1) td:nth-child(1) a").text()); // acronym
                            //                            System.out.println(e.select("tr:nth-child(3) td:nth-child(2)").text()); // location
                        }
                    }
                    //IMPORTANT! Do not change the following:
                    Thread.sleep(DELAY * 1000); //rate-limit the queries
                }
            }
            // process rows
            for (int i = 0; i < rows.size(); i += 2) {
                // First Row of information
                Element row1 = rows.get(i);
                String acronym = row1.select("td:nth-child(1) a").text();
                String name = row1.select("td:nth-child(2)").text();

                // Second row of information
                Element row2 = rows.get(i + 1);
                String location = row2.select("td:nth-child(2)").text();
                System.out.println(acronym + '\t' + name + '\t' + location);
                writer.write(acronym + '\t' + name + '\t' + location + '\n');
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException i) {
            i.printStackTrace();
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


