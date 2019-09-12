package artistenverein.Lagerverwaltung;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.salespointframework.quantity.Quantity;
import org.springframework.util.Assert;

/**
 * Die Klasse MindestMenge ermöglicht die Speicherung eines Quantity-Objektes in
 * einem CrudRepository
 * 
 * @author Emanuel Kern
 */
@Entity
public class InventarKonfiguration {

	private static final int PROZENT_MAX = 99;

	/**
	 * Die Enumeration KonfigurationsTyp ermöglicht die Festlegung der
	 * Inventarkonfiguration auf die Typen Rabatt und Mindestmenge
	 * 
	 * @author Emanuel Kern
	 */
	public static enum KonfigurationsTyp {
		RABATT, MINDEST_MENGE;
	}

	private @Id @GeneratedValue long id;
	private int wert;
	private KonfigurationsTyp typ;

	/**
	 * Leerer Konstruktor für die Klasse InventarKonfiguration. Wird von
	 * Spring/Hibernate benötigt, sollte aber nicht benutzt werden
	 *
	 */
	@SuppressWarnings("unused")
	public InventarKonfiguration() {
		// required by Spring
	}

	/**
	 * Erstellt ein neues Objekt vom Typ MindestMenge einer Quantity in der
	 * gegebenen Höhe
	 *
	 * @param wert
	 *            der zu speicherende Wert als int
	 * @param typ
	 *            der Typ des zu speichernden Wertes als Enum KonfigurationsTyp
	 */
	public InventarKonfiguration(int wert, KonfigurationsTyp typ) {
		Assert.notNull(typ, "typ darf nicht null sein!");
		if (wert < 1 && typ == KonfigurationsTyp.MINDEST_MENGE) {
			throw new IllegalArgumentException(
					"wert darf nicht kleiner 1 sein, wenn der Typ Mindestmenge ausgewählt ist!");
		}
		if (wert < 0 && typ == KonfigurationsTyp.RABATT) {
			throw new IllegalArgumentException("wert darf nicht kleiner 0 sein, wenn der Typ Rabatt ausgewählt ist!");
		}
		if (wert > PROZENT_MAX && typ == KonfigurationsTyp.RABATT) {
			throw new IllegalArgumentException(
					"wert darf nicht größer als 99 sein, wenn der Typ Rabatt ausgewählt ist!");
		}
		this.wert = wert;
		this.typ = typ;
	}

	/**
	 * Gibt die gespeicherte MindestMenge als Quantity zurück
	 *
	 * @return die MindestMenge als Quantity
	 */
	public Quantity getQuantity() {
		if (typ != KonfigurationsTyp.MINDEST_MENGE) {
			throw new UnsupportedOperationException("getQuantity() ist nur für Konfiguration MindestMenge erlaubt!");
		}
		return Quantity.of(wert);
	}

	/**
	 * Gibt den gespeicherten Rabatt als int zurück
	 *
	 * @return der Rabatt als int
	 */
	public int getRabatt() {
		if (typ != KonfigurationsTyp.RABATT) {
			throw new UnsupportedOperationException("getRabatt() ist nur für Konfiguration Rabatt erlaubt!");
		}
		return wert;
	}

	/**
	 * Setzt die gespeicherte Quantity auf den übergebenen Wert
	 * 
	 * @param mindestMenge
	 *            die zu speicherende MindestMenge als int
	 */
	public void setWert(int wert) {
		if (wert < 1 && typ == KonfigurationsTyp.MINDEST_MENGE) {
			throw new IllegalArgumentException(
					"wert darf nicht kleiner 1 sein, wenn der Typ Mindestmenge ausgewählt ist!");
		}
		if (wert < 0 && typ == KonfigurationsTyp.RABATT) {
			throw new IllegalArgumentException("wert darf nicht kleiner 0 sein, wenn der Typ Rabatt ausgewählt ist!");
		}
		if (wert > PROZENT_MAX && typ == KonfigurationsTyp.RABATT) {
			throw new IllegalArgumentException(
					"wert darf nicht größer als 99 sein, wenn der Typ Rabatt ausgewählt ist!");
		}
		this.wert = wert;
	}

	/**
	 * Prüft, ob der Typ des Objektes Rabatt ist
	 *
	 * @return true, wenn typ gleich Rabatt ist
	 */
	public boolean hatTypRabatt() {
		if (typ == KonfigurationsTyp.RABATT) {
			return true;
		}
		return false;
	}

	/**
	 * Prüft, ob der Typ des Objektes MindestMenge ist
	 *
	 * @return true, wenn typ gleich MindestMenge ist
	 */
	public boolean hatTypMindestMenge() {
		if (typ == KonfigurationsTyp.MINDEST_MENGE) {
			return true;
		}
		return false;
	}

}
