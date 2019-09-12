package artistenverein.Lagerverwaltung;

import javax.persistence.Entity;

import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
import org.springframework.util.Assert;

/**
 * Die Klasse Artikel erweitert die Salespoint-Klasse Product um die Felder bild
 * und beschreibung
 * 
 * @author Emanuel Kern
 */
@Entity
public class Artikel extends Product {

	private static final long serialVersionUID = 609406522455389082L;

	private String bild;

	private String beschreibung;
	
	private boolean gelisted = true;

	@SuppressWarnings("unused")
	private Artikel() {
	}

	/**
	 * Erstellt einen neuen Artikel mit den angegeben Parametern
	 *
	 * @param name
	 *            der Name des Artikels als String
	 * @param bild
	 *            der Dateiname des Bildes OHNE Dateiendung
	 * @param preis
	 *            der Preis des Artikels als Money-Objekt
	 * @param beschreibung
	 *            die Beschreibung des Artikels als String
	 */
	public Artikel(String name, String bild, Money preis, String beschreibung) {

		super(name, preis);
		Assert.notNull(bild, "bild darf nicht null sein!");
		Assert.notNull(beschreibung, "beschreibung darf nicht null sein!");
		Assert.hasText(bild, "bild darf nicht leer sein!");
		Assert.hasText(beschreibung, "beschreibung darf nicht leer sein!");

		this.bild = bild;
		this.beschreibung = beschreibung;
	}

	/**
	 * Gibt den Namen des Bildes als String zurück
	 *
	 * @return der Name des Bildes als String
	 */
	public String getBild() {
		return bild;
	}

	/**
	 * Setzt das Bild des Artikels auf den übergebenen String
	 * 
	 * @param bild
	 *            der Dateiname des Bildes OHNE Dateiendung
	 */
	public void setBild(String bild) {
		Assert.notNull(bild, "bild darf nicht null sein!");
		Assert.hasText(bild, "bild darf nicht leer sein!");
		this.bild = bild;
	}

	/**
	 * Gibt die Beschreibung des Artikels als String zurück
	 *
	 * @return die Beschreibung des Artikels als String
	 */
	public String getBeschreibung() {
		return beschreibung;
	}

	/**
	 * Setzt die Beschreibung des Artikels auf den übergebenen String
	 * 
	 * @param beschreibung
	 *            die Beschreibung des Artikels als String
	 */
	public void setBeschreibung(String beschreibung) {
		Assert.notNull(beschreibung, "beschreibung darf nicht null sein!");
		Assert.hasText(beschreibung, "beschreibung darf nicht leer sein!");
		this.beschreibung = beschreibung;
	}

	/**
	 * Vergleicht diesen Artikel mit dem übergebenen anhand des Namens, überschreibt
	 * die compareTo() Operation von Product
	 *
	 * @param o1
	 *            ein Objekt das mit dem Artikel
	 * @return ein negativer int, 0, oder ein positiver int wenn dieses Objekt
	 *         kleiner als, gleich, oder größer als das spezifizierte Objekt ist.
	 * @throws NullPointerException
	 *             if the specified object is null
	 * @throws ClassCastException
	 *             if the specified object's type prevents it from being compared to
	 *             this object.
	 */
	@Override
	public int compareTo(Product o1) {
		Artikel d = (Artikel) o1;
		return this.getName().compareTo(d.getName());
	}
	
	/**
	 * Gibt den Status des Artikels zurück
	 *
	 * @return der Status des Artikels als boolean
	 */
	public boolean getGelisted() {
		return gelisted;
	}
	
	/**
	 * Setzt den Status des Artikels auf den übergebenen Wert
	 * 
	 * @param gelisted
	 *            der neue Status
	 */
	public void setGelisted(boolean gelisted) {
		this.gelisted = gelisted;
	}
}
