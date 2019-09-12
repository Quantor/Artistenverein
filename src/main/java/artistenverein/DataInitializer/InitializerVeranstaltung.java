
package artistenverein.DataInitializer;

import static org.salespointframework.core.Currencies.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.javamoney.moneta.Money;
import org.salespointframework.core.DataInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;

import artistenverein.Personenverwaltung.RepositoryUser;
import artistenverein.Personenverwaltung.User;
import artistenverein.Veranstaltungen.Bewertung;
import artistenverein.Veranstaltungen.BuchungRepository;
import artistenverein.Veranstaltungen.EntityVeranstaltung;
import artistenverein.Veranstaltungen.VeranstaltungsKatalog;
import artistenverein.Veranstaltungen.Zusatzkosten;
import artistenverein.Veranstaltungen.ZusatzkostenRepository;
import artistenverein.Veranstaltungen.EntityVeranstaltung.VeranstaltungsType;
import artistenverein.Zeitverwaltung.ZeitRepository;

/**
 * Die Klasse Initialisiert Veranstaltungen
 *
 * @author Emanuel Kern
 */
@Component
public class InitializerVeranstaltung implements DataInitializer {
	private final VeranstaltungsKatalog veranstaltungsKatalog;
	private final RepositoryUser customerRepository;
	private final ZusatzkostenRepository zusatzKostenRepro;

    /**
     * Der Konstruktor
     *
     * @param veranstaltungsKatalog
     *  Das Repository, in dem die Veranstaltungen gespeichert werden.
     *
     * @param customerRepository
     *  Das Repository, in dem die Kunden gespeichert werden.
     *
     * @param zusatzKostenRepro
     *  Das Repository, in dem die Zusatzkosten gespeichert werden.
     *
     * @param zeitRepository
     *  Das Repository, in dem die Sperrzeiten gespeichert werden.
     *
     * @param accountManager
     *  Der Account Manager
     *
     * @param buchungRepository
     *  Das Repository, in dem Buchungen gespeichert werden.
     *
     * @param userRepository
     *  Das Repository, in dem Nutzer gespeichert werden
     */
	@Autowired
	public InitializerVeranstaltung(VeranstaltungsKatalog veranstaltungsKatalog, RepositoryUser customerRepository,
			ZusatzkostenRepository zusatzKostenRepro, ZeitRepository zeitRepository, UserAccountManager accountManager,
			BuchungRepository buchungRepository, RepositoryUser userRepository) {

		Assert.notNull(veranstaltungsKatalog, "veranstaltungsKatalog must not be null!");

		this.veranstaltungsKatalog = veranstaltungsKatalog;
		this.customerRepository = customerRepository;
		this.zusatzKostenRepro = zusatzKostenRepro;
	}

    /**
     * Diese Funktion initialisiert den Veranstaltungskatalog und die Zusatzkosten
     */
	@Override
	public void initialize() {

		initializeCatalog(veranstaltungsKatalog);
		initializeZusatzkosten();
	}

    /**
     * Diese Funktion fügt einige Veranstaltungen zur Datenbank hinzu.
     *
     * @param veranstaltungskatalog
     *  Hier werden alle Veranstaltungen gespeichert.
     */
	private void initializeCatalog(VeranstaltungsKatalog veranstaltungskatalog) {

		Iterable<User> artisten = customerRepository.findAll();
		LocalDateTime start = LocalDateTime.of(2017, 12, 24, 19, 30);
		LocalDateTime end = LocalDateTime.of(2018, 12, 31, 23, 59);

		LocalDateTime start1 = LocalDateTime.of(2017, 11, 27, 19, 30);
		LocalDateTime end1 = LocalDateTime.of(2018, 12, 1, 23, 59);

		LocalDateTime start2 = LocalDateTime.of(2017, 12, 12, 19, 30);
		LocalDateTime end2 = LocalDateTime.of(2018, 12, 30, 23, 59);

		LocalDateTime start3 = LocalDateTime.of(2015, 01, 11, 19, 30);
		LocalDateTime end3 = LocalDateTime.of(2016, 12, 30, 23, 59);

		LocalDateTime start4 = LocalDateTime.of(2016, 01, 30, 19, 30);
		LocalDateTime end4 = LocalDateTime.of(2017, 12, 30, 23, 59);

		if (veranstaltungskatalog.findAll().iterator().hasNext()) {
			return;
		}

		Set<UserAccount> artistengruppe = new HashSet<>();
		for (Iterator<User> it = artisten.iterator(); it.hasNext();) {
			User artist = it.next();
			artistengruppe.add(artist.getUserAccount());
		

			veranstaltungskatalog.save(new EntityVeranstaltung("fliegende Bälle und Kegel", Money.of(100, EURO),
					"1-2-3 jonglieren ist Zauberei", 200, VeranstaltungsType.SHOW, start, end, artistengruppe, 1,"Tanzbären"));
			
			veranstaltungskatalog.save(new EntityVeranstaltung("Kohlen Sohlen", Money.of(125, EURO),
					"Die Meister überwinden die Flammen", 200, VeranstaltungsType.SHOW, start2, end2, artistengruppe, 1, "Tanzbären"));
			
			veranstaltungskatalog.save(new EntityVeranstaltung("Tanz für den Teufel", Money.of(666, EURO),
					"Tanzen auf dem Hexenberg", 120, VeranstaltungsType.SHOW, start2, end2, artistengruppe, 1, "Tanzbären"));

			veranstaltungskatalog
					.save(new EntityVeranstaltung("Jonglieren", Money.of(50, EURO), "Lerne wie Artisten zu jonglieren",
							120, VeranstaltungsType.WORKSHOP, start1, end1, artistengruppe, 2, ""));
			veranstaltungskatalog.save(new EntityVeranstaltung("Feuer spucken", Money.of(50, EURO),
					"Spucke Feuer wie ein Drachen", 120, VeranstaltungsType.WORKSHOP, start2, end2, artistengruppe, 2, ""));

			EntityVeranstaltung highlight = new EntityVeranstaltung("Schwert Schlucken", Money.of(25, EURO),
					"Der berühmte Meister wird euch zeigen, wie man ein 2 Meter Kriegsmesser komplett in seinme Rachen verschwinden lassen kann.",
					120, VeranstaltungsType.WORKSHOP, start3, end3, artistengruppe, 1, "");

			highlight.addBewertung(new Bewertung("Coole Sache", 5, start3, it.next().getUserAccount()));

			highlight.addBewertung(new Bewertung("War ganz ok", 3, start3, it.next().getUserAccount()));

			highlight.addBewertung(new Bewertung("Will mein Geld zurück", 1, start3, it.next().getUserAccount()));
			veranstaltungskatalog.save(highlight);

			EntityVeranstaltung old = new EntityVeranstaltung("Schnee von Gestern", Money.of(99, EURO),
					"Spaß mit Schnee", 120, VeranstaltungsType.SHOW, start4, end4, artistengruppe, 1, "Tanzbären");

			old.addBewertung(new Bewertung("Schneeman bauen macht Spaß", 3, start3, it.next().getUserAccount()));

			old.addBewertung(new Bewertung("Winterwunderland", 4, start3, it.next().getUserAccount()));

			old.addBewertung(new Bewertung("Ich bin ein Miesepeter ", 1, start3, it.next().getUserAccount()));
			veranstaltungskatalog.save(old);
			return;
		}

	}

    /**
     * Diese Funktion setzt beispielhafte Zusatzkosten.
     */
	private void initializeZusatzkosten() {
		zusatzKostenRepro.deleteAll();
		zusatzKostenRepro.save(new Zusatzkosten(50.0));
	}
}
