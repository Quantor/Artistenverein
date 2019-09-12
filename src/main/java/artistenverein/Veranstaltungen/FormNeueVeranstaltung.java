package artistenverein.Veranstaltungen;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.util.Assert;

/**
 * Ein Formular zur Validierung der Eingaben beim Erstellen und Bearbeiten
 * einer Veranstaltung
 * 
 * @author Luisa Leopold
 */
public class FormNeueVeranstaltung {

	@NotEmpty(message = "{NeueVeranstaltung.name.NotEmpty}")
	private String name;

	@NotEmpty(message = "{NeueVeranstaltung.preis.NotEmpty}")
	private String preis;

	@NotEmpty(message = "{NeueVeranstaltung.dauer.NotEmpty}")
	private String dauer;
	
	@NotEmpty(message = "{NeueVeranstaltung.beschreibung.NotEmpty}")
	private String beschreibung;
	
	@NotEmpty(message = "{NeueVeranstaltung.startDatum.NotEmpty}")
	private String startDatum;
	
	@NotEmpty(message = "{NeueVeranstaltung.startZeit.NotEmpty}")
	private String startZeit;
	
	@NotEmpty(message = "{NeueVeranstaltung.endDatum.NotEmpty}")
	private String endDatum;
	
	@NotEmpty(message = "{NeueVeranstaltung.endZeit.NotEmpty}")
	private String endZeit;
	
	private String tage;
	
	private String gruppe;
	
	/**
	 * Setzt den Wert für Gruppe bei null auf einen leeren String
	 *
	 * @return
	 * 	entweder den Gruppennamen als String oder ein leerer String
	 */
	public String getGruppe() {
		if(gruppe == null) {
			return "";
		}
		return gruppe;
	}

    /**
     * Setzt die Gruppe der Veranstaltung
     *
     * @param gruppe
     *  Die Gruppe, auf die die Veranstaltung gehen soll.
     */
	public void setGruppe(String gruppe) {
		//Assert.notNull(gruppe, "gruppe darf nicht null sein!");
		this.gruppe = gruppe;
	}

    /**
     * Gibt den Namen der Veranstaltung zurück.
     *
     * @return
     *  Der Name der Veranstaltung
     */
	public String getName() {
		return name;
	}

    /**
     * Setzt den Namen der Veranstaltung.
     *
     * @param name
     *  Der Name, den die Veranstaltung bekommen soll.
     */
	public void setName(String name) {
		Assert.notNull(name, "name darf nicht null sein!");
		this.name = name;
	}

    /**
     * Gibt den Preis der Veranstaltung zurück.
     *
     * @return
     *  Der Preis der Veranstaltung.
     */
	public String getPreis() {
		return preis;
	}

    /**
     * Setzt den Preis der Veranstaltung.
     *
     * @param preis
     *  Der Preis, den die Veranstaltung haben soll.
     */
	public void setPreis(String preis) {
		Assert.notNull(preis, "preis darf nicht null sein!");
		this.preis = preis;
	}

    /**
     * Gibt die Beschreibung der Veranstaltung zurück.
     *
     * @return
     *  Die Beschreibung der Veranstaltung.
     */
	public String getBeschreibung() {
		return beschreibung;
	}

    /**
     * Setzt die Beschreibung der Veranstaltung.
     *
     * @param beschreibung
     *  Die neue Beschreibung der Veranstaltung.
     */
	public void setBeschreibung(String beschreibung) {
		Assert.notNull(beschreibung, "beschreibung darf nicht null sein!");
		this.beschreibung = beschreibung;
	}

    /**
     * Gibt die Dauer der Veranstaltung zurück.
     *
     * @return
     *  Die Dauer der Veranstaltung
     */
	public String getDauer() {
		return dauer;
	}

    /**
     * Setzt die Dauer der Veranstaltung.
     *
     * @param dauer
     *  Die neue Dauer der Veranstaltung.
     */
	public void setDauer(String dauer) {
		Assert.notNull(dauer, "dauer darf nicht null sein!");
		this.dauer = dauer;
	}

    /**
     * Gibt das Startdatum der Veranstaltung zurück.
     *
     * @return
     *  Das Startdatum der Veranstaltung.
     */
	public String getStartDatum() {
		return startDatum;
	}

    /**
     * Setzt das Startdatum der Veranstaltung.
     *
     * @param startDatum
     *  Das neue Startdatum der Veranstaltung.
     */
	public void setStartDatum(String startDatum) {
		Assert.notNull(startDatum, "startDatum darf nicht null sein!");
		this.startDatum = startDatum;
	}

    /**
     * Setzt die Startzeit der Veranstaltung.
     *
     * @param startZeit
     *  Die neue Startzeit der Veranstaltung.
     */
	public void setStartZeit(String startZeit) {
		Assert.notNull(startZeit, "startZeit darf nicht null sein!");
		this.startZeit = startZeit;
	}

    /**
     * Gibt die Startzeit der Veranstaltung zurück.
     *
     * @return
     *  Die Startzeit der Veranstaltung.
     */
	public String getStartZeit() {
		return startZeit;
	}

    /**
     * Gibt das Enddatum der Veranstaltung zurück.
     *
     * @return
     *  Das Enddatum der Veranstaltung.
     */
	public String getEndDatum() {
		return endDatum;
	}

    /**
     * Setzt das Enddatum der Veranstaltung.
     *
     * @param endDatum
     *  Das neue Enddatum der Veranstaltung.
     */
	public void setEndDatum(String endDatum) {
		Assert.notNull(endDatum, "endDatum darf nicht null sein!");
		this.endDatum = endDatum;
	}

    /**
     * Gibt die Endzeit der Veranstaltung zurück.
     *
     * @return
     *  Die Endzeit der Veranstaltung.
     */
	public String getEndZeit() {
		return endZeit;
	}

    /**
     * Setzt die Endzeit der Veranstaltung.
     *
     * @param endZeit
     *  Die neue Endzeit der Veranstaltung.
     */
	public void setEndZeit(String endZeit) {
		Assert.notNull(endZeit, "endZeit darf nicht null sein!");
		this.endZeit = endZeit;
	}

    /**
     * Setzt die Tage der Veranstaltung.
     *
     * @param tage
     *  Die neuen Tage der Veranstaltung.
     */
	public void setTage(String tage) {
		this.tage = tage;
	}

    /**
     * Gibt die Tage der Veranstaltung zurück.
     *
     * @return
     *  Die Tage der Veranstaltung.
     */
	public String getTage() {
		return tage;
	}
}
