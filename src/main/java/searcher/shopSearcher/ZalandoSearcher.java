package searcher.shopSearcher;

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
    private static final int URL_BORDER_LENGTH = 35;
    final int TIMEOUT = 5000;
    final String SHOP_NAME = "Zalando";
    // Strings that cannot be contained by desirable links
    final List<String> forbiddenList = List.of("faq", "dostawa", "polityka", "reklamacja", "marketing", "home", "tabela",
            "zwrotu", "odziez", "polityka", "obuwie", "prywatnosci", "okazje", "akcesoria", "cart", "rozmiar",
            "wishlist", "myaccount", "upominkowe", "activation", "firmy", "marki", "regulamin");
    final List<String> forbiddenButCommonList = List.of("sport");

    @Override
    public List<Offer> getOffers(String shoeName, boolean genderMale, String size) {
        ContentSearcher contentSearcher = new ContentSearcher();

        shoeName = shoeName.replace(" ", "+"); // the cannot be whitespace in url
        Set<URL> linksOnPage;
        if (genderMale) {
            linksOnPage = contentSearcher.getLinksOnPageThatStartsWithSlash(ADDRESS + "/mezczyzni/"
                    + "__rozmiar-" + size.replace(".", "~") + "/?q=" + shoeName, TIMEOUT, ADDRESS);

        } else {
            linksOnPage = contentSearcher.getLinksOnPageThatStartsWithSlash(ADDRESS + "/kobiety/"
                    + "__rozmiar-" + size.replace(".", "~") + "/?q=" + shoeName, TIMEOUT, ADDRESS);
        }
        List<URL> listWithoutForbidden = getListWithoutForbidden(linksOnPage);
        //listWithoutForbidden.forEach(System.out::println);

        List<Offer> offers = new ArrayList<>();
        for (URL url : listWithoutForbidden) {
            String content = contentSearcher.getContent(url.toString(), TIMEOUT);
            Offer offer = getOffer(content, url);
            offers.add(offer);

        }
        return offers;
    }

    private Offer getOffer(String html, URL url) {
        String name;
        BigDecimal price;
        String offerUrl = url.toString();
        String imageUrl;

        name = getName(html);
        price = getPrice(html);
        imageUrl = getImageUrl(html);


        Offer offer = new Offer(name, price, imageUrl, offerUrl, SHOP_NAME);
        return offer;
    }

    private String getImageUrl(String html) {
        String sectionForSubstring = "_8Nfi4s z-pdp__escape-grid";
        String htmlImgClass = "Q8HVfj oMyDaX hsKyRV _8Nfi4s BQJRnm uijqg-";
        html = html.substring(html.indexOf(sectionForSubstring));
        html = html.substring(html.indexOf(htmlImgClass));
        String start = "src=\"";
        html = html.substring(html.indexOf(start) + start.length());
        html = html.substring(0, html.indexOf("\""));
        //System.out.println(html);

        return html;
    }

    private BigDecimal getPrice(String html) {
        // if on sale, html tags may be different, so:
        if (html.contains("taniej")) {
            String price;
            String nameBegin = "A95iT1 pDVUjz nmA88J _0uQAcH AHAcbe gzB009\">";
            int startIndex = html.indexOf(nameBegin);
            startIndex = startIndex + nameBegin.length();
            String end = "zł</span>";
            if (startIndex != -1) {
                price = html.substring(startIndex);
                price = price.substring(0, price.indexOf(end));
                price = price.replaceAll("\\s", "");
                price = price.replaceAll("\\u00A0", "");
                price = price.replaceAll("&#160;", "");
                price = price.replaceAll("(^\\h*)|(\\h*$)", "");
                price = price.replaceAll(",", ".");
                price = price.trim();

                return new BigDecimal(price);
            }
        } else {
            String price;
            String nameBegin = "Xb35xC\">";
            int startIndex = html.indexOf(nameBegin);
            startIndex = startIndex + nameBegin.length();
            String end = "zł</span>";
            if (startIndex != -1) {
                price = html.substring(startIndex);
                price = price.substring(0, price.indexOf(end));
                price = price.replaceAll("\\s", "");
                price = price.replaceAll("\\u00A0", "");
                price = price.replaceAll("&#160;", "");
                price = price.replaceAll("(^\\h*)|(\\h*$)", "");
                price = price.replaceAll(",", ".");
                price = price.trim();

                //System.out.println("|"+price+"|");
                return new BigDecimal(price);
            } else {
                System.out.println("PRICE CANNOT BE FOUND");
            }
        }

        return BigDecimal.ZERO;
    }

    private String getName(String html) {
        String name;
        String nameBegin = "BicgmA\" tag=\"h1\">";
        int startIndex = html.lastIndexOf(nameBegin);
        startIndex = startIndex + nameBegin.length();
        int endIndex = html.indexOf("</h1>");
        if (startIndex != -1 || endIndex != -1) {
            name = html.substring(startIndex, endIndex);
        } else {
            name = "NAME CANNOT BE FOUND";
        }
        return name;
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
        for(URL url: urlsToDelete){
            urlList.remove(url);
        }
        return urlList;
    }
}
