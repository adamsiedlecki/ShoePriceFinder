import data.Offer;
import data.Shoe;
import excel.RaportCreator;
import searcher.shopSearcher.sneakerShop.SneakerShopSearcher;
import searcher.shopSearcher.zalando.ZalandoSearcher;
import ui.CmdUi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ShoePriceFinderMain {
    public static void main(String[] args) {
        CmdUi cmdUi = new CmdUi();
        Shoe shoe = cmdUi.getShoeInfo();

        String gender;
        if (shoe.isForMale()) {
            gender = "MALE";
        } else {
            gender = "FEMALE";
        }
        System.out.println("Searching started... ( " + shoe.getShoeName() + " " + gender + " " + shoe.getShoeSize() + " )");

        List<Offer> offers = new ArrayList<>();

//        NikeSearcher nikeSearcher = new NikeSearcher();
//        nikeSearcher.getOffers(shoeName, genderMale, size);

        ZalandoSearcher zalandoSearcher = new ZalandoSearcher();
        offers.addAll(zalandoSearcher.getOffers(shoe.getShoeName(), shoe.isForMale(), shoe.getShoeSize()));

        SneakerShopSearcher sneakerShopSearcher = new SneakerShopSearcher();
        offers.addAll(sneakerShopSearcher.getOffers(shoe.getShoeName(), shoe.isForMale(), shoe.getShoeSize()));

        //offers.forEach(System.out::println);

        printOffersAndCreateRaport(offers, shoe.getShoeName(), shoe.getShoeSize(), shoe.isForMale());
    }

    private static void printOffersAndCreateRaport(List<Offer> offers, String shoeName, String size, boolean genderMale) {
        offers.sort(Comparator.comparing(Offer::getPrice));
        offers.forEach(System.out::println);

        RaportCreator raportCreator = new RaportCreator();
        try {
            raportCreator.saveOffersToExcel(shoeName, size, genderMale, offers);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
