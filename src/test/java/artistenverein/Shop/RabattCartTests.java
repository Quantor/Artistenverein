package artistenverein.Shop;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.salespointframework.core.Currencies.EURO;

import org.javamoney.moneta.Money;
import org.junit.Before;
import org.junit.Test;
import org.salespointframework.quantity.Quantity;
import org.springframework.beans.factory.annotation.Autowired;

import artistenverein.AbstractIntegrationTests;
import artistenverein.Lagerverwaltung.Artikel;
import artistenverein.Lagerverwaltung.KonfigurationsRepository;

public class RabattCartTests extends AbstractIntegrationTests {

	@Autowired
	private KonfigurationsRepository konfigurationsRep;
	private static RabattCart testCartTrue1;
	private static RabattCart testCartTrue2;
	private static RabattCart testCartFalse1;
	private static RabattCart testCartFalse2;
	private static Artikel testArtikel1;
	private static Artikel testArtikel2;
	private final static Number preis1 = 10.00;
	private final static Number preis2 = 15.00;
	private final static double anzahl1 = 1;
	private final static double anzahl2 = 2;

	@Before
	public void setUp() {
		testArtikel1 = new Artikel("Fackel", "fac", Money.of(preis1, EURO),
				"Einfache Fackel, zum Jonglieren geeignet.");
		testArtikel2 = new Artikel("Pois", "poi", Money.of(preis2, EURO), "Feuer-Poi");
		testCartTrue1 = new RabattCart(konfigurationsRep);
		testCartTrue1.addOrUpdateItem(testArtikel1, Quantity.of(anzahl1));
		testCartTrue1.setRabattStatus(true);
		testCartTrue2 = new RabattCart(konfigurationsRep);
		testCartTrue2.addOrUpdateItem(testArtikel1, Quantity.of(anzahl1));
		testCartTrue2.addOrUpdateItem(testArtikel2, Quantity.of(anzahl2));
		testCartTrue2.setRabattStatus(true);
		testCartFalse1 = new RabattCart(konfigurationsRep);
		testCartFalse1.addOrUpdateItem(testArtikel1, Quantity.of(anzahl1));
		testCartFalse1.setRabattStatus(false);
		testCartFalse2 = new RabattCart(konfigurationsRep);
		testCartFalse2.addOrUpdateItem(testArtikel1, Quantity.of(anzahl1));
		testCartFalse2.addOrUpdateItem(testArtikel2, Quantity.of(anzahl2));
		testCartFalse1.setRabattStatus(false);
	}

	@Test
	public void getTotalPriceTest() {
		int rabatt = 100 - konfigurationsRep.getRabatt().getRabatt();
		double calcRabatt = 0.1 * rabatt;
		assertTrue("RabattCart.getPrice() sollte den korrekten Wert mit einberechnetem Rabatt zurückgeben",
				testCartTrue1.getPrice().isEqualTo(Money.of(calcRabatt, EURO)));
		calcRabatt = 0.4 * rabatt;
		assertTrue("RabattCart.getPrice() sollte den korrekten Wert mit einberechnetem Rabatt zurückgeben",
				testCartTrue2.getPrice().isEqualTo(Money.of(calcRabatt, EURO)));
		assertTrue("RabattCart.getPrice() sollte den Rabatt nicht mit einberechnen, wenn er nicht aktiviert ist",
				testCartFalse1.getPrice().isEqualTo(Money.of(10.00, EURO)));
		assertTrue("RabattCart.getPrice() sollte den Rabatt nicht mit einberechnen, wenn er nicht aktiviert ist",
				testCartFalse2.getPrice().isEqualTo(Money.of(40.00, EURO)));
	}

	@Test
	public void getRabattStatusTest() {
		assertTrue("RabattCart.getRabattStatus() sollte true zurückgeben wenn der Rabatt aktiviert ist!",
				testCartTrue1.getRabattStatus());
		assertTrue("RabattCart.getRabattStatus() sollte true zurückgeben wenn der Rabatt aktiviert ist!",
				testCartTrue2.getRabattStatus());
		assertFalse("RabattCart.getRabattStatus() sollte false zurückgeben wenn der Rabatt deaktiviert ist!",
				testCartFalse1.getRabattStatus());
		assertFalse("RabattCart.getRabattStatus() sollte false zurückgeben wenn der Rabatt deaktiviert ist!",
				testCartFalse2.getRabattStatus());
	}

	@Test
	public void setRabattStatusTest() {
		testCartTrue1.setRabattStatus(false);
		assertFalse("RabattCart.setRabattStatus() sollte den Wert korrekt setzen!", testCartTrue1.getRabattStatus());
		testCartTrue2.setRabattStatus(false);
		assertFalse("RabattCart.setRabattStatus() sollte den Wert korrekt setzen!", testCartTrue2.getRabattStatus());
		testCartFalse1.setRabattStatus(true);
		assertTrue("RabattCart.setRabattStatus() sollte den Wert korrekt setzen!", testCartFalse1.getRabattStatus());
		testCartFalse2.setRabattStatus(true);
		assertTrue("RabattCart.setRabattStatus() sollte den Wert korrekt setzen!", testCartFalse2.getRabattStatus());
	}
}
