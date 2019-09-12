package artistenverein.Personenverwaltung;

import static artistenverein.Zeitverwaltung.Haeufigkeit.EINMAL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ExtendedModelMap;

import artistenverein.AbstractIntegrationTests;
import artistenverein.Personenverwaltung.ControllerSperrzeiten;
import artistenverein.Personenverwaltung.ManagerUser;
import artistenverein.Zeitverwaltung.ArtistSperrZeit;
import artistenverein.Zeitverwaltung.Haeufigkeit;
import artistenverein.Zeitverwaltung.ZeitRepository;
import artistenverein.Zeitverwaltung.Zeitverwaltung;

public class ControllerSperrzeitenTests extends AbstractIntegrationTests {

	@Autowired
	private ManagerUser artistManagement;
	@Autowired
	private Zeitverwaltung zeitverwaltung;
	@Autowired
	private ZeitRepository zeitRepo;
	private ControllerSperrzeiten testController;
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

	private LocalTime TestTime1;
	private LocalTime TestTime2;
	private String TestTime1String;
	private String TestTime2String;

	private Duration TestDauer1;
	private Duration TestDauer2;
	private Duration TestDauer3;
	private Duration TestDauer4;

	private UserAccount genji;
	private UserAccount lucio;

	@Before
	public void setUp() {
		testController = new ControllerSperrzeiten(artistManagement, zeitverwaltung, zeitRepo);
		genji = accounts.findByUsername("genji").get();
		lucio = accounts.findByUsername("lucio").get();

		TestZeitpunkt1 = LocalDateTime.of(2017, 1, 1, 12, 0);
		TestZeitpunkt2 = LocalDateTime.of(2016, 1, 1, 10, 30);
		TestZeitpunkt3 = LocalDateTime.of(2017, 4, 1, 11, 45);
		TestZeitpunkt4 = LocalDateTime.of(2017, 12, 24, 0, 0);

		TestTime1 = TestZeitpunkt1.toLocalTime();
		TestTime2 = LocalTime.of(1, 00);
		TestTime1String = TestTime1.toString();
		TestTime2String = TestTime2.toString();


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
	public void ControllerSperrzeitenErstellTest() {
		try {
			ControllerSperrzeiten test = new ControllerSperrzeiten(null, zeitverwaltung, zeitRepo);
			test.toString();
			fail("ControllerSperrzeiten.ControllerSperrzeiten() sollte keine Nullpointer akzeptieren");
		} catch (IllegalArgumentException e) {
			assertEquals("artistManagement darf nicht null sein!", e.getMessage());
		}

		try {
			ControllerSperrzeiten test = new ControllerSperrzeiten(artistManagement, null, zeitRepo);
			test.toString();
			fail("ControllerSperrzeiten.ControllerSperrzeiten() sollte keine Nullpointer akzeptieren");
		} catch (IllegalArgumentException e) {
			assertEquals("zeitverwaltung darf nicht null sein!", e.getMessage());
		}

		try {
			ControllerSperrzeiten test = new ControllerSperrzeiten(artistManagement, zeitverwaltung, null);
			test.toString();
			fail("ControllerSperrzeiten.ControllerSperrzeiten() sollte keine Nullpointer akzeptieren");
		} catch (IllegalArgumentException e) {
			assertEquals("zeitRepository darf nicht null sein!", e.getMessage());
		}
	}

	@Test
	public void sperrzeitenTest() {
		ExtendedModelMap model = new ExtendedModelMap();
		String retString = testController.sperrzeiten(genji, model);
		assertEquals("Zeitverwaltung/sperrzeiten", retString);
		assertNotNull("ControllerArtikelKatalog.sperrzeiten() sollte den eingeloggten User zurückgeben",
				model.get("user"));
		assertEquals("ControllerArtikelKatalog.sperrzeiten() sollte den eingeloggten User zurückgeben",
				artistManagement.findeUserAccount(genji), model.get("user"));
		assertEquals("ControllerArtikelKatalog.sperrzeiten() sollte die Sperrzeiten des artisten zurückgeben",
				zeitRepo.getSperrzeiten(genji), model.get("zeiten"));
	}

	@Test
	public void sperrzeitloeschenTest() {
		ExtendedModelMap model = new ExtendedModelMap();
		String retString = testController.sperrzeitloeschen(genji, model, testZeit1.getId());
		assertEquals("Zeitverwaltung/sperrzeiten", retString);
		assertNotNull("ControllerArtikelKatalog.sperrzeitloeschen() sollte den eingeloggten User zurückgeben",
				model.get("user"));
		assertEquals("ControllerArtikelKatalog.sperrzeitloeschen() sollte den eingeloggten User zurückgeben",
				artistManagement.findeUserAccount(genji), model.get("user"));
		assertEquals("ControllerArtikelKatalog.sperrzeitloeschen() sollte die Sperrzeiten des artisten zurückgeben",
				zeitRepo.getSperrzeiten(genji), model.get("zeiten"));
		for (ArtistSperrZeit z : zeitRepo.findAll()) {
			Assert.assertNotEquals(
					"ControllerArtikelKatalog.sperrzeitloeschen() sollte die übergebene Sperrzeit tatsächlich loeschen",
					testZeit1.getId(), z.getId());
		}
	}

	@Test
	public void sperrzeiterstellenTest() {
		zeitRepo.deleteAll();
		ExtendedModelMap model = new ExtendedModelMap();
		String retString = testController.sperrzeiterstellen(genji, model, "", null, null, "gehtSchief", "", "");
		assertEquals("/error", retString);

		zeitRepo.deleteAll();
		model = new ExtendedModelMap();
		retString = testController.sperrzeiterstellen(genji, model, "2017-1-1", TestTime1String, TestTime2String,
				"einmalig", "", "");
		assertEquals("redirect:/sperrzeiten", retString);
		assertEquals("ControllerArtikelKatalog.sperrzeitloeschen() sollte die Sperrzeiten des artisten zurückgeben",
				zeitRepo.getSperrzeiten(genji), model.get("zeiten"));
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
			assertEquals("Zeitverwaltung.sperrzeitEintragenMonatlich() sollte die korrekten Werte abspeichern", genji,
					z.getArtist());
			assertEquals("Zeitverwaltung.sperrzeitEintragenMonatlich() sollte die korrekten Werte abspeichern",
					Haeufigkeit.EINMAL, z.getHaeufigkeit());
		}
		assertEquals("ControllerArtikelKatalog.sperrzeiterstellen() sollte nur eine Sperrzeit erstellen", 1, i);

		zeitRepo.deleteAll();
		model = new ExtendedModelMap();
		retString = testController.sperrzeiterstellen(genji, model, "2017-1-1", TestTime1String, TestTime2String,
				"einmalig", null, null);
		assertEquals("redirect:/sperrzeiten", retString);
		assertEquals("ControllerArtikelKatalog.sperrzeiterstellen() sollte die Sperrzeiten des artisten zurückgeben",
				zeitRepo.getSperrzeiten(genji), model.get("zeiten"));
		i = 0;
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
			assertEquals("Zeitverwaltung.sperrzeitEintragenMonatlich() sollte die korrekten Werte abspeichern", genji,
					z.getArtist());
			assertEquals("Zeitverwaltung.sperrzeitEintragenMonatlich() sollte die korrekten Werte abspeichern",
					Haeufigkeit.EINMAL, z.getHaeufigkeit());
		}
		assertEquals("ControllerArtikelKatalog.sperrzeiterstellen() sollte nur eine Sperrzeit erstellen", 1, i);

		zeitRepo.deleteAll();
		model = new ExtendedModelMap();
		retString = testController.sperrzeiterstellen(genji, model, "2017-1-1", TestTime1String, TestTime2String,
				"einmalig", "name", "kommentar");
		assertEquals("redirect:/sperrzeiten", retString);
		assertEquals("ControllerArtikelKatalog.sperrzeiterstellen() sollte die Sperrzeiten des artisten zurückgeben",
				zeitRepo.getSperrzeiten(genji), model.get("zeiten"));
		i = 0;
		for (ArtistSperrZeit z : zeitRepo.findAll()) {
			i++;
			assertEquals("Zeitverwaltung.sperrzeitEintragenMonatlich() sollte die korrekten Werte abspeichern",
					TestZeitpunkt1, z.getStart());
			assertEquals("Zeitverwaltung.sperrzeitEintragenMonatlich() sollte die korrekten Werte abspeichern",
					TestDauer1, z.getDauer());
			assertEquals("Zeitverwaltung.sperrzeitEintragenMonatlich() sollte die korrekten Werte abspeichern", "name",
					z.getName());
			assertEquals("Zeitverwaltung.sperrzeitEintragenMonatlich() sollte die korrekten Werte abspeichern",
					"kommentar", z.getKommentar());
			assertEquals("Zeitverwaltung.sperrzeitEintragenMonatlich() sollte die korrekten Werte abspeichern", genji,
					z.getArtist());
			assertEquals("Zeitverwaltung.sperrzeitEintragenMonatlich() sollte die korrekten Werte abspeichern",
					Haeufigkeit.EINMAL, z.getHaeufigkeit());
		}
		assertEquals("ControllerArtikelKatalog.sperrzeiterstellen() sollte nur eine Sperrzeit erstellen", 1, i);
	}
}
