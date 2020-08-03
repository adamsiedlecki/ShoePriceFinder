package data;

import java.math.BigDecimal;

public class Offer {
    private final String name;
    private final BigDecimal price;
    private final String imageUrl;
    private final String url;
    private final String shopName;

    public Offer(String name, BigDecimal price, String imageUrl, String url, String shopName) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.url = url;
        this.shopName = shopName;
    }

    @Override
    public String toString() {
        return "Offer{" +
                "name='" + name + '\'' +
                ", price=" + price +
                ", imageUrl='" + imageUrl + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getUrl() {
        return url;
    }
}
