package artistenverein.Veranstaltungen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.salespointframework.core.Currencies.EURO;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.javamoney.moneta.Money;
import org.junit.Before;
import org.junit.Test;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;

import artistenverein.AbstractIntegrationTests;
import artistenverein.Lagerverwaltung.Artikel;
import artistenverein.Veranstaltungen.Bewertung;
import artistenverein.Veranstaltungen.EntityVeranstaltung;
import artistenverein.Veranstaltungen.Kommentar;
import artistenverein.Veranstaltungen.EntityVeranstaltung.VeranstaltungsType;

public class EntityVeranstaltungTest extends AbstractIntegrationTests {

	@Autowired
	private UserAccountManager accounts;
	private static EntityVeranstaltung testWorkshop;
	private static EntityVeranstaltung testShow;
	private final static Number preis1 = 99.99;
	private final static Number preis2 = 14.99;
	private final static LocalDateTime start1 = LocalDateTime.of(2017, 12, 1, 19, 30);
	private final static LocalDateTime end1 = LocalDateTime.of(2017, 12, 31, 23, 59);
	private final static LocalDateTime start2 = LocalDateTime.of(2018, 5, 3, 19, 30);
	private final static LocalDateTime end2 = LocalDateTime.of(2018, 6, 24, 23, 59);
	
	private final static UserAccount userAccount1 = new UserAccount();
	private final static UserAccount userAccount2 = new UserAccount();
	private static Set<UserAccount> artisten1 = new HashSet<>();
	private static Set<UserAccount> artisten2 = new HashSet<>();
	private UserAccount hans;
	private UserAccount genji;
	private UserAccount lucio;
	
	private static Artikel artikelFac;
	private static Artikel artikelPoi;
	
	private final static Number preis3 = 9.99;
	private final static Number preis4 = 14.99;
	
	@Before
	public void setUp() {
		genji = accounts.findByUsername("genji").get();
		lucio = accounts.findByUsername("lucio").get();
		testWorkshop = new EntityVeranstaltung("Feuerspucken", Money.of(preis1, EURO),
				"Lerne Feuerspucken mit unseren erfahrenen Feuerkünstlern.", 200, VeranstaltungsType.WORKSHOP,
				start1, end1, artisten1, 1, "");
		testShow = new EntityVeranstaltung("Jonglieren mit Bällen", Money.of(preis2, EURO),
				"Lustige Clowns jonglieren auf Einrädern mit Bällen", 300, VeranstaltungsType.SHOW,
				start2, end2, artisten2, 1, "Tanzbären");
		hans = accounts.findByUsername("hans").get();
		
		artikelFac = new Artikel("Fackel_test", "noimage", Money.of(preis3, EURO), "Einfache Fackel.");
		artikelPoi = new Artikel("Pois_test", "noimage", Money.of(preis4, EURO), "Feuer-Poi");
	}
	
	@Test
	public void veranstaltungCompareMittelwertTest() {
		
		testWorkshop.addBewertung(new Bewertung("bewertung1", 4, start1, userAccount1));
		testWorkshop.addBewertung(new Bewertung("bewertung2", 2, start2, userAccount2));
		testShow.addBewertung(new Bewertung("bewertung1", 5, start1, userAccount1));

		assertTrue("EntityVeranstaltung sollte sich entsprechend der Spezifikation des Comparators verhalten",
				EntityVeranstaltung.compareMittelwert().compare(testWorkshop, testShow) >= -1 );
		assertTrue("EntityVeranstaltung sollte sich entsprechend der Spezifikation des Comparators verhalten",
				EntityVeranstaltung.compareMittelwert().compare(testShow, testWorkshop) <= 1 );
		assertTrue("EntityVeranstaltung sollte sich entsprechend der Spezifikation des Comparators verhalten",
				EntityVeranstaltung.compareMittelwert().compare(testWorkshop, testWorkshop) == 0 );
	}
	
	@Test
	public void veranstaltungSetBewertungTest() {
	
		testWorkshop.addBewertung(new Bewertung("bewertung1", 4, start1, userAccount1));
		testWorkshop.addBewertung(new Bewertung("bewertung2", 2, start2, userAccount2));
		
		assertTrue("EntityVeranstaltung sollte den entsprechenden Mittelwert ausgeben.",
				testWorkshop.getMittelwert() == 3 );
		assertTrue("EntityVeranstaltung sollte bei nicht vorhandenen Bewertungen 0 angeben.",
				testShow.getMittelwert() == 0 );
	}
	
	@Test
	public void addBewertungTest() {
		try {
			testShow.addBewertung(null);
			fail("Veranstaltung.addBewertung() sollte eine IllegalArgumentException werfen, wenn das Argument bewertung null ist."
					+ "null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "bewertung darf nicht null sein!");
		}
		LocalDateTime now = LocalDateTime.now();
		testShow.addBewertung(new Bewertung("test", 5, now, hans));
		for (Bewertung b : testShow.getBewertungen()) {
			assertEquals(hans, b.getAutor());
			assertEquals(5, b.getBewertung());
			assertEquals(now, b.getDatum());
			assertEquals("test", b.getText());
		}
	}
	
	@Test
	public void addKommentarTest() {
		try {
			testShow.addBewertung(null);
			fail("Veranstaltung.addBewertung() sollte eine IllegalArgumentException werfen, wenn das Argument bewertung null ist."
					+ "null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "bewertung darf nicht null sein!");
		}
		LocalDateTime now = LocalDateTime.now();
		testShow.addKommentar(new Kommentar("test", now));
		for (Kommentar b : testShow.getKommentare()) {
			assertEquals(now, b.getDatum());
			assertEquals("test", b.getText());
		}
	}
	
	@Test
	public void veranstaltungErstellNullPointerTest() {
		
		try {
			EntityVeranstaltung a = new EntityVeranstaltung("Veranstaltung",  Money.of(preis1, EURO), null ,
					200, VeranstaltungsType.SHOW, start1, end1, artisten1, 1, "Tanzbären");
			a.getId();
			fail("Artikel.Artikel() sollte eine IllegalArgumentException werfen, wenn das Argument bild null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "beschreibung darf nicht null sein!");
		}
		
		try {
			EntityVeranstaltung a = new EntityVeranstaltung("Veranstaltung",  Money.of(preis1, EURO), "Beschreibung" ,
					200, null, start1, end1, artisten1, 1, "");
			a.getId();
			fail("Artikel.Artikel() sollte eine IllegalArgumentException werfen, wenn das Argument VeranstaltungsType"
					+ "null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "type darf nicht null sein!");
		}
		
		try {
			EntityVeranstaltung a = new EntityVeranstaltung("Veranstaltung",  Money.of(preis1, EURO), "Beschreibung" ,
					200, VeranstaltungsType.SHOW, null , end1, artisten1, 1, "Tanzbären");
			a.getId();
			fail("Artikel.Artikel() sollte eine IllegalArgumentException werfen, wenn das Argument startDatum"
					+ "null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "startDatum darf nicht null sein!");
		}
		
		try {
			EntityVeranstaltung a = new EntityVeranstaltung("Veranstaltung",  Money.of(preis1, EURO), "Beschreibung" ,
					200, VeranstaltungsType.SHOW, start1, null, artisten1, 1, "Tanzbären");
			a.getId();
			fail("Artikel.Artikel() sollte eine IllegalArgumentException werfen, wenn das Argument endDatum"
					+ "null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "endDatum darf nicht null sein!");
		}
		
		try {
			EntityVeranstaltung a = new EntityVeranstaltung("Veranstaltung",  Money.of(preis1, EURO), "Beschreibung" ,
					200, VeranstaltungsType.SHOW, start1, end1, null, 1, "Tanzbären");
			a.getId();
			fail("Artikel.Artikel() sollte eine IllegalArgumentException werfen, wenn das Argument artisten"
					+ "null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "artisten darf nicht null sein!");
		}
	}
	
	@Test
	public void setArtistTest() {
		try {
			testShow.setArtist(null);
			fail("Veranstaltung.setArtist sollte eine IllegalArgumentException werfen, wenn das Argument artist null ist."
					+ "null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "artist darf nicht null sein!");
		}
		
		Set<UserAccount> artists = new HashSet<UserAccount>();
		artists.add(genji);
		artists.add(lucio);
		testShow.setArtist(artists);
		assertEquals("Veranstaltung.setArtist() sollte die Artisten korrekt setzen", artists, testShow.getArtisten());
	}
	
	@Test
	public void setArtikellistTest() {
		try {
			testShow.setArtikellist(null);
			fail("Veranstaltung.setArtikellist sollte eine IllegalArgumentException werfen, wenn das Argument artikellist null ist."
					+ "null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "artikellist darf nicht null sein!");
		}
		
		Set<Artikel> artikel = new HashSet<Artikel>();
		artikel.add(artikelFac);
		artikel.add(artikelPoi);
		testShow.setArtikellist(artikel);
		assertEquals("Veranstaltung.setArtikellist() sollte die Artikelliste korrekt setzen", artikel, testShow.getArtikellist());
	}
	
	@Test
	public void addArtikelTest() {
		try {
			testShow.addArtikel(null);
			fail("Veranstaltung.addArtikel() sollte eine IllegalArgumentException werfen, wenn das Argument artikel null ist."
					+ "null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "artikel darf nicht null sein!");
		}
		
		Set<Artikel> artikel = new HashSet<Artikel>();
		artikel.add(artikelFac);
		testShow.addArtikel(artikelFac);
		assertEquals("Veranstaltung.setArtikellist() sollte die Artikelliste korrekt setzen", artikel, testShow.getArtikellist());
	}

	@Test
	public void setStartDatumTest() {
		try {
			testShow.setStartDatum(null);
			fail("Veranstaltung.setStartDatum() sollte eine IllegalArgumentException werfen, wenn das Argument startDatum null ist."
					+ "null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "startDatum darf nicht null sein!");
		}

		LocalDateTime testDatum = testShow.getStartDatum().plusDays(1);
		testShow.setStartDatum(testDatum);
		assertEquals("EntityVeranstaltung.setEndDatum() sollte den Wert korrekt übernehmen", testDatum, testShow.getStartDatum());
	}
	
	@Test
	public void setEndDatumTest() {
		try {
			testShow.setEndDatum(null);
			fail("Veranstaltung.setEndDatum() sollte eine IllegalArgumentException werfen, wenn das Argument endDatum null ist."
					+ "null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "endDatum darf nicht null sein!");
		}
		
		LocalDateTime testDatum = testShow.getStartDatum().plusDays(40);
		testShow.setEndDatum(testDatum);
		assertEquals("EntityVeranstaltung.setEndDatum() sollte den Wert korrekt übernehmen", testDatum, testShow.getEndDatum());
	}
	
	@Test
	public void setBeschreibungTest() {
		try {
			testShow.setBeschreibung(null);
			fail("Veranstaltung.setBeschreibung() sollte eine IllegalArgumentException werfen, wenn das Argument beschreibung null ist."
					+ "null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "beschreibung darf nicht null sein!");
		}

		String beschreibung = "garantiert neuer String";
		testShow.setBeschreibung(beschreibung);
		assertEquals("EntityVeranstaltung.setEndDatum() sollte den Wert korrekt übernehmen", beschreibung, testShow.getBeschreibung());
	}
	
	@Test
	public void setDauerTest() {
		int dauer = 100;
		testShow.setDauer(dauer);
		assertEquals("EntityVeranstaltung.setDauer() sollte den Wert korrekt übernehmen", dauer, testShow.getDauer());
	}
	
	@Test
	public void addKommentarNullPointerTest() {
		try {
			testShow.addKommentar(null);
			fail("Veranstaltung.addKommentar() sollte eine IllegalArgumentException werfen, wenn das Argument kommentar null ist."
					+ "null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "kommentar darf nicht null sein!");
		}
	}
}
