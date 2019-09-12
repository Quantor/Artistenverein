package artistenverein.Lagerverwaltung;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.salespointframework.quantity.Quantity;

import artistenverein.AbstractIntegrationTests;

public class InventarKonfigurationTests extends AbstractIntegrationTests {

	private static InventarKonfiguration invKonfMindestMenge;
	private static InventarKonfiguration invKonfRabatt;

	@Before
	public void setUp() {
		invKonfMindestMenge = new InventarKonfiguration(10, InventarKonfiguration.KonfigurationsTyp.MINDEST_MENGE);
		invKonfRabatt = new InventarKonfiguration(20, InventarKonfiguration.KonfigurationsTyp.RABATT);
	}

	@Test
	public void InvKonfRabattErstellTest() {
		try {
			InventarKonfiguration test = new InventarKonfiguration(-1, InventarKonfiguration.KonfigurationsTyp.RABATT);
			test.hatTypRabatt();
			fail("InventarKonfiguration.InventarKonfiguration() sollte beim Typ Rabatt nicht den Wert -1 akzeptieren!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "wert darf nicht kleiner 0 sein, wenn der Typ Rabatt ausgewählt ist!");
		}

		try {
			InventarKonfiguration test = new InventarKonfiguration(100, InventarKonfiguration.KonfigurationsTyp.RABATT);
			test.hatTypRabatt();
			fail("InventarKonfiguration.InventarKonfiguration() sollte beim Typ Rabatt nicht den Wert 100 akzeptieren!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "wert darf nicht größer als 99 sein, wenn der Typ Rabatt ausgewählt ist!");
		}

		try {
			InventarKonfiguration test = new InventarKonfiguration(0, InventarKonfiguration.KonfigurationsTyp.RABATT);
			assertTrue("InventarKonfiguration.InventarKonfiguration() sollte den korrekten Typ speichern!",
					test.hatTypRabatt());
			assertEquals("InventarKonfiguration.InventarKonfiguration() sollte den korrekten Rabatt speichern!",
					test.getRabatt(), 0);
		} catch (IllegalArgumentException e) {
			fail("InventarKonfiguration.InventarKonfiguration() sollte den Wert 0 beim Typ Rabatt akzeptieren!");
		}

		try {
			InventarKonfiguration test = new InventarKonfiguration(99, InventarKonfiguration.KonfigurationsTyp.RABATT);
			assertTrue("InventarKonfiguration.InventarKonfiguration() sollte den korrekten Typ speichern!",
					test.hatTypRabatt());
			assertEquals("InventarKonfiguration.InventarKonfiguration() sollte den korrekten Rabatt speichern!",
					test.getRabatt(), 99);
		} catch (IllegalArgumentException e) {
			fail("InventarKonfiguration.InventarKonfiguration() sollte den Wert 99 beim Typ Rabatt akzeptieren!");
		}

		try {
			InventarKonfiguration test = new InventarKonfiguration(20, InventarKonfiguration.KonfigurationsTyp.RABATT);
			assertTrue("InventarKonfiguration.InventarKonfiguration() sollte den korrekten Typ speichern!",
					test.hatTypRabatt());
			assertEquals("InventarKonfiguration.InventarKonfiguration() sollte den korrekten Rabatt speichern!",
					test.getRabatt(), 20);
		} catch (IllegalArgumentException e) {
			fail("InventarKonfiguration.InventarKonfiguration() sollte den Wert 20 beim Typ Rabatt akzeptieren!");
		}
	}

	@Test
	public void InvKonfMindestMengeErstellTest() {
		try {
			InventarKonfiguration test = new InventarKonfiguration(-1,
					InventarKonfiguration.KonfigurationsTyp.MINDEST_MENGE);
			test.hatTypMindestMenge();
			fail("InventarKonfiguration.InventarKonfiguration() sollte beim Typ Mindestmenge nicht den Wert -1 akzeptieren!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "wert darf nicht kleiner 1 sein, wenn der Typ Mindestmenge ausgewählt ist!");
		}
		
		try {
			InventarKonfiguration test = new InventarKonfiguration(0,
					InventarKonfiguration.KonfigurationsTyp.MINDEST_MENGE);
			test.hatTypMindestMenge();
			fail("InventarKonfiguration.InventarKonfiguration() sollte beim Typ Mindestmenge nicht den Wert 0 akzeptieren!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "wert darf nicht kleiner 1 sein, wenn der Typ Mindestmenge ausgewählt ist!");
		}

		try {
			InventarKonfiguration test = new InventarKonfiguration(1,
					InventarKonfiguration.KonfigurationsTyp.MINDEST_MENGE);
			assertTrue("InventarKonfiguration.InventarKonfiguration() sollte den korrekten Typ speichern!",
					test.hatTypMindestMenge());
			assertEquals("InventarKonfiguration.InventarKonfiguration() sollte die korrekte Mindestmenge speichern!",
					test.getQuantity(), Quantity.of(1));
		} catch (IllegalArgumentException e) {
			fail("InventarKonfiguration.InventarKonfiguration() sollte den Wert 0 beim Typ Mindestmenge akzeptieren!");
		}
		

		try {
			InventarKonfiguration test = new InventarKonfiguration(99,
					InventarKonfiguration.KonfigurationsTyp.MINDEST_MENGE);
			assertTrue("InventarKonfiguration.InventarKonfiguration() sollte den korrekten Typ speichern!",
					test.hatTypMindestMenge());
			assertEquals("InventarKonfiguration.InventarKonfiguration() sollte die korrekte Mindestmenge speichern!",
					test.getQuantity(), Quantity.of(99));
		} catch (IllegalArgumentException e) {
			fail("InventarKonfiguration.InventarKonfiguration() sollte den Wert 99 beim Typ Mindestmenge akzeptieren!");
		}

		try {
			InventarKonfiguration test = new InventarKonfiguration(100,
					InventarKonfiguration.KonfigurationsTyp.MINDEST_MENGE);
			assertTrue("InventarKonfiguration.InventarKonfiguration() sollte den korrekten Typ speichern!",
					test.hatTypMindestMenge());
			assertEquals("InventarKonfiguration.InventarKonfiguration() sollte die korrekte Mindestmenge speichern!",
					test.getQuantity(), Quantity.of(100));
		} catch (IllegalArgumentException e) {
			fail("InventarKonfiguration.InventarKonfiguration() sollte den Wert 100 beim Typ Mindestmenge akzeptieren!");
		}

	}

	@Test
	public void InvKonfNullPointerErstellTest() {
		try {
			InventarKonfiguration test = new InventarKonfiguration(10, null);
			test.hatTypRabatt();
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "typ darf nicht null sein!");
		}
	}

	@Test
	public void setWertRabattTest() {
		try {
			invKonfRabatt.setWert(-1);
			fail("InventarKonfiguration.InventarKonfiguration() sollte beim Typ Rabatt nicht den Wert -1 akzeptieren!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "wert darf nicht kleiner 0 sein, wenn der Typ Rabatt ausgewählt ist!");
		}

		try {
			invKonfRabatt.setWert(100);
			fail("InventarKonfiguration.InventarKonfiguration() sollte beim Typ Rabatt nicht den Wert 100 akzeptieren!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "wert darf nicht größer als 99 sein, wenn der Typ Rabatt ausgewählt ist!");
		}

		invKonfRabatt.setWert(0);
		assertEquals("InventarKonfiguration.setWert() sollte den Wert korrekt speichern", invKonfRabatt.getRabatt(), 0);
		invKonfRabatt.setWert(10);
		assertEquals("InventarKonfiguration.setWert() sollte den Wert korrekt speichern", invKonfRabatt.getRabatt(),
				10);
		invKonfRabatt.setWert(99);
		assertEquals("InventarKonfiguration.setWert() sollte den Wert korrekt speichern", invKonfRabatt.getRabatt(),
				99);

	}

	@Test
	public void setWertMindestMengeTest() {
		try {
			invKonfMindestMenge.setWert(-1);
			fail("InventarKonfiguration.InventarKonfiguration() sollte beim Typ Mindestmenge nicht den Wert -1 akzeptieren!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "wert darf nicht kleiner 1 sein, wenn der Typ Mindestmenge ausgewählt ist!");
		}

		try {
			invKonfMindestMenge.setWert(0);
			fail("InventarKonfiguration.InventarKonfiguration() sollte beim Typ Mindestmenge nicht den Wert 0 akzeptieren!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "wert darf nicht kleiner 1 sein, wenn der Typ Mindestmenge ausgewählt ist!");
		}

		invKonfMindestMenge.setWert(1);
		assertEquals("InventarKonfiguration.setWert() sollte den Wert korrekt speichern",
				invKonfMindestMenge.getQuantity(), Quantity.of(1));
		invKonfMindestMenge.setWert(10);
		assertEquals("InventarKonfiguration.setWert() sollte den Wert korrekt speichern",
				invKonfMindestMenge.getQuantity(), Quantity.of(10));
		invKonfMindestMenge.setWert(99);
		assertEquals("InventarKonfiguration.setWert() sollte den Wert korrekt speichern",
				invKonfMindestMenge.getQuantity(), Quantity.of(99));
		invKonfMindestMenge.setWert(100);
		assertEquals("InventarKonfiguration.setWert() sollte den Wert korrekt speichern",
				invKonfMindestMenge.getQuantity(), Quantity.of(100));
	}

	@Test
	public void hatTypRabattTest() {
		assertTrue("InventarKonfiguration.hatTypRabatt() sollte den korrekten Wert zurückgeben",
				invKonfRabatt.hatTypRabatt());
		assertFalse("InventarKonfiguration.hatTypRabatt() sollte den korrekten Wert zurückgeben",
				invKonfMindestMenge.hatTypRabatt());
	}

	@Test
	public void hatTypMindestMengeTest() {
		assertTrue("InventarKonfiguration.hatTypMindestMenge() sollte den korrekten Wert zurückgeben",
				invKonfMindestMenge.hatTypMindestMenge());
		assertFalse("InventarKonfiguration.hatTypMindestMenge() sollte den korrekten Wert zurückgeben",
				invKonfRabatt.hatTypMindestMenge());
	}

	@Test
	public void getRabattTest() {
		try {
			invKonfMindestMenge.getRabatt();
		} catch (UnsupportedOperationException e) {
			assertEquals(e.getMessage(), "getRabatt() ist nur für Konfiguration Rabatt erlaubt!");
		}

		assertEquals("InventarKonfiguration.getRabatt() sollte den korrekten Wert zurückgeben", 20,
				invKonfRabatt.getRabatt());
	}

	@Test
	public void getQuantityTest() {
		try {
			invKonfRabatt.getQuantity();
		} catch (UnsupportedOperationException e) {
			assertEquals(e.getMessage(), "getQuantity() ist nur für Konfiguration MindestMenge erlaubt!");
		}
		assertEquals("InventarKonfiguration.getQuantity() sollte den korrekten Wert zurückgeben", Quantity.of(10),
				invKonfMindestMenge.getQuantity());
	}
}
