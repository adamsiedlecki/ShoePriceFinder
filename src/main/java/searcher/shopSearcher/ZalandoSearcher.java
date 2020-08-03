package searcher.shopSearcher;

import data.Offer;
import http.ContentSearcher;
import searcher.Searcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Set;

public class ZalandoSearcher implements Searcher {

    final String ADDRESS = "https://www.zalando.pl";
    final int TIMEOUT = 5000;


    @Override
    public List<Offer> getOffers(String shoeName, boolean genderMale, String size) {
        ContentSearcher contentSearcher = new ContentSearcher();
        if(genderMale){
            shoeName = shoeName.replace(" ", "+"); // the cannot be whitespace in url
            Set<URL> linksOnPage = contentSearcher.getLinksOnPageThatStartsWithSlash(ADDRESS + "/mezczyzni/?q=" + shoeName, TIMEOUT, ADDRESS);
            System.out.println(linksOnPage);
        }
        return null;
    }
}
