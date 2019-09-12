
package artistenverein.Zeitverwaltung;

import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import artistenverein.AbstractIntegrationTests;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Ruby on 12/4/17.
 */
public class ZeitverwaltungTests extends AbstractIntegrationTests {

	private static UserAccount testArtist;
	@Autowired
	private UserAccountManager userAccountManager;
	@Autowired
	private ZeitRepository zeitRepo;
	@Autowired
	private UserAccountManager accounts;
	private static LocalDateTime TestZeitpunkt1;
	private static Duration TestDauer1;
	@Autowired
	private Zeitverwaltung zeitverwaltung;

	@Before
	public void setUp() {
		if (!userAccountManager.findByUsername("genji").isPresent()) {
			Role customerRole = Role.of("ROLE_ARTIST");
			testArtist = userAccountManager.create("genji", "123", customerRole);
			testArtist.setEmail("NeedHealing@gmail.de");
			testArtist.setLastname("Shimada");
			testArtist.setFirstname("Genji");
		} else {
			testArtist = userAccountManager.findByUsername("genji").get();
		}

		TestZeitpunkt1 = LocalDateTime.of(2017, 1, 1, 12, 0);
		LocalDateTime TestZeitpunkt2 = LocalDateTime.of(2016, 1, 1, 10, 30);
		LocalDateTime TestZeitpunkt3 = LocalDateTime.of(2017, 4, 1, 11, 45);

		TestDauer1 = Duration.ofHours(1);
		Duration TestDauer2 = Duration.ofHours(5);
		Duration TestDauer3 = Duration.ofHours(10);

		zeitRepo.deleteAll();
		zeitverwaltung.sperrzeitEintragen(TestZeitpunkt1, TestDauer1, Haeufigkeit.EINMAL, "Name", "Kommentar",
				testArtist);
		zeitverwaltung.sperrzeitEintragen(TestZeitpunkt2, TestDauer2, Haeufigkeit.WOECHENTLICH, "Name2", "Kommentar2",
				testArtist);
		zeitverwaltung.sperrzeitEintragen(TestZeitpunkt3, TestDauer3, Haeufigkeit.MONATLICH, "Name3", "Kommentar3",
				testArtist);
	}

	@Test
	public void ZeitverwaltungErstellTest() {
		try {
			Zeitverwaltung zv = new Zeitverwaltung(null, accounts);
			zv.getClass();
			fail("ArtistSperrZeit.ArtistSperrZeit() sollte eine IllegalArgumentException werfen, wenn das Argument zeitRepo null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "zeitRepo darf nicht null sein!");
		}

		try {
			Zeitverwaltung zv = new Zeitverwaltung(zeitRepo, null);
			zv.getClass();
			fail("ArtistSperrZeit.ArtistSperrZeit() sollte eine IllegalArgumentException werfen, wenn das Argument accountManager null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "accountManager darf nicht null sein!");
		}
	}

	@Test
	public void loescheSperrzeitTest() {
		long id = 0;
		int i = 0;
		for (ArtistSperrZeit z : zeitRepo.findAll()) {
			i++;
			id = z.getId();
		}
		zeitverwaltung.loescheSperrzeit(0);
		int j = 0;
		for (ArtistSperrZeit z : zeitRepo.findAll()) {
			j++;
			id = z.getId();
		}
		assertEquals("Zeitverwaltung.loescheSperrzeit() sollte nichts löschen, wenn die id nicht vorhanden ist", i, j);
		zeitverwaltung.loescheSperrzeit(id);
		j = 0;
		for (ArtistSperrZeit z : zeitRepo.findAll()) {
			j++;
			id = z.getId();
		}
		assertEquals("Zeitverwaltung.loescheSperrzeit() sollte die Sperrzeit löschen, wenn die id vorhanden ist", i - 1,
				j);

	}

	@Test
	public void SchaltjahrTests() {
		assertTrue(Zeitverwaltung.istSchaltjahr(2000));
		assertFalse(Zeitverwaltung.istSchaltjahr(2100));
		assertFalse(Zeitverwaltung.istSchaltjahr(2200));
		assertFalse(Zeitverwaltung.istSchaltjahr(1900));
		assertTrue(Zeitverwaltung.istSchaltjahr(2004));
		assertTrue(Zeitverwaltung.istSchaltjahr(2008));
		assertTrue(Zeitverwaltung.istSchaltjahr(1996));
		assertFalse(Zeitverwaltung.istSchaltjahr(2001));
		assertFalse(Zeitverwaltung.istSchaltjahr(2423));
		assertFalse(Zeitverwaltung.istSchaltjahr(1999));
	}

	@Test
	public void sperrzeitEintragenExceptionsTests() {
		try {
			zeitverwaltung.sperrzeitEintragen(null, TestDauer1, Haeufigkeit.EINMAL, "", "", testArtist);
			fail("Zeit.sperrzeitEintragen() sollte eine IllegalArgumentException werfen, wenn das Argument zeitpunkt"
					+ " null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "zeitpunkt darf nicht null sein!");
		}

		try {
			zeitverwaltung.sperrzeitEintragen(TestZeitpunkt1, null, Haeufigkeit.EINMAL, "", "", testArtist);
			fail("Zeit.sperrzeitEintragen() sollte eine IllegalArgumentException werfen, wenn das Argument dauer"
					+ " null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "dauer darf nicht null sein!");
		}

		try {
			zeitverwaltung.sperrzeitEintragen(TestZeitpunkt1, TestDauer1, null, "", "", testArtist);
			fail("Zeit.sperrzeitEintragen() sollte eine IllegalArgumentException werfen, wenn das Argument haeufigkeit"
					+ " null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "haeufigkeit darf nicht null sein!");
		}

		try {
			zeitverwaltung.sperrzeitEintragen(TestZeitpunkt1, TestDauer1, Haeufigkeit.EINMAL, null, "", testArtist);
			fail("Zeit.sperrzeitEintragen() sollte eine IllegalArgumentException werfen, wenn das Argument name"
					+ " null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "name darf nicht null sein!");
		}

		try {
			zeitverwaltung.sperrzeitEintragen(TestZeitpunkt1, TestDauer1, Haeufigkeit.EINMAL, "", null, testArtist);
			fail("Zeit.sperrzeitEintragen() sollte eine IllegalArgumentException werfen, wenn das Argument kommentar"
					+ " null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "kommentar darf nicht null sein!");
		}

		try {
			zeitverwaltung.sperrzeitEintragen(TestZeitpunkt1, TestDauer1, Haeufigkeit.EINMAL, "", "", null);
			fail("Zeit.sperrzeitEintragen() sollte eine IllegalArgumentException werfen, wenn das Argument kommentar"
					+ " null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "artist darf nicht null sein!");
		}
	}

	@Test
	public void sperrzeitEintragenEinmaligExceptionsTests() {
		try {
			zeitverwaltung.sperrzeitEintragenEinmalig(null, TestDauer1, "", "", testArtist);
			fail("Zeit.sperrzeitEintragenEinmalig() sollte eine IllegalArgumentException werfen, wenn das Argument zeitpunkt"
					+ " null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "zeitpunkt darf nicht null sein!");
		}

		try {
			zeitverwaltung.sperrzeitEintragenEinmalig(TestZeitpunkt1, null, "", "", testArtist);
			fail("Zeit.sperrzeitEintragenEinmalig() sollte eine IllegalArgumentException werfen, wenn das Argument dauer"
					+ " null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "dauer darf nicht null sein!");
		}

		try {
			zeitverwaltung.sperrzeitEintragenEinmalig(TestZeitpunkt1, TestDauer1, null, "", testArtist);
			fail("Zeit.sperrzeitEintragenEinmalig() sollte eine IllegalArgumentException werfen, wenn das Argument name"
					+ " null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "name darf nicht null sein!");
		}

		try {
			zeitverwaltung.sperrzeitEintragenEinmalig(TestZeitpunkt1, TestDauer1, "", null, testArtist);
			fail("Zeit.sperrzeitEintragenEinmalig() sollte eine IllegalArgumentException werfen, wenn das Argument kommentar"
					+ " null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "kommentar darf nicht null sein!");
		}

		try {
			zeitverwaltung.sperrzeitEintragenEinmalig(TestZeitpunkt1, TestDauer1, "", "", null);
			fail("Zeit.sperrzeitEintragenEinmalig() sollte eine IllegalArgumentException werfen, wenn das Argument kommentar"
					+ " null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "artist darf nicht null sein!");
		}
	}

	@Test
	public void sperrzeitEintragenJaehrlichExceptionsTests() {
		try {
			zeitverwaltung.sperrzeitEintragenJaehrlich(null, TestDauer1, "", "", testArtist);
			fail("Zeit.sperrzeitEintragenJaehrlich() sollte eine IllegalArgumentException werfen, wenn das Argument zeitpunkt"
					+ " null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "zeitpunkt darf nicht null sein!");
		}

		try {
			zeitverwaltung.sperrzeitEintragenJaehrlich(TestZeitpunkt1, null, "", "", testArtist);
			fail("Zeit.sperrzeitEintragenJaehrlich() sollte eine IllegalArgumentException werfen, wenn das Argument dauer"
					+ " null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "dauer darf nicht null sein!");
		}

		try {
			zeitverwaltung.sperrzeitEintragenJaehrlich(TestZeitpunkt1, TestDauer1, null, "", testArtist);
			fail("Zeit.sperrzeitEintragenJaehrlich() sollte eine IllegalArgumentException werfen, wenn das Argument name"
					+ " null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "name darf nicht null sein!");
		}

		try {
			zeitverwaltung.sperrzeitEintragenJaehrlich(TestZeitpunkt1, TestDauer1, "", null, testArtist);
			fail("Zeit.sperrzeitEintragenJaehrlich() sollte eine IllegalArgumentException werfen, wenn das Argument kommentar"
					+ " null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "kommentar darf nicht null sein!");
		}

		try {
			zeitverwaltung.sperrzeitEintragenJaehrlich(TestZeitpunkt1, TestDauer1, "", "", null);
			fail("Zeit.sperrzeitEintragenJaehrlich() sollte eine IllegalArgumentException werfen, wenn das Argument kommentar"
					+ " null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "artist darf nicht null sein!");
		}
	}

	@Test
	public void sperrzeitEintragenMonatlichExceptionsTests() {
		try {
			zeitverwaltung.sperrzeitEintragenMonatlich(null, TestDauer1, "", "", testArtist);
			fail("Zeit.sperrzeitEintragenMonatlich() sollte eine IllegalArgumentException werfen, wenn das Argument zeitpunkt"
					+ " null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "zeitpunkt darf nicht null sein!");
		}

		try {
			zeitverwaltung.sperrzeitEintragenMonatlich(TestZeitpunkt1, null, "", "", testArtist);
			fail("Zeit.sperrzeitEintragenMonatlich() sollte eine IllegalArgumentException werfen, wenn das Argument dauer"
					+ " null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "dauer darf nicht null sein!");
		}

		try {
			zeitverwaltung.sperrzeitEintragenMonatlich(TestZeitpunkt1, TestDauer1, null, "", testArtist);
			fail("Zeit.sperrzeitEintragenMonatlich() sollte eine IllegalArgumentException werfen, wenn das Argument name"
					+ " null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "name darf nicht null sein!");
		}

		try {
			zeitverwaltung.sperrzeitEintragenMonatlich(TestZeitpunkt1, TestDauer1, "", null, testArtist);
			fail("Zeit.sperrzeitEintragenMonatlich() sollte eine IllegalArgumentException werfen, wenn das Argument kommentar"
					+ " null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "kommentar darf nicht null sein!");
		}

		try {
			zeitverwaltung.sperrzeitEintragenMonatlich(TestZeitpunkt1, TestDauer1, "", "", null);
			fail("Zeit.sperrzeitEintragenMonatlich() sollte eine IllegalArgumentException werfen, wenn das Argument kommentar"
					+ " null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "artist darf nicht null sein!");
		}
	}

	@Test
	public void sperrzeitEintragenWoechentlichExceptionsTests() {
		try {
			zeitverwaltung.sperrzeitEintragenWoechentlich(null, TestDauer1, "", "", testArtist);
			fail("Zeit.sperrzeitEintragenWoechentlich() sollte eine IllegalArgumentException werfen, wenn das Argument zeitpunkt"
					+ " null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "zeitpunkt darf nicht null sein!");
		}

		try {
			zeitverwaltung.sperrzeitEintragenWoechentlich(TestZeitpunkt1, null, "", "", testArtist);
			fail("Zeit.sperrzeitEintragenWoechentlich() sollte eine IllegalArgumentException werfen, wenn das Argument dauer"
					+ " null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "dauer darf nicht null sein!");
		}

		try {
			zeitverwaltung.sperrzeitEintragenWoechentlich(TestZeitpunkt1, TestDauer1, null, "", testArtist);
			fail("Zeit.sperrzeitEintragenWoechentlich() sollte eine IllegalArgumentException werfen, wenn das Argument name"
					+ " null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "name darf nicht null sein!");
		}

		try {
			zeitverwaltung.sperrzeitEintragenWoechentlich(TestZeitpunkt1, TestDauer1, "", null, testArtist);
			fail("Zeit.sperrzeitEintragenWoechentlich() sollte eine IllegalArgumentException werfen, wenn das Argument kommentar"
					+ " null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "kommentar darf nicht null sein!");
		}

		try {
			zeitverwaltung.sperrzeitEintragenWoechentlich(TestZeitpunkt1, TestDauer1, "", "", null);
			fail("Zeit.sperrzeitEintragenWoechentlich() sollte eine IllegalArgumentException werfen, wenn das Argument kommentar"
					+ " null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "artist darf nicht null sein!");
		}
	}

	@Test
	public void sperrzeitEintragenTaeglichExceptionsTests() {
		try {
			zeitverwaltung.sperrzeitEintragenTaeglich(null, TestDauer1, "", "", testArtist);
			fail("Zeit.sperrzeitEintragenTaeglich() sollte eine IllegalArgumentException werfen, wenn das Argument zeitpunkt"
					+ " null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "zeitpunkt darf nicht null sein!");
		}

		try {
			zeitverwaltung.sperrzeitEintragenTaeglich(TestZeitpunkt1, null, "", "", testArtist);
			fail("Zeit.sperrzeitEintragenTaeglich() sollte eine IllegalArgumentException werfen, wenn das Argument dauer"
					+ " null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "dauer darf nicht null sein!");
		}

		try {
			zeitverwaltung.sperrzeitEintragenTaeglich(TestZeitpunkt1, TestDauer1, null, "", testArtist);
			fail("Zeit.sperrzeitEintragenTaeglich() sollte eine IllegalArgumentException werfen, wenn das Argument name"
					+ " null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "name darf nicht null sein!");
		}

		try {
			zeitverwaltung.sperrzeitEintragenTaeglich(TestZeitpunkt1, TestDauer1, "", null, testArtist);
			fail("Zeit.sperrzeitEintragenTaeglich() sollte eine IllegalArgumentException werfen, wenn das Argument kommentar"
					+ " null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "kommentar darf nicht null sein!");
		}

		try {
			zeitverwaltung.sperrzeitEintragenTaeglich(TestZeitpunkt1, TestDauer1, "", "", null);
			fail("Zeit.sperrzeitEintragenTaeglich() sollte eine IllegalArgumentException werfen, wenn das Argument kommentar"
					+ " null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "artist darf nicht null sein!");
		}
	}

	@Test
	public void sperrzeitEintragenEinmaligTest() {
		zeitRepo.deleteAll();
		zeitverwaltung.sperrzeitEintragenEinmalig(TestZeitpunkt1, TestDauer1, "", "", testArtist);
		int i = 0;
		for (ArtistSperrZeit z : zeitRepo.findAll()) {
			i++;
			assertEquals("Zeitverwaltung.sperrzeitEintragenEinmalig() sollte die korrekten Werte abspeichern",
					TestZeitpunkt1, z.getStart());
			assertEquals("Zeitverwaltung.sperrzeitEintragenEinmalig() sollte die korrekten Werte abspeichern",
					TestDauer1, z.getDauer());
			assertEquals("Zeitverwaltung.sperrzeitEintragenEinmalig() sollte die korrekten Werte abspeichern", "",
					z.getName());
			assertEquals("Zeitverwaltung.sperrzeitEintragenEinmalig() sollte die korrekten Werte abspeichern", "",
					z.getKommentar());
			assertEquals("Zeitverwaltung.sperrzeitEintragenEinmalig() sollte die korrekten Werte abspeichern",
					testArtist, z.getArtist());
			assertEquals("Zeitverwaltung.sperrzeitEintragenEinmalig() sollte die korrekten Werte abspeichern",
					Haeufigkeit.EINMAL, z.getHaeufigkeit());
		}
		assertEquals("Zeitverwaltung.sperrzeitEintragenEinmalig() sollte nur eine Sperrzeit anlegen", 1, i);
	}

	@Test
	public void sperrzeitEintragenJaehrlichTest() {
		zeitRepo.deleteAll();
		zeitverwaltung.sperrzeitEintragenJaehrlich(TestZeitpunkt1, TestDauer1, "", "", testArtist);
		int i = 0;
		for (ArtistSperrZeit z : zeitRepo.findAll()) {
			i++;
			assertEquals("Zeitverwaltung.sperrzeitEintragenJaehrlich() sollte die korrekten Werte abspeichern",
					TestZeitpunkt1, z.getStart());
			assertEquals("Zeitverwaltung.sperrzeitEintragenJaehrlich() sollte die korrekten Werte abspeichern",
					TestDauer1, z.getDauer());
			assertEquals("Zeitverwaltung.sperrzeitEintragenJaehrlich() sollte die korrekten Werte abspeichern", "",
					z.getName());
			assertEquals("Zeitverwaltung.sperrzeitEintragenJaehrlich() sollte die korrekten Werte abspeichern", "",
					z.getKommentar());
			assertEquals("Zeitverwaltung.sperrzeitEintragenJaehrlich() sollte die korrekten Werte abspeichern",
					testArtist, z.getArtist());
			assertEquals("Zeitverwaltung.sperrzeitEintragenJaehrlich() sollte die korrekten Werte abspeichern",
					Haeufigkeit.JAEHRLICH, z.getHaeufigkeit());
		}
		assertEquals("Zeitverwaltung.sperrzeitEintragenJaehrlich() sollte nur eine Sperrzeit anlegen", 1, i);
	}

	@Test
	public void sperrzeitEintragenMonatlichTest() {
		zeitRepo.deleteAll();
		zeitverwaltung.sperrzeitEintragenMonatlich(TestZeitpunkt1, TestDauer1, "", "", testArtist);
		int i = 0;
		for (ArtistSperrZeit z : zeitRepo.findAll()) {
			i++;
			assertEquals("Zeitverwaltung.sperrzeitEintragenMonatlich() sollte die korrekten Werte abspeichern",
					TestZeitpunkt1, z.getStart());
			assertEquals("Zeitverwaltung.sperrzeitEintragenMonatlich() sollte die korrekten Werte abspeichern",
					TestDauer1, z.getDauer());
			assertEquals("Zeitverwaltung.sperrzeitEintragenMonatlich() sollte die korrekten Werte abspeichern", "",
					z.getName());
			assertEquals("Zeitverwaltung.sperrzeitEintragenMonatlich() sollte die korrekten Werte abspeichern", "",
					z.getKommentar());
			assertEquals("Zeitverwaltung.sperrzeitEintragenMonatlich() sollte die korrekten Werte abspeichern",
					testArtist, z.getArtist());
			assertEquals("Zeitverwaltung.sperrzeitEintragenMonatlich() sollte die korrekten Werte abspeichern",
					Haeufigkeit.MONATLICH, z.getHaeufigkeit());
		}
		assertEquals("Zeitverwaltung.sperrzeitEintragenMonatlich() sollte nur eine Sperrzeit anlegen", 1, i);
	}

	@Test
	public void sperrzeitEintragenWoechentlichTest() {
		zeitRepo.deleteAll();
		zeitverwaltung.sperrzeitEintragenWoechentlich(TestZeitpunkt1, TestDauer1, "", "", testArtist);
		int i = 0;
		for (ArtistSperrZeit z : zeitRepo.findAll()) {
			i++;
			assertEquals("Zeitverwaltung.sperrzeitEintragenWoechentlich() sollte die korrekten Werte abspeichern",
					TestZeitpunkt1, z.getStart());
			assertEquals("Zeitverwaltung.sperrzeitEintragenWoechentlich() sollte die korrekten Werte abspeichern",
					TestDauer1, z.getDauer());
			assertEquals("Zeitverwaltung.sperrzeitEintragenWoechentlich() sollte die korrekten Werte abspeichern", "",
					z.getName());
			assertEquals("Zeitverwaltung.sperrzeitEintragenWoechentlich() sollte die korrekten Werte abspeichern", "",
					z.getKommentar());
			assertEquals("Zeitverwaltung.sperrzeitEintragenWoechentlich() sollte die korrekten Werte abspeichern",
					testArtist, z.getArtist());
			assertEquals("Zeitverwaltung.sperrzeitEintragenWoechentlich() sollte die korrekten Werte abspeichern",
					Haeufigkeit.WOECHENTLICH, z.getHaeufigkeit());
		}
		assertEquals("Zeitverwaltung.sperrzeitEintragenWoechentlich() sollte nur eine Sperrzeit anlegen", 1, i);
	}

	@Test
	public void sperrzeitEintragenTaeglichTest() {
		zeitRepo.deleteAll();
		zeitverwaltung.sperrzeitEintragenTaeglich(TestZeitpunkt1, TestDauer1, "", "", testArtist);
		int i = 0;
		for (ArtistSperrZeit z : zeitRepo.findAll()) {
			i++;
			assertEquals("Zeitverwaltung.sperrzeitEintragenTaeglich() sollte die korrekten Werte abspeichern",
					TestZeitpunkt1, z.getStart());
			assertEquals("Zeitverwaltung.sperrzeitEintragenTaeglich() sollte die korrekten Werte abspeichern",
					TestDauer1, z.getDauer());
			assertEquals("Zeitverwaltung.sperrzeitEintragenTaeglich() sollte die korrekten Werte abspeichern", "",
					z.getName());
			assertEquals("Zeitverwaltung.sperrzeitEintragenTaeglich() sollte die korrekten Werte abspeichern", "",
					z.getKommentar());
			assertEquals("Zeitverwaltung.sperrzeitEintragenTaeglich() sollte die korrekten Werte abspeichern",
					testArtist, z.getArtist());
			assertEquals("Zeitverwaltung.sperrzeitEintragenTaeglich() sollte die korrekten Werte abspeichern",
					Haeufigkeit.TAEGLICH, z.getHaeufigkeit());
		}
		assertEquals("Zeitverwaltung.sperrzeitEintragenTaeglich() sollte nur eine Sperrzeit anlegen", 1, i);
	}

	@Test
	public void sperrzeitEintragenTest() {
		zeitRepo.deleteAll();
		zeitverwaltung.sperrzeitEintragen(TestZeitpunkt1, TestDauer1, Haeufigkeit.EINMAL, "", "", testArtist);
		int i = 0;
		for (ArtistSperrZeit z : zeitRepo.findAll()) {
			i++;
			assertEquals("Zeitverwaltung.sperrzeitEintragenTaeglich() sollte die korrekten Werte abspeichern",
					TestZeitpunkt1, z.getStart());
			assertEquals("Zeitverwaltung.sperrzeitEintragenTaeglich() sollte die korrekten Werte abspeichern",
					TestDauer1, z.getDauer());
			assertEquals("Zeitverwaltung.sperrzeitEintragenTaeglich() sollte die korrekten Werte abspeichern", "",
					z.getName());
			assertEquals("Zeitverwaltung.sperrzeitEintragenTaeglich() sollte die korrekten Werte abspeichern", "",
					z.getKommentar());
			assertEquals("Zeitverwaltung.sperrzeitEintragenTaeglich() sollte die korrekten Werte abspeichern",
					testArtist, z.getArtist());
			assertEquals("Zeitverwaltung.sperrzeitEintragenTaeglich() sollte die korrekten Werte abspeichern",
					Haeufigkeit.EINMAL, z.getHaeufigkeit());
		}
		assertEquals("Zeitverwaltung.sperrzeitEintragenTaeglich() sollte nur eine Sperrzeit anlegen", 1, i);

		zeitRepo.deleteAll();
		zeitverwaltung.sperrzeitEintragen(TestZeitpunkt1, TestDauer1, Haeufigkeit.JAEHRLICH, "", "", testArtist);
		i = 0;
		for (ArtistSperrZeit z : zeitRepo.findAll()) {
			i++;
			assertEquals("Zeitverwaltung.sperrzeitEintragenTaeglich() sollte die korrekten Werte abspeichern",
					TestZeitpunkt1, z.getStart());
			assertEquals("Zeitverwaltung.sperrzeitEintragenTaeglich() sollte die korrekten Werte abspeichern",
					TestDauer1, z.getDauer());
			assertEquals("Zeitverwaltung.sperrzeitEintragenTaeglich() sollte die korrekten Werte abspeichern", "",
					z.getName());
			assertEquals("Zeitverwaltung.sperrzeitEintragenTaeglich() sollte die korrekten Werte abspeichern", "",
					z.getKommentar());
			assertEquals("Zeitverwaltung.sperrzeitEintragenTaeglich() sollte die korrekten Werte abspeichern",
					testArtist, z.getArtist());
			assertEquals("Zeitverwaltung.sperrzeitEintragenTaeglich() sollte die korrekten Werte abspeichern",
					Haeufigkeit.JAEHRLICH, z.getHaeufigkeit());
		}
		assertEquals("Zeitverwaltung.sperrzeitEintragenTaeglich() sollte nur eine Sperrzeit anlegen", 1, i);

		zeitRepo.deleteAll();
		zeitverwaltung.sperrzeitEintragen(TestZeitpunkt1, TestDauer1, Haeufigkeit.MONATLICH, "", "", testArtist);
		i = 0;
		for (ArtistSperrZeit z : zeitRepo.findAll()) {
			i++;
			assertEquals("Zeitverwaltung.sperrzeitEintragenTaeglich() sollte die korrekten Werte abspeichern",
					TestZeitpunkt1, z.getStart());
			assertEquals("Zeitverwaltung.sperrzeitEintragenTaeglich() sollte die korrekten Werte abspeichern",
					TestDauer1, z.getDauer());
			assertEquals("Zeitverwaltung.sperrzeitEintragenTaeglich() sollte die korrekten Werte abspeichern", "",
					z.getName());
			assertEquals("Zeitverwaltung.sperrzeitEintragenTaeglich() sollte die korrekten Werte abspeichern", "",
					z.getKommentar());
			assertEquals("Zeitverwaltung.sperrzeitEintragenTaeglich() sollte die korrekten Werte abspeichern",
					testArtist, z.getArtist());
			assertEquals("Zeitverwaltung.sperrzeitEintragenTaeglich() sollte die korrekten Werte abspeichern",
					Haeufigkeit.MONATLICH, z.getHaeufigkeit());
		}
		assertEquals("Zeitverwaltung.sperrzeitEintragenTaeglich() sollte nur eine Sperrzeit anlegen", 1, i);

		zeitRepo.deleteAll();
		zeitverwaltung.sperrzeitEintragen(TestZeitpunkt1, TestDauer1, Haeufigkeit.WOECHENTLICH, "", "", testArtist);
		i = 0;
		for (ArtistSperrZeit z : zeitRepo.findAll()) {
			i++;
			assertEquals("Zeitverwaltung.sperrzeitEintragenTaeglich() sollte die korrekten Werte abspeichern",
					TestZeitpunkt1, z.getStart());
			assertEquals("Zeitverwaltung.sperrzeitEintragenTaeglich() sollte die korrekten Werte abspeichern",
					TestDauer1, z.getDauer());
			assertEquals("Zeitverwaltung.sperrzeitEintragenTaeglich() sollte die korrekten Werte abspeichern", "",
					z.getName());
			assertEquals("Zeitverwaltung.sperrzeitEintragenTaeglich() sollte die korrekten Werte abspeichern", "",
					z.getKommentar());
			assertEquals("Zeitverwaltung.sperrzeitEintragenTaeglich() sollte die korrekten Werte abspeichern",
					testArtist, z.getArtist());
			assertEquals("Zeitverwaltung.sperrzeitEintragenTaeglich() sollte die korrekten Werte abspeichern",
					Haeufigkeit.WOECHENTLICH, z.getHaeufigkeit());
		}
		assertEquals("Zeitverwaltung.sperrzeitEintragenTaeglich() sollte nur eine Sperrzeit anlegen", 1, i);

		zeitRepo.deleteAll();
		zeitverwaltung.sperrzeitEintragen(TestZeitpunkt1, TestDauer1, Haeufigkeit.TAEGLICH, "", "", testArtist);
		i = 0;
		for (ArtistSperrZeit z : zeitRepo.findAll()) {
			i++;
			assertEquals("Zeitverwaltung.sperrzeitEintragenTaeglich() sollte die korrekten Werte abspeichern",
					TestZeitpunkt1, z.getStart());
			assertEquals("Zeitverwaltung.sperrzeitEintragenTaeglich() sollte die korrekten Werte abspeichern",
					TestDauer1, z.getDauer());
			assertEquals("Zeitverwaltung.sperrzeitEintragenTaeglich() sollte die korrekten Werte abspeichern", "",
					z.getName());
			assertEquals("Zeitverwaltung.sperrzeitEintragenTaeglich() sollte die korrekten Werte abspeichern", "",
					z.getKommentar());
			assertEquals("Zeitverwaltung.sperrzeitEintragenTaeglich() sollte die korrekten Werte abspeichern",
					testArtist, z.getArtist());
			assertEquals("Zeitverwaltung.sperrzeitEintragenTaeglich() sollte die korrekten Werte abspeichern",
					Haeufigkeit.TAEGLICH, z.getHaeufigkeit());
		}
		assertEquals("Zeitverwaltung.sperrzeitEintragenTaeglich() sollte nur eine Sperrzeit anlegen", 1, i);

	}

	@Test
	public void ueberschneidungsTests() {
		// TestZeit1 ist Einmalig am 1.1.17, 12:00 eine Stunde
		// TestZeit2 ist wöchentlich am 1.1.16, 10:30 5 Stunden (Freitags)
		// TestZeit3 ist monatlich am 1.4.17, 11:45 10 Stunden
		// Überschneidet TestZeit1
		assertTrue(zeitverwaltung.ueberschneidetSperrzeit(LocalDateTime.of(2017, 1, 1, 12, 30), Duration.ofMinutes(45),
				testArtist));
		assertFalse(zeitverwaltung.ueberschneidetSperrzeit(LocalDateTime.of(2017, 11, 4, 1, 0), Duration.ofHours(10),
				testArtist));
		// Überschneidet TestZeit2
		assertTrue(zeitverwaltung.ueberschneidetSperrzeit(LocalDateTime.of(2017, 4, 7, 10, 0), Duration.ofHours(3),
				testArtist));
		// Überschneidet TestZeit3
		assertTrue(zeitverwaltung.ueberschneidetSperrzeit(LocalDateTime.of(2018, 12, 1, 9, 20), Duration.ofHours(3),
				testArtist));
	}
}
