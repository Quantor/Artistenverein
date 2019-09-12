package artistenverein.Lagerverwaltung;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.salespointframework.core.Currencies.EURO;

import java.util.Random;

import org.javamoney.moneta.Money;
import org.junit.Before;
import org.junit.Test;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.quantity.Quantity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.validation.FieldError;

import artistenverein.AbstractIntegrationTests;

public class ControllerArtikelKatalogTests extends AbstractIntegrationTests {
	@Autowired
	private ArtikelKatalog katalog;
	@Autowired
	private ArtikelManager manager;
	@Autowired
	private Inventory<InventoryItem> inventar;
	private ControllerArtikelKatalog testController;
	private static MockMultipartFile MMpFile;
	private static Artikel testArtikel1;
	private static Artikel testArtikel2;
	private final static Number preis1 = 9.99;
	private final static Number preis2 = 14.99;

	@Before
	public void setUp() {
		testArtikel1 = new Artikel("Fackel", "diesenBildnamengibtesnich", Money.of(preis1, EURO), "Einfache Fackel.");
		testArtikel2 = new Artikel("Pois", "diesenBildnamengibtesnich", Money.of(preis2, EURO), "Feuer-Poi");
		testController = new ControllerArtikelKatalog(katalog, manager, inventar);
		katalog.deleteAll();
		inventar.deleteAll();
		katalog.save(testArtikel1);
		katalog.save(testArtikel2);
		inventar.save(new InventoryItem(testArtikel1, Quantity.of(10)));
		inventar.save(new InventoryItem(testArtikel2, Quantity.of(10)));
		byte[] b = new byte[20];
		Random rand = new Random();
		rand.nextBytes(b);
		MMpFile = new MockMultipartFile("fac_test.jpg", b);
	}

	@Test
	public void ControllerArtikelKatalogErstellTest() {
		try {
			ControllerArtikelKatalog test = new ControllerArtikelKatalog(null, manager, inventar);
			test.toString();
			fail("ControllerArtikelKatalog.ControllerArtikelKatalog() sollte keine Nullpointer akzeptieren");
		} catch (IllegalArgumentException e) {
			assertEquals("katalog darf nicht null sein!", e.getMessage());
		}

		try {
			ControllerArtikelKatalog test = new ControllerArtikelKatalog(katalog, null, inventar);
			test.toString();
			fail("ControllerArtikelKatalog.ControllerArtikelKatalog() sollte keine Nullpointer akzeptieren");
		} catch (IllegalArgumentException e) {
			assertEquals("manager darf nicht null sein!", e.getMessage());
		}

		try {
			ControllerArtikelKatalog test = new ControllerArtikelKatalog(katalog, manager, null);
			test.toString();
			fail("ControllerArtikelKatalog.ControllerArtikelKatalog() sollte keine Nullpointer akzeptieren");
		} catch (IllegalArgumentException e) {
			assertEquals("inventar darf nicht null sein!", e.getMessage());
		}
	}

	@Test
	public void artikelKatalogAnzeigenTest() {
		ExtendedModelMap model = new ExtendedModelMap();
		String retString = testController.artikelKatalog(model);
		assertEquals("Shop/artikelKatalog", retString);
		assertNotNull("ControllerArtikelKatalog.artikelKatalog() sollte die vorhandenen Artikel zurückgeben",
				model.get("katalog"));
		assertEquals("ControllerArtikelKatalog.artikelKatalog() sollte die vorhandenen Artikel zurückgeben\"",
				katalog.findAll(), model.get("katalog"));
	}

	@Test
	public void artikelKatalogSortierenTest() {
		String suche = "";
		String sortierung = "rev_alpha";
		ExtendedModelMap model = new ExtendedModelMap();
		String retString = testController.artikelKatalog(model, suche, sortierung);
		assertEquals("Shop/artikelKatalog", retString);
		assertNotNull("ControllerArtikelKatalog.artikelKatalog() sollte die vorhandenen Artikel zurückgeben",
				model.get("katalog"));
		assertEquals("ControllerArtikelKatalog.artikelKatalog() sollte die vorhandenen Artikel zurückgeben\"",
				katalog.findAndSort(suche, sortierung), model.get("katalog"));
		assertEquals("ControllerArtikelKatalog.artikelKatalog() sollte die benutzen Parameter wieder zurückgeben",
				suche, model.get("suche"));
		assertEquals("ControllerArtikelKatalog.artikelKatalog() sollte die benutzen Parameter wieder zurückgeben",
				sortierung, model.get("sortierung"));
	}

	@Test
	public void artikelKatalogSuchenTest() {
		String suche = "a";
		String sortierung = "";
		ExtendedModelMap model = new ExtendedModelMap();
		String retString = testController.artikelKatalog(model, suche, sortierung);
		assertEquals("Shop/artikelKatalog", retString);
		assertNotNull("ControllerArtikelKatalog.artikelKatalog() sollte die vorhandenen Artikel zurückgeben",
				model.get("katalog"));
		assertEquals("ControllerArtikelKatalog.artikelKatalog() sollte die vorhandenen Artikel zurückgeben\"",
				katalog.findAndSort(suche, sortierung), model.get("katalog"));
		assertEquals("ControllerArtikelKatalog.artikelKatalog() sollte die benutzen Parameter wieder zurückgeben",
				suche, model.get("suche"));
		assertEquals("ControllerArtikelKatalog.artikelKatalog() sollte die benutzen Parameter wieder zurückgeben",
				sortierung, model.get("sortierung"));
	}

	@Test
	public void artikelKatalogSuchenUndSortierenTest() {
		String suche = "a";
		String sortierung = "rev_alpha";
		ExtendedModelMap model = new ExtendedModelMap();
		String retString = testController.artikelKatalog(model, suche, sortierung);
		assertEquals("Shop/artikelKatalog", retString);
		assertNotNull("ControllerArtikelKatalog.artikelKatalog() sollte die vorhandenen Artikel zurückgeben",
				model.get("katalog"));
		assertEquals("ControllerArtikelKatalog.artikelKatalog() sollte die vorhandenen Artikel zurückgeben\"",
				katalog.findAndSort(suche, sortierung), model.get("katalog"));
		assertEquals("ControllerArtikelKatalog.artikelKatalog() sollte die benutzen Parameter wieder zurückgeben",
				suche, model.get("suche"));
		assertEquals("ControllerArtikelKatalog.artikelKatalog() sollte die benutzen Parameter wieder zurückgeben",
				sortierung, model.get("sortierung"));
	}

	@Test
	public void detailTest() {
		ExtendedModelMap model = new ExtendedModelMap();
		String retString = testController.detail(testArtikel1, model);
		assertEquals("Shop/artikelDetail", retString);
		assertTrue("ControllerArtikelKatalog.detail() sollte korrekt zurückgeben ob der Artikel bestellbar ist",
				(boolean) model.get("orderable"));
		assertEquals("ControllerArtikelKatalog.detail() sollte den korrekten Artikel zurückgeben", testArtikel1,
				model.get("artikel"));
		assertEquals("ControllerArtikelKatalog.detail() sollte den korrekten Vorrat des Artikels zurückgeben",
				Quantity.of(10), model.get("quantity"));

		inventar.deleteAll();
		model = new ExtendedModelMap();
		retString = testController.detail(testArtikel1, model);
		assertEquals("Shop/artikelDetail", retString);
		assertFalse("ControllerArtikelKatalog.detail() sollte korrekt zurückgeben ob der Artikel bestellbar ist",
				(boolean) model.get("orderable"));
		assertEquals("ControllerArtikelKatalog.detail() sollte den korrekten Artikel zurückgeben", testArtikel1,
				model.get("artikel"));
		assertEquals("ControllerArtikelKatalog.detail() sollte den korrekten Vorrat des Artikels zurückgeben",
				Quantity.of(0), model.get("quantity"));

		inventar.save(new InventoryItem(testArtikel1, Quantity.of(0)));
		model = new ExtendedModelMap();
		retString = testController.detail(testArtikel1, model);
		assertEquals("Shop/artikelDetail", retString);
		assertFalse("ControllerArtikelKatalog.detail() sollte korrekt zurückgeben ob der Artikel bestellbar ist",
				(boolean) model.get("orderable"));
		assertEquals("ControllerArtikelKatalog.detail() sollte den korrekten Artikel zurückgeben", testArtikel1,
				model.get("artikel"));
		assertEquals("ControllerArtikelKatalog.detail() sollte den korrekten Vorrat des Artikels zurückgeben",
				Quantity.of(0), model.get("quantity"));
	}

	@Test
	public void erstelleNeuTest() {
		ArtikelErstellFormular formNeu = new ArtikelErstellFormular();
		BindingResult ergebnis = new DirectFieldBindingResult(formNeu, "formNeu");
		formNeu.setName("");
		formNeu.setBeschreibung("Beschreibung");
		formNeu.setBild(MMpFile);
		formNeu.setPreis("1.99");
		ergebnis.addError(new FieldError("formular", "Name", "Der Name darf nicht leer sein."));
		String retString = testController.erstelleNeu(formNeu, ergebnis);
		assertEquals("Shop/artikelErstellen", retString);

		inventar.deleteAll();
		katalog.deleteAll();

		formNeu = new ArtikelErstellFormular();
		ergebnis = new DirectFieldBindingResult(formNeu, "formNeu");
		formNeu.setName("Name");
		formNeu.setBeschreibung("Beschreibung");
		formNeu.setBild(MMpFile);
		formNeu.setPreis("1.99");
		retString = testController.erstelleNeu(formNeu, ergebnis);
		assertTrue("Shop/artikelErstellen", retString.startsWith("redirect:/artikel/"));
		retString = retString.replace("redirect:/artikel/", "");
		int i = 0;
		for (Artikel a : katalog.findAll()) {
			i++;
			assertEquals(a.getId().toString(), retString);
			assertEquals(formNeu.getName(), a.getName());
			assertEquals("noimage", a.getBild());
			assertEquals(formNeu.getBeschreibung(), a.getBeschreibung());
			assertEquals(Money.of(1.99, EURO), a.getPrice());
		}
		assertEquals("ControllerArtikelKatalog.erstelleNeu() sollte nur eine Instanz des Artikels erstellen", 1, i);
	}

	@Test
	public void erstelleTest() {
		ExtendedModelMap model = new ExtendedModelMap();
		String retString = testController.erstelle(model);
		assertEquals("Shop/artikelErstellen", retString);
		assertNotNull(
				"ControllerArtikelKatalog.erstelle() sollte dem Model eine neue Instanz von ArtikelErstellformular hinzufügen",
				model.get("formular"));
		ArtikelErstellFormular form = (ArtikelErstellFormular) model.get("formular");
		assertNull(
				"ControllerArtikelKatalog.erstelle() sollte dem Model eine neue Instanz von ArtikelErstellformular hinzufügen",
				form.getBeschreibung());
		assertNull(
				"ControllerArtikelKatalog.erstelle() sollte dem Model eine neue Instanz von ArtikelErstellformular hinzufügen",
				form.getBild());
		assertNull(
				"ControllerArtikelKatalog.erstelle() sollte dem Model eine neue Instanz von ArtikelErstellformular hinzufügen",
				form.getPreis());
		assertNull(
				"ControllerArtikelKatalog.erstelle() sollte dem Model eine neue Instanz von ArtikelErstellformular hinzufügen",
				form.getName());
	}

	@Test
	public void bearbeiteExistierendTest() {
		ExtendedModelMap model = new ExtendedModelMap();
		String retString = testController.bearbeiteExistierend(testArtikel1, model);
		assertEquals("Shop/artikelBearbeiten", retString);
		assertNotNull(
				"ControllerArtikelKatalog.bearbeiteExistierend() sollte dem Model eine neue Instanz von ArtikelErstellformular hinzufügen",
				model.get("formular"));
		ArtikelErstellFormular form = (ArtikelErstellFormular) model.get("formular");
		assertNull(
				"ControllerArtikelKatalog.bearbeiteExistierend() sollte dem Model eine neue Instanz von ArtikelErstellformular hinzufügen",
				form.getBeschreibung());
		assertNull(
				"ControllerArtikelKatalog.bearbeiteExistierend() sollte dem Model eine neue Instanz von ArtikelErstellformular hinzufügen",
				form.getBild());
		assertNull(
				"ControllerArtikelKatalog.bearbeiteExistierend() sollte dem Model eine neue Instanz von ArtikelErstellformular hinzufügen",
				form.getPreis());
		assertNull(
				"ControllerArtikelKatalog.bearbeiteExistierend() sollte dem Model eine neue Instanz von ArtikelErstellformular hinzufügen",
				form.getName());
		assertNotNull(
				"ControllerArtikelKatalog.bearbeiteExistierend() sollte dem Model den übergebenen Artikel hinzufügen",
				model.get("formular"));
		assertEquals(
				"ControllerArtikelKatalog.bearbeiteExistierend() sollte dem Model den übergebenen Artikel hinzufügen",
				testArtikel1, model.get("artikel"));
	}

	@Test
	public void bearbeiteTest() {
		ArtikelErstellFormular formNeu = new ArtikelErstellFormular();
		BindingResult ergebnis = new DirectFieldBindingResult(formNeu, "formNeu");
		formNeu.setName("");
		formNeu.setBeschreibung("Beschreibung");
		formNeu.setBild(MMpFile);
		formNeu.setPreis("1.99");
		ergebnis.addError(new FieldError("formular", "Name", "Der Name darf nicht leer sein."));
		String retString = testController.bearbeite(testArtikel1, formNeu, ergebnis);
		assertEquals("Shop/artikelBearbeiten", retString);

		inventar.deleteAll();
		katalog.deleteAll();
		testArtikel1 = new Artikel("Fackel", "diesenBildnamengibtesnich", Money.of(preis1, EURO), "Einfache Fackel.");
		katalog.save(testArtikel1);
		inventar.save(new InventoryItem(testArtikel1, Quantity.of(10)));
		
		formNeu = new ArtikelErstellFormular();
		ergebnis = new DirectFieldBindingResult(formNeu, "formNeu");
		formNeu.setName("Name");
		formNeu.setBeschreibung("Beschreibung");
		formNeu.setBild(MMpFile);
		formNeu.setPreis("1.99");
		retString = testController.bearbeite(testArtikel1, formNeu, ergebnis);
		assertTrue("Shop/artikelErstellen", retString.startsWith("redirect:/artikel/"));
		retString = retString.replace("redirect:/artikel/", "");
		int i = 0;
		for (Artikel a : katalog.findAll()) {
			i++;
			assertEquals(a.getId().toString(), retString);
			assertEquals(formNeu.getName(), a.getName());
			assertEquals("diesenBildnamengibtesnich", a.getBild());
			assertEquals(formNeu.getBeschreibung(), a.getBeschreibung());
			assertEquals(Money.of(1.99, EURO), a.getPrice());
		}
		assertEquals("ControllerArtikelKatalog.bearbeite() sollte nur eine Instanz des Artikels bearbeiten", 1, i);
	}

	@Test
	public void loeschenTest() {
		String retString = testController.loeschen(testArtikel1);
		assertEquals("redirect:/inventarverwaltung", retString);
		int i = 0;
		for (InventoryItem invItem : inventar.findAll()) {
			i++;
			if (invItem.getProduct().equals(testArtikel1)) {
				fail("ControllerArtikelKatalog.loeschen() sollte den Artikel tatsächlich loeschen");
			}
		}
		assertEquals("ControllerArtikelKatalog.loeschen() sollte den Artikel tatsächlich loeschen", 1, i);
		i = 0;
		for (Artikel a : katalog.findAllListed()) {
			i++;
			if (a.equals(testArtikel1)) {
				fail("ControllerArtikelKatalog.loeschen() sollte den Artikel tatsächlich loeschen");
			}
		}
		assertEquals("ControllerArtikelKatalog.loeschen() sollte den Artikel tatsächlich loeschen", 1, i);
	}

}
