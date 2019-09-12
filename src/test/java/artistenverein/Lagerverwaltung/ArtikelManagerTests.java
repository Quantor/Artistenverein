package artistenverein.Lagerverwaltung;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.salespointframework.core.Currencies.EURO;

import java.util.Random;

import org.javamoney.moneta.Money;
import org.junit.Before;
import org.junit.Test;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;

import artistenverein.AbstractIntegrationTests;

public class ArtikelManagerTests extends AbstractIntegrationTests {

	@Autowired
	private ArtikelKatalog katalog;

	@Autowired
	private Inventory<InventoryItem> inventar;

	private static Artikel artikelFac;
	private static Artikel artikelPoi;
	private final static Number preis1 = 9.99;
	private final static Number preis2 = 14.99;

	private static ArtikelManager testArtikelManager;
	
	private static MockMultipartFile MMpFileFac;
	private static MockMultipartFile MMpFilePoi;
	
	private static ArtikelErstellFormular formFac;
	private static ArtikelErstellFormular formPoi;

	@Before
	public void setUp() {
		testArtikelManager = new ArtikelManager(katalog, inventar);
		artikelFac = new Artikel("Fackel_test", "noimage", Money.of(preis1, EURO), "Einfache Fackel.");
		artikelPoi = new Artikel("Pois_test", "noimage", Money.of(preis2, EURO), "Feuer-Poi");
		
		byte[] b = new byte[20];
		Random rand = new Random();
		rand.nextBytes(b);
		MMpFileFac = new MockMultipartFile("fac_test.jpg", b);
		rand.nextBytes(b);
		MMpFilePoi = new MockMultipartFile("poi_test.jpg", b);
		
		formFac = new ArtikelErstellFormular();
		formFac.setBeschreibung(artikelFac.getBeschreibung());
		formFac.setBild(MMpFileFac);
		formFac.setName(artikelFac.getName());
		formFac.setPreis(preis1.toString());
		
		formPoi = new ArtikelErstellFormular();
		formPoi.setBeschreibung(artikelPoi.getBeschreibung());
		formPoi.setBild(MMpFilePoi);
		formPoi.setName(artikelPoi.getName());
		formPoi.setPreis(preis2.toString());
		inventar.deleteAll();
		katalog.deleteAll();
	}

	@Test
	public void ArtikelManagerErstellTest() {
		try {
			ArtikelManager test = new ArtikelManager(null, inventar);
			test.loesche(null);
			fail("ArtikelManager.ArtikelManager() sollte keinen Nullpointer akzeptieren im Argument katalog");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "katalog darf nicht null sein!");
		}

		try {
			ArtikelManager test = new ArtikelManager(katalog, null);
			test.loesche(null);
			fail("ArtikelManager.ArtikelManager() sollte keinen Nullpointer akzeptieren im Argument inventar");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "inventar darf nicht null sein!");
		}
	}
	
	@Test
	public void erstelleArtikelTest() {
		try {
			testArtikelManager.erstelleArtikel(null);
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "formular darf nicht null sein!");
		}
		Artikel test = testArtikelManager.erstelleArtikel(formFac);
		assertEquals(artikelFac.getName(), test.getName());
		assertEquals(artikelFac.getBild(), test.getBild());
		assertEquals(artikelFac.getBeschreibung(), test.getBeschreibung());
		assertEquals(artikelFac.getPrice(), test.getPrice());
		for (Artikel a : katalog.findAll()) {
			assertEquals(artikelFac.getName(), a.getName());
			assertEquals(artikelFac.getBild(), a.getBild());
			assertEquals(artikelFac.getBeschreibung(), a.getBeschreibung());
			assertEquals(artikelFac.getPrice(), a.getPrice());
		}
		for (InventoryItem invItem : inventar.findAll()) {
			Artikel a = (Artikel) invItem.getProduct();
			assertEquals(artikelFac.getName(), a.getName());
			assertEquals(artikelFac.getBild(), a.getBild());
			assertEquals(artikelFac.getBeschreibung(), a.getBeschreibung());
			assertEquals(artikelFac.getPrice(), a.getPrice());
		}
		
		inventar.deleteAll();
		katalog.deleteAll();
		
		test = testArtikelManager.erstelleArtikel(formPoi);
		assertEquals(artikelPoi.getName(), test.getName());
		assertEquals(artikelPoi.getBild(), test.getBild());
		assertEquals(artikelPoi.getBeschreibung(), test.getBeschreibung());
		assertEquals(artikelPoi.getPrice(), test.getPrice());
		for (Artikel a : katalog.findAll()) {
			assertEquals(artikelPoi.getName(), a.getName());
			assertEquals(artikelPoi.getBild(), a.getBild());
			assertEquals(artikelPoi.getBeschreibung(), a.getBeschreibung());
			assertEquals(artikelPoi.getPrice(), a.getPrice());
		}
		for (InventoryItem invItem : inventar.findAll()) {
			Artikel a = (Artikel) invItem.getProduct();
			assertEquals(artikelPoi.getName(), a.getName());
			assertEquals(artikelPoi.getBild(), a.getBild());
			assertEquals(artikelPoi.getBeschreibung(), a.getBeschreibung());
			assertEquals(artikelPoi.getPrice(), a.getPrice());
		}
		testArtikelManager.erstelleArtikel(formFac);
		int i = 0;
		for (Artikel a : katalog.findAll()) {
			a.getId();
			i++;
		}
		assertEquals("Der Katalog sollte genau zwei Elemente enthalten nachdem zwei Elemente hinzugefügt wurden", 2, i);
		i = 0;
		for (InventoryItem inv : inventar.findAll()) {
			inv.getId();
			i++;
		}
		assertEquals("Das Inventar sollte genau zwei Elemente enthalten nachdem zwei Elemente hinzugefügt wurden", 2, i);
		inventar.deleteAll();
		katalog.deleteAll();
	}

	@Test
	public void bearbeiteArtikelTest() {
		try {
			testArtikelManager.bearbeiteArtikel(null, formFac);
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "artikel darf nicht null sein!");
		}
		
		try {
			testArtikelManager.bearbeiteArtikel(artikelFac, null);
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "formular darf nicht null sein!");
		}
		
		inventar.deleteAll();
		katalog.deleteAll();
		Artikel test1 = testArtikelManager.erstelleArtikel(formFac);
		Artikel test2 = testArtikelManager.erstelleArtikel(formPoi);
		
		test1.setBild("diesesbildgibtesnicht");
		katalog.save(test1);
		test2.setBild("diesesbildgibtesnicht");
		katalog.save(test2);
		
		formFac.setName("NeuerFackelname");
		formFac.setBeschreibung("FackelBeschreibungTestString");
		formFac.setPreis(preis2.toString());
		
		test1 = testArtikelManager.bearbeiteArtikel(test1, formFac);
		assertEquals("NeuerFackelname", test1.getName());
		assertEquals("diesesbildgibtesnicht", test1.getBild());
		assertEquals("FackelBeschreibungTestString", test1.getBeschreibung());
		assertEquals(Money.of(preis2, EURO), test1.getPrice());
		
		formPoi.setPreis(preis1.toString());
		test2 = testArtikelManager.bearbeiteArtikel(test2, formPoi);
		assertEquals(artikelPoi.getName(), test2.getName());
		assertEquals("diesesbildgibtesnicht", test2.getBild());
		assertEquals(artikelPoi.getBeschreibung(), test2.getBeschreibung());
		assertEquals(Money.of(preis1, EURO), test2.getPrice());
		
	}
	
	@Test
	public void loescheTest() {
		try {
			testArtikelManager.loesche(null);
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "artikel darf nicht null sein!");
		}
		
		inventar.deleteAll();
		katalog.deleteAll();
		Artikel test1 = testArtikelManager.erstelleArtikel(formFac);
		Artikel test2 = testArtikelManager.erstelleArtikel(formPoi);
		
		test1.setBild("diesesbildgibtesnicht");
		katalog.save(test1);
		test2.setBild("diesesbildgibtesnicht");
		katalog.save(test2);
		
		testArtikelManager.loesche(test1);
		int i = 0;
		for (Artikel a : katalog.findAllListed()) {
			a.getId();
			i++;
		}
		assertEquals("Der Katalog sollte genau ein Element enthalten nachdem ein Element von zweien gelöscht wurde", 1, i);
		i = 0;
		for (InventoryItem inv : inventar.findAll()) {
			inv.getId();
			i++;
		}
		assertEquals("Das Inventar sollte genau ein Element enthalten nachdem ein Element von zweien gelöscht wurde", 1, i);
		
		testArtikelManager.loesche(test2);
		i = 0;
		for (Artikel a : katalog.findAllListed()) {
			a.getId();
			i++;
		}
		assertEquals("Der Katalog sollte kein Element enthalten nachdem alle Elemente gelöscht wurden", 0, i);
		i = 0;
		for (InventoryItem inv : inventar.findAll()) {
			inv.getId();
			i++;
		}
		assertEquals("Das Inventar sollte kein Element enthalten nachdem alle Elemente gelöscht wurden", 0, i);
	}

}
