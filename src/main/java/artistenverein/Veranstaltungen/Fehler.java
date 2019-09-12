package artistenverein.Veranstaltungen;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.salespointframework.useraccount.UserAccount;
import org.springframework.util.Assert;

import artistenverein.Zeitverwaltung.Zeitverwaltung;

/**
 * Eine Klasse, die Fehler in Formularen handlet.
 *
 * @author Luisa
 */
public class Fehler {

	private List<Integer> fehlerliste;

    /**
     * Verschiedene Konstanten, die gebraucht werden um verschiedene Fehler zu klassifizieren.
     */
	public static class Constants {
		public static final int SPERRZEIT = 0; // Sperrzeiten von Artisten oder Vorstand gerade aktiv--> angegebenen
												// Zeitraum überprüfen
		public static final int HALLE = 1; // Halle zu der Zeit nicht verfügbar
		public static final int ZEITRAUM = 2; // eingegebener Zeitraum liegt nicht im vorgegebenen
		public static final int VERGANGENHEIT = 3; // angegebenes Datum liegt in der Vergangenheit
		public static final int VERBUCHT = 4; // artisten sind zu gegebenem Zeitraum nicht mehr verfügbar
		public static final int DAUER = 5; // eingegebene Dauer stimmt nicht (z.b. negative Zahl)
		public static final int DATUM = 6; // end datum liegt vor Startdatum
		public static final int BEWERTUNG = 7; // bewertung liegt nicht im Bereich 0-5
		public static final int ZUSATZKOSTEN = 8; // Zusatzkosten ungültig (Buchstabe, Minusbereich)
	}

	private static final int MAX_BEWERTUNG = 5;
	private static final int MIN_BEWERTUNG = 0;

    /**
     * Der Standardkonstruktor, fehlerliste wird leer initialisiert.
     */
	public Fehler() {
		this.fehlerliste = new ArrayList<>();
	}

    /**
     * Diese Funktion überprüft, ob eine zu buchende Veranstaltung trotz Sperrzeiten des Artisten buchbar ist.
     *
     * @param veranstaltung
     *  Die Veranstaltung, die gebucht werden soll.
     *
     * @param datum
     *  Die Zeit und Datum, zu der die Veranstaltung beginnen soll.
     *
     * @param dauer
     *  Die Dauer der Veranstaltung.
     *
     * @param zeitverwaltung
     *  Die Zeitverwaltung, in der die Sperrzeiten gespeichert sind.
     *
     * @return
     *  Gibt 'true' zurück, wenn eine Sperrzeit überschnitten wird und die Veranstaltung nicht buchbar ist.
     *  Ansonsten wird 'false' zurückgegeben.
     */
	public boolean pruefeSperrzeit(EntityVeranstaltung veranstaltung,
								   LocalDateTime datum, Duration dauer,
								   Zeitverwaltung zeitverwaltung) {

		Assert.notNull(veranstaltung, "veranstaltung darf nicht null sein!");
		Assert.notNull(datum, "start-Datum darf nicht null sein!");
		Assert.notNull(dauer, "dauer darf nicht null sein!");
		//Assert.notNull(userRepository, "userRepository darf nicht null sein!");
		Assert.notNull(zeitverwaltung, "zeitverwaltung darf nicht null sein!");

		for (Iterator<UserAccount> it = veranstaltung.getArtisten().iterator(); it.hasNext();) {
			UserAccount user = it.next();
			if (zeitverwaltung.ueberschneidetSperrzeit(datum, dauer, user)) {
				fehlerliste.add(Constants.SPERRZEIT);
				return true;
			}
		}
		return false;
	}

    /**
     * Diese Funktion überprüft, ob die Halle zu einem bestimmten Zeitpunkt noch verfügbar und buchbar ist.
     *
     * @param start
     *  Startzeit der Zeitspanne, die überprüft wird.
     *
     * @param dauer
     *  Dauer der Zeitspanne, die überprüft wird.
     *
     * @param buchungRepository
     *  Das Repository, in dem alle Buchungen gespeichert sind.
     *
     * @return
     *  Gibt 'true' zurück, falls die Halle schon besetzt ist, andernfalls 'falls'.
     */
	public boolean pruefeHalle(LocalDateTime start, Duration dauer, BuchungRepository buchungRepository) {
		Assert.notNull(buchungRepository, "buchungRepository darf nicht null sein!");
		Assert.notNull(start, "start-Datum darf nicht null sein!");
		Assert.notNull(dauer, "dauer darf nicht null sein!");
		for (Buchung b : buchungRepository.findAll()) {
			if (b.getOrt().equals("Halle") && datumUeberschneidet(start, dauer, b.getDatum(), b.getEndDatum())) {
				fehlerliste.add(Constants.HALLE);
				return true;
			}
		}

		return false;
	}

    /**
     * Diese Funktion überprüft, ob eine Veranstaltung innerhalb des für die Veranstaltung erlaubten Zeitraumes gebucht
     * wird.
     *
     * @param veranstaltung
     *  Die Veranstaltung, für die ein Zeitpunkt überprüft werden soll.
     *
     * @param datum
     *  Der Zeitpunkt, der überprüft werden soll.
     *
     * @return
     *  Es wird 'true' zurückgegeben, wenn die Veranstaltung innerhalb des vorgegebenen Zeitraums stattfindet,
     *  andernfalls 'false'.
     */
	public boolean pruefeZeitraum(EntityVeranstaltung veranstaltung, LocalDateTime datum) {
		Assert.notNull(veranstaltung, "veranstaltung darf nicht null sein!");
		Assert.notNull(datum, "datum darf nicht null sein!");
		if ((veranstaltung.getStartDatum().compareTo(datum) > 0)
				|| (veranstaltung.getEndDatum().compareTo(datum.plusMinutes(veranstaltung.getDauer())) < 0)) {
			fehlerliste.add(Constants.ZEITRAUM);
			return true;
		}
		return false;
	}

    /**
     * Diese Funktion überprüft, ob ein Datum bereits in der Vergangenheit liegt.
     *
     * @param date
     *  Das zu überprüfende Datum.
     *
     * @return
     *  Gibt 'true' zurück, falls das Datum in der Vergangenheit liegt, sonst 'false'.
     */
	public boolean pruefeVergangenheit(LocalDateTime date) {
		Assert.notNull(date, "datum darf nicht null sein!");
		LocalDateTime today = LocalDateTime.now();
		if (date.compareTo(today) <= 0) {
			fehlerliste.add(Constants.VERGANGENHEIT);
			return true;
		}
		return false;
	}

    /**
     * Diese Funktion überprüft, ob eine Veranstaltung bei einem Artisten bereits ausgebucht ist.
     *
     * @param veranstaltung
     *  Die Veranstaltung, um die es geht.
     *
     * @param buchungRepository
     *  Das Repository, in dem alle Buchungen gespeichert sind.
     *
     * @param start
     *  Der Startzeitpunkt der Veranstaltung, die gebucht werden soll.
     *
     * @param dauer
     *  Die Dauer der Veranstaltung, die gebucht werden soll.
     *
     * @return
     *  Gibt 'true' zurück, falls der Artist verbucht ist, sonst 'false'.
     */
	public boolean pruefeArtistenVerbucht(EntityVeranstaltung veranstaltung, BuchungRepository buchungRepository,
			LocalDateTime start, Duration dauer) {
		Assert.notNull(veranstaltung, "veranstaltung darf nicht null sein!");
		Assert.notNull(buchungRepository, "buchungRepository darf nicht null sein!");
		Assert.notNull(start, "start-Datum darf nicht null sein!");
		Assert.notNull(dauer, "dauer darf nicht null sein!");
		for (Buchung buchung : buchungRepository.findAll()) {
			for (UserAccount artist : veranstaltung.getArtisten()) {
				if (buchung.getVeranstaltung().getArtisten().contains(artist)
						&& datumUeberschneidet(start, dauer, buchung.getDatum(), buchung.getEndDatum())) {
					fehlerliste.add(Constants.VERBUCHT);
					return true;
				}
			}
		}
		return false;
	}

    /**
     * Diese Funktion überprüft, ob die Dauer nicht falsch ist.
     *
     * @param dauer
     *  Die Dauer, die überprüft werden soll.
     *
     * @return
     *  Gibt 'true' zurück, wenn die Dauer falsch ist (zum Beispiel negativ), sonst 'false'.
     */
	public boolean pruefeDauer(Duration dauer) {

		Assert.notNull(dauer, "Dauer darf nicht null sein!");

		if (!dauer.isNegative()) {
			return false;
		} else {
			fehlerliste.add(Constants.DAUER);
			return true;
		}
	}

    /**
     * Diese Funktion überprüft ein Start- und Enddatum auf Sinnhaftigkeit.
     *
     * @param start
     *  Das Startdatum.
     *
     * @param end
     *  Das Enddatum.
     *
     * @return
     *  Gibt 'true' zurück, wenn das Ende vor dem Anfang liegt, sonst 'false'.
     */
	public boolean pruefeDatum(LocalDateTime start, LocalDateTime end) {
		Assert.notNull(start, "Start-Datum darf nicht null sein!");
		Assert.notNull(end, "End-Datum darf nicht null sein!");
		if (start.compareTo(end) >= 0) {
			fehlerliste.add(Constants.DATUM);
			return true;
		} else {
			return false;
		}
	}

    /**
     * Diese Funktion überprüft, ob eine Bewertung im Rahmen liegt.
     *
     * @param bewertung
     *  Die zu überprüfende Bewertung.
     *
     * @return
     *  Gibt 'true' zurück, wenn die Bewertung außerhalb des Bereichs 0 bis 5 liegt, sonst 'false'.
     */
	public boolean pruefeBewertung(double bewertung) {
		if ((bewertung >= MIN_BEWERTUNG) && (bewertung <= MAX_BEWERTUNG)) {
			return false;
		} else {
			fehlerliste.add(Constants.BEWERTUNG);
			return true;
		}
	}

    /**
     * Diese Funktion überprüft, ob ein String eine sinnvolle Geldmenge enthält.
     *
     * @param zusatzkosten
     *  Der String, der überprüft werden soll.
     *
     * @return
     *  Gibt 'true' zurück, wenn der Wert keine Dezimalzahl darstellt oder kleiner null ist, sonst 'false'.
     */
	public boolean pruefeZusatzkosten(String zusatzkosten) {
		Assert.notNull(zusatzkosten, "Zusatzkosten darf nicht null sein!");
		try {
			double z = Double.parseDouble(zusatzkosten);
			if (z < 0) {
				fehlerliste.add(Constants.ZUSATZKOSTEN);
				return true;
			}
			return false;
		} catch (java.lang.NumberFormatException ex) {
			fehlerliste.add(Constants.ZUSATZKOSTEN);
			return true;
		}
	}

    /**
     * Diese Funktion überprüft, ob eine Zeitspanne eine andere überschneidet.
     *
     * @param start
     *  Startzeitpunkt der ersten Zeitspanne.
     *
     * @param dauer
     *  Dauer der ersten Zeitspanne.
     *
     * @param pruefStart
     *  Startzeitpunkt der zweiten Zeitspanne.
     *
     * @param pruefEnde
     *  Endzeitpunkt der zweiten Zeitspanne.
     *
     * @return
     *  Gibt 'true' zurück, wenn die Zeitspannen sich überschneiden, sonst 'false'.
     */
	private boolean datumUeberschneidet(LocalDateTime start, Duration dauer, LocalDateTime pruefStart,
			LocalDateTime pruefEnde) {
		LocalDateTime ende = start.plus(dauer);
		boolean returnValue = false;
		if ((start.isAfter(pruefStart) && start.isBefore(pruefEnde)) || start.isEqual(pruefStart)) {
			returnValue = true;
		} else if ((ende.isBefore(pruefEnde) && ende.isAfter(pruefStart)) || ende.isEqual(pruefEnde)) {
			returnValue = true;
		} else if (start.isBefore(pruefStart) && ende.isAfter(pruefEnde)) {
			returnValue = true;
		} else if (start.isEqual(pruefEnde) || (ende.isEqual(pruefStart))) {
			returnValue = true;
		}
		return returnValue;
	}

    /**
     * Gibt die gesamte Fehlerliste zurück.
     *
     * @return
     *  Die Liste aller Fehler, die bei überprüfungen aufgetreten sind.
     */
	public List<Integer> getFehlerliste() {
		return fehlerliste;
	}

}
