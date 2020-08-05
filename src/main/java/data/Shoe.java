package data;

public class Shoe {
    private final String shoeName;
    private final String shoeSize;
    private final boolean isForMale;

    public Shoe(String shoeName, String shoeSize, boolean isForMale) {
        this.shoeName = shoeName;
        this.shoeSize = shoeSize;
        this.isForMale = isForMale;
    }

    public String getShoeName() {
        return shoeName;
    }

    public String getShoeSize() {
        return shoeSize;
    }

    public boolean isForMale() {
        return isForMale;
    }
}
