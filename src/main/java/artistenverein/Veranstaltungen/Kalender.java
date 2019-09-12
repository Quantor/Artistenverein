package artistenverein.Veranstaltungen;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.salespointframework.useraccount.UserAccount;

import artistenverein.Zeitverwaltung.Zeitverwaltung;

/**
 * Die Klasse Kalender enthält alle Funktionen,
 * die für die Berechnung des Kalenders benötigt werden
 * @author Oliver
 *
 */
public class Kalender {
	
	private LocalDateTime datum;
	private static final double WOCHENTAGE = 7.0;

	/**
	 * Erstellt den Kalender und setzt das Datum des Kalenders auf das aktuelle
	 */
	public Kalender() {
		datum = LocalDateTime.now();
	}
	
	/**
	 * Gibt die Buchungen zu dem Kunden im Monat zurück
	 * @param buchungRepository
	 * 				Das Repository, welches alle Buchungen enthält
	 * @param userAccount
	 * 				Der UserAccount des Kunden
	 * @return	eine Liste von Buchungen des Kunden zum Monat des Datums des Kalenders
	 */
	public List<Buchung> getBuchungenZuKundeImMonat(BuchungRepository buchungRepository, UserAccount userAccount) {
		Iterable<Buchung> buchungen = buchungRepository.findAll();
		List<Buchung> buchungenZuKundeImMonat = new ArrayList<Buchung>();
		for (Iterator<Buchung> it = buchungen.iterator(); it.hasNext();) {
			Buchung buchung = it.next();
			if (buchung.getKunde().getId().equals(userAccount.getId())
					&& buchung.getDatum().getMonth().equals(datum.getMonth())) {
				buchungenZuKundeImMonat.add(buchung);
			}
		}
		return buchungenZuKundeImMonat;
	}
	
	/**
	 * Gibt die Buchungen zu dem Artist im Monat zurück
	 * @param buchungRepository
	 * 				Das Repository, welches alle Buchungen enthält
	 * @param userAccount
	 * 				Der UserAccount zum Artisten
	 * @return	eine Liste von Buchungen des Artisten zum Monat des Datums des Kalenders
	 */
	public List<Buchung> getBuchungenZuArtistImMonat(BuchungRepository buchungRepository, UserAccount userAccount) {
		Iterable<Buchung> buchungen = buchungRepository.findAll();
		List<Buchung> buchungenZuArtistImMonat = new ArrayList<Buchung>();
		for (Iterator<Buchung> it = buchungen.iterator(); it.hasNext();) {
			Buchung buchung = it.next();
			if (buchung.getVeranstaltung().getArtisten().contains(userAccount)
					&& buchung.getDatum().getMonth().equals(datum.getMonth())) {

				buchungenZuArtistImMonat.add(buchung);
			}
		}
		return buchungenZuArtistImMonat;
	}
	
	/**
	 * 	füllt Kalender durch eine Tabelle mit Zahlen und Veranstaltungen, die in einem zweidim. Array gespeichert werden,
	 *	wobei die Größe durch die nötige Zeilen- und Spaltenanzahl bestimmt wird
	 * @param buchungenZuKundeImMonat
	 * 					eine List von Buchungen des Kunden im Monat des Kalenders
	 * @return	ein zweidim. Array der die Einträge als Strings des Kalendermonats enthält
	 */
	public String[][] getEintraegeFuerMonat(List<Buchung> buchungenZuKundeImMonat) {
		
		List<Buchung> buchungenZuDatum = new ArrayList<Buchung>();
		boolean leapYear = Zeitverwaltung.istSchaltjahr(datum.getYear());
		int tageImMonat = datum.getMonth().length(leapYear);
		//um festzustellen, was der erste Wochentag in diesem Monat ist
		datum = LocalDateTime.of(datum.getYear(), datum.getMonthValue(), 1, 0, 0);
		//-1, da der Array "eintraege" so viele Einträge, wie startTag groß ist, mit leeren Strings füllt(bei Montag also 0)
		int startTag = datum.getDayOfWeek().getValue() - 1;
		int maxZeilen = (int) Math.ceil((tageImMonat + startTag) / WOCHENTAGE); 
		String[][] eintraege = new String[maxZeilen][(int) WOCHENTAGE];

		int aktuelleZeile = 0, aktuelleSpalte = 0;
		while (aktuelleSpalte < startTag) {
			eintraege[aktuelleZeile][aktuelleSpalte] = "";
			aktuelleSpalte++;
		}
		//"(n*wochenTage)+m+1-startTag" steht für den aktuelle Tag im Monat(1-31)
		while (((aktuelleZeile * WOCHENTAGE) + aktuelleSpalte + 1 - startTag) <= tageImMonat)  {
			while (aktuelleSpalte < WOCHENTAGE && ((aktuelleZeile * WOCHENTAGE)
					+ aktuelleSpalte + 1 - startTag) <= tageImMonat) {
				Buchung buchungZuDatum = findeBuchungMitDatum(buchungenZuKundeImMonat,
						((aktuelleZeile * WOCHENTAGE) + aktuelleSpalte + 1 - startTag));
				if (buchungZuDatum != null) {
					buchungenZuDatum.add(buchungZuDatum);
				} else {
					fuelleZelleMitWerten(buchungenZuDatum, eintraege, startTag, aktuelleZeile, aktuelleSpalte);
					aktuelleSpalte++;
				}
			}
			if(aktuelleZeile<maxZeilen - 1) {
				aktuelleSpalte = 0;
				aktuelleZeile++;
			}
		}

		while (aktuelleSpalte < WOCHENTAGE) {
			eintraege[aktuelleZeile][aktuelleSpalte] = "";
			aktuelleSpalte++;
		}
		
		return eintraege;
	}
	
	/**
	 * Gibt erste Buchung zum gewünschten Datum zurück
	 * @param buchungenZuKundeImMonat
	 * 					Buchungen zu dem Kunde im Monat als Liste von Buchungen
	 * @param tagDatum
	 * 					das gewünschte Datum als double
	 * @return eine Buchung mit dem Datum
	 */
	public Buchung findeBuchungMitDatum(List<Buchung> buchungenZuKundeImMonat, double tagDatum) {
		Iterator<Buchung> it = buchungenZuKundeImMonat.iterator();
		while (it.hasNext()) {
			Buchung buchung = it.next();
			if (buchung.getDatum().getDayOfMonth() == (int) tagDatum) {
				buchungenZuKundeImMonat.remove(buchung);
				return buchung;
			}
		}
		return null;
	}
	
	/**
	 * füllt die einträge des Kalenders mit Werten für ein Feld/Datum
	 * @param buchungenZuDatum
	 * 					eine Liste von allen Buchungen zum aktuellen Feld/Datum
	 * @param eintraege
	 * 					alle bisherigen Einträge des Kalenders als zweidimensionaler Array
	 * @param startTag
	 * 					der erste Eintrag im Monat des Kalenders, der nicht leer ist als int
	 * @param aktuelleZeile
	 * 					die aktuelle Zeile im Kalender als int
	 * @param aktuelleSpalte
	 * 					die aktuelle Spalte im Kalender als int
	 */
	public void fuelleZelleMitWerten(List<Buchung> buchungenZuDatum, String[][] eintraege,
			int startTag, int aktuelleZeile, int aktuelleSpalte) {
		eintraege[aktuelleZeile][aktuelleSpalte] = Integer.toString((int) ((aktuelleZeile * WOCHENTAGE)
				+ aktuelleSpalte + 1 - startTag)) + ": ";
		while (!buchungenZuDatum.isEmpty()) {
			eintraege[aktuelleZeile][aktuelleSpalte] += buchungenZuDatum.get(0).getVeranstaltung().getBeschreibung()
					+ " Uhrzeit: " + buchungenZuDatum.get(0).getDatum().toLocalTime().toString() + " Uhr" + " Dauer: " +
					buchungenZuDatum.get(0).getVeranstaltung().getDauer() + " Minuten| ";
			buchungenZuDatum.remove(0);
		}
	}
	
	/**
	 * gib das Datum zurück
	 * @return das Datum als LocalDateTime
	 */
	public LocalDateTime getDatum() {
		return this.datum;
	}
	
	/**
	 * Setzt das Datum auf den übergebenen Wert
	 * @param datum 
	 * 			das Datum als LocalDateTime
	 */
	public void setDatum(LocalDateTime datum) {
		this.datum = datum;
	}

}
