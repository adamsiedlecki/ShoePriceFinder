package searcher;

import data.Offer;

import java.util.List;

public interface Searcher {
    List<Offer> getOffers(String shoeName, boolean genderMale, String size);
}
