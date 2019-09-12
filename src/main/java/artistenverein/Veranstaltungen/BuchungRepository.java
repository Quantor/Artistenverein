package artistenverein.Veranstaltungen;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.salespointframework.useraccount.UserAccount;
import org.springframework.data.repository.CrudRepository;

import artistenverein.Veranstaltungen.Buchung;
import artistenverein.Zeitverwaltung.Haeufigkeit;
import artistenverein.Zeitverwaltung.Zeit;

/**
 * Dient zum Speichern der Buchungen des Vereins
 *
 * @author Luisa
 */
public interface BuchungRepository extends CrudRepository<Buchung, Long> {

	/**
	 * Gibt zurück, ob der übergebene Kunde berechtigt ist einen Rabatt im Shop zu
	 * bekommen
	 *
	 * @param kunde
	 *            der zu prüfende Kunde
	 * 
	 * @return Rabattberechtigung: true = bekommt Rabatt
	 */
	public default boolean bekommtRabatt(UserAccount kunde) {
		for (Buchung buchung : findAll()) {
			if (buchung.getVeranstaltung().getType().equals(EntityVeranstaltung.VeranstaltungsType.WORKSHOP)
					&& buchung.getKunde().equals(kunde) && buchung.getDatum().isAfter(LocalDateTime.now())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gibt alle Zeiten zurück, zu denen die Halle ausgebucht ist
	 * 
	 * @return alle gebuchten Zeiten
	 */
	public default Iterable<Zeit> getHallenZeiten() {
		ArrayList<Zeit> zeiten = new ArrayList<Zeit>();
		for (Buchung b : findAll()) {
			if (b.getOrt().equals("Halle")) {
				zeiten.add(new Zeit(b.getDatum(), Duration.ofMinutes(b.getVeranstaltung().getDauer()),
						Haeufigkeit.EINMAL));
			}
		}
		return zeiten;
	}

	/**
	 * Gibt alle Buchungen der Halle zurück
	 * 
	 * @return alle Buchungen mit Ort Halle
	 */
	public default List<Buchung> getHallenBuchungen() {
		List<Buchung> hallenBuchungen = new ArrayList<Buchung>();
		for (Buchung b : findAll()) {
			if (b.getOrt().equals("Halle")) {
				hallenBuchungen.add(b);
			}
		}
		return hallenBuchungen;
	}

	/**
	 * löscht alle zusammenhängenden Buchungen
	 * 
	 * @param buchung
	 *            eine beliebige der zu löschenden Buchungen
	 */
	public default void deleteAllUUID(Buchung buchung) {
		UUID buchungId = buchung.getBuchungId();
		for (Buchung b : findAll()) {
			if (b.getBuchungId().equals(buchungId)) {
				delete(b);
			}
		}
	}
}