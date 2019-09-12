package artistenverein.Highlights;

import org.salespointframework.useraccount.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;

import artistenverein.Veranstaltungen.Bewertung;
import artistenverein.Veranstaltungen.EntityVeranstaltung;

/**
 * Die Klasse Highlight dient zur Sammlung der Daten, die zu einer Veranstaltung
 * auf der Hauptseite angezeigt werden
 * 
 */
public class Highlight {

	private EntityVeranstaltung veranstaltung;
	private UserAccount kunde;
	private Bewertung bewertung;

	/**
	 * Erstellt ein neues Highlight mit den angegeben Parametern
	 *
	 * @param v
	 *            die Veranstaltung die als Highlight angezeigt werden soll
	 * @param b
	 *            die Bewertung der Veranstaltung die angezeigt werden soll
	 */
	@Autowired
	public Highlight(EntityVeranstaltung v, Bewertung b) {
		this.veranstaltung = v;
		this.bewertung = b;
		this.kunde = b.getAutor();

	}

	/**
	 * Gibt die Veranstaltung des Highlights zurück
	 *
	 * @return die Veranstaltung des Highlights
	 */
	public EntityVeranstaltung getVeranstaltung() {
		return veranstaltung;
	}

	/**
	 * Gibt den Kunden zurück der die Bewertung abgegeben hat
	 *
	 * @return UserAccount des Kunden
	 */
	public UserAccount getKunde() {
		return kunde;
	}

	/**
	 * Gibt die Bewertung des Highlights zurück
	 *
	 * @return die Bewertung des Highlights
	 */
	public Bewertung getBewertung() {
		return bewertung;
	}

}
