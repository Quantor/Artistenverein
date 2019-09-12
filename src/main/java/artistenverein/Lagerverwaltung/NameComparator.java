package artistenverein.Lagerverwaltung;

import java.util.Comparator;

import org.salespointframework.catalog.Product;

/**
 * Die Klasse NameComparator implementiert das Java-Interface Comparable für die
 * Salespoint-Klasse Product um Elemente diesen Types anhand des Feldes name
 * vergleichen und ordnen zu können
 * 
 * @author Emanuel Kern
 */
public class NameComparator implements Comparator<Product> {

	@Override
	public int compare(Product o1, Product o2) {
		return o1.getName().compareTo(o2.getName());
	}
}