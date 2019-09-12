package artistenverein.Veranstaltungen;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.money.MonetaryAmount;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import org.salespointframework.catalog.Product;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.util.Assert;

import artistenverein.Veranstaltungen.EntityVeranstaltung;

/**
 * Die Klasse Buchung erstellt Buchungen mit jeweiligem Datum, Ort,
 * Veranstaltung, Preis und Kunden
 * 
 * @author Luisa Leopold
 */
@Entity
public class Buchung extends Product {

	private static final long serialVersionUID = 3602164805477720502L;
	private String ort;
	private LocalDateTime datum;
	private UUID buchungId = UUID.randomUUID();
	@OneToOne
	private EntityVeranstaltung veranstaltung;
	@OneToOne
	private UserAccount kunde;

	/**
	 * Ein Standardkonstruktor. Wird nur von Spring benötigt und sollte andernfalls nicht verwendet werden.
	 */
	@SuppressWarnings("unused")
	public Buchung() {
		// required by Spring
	}

	/**
	 * Erstellt eine neue Buchung mit den angegebenen Parametern
	 * 
	 * @param ort
	 *            Ort, an dem die Veranstaltng stattfinden soll ("Halle" für die
	 *            vereinseigene Halle) als String
	 * @param datum
	 *            Datum, an dem die Veranstaltung gebucht ist als LocalDateTime
	 *            Objekt
	 * @param veranstaltung
	 *            Veranstaltung, die gebucht wurde als EntityVeranstaltung
	 * @param kunde
	 *            Kunde, der die Veranstaltung gebucht hat als UserAccount
	 * @param preis
	 *            Preis, den der Kunde für die Veranstaltung bezahlen muss als
	 *            MonetaryAmount Objekt
	 * @param buchungId
	 *            Id der Buchung (wichtig, falls Veranstaltung über mehrere Tage
	 *            geht) als UUID Objekt
	 */
	public Buchung(String ort, LocalDateTime datum, EntityVeranstaltung veranstaltung, UserAccount kunde,
			MonetaryAmount preis, UUID buchungId) {
		super("buchung", preis);

		Assert.notNull(ort, "ort darf nicht null sein!");
		Assert.notNull(datum, "datum darf nicht null sein!");
		Assert.notNull(veranstaltung, "veranstaltung darf nicht null sein!");
		Assert.notNull(kunde, "kunde darf nicht null sein!");
		Assert.notNull(buchungId, "buchungId darf nicht null sein!");

		this.ort = ort;
		this.datum = datum;
		this.veranstaltung = veranstaltung;
		this.kunde = kunde;
		this.buchungId = buchungId;
	}

    /**
     * Gibt den Ort der Buchung zurück.
     *
     * @return
     *  Der Ort der Buchung.
     */
	public String getOrt() {
		return ort;
	}

	/**
	 * setzt den Ort auf den übergebenen Wert
	 * 
	 * @param ort
	 *            der neue Ort
	 */
	public void setOrt(String ort) {
		Assert.notNull(ort, "ort darf nicht null sein!");
		this.ort = ort;
	}

    /**
     * Gibt das Datum der Buchung zurück.
     *
     * @return
     *  Das Datum der Buchung.
     */
	public LocalDateTime getDatum() {
		return datum;
	}

	/**
	 * setzt das Datum der Buchung auf den übergebenen Wert
	 * 
	 * @param datum
	 *            das neue Datum
	 */
	public void setDatum(LocalDateTime datum) {
		Assert.notNull(datum, "datum darf nicht null sein!");
		this.datum = datum;
	}

    /**
     * Gibt das Enddatum der Buchung zurück.
     *
     * @return
     *  Das Enddatum der Buchung.
     */
	public LocalDateTime getEndDatum() {
		return getDatum().plusMinutes(veranstaltung.getDauer());
	}

    /**
     * Gibt die Veranstaltung die gebucht wurde/wird zurück.
     *
     * @return
     *  Die Veranstaltung.
     */
	public EntityVeranstaltung getVeranstaltung() {
		return veranstaltung;
	}

	/**
	 * setzt die Veranstaltung auf den übergebenen Wert
	 * 
	 * @param veranstaltung
	 *            die neue Veranstaltung
	 */
	public void setVeranstaltung(EntityVeranstaltung veranstaltung) {
		Assert.notNull(veranstaltung, "veranstaltung darf nicht null sein!");
		this.veranstaltung = veranstaltung;
	}

	/**
	 * setzt den Kunden auf den übergebenen Wert
	 * 
	 * @param kunde
	 *            der neue Kunde
	 */
	public void setKunde(UserAccount kunde) {
		Assert.notNull(kunde, "kunde darf nicht null sein!");
		this.kunde = kunde;
	}

    /**
     * Gibt den Nutzeraccount des Kunden, der die Buchung erstellt hat zurück.
     *
     * @return
     *  Der Nutzeraccount des Kunden
     */
	public UserAccount getKunde() {
		return kunde;
	}

    /**
     * Gibt den Namen der gebuchten Veranstaltung zurück.
     *
     * @return
     *  Der Name der gebuchten Veranstaltung als String.
     */
	@Override
	public String toString() {
		return veranstaltung.getName();
	}

    /**
     * Gibt die BuchungsID zurück.
     *
     * @return
     *  Die BuchungsID.
     */
	public UUID getBuchungId() {
		return buchungId;
	}

}