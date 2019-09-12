package artistenverein.Lagerverwaltung;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.salespointframework.catalog.Catalog;

/**
 * Das Interface ArtikelKatalog erweiter die Salespoint-Klasse Catalog<Artikel>
 * um die Funktion findByPattern um die Suche nach einem Substring des Namens
 * oder der Beschreibung zu ermöglichen
 * 
 * @author Emanuel Kern
 */
public interface ArtikelKatalog extends Catalog<Artikel> {

	/**
	 * Findet alle gelisteten Artikel im Katalog, die im Namen den übergebenen String enthalten
	 * und sortiert die zurückgegebenen Artikel entsprechend der Angabe
	 * <p>
	 * Sortierungen:
	 * <ul>alpha: Alphabetisch A-Z
	 * <ul>rev_alpha: Alphabetisch Z-A
	 * <ul>price: nach Preis aufsteigend
	 * <ul>rev_price: nach Preis absteigend
	 * <li>
	 *
	 * @param suche
	 *            der zu suchende String
	 * @param sortierung
	 * 			die gewünschte sortierung als String
	 * @return ein Iterable<Artikel> aller gefundenen Artikel
	 */
	public default Iterable<Artikel> findAndSort(String suche, String sortierung) {
		
		String search;
		String sort;
		
		if (suche == null) {
			search = "";
		} else {
			search = suche;
		}
		
		if (sortierung == null) {
			sort = "";
		} else {
			sort = sortierung;
		}
		
		Iterable<Artikel> all = this.findAllListed();
		ArrayList<Artikel> result = new ArrayList<Artikel>();
		for (Artikel artikel : all) {
			if (artikel.getName().toLowerCase().contains(search.toLowerCase())) {
				result.add(artikel);
			}
		}
		switch (sort) {
			case "alpha":
				Collections.sort(result, new NameComparator());
				break;
			case "rev_alpha":
				Collections.sort(result, new NameComparator());
				Collections.reverse(result);
				break;
			case "price":
				Collections.sort(result, new PriceComparator());
				break;
			case "rev_price":
				Collections.sort(result, new PriceComparator());
				Collections.reverse(result);
				break;
			default:
				break;
		}
		return result;
	}
	
	/**
	 * Findet alle gelisteten Artikel im Katalog
	 *
	 * @return ein Iterable<Artikel> aller gelisteten Artikel
	 */
	public default Iterable<Artikel> findAllListed() {
		List<Artikel> artikel = new ArrayList<Artikel>();
		for (Artikel a : findAll()) {
			if (a.getGelisted()) {
				artikel.add(a);
			}
		}
		return artikel;
	}

}
