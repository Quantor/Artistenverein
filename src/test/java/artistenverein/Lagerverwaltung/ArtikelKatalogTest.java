package artistenverein.Lagerverwaltung;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import artistenverein.AbstractIntegrationTests;

public class ArtikelKatalogTest extends AbstractIntegrationTests {

	@Autowired
	private ArtikelKatalog katalog;

	private static String search1;
	private static String search2;
	private static String search3;
	private static String search4;
	private static NameComparator nameComp;
	private static PriceComparator priceComp;

	@BeforeClass
	public static void setUp() {
		search1 = "a";
		search2 = "b";
		search3 = "Fackel";
		search4 = "diesenstringgibtesbestimmmtnicht";
		nameComp = new NameComparator();
		priceComp = new PriceComparator();
	}

	@Test
	public void ArtikelKatalogFindByPatternTest() {
		assertEquals("Searching for Empty String should return all Elements", katalog.findAll(),
				katalog.findAndSort("", ""));

		assertEquals("Searching for Empty String should return all Elements", katalog.findAll(),
				katalog.findAndSort(null, ""));

		assertEquals("Searching for Empty String should return all Elements", katalog.findAll(),
				katalog.findAndSort("", null));

		assertEquals("Searching for Empty String should return all Elements", katalog.findAll(),
				katalog.findAndSort(null, null));

		Iterable<Artikel> artikel = katalog.findAndSort(search1, "");
		for (Artikel a : artikel) {
			if (a.getName().toLowerCase().contains(search1.toLowerCase()) == false) {
				fail("ArtikelKatalog.findByPattern() sollte nur die Artikel zurückgeben, deren Name den gesuchten"
						+ " String enthält!");
			}
		}

		artikel = katalog.findAndSort(search2, "");
		for (Artikel a : artikel) {
			if (a.getName().toLowerCase().contains(search2.toLowerCase()) == false) {
				fail("ArtikelKatalog.findByPattern() sollte nur die Artikel zurückgeben, deren Name den gesuchten"
						+ " String enthält!");
			}
		}

		artikel = katalog.findAndSort(search3, "");
		for (Artikel a : artikel) {
			if (a.getName().toLowerCase().contains(search3.toLowerCase()) == false) {
				fail("ArtikelKatalog.findByPattern() sollte nur die Artikel zurückgeben, deren Name den gesuchten"
						+ " String enthält!");
			}
		}

		artikel = katalog.findAndSort(search4, "");
		assertThat(artikel).hasSize(0);
	}

	@Test
	public void ArtikelKatalogSortTest() {
		Iterable<Artikel> artikel = katalog.findAndSort("", "alpha");
		Iterator<Artikel> it1 = artikel.iterator();
		Iterator<Artikel> it2 = artikel.iterator();
		if (it2.hasNext()) {
			it2.next();
			while (it1.hasNext() && it2.hasNext()) {
				assertTrue(
						"Wenn sortierung = \"alpha\" übergeben wird, sollten die Elemente auch tatsächlich in"
								+ " aufsteigender Alphabetischer Reihenfolge sortiert sein!",
						nameComp.compare(it1.next(), it2.next()) <= 0);
			}
		}

		artikel = katalog.findAndSort("", "rev_alpha");
		it1 = artikel.iterator();
		it2 = artikel.iterator();
		if (it2.hasNext()) {
			it2.next();
			while (it1.hasNext() && it2.hasNext()) {
				assertTrue(
						"Wenn sortierung = \"rev_alpha\" übergeben wird, sollten die Elemente auch tatsächlich in"
								+ " absteigender Alphabetischer Reihenfolge sortiert sein!",
						nameComp.compare(it1.next(), it2.next()) >= 0);
			}
		}

		artikel = katalog.findAndSort("", "price");
		it1 = artikel.iterator();
		it2 = artikel.iterator();
		if (it2.hasNext()) {
			it2.next();
			while (it1.hasNext() && it2.hasNext()) {
				assertTrue(
						"Wenn sortierung = \"price\" übergeben wird, sollten die Elemente auch tatsächlich nach"
								+ " aufsteigendem Preis sortiert sein!",
						priceComp.compare(it1.next(), it2.next()) <= 0);
			}
		}

		artikel = katalog.findAndSort("", "rev_price");
		it1 = artikel.iterator();
		it2 = artikel.iterator();
		if (it2.hasNext()) {
			it2.next();
			while (it1.hasNext() && it2.hasNext()) {
				assertTrue("Wenn sortierung = \"rev_price\" übergeben wird, sollten die Elemente auch tatsächlich nach"
						+ " absteigendem Preis sortiert sein!", priceComp.compare(it1.next(), it2.next()) >= 0);
			}
		}

		artikel = katalog.findAndSort("", "rev_price_falsch_geschrieben");
		assertEquals("Sorting with wrong String should return all Elements without special ordering", katalog.findAll(),
				artikel);

	}
}
