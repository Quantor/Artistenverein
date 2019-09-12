package artistenverein.Lagerverwaltung;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.salespointframework.core.Currencies.EURO;

import org.javamoney.moneta.Money;
import org.junit.BeforeClass;
import org.junit.Test;

import artistenverein.AbstractIntegrationTests;

public class ArtikelTest extends AbstractIntegrationTests {

	private static Artikel testArtikel1;
	private static Artikel testArtikel2;
	private final static Number preis1 = 9.99;
	private final static Number preis2 = 14.99;

	@BeforeClass
	public static void setUp() {
		testArtikel1 = new Artikel("Fackel", "fac", Money.of(preis1, EURO),
				"Einfache Fackel, zum Jonglieren geeignet.");
		testArtikel2 = new Artikel("Pois", "poi", Money.of(preis2, EURO), "Feuer-Poi");
	}

	@Test
	public void ArtikelCompareToTest() {
		assertTrue("Artikel.compareTo() sollte sich entsprechend der Spezifikation des Interfaces Comparable verhalten",
				testArtikel1.compareTo(testArtikel2) <= -1);
		assertTrue("Artikel.compareTo() sollte sich entsprechend der Spezifikation des Interfaces Comparable verhalten",
				testArtikel2.compareTo(testArtikel1) >= 1);
		assertTrue("Artikel.compareTo() sollte sich entsprechend der Spezifikation des Interfaces Comparable verhalten",
				testArtikel1.compareTo(testArtikel1) == 0);
	}

	@Test
	public void ArtikelErstellNullPointerTest() {
		try {
			Artikel a = new Artikel("Fackel", null, Money.of(preis1, EURO), "Einfache Fackel, zum Jonglieren geeignet.");
			a.getId();
			fail("Artikel.Artikel() sollte eine IllegalArgumentException werfen, wenn das Argument bild null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "bild darf nicht null sein!");
		}

		try {
			Artikel a = new Artikel("Fackel", "fac", Money.of(preis1, EURO), null);
			a.getId();
			fail("Artikel.Artikel() sollte eine IllegalArgumentException werfen, wenn das Argument beschreibung null"
					+ " ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "beschreibung darf nicht null sein!");
		}
	}

	@Test
	public void ArtikelErstellEmptyStringTest() {
		try {
			Artikel a = new Artikel("Fackel", "", Money.of(preis1, EURO), "Einfache Fackel, zum Jonglieren geeignet.");
			a.getId();
			fail("Artikel.Artikel() sollte eine IllegalArgumentException werfen, wenn das Argument bild leer ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "bild darf nicht leer sein!");
		}

		try {
			Artikel a = new Artikel("Fackel", "fac", Money.of(preis1, EURO), "");
			a.getId();
			fail("Artikel.Artikel() sollte eine IllegalArgumentException werfen, wenn das Argument beschreibung leer"
					+ " ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "beschreibung darf nicht leer sein!");
		}
	}

	@Test
	public void ArtikelSetBildTest() {
		try {
			testArtikel1.setBild(null);
			fail("Artikel.setBild() sollte eine IllegalArgumentException werfen, wenn das Argument bild null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "bild darf nicht null sein!");
		}

		try {
			testArtikel1.setBild("");
			fail("Artikel.setBild() sollte eine IllegalArgumentException werfen, wenn das Argument bild leer ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "bild darf nicht leer sein!");
		}
	}

	@Test
	public void ArtikelSetBeschreibungTest() {
		try {
			testArtikel1.setBeschreibung(null);
			fail("Artikel.setBild() sollte eine IllegalArgumentException werfen, wenn das Argument beschreibung null"
					+ " ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "beschreibung darf nicht null sein!");
		}

		try {
			testArtikel1.setBeschreibung("");
			fail("Artikel.setBeschreibung() sollte eine IllegalArgumentException werfen, wenn das Argument beschreibung"
					+ " leer ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "beschreibung darf nicht leer sein!");
		}
	}
}
