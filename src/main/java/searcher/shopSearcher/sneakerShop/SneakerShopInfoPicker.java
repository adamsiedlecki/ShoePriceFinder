package searcher.shopSearcher.sneakerShop;

import config.Config;
import org.apache.commons.lang3.math.NumberUtils;
import searcher.InfoPicker;

import java.math.BigDecimal;

public class SneakerShopInfoPicker implements InfoPicker {

    @Override
    public String getImageUrl(String html) {
        String sectionForSubstring = "_8Nfi4s z-pdp__escape-grid";
        String htmlImgClass = "Q8HVfj oMyDaX hsKyRV _8Nfi4s BQJRnm uijqg-";
        int sectionStart = html.indexOf(sectionForSubstring);
        if (sectionStart == -1) {
            return Config.HTML_ELEMENT_CANNOT_BE_FOUND;
        }
        html = html.substring(sectionStart);
        int imgStart = html.indexOf(htmlImgClass);
        if (imgStart == -1) {
            return Config.HTML_ELEMENT_CANNOT_BE_FOUND;
        }
        html = html.substring(imgStart);
        String start = "src=\"";
        html = html.substring(html.indexOf(start) + start.length());
        String imageUrl = html.substring(0, html.indexOf("\""));

        if (imageUrl.length() > Config.MAX_URL_LENGTH) {
            return Config.HTML_ELEMENT_CANNOT_BE_FOUND;
        }
        return imageUrl;
    }

    @Override
    public BigDecimal getPrice(String html) {
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

                if (NumberUtils.isCreatable(price)) {
                    return new BigDecimal(price);
                } else {
                    System.out.println("PRICE " + price + " IS NOT CREATABLE");
                }

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

                if (NumberUtils.isCreatable(price)) {
                    return new BigDecimal(price);
                } else {
                    System.out.println("PRICE " + price + " IS NOT CREATABLE");
                }
            } else {
                System.out.println(Config.HTML_ELEMENT_CANNOT_BE_FOUND);
            }
        }

        return BigDecimal.ZERO;
    }

    @Override
    public String getName(String html) {
        String name;
        String nameBegin = "BicgmA\" tag=\"h1\">";
        int startIndex = html.lastIndexOf(nameBegin);
        startIndex = startIndex + nameBegin.length();
        int endIndex = html.indexOf("</h1>");

        if (startIndex != -1 && endIndex != -1) {
            name = html.substring(startIndex, endIndex);
        } else {
            name = Config.HTML_ELEMENT_CANNOT_BE_FOUND;
        }
        if (name.length() > Config.MAX_SHOE_NAME_LENGTH) {
            return name.substring(0, Config.MAX_SHOE_NAME_LENGTH);
        } else {
            return name;
        }
    }
}
