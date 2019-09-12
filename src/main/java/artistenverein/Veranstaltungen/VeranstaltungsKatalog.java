package artistenverein.Veranstaltungen;

import org.salespointframework.catalog.Catalog;

import artistenverein.Veranstaltungen.EntityVeranstaltung;
import artistenverein.Veranstaltungen.EntityVeranstaltung.VeranstaltungsType;

/**
 * Das Interface VeranstaltungsKatalog erweiter die Salespoint-Klasse
 * Catalog<EntityVeranstaltung> um die Funktion findByType(VeranstaltungsType
 * type) um die Veranstaltungen nach Typ (Workshop oder Show) zu durchsuchen
 * 
 * @author Luisa Leopold
 */
public interface VeranstaltungsKatalog extends Catalog<EntityVeranstaltung> {

	/**
	 * dient zum finden der Veranstaltungen mit einem bestimmren Typ
	 * 
	 * @param type
	 *            der Typ der zu suchenen Veranstaltungen
	 * 
	 * @return alle Veranstaltungen mit dem gesuchten Typ
	 */
	Iterable<EntityVeranstaltung> findByType(VeranstaltungsType type);
}