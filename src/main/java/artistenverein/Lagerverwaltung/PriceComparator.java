package artistenverein.Lagerverwaltung;

import java.util.Comparator;

import org.salespointframework.catalog.Product;

/**
 * Die Klasse PriceComparator implementiert das Java-Interface Comparable für
 * die Salespoint-Klasse Product um Elemente diesen Types anhand des Feldes
 * price vergleichen und ordnen zu können
 * 
 * @author Emanuel Kern
 */
public class PriceComparator implements Comparator<Product> {

	@Override
	public int compare(Product o1, Product o2) {
		return o1.getPrice().compareTo(o2.getPrice());
	}
}