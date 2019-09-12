package artistenverein.Veranstaltungen;

import static org.junit.Assert.*;
import static org.salespointframework.core.Currencies.EURO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.javamoney.moneta.Money;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;

import artistenverein.AbstractIntegrationTests;
import artistenverein.Personenverwaltung.Artistengruppe;
import artistenverein.Personenverwaltung.ManagerUser;
import artistenverein.Personenverwaltung.RepositoryGruppen;
import artistenverein.Personenverwaltung.RepositoryUser;
import artistenverein.Personenverwaltung.User;
import artistenverein.Veranstaltungen.Buchung;
import artistenverein.Veranstaltungen.BuchungRepository;
import artistenverein.Veranstaltungen.EntityVeranstaltung;
import artistenverein.Veranstaltungen.Kalender;
import artistenverein.Veranstaltungen.VeranstaltungsKatalog;
import artistenverein.Veranstaltungen.EntityVeranstaltung.VeranstaltungsType;

public class KalenderTests extends AbstractIntegrationTests {

	@Autowired
	private ManagerUser managerUser;
	@Autowired
	private BuchungRepository buchungRepository;
	@Autowired
	private VeranstaltungsKatalog veranstaltungskatalog;
	@Autowired
	private UserAccountManager userAccountManager;
	@Autowired
	private RepositoryUser userRepository;
	@Autowired
	private RepositoryGruppen gruppenRepository;
	@Autowired
	private RepositoryUser repositoryUser;

	LocalDateTime start = LocalDateTime.of(2017, 12, 24, 19, 30);
	LocalDateTime end = LocalDateTime.of(2018, 12, 31, 23, 59);

	LocalDateTime start1 = LocalDateTime.of(2017, 11, 27, 19, 30);
	LocalDateTime end1 = LocalDateTime.of(2018, 12, 1, 23, 59);

	LocalDateTime start2 = LocalDateTime.of(2017, 12, 12, 19, 30);
	LocalDateTime end2 = LocalDateTime.of(2018, 12, 30, 23, 59);

	@Before
	public void setUp() {
		buchungRepository.deleteAll();
		veranstaltungskatalog.deleteAll();
		buchungRepository.deleteAll();
		veranstaltungskatalog.deleteAll();
		gruppenRepository.deleteAll();
		repositoryUser.deleteAll();

		Role customerRole = Role.of("ROLE_CUSTOMER");

		UserAccount u1 = userAccountManager.create("hans2", "123", customerRole);
		u1.setEmail("hans@web.de");
		u1.setFirstname("Hans");
		u1.setLastname("Schneider");
		User k1 = new User(u1);
		userRepository.save(k1);

		UserAccount u2 = userAccountManager.create("dextermorgan2", "123", customerRole);
		u2.setEmail("dextermorgan@web.de");
		u2.setFirstname("Dexter");
		u2.setLastname("Morgan");
		User k2 = new User(u2);
		userRepository.save(k2);

		customerRole = Role.of("ROLE_ARTIST");

		UserAccount ua1 = userAccountManager.create("genji2", "123", customerRole);
		ua1.setEmail("NeedHealing@gmail.de");
		ua1.setLastname("Shimada");
		ua1.setFirstname("Genji");
		userAccountManager.save(ua1);

		UserAccount ua2 = userAccountManager.create("lucio2", "123", customerRole);
		ua2.setEmail("boostio@web.de");
		ua2.setLastname("Correia ");
		ua2.setFirstname("Lúcio");
		userAccountManager.save(ua2);

		UserAccount ua3 = userAccountManager.create("beepBop2", "123", customerRole);
		ua3.setEmail("playOfTheGame@gmail.de");
		ua3.setLastname("Bastion");
		ua3.setFirstname("Sepp");
		userAccountManager.save(ua3);

		User c1 = new User(ua1);
		User c2 = new User(ua2);
		User c3 = new User(ua3);

		repositoryUser.save(Arrays.asList(c1, c2, c3));

		Artistengruppe gruppe1 = new Artistengruppe("Tanzbären");
		gruppe1.addMitglied(c1);
		gruppe1.addMitglied(c2);
		gruppe1.addMitglied(c3);

		gruppenRepository.save(gruppe1);

		Set<UserAccount> artistengruppe = new HashSet<>();
		artistengruppe.add(ua1);
		artistengruppe.add(ua2);
		artistengruppe.add(ua3);

		veranstaltungskatalog.save(new EntityVeranstaltung("Jongliershow", Money.of(100, EURO),
				"1-2-3 jonglieren ist Zauberei", 200, VeranstaltungsType.SHOW, start, end, artistengruppe, 1, "Tanzbären"));

		veranstaltungskatalog.save(new EntityVeranstaltung("Jonglieren", Money.of(50, EURO),
				"Lerne wie Artisten zu jonglieren", 120, VeranstaltungsType.WORKSHOP, start1, end1, artistengruppe, 2, ""));

		veranstaltungskatalog.save(new EntityVeranstaltung("Feuer spucken", Money.of(50, EURO),
				"Spucke Feuer wie ein Drachen", 120, VeranstaltungsType.WORKSHOP, start2, end2, artistengruppe, 2, ""));

	}

	@Test
	public void getBuchungenZuKundeImMonatTest() {
		Kalender kalender = new Kalender();
		kalender.setDatum(LocalDateTime.of(2017, 12, 1, 0, 0));

		User user = managerUser.sucheUserMitUsername("hans2");
		EntityVeranstaltung v1 = veranstaltungskatalog.findByName("Jongliershow").iterator().next();
		Buchung b1 = new Buchung("Halle", v1.getStartDatum(), v1, user.getUserAccount(), v1.getPrice(),
				UUID.randomUUID());
		buchungRepository.save(b1);

		User user2 = managerUser.sucheUserMitUsername("dextermorgan2");
		EntityVeranstaltung v2 = veranstaltungskatalog.findByName("Feuer spucken").iterator().next();
		Buchung b2 = new Buchung("Halle", v2.getStartDatum(), v2, user2.getUserAccount(), v2.getPrice(),
				UUID.randomUUID());
		buchungRepository.save(b2);

		EntityVeranstaltung v3 = veranstaltungskatalog.findByName("Jonglieren").iterator().next();
		Buchung b3 = new Buchung("Halle", v3.getStartDatum(), v3, user.getUserAccount(), v3.getPrice(),
				UUID.randomUUID());
		buchungRepository.save(b3);

		List<Buchung> testListe = kalender.getBuchungenZuKundeImMonat(managerUser.getBuchungen(),
				user.getUserAccount());
		List<Buchung> richtigeListe = new ArrayList<Buchung>();
		richtigeListe.add(b1);

		assertEquals(richtigeListe, testListe);
	}

	@Test
	public void getBuchungenZuArtistImMonatTest() {
		Kalender kalender = new Kalender();
		kalender.setDatum(LocalDateTime.of(2017, 12, 1, 0, 0));

		User user = managerUser.sucheUserMitUsername("genji2");
		EntityVeranstaltung v1 = veranstaltungskatalog.findByName("Jongliershow").iterator().next();
		Buchung b1 = new Buchung("Halle", v1.getStartDatum(), v1, user.getUserAccount(), v1.getPrice(),
				UUID.randomUUID());
		buchungRepository.save(b1);

		User user2 = managerUser.sucheUserMitUsername("dextermorgan2");
		EntityVeranstaltung v2 = veranstaltungskatalog.findByName("Feuer spucken").iterator().next();
		Buchung b2 = new Buchung("Halle", v2.getStartDatum(), v2, user2.getUserAccount(), v2.getPrice(),
				UUID.randomUUID());
		buchungRepository.save(b2);

		EntityVeranstaltung v3 = veranstaltungskatalog.findByName("Jonglieren").iterator().next();
		Buchung b3 = new Buchung("Halle", v3.getStartDatum(), v3, user.getUserAccount(), v3.getPrice(),
				UUID.randomUUID());
		buchungRepository.save(b3);

		List<Buchung> testListe = kalender.getBuchungenZuArtistImMonat(managerUser.getBuchungen(),
				user.getUserAccount());
		List<Buchung> richtigeListe = new ArrayList<Buchung>();
		richtigeListe.add(b1);
		richtigeListe.add(b2);

		assertEquals(richtigeListe, testListe);
	}

	@Test
	public void findeBuchungMitDatumTest() {
		Kalender kalender = new Kalender();
		kalender.setDatum(LocalDateTime.of(2017, 12, 1, 0, 0));

		User user = managerUser.sucheUserMitUsername("hans2");
		EntityVeranstaltung v1 = veranstaltungskatalog.findByName("Jongliershow").iterator().next();
		Buchung b1 = new Buchung("Halle", v1.getStartDatum(), v1, user.getUserAccount(), v1.getPrice(),
				UUID.randomUUID());
		buchungRepository.save(b1);

		User user2 = managerUser.sucheUserMitUsername("dextermorgan2");
		EntityVeranstaltung v2 = veranstaltungskatalog.findByName("Feuer spucken").iterator().next();
		Buchung b2 = new Buchung("Halle", v2.getStartDatum(), v2, user2.getUserAccount(), v2.getPrice(),
				UUID.randomUUID());
		buchungRepository.save(b2);

		EntityVeranstaltung v3 = veranstaltungskatalog.findByName("Jonglieren").iterator().next();
		Buchung b3 = new Buchung("Halle", v3.getStartDatum(), v3, user.getUserAccount(), v3.getPrice(),
				UUID.randomUUID());
		buchungRepository.save(b3);

		List<Buchung> buchungenZuKundeImMonat = kalender.getBuchungenZuKundeImMonat(managerUser.getBuchungen(),
				user.getUserAccount());

		Buchung testBuchung = kalender.findeBuchungMitDatum(buchungenZuKundeImMonat, 24);
		Buchung richtigeBuchung = b1;

		assertEquals(testBuchung, richtigeBuchung);
	}

	@Test
	public void getEintraegeFuerMonat() {
		Kalender kalender = new Kalender();
		kalender.setDatum(LocalDateTime.of(2017, 12, 1, 0, 0));

		User user = managerUser.sucheUserMitUsername("hans2");
		EntityVeranstaltung v1 = veranstaltungskatalog.findByName("Jongliershow").iterator().next();
		Buchung b1 = new Buchung("Halle", v1.getStartDatum(), v1, user.getUserAccount(), v1.getPrice(),
				UUID.randomUUID());
		buchungRepository.save(b1);

		User user2 = managerUser.sucheUserMitUsername("dextermorgan2");
		EntityVeranstaltung v2 = veranstaltungskatalog.findByName("Feuer spucken").iterator().next();
		Buchung b2 = new Buchung("Halle", v2.getStartDatum(), v2, user2.getUserAccount(), v2.getPrice(),
				UUID.randomUUID());
		buchungRepository.save(b2);

		EntityVeranstaltung v3 = veranstaltungskatalog.findByName("Jonglieren").iterator().next();
		Buchung b3 = new Buchung("Halle", v3.getStartDatum(), v3, user.getUserAccount(), v3.getPrice(),
				UUID.randomUUID());
		buchungRepository.save(b3);

		List<Buchung> buchungenZuKundeImMonat = kalender.getBuchungenZuKundeImMonat(managerUser.getBuchungen(),
				user.getUserAccount());

		String[][] testEintraege = kalender.getEintraegeFuerMonat(buchungenZuKundeImMonat);
		String[][] richtigeEintraege = { { "", "", "", "", "1: ", "2: ", "3: " },
				{ "4: ", "5: ", "6: ", "7: ", "8: ", "9: ", "10: " },
				{ "11: ", "12: ", "13: ", "14: ", "15: ", "16: ", "17: " },
				{ "18: ", "19: ", "20: ", "21: ", "22: ", "23: ",
						"24: " + b1.getVeranstaltung().getBeschreibung() + " Uhrzeit: " + b1.getDatum().toLocalTime().toString() + " Uhr" + " Dauer: "
								+ b1.getVeranstaltung().getDauer() + " Minuten| " },
				{ "25: ", "26: ", "27: ", "28: ", "29: ", "30: ", "31: " } };

		Assert.assertArrayEquals(richtigeEintraege, testEintraege);

	};

}
