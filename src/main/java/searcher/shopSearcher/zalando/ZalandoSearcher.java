package searcher.shopSearcher.zalando;

import config.Config;
import data.Offer;
import http.ContentSearcher;
import searcher.Searcher;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ZalandoSearcher implements Searcher {

    final String ADDRESS = "https://www.zalando.pl";
    // if url contains forbidden but common string and its length is lower that URL_BORDER_LENGTH, it should be removed
    private static final int URL_BORDER_LENGTH = 40;
    final int TIMEOUT = 5000;
    final String SHOP_NAME = "Zalando";
    // Strings that cannot be contained by desirable links
    final List<String> forbiddenList = List.of("faq", "dostawa", "polityka", "reklamacja", "marketing", "home", "tabela",
            "zwrotu", "odziez", "polityka", "obuwie", "prywatnosci", "okazje", "akcesoria", "cart", "rozmiar",
            "wishlist", "myaccount", "upominkowe", "activation", "firmy", "marki", "regulamin", "outfits");
    final List<String> forbiddenButCommonList = List.of("sport");

    @Override
    public List<Offer> getOffers(String shoeName, boolean genderMale, String size) {
        ContentSearcher contentSearcher = new ContentSearcher();

        String shoeNameForUrl = shoeName.replace(" ", "+"); // the cannot be whitespace in url
        Set<URL> linksOnPage;
        if (genderMale) {
            linksOnPage = contentSearcher.getLinksOnPageThatStartsWithSlash(ADDRESS + "/mezczyzni/"
                    + "__rozmiar-" + size.replace(".", "~") + "/?q=" + shoeNameForUrl, TIMEOUT, ADDRESS);

        } else {
            linksOnPage = contentSearcher.getLinksOnPageThatStartsWithSlash(ADDRESS + "/kobiety/"
                    + "__rozmiar-" + size.replace(".", "~") + "/?q=" + shoeNameForUrl, TIMEOUT, ADDRESS);
        }
        List<URL> listWithoutForbidden = getListWithoutForbidden(linksOnPage);
        listWithoutForbidden = getShortLinksAway(listWithoutForbidden);
        //listWithoutForbidden.forEach(System.out::println);

        List<Offer> offers = new ArrayList<>();
        for (URL url : listWithoutForbidden) {
            String content = contentSearcher.getContent(url.toString(), TIMEOUT);
            Offer offer = getOffer(content, url, shoeName);
            if (offer.getName().equals(Config.HTML_ELEMENT_CANNOT_BE_FOUND) ||
                    offer.getImageUrl().equals(Config.HTML_ELEMENT_CANNOT_BE_FOUND) ||
                    offer.getUrl().equals(Config.HTML_ELEMENT_CANNOT_BE_FOUND)) {

                System.out.println("Offer is not complete. Url or name may be invalid");
            } else {
                offers.add(offer);
            }


        }
        return offers;
    }

    private Offer getOffer(String html, URL url, String shoeName) {
        String name;
        BigDecimal price;
        String offerUrl = url.toString();
        String imageUrl;

        String lowerCaseName = shoeName.toLowerCase();
        String lowerCaseHtml = html.toLowerCase();

        if (!lowerCaseHtml.contains(lowerCaseName)) {
            System.out.println("WEBPAGE: " + url.toString() + "\n PROBABLY IS NOT A CORRECT RESULT");

        }
        ZalandoInfoPicker picker = new ZalandoInfoPicker();
        name = picker.getName(html);
        price = picker.getPrice(html);
        imageUrl = picker.getImageUrl(html);


        Offer offer = new Offer(name, price, imageUrl, offerUrl, SHOP_NAME);
        return offer;
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
            for (String forbiddenButCommon : forbiddenButCommonList) {
                if (url.toString().contains(forbiddenButCommon) && url.toString().length() < URL_BORDER_LENGTH) {
                    urlsToDelete.add(url);
                    break;
                }

            }
        }
        for (URL url : urlsToDelete) {
            urlList.remove(url);
        }
        return urlList;
    }

    private List<URL> getShortLinksAway(List<URL> urls) {
        List<URL> urlsToDelete = new ArrayList<>();
        for (URL url : urls) {
            if (url.toString().length() < URL_BORDER_LENGTH) {
                urlsToDelete.add(url);
            }
        }
        for (URL url : urlsToDelete) {
            urls.remove(url);
        }
        return urls;
    }
}
