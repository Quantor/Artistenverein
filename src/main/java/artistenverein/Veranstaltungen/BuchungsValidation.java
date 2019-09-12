package artistenverein.Veranstaltungen;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.util.Assert;

/**
 * Ein Formular zur Validierung der Eingaben beim Erstellen einer Buchung
 * 
 * @author Luisa Leopold
 */
public class BuchungsValidation {

	@NotEmpty(message = "{BuchungsValidation.datum.NotEmpty}")
	private String datum;
	@NotEmpty(message = "{BuchungsValidation.zeit.NotEmpty}")
	private String zeit;
	@NotEmpty(message = "{BuchungsValidation.ortwahl.NotEmpty}")
	private String ortwahl;
	private String ort;

	/**
	 * Überprüft, falls "außerhalb" ausgewählt wurde, ob ein konkreter Ort darunter
	 * angegeben wurde
	 * 
	 * @return false, wenn bei der Wahl von "außerhalb" kein konkreter Ort angegeben
	 *         wurde true, wenn entweder "Halle" ausgewählt wurde oder ein konkreter
	 *         Ort für "außerhalb" angegeben wurde
	 */
	public boolean validierungOrt() {
		if (ortwahl.equals("außerhalb")) {
			return (!ort.isEmpty());
		}
		return true;
	}

	/**
	 * Gibt das Datum der Buchung zurück.
	 *
	 * @return Das Datum der Buchung.
	 */
	public String getDatum() {
		return datum;
	}

	/**
	 * setzt das Datum auf den übergebenen Wert
	 * 
	 * @param datum
	 *            das neue Datum
	 */
	public void setDatum(String datum) {
		Assert.notNull(datum, "datum darf nicht null sein!");
		this.datum = datum;
	}

	/**
	 * Gibt den Ort der Buchung zurück.
	 *
	 * @return Der Ort der Buchung.
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
	 * Gibt die Zeit der Buchung zurück.
	 *
	 * @return Die Zeit der Buchung.
	 */
	public String getZeit() {
		return zeit;
	}

	/**
	 * setzt die Zeit auf den übergebenen Wert
	 * 
	 * @param zeit
	 *            die neue Zeit
	 */
	public void setZeit(String zeit) {
		Assert.notNull(zeit, "zeit darf nicht null sein!");
		this.zeit = zeit;
	}

	/**
	 * setzt die Ortwahl auf den übergebenen Wert
	 * 
	 * @param ortwahl
	 *            die neue Ortwahl
	 */
	public void setOrtwahl(String ortwahl) {
		Assert.notNull(ortwahl, "ortwahl darf nicht null sein!");
		this.ortwahl = ortwahl;
	}

	/**
	 * Gibt die Ortwahl der Buchung zurück.
	 *
	 * @return Die Ortwahl der Buchung.
	 */
	public String getOrtwahl() {
		return ortwahl;
	}
}
