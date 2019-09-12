package artistenverein.Lagerverwaltung;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.salespointframework.core.Currencies.EURO;

import org.javamoney.moneta.Money;
import org.junit.Before;
import org.junit.Test;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.quantity.Quantity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ExtendedModelMap;

import artistenverein.AbstractIntegrationTests;

public class ControllerInventarTests extends AbstractIntegrationTests {
	@Autowired
	private Inventory<InventoryItem> inventar;
	@Autowired
	private KonfigurationsRepository konfRep;
	private ControllerInventar testController;
	@Autowired
	private ArtikelKatalog katalog;
	private static final int DEFAULT_MENGE = 10;
	private static final int DEFAULT_RABATT = 20;
	private static Artikel testArtikel1;
	private static Artikel testArtikel2;
	private final static Number preis1 = 9.99;
	private final static Number preis2 = 14.99;

	@Before
	public void setUp() {
		konfRep.deleteAll();
		if (!this.konfRep.enthaeltMindestMenge()) {
			this.konfRep.save(
					new InventarKonfiguration(DEFAULT_MENGE, InventarKonfiguration.KonfigurationsTyp.MINDEST_MENGE));
		}
		if (!this.konfRep.enthaeltRabatt()) {
			this.konfRep
					.save(new InventarKonfiguration(DEFAULT_RABATT, InventarKonfiguration.KonfigurationsTyp.RABATT));
		}
		testController = new ControllerInventar(inventar, konfRep);

		inventar.deleteAll();
		katalog.deleteAll();

		testArtikel1 = new Artikel("Fackel", "fac", Money.of(preis1, EURO), "Einfache Fackel.");
		testArtikel2 = new Artikel("Pois", "poi", Money.of(preis2, EURO), "Feuer-Poi");

		katalog.save(testArtikel1);
		katalog.save(testArtikel2);
		inventar.save(new InventoryItem(testArtikel1, Quantity.of(10)));
		inventar.save(new InventoryItem(testArtikel2, Quantity.of(10)));

	}

	@Test
	public void ControllerInventarErstellTest() {
		try {
			ControllerInventar test = new ControllerInventar(null, konfRep);
			test.toString();
			fail("ControllerInventar.ControllerInventar() sollte keine Nullpointer akzeptieren");
		} catch (IllegalArgumentException e) {
			assertEquals("inventar darf nicht null sein!", e.getMessage());
		}

		try {
			ControllerInventar test = new ControllerInventar(inventar, null);
			test.toString();
			fail("ControllerInventar.ControllerInventar() sollte keine Nullpointer akzeptieren");
		} catch (IllegalArgumentException e) {
			assertEquals("konfigurationsRep darf nicht null sein!", e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void vorratAnzeigenTest() {
		ExtendedModelMap model = new ExtendedModelMap();
		String retString = testController.vorrat(model);
		assertEquals("Inventarverwaltung/stock", retString);
		assertEquals("ControllerInventar.vorrat() sollte den korrekten Rabatt zurückgeben", DEFAULT_RABATT,
				(int) model.get("rabatt"));
		assertEquals("ControllerInventar.vorrat() sollte die korrekte MindestMenge zurückgeben",
				Quantity.of(DEFAULT_MENGE), (Quantity) model.get("mindestMenge"));
		assertNotNull("ControllerInventar.vorrat() sollte die vorhandenen Artikel auflisten", model.get("stock"));
		int i = 0;
		for (InventoryItem invItem : (Iterable<InventoryItem>) model.get("stock")) {
			i++;
			invItem.getId();
		}
		assertEquals("ControllerInventar.vorrat() sollte alle Artikel zurückgeben", 2, i);
	}

	@Test
	public void vorratEinzelnTest() {
		Artikel[] artikelList = { testArtikel1, testArtikel2 };
		String[] anzahl = { "0", "0" };
		String retString = testController.vorrat(testArtikel1, artikelList, anzahl);
		assertEquals("redirect:/inventarverwaltung", retString);
		for (InventoryItem invItem : inventar.findAll()) {
			assertEquals("ControllerInventar.vorrat() sollte den Vorrat entsprechend der Angaben erhöhen",
					Quantity.of(DEFAULT_MENGE), invItem.getQuantity());

		}

		anzahl[0] = "0";
		anzahl[1] = "0";
		retString = testController.vorrat(testArtikel2, artikelList, anzahl);
		assertEquals("redirect:/inventarverwaltung", retString);
		for (InventoryItem invItem : inventar.findAll()) {
			assertEquals("ControllerInventar.vorrat() sollte den Vorrat entsprechend der Angaben erhöhen",
					Quantity.of(DEFAULT_MENGE), invItem.getQuantity());

		}

		anzahl[0] = "1";
		anzahl[1] = "0";
		retString = testController.vorrat(testArtikel1, artikelList, anzahl);
		assertEquals("redirect:/inventarverwaltung", retString);
		for (InventoryItem invItem : inventar.findAll()) {
			if (invItem.getProduct().equals(testArtikel1)) {
				assertEquals("ControllerInventar.vorrat() sollte den Vorrat entsprechend der Angaben erhöhen",
						Quantity.of(DEFAULT_MENGE + 1), invItem.getQuantity());
			} else {
				assertEquals("ControllerInventar.vorrat() sollte den Vorrat entsprechend der Angaben erhöhen",
						Quantity.of(DEFAULT_MENGE), invItem.getQuantity());
			}
		}
		
		anzahl[0] = "0";
		anzahl[1] = "1";
		retString = testController.vorrat(testArtikel2, artikelList, anzahl);
		assertEquals("redirect:/inventarverwaltung", retString);
		for (InventoryItem invItem : inventar.findAll()) {
			assertEquals("ControllerInventar.vorrat() sollte den Vorrat entsprechend der Angaben erhöhen",
						Quantity.of(DEFAULT_MENGE + 1), invItem.getQuantity());
		}
		
		anzahl[0] = "1";
		anzahl[1] = "1";
		retString = testController.vorrat(testArtikel1, artikelList, anzahl);
		assertEquals("redirect:/inventarverwaltung", retString);
		for (InventoryItem invItem : inventar.findAll()) {
			if (invItem.getProduct().equals(testArtikel1)) {
				assertEquals("ControllerInventar.vorrat() sollte den Vorrat entsprechend der Angaben erhöhen",
						Quantity.of(DEFAULT_MENGE + 2), invItem.getQuantity());
			} else {
				assertEquals("ControllerInventar.vorrat() sollte den Vorrat entsprechend der Angaben erhöhen",
						Quantity.of(DEFAULT_MENGE + 1), invItem.getQuantity());
			}
		}
		
		anzahl[0] = "1";
		anzahl[1] = "1";
		retString = testController.vorrat(testArtikel2, artikelList, anzahl);
		assertEquals("redirect:/inventarverwaltung", retString);
		for (InventoryItem invItem : inventar.findAll()) {
			assertEquals("ControllerInventar.vorrat() sollte den Vorrat entsprechend der Angaben erhöhen",
						Quantity.of(DEFAULT_MENGE + 2), invItem.getQuantity());
		}
	}

	@Test
	public void vorratAlleTest() {
		Artikel[] artikelList = { testArtikel1, testArtikel2 };
		String[] anzahl = { "0", "0" };
		String retString = testController.vorrat(artikelList, anzahl);
		assertEquals("redirect:/inventarverwaltung", retString);
		for (InventoryItem invItem : inventar.findAll()) {
			assertEquals("ControllerInventar.vorrat() sollte den Vorrat entsprechend der Angaben erhöhen",
					Quantity.of(DEFAULT_MENGE), invItem.getQuantity());

		}

		anzahl[0] = "1";
		anzahl[1] = "0";
		retString = testController.vorrat(artikelList, anzahl);
		assertEquals("redirect:/inventarverwaltung", retString);
		for (InventoryItem invItem : inventar.findAll()) {
			if (invItem.getProduct().equals(testArtikel1)) {
				assertEquals("ControllerInventar.vorrat() sollte den Vorrat entsprechend der Angaben erhöhen",
						Quantity.of(DEFAULT_MENGE + 1), invItem.getQuantity());
			} else {
				assertEquals("ControllerInventar.vorrat() sollte den Vorrat entsprechend der Angaben erhöhen",
						Quantity.of(DEFAULT_MENGE), invItem.getQuantity());
			}
		}
		
		anzahl[0] = "0";
		anzahl[1] = "1";
		retString = testController.vorrat(artikelList, anzahl);
		assertEquals("redirect:/inventarverwaltung", retString);
		for (InventoryItem invItem : inventar.findAll()) {
			assertEquals("ControllerInventar.vorrat() sollte den Vorrat entsprechend der Angaben erhöhen",
						Quantity.of(DEFAULT_MENGE + 1), invItem.getQuantity());
		}
		
		anzahl[0] = "1";
		anzahl[1] = "1";
		retString = testController.vorrat(artikelList, anzahl);
		assertEquals("redirect:/inventarverwaltung", retString);
		for (InventoryItem invItem : inventar.findAll()) {
			assertEquals("ControllerInventar.vorrat() sollte den Vorrat entsprechend der Angaben erhöhen",
						Quantity.of(DEFAULT_MENGE + 2), invItem.getQuantity());
		}
	}

	@Test
	public void setMindestMengeTest() {
		String retString = testController.setMindestMenge("-1");
		assertEquals("redirect:/inventarverwaltung", retString);
		assertEquals("ControllerInventar.setMindestMenge() sollte negative Werte ignorieren",
				Quantity.of(DEFAULT_MENGE), konfRep.getMindestMenge().getQuantity());

		retString = testController.setMindestMenge("0");
		assertEquals("redirect:/inventarverwaltung", retString);
		assertEquals("ControllerInventar.setMindestMenge() sollte den Wert 0 ignorieren", Quantity.of(DEFAULT_MENGE),
				konfRep.getMindestMenge().getQuantity());

		retString = testController.setMindestMenge("1");
		assertEquals("redirect:/inventarverwaltung", retString);
		assertEquals("ControllerInventar.setMindestMenge() sollte korrekte Werte übernehmen", Quantity.of(1),
				konfRep.getMindestMenge().getQuantity());

		retString = testController.setMindestMenge("20");
		assertEquals("redirect:/inventarverwaltung", retString);
		assertEquals("ControllerInventar.setMindestMenge() sollte korrekte Werte übernehmen", Quantity.of(20),
				konfRep.getMindestMenge().getQuantity());

		retString = testController.setMindestMenge("99");
		assertEquals("redirect:/inventarverwaltung", retString);
		assertEquals("ControllerInventar.setMindestMenge() sollte korrekte Werte übernehmen", Quantity.of(99),
				konfRep.getMindestMenge().getQuantity());

		retString = testController.setMindestMenge("100");
		assertEquals("redirect:/inventarverwaltung", retString);
		assertEquals("ControllerInventar.setMindestMenge() sollte zu hohe Werte ignorieren", Quantity.of(100),
				konfRep.getMindestMenge().getQuantity());

		retString = testController.setMindestMenge("asdf");
		assertEquals("redirect:/inventarverwaltung", retString);
		assertEquals("ControllerInventar.setMindestMenge() sollte nicht interpretierbare Werte ignorieren",
				Quantity.of(100), konfRep.getMindestMenge().getQuantity());
	}

	@Test
	public void setRabattTest() {
		String retString = testController.setRabatt("-1");
		assertEquals("redirect:/inventarverwaltung", retString);
		assertEquals("ControllerInventar.setRabatt() sollte negative Werte ignorieren", DEFAULT_RABATT,
				konfRep.getRabatt().getRabatt());

		retString = testController.setRabatt("0");
		assertEquals("redirect:/inventarverwaltung", retString);
		assertEquals("ControllerInventar.setRabatt() sollte korrekte Werte übernehmen", 0,
				konfRep.getRabatt().getRabatt());

		retString = testController.setRabatt("1");
		assertEquals("redirect:/inventarverwaltung", retString);
		assertEquals("ControllerInventar.setRabatt() sollte korrekte Werte übernehmen", 1,
				konfRep.getRabatt().getRabatt());

		retString = testController.setRabatt("20");
		assertEquals("redirect:/inventarverwaltung", retString);
		assertEquals("ControllerInventar.setRabatt() sollte korrekte Werte übernehmen", 20,
				konfRep.getRabatt().getRabatt());

		retString = testController.setRabatt("99");
		assertEquals("redirect:/inventarverwaltung", retString);
		assertEquals("ControllerInventar.setRabatt() sollte korrekte Werte übernehmen", 99,
				konfRep.getRabatt().getRabatt());

		retString = testController.setRabatt("100");
		assertEquals("redirect:/inventarverwaltung", retString);
		assertEquals("ControllerInventar.setRabatt() sollte zu hohe Werte ignorieren", 99,
				konfRep.getRabatt().getRabatt());

		retString = testController.setRabatt("asdf");
		assertEquals("redirect:/inventarverwaltung", retString);
		assertEquals("ControllerInventar.setRabatt() sollte nicht interpretierbare Werte ignorieren", 99,
				konfRep.getRabatt().getRabatt());
	}

	@Test
	public void autoBuyerTest() {
		konfRep.getMindestMenge().setWert(5);
		testController.autoBuyer();
		for (InventoryItem invItem : inventar.findAll()) {
			assertTrue(
					"ControllerInventar.autoBuyer() sollte die vorrätige Menge nicht erhöhen, wenn genügend Artikel vprrätig sind",
					invItem.getQuantity().equals(Quantity.of(DEFAULT_MENGE)));
		}
		konfRep.getMindestMenge().setWert(20);
		testController.autoBuyer();
		for (InventoryItem invItem : inventar.findAll()) {
			assertTrue(
					"ControllerInventar.autoBuyer() sollte die vorrätige Menge nicht erhöhen, wenn genügend Artikel vprrätig sind",
					invItem.getQuantity().equals(Quantity.of(20)));
		}
	}
}
