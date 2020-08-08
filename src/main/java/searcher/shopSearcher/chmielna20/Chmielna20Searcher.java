package searcher.shopSearcher.chmielna20;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import config.Config;
import data.Offer;
import http.ContentSearcher;
import org.apache.commons.lang3.math.NumberUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import searcher.Searcher;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Chmielna20Searcher implements Searcher {

    final String ADDRESS = "https://sneakershop.pl";
    final int TIMEOUT = 15000;
    final String SHOP_NAME = "Chmielna20 / Warsaw Sneaker Store";
    /* Example url:
     * https://chmielna20.pl/products/air-force-1-obuwie-mezczyzna-47eu/keyword,air%20force%201/category,2/gender,M/size,47.5/sizetype,EU/item,48/sort,1?keyword=air%20force%201
     * */

    @Override
    public List<Offer> getOffers(String shoeName, boolean genderMale, String size) {
        ContentSearcher contentSearcher = new ContentSearcher();

        String rawLink = "https://chmielna20.pl/products/SHOE_NAME-obuwie-GENDER-SIZEeu/keyword,SHOE_NAME_SPACE/category,2/gender,GENDER_LETTER/size,SIZE/sizetype,EU/item,48/sort,1?keyword=SHOE_NAME_SPACE";

        String url = addInfoToUrl(rawLink, shoeName, genderMale, size);

        Document doc = null;

        doc = Jsoup.parse(getHtml(url));

        if (doc == null) {
            System.out.println(SHOP_NAME + " doc is null!");
            return List.of();
        }

        return getOffersFromDoc(doc);
    }

    private String getHtml(String url) {
        String response = null;
        WebClient client = new WebClient(BrowserVersion.CHROME);

        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(true);
        client.getOptions().setThrowExceptionOnFailingStatusCode(false);
        client.getOptions().setRedirectEnabled(true);
        client.getCache().setMaxSize(0);
        client.waitForBackgroundJavaScript(10000);
        client.setJavaScriptTimeout(10000);
        client.waitForBackgroundJavaScriptStartingBefore(10000);

        try {

            HtmlPage page = client.getPage(url);

            synchronized (page) {
                page.wait(7000);
            }
            //Print cookies for test purposes. Comment out in production.
            URL _url = new URL(url);
            for (Cookie c : client.getCookies(_url)) {
                System.out.println(c.getName() + "=" + c.getValue());
            }

            //This prints the content after bypassing Cloudflare.
            response = client.getPage(url).getWebResponse().getContentAsString();
            System.out.println(response);
        } catch (FailingHttpStatusCodeException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    }

    private List<Offer> getOffersFromDoc(Document doc) {
        List<Offer> offers = new ArrayList<>();
        System.out.println(doc.toString());
        Elements elements = doc.getElementsByClass("box-flag-item-down");
        elements.forEach(product -> {
            Elements productNameElements = product.getElementsByClass("products__item-name").tagName("b");
            String productName = productNameElements.get(0).ownText();
            String productAddress = product.getElementsByTag("a").get(0).attr("href");

            String productPrice = product.getElementsByClass("price__tag").get(0).ownText();
            productPrice = productPrice.replaceAll("zł", "");
            productPrice = productPrice.replaceAll(",", ".");
            productPrice = productPrice.trim();

            String imageUrl = product.getElementsByTag("img").get(0).attr("src");

            if (NumberUtils.isCreatable(productPrice)) {
                offers.add(new Offer(productName, new BigDecimal(productPrice), imageUrl, productAddress, SHOP_NAME));
            } else {
                System.out.println(Config.PRICE_IS_NOT_A_CREATABLE_NUMBER + productPrice);
            }
        });
        return offers;
    }

    private String addInfoToUrl(String url, String shoeName, boolean genderMale, String size) {
        String rawLink = "https://chmielna20.pl/products/SHOE_NAME-obuwie-GENDER-SIZEeu/keyword,SHOE_NAME_SPACE/category,2/" +
                "gender,GENDER_LETTER/size,SIZE/sizetype,EU/item,SIZE/sort,1?keyword=SHOE_NAME_SPACE";
        String shoeNameDashed = shoeName.replace(" ", "-");
        String shoeNameSpace = shoeName.replace(" ", "%20");
        String gender = genderMale ? "mezczyzna" : "kobieta";
        String genderLetter = genderMale ? "M" : "W";

        url = url.replaceFirst("SHOE_NAME", shoeNameDashed);
        url = url.replaceFirst("GENDER", gender);
        url = url.replaceAll("SIZE", size);
        url = url.replaceAll("SHOE_NAME_SPACE", shoeNameSpace);
        url = url.replaceFirst("GENDER_LETTER", genderLetter);
        return url;
    }

}
