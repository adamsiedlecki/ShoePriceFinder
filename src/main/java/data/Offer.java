package data;

import java.math.BigDecimal;

public class Offer {
    private String name;
    private BigDecimal price;
    private String link;

    public Offer(String name, BigDecimal price, String link) {
        this.name = name;
        this.price = price;
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getLink() {
        return link;
    }
}
