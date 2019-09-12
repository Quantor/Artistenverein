package artistenverein.Veranstaltungen;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Dient zur Speicherung der Zusatzkosten bei Buchung außerhalb der Vereinshalle
 *
 */
@Entity
public class Zusatzkosten {

	private double zusatzKostenWert;
	private @Id @GeneratedValue long id;

	/**
	 * Konstruktor
	 * 
	 * @param value
	 *            die Zusatzkosten als double
	 */
	public Zusatzkosten(double value) {
		zusatzKostenWert = value;
	}

	public Zusatzkosten() {
		// Required By Spring
	}

	public double getZusatzKostenWert() {
		return zusatzKostenWert;
	}

	/**
	 * setzt die Zusatzkosten auf den übergebenen Wert
	 * 
	 * @param zusatzKostenWert
	 *            die neuen Zusatzkosten als double
	 */
	public void setZusatzKostenWert(double zusatzKostenWert) {
		this.zusatzKostenWert = zusatzKostenWert;
	}

	@Override
	public String toString() {
		return String.valueOf(zusatzKostenWert);
	}

}
