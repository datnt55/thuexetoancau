package grab.com.thuexetoancau.model;

/**
 * Created by DatNT on 7/20/2017.
 */

public class Car {
    private int size;
    private String  name;
    private boolean isCar;
    private int image;
    private int price01way;
    private int price02way;
    private int price11way;
    private long totalPrice;
    private boolean selected;

    public Car(int size,boolean isCar, String name, int image, int price01way, int price02way, int price11way) {
        this.size = size;
        this.isCar = isCar;
        this.name = name;
        this.image = image;
        this.selected = false;
        this.price01way = price01way;
        this.price02way = price02way;
        this.price11way = price11way;
    }

    public Car(Car car) {
        this.size = car.size;
        this.name = car.name;
        this.image = car.image;
        this.selected = false;
        this.price01way = car.price01way;
        this.price02way = car.price02way;
        this.price11way = car.price11way;
        this.isCar = car.isCar;
    }

    public boolean isCar() {
        return isCar;
    }

    public void setCar(boolean car) {
        isCar = car;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getPrice01way() {
        return price01way;
    }

    public void setPrice01way(int price01way) {
        this.price01way = price01way;
    }

    public int getPrice02way() {
        return price02way;
    }

    public void setPrice02way(int price02way) {
        this.price02way = price02way;
    }

    public int getPrice11way() {
        return price11way;
    }

    public void setPrice11way(int price11way) {
        this.price11way = price11way;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public long getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(long totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
