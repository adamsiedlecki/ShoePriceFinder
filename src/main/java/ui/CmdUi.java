package ui;

import config.Config;
import data.Shoe;
import tool.BannerPrinter;

import java.util.Scanner;

public class CmdUi {

    public Shoe getShoeInfo() {
        String shoeName;
        String size;
        boolean genderMale = true;

        BannerPrinter.print();
        System.out.println(Config.USER_WARNING);

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter shoe name: ");
        shoeName = scanner.nextLine();
        if (shoeName == null || shoeName.isBlank() || shoeName.isEmpty()) {
            shoeName = Config.DEFAULT_SHOE_NAME;
        }
        System.out.println("Enter your EU size (with dot at decimal place if needed) (size may not be used in case of some sites): ");
        size = scanner.nextLine();
        if (size == null || size.isBlank() || size.isEmpty()) {
            size = Config.DEFAULT_SHOE_SIZE;
        }
        System.out.println("Enter your gender (m or f): ");
        String gender = scanner.nextLine();
        if (gender == null) {
            System.out.println("Incorrect gender, male used as default.");
        } else if (gender.equals("f")) {
            genderMale = false;
        }
        return new Shoe(shoeName, size, genderMale);
    }
}
