package artistenverein.Veranstaltungen;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.util.Assert;

import artistenverein.Lagerverwaltung.Artikel;

/**
 * Die Klasse Artikel erweitert die Salespoint-Klasse Product um die Felder
 * beschreibung, dauer, startDatum, endDatum, mittelwertBewertung, tage und
 * VeranstaltungsType
 * 
 * @author Luisa Leopold
 */
@Entity
public class EntityVeranstaltung extends Product {

	private static final long serialVersionUID = 4660334975788813925L;

	/**
	 * Die Enumeration VeranstaltungsType dient zur Bestimmung des Typs der
	 * Veranstaltung
	 * 
	 * @author Luisa Leopold
	 */
	public static enum VeranstaltungsType {
		WORKSHOP, SHOW;
	}

	private String beschreibung;
	private int dauer;
	private LocalDateTime startDatum;
	private LocalDateTime endDatum;
	private double mittelwertBewertung;
	private int tage;
	private VeranstaltungsType type;
	private String gruppenname;
	@ManyToMany
	private Set<UserAccount> artisten = new HashSet<>();
	@OneToMany(cascade = CascadeType.ALL)
	private List<Bewertung> bewertungen = new ArrayList<>();
	@OneToMany(cascade = CascadeType.ALL)
	private List<Kommentar> kommentare = new ArrayList<>();
	@OneToMany(cascade = CascadeType.ALL)
	private Set<Artikel> artikellist = new HashSet<>();

	private static final double ROUNDING_PRECISION = 100.0;

	@SuppressWarnings("unused")
	EntityVeranstaltung() {
	}

	/**
	 * Erstellt eine neue Veranstaltung mit den angegeben Parametern
	 *
	 * @param titel
	 *            der Name der Veranstaltung
	 * @param preis
	 *            der Preis der Veranstaltung als Money-Objekt
	 * @param beschreibung
	 *            die Beschreibung der Veranstaltung als String
	 * @param dauer
	 *            die Dauer einer einzelnen Veranstaltung als Integer
	 * @param type
	 *            der Typ einer Veranstaltung (Workshop oder Show) als
	 *            VeranstaltungsType
	 * @param startDatum
	 *            das Datum ab welchem die Veranstaltung anngeboten werden soll als
	 *            LocalDateTime- Objekt
	 * @param endDatum
	 *            das Datum bis zu welchem die Veranstaltung angeboten werden soll
	 *            als LocalDateTime- Objekt
	 * @param artisten
	 *            die Artistengruppe oder der einzelne Arist, welcher die
	 *            Veranstaltung anbietet in einem Set<UserAccount>
	 * @param tage
	 *            bei einem Workshop wie viele Tage dieser gehen soll (bei einer
	 *            Show automatisch 1) als Integer
	 */
	public EntityVeranstaltung(String titel, Money preis, String beschreibung, int dauer, VeranstaltungsType type,
			LocalDateTime startDatum, LocalDateTime endDatum, Set<UserAccount> artisten, int tage, String gruppenname) {
		super(titel, preis);
		Assert.notNull(beschreibung, "beschreibung darf nicht null sein!");
		// Assert.notNull(dauer, "dauer darf nicht null sein!");
		Assert.notNull(type, "type darf nicht null sein!");
		Assert.notNull(startDatum, "startDatum darf nicht null sein!");
		Assert.notNull(endDatum, "endDatum darf nicht null sein!");
		Assert.notNull(artisten, "artisten darf nicht null sein!");
		Assert.hasText(beschreibung, "beschreibung darf nicht leer sein!");

		this.beschreibung = beschreibung;
		this.type = type;
		this.dauer = dauer;
		this.startDatum = startDatum;
		this.endDatum = endDatum;
		this.artisten = artisten;
		this.tage = tage;
		this.gruppenname = gruppenname;
		if (!bewertungen.isEmpty()) {
			this.mittelwertBewertung = this.setBewertung();
		} else {
			this.mittelwertBewertung = 0;
		}
	}

	private double setBewertung() {
		int gesamt = 0;
		if (bewertungen != null) {
			for (int i = 0; i < bewertungen.size(); i++) {
				gesamt = gesamt + bewertungen.get(i).getBewertung();
			}
			return ((double) gesamt / (double) bewertungen.size());
		}
		return 0;
	}

	public Set<UserAccount> getArtisten() {
		return artisten;
	}

	/**
	 * setzt die veranstaltenden Artisten auf die übergebenen Artisten
	 * 
	 * @param ua
	 *            die neuen Artisten der Veranstaltung
	 */
	public void setArtist(Set<UserAccount> ua) {
		Assert.notNull(ua, "artist darf nicht null sein!");
		this.artisten = ua;
	}

	public Set<Artikel> getArtikellist() {
		return artikellist;
	}

	/**
	 * setzt die Artikelliste auf die übergebene Liste
	 * 
	 * @param artikellist
	 *            die neue Artikelliste
	 */
	public void setArtikellist(Set<Artikel> artikellist) {
		Assert.notNull(artikellist, "artikellist darf nicht null sein!");
		this.artikellist = artikellist;
	}

	public String getBeschreibung() {
		return beschreibung;
	}

	/**
	 * setzt die Beschreibung der Veranstaltung auf den übergebenen Wert
	 * 
	 * @param beschreibung
	 *            die neue Beschreibung
	 */
	public void setBeschreibung(String beschreibung) {
		Assert.notNull(beschreibung, "beschreibung darf nicht null sein!");
		this.beschreibung = beschreibung;
	}

	public int getDauer() {
		return dauer;
	}

	/**
	 * setzt die Dauer der Veranstaltung auf den übergebenen Wert
	 * 
	 * @param dauer
	 *            die neue Dauer
	 */
	public void setDauer(int dauer) {
		this.dauer = dauer;
	}

	public VeranstaltungsType getType() {
		return type;
	}

	/**
	 * Ordnet der Veranstaltung eine neue Bewertung zu und berechnet den aktuellen
	 * Mittelwert der Bewertungen neu
	 * 
	 * @param bewertung
	 *            die abgegebene Bewertung als Bewertung
	 */
	public void addBewertung(Bewertung bewertung) {
		Assert.notNull(bewertung, "bewertung darf nicht null sein!");
		bewertungen.add(bewertung);
		this.mittelwertBewertung = this.setBewertung();
	}

	public Iterable<Bewertung> getBewertungen() {
		return bewertungen;
	}

	/**
	 * Ordnet der Veranstaltung einen neuen Kommentar zu und berechnet den aktuellen
	 * Mittelwert der Bewertungen neu
	 * 
	 * @param kommentar
	 *            der abgegebene Kommentar
	 */
	public void addKommentar(Kommentar kommentar) {
		Assert.notNull(kommentar, "kommentar darf nicht null sein!");
		kommentare.add(kommentar);
		this.mittelwertBewertung = this.setBewertung();
	}

	public Iterable<Kommentar> getKommentare() {
		return kommentare;
	}

	/**
	 * Ordnet der Veranstaltung einen neuen Artikel zu
	 * 
	 * @param artikel
	 *            der neue Artikel
	 */
	public void addArtikel(Artikel artikel) {
		Assert.notNull(artikel, "artikel darf nicht null sein!");
		artikellist.add(artikel);
	}

	public Iterable<Artikel> getArtikel() {
		return artikellist;
	}

	public LocalDateTime getStartDatum() {
		return startDatum;
	}

	public LocalDateTime getEndDatum() {
		return endDatum;
	}

	/**
	 * setzt das Startdatum der Veranstaltung auf den übergebenen Wert
	 * 
	 * @param startDatum
	 *            das neue Startdatum
	 */
	public void setStartDatum(LocalDateTime startDatum) {
		Assert.notNull(startDatum, "startDatum darf nicht null sein!");
		this.startDatum = startDatum;
	}

	/**
	 * setzt das Enddatum der Veranstaltung auf den übergebenen Wert
	 * 
	 * @param endDatum
	 *            das neue Enddatum
	 */
	public void setEndDatum(LocalDateTime endDatum) {
		Assert.notNull(endDatum, "endDatum darf nicht null sein!");
		this.endDatum = endDatum;
	}

	/**
	 * Gibt den gerundeten Mittelwert aller Bewertungen zurück
	 * 
	 * @return den gerundeten Mittelwert aller Bewertungen als double
	 */
	public double getMittelwertBewertung() {
		return Math.round(ROUNDING_PRECISION * this.mittelwertBewertung) / ROUNDING_PRECISION;
	}

	public double getMittelwert() {
		return mittelwertBewertung;
	}

	/**
	 * Vergleicht die Veranstaltungen anhand dem Mittelwert der jeweiligen
	 * Bewertungen
	 * 
	 * @return gibt die Veranstaltung mit dem jeweils größeren Mittelwert der
	 *         jeweiligen Bewertungen zurück
	 */
	static Comparator<EntityVeranstaltung> compareMittelwert() {
		return new Comparator<EntityVeranstaltung>() {
			public int compare(EntityVeranstaltung v1, EntityVeranstaltung v2) {
				return (Double.compare(v1.getMittelwertBewertung(), v2.getMittelwertBewertung())) * (-1);
			}
		};
	}

	public int getTage() {
		return tage;
	}

	/**
	 * setzt die Anzahl der Tage der Veranstaltung auf den übergebenen Wert
	 * 
	 * @param tage
	 *            die neue Anzahl Tage
	 */
	public void setTage(int tage) {
		this.tage = tage;
	}

	public String getGruppenname() {
		return gruppenname;
	}

	/**
	 * setzt den zugeordneten Gruppennamen auf den übergeben Wert
	 * 
	 * @param gruppenname
	 *            der neue Gruppenname
	 */
	public void setGruppenname(String gruppenname) {
		this.gruppenname = gruppenname;
	}

}
