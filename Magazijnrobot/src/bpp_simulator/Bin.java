package bpp_simulator;

import java.util.ArrayList;

/**
 *
 * @author leroy
 */
public class Bin {

    private int HuidigeGrootte = 0;
    private int MaxGrootte;
    private ArrayList<Product> Producten = new ArrayList<Product>();

    public Bin() {
    }

    public int getMaxGrootte() {
        return MaxGrootte;
    }

    public ArrayList<Product> getProducten() {
        return Producten;
    }

    public Bin(Product Product, int MaxGrootte) {
        Producten.add(Product);
        this.MaxGrootte = MaxGrootte;
        HuidigeGrootte += Product.getLength();
    }

    public Bin(Product Product) {
        Producten.add(Product);
    }

    public int getVolume() {
        int volume = 0;
        for (Product product : Producten) {
            volume += product.getLength();
        }
        return volume;
    }

    public Bin(int MaxGrootte) {
        this.MaxGrootte = MaxGrootte;
    }

    public void addProduct(Product Product) {
        Producten.add(Product);
        HuidigeGrootte += Product.getLength();
    }

    public boolean addProductCheck(Product Product) {
        if (HuidigeGrootte + Product.getLength() <= MaxGrootte) {
            addProduct(Product);
            return true;
        } else {
            return false; // item didn't fit
        }
    }

    public void removeProduct(Product Product) {
        HuidigeGrootte -= Product.getLength();
        Producten.remove(Product);
    }

    public int getHuidigeGrootte() {
        return HuidigeGrootte;
    }

    public void setHuidigeGrootte(int HuidigeGrootte) {
        this.HuidigeGrootte = HuidigeGrootte;
    }

    public void addHuidigeGrootte(int HuidigeGrootte) {
        this.HuidigeGrootte = Integer.sum(this.HuidigeGrootte, HuidigeGrootte);
    }

    public int getAantalProducten() {
        return Producten.size();
    }

    public Bin deepCopy() {
        Bin copy = new Bin();
        copy.Producten = new ArrayList<>(Producten);
        copy.HuidigeGrootte = HuidigeGrootte;
        copy.MaxGrootte = MaxGrootte;
        return copy;
    }

}
