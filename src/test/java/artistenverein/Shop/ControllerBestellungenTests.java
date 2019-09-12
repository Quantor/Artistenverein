package artistenverein.Shop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.salespointframework.core.Currencies.EURO;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.javamoney.moneta.Money;
import org.junit.Before;
import org.junit.Test;
import org.salespointframework.core.Streamable;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.order.Cart;
import org.salespointframework.order.CartItem;
import org.salespointframework.order.Order;
import org.salespointframework.order.OrderManager;
import org.salespointframework.order.OrderStatus;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.time.Interval;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ExtendedModelMap;

import artistenverein.AbstractIntegrationTests;
import artistenverein.Lagerverwaltung.Artikel;
import artistenverein.Lagerverwaltung.ArtikelKatalog;
import artistenverein.Lagerverwaltung.InventarKonfiguration;
import artistenverein.Lagerverwaltung.KonfigurationsRepository;
import artistenverein.Veranstaltungen.Buchung;
import artistenverein.Veranstaltungen.BuchungRepository;
import artistenverein.Veranstaltungen.EntityVeranstaltung;
import artistenverein.Veranstaltungen.VeranstaltungsKatalog;

public class ControllerBestellungenTests extends AbstractIntegrationTests {

	@Autowired
	private ControllerBestellungen testController;
	@Autowired
	private KonfigurationsRepository konfRep;
	@Autowired
	private OrderManager<Order> orderManager;
	@Autowired
	private BuchungRepository buchungRep;
	@Autowired
	private Inventory<InventoryItem> inventar;
	@Autowired
	private UserAccountManager accounts;
	@Autowired
	private ArtikelKatalog aKatalog;
	@Autowired
	private VeranstaltungsKatalog vKatalog;
	private static UserAccount testAccount;
	private static RabattCart testCart;
	private static Artikel testArtikel1;
	private static Artikel testArtikel2;
	private static Artikel testArtikel3;
	private final static Number preis1 = 9.99;
	private final static Number preis2 = 14.99;
	private static final int DEFAULT_MENGE = 10;
	private static final int DEFAULT_RABATT = 20;

	@Before
	public void setUp() {
		konfRep.deleteAll();
		this.konfRep
				.save(new InventarKonfiguration(DEFAULT_MENGE, InventarKonfiguration.KonfigurationsTyp.MINDEST_MENGE));
		this.konfRep.save(new InventarKonfiguration(DEFAULT_RABATT, InventarKonfiguration.KonfigurationsTyp.RABATT));

		testArtikel1 = new Artikel("Fackel", "fac", Money.of(preis1, EURO), "Einfache Fackel.");
		testArtikel2 = new Artikel("Pois", "poi", Money.of(preis2, EURO), "Feuer-Poi");
		testArtikel3 = new Artikel("Pois2", "poi2", Money.of(preis2, EURO), "Feuer-Poi2");
		testArtikel3.setGelisted(false);
		testCart = (RabattCart) testController.initialisiereWarenkorb();
		testAccount = accounts.findByUsername("hans").get();

		aKatalog.deleteAll();
		inventar.deleteAll();
		aKatalog.save(testArtikel1);
		aKatalog.save(testArtikel2);
		aKatalog.save(testArtikel3);
		inventar.save(new InventoryItem(testArtikel1, Quantity.of(10)));
		inventar.save(new InventoryItem(testArtikel2, Quantity.of(10)));
		buchungRep.deleteAll();
	}

	@Test
	public void ControllerBestellungenErstellTest() {
		try {
			ControllerBestellungen test = new ControllerBestellungen(null, konfRep, buchungRep, inventar);
			test.bearbeiten();
			fail("ControllerBestellungen.ControllerBestellungen() sollte keine Nullpointer akzeptieren");
		} catch (IllegalArgumentException e) {
			assertEquals("orderManager darf nicht null sein!", e.getMessage());
		}

		try {
			ControllerBestellungen test = new ControllerBestellungen(orderManager, null, buchungRep, inventar);
			test.bearbeiten();
			fail("ControllerBestellungen.ControllerBestellungen() sollte keine Nullpointer akzeptieren");
		} catch (IllegalArgumentException e) {
			assertEquals("konfigurationsRep darf nicht null sein!", e.getMessage());
		}

		try {
			ControllerBestellungen test = new ControllerBestellungen(orderManager, konfRep, null, inventar);
			test.bearbeiten();
			fail("ControllerBestellungen.ControllerBestellungen() sollte keine Nullpointer akzeptieren");
		} catch (IllegalArgumentException e) {
			assertEquals("buchungRep darf nicht null sein!", e.getMessage());
		}

		try {
			ControllerBestellungen test = new ControllerBestellungen(orderManager, konfRep, buchungRep, null);
			test.bearbeiten();
			fail("ControllerBestellungen.ControllerBestellungen() sollte keine Nullpointer akzeptieren");
		} catch (IllegalArgumentException e) {
			assertEquals("inventar darf nicht null sein!", e.getMessage());
		}
	}

	@Test
	public void initialisiereWarenkorbTest() {
		Cart c = testController.initialisiereWarenkorb();
		assertNotNull("ControllerBestellungen.intitalisiereWarenkorb() sollte eine neue Instanz von Cart zurückgeben!",
				c);
		RabattCart rc = (RabattCart) c;
		assertFalse("ControllerBestellungen.intitalisiereWarenkorb() sollte eine neue Instanz von Cart zurückgeben!",
				rc.getRabattStatus());
		assertEquals("ControllerBestellungen.intitalisiereWarenkorb() sollte eine neue Instanz von Cart zurückgeben!",
				Money.of(0.00, EURO), rc.getPrice());
	}

	@Test
	public void artikelHinzufuegenTest() {
		String retString = testController.artikelHinzufuegen(testArtikel1, "1", testCart, Optional.of(testAccount));
		assertEquals("redirect:/warenkorb", retString);
		assertEquals(Money.of(preis1, EURO), testCart.getPrice());

		testController.artikelHinzufuegen(testArtikel1, "1", testCart, Optional.of(testAccount));
		assertEquals(Money.of(19.98, EURO), testCart.getPrice());

		testCart.clear();
		testController.artikelHinzufuegen(testArtikel1, "asd", testCart, Optional.of(testAccount));
		assertEquals(Money.of(9.99, EURO), testCart.getPrice());

		testCart.clear();
		testController.artikelHinzufuegen(testArtikel1, "200", testCart, Optional.of(testAccount));
		assertEquals(Money.of(9.99, EURO), testCart.getPrice());

		testCart.clear();
		testController.artikelHinzufuegen(testArtikel1, "-1", testCart, Optional.of(testAccount));
		assertEquals(Money.of(9.99, EURO), testCart.getPrice());

		testCart.clear();
		testController.artikelHinzufuegen(testArtikel1, "1", testCart, Optional.empty());
		assertEquals(Money.of(9.99, EURO), testCart.getPrice());
	}

	@Test
	public void warenkorbAnzeigenTest() {
		ExtendedModelMap model = new ExtendedModelMap();
		String retString = testController.warenkorb(model, testCart, Optional.of(testAccount));
		assertEquals("Shop/warenkorb", retString);
		assertFalse("ControllerBestellungen.warenkorb() sollte den korrekten Wert in das übergebene model speichern!",
				(boolean) model.get("fehler"));
		assertEquals("ControllerBestellungen.warenkorb() sollte den korrekten Wert in das übergebene model speichern!",
				20, (int) model.get("rabatt"));

		model = new ExtendedModelMap();
		retString = testController.warenkorb(model, testCart, Optional.empty());
		assertEquals("Shop/warenkorb", retString);
		assertFalse("ControllerBestellungen.warenkorb() sollte den korrekten Wert in das übergebene model speichern!",
				(boolean) model.get("fehler"));
		assertEquals("ControllerBestellungen.warenkorb() sollte den korrekten Wert in das übergebene model speichern!",
				20, (int) model.get("rabatt"));
	}

	@Test
	public void warenkorbKaufenTest() {
		ExtendedModelMap model = new ExtendedModelMap();
		String retString = testController.warenkorb(model, testCart, Optional.of(testAccount), "false");
		assertEquals("Shop/warenkorb", retString);
		assertFalse("ControllerBestellungen.warenkorb() sollte den korrekten Wert in das übergebene model speichern!",
				(boolean) model.get("fehler"));
		assertEquals("ControllerBestellungen.warenkorb() sollte den korrekten Wert in das übergebene model speichern!",
				20, (int) model.get("rabatt"));

		testCart.addOrUpdateItem(testArtikel1, Quantity.of(10000));
		model = new ExtendedModelMap();
		retString = testController.warenkorb(model, testCart, Optional.of(testAccount), "true");
		assertEquals("Shop/warenkorb", retString);
		assertTrue("ControllerBestellungen.warenkorb() sollte den korrekten Wert in das übergebene model speichern!",
				(boolean) model.get("fehler"));
		assertFalse("ControllerBestellungen.warenkorb() sollte den korrekten Wert in das übergebene model speichern!",
				(boolean) model.get("success"));
		assertEquals("ControllerBestellungen.warenkorb() sollte den korrekten Wert in das übergebene model speichern!",
				20, (int) model.get("rabatt"));

		try {
			TimeUnit.MILLISECONDS.sleep(200);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		Interval.IntervalBuilder orderIntervalBuilder = Interval.from(LocalDateTime.now());
		try {
			TimeUnit.MILLISECONDS.sleep(200);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		testCart.clear();
		testCart.addOrUpdateItem(testArtikel1, Quantity.of(1));
		model = new ExtendedModelMap();
		retString = testController.warenkorb(model, testCart, Optional.of(testAccount), "true");
		assertEquals("Shop/warenkorb", retString);
		assertFalse("ControllerBestellungen.warenkorb() sollte den korrekten Wert in das übergebene model speichern!",
				(boolean) model.get("fehler"));
		assertTrue("ControllerBestellungen.warenkorb() sollte den korrekten Wert in das übergebene model speichern!",
				(boolean) model.get("success"));
		assertEquals("ControllerBestellungen.warenkorb() sollte den korrekten Wert in das übergebene model speichern!",
				20, (int) model.get("rabatt"));
		try {
			TimeUnit.MILLISECONDS.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Interval orderInterval = orderIntervalBuilder.to(LocalDateTime.now());
		int i = 0;
		for (Order o : orderManager.findBy(orderInterval)) {
			i++;
			assertEquals("Die Bestellung sollte dem Wert des Warenkorbs entsprechen!", Money.of(9.99, EURO),
					o.getTotalPrice());
		}
		assertEquals("Es sollte nur eine Bestellung getätigt worden sein!", 1, i);

		orderIntervalBuilder = Interval.from(LocalDateTime.now());
		try {
			TimeUnit.MILLISECONDS.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		testCart.clear();
		testCart.addOrUpdateItem(testArtikel1, Quantity.of(1));
		model = new ExtendedModelMap();
		retString = testController.warenkorb(model, testCart, Optional.empty(), "true");
		assertEquals("Shop/warenkorb", retString);
		assertFalse("ControllerBestellungen.warenkorb() sollte den korrekten Wert in das übergebene model speichern!",
				(boolean) model.get("fehler"));
		assertFalse("ControllerBestellungen.warenkorb() sollte den korrekten Wert in das übergebene model speichern!",
				(boolean) model.get("success"));
		assertEquals("ControllerBestellungen.warenkorb() sollte den korrekten Wert in das übergebene model speichern!",
				20, (int) model.get("rabatt"));
		try {
			TimeUnit.MILLISECONDS.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		orderInterval = orderIntervalBuilder.to(LocalDateTime.now());
		i = 0;
		for (Order o : orderManager.findBy(orderInterval)) {
			o.getId();
			i++;
			fail("Es sollte keine Bestellung getätigt worden sein!");
		}
		assertEquals("Es sollte keine Bestellung getätigt worden sein!", 0, 0);
		
		try {
			TimeUnit.MILLISECONDS.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		orderIntervalBuilder = Interval.from(LocalDateTime.now());
		try {
			TimeUnit.MILLISECONDS.sleep(200);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		testCart.clear();
		testCart.setRabattStatus(true);
		testCart.addOrUpdateItem(testArtikel1, Quantity.of(1));
		testAccount = accounts.findByUsername("boss").get();
		model = new ExtendedModelMap();
		retString = testController.warenkorb(model, testCart, Optional.of(testAccount), "true");
		assertEquals("Shop/warenkorb", retString);
		assertFalse("ControllerBestellungen.warenkorb() sollte den korrekten Wert in das übergebene model speichern!",
				(boolean) model.get("fehler"));
		assertFalse("ControllerBestellungen.warenkorb() sollte den korrekten Wert in das übergebene model speichern!",
				(boolean) model.get("success"));
		assertEquals("ControllerBestellungen.warenkorb() sollte den korrekten Wert in das übergebene model speichern!",
				20, (int) model.get("rabatt"));
		try {
			TimeUnit.MILLISECONDS.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		orderInterval = orderIntervalBuilder.to(LocalDateTime.now());
		i = 0;
		for (Order o : orderManager.findBy(orderInterval)) {
			o.getId();
			i++;
			fail("Es sollte keine Bestellung getätigt worden sein!");
		}
		assertEquals("Es sollte keine Bestellung getätigt worden sein!", 0, 0);
		assertFalse("ControllerBestellungen.rabattAnwenden() sollte den RabattStatus entsprechend setzen",
				testCart.getRabattStatus());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void bestellungenTest() {
		int i = 0;
		for (Order o : orderManager.findBy(testAccount)) {
			o.getId();
			i++;
		}
		ExtendedModelMap model = new ExtendedModelMap();
		String retString = testController.bestellungen(model, Optional.of(testAccount));
		assertEquals("Shop/bestellungen", retString);
		assertNotNull("ControllerBestellungen.bestellungen() sollte die Bestellungen des Nutzers dem Model hinzufügen",
				model.get("ordersCompleted"));
		int j = 0;
		for (Order o : (Streamable<Order>) model.get("ordersCompleted")) {
			j++;
			assertEquals("ControllerBestellungen.bestellungen() sollte nur Bestellungen des Nutzers zurückgeben",
					testAccount, o.getUserAccount());
		}
		assertEquals(
				"ControllerBestellungen.bestellungen() sollte die korrekte Anzahl Bestellungen dem Model hinzufügen", i,
				j);

		UserAccount boss = accounts.findByUsername("boss").get();
		i = 0;
		for (Order o : orderManager.findBy(OrderStatus.COMPLETED)) {
			o.getId();
			i++;
		}
		model = new ExtendedModelMap();
		retString = testController.bestellungen(model, Optional.of(boss));
		assertEquals("Shop/bestellungen", retString);
		assertNotNull("ControllerBestellungen.bestellungen() sollte die Bestellungen des Nutzers dem Model hinzufügen",
				model.get("ordersCompleted"));
		j = 0;
		for (Order o : (Streamable<Order>) model.get("ordersCompleted")) {
			o.getId();
			j++;
		}
		assertEquals(
				"ControllerBestellungen.bestellungen() sollte die korrekte Anzahl Bestellungen dem Model hinzufügen", i,
				j);

		UserAccount genji = accounts.findByUsername("genji").get();
		model = new ExtendedModelMap();
		retString = testController.bestellungen(model, Optional.of(genji));
		assertEquals("redirect:/", retString);

		Optional<UserAccount> empty = Optional.empty();
		model = new ExtendedModelMap();
		retString = testController.bestellungen(model, empty);
		assertEquals("redirect:/", retString);
	}

	@Test
	public void detailTest() {
		Interval.IntervalBuilder orderIntervalBuilder = Interval.from(LocalDateTime.now());
		testCart.clear();
		testCart.addOrUpdateItem(testArtikel1, Quantity.of(1));
		ExtendedModelMap model = new ExtendedModelMap();
		testController.warenkorb(model, testCart, Optional.of(testAccount), "true");
		Interval orderInterval = orderIntervalBuilder.to(LocalDateTime.now());
		Order testOrder = null;
		for (Order o : orderManager.findBy(orderInterval)) {
			testOrder = o;
			break;
		}

		model = new ExtendedModelMap();
		String retString = testController.detail(testOrder, model, Optional.of(testAccount));
		assertEquals("Shop/bestellungDetail", retString);
		assertNotNull("ControllerBestellungen.bestellungen() sollte die Bestellung des Nutzers dem Model hinzufügen",
				model.get("order"));

		UserAccount boss = accounts.findByUsername("boss").get();
		model = new ExtendedModelMap();
		retString = testController.detail(testOrder, model, Optional.of(boss));
		assertEquals("Shop/bestellungDetail", retString);
		assertNotNull("ControllerBestellungen.bestellungen() sollte die Bestellung dem Model hinzufügen",
				model.get("order"));

		UserAccount genji = accounts.findByUsername("genji").get();
		model = new ExtendedModelMap();
		retString = testController.detail(testOrder, model, Optional.of(genji));
		assertEquals("redirect:/", retString);

		UserAccount dextermorgan = accounts.findByUsername("dextermorgan").get();
		model = new ExtendedModelMap();
		retString = testController.detail(testOrder, model, Optional.of(dextermorgan));
		assertEquals("redirect:/", retString);

		Optional<UserAccount> empty = Optional.empty();
		model = new ExtendedModelMap();
		retString = testController.detail(testOrder, model, empty);
		assertEquals("redirect:/", retString);
	}

	@Test
	public void leerenTest() {
		String retString = testController.leeren();
		assertEquals("redirect:/warenkorb", retString);

		testCart.clear();
		retString = testController.leeren(testCart);
		assertEquals("redirect:/warenkorb", retString);
		assertTrue("ControllerBestellungen.leeren() sollte den Warenkorb tatsächlich leeren", testCart.isEmpty());

		testCart.addOrUpdateItem(testArtikel1, Quantity.of(5));
		testCart.addOrUpdateItem(testArtikel2, Quantity.of(2));
		retString = testController.leeren(testCart);
		assertEquals("redirect:/warenkorb", retString);
		assertTrue("ControllerBestellungen.leeren() sollte den Warenkorb tatsächlich leeren", testCart.isEmpty());
	}

	@Test
	public void bearbeitenTest() {
		String retString = testController.bearbeiten();
		assertEquals("redirect:/warenkorb", retString);

		testCart.clear();
		testCart.addOrUpdateItem(testArtikel1, Quantity.of(5));
		CartItem cItem = null;
		for (CartItem i : testCart) {
			cItem = i;
		}

		retString = testController.bearbeiten(testArtikel1, cItem.getId(), "0", testCart);
		assertEquals("redirect:/warenkorb", retString);
		assertTrue("ControllerBestellungen.bearbeitem() sollte den Warenkorb tatsächlich bearbeiten",
				testCart.isEmpty());

		testCart.clear();
		testCart.addOrUpdateItem(testArtikel1, Quantity.of(5));
		cItem = null;
		for (CartItem i : testCart) {
			cItem = i;
		}

		retString = testController.bearbeiten(testArtikel1, cItem.getId(), "1", testCart);
		assertEquals("redirect:/warenkorb", retString);
		for (CartItem i : testCart) {
			cItem = i;
		}
		assertEquals("ControllerBestellungen.bearbeitem() sollte den Warenkorb tatsächlich bearbeiten", Quantity.of(1),
				cItem.getQuantity());

		retString = testController.bearbeiten(testArtikel1, cItem.getId(), "200", testCart);
		assertEquals("redirect:/warenkorb", retString);
		for (CartItem i : testCart) {
			cItem = i;
		}
		assertEquals("ControllerBestellungen.bearbeitem() sollte den Warenkorb tatsächlich bearbeiten", Quantity.of(10),
				cItem.getQuantity());

		retString = testController.bearbeiten(testArtikel1, cItem.getId(), "-1", testCart);
		assertEquals("redirect:/warenkorb", retString);
		for (CartItem i : testCart) {
			cItem = i;
		}
		assertEquals("ControllerBestellungen.bearbeitem() sollte den Warenkorb tatsächlich bearbeiten", Quantity.of(1),
				cItem.getQuantity());

		retString = testController.bearbeiten(testArtikel1, cItem.getId(), "asd", testCart);
		assertEquals("redirect:/warenkorb", retString);
		CartItem cItem2 = null;
		for (CartItem i : testCart) {
			cItem2 = i;
		}
		assertEquals("ControllerBestellungen.bearbeitem() sollte den Warenkorb tatsächlich bearbeiten",
				cItem.getQuantity(), cItem2.getQuantity());

		testCart.clear();
		testCart.addOrUpdateItem(testArtikel3, Quantity.of(5));
		retString = testController.bearbeiten(testArtikel3, cItem.getId(), "1", testCart);
		assertEquals("redirect:/warenkorb", retString);
		assertEquals(
				"ControllerBestellungen.ungelisteteArtikelEntfernen() sollte ungelistete Artikel tatsächlich entfernen",
				Money.of(0, EURO), testCart.getPrice());
	}

	@Test
	public void rabattAnwendenTest() {
		testCart.clear();
		testCart.addOrUpdateItem(testArtikel1, Quantity.of(1));
		ExtendedModelMap model = new ExtendedModelMap();
		testController.warenkorb(model, testCart, Optional.of(testAccount));
		assertFalse("ControllerBestellungen.rabattAnwenden() sollte den RabattStatus entsprechend setzen",
				testCart.getRabattStatus());

		UserAccount genji = accounts.findByUsername("genji").get();
		Set<UserAccount> artists = new HashSet<UserAccount>();
		artists.add(genji);
		EntityVeranstaltung neueV = new EntityVeranstaltung("test", Money.of(1.99, EURO), "test", 120,
				EntityVeranstaltung.VeranstaltungsType.WORKSHOP, LocalDateTime.now(), LocalDateTime.now().plusDays(20),
				artists, 1, "");
		vKatalog.save(neueV);
		Buchung neueB = new Buchung("Halle", LocalDateTime.now().plusDays(4), neueV, testAccount, Money.of(1.99, EURO),
				UUID.randomUUID());
		buchungRep.save(neueB);

		testCart.clear();
		testCart.addOrUpdateItem(testArtikel1, Quantity.of(1));
		model = new ExtendedModelMap();
		testController.warenkorb(model, testCart, Optional.of(testAccount));
		assertTrue("ControllerBestellungen.rabattAnwenden() sollte den RabattStatus entsprechend setzen",
				testCart.getRabattStatus());
	}
}
