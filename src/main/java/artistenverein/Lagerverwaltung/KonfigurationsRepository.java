package artistenverein.Lagerverwaltung;

import org.springframework.data.repository.CrudRepository;

/**
 * Das Interface MindestMengeRepository dient zum Speichern der MindestMenge an
 * Artikeln, die im Shop vorrätig sein sollen, da die Salespoint-Klasse Quantity
 * final ist
 * 
 * @author Emanuel Kern
 */
public interface KonfigurationsRepository extends CrudRepository<InventarKonfiguration, Long> {

	/**
	 * Prüft ob ein Rabattwert vorhanden ist
	 *
	 * @return true, wenn das Repo einen Rabattwert enthält, sonst false
	 */
	public default boolean enthaeltRabatt() {
		for (InventarKonfiguration m : findAll()) {
			if (m.hatTypRabatt()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Prüft ob ein Mindestmengewert vorhanden ist
	 *
	 * @return true, wenn das Repo einen Mindestmengewert enthält, sonst false
	 */
	public default boolean enthaeltMindestMenge() {
		for (InventarKonfiguration m : findAll()) {
			if (m.hatTypMindestMenge()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gibt die InventarKonfiguration der MindestMenge zurück
	 *
	 * @return die MindestMenge als Inventarkonfiguration
	 */
	public default InventarKonfiguration getMindestMenge() {
		InventarKonfiguration mindestMenge = null;
		for (InventarKonfiguration m : findAll()) {
			if (m.hatTypMindestMenge()) {
				mindestMenge = m;
				break;
			}
		}
		return mindestMenge;
	}

	/**
	 * Gibt die InventarKonfiguration des Rabatt zurück
	 *
	 * @return der Rabatt als Inventarkonfiguration
	 */
	public default InventarKonfiguration getRabatt() {
		InventarKonfiguration rabatt = null;
		for (InventarKonfiguration m : findAll()) {
			if (m.hatTypRabatt()) {
				rabatt = m;
				break;
			}
		}
		return rabatt;
	}
}