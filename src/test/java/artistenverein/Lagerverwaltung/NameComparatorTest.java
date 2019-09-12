package artistenverein.Lagerverwaltung;

import static org.junit.Assert.assertTrue;
import static org.salespointframework.core.Currencies.EURO;

import org.javamoney.moneta.Money;
import org.junit.BeforeClass;
import org.junit.Test;

import artistenverein.AbstractIntegrationTests;

public class NameComparatorTest extends AbstractIntegrationTests {
	private static Artikel testArtikel1;
	private static Artikel testArtikel2;
	private static NameComparator comp;
	private final static Number preis1 = 9.99;
	private final static Number preis2 = 14.99;

	@BeforeClass
	public static void setUp() {
		testArtikel1 = new Artikel("Fackel", "fac", Money.of(preis1, EURO), "Einfache Fackel zum Jonglieren.");
		testArtikel2 = new Artikel("Pois", "poi", Money.of(preis2, EURO), "Feuer-Poi");
		comp = new NameComparator();
	}

	@Test
	public void compareTest() {
		assertTrue("NameComparator.compare() sollte 0 zurückgeben, wenn die Namen der beiden Artikel gleich sind!",
				comp.compare(testArtikel1, testArtikel1) == 0);
		assertTrue(
				"NameComparator.compare() sollte einen negativen int zurückgeben, wenn der Name des ersten Artikels"
						+ " alphabetisch \"kleiner\" ist als der Name des zweiten Artikels!",
				comp.compare(testArtikel1, testArtikel2) <= -1);
		assertTrue(
				"NameComparator.compare() sollte einen positiven int zurückgeben, wenn der Name des ersten Artikels"
						+ " alphabetisch \"größer\" ist als der Name des zweiten Artikels!",
				comp.compare(testArtikel2, testArtikel1) >= 1);
	}
}
