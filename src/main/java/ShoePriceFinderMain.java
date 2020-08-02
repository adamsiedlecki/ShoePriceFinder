import config.Config;
import tool.BannerPrinter;

import java.util.Scanner;

public class ShoePriceFinderMain {
    public static void main(String[] args) {
        BannerPrinter.print();
        
        String shoeName;
        String size;

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter shoe name: ");
        shoeName = scanner.nextLine();
        if(shoeName==null || shoeName.isBlank() || shoeName.isEmpty()){
            shoeName = Config.DEFAULT_SHOE_NAME;
        }
        System.out.println("Enter your EU size (may not be used in case of some sites): ");
        size = scanner.nextLine();
        if(size==null || size.isBlank() || size.isEmpty()){
            size = Config.DEFAULT_SHOE_SIZE;
        }
    }
}
