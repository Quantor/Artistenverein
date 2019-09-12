package artistenverein.Zeitverwaltung;

import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Die Zeitverwaltungsklasse verwaltet ein Zeitrepository mit weiteren Funktionen.
 *
 * @author Anton Reinhard
 */
@Service
public class Zeitverwaltung {

	private ZeitRepository zeitRepo;
	private UserAccountManager accountManager;

	private static final int SCHALTJAHRAUSNAHME = 400;
	private static final int NICHTSCHALTJAHRAUSNAHME = 100;
	private static final int SCHALTJAHRFREQUENZ = 4;

	/**
	 * Erstellt ein neues Zeitverwaltungsobjekt.
	 *
	 * @param zeitRepo
	 * 	Das verwendete Repository mit den Daten für die Persistenz. Wird durch Autowired
	 * 	automatisch übergeben
	 *
	 * @param accountManager
	 * 	Der Account Manager wird auch von Autowired automatisch übergeben und ist für Authentifizierung
	 * 	wichtig.
	 */
	@Autowired
	public Zeitverwaltung(ZeitRepository zeitRepo, UserAccountManager accountManager) {
		Assert.notNull(zeitRepo, "zeitRepo darf nicht null sein!");
		Assert.notNull(accountManager, "accountManager darf nicht null sein!");
		this.zeitRepo = zeitRepo;
		this.accountManager = accountManager;
	}

	/**
	 * Löscht die Sperrzeit mit der angegebenen ID. Falls die ID nicht existiert passiert nichts.
	 *
	 * @param id
	 * 	Die ID der Sperrzeit, die gelöscht werden soll.
	 */
	public void loescheSperrzeit(long id) {
		for (Zeit z : zeitRepo.findAll()) {
			if (z.getId() == id) {
				zeitRepo.delete(id);
				return;
			}
		}
	}

	/**
	 * Gibt zurück, ob das angegebene Jahr im gregorianischen Kalender ein Schlatjahr ist oder nicht.
	 *
	 * @param jahr
	 * 	Das zu überprüfende Jahr
	 *
	 * @return
	 * 	Ein boolescher Wert. False, wenn jahr kein Schaltjahr ist, True, wenn es ein Schaltjahr ist.
	 */
	static public boolean istSchaltjahr(int jahr) {
		if (jahr % SCHALTJAHRAUSNAHME == 0) {
			return true;
		}
		if (jahr % NICHTSCHALTJAHRAUSNAHME == 0) {
			return false;
		}
		return jahr % SCHALTJAHRFREQUENZ == 0;
	}

	/**
	 * Trägt eine einmalige Sperrzeit in die Zeitverwaltung ein.
	 *
	 * @param zeitpunkt
	 * 	Der Startzeitpunkt der Sperrzeit.
	 *
	 * @param dauer
	 * 	Die Dauer der Sperrzeit.
	 *
	 * @param name
	 * 	Ein Name für die Sperrzeit.
	 *
	 * @param kommentar
	 * 	Ein Kommentar für die Sperrzeit.
	 *
	 * @param artist
	 * 	Der Artist, für den die Sperrzeit eingetragen wird.
	 *
	 */
	public void sperrzeitEintragenEinmalig(LocalDateTime zeitpunkt, Duration dauer, String name, String kommentar,
			UserAccount artist) {
		Assert.notNull(zeitpunkt, "zeitpunkt darf nicht null sein!");
		Assert.notNull(dauer, "dauer darf nicht null sein!");
		Assert.notNull(name, "name darf nicht null sein!");
		Assert.notNull(kommentar, "kommentar darf nicht null sein!");
		Assert.notNull(artist, "artist darf nicht null sein!");

		zeitRepo.save(new ArtistSperrZeit(zeitpunkt, dauer, Haeufigkeit.EINMAL, name, kommentar, artist));
	}

	/**
	 * Trägt eine jährliche Sperrzeit in die Zeitverwaltung ein. Die Sperrzeit wird jährlich am gleichen Datum
	 * wiederholt.
	 *
	 * @param zeitpunkt
	 * 	Der Startzeitpunkt der Sperrzeit.
	 *
	 * @param dauer
	 * 	Die Dauer der Sperrzeit.
	 *
	 * @param name
	 * 	Ein Name für die Sperrzeit.
	 *
	 * @param kommentar
	 * 	Ein Kommentar für die Sperrzeit.
	 *
	 * @param artist
	 * 	Der Artist, für den die Sperrzeit eingetragen wird.
	 *
	 */
	public void sperrzeitEintragenJaehrlich(LocalDateTime zeitpunkt, Duration dauer, String name, String kommentar,
			UserAccount artist) {
		Assert.notNull(zeitpunkt, "zeitpunkt darf nicht null sein!");
		Assert.notNull(dauer, "dauer darf nicht null sein!");
		Assert.notNull(name, "name darf nicht null sein!");
		Assert.notNull(kommentar, "kommentar darf nicht null sein!");
		Assert.notNull(artist, "artist darf nicht null sein!");

		zeitRepo.save(new ArtistSperrZeit(zeitpunkt, dauer, Haeufigkeit.JAEHRLICH, name, kommentar, artist));
	}

	/**
	 * Trägt eine monatliche Sperrzeit in die Zeitverwaltung ein. Die Sperrzeit wird monatlich am gleichen Tag des
	 * Monats wiederholt.
	 *
	 * @param zeitpunkt
	 * 	Der Startzeitpunkt der Sperrzeit.
	 *
	 * @param dauer
	 * 	Die Dauer der Sperrzeit.
	 *
	 * @param name
	 * 	Ein Name für die Sperrzeit.
	 *
	 * @param kommentar
	 * 	Ein Kommentar für die Sperrzeit.
	 *
	 * @param artist
	 * 	Der Artist, für den die Sperrzeit eingetragen wird.
	 *
	 */
	public void sperrzeitEintragenMonatlich(LocalDateTime zeitpunkt, Duration dauer, String name, String kommentar,
			UserAccount artist) {
		Assert.notNull(zeitpunkt, "zeitpunkt darf nicht null sein!");
		Assert.notNull(dauer, "dauer darf nicht null sein!");
		Assert.notNull(name, "name darf nicht null sein!");
		Assert.notNull(kommentar, "kommentar darf nicht null sein!");
		Assert.notNull(artist, "artist darf nicht null sein!");

		zeitRepo.save(new ArtistSperrZeit(zeitpunkt, dauer, Haeufigkeit.MONATLICH, name, kommentar, artist));
	}

	/**
	 * Trägt eine wöchentliche Sperrzeit in die Zeitverwaltung ein. Die Sperrzeit wird wöchentlich am gleichen
	 * Wochentag wiederholt.
	 *
	 * @param zeitpunkt
	 * 	Der Startzeitpunkt der Sperrzeit.
	 *
	 * @param dauer
	 * 	Die Dauer der Sperrzeit.
	 *
	 * @param name
	 * 	Ein Name für die Sperrzeit.
	 *
	 * @param kommentar
	 * 	Ein Kommentar für die Sperrzeit.
	 *
	 * @param artist
	 * 	Der Artist, für den die Sperrzeit eingetragen wird.
	 *
	 */
	public void sperrzeitEintragenWoechentlich(LocalDateTime zeitpunkt, Duration dauer, String name, String kommentar,
			UserAccount artist) {
		Assert.notNull(zeitpunkt, "zeitpunkt darf nicht null sein!");
		Assert.notNull(dauer, "dauer darf nicht null sein!");
		Assert.notNull(name, "name darf nicht null sein!");
		Assert.notNull(kommentar, "kommentar darf nicht null sein!");
		Assert.notNull(artist, "artist darf nicht null sein!");

		zeitRepo.save(new ArtistSperrZeit(zeitpunkt, dauer, Haeufigkeit.WOECHENTLICH, name, kommentar, artist));
	}

	/**
	 * Trägt eine tägliche Sperrzeit in die Zeitverwaltung ein. Die Sperrzeit wird täglich wiederholt.
	 *
	 * @param zeitpunkt
	 * 	Der Startzeitpunkt der Sperrzeit.
	 *
	 * @param dauer
	 * 	Die Dauer der Sperrzeit.
	 *
	 * @param name
	 * 	Ein Name für die Sperrzeit.
	 *
	 * @param kommentar
	 * 	Ein Kommentar für die Sperrzeit.
	 *
	 * @param artist
	 * 	Der Artist, für den die Sperrzeit eingetragen wird.
	 *
	 */
	public void sperrzeitEintragenTaeglich(LocalDateTime zeitpunkt, Duration dauer, String name, String kommentar,
			UserAccount artist) {
		Assert.notNull(zeitpunkt, "zeitpunkt darf nicht null sein!");
		Assert.notNull(dauer, "dauer darf nicht null sein!");
		Assert.notNull(name, "name darf nicht null sein!");
		Assert.notNull(kommentar, "kommentar darf nicht null sein!");
		Assert.notNull(artist, "artist darf nicht null sein!");

		zeitRepo.save(new ArtistSperrZeit(zeitpunkt, dauer, Haeufigkeit.TAEGLICH, name, kommentar, artist));
	}

	/**
	 * Trägt eine Sperrzeit in die Zeitverwaltung ein. Die Häufigkeit wird als Variable mit angegeben.
	 *
	 * @param zeitpunkt
	 * 	Der Startzeitpunkt der Sperrzeit.
	 *
	 * @param dauer
	 * 	Die Dauer der Sperrzeit.
	 *
	 * @param haeufigkeit
	 *  Die Häufigkeit der Sperrzeit als Enum.
	 *
	 * @param name
	 * 	Ein Name für die Sperrzeit.
	 *
	 * @param kommentar
	 * 	Ein Kommentar für die Sperrzeit.
	 *
	 * @param artist
	 * 	Der Artist, für den die Sperrzeit eingetragen wird.
	 *
	 */
	public void sperrzeitEintragen(LocalDateTime zeitpunkt, Duration dauer, Haeufigkeit haeufigkeit, String name,
			String kommentar, UserAccount artist) {
		Assert.notNull(zeitpunkt, "zeitpunkt darf nicht null sein!");
		Assert.notNull(dauer, "dauer darf nicht null sein!");
		Assert.notNull(haeufigkeit, "haeufigkeit darf nicht null sein!");
		Assert.notNull(name, "name darf nicht null sein!");
		Assert.notNull(kommentar, "kommentar darf nicht null sein!");
		Assert.notNull(artist, "artist darf nicht null sein!");
		zeitRepo.save(new ArtistSperrZeit(zeitpunkt, dauer, haeufigkeit, name, kommentar, artist));
	}

	/**
	 * Prüft, ob eine gegebene Zeitspanne sich mit einer eingetragenen Sperrzeit überschneidet.
	 *
	 * @param zeitpunkt
	 * 	Der Startzeitpunkt der zu überprüfenden Zeitspanne.
	 *
	 * @param dauer
	 * 	Die Dauer der zu überprüfenden Zeitspanne.
	 *
	 * @param artist
	 * 	Der Artist, dessen Sperrzeiten zur Überprüfung herangezogen werden sollen.
	 *
	 * @return
	 * 	Gibt 'true' als boolschen Wert zurück, falls die angegebene Zeitspanne in sich mit einer Sperrzeit des
	 * 	angegebenen Artisten überschneidet. Sonst wird 'false' zurückgegeben.
	 */
	public boolean ueberschneidetSperrzeit(LocalDateTime zeitpunkt, Duration dauer, UserAccount artist) {
		for (ArtistSperrZeit z : zeitRepo.findAll()) {
			// prüfen ob es überschneidet.
			if (z.getArtist().equals(artist) && z.amSelbenTag(zeitpunkt.toLocalDate())
					&& z.zurSelbenZeit(zeitpunkt.toLocalTime(), dauer)) {
				return true;
			}
			UserAccount boss = accountManager.findByUsername("boss").get();
			if (z.getArtist().equals(boss) && z.amSelbenTag(zeitpunkt.toLocalDate())
					&& z.zurSelbenZeit(zeitpunkt.toLocalTime(), dauer)) {
				return true;
			}
			
		}
		return false;
	}
}
