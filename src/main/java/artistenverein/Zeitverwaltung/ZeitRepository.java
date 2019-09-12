package artistenverein.Zeitverwaltung;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.salespointframework.useraccount.UserAccount;
import org.springframework.data.repository.CrudRepository;

/**
 * Ein allgemeines Repository, in dem Zeiten gespeichert werden können und einzelne Zeiten auf
 * Überschneidungen innerhalb des Repositorys überprüft werden können.
 *
 * @author Anton Reinhard
 */
public interface ZeitRepository extends CrudRepository<ArtistSperrZeit, Long> {

    /**
     * Überprüft, ob im Repository eine Sperrzeit des gegebenen Artisten mit der übergebenen Zeit überschneidet.
     *
     * @param zeitpunkt
     *  Startzeitpunkt der zu überprüfenden Zeit.
     *
     * @param dauer
     *  Dauer der zu überprüfenden Zeit.
     *
     * @param artist
     *  Artist, für den die Sperrzeiten überprüft werden sollen.
     *
     * @return
     *  Gibt 'true' zurück, wenn eine überschneidende Sperrzeit beim übergebenen Artisten gefunden wurde,
     *  sonst 'false'.
     */
	default boolean ueberschneidetSperrzeit(LocalDateTime zeitpunkt, Duration dauer, UserAccount artist) {
		if (zeitpunkt == null) {
			throw new IllegalArgumentException("zeitpunkt darf nicht null sein!");
		}
		if (dauer == null) {
			throw new IllegalArgumentException("dauer darf nicht null sein!");
		}
		if (artist == null) {
			throw new IllegalArgumentException("artist darf nicht null sein!");
		}
		
		for (ArtistSperrZeit z : findAll()) {
			// prüfen ob es überschneidet.
			if (z.amSelbenTag(zeitpunkt.toLocalDate()) && z.zurSelbenZeit(zeitpunkt.toLocalTime(), dauer)
					&& z.getArtist().equals(artist)) {
				return true;
			}

		}
		return false;
	}

    /**
     * Gibt ein Iterierbares Objekt zurück, was alle ArtistenSperrzeiten dieses Repositories für den gegebenen
     * Artisten enthält.
     *
     * @param artist
     *  Der Artist, für den die Sperrzeiten zurückgegeben werden.
     *
     * @return
     *  Ein Iterable von ArtistSperrZeiten des Artists.
     */
	default Iterable<ArtistSperrZeit> getSperrzeiten(UserAccount artist) {
		if (artist == null) {
			throw new IllegalArgumentException("artist darf nicht null sein!");
		}
		
		ArrayList<ArtistSperrZeit> result = new ArrayList<ArtistSperrZeit>();
		for (ArtistSperrZeit z : findAll()) {
			if (z.getArtist().equals(artist)) {
				result.add(z);
			}
		}
		return result;
	}
}