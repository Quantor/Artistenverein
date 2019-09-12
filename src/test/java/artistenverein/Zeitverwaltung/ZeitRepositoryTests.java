package artistenverein.Zeitverwaltung;

import static artistenverein.Zeitverwaltung.Haeufigkeit.EINMAL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.Duration;
import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Test;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import artistenverein.AbstractIntegrationTests;

public class ZeitRepositoryTests extends AbstractIntegrationTests {

	@Autowired
	private ZeitRepository zeitRepo;
	@Autowired
	private UserAccountManager accounts;
	private ArtistSperrZeit testZeit1;
	private ArtistSperrZeit testZeit2;
	private ArtistSperrZeit testZeit3;
	private ArtistSperrZeit testZeit4;

	private LocalDateTime TestZeitpunkt1;
	private LocalDateTime TestZeitpunkt2;
	private LocalDateTime TestZeitpunkt3;
	private LocalDateTime TestZeitpunkt4;
	private LocalDateTime TestZeitpunkt5;
	private LocalDateTime TestZeitpunkt6;

	private Duration TestDauer1;
	private Duration TestDauer2;
	private Duration TestDauer3;
	private Duration TestDauer4;

	private UserAccount genji;
	private UserAccount lucio;
	private UserAccount mercy;

	@Before
	public void setUp() {
		genji = accounts.findByUsername("genji").get();
		lucio = accounts.findByUsername("lucio").get();
		mercy = accounts.findByUsername("mercy").get();

		TestZeitpunkt1 = LocalDateTime.of(2017, 1, 1, 12, 0);
		TestZeitpunkt2 = LocalDateTime.of(2016, 1, 1, 10, 30);
		TestZeitpunkt3 = LocalDateTime.of(2017, 4, 1, 11, 45);
		TestZeitpunkt4 = LocalDateTime.of(2017, 12, 24, 0, 0);
		TestZeitpunkt5 = LocalDateTime.of(2012, 7, 24, 0, 0);
		TestZeitpunkt6 = LocalDateTime.of(2020, 8, 24, 0, 0);

		TestDauer1 = Duration.ofHours(1);
		TestDauer2 = Duration.ofHours(5);
		TestDauer3 = Duration.ofHours(10);
		TestDauer4 = Duration.ofHours(24);

		testZeit1 = new ArtistSperrZeit(TestZeitpunkt1, TestDauer1, EINMAL, "Name1", "Kommentar1", genji);
		testZeit2 = new ArtistSperrZeit(TestZeitpunkt2, TestDauer2, Haeufigkeit.WOECHENTLICH, "Name2", "Kommentar2",
				genji);
		testZeit3 = new ArtistSperrZeit(TestZeitpunkt3, TestDauer3, Haeufigkeit.MONATLICH, "Name3", "Kommentar3",
				lucio);
		testZeit4 = new ArtistSperrZeit(TestZeitpunkt4, TestDauer4, Haeufigkeit.JAEHRLICH, "Name4", "Kommentar4",
				lucio);

		zeitRepo.deleteAll();
		zeitRepo.save(testZeit1);
		zeitRepo.save(testZeit2);
		zeitRepo.save(testZeit3);
		zeitRepo.save(testZeit4);
	}

	@Test
	public void ueberschneidetSperrzeitNullPointerTest() {
		try {
			zeitRepo.ueberschneidetSperrzeit(null, TestDauer1, genji);
			fail("ZeitRepository.ueberschneidetSperrzeit() sollte keine Nullpointer akzeptieren");
		} catch (InvalidDataAccessApiUsageException e) {
			assertEquals(
					"zeitpunkt darf nicht null sein!; nested exception is java.lang.IllegalArgumentException: zeitpunkt darf nicht null sein!",
					e.getMessage());
		}

		try {
			zeitRepo.ueberschneidetSperrzeit(TestZeitpunkt1, null, genji);
			fail("ZeitRepository.ueberschneidetSperrzeit() sollte keine Nullpointer akzeptieren");
		} catch (InvalidDataAccessApiUsageException e) {
			assertEquals(
					"dauer darf nicht null sein!; nested exception is java.lang.IllegalArgumentException: dauer darf nicht null sein!",
					e.getMessage());
		}

		try {
			zeitRepo.ueberschneidetSperrzeit(TestZeitpunkt1, TestDauer1, null);
			fail("ZeitRepository.ueberschneidetSperrzeit() sollte keine Nullpointer akzeptieren");
		} catch (InvalidDataAccessApiUsageException e) {
			assertEquals(
					"artist darf nicht null sein!; nested exception is java.lang.IllegalArgumentException: artist darf nicht null sein!",
					e.getMessage());
		}
	}

	@Test
	public void ueberschneidetSperrzeitTest() {
		assertFalse(
				"ZeitRepository.ueberschneidetSperrzeit() sollte korrekt zurückgeben, ob es eine Überschneidung gibt",
				zeitRepo.ueberschneidetSperrzeit(TestZeitpunkt1, TestDauer1, mercy));
		assertFalse(
				"ZeitRepository.ueberschneidetSperrzeit() sollte korrekt zurückgeben, ob es eine Überschneidung gibt",
				zeitRepo.ueberschneidetSperrzeit(TestZeitpunkt1.plusHours(2), TestDauer1, mercy));
		assertFalse(
				"ZeitRepository.ueberschneidetSperrzeit() sollte korrekt zurückgeben, ob es eine Überschneidung gibt",
				zeitRepo.ueberschneidetSperrzeit(TestZeitpunkt5, TestDauer1, mercy));
		assertFalse(
				"ZeitRepository.ueberschneidetSperrzeit() sollte korrekt zurückgeben, ob es eine Überschneidung gibt",
				zeitRepo.ueberschneidetSperrzeit(TestZeitpunkt6, TestDauer1, mercy));
		assertTrue(
				"ZeitRepository.ueberschneidetSperrzeit() sollte korrekt zurückgeben, ob es eine Überschneidung gibt",
				zeitRepo.ueberschneidetSperrzeit(TestZeitpunkt1, TestDauer1, genji));
		assertTrue(
				"ZeitRepository.ueberschneidetSperrzeit() sollte korrekt zurückgeben, ob es eine Überschneidung gibt",
				zeitRepo.ueberschneidetSperrzeit(TestZeitpunkt1.plusMinutes(30), TestDauer1, genji));
		assertFalse(
				"ZeitRepository.ueberschneidetSperrzeit() sollte korrekt zurückgeben, ob es eine Überschneidung gibt",
				zeitRepo.ueberschneidetSperrzeit(TestZeitpunkt1.plusMinutes(120), TestDauer1, genji));
		assertFalse(
				"ZeitRepository.ueberschneidetSperrzeit() sollte korrekt zurückgeben, ob es eine Überschneidung gibt",
				zeitRepo.ueberschneidetSperrzeit(TestZeitpunkt5, TestDauer1, genji));
		assertFalse(
				"ZeitRepository.ueberschneidetSperrzeit() sollte korrekt zurückgeben, ob es eine Überschneidung gibt",
				zeitRepo.ueberschneidetSperrzeit(TestZeitpunkt6, TestDauer1, genji));
	}

	@Test
	public void getSperrzeitenNullPointerTest() {
		try {
			zeitRepo.getSperrzeiten(null);
			fail("ZeitRepository.ueberschneidetSperrzeit() sollte keine Nullpointer akzeptieren");
		} catch (InvalidDataAccessApiUsageException e) {
			assertEquals(
					"artist darf nicht null sein!; nested exception is java.lang.IllegalArgumentException: artist darf nicht null sein!",
					e.getMessage());
		}
	}

	@Test
	public void getSperrzeitenTest() {
		Iterable<ArtistSperrZeit> zeiten = zeitRepo.getSperrzeiten(genji);
		int i = 0;
		for (ArtistSperrZeit z : zeiten) {
			i++;
			assertEquals("ZeitRepository.getSperrzeiten() sollte nur Sperrzeiten des Artisten zurückgeben", genji,
					z.getArtist());
		}
		assertEquals("ZeitRepository.getSperrzeiten() sollte alle Sperrzeiten des Artisten zurückgeben", 2, i);

		zeiten = zeitRepo.getSperrzeiten(lucio);
		i = 0;
		for (ArtistSperrZeit z : zeiten) {
			i++;
			assertEquals("ZeitRepository.getSperrzeiten() sollte nur Sperrzeiten des Artisten zurückgeben", lucio,
					z.getArtist());
		}
		assertEquals("ZeitRepository.getSperrzeiten() sollte alle Sperrzeiten des Artisten zurückgeben", 2, i);

		zeiten = zeitRepo.getSperrzeiten(mercy);
		i = 0;
		for (ArtistSperrZeit z : zeiten) {
			z.getClass();
			fail("ZeitRepository.getSperrzeiten() sollte nur Sperrzeiten des Artisten zurückgeben");
		}
	}
}
