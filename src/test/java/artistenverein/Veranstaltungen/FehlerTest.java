package artistenverein.Veranstaltungen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.salespointframework.core.Currencies.EURO;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.javamoney.moneta.Money;
import org.junit.BeforeClass;
import org.junit.Test;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;

import artistenverein.AbstractIntegrationTests;
import artistenverein.Personenverwaltung.RepositoryUser;
import artistenverein.Personenverwaltung.User;
import artistenverein.Veranstaltungen.Buchung;
import artistenverein.Veranstaltungen.BuchungRepository;
import artistenverein.Veranstaltungen.EntityVeranstaltung;
import artistenverein.Veranstaltungen.Fehler;
import artistenverein.Veranstaltungen.VeranstaltungsKatalog;
import artistenverein.Veranstaltungen.EntityVeranstaltung.VeranstaltungsType;
import artistenverein.Zeitverwaltung.ZeitRepository;
import artistenverein.Zeitverwaltung.Zeitverwaltung;

public class FehlerTest extends AbstractIntegrationTests {
	@Autowired
	BuchungRepository buchungRepository;
	@Autowired
	RepositoryUser userRepository;
	@Autowired
	VeranstaltungsKatalog veranstaltungsKatalog;
	@Autowired
	UserAccountManager userAccountManager;
	@Autowired
	ZeitRepository zeitRepository;
	
	
	private Fehler testFehler1 = new Fehler();
	private final LocalDateTime start = LocalDateTime.of(2017, 12, 24, 19, 30);
	private final LocalDateTime end = LocalDateTime.of(2017, 12, 31, 23, 59);
	private final LocalDateTime vorStart = LocalDateTime.of(2017,12,23,19,30);
	private final LocalDateTime vorEnd = LocalDateTime.of(2017,12,31,23,00);
	private final LocalDateTime genauEndDatum = LocalDateTime.of(2017, 12, 24, 22, 50);
	private final LocalDateTime vorStartDatum = LocalDateTime.of(2017, 12, 24, 16, 10);
	LocalDateTime today = LocalDateTime.now();

	
	@BeforeClass
	public static void setUp() {
		
	}
	
	public User erstelleKunde() {
		User user = new User(new UserAccount());
		for(User userRepo: userRepository.findAll()) {
			if(userRepo.getUserAccount().getUsername().equals("hans")) {
				user = userRepo;
			}
		}
		userRepository.save(user);
		return user;
	}
	
	public User erstelleArtist(String name) {
		
		//veranstaltungsKatalog.save(veranstaltung);
		
		User artist = new User(new UserAccount());
		for(User userRepo: userRepository.findAll()) {
			if(userRepo.getUserAccount().getUsername().equals(name)) {
				artist = userRepo;
			}
		}
		userRepository.save(artist);
		return artist;
	}
	
	public EntityVeranstaltung erstelleVeranstaltung(String s, Set<UserAccount> artistengruppe) {
		EntityVeranstaltung veranstaltung = new EntityVeranstaltung(s, Money.of(100, EURO),
				"Test Beschreibung", 200, VeranstaltungsType.SHOW, start, end, artistengruppe, 1, "Tanzbären");
		veranstaltungsKatalog.save(veranstaltung);
		return veranstaltung;
	}
	
	@Test
	public void pruefeSperrzeitTest() {
		Zeitverwaltung zeitverwaltung = new Zeitverwaltung(zeitRepository, userAccountManager);
		User artist = erstelleArtist("lucio");
		User artist2 = erstelleArtist("genji");
		zeitverwaltung.sperrzeitEintragenEinmalig(start, Duration.ofMinutes(100), "Test", "Test",
				artist.getUserAccount());
		zeitverwaltung.sperrzeitEintragenEinmalig(end, Duration.ofMinutes(100), "Test", "Test",
				artist.getUserAccount());
		Set<UserAccount> artisten = new HashSet<>();
		artisten.add(artist.getUserAccount());
		artisten.add(artist2.getUserAccount());
		EntityVeranstaltung veranstaltung = erstelleVeranstaltung("Test",artisten);
		
		
		assertTrue("Fehler.pruefeSperrzeit() sollte true zurückgeben, falls mindestens ein Artiste zum eingegebenen Zeitraum seine Sperrzeit hat",
				testFehler1.pruefeSperrzeit(veranstaltung, start, Duration.ofMinutes(veranstaltung.getDauer()) ,
						zeitverwaltung)== true);
		assertTrue("Fehler.pruefeSperrzeit() sollte true zurückgeben, falls mindestens ein Artiste zum eingegebenen Zeitraum seine Sperrzeit hat (Grenzfall)",
				testFehler1.pruefeSperrzeit(veranstaltung, start.minusMinutes(100), Duration.ofMinutes(veranstaltung.getDauer()) ,
						zeitverwaltung)== true);
		assertTrue("Fehler.pruefeSperrzeit() sollte true zurückgeben, falls mindestens ein Artiste zum eingegebenen Zeitraum seine Sperrzeit hat",
				testFehler1.pruefeSperrzeit(veranstaltung, end, Duration.ofMinutes(veranstaltung.getDauer()) ,
						zeitverwaltung)== true);
		/*assertTrue("Fehler.pruefeSperrzeit() sollte true zurückgeben, falls mindestens ein Artiste zum eingegebenen Zeitraum seine Sperrzeit hat(Grenzwert)",
				testFehler1.pruefeSperrzeit(veranstaltung, end.plusMinutes(100), Duration.ofMinutes(veranstaltung.getDauer()) ,
						userRepository, zeitverwaltung)== true);*/
		assertTrue("Fehler.pruefeSperrzeit() sollte false zurückgeben, falls keiner der Artisten zum eingegebenen Zeitraum seine Sperrzeit hat",
				testFehler1.pruefeSperrzeit(veranstaltung, end.plusMinutes(101), Duration.ofMinutes(veranstaltung.getDauer()) ,
						zeitverwaltung)== false);
	}
	
	@Test
	public void pruefeSperrzeitNullPointerTest() {
		
		Set<UserAccount> artisten = new HashSet<>();
		EntityVeranstaltung veranstaltung = erstelleVeranstaltung("Test", artisten);
		
		try {
			Zeitverwaltung zeitverwaltung = new Zeitverwaltung(zeitRepository, userAccountManager);
			testFehler1.pruefeSperrzeit(null, start, Duration.ofMinutes(200), zeitverwaltung);
			fail("Fehler.pruefeArtistenVerbucht() sollte eine IllegalArgumentException werfen, wenn das Argument Veranstaltung null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "veranstaltung darf nicht null sein!");
		}
		
		try {
			Zeitverwaltung zeitverwaltung = new Zeitverwaltung(zeitRepository, userAccountManager);
			testFehler1.pruefeSperrzeit(veranstaltung, null, Duration.ofMinutes(200), zeitverwaltung);
			fail("Fehler.pruefeArtistenVerbucht() sollte eine IllegalArgumentException werfen, wenn das Argument start-Datum null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "start-Datum darf nicht null sein!");
		}
		
		try {
			Zeitverwaltung zeitverwaltung = new Zeitverwaltung(zeitRepository, userAccountManager);
			testFehler1.pruefeSperrzeit(veranstaltung, start , null, zeitverwaltung);
			fail("Fehler.pruefeArtistenVerbucht() sollte eine IllegalArgumentException werfen, wenn das Argument Dauer null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "dauer darf nicht null sein!");
		}
		
		try {
			testFehler1.pruefeSperrzeit(veranstaltung, start, Duration.ofMinutes(200), null);
			fail("Fehler.pruefeArtistenVerbucht() sollte eine IllegalArgumentException werfen, wenn das Argument Zeitverwaltung null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "zeitverwaltung darf nicht null sein!");
		}
	}
	
	@Test
	public void pruefeVerbuchtTest() {
		User artist = erstelleArtist("genji");
		User artist2 = erstelleArtist("lucio");
		User user = erstelleKunde(); 
		Set<UserAccount> artistengruppe = new HashSet<>();
		artistengruppe.add(artist.getUserAccount());
		artistengruppe.add(artist2.getUserAccount());
		Set<UserAccount> artistengruppe2 = new HashSet<>();
		artistengruppe2.add(artist.getUserAccount());
		EntityVeranstaltung veranstaltung = erstelleVeranstaltung("TestShow1", artistengruppe);
		EntityVeranstaltung veranstaltung2 = erstelleVeranstaltung("TestShow2", artistengruppe2);
		buchungRepository.save(new Buchung("Halle", start, veranstaltung, user.getUserAccount(), Money.of(40, EURO), UUID.randomUUID()));
		
		
		assertTrue("Fehler.pruefeVerbucht() sollte true zurückgeben, falls mindestens ein Artiste zum eingegebenen Zeitraum nicht mehr verfügbar ist. (auch genau Start)",
				testFehler1.pruefeArtistenVerbucht(veranstaltung2, buchungRepository, vorStartDatum , Duration.ofMinutes(veranstaltung.getDauer()))== true);
		assertTrue("Fehler.pruefeVerbucht() sollte true zurückgeben, falls mindestens ein Artiste zum eingegebenen Zeitraum nicht mehr verfügbar ist. (auch genau Ende)",
				testFehler1.pruefeArtistenVerbucht(veranstaltung2, buchungRepository, genauEndDatum, Duration.ofMinutes(veranstaltung.getDauer()))== true);
		assertTrue("Fehler.pruefeVerbucht() sollte true zurückgeben, falls mindestens ein Artiste zum eingegebenen Zeitraum nicht mehr verfügbar ist.",
				testFehler1.pruefeArtistenVerbucht(veranstaltung2, buchungRepository, start.plusMinutes(130), Duration.ofMinutes(veranstaltung.getDauer()))== true);
		assertTrue("Fehler.pruefeVerbucht() sollte false zurückgeben, falls alle Artisten zum eingegebenen Zeitraum verfügbar sind.",
				testFehler1.pruefeArtistenVerbucht(veranstaltung2, buchungRepository, genauEndDatum.plusMinutes(1), Duration.ofMinutes(veranstaltung.getDauer()))== false);
		assertTrue("Fehler.pruefeVerbucht() sollte false zurückgeben, falls alle Artisten zum eingegebenen Zeitraum verfügbar sind.",
				testFehler1.pruefeArtistenVerbucht(veranstaltung2, buchungRepository, vorStartDatum.minusMinutes(1), Duration.ofMinutes(veranstaltung.getDauer()))== false);
	}
	
	@Test
	public void pruefeVerbuchtVeranstaltungNullPointerTest() {
		
		Set<UserAccount> artisten = new HashSet<>();
		EntityVeranstaltung veranstaltung = erstelleVeranstaltung("Test", artisten);
		
		try {
			testFehler1.pruefeArtistenVerbucht(null, buchungRepository, start, Duration.ofMinutes(200));
			fail("Fehler.pruefeArtistenVerbucht() sollte eine IllegalArgumentException werfen, wenn das Argument Veranstaltung null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "veranstaltung darf nicht null sein!");
		}
		
		try {
			testFehler1.pruefeArtistenVerbucht(veranstaltung, null, start, Duration.ofMinutes(200));
			fail("Fehler.pruefeArtistenVerbucht() sollte eine IllegalArgumentException werfen, wenn das Argument BuchungsRepository null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "buchungRepository darf nicht null sein!");
		}

		try {
			testFehler1.pruefeArtistenVerbucht(veranstaltung, buchungRepository, null, Duration.ofMinutes(200));
			fail("Fehler.pruefeArtistenVerbucht() sollte eine IllegalArgumentException werfen, wenn das Argument Start-Datum null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "start-Datum darf nicht null sein!");
		}

		try {
			testFehler1.pruefeArtistenVerbucht(veranstaltung, buchungRepository,start, null);
			fail("Fehler.pruefeArtistenVerbucht() sollte eine IllegalArgumentException werfen, wenn das Argument Dauer null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "dauer darf nicht null sein!");
		}
	}
	
	
	@Test
	public void pruefeVergangenheitTest() {
		assertTrue("Fehler.pruefeVergangenheit() sollte true zurückgeben, falls eingegebenes Datum in der Vergangenheit liegt.",
				testFehler1.pruefeVergangenheit(today.minusMinutes(1000))==true);
		assertTrue("Fehler.pruefeVergangenheit() sollte false zurückgeben, falls eingegebenes Datum in der Zukunft liegt.",
				testFehler1.pruefeVergangenheit(today.plusMinutes(1000))==false);
		assertTrue("Fehler.pruefeVergangenheit() sollte true zurückgeben, falls eingegebenes Datum in der Vergangenheit (genau jetzt) liegt.",
				testFehler1.pruefeVergangenheit(today)==true);
		
	}
	
	@Test
	public void pruefeVergangenheitNullPointerTest() {
		try {
			testFehler1.pruefeVergangenheit(null);
			fail("Fehler.pruefeVergangenheit() sollte eine IllegalArgumentException werfen, wenn das Argument Datum null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "datum darf nicht null sein!");
		}
	}
	
	@Test
	public void pruefeHalleTest() {

		User artist = erstelleArtist("genji");
		User user = erstelleKunde();
		Set<UserAccount> artistengruppe = new HashSet<>();
		artistengruppe.add(artist.getUserAccount());
		EntityVeranstaltung veranstaltung = erstelleVeranstaltung("TestShow", artistengruppe);
		buchungRepository.save(new Buchung("Halle", start, veranstaltung, user.getUserAccount(), Money.of(40, EURO), UUID.randomUUID()));
		
		assertTrue("Fehler.pruefeHalle() sollte true zurückgeben, falls Halle im gebuchten Zeitraum besetzt ist.",
				testFehler1.pruefeHalle(start, Duration.ofMinutes(200),  buchungRepository)==true);
		assertTrue("Fehler.pruefeHalle() sollte false zurückgeben, falls Halle im gebuchten Zeitraum nicht besetzt ist.",
				testFehler1.pruefeHalle(vorStart, Duration.ofMinutes(200),  buchungRepository)==false);
		assertTrue("Fehler.pruefeHalle() sollte true zurückgeben, falls Halle im gebuchten Zeitraum besetzt ist. (mit Dauer!, genau End-Datum)",
				testFehler1.pruefeHalle(genauEndDatum, Duration.ofMinutes(200),  buchungRepository)==true);
		assertTrue("Fehler.pruefeHalle() sollte true zurückgeben, falls Halle im gebuchten Zeitraum besetzt ist. (mit Dauer!, genau Start-Datum)",
				testFehler1.pruefeHalle(vorStartDatum, Duration.ofMinutes(200),  buchungRepository)==true);
	}
	
	@Test
	public void pruefeHalleNullPointerTest() {
		try {
			testFehler1.pruefeHalle(start, Duration.ofMinutes(200), null);
			fail("Fehler.pruefeHalle() sollte eine IllegalArgumentException werfen, wenn das Argument BuchungsRepository null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "buchungRepository darf nicht null sein!");
		}
		
		try {
			testFehler1.pruefeHalle(null, Duration.ofMinutes(200), buchungRepository);
			fail("Fehler.pruefeHalle() sollte eine IllegalArgumentException werfen, wenn das Argument start-Datum null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "start-Datum darf nicht null sein!");
		}

		try {
			testFehler1.pruefeHalle(start, null, buchungRepository);
			fail("Fehler.pruefeHalle() sollte eine IllegalArgumentException werfen, wenn das Argument Dauer null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "dauer darf nicht null sein!");
		}
	}
	
	@Test
	public void pruefeDauerTest() {
		assertTrue("Fehler.pruefeDauer() sollte true bei negativen Eingaben ausgeben",
				 (testFehler1.pruefeDauer(Duration.ofMinutes(-2))==true));
		assertTrue("Fehler.pruefeDauer() sollte false bei positiven Eingaben ausgeben",
				testFehler1.pruefeDauer(Duration.ofMinutes(200))==false);
	}
	
	@Test
	public void pruefeDauerNullPointerTest() {
		try {
			testFehler1.pruefeDauer(null);
			fail("Fehler.pruefeDauer() sollte eine IllegalArgumentException werfen, wenn das Argument Dauer null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "Dauer darf nicht null sein!");
		}
	}
	
	@Test
	public void pruefeDatumTest() {
		assertTrue("Fehler.pruefeDatum() sollte true ausegben, wenn das Startdatum hinter dem Enddatum liegt", 
				testFehler1.pruefeDatum(end, start)==true);
		assertTrue("Fehler.pruefeDatum() sollte false ausegben, wenn das Startdatum vor dem Enddatum liegt",
				testFehler1.pruefeDatum(start, end)==false);
		assertTrue("Fehler.pruefeDatum() sollte true ausegben, wenn das Startdatum gleich dem Enddatum liegt",
				testFehler1.pruefeDatum(start, start)==true);
	}
	
	@Test
	public void pruefeDatumNullPointerTest() {
		try {
			testFehler1.pruefeDatum(null,end);
			fail("Fehler.pruefeDatum() sollte eine IllegalArgumentException werfen, wenn das Argument start-Datum null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "Start-Datum darf nicht null sein!");
		}

		try {
			testFehler1.pruefeDatum(start,null);
			fail("Fehler.pruefeDatum() sollte eine IllegalArgumentException werfen, wenn das Argument end-Datum null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "End-Datum darf nicht null sein!");
		}
	}
	
	@Test
	public void pruefeZusatzkostenTest() {
		assertTrue("Fehler.pruefeZusatzkosten() sollte true ausegben,wenn ein String eingegeben wird.", 
				testFehler1.pruefeZusatzkosten("zusatzkosten")==true);
		assertTrue("Fehler.pruefeZusatzkosten() sollte true ausgeben, wenn eine negative Zahl eingegeben wird.",
				testFehler1.pruefeZusatzkosten("-1")==true);
		assertTrue("Fehler.pruefeZusatzkosten() sollte false ausgeben, wenn eine positive Zahl eingegeben wird.",
				testFehler1.pruefeZusatzkosten("200")==false);
		
	}
	
	@Test
	public void pruefeZusatzkostenNullPointerTest() {
		try {
			testFehler1.pruefeZusatzkosten(null);
			fail("Fehler.pruefeZusatzkosten() sollte eine IllegalArgumentException werfen, wenn das Argument zusatzkosten null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "Zusatzkosten darf nicht null sein!");
		}
	}
	
	@Test
	public void pruefeBewertungTest() {
		assertTrue("Fehler.pruefeBewertung() sollte true ausgeben, wenn eingegebene Bewertung kleiner als 0 ist.",
				testFehler1.pruefeBewertung(-1)==true);
		assertTrue("Fehler.pruefeBewertung() sollte true ausgeben, wenn eingegebene Bewertung groeßer als 5 ist.",
				testFehler1.pruefeBewertung(6)==true);
		assertTrue("Fehler.pruefeBewertung() sollte false ausgeben, wenn eingegebene Bewertung im Bereich zwischen 0 und 5 liegt.",
				testFehler1.pruefeBewertung(3)==false);
	}
	
	@Test
	public void pruefeZeitraumTest() {
		Set<UserAccount> artistengruppe = new HashSet<>();
		EntityVeranstaltung veranstaltung = erstelleVeranstaltung("TestShow", artistengruppe);
		assertTrue("Fehler.pruefeZeitraum() sollte true ausgeben,falls Datum vor vorgegebenem Zeitraum der Veranstaltung liegt",
				testFehler1.pruefeZeitraum(veranstaltung, vorStart)==true);
		assertTrue("Fehler.pruefeZeitraum() sollte true ausgeben,falls Datum hinter vorgegebenem Zeitraum der Veranstaltung liegt",
				testFehler1.pruefeZeitraum(veranstaltung, end)==true);
		assertTrue("Fehler.pruefeZeitraum() sollte false ausgeben,falls Datum im vorgegebenem Zeitraum der Veranstaltung liegt",
				testFehler1.pruefeZeitraum(veranstaltung, start)==false);
		assertTrue("Fehler.pruefeZeitraum() sollte true ausgeben,falls Datum(mit Dauer!) hinter vorgegebenem Zeitraum der Veranstaltung liegt",
				testFehler1.pruefeZeitraum(veranstaltung, vorEnd)==true);
	}
	
	@Test
	public void pruefeZeitraumNullPointerTest() {
		
		Set<UserAccount> artisten = new HashSet<>();
		EntityVeranstaltung veranstaltung = erstelleVeranstaltung("Test", artisten);
		
		try {
			testFehler1.pruefeZeitraum(null, start);
			fail("Fehler.pruefeZeitraum() sollte eine IllegalArgumentException werfen, wenn das Argument veranstaltung null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "veranstaltung darf nicht null sein!");
		}
		
		try {
			testFehler1.pruefeZeitraum(veranstaltung, null);
			fail("Fehler.pruefeZeitraum() sollte eine IllegalArgumentException werfen, wenn das Argument Datum null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "datum darf nicht null sein!");
		}
	}
}