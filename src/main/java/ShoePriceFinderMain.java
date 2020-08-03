import config.Config;
import data.Offer;
import searcher.shopSearcher.ZalandoSearcher;
import tool.BannerPrinter;

import java.util.List;
import java.util.Scanner;

public class ShoePriceFinderMain {
    public static void main(String[] args) {
        BannerPrinter.print();

        String shoeName;
        String size;
        boolean  genderMale = true;

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter shoe name: ");
        shoeName = scanner.nextLine();
        if(shoeName==null || shoeName.isBlank() || shoeName.isEmpty()){
            shoeName = Config.DEFAULT_SHOE_NAME;
        }
        System.out.println("Enter your EU size (with dot at decimal place if needed) (size may not be used in case of some sites): ");
        size = scanner.nextLine();
        if(size==null || size.isBlank() || size.isEmpty()){
            size = Config.DEFAULT_SHOE_SIZE;
        }
        System.out.println("Enter your gender (m or f): ");
        String gender = scanner.nextLine();
        if(gender==null ){
            System.out.println("Incorrect gender, male used as default.");
        } else if (gender.equals("f")) {
            genderMale = false;
        }
        if (genderMale) {
            System.out.println("Searching started... ( " + shoeName + " MALE " + size + " )");
        } else {
            System.out.println("Searching started... ( " + shoeName + " FEMALE " + size + " )");
        }

        ZalandoSearcher zalandoSearcher = new ZalandoSearcher();
        List<Offer> offers = zalandoSearcher.getOffers(shoeName, genderMale, size);
        offers.forEach(System.out::println);

    }
}
