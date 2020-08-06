package searcher.shopSearcher.sneakerShop;

import config.Config;
import data.Offer;
import http.ContentSearcher;
import org.apache.commons.lang3.math.NumberUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import searcher.Searcher;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SneakerShopSearcher implements Searcher {

    // if url contains forbidden but common string and its length is lower that URL_BORDER_LENGTH, it should be removed
    private static final int URL_BORDER_LENGTH = 40;
    final String ADDRESS = "https://sneakershop.pl";
    final int TIMEOUT = 5000;
    final String SHOP_NAME = "SneakerShop";
    // Strings that cannot be contained by desirable links
    final List<String> forbiddenList = List.of("faq", "dostawa", "polityka", "reklamacja", "marketing", "home", "tabela",
            "zwrotu", "odziez", "polityka", "obuwie", "prywatnosci", "okazje", "akcesoria", "cart", "rozmiar",
            "wishlist", "myaccount", "upominkowe", "activation", "firmy", "marki", "regulamin", "outfits");
    final List<String> forbiddenButCommonList = List.of("sport");

    @Override
    public List<Offer> getOffers(String shoeName, boolean genderMale, String size) {
        ContentSearcher contentSearcher = new ContentSearcher();

        String shoeNameForUrl = shoeName.replace(" ", "+"); // the cannot be whitespace in url
        String addressWithParams = ADDRESS + "/pl/search.html?filter_sizes=760&filter_traits[1343913850]=GENDER" +
                "&filter_pricerange=&text=" + shoeNameForUrl;

        /*
        760 is 40cm
        * filter_traits[1343913850]=86 86 is for women, 87 for men
        * */

        //addressWithParams = addressWithParams.replace("SIZE", ""+getSizeParam(size));
        String gender = genderMale ? "87" : "86";
        addressWithParams = addressWithParams.replace("GENDER", gender);

        //System.out.println(addressWithParams);

        List<Offer> offers = new ArrayList<>();

        Document doc = null;
        try {
            doc = Jsoup.connect(addressWithParams).get();
            List<Element> liList = doc.getElementsByClass("filter_items_1340356124").get(0).getElementsByTag("li");
            for (Element li : liList) {
                Element wrapper = li.getElementsByClass("filter_name_wrapper").get(0);
                String sizeText = wrapper.getElementsByTag("span").attr("data-filter");
                // example sizeText: 38.5 - 24 cm
                String sizeFragment = sizeText.substring(0, 4);
                sizeFragment.trim();
                if (sizeFragment.equals(size)) {
                    // filter_quantity_1340356124_val758_quantity
                    String encodedSize = wrapper.getElementsByTag("span").attr("id");
                    encodedSize = encodedSize.replaceAll("val", "").split("_")[3];
                    addressWithParams = addressWithParams.replace("760", encodedSize);
                }
            }
            doc = Jsoup.connect(addressWithParams).get();
            System.out.println(addressWithParams);

            if (doc == null) {
                return List.of();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println(doc.text());
        Elements elements = doc.getElementsByClass("product_wrapper_sub");
        //System.out.println(elements.toString());
        elements.forEach(product -> {
            Elements productNameElements = product.getElementsByClass("product-name");
            String productName = productNameElements.get(0).ownText();
            String productAddress = ADDRESS + productNameElements.get(0).attr("href");

            String productPrice = product.getElementsByClass("price").get(0).ownText();
            productPrice = productPrice.replaceAll("zł", "");
            productPrice = productPrice.replaceAll(",", ".");
            productPrice = productPrice.trim();

            String imageUrl = product.getElementsByTag("img").get(0).attr("data-src");

            if (NumberUtils.isCreatable(productPrice)) {
                offers.add(new Offer(productName, new BigDecimal(productPrice), imageUrl, productAddress, SHOP_NAME));
            } else {
                System.out.println(Config.PRICE_IS_NOT_A_CREATABLE_NUMBER + productPrice);
            }
        });

        return offers;
    }

    private String getSizeParam(String size) {
        int defaultParam = 771; // for 47.5 EU size, 772 for 48
        if (NumberUtils.isCreatable(size)) {
            float sizeFloat = Float.parseFloat(size);
            if (sizeFloat < 47.5) {
                while (sizeFloat != 47.5) {
                    sizeFloat = sizeFloat + 0.5f;
                    defaultParam--;
                }
                return "" + defaultParam;
            } else if (sizeFloat > 47.5) {
                while (sizeFloat != 47.5) {
                    sizeFloat = sizeFloat - 0.5f;
                    defaultParam++;
                }
                return "" + defaultParam;
            } else {
                return "" + defaultParam;
            }
        } else {
            System.out.println("Size is not creatable");
        }
        return "";
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
