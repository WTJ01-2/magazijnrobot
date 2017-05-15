package bpp_simulator;

import java.util.ArrayList;

public class Bin {

	private int CurrentSize = 0;
	private int MaxSize;
	private ArrayList<Product> Products = new ArrayList<>();

	public Bin() {
	}

	public int getMaxSize() {
		return MaxSize;
	}

	public ArrayList<Product> getProducts() {
		return Products;
	}
        // Constructor, gebruik Bin(Product, 10); of Bin(10); of Bin(Product);
	public Bin(Product Product, int MaxGrootte) {
		Products.add(Product);
		this.MaxSize = MaxGrootte;
		CurrentSize += Product.getLength();
	}

        
	public Bin(Product Product) {
		Products.add(Product);
	}
        
	public Bin(int MaxSize) {
		this.MaxSize = MaxSize;
	}

        // addProduct functie om een product toe te voegen aan de huidige bin
	public void addProduct(Product Product) {
		Products.add(Product);
		CurrentSize += Product.getLength();
	}

        // addProduct overloading met check om te kijken of het product wel in de doos past
	public boolean addProduct(Product Product, boolean Check) {
		if (Check) {
			if (CurrentSize + Product.getLength() <= MaxSize) {
				addProduct(Product);
				return true; // past wel
			} else {
				return false; // past niet
			}
		} else {
			return false;
		}
	}

	public void removeProduct(Product Product) {
		CurrentSize -= Product.getLength();
		Products.remove(Product);
	}

	public int getCurrentSize() {
		return CurrentSize;
	}

	public int getProductAmount() {
		return Products.size();
	}

        // Kopieeer huidige bin naar een nieuwe bin en die returnen
	public Bin deepCopy() {
		Bin copy = new Bin(MaxSize);
		copy.Products = new ArrayList<>(Products);
		copy.CurrentSize = CurrentSize;
		return copy;
	}
}
