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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ZalandoSearcher implements Searcher {

    final String ADDRESS = "https://www.zalando.pl";
    final int TIMEOUT = 5000;
    // Strings that cannot be contained by desirable links
    final List<String> forbiddenList = List.of("faq", "dostawa", "polityka", "reklamacja", "marketing", "home", "tabela",
            "zwrotu", "odziez", "polityka", "obuwie", "prywatnosci");


    @Override
    public List<Offer> getOffers(String shoeName, boolean genderMale, String size) {
        ContentSearcher contentSearcher = new ContentSearcher();
        if(genderMale){
            shoeName = shoeName.replace(" ", "+"); // the cannot be whitespace in url
            Set<URL> linksOnPage = contentSearcher.getLinksOnPageThatStartsWithSlash(ADDRESS + "/mezczyzni/"+"__rozmiar-"+size.replace(".","~")+"/?q=" + shoeName, TIMEOUT, ADDRESS);
            List<URL> listWithoutForbidden = getListWithoutForbidden(linksOnPage);
            System.out.println(listWithoutForbidden);
        }
        return null;
    }

    private List<URL> getListWithoutForbidden(Set<URL> urls) {
        List<URL> urlList = new ArrayList<>(urls);
        List<URL> urlsToDelete = new ArrayList<>();
        for (int i = 0; i < urlList.size(); i++) {
            URL url = urlList.get(i);
            for (String forbidden : forbiddenList) {
                if (url.toString().contains(forbidden)) {
                    //urlList.remove(i);
                    urlsToDelete.add(url);
                    break;
                }

            }
        }
        for(URL url: urlsToDelete){
            urlList.remove(url);
        }
        return urlList;
    }
}
