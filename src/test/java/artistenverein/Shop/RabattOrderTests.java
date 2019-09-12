package artistenverein.Shop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.salespointframework.core.Currencies.EURO;

import org.javamoney.moneta.Money;
import org.junit.Before;
import org.junit.Test;
import org.salespointframework.payment.Cash;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;

import artistenverein.AbstractIntegrationTests;
import artistenverein.Lagerverwaltung.Artikel;

public class RabattOrderTests extends AbstractIntegrationTests {

	@Autowired
	private UserAccountManager accounts;
	private static RabattOrder testOrderTrue1;
	private static RabattOrder testOrderTrue2;
	private static RabattOrder testOrderFalse1;
	private static RabattOrder testOrderFalse2;
	private static RabattOrder testOrderTrue3;
	private static RabattCart testCart1;
	private static RabattCart testCart2;
	private static RabattCart testCart3;
	private static UserAccount testAccount;
	private static Artikel testArtikel1;
	private static Artikel testArtikel2;
	private static Artikel testArtikel3;
	private final static Number preis1 = 10.00;
	private final static Number preis2 = 15.00;
	private final static Number preis3 = 9.99;
	private final static double anzahl1 = 1;
	private final static double anzahl2 = 2;

	@Before
	public void setUp() {
		testAccount = accounts.findByUsername("hans").get();
		testOrderTrue1 = new RabattOrder(testAccount, Cash.CASH, 10, true);
		testOrderTrue2 = new RabattOrder(testAccount, Cash.CASH, 10, true);
		testOrderTrue3 = new RabattOrder(testAccount, Cash.CASH, 20, true);
		testOrderFalse1 = new RabattOrder(testAccount, Cash.CASH, 10, false);
		testOrderFalse2 = new RabattOrder(testAccount, Cash.CASH, 10, false);
		testArtikel1 = new Artikel("Fackel", "fac", Money.of(preis1, EURO),
				"Einfache Fackel, zum Jonglieren geeignet.");
		testArtikel2 = new Artikel("Pois", "poi", Money.of(preis2, EURO), "Feuer-Poi");
		testArtikel3 = new Artikel("Pois2", "poi2", Money.of(preis3, EURO), "Feuer-Poi2");
		testCart1 = new RabattCart();
		testCart1.addOrUpdateItem(testArtikel1, Quantity.of(anzahl1));
		testCart2 = new RabattCart();
		testCart2.addOrUpdateItem(testArtikel1, Quantity.of(anzahl1));
		testCart2.addOrUpdateItem(testArtikel2, Quantity.of(anzahl2));
		testCart1.addItemsTo(testOrderTrue1);
		testCart2.addItemsTo(testOrderTrue2);
		testCart1 = new RabattCart();
		testCart1.addOrUpdateItem(testArtikel1, Quantity.of(anzahl1));
		testCart2 = new RabattCart();
		testCart2.addOrUpdateItem(testArtikel1, Quantity.of(anzahl1));
		testCart2.addOrUpdateItem(testArtikel2, Quantity.of(anzahl2));
		testCart3 = new RabattCart();
		testCart3.addOrUpdateItem(testArtikel3, Quantity.of(anzahl1));
		testCart1.addItemsTo(testOrderFalse1);
		testCart2.addItemsTo(testOrderFalse2);
		testCart3.addItemsTo(testOrderTrue3);
	}
	
	@Test
	public void RabattOrderErstellTest() {
		try {
			RabattOrder testOrder = new RabattOrder(testAccount, Cash.CASH, 0, true);
			assertEquals("RabattOrder.RabattOrder() sollte den Rabatt korrekt speichern", testOrder.getRabatt(), 0);
		} catch (IllegalArgumentException e) {
			fail("RabattOrder.RabattOrder() sollte den Rabatt vom Wert 0 akzeptieren");
		}

		try {
			RabattOrder testOrder = new RabattOrder(testAccount, Cash.CASH, 10, true);
			assertEquals("RabattOrder.RabattOrder() sollte den Rabatt korrekt speichern", testOrder.getRabatt(), 10);
		} catch (IllegalArgumentException e) {
			fail("RabattOrder.RabattOrder() sollte den Rabatt vom Wert 10 akzeptieren");
		}

		try {
			RabattOrder testOrder = new RabattOrder(testAccount, Cash.CASH, 99, true);
			assertEquals("RabattOrder.RabattOrder() sollte den Rabatt korrekt speichern", testOrder.getRabatt(), 99);
		} catch (IllegalArgumentException e) {
			fail("RabattOrder.RabattOrder() sollte den Rabatt vom Wert 99 akzeptieren");
		}

		try {
			RabattOrder testOrder = new RabattOrder(testAccount, Cash.CASH, -10, true);
			testOrder.getRabatt();
			fail("RabattOrder.RabattOrder() sollte eine IllegalArgumentException werfen, wenn das Argument rabbatt kleiner 0 ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "rabatt darf nicht kleiner 0 sein!");
		}

		try {
			RabattOrder testOrder = new RabattOrder(testAccount, Cash.CASH, 100, true);
			testOrder.getRabatt();
			fail("RabattOrder.RabattOrder() sollte eine IllegalArgumentException werfen, wenn das Argument größer 99 ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "rabatt darf nicht größer 99 sein!");
		}
	}

	@Test
	public void getTotalPriceTest() {
		assertTrue("RabattOrder.getTotalPrice() sollte den korrekten Wert mit einberechnetem Rabatt zurückgeben",
				testOrderTrue1.getTotalPrice().isEqualTo(Money.of(9.00, EURO)));
		assertTrue("RabattOrder.getTotalPrice() sollte den korrekten Wert mit einberechnetem Rabatt zurückgeben",
				testOrderTrue2.getTotalPrice().isEqualTo(Money.of(36.00, EURO)));
		assertTrue("RabattOrder.getTotalPrice() sollte den Rabatt nicht mit einberechnen, wenn er nicht aktiviert ist",
				testOrderFalse1.getTotalPrice().isEqualTo(Money.of(10.00, EURO)));
		assertTrue("RabattOrder.getTotalPrice() sollte den Rabatt nicht mit einberechnen, wenn er nicht aktiviert ist",
				testOrderFalse2.getTotalPrice().isEqualTo(Money.of(40.00, EURO)));
	}
	
	@Test
	public void getTotalPriceStringTest() {
		assertEquals("RabattOrder.getTotalPriceString() sollte den korrekten Wert mit einberechnetem Rabatt zurückgeben",
				"EUR 9", testOrderTrue1.getTotalPriceString());
		assertEquals("RabattOrder.getTotalPriceString() sollte den korrekten Wert mit einberechnetem Rabatt zurückgeben",
				"EUR 40", testOrderFalse2.getTotalPriceString());
		assertEquals("RabattOrder.getTotalPriceString() sollte den korrekten Wert mit einberechnetem Rabatt zurückgeben",
				"EUR 7.99", testOrderTrue3.getTotalPriceString());
		
	}
	
	@Test
	public void getRabattStatusTest() {
		assertTrue("RabattOrder.getRabattStatus() sollte true zurückgeben wenn der Rabatt aktiviert ist!",
				testOrderTrue1.getRabattStatus());
		assertTrue("RabattOrder.getRabattStatus() sollte true zurückgeben wenn der Rabatt aktiviert ist!",
				testOrderTrue2.getRabattStatus());
		assertFalse("RabattOrder.getRabattStatus() sollte false zurückgeben wenn der Rabatt deaktiviert ist!",
				testOrderFalse1.getRabattStatus());
		assertFalse("RabattOrder.getRabattStatus() sollte false zurückgeben wenn der Rabatt deaktiviert ist!",
				testOrderFalse1.getRabattStatus());
	}
}
