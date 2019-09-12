package artistenverein.Zeitverwaltung;

import org.salespointframework.useraccount.UserAccount;
import org.springframework.util.Assert;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Das Repository, in dem Sperrzeiten mit zugehörigen Artisten gespeichert
 * werden.
 *
 * @author Emanuel Kern
 */
@Entity
public class ArtistSperrZeit extends Zeit {

	@OneToOne
	private UserAccount artist;

	/**
	 * Diese Funktion ist nur hier, weil die Klasse einen Standardkonstruktor
	 * braucht. Sie sollte ansonsten nicht verwendet werden.
	 */
	@SuppressWarnings("unused")
	protected ArtistSperrZeit() {
		super();
	}

	/**
	 * Konstruktor für eine Artisten Sperrzeit. Wenn ein Name oder Kommentar
	 * erstellt werden soll, kann dafür der entsprechende andere Konstruktor
	 * verwendet werden.
	 *
	 * @param start
	 *            Startzeitpunkt der Sperrzeit, die erstellt wird.
	 *
	 * @param dauer
	 *            Dauer der Sperrzeit, die erstellt wird.
	 *
	 * @param haeufigkeit
	 *            Häufigkeit der Sperrzeit, die erstellt wird als Enum.
	 *
	 * @param artist
	 *            Der Artist, für den die Sperrzeit erstellt wird.
	 *
	 */
	ArtistSperrZeit(LocalDateTime start, Duration dauer, Haeufigkeit haeufigkeit, UserAccount artist) {
		super(start, dauer, haeufigkeit);
		Assert.notNull(artist, "artist darf nicht null sein!");
		this.artist = artist;
	}

	/**
	 * Konstruktor für eine Artisten Sperrzeit mit Namen und Kommentar. Wenn kein
	 * Name oder Kommentar erstellt werden soll, kann dafür der entsprechende andere
	 * Konstruktor verwendet werden.
	 *
	 * @param start
	 *            Startzeitpunkt der Sperrzeit, die erstellt wird.
	 *
	 * @param dauer
	 *            Dauer der Sperrzeit, die erstellt wird.
	 *
	 * @param haeufigkeit
	 *            Häufigkeit der Sperrzeit, die erstellt wird als Enum.
	 *
	 * @param name
	 *            Name der Sperrzeit, die erstellt wird.
	 *
	 * @param kommentar
	 *            Kommentar zu der Sperrzeit, die erstellt wird.
	 *
	 * @param artist
	 *            Der Artist, für den die Sperrzeit erstellt wird.
	 *
	 */
	public ArtistSperrZeit(LocalDateTime start, Duration dauer, Haeufigkeit haeufigkeit, String name, String kommentar,
			UserAccount artist) {
		super(start, dauer, haeufigkeit, name, kommentar);
		Assert.notNull(artist, "artist darf nicht null sein!");
		this.artist = artist;
	}

	/**
	 * Der Artist dem die Sperrzeit gehört, kann gesetzt werden.
	 *
	 * @param artist
	 *            Der Artist, dem die Sperrzeit zukünfig zugeordnet werden soll.
	 *
	 */
	public void setArtist(UserAccount artist) {
		Assert.notNull(artist, "artist darf nicht null sein!");
		this.artist = artist;
	}

	/**
	 * Gibt den Artist, dem diese Sperrzeit gehört zurück.
	 *
	 * @return Der Artist, dem diese Sperrzeit gehört.
	 *
	 */
	public UserAccount getArtist() {
		return artist;
	}

}
