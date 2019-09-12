package artistenverein.Veranstaltungen;

import static org.salespointframework.core.Currencies.EURO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.javamoney.moneta.Money;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import artistenverein.Lagerverwaltung.Artikel;
import artistenverein.Veranstaltungen.EntityVeranstaltung.VeranstaltungsType;

/**
 * Die Klasse VeranstaltungsManager dient zum Erstellen und Bearbeiten von 
 * Veranstaltungen im Veranstaltungskatalog
 * 
 * @author Luisa Leopold
 */
@Service
public class VeranstaltungsManager {

	private final VeranstaltungsKatalog veranstaltungsKatalog;
	private final static int EMPFEHLUNGEN_ANZAHL = 3;

	/**
	 * erstellt einen neuen VeranstaltungsManager mit den folgenen Parametern
	 * 
	 * @param veranstaltungsKatalog
	 * 				der VeranstaltungsKatalog, normalerweise von Spring automatisch
	 *           	verknüpft
	 */
	public VeranstaltungsManager(VeranstaltungsKatalog veranstaltungsKatalog) {
		Assert.notNull(veranstaltungsKatalog, "veranstaltungsKatalog darf nicht null sein!");
		this.veranstaltungsKatalog = veranstaltungsKatalog;
	}

	/**
	 * Erstellt eine neue Veranstaltung, speichert sie im veranstaltungsKatalog und gibt sie anschließend 
	 * zurück
	 * 
	 * @param neueVeranstaltung
	 * 				ein (valides) FormNeueVeranstaltung, das die Eigenschaften der
	 *            Veranstaltung enthält
	 * @param vt
	 * 				der Veranstaltung- Typ, den die Veranstaltung erhalten soll (Workshop oder Show)
	 * 				als VeranstaltungsType Objekt
	 * @param artisten
	 * 				ein Set<UserAccount> von Artisten, die der Veranstaltung zugeordnet werden sollen 
	 * 				(enthällt z.b. alle Artisten einer Artistengruppe oder einen einzelnen Artisten)
	 * @return	die erstellte Veransaltung
	 */
	public EntityVeranstaltung erstelleNeueVeranstaltung(FormNeueVeranstaltung neueVeranstaltung, VeranstaltungsType vt,
			Set<UserAccount> artisten, String gruppe) {

		Assert.notNull(neueVeranstaltung, "neueVeranstaltung darf nicht null sein!");
		// Assert.notNull(userAccount, "userAccount darf nicht null sein!");
		Assert.notNull(vt, "vt darf nicht null sein!");
		// Assert.notNull(artisten, "artisten darf nicht null sein!");

		LocalDateTime start = getDatum(neueVeranstaltung.getStartDatum(), neueVeranstaltung.getStartZeit());
		LocalDateTime end = getDatum(neueVeranstaltung.getEndDatum(), neueVeranstaltung.getEndZeit());

		int preis = Integer.parseInt(neueVeranstaltung.getPreis());
		int dauer = Integer.parseInt(neueVeranstaltung.getDauer());
		int tage;
		if (vt == VeranstaltungsType.SHOW) {
			tage = 1;
		} else {
			tage = Integer.parseInt(neueVeranstaltung.getTage());
		}
		EntityVeranstaltung erstellteVeranstaltung = new EntityVeranstaltung(neueVeranstaltung.getName(),
				Money.of(preis, EURO), neueVeranstaltung.getBeschreibung(), dauer, vt, start, end, artisten, tage,
				gruppe);
		veranstaltungsKatalog.save(erstellteVeranstaltung);
		return (erstellteVeranstaltung);
	}

	/**
	 * Ordnet einer Veranstaltung Artikel des Shops zu
	 * 
	 * @param veranstaltung
	 * 				die Veranstaltung, der Artikel zugeordnet werden sollen als 
	 * 				EntityVeranstaltung Objekt
	 * @param artikel
	 * 				die Artikel die zugeordent werden sollen als Artikel- Array (Artikel[])
	 */
	public void addArtikel(EntityVeranstaltung veranstaltung, Artikel[] artikel) {
		Assert.notNull(veranstaltung, "veranstaltung darf nicht null sein!");
		Assert.notNull(artikel, "artikel darf nicht null sein!");

		for (int i = 0; i < artikel.length; i++) {
			veranstaltung.addArtikel(artikel[i]);
			veranstaltungsKatalog.save(veranstaltung);
		}
	}

	/**
	 * Verwandelt ein Datum und eine Zeit, die in String gegeben sind in ein LoclDateTime Objekt
	 * 
	 * @param datum
	 * 				Datum als String
	 * @param zeit
	 * 				Zeit als String
	 * @return		LocalDateTime Objekt der beiden Strings
	 */
	public LocalDateTime getDatum(String datum, String zeit) {
		Assert.notNull(datum, "datum darf nicht null sein!");
		Assert.notNull(zeit, "zeit darf nicht null sein!");

		String[] parts = datum.split("-");
		String[] timeParts = zeit.split(":");
		LocalDateTime start = LocalDateTime.of(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]),
				Integer.parseInt(parts[2]), Integer.parseInt(timeParts[0]), Integer.parseInt(timeParts[1]));
		return start;
	}
	
	/**
	 * Gibt eine Liste der besten Veranstaltungen (die mit den besten Bewertugen) zurück
	 * 
	 * @return	Liste der best- bewertesten Veranstaltungen als List<EntityVeranstaltung>
	 */
	public List<EntityVeranstaltung> getBesteBewertungen() {
		List<EntityVeranstaltung> empfehlungen = new ArrayList<>();
		for (Iterator<EntityVeranstaltung> it = veranstaltungsKatalog.findAll().iterator(); it.hasNext();) {
			EntityVeranstaltung veranstaltung = it.next();
			empfehlungen.add(veranstaltung);
		}
		Collections.sort(empfehlungen, EntityVeranstaltung.compareMittelwert());

		if (empfehlungen.size() > EMPFEHLUNGEN_ANZAHL) {
			return empfehlungen.subList(0, EMPFEHLUNGEN_ANZAHL);
		} else {
			return empfehlungen;
		}
	}

}