package artistenverein.Veranstaltungen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.salespointframework.core.Currencies.EURO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.javamoney.moneta.Money;
import org.junit.Before;
import org.junit.Test;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ExtendedModelMap;

import artistenverein.AbstractIntegrationTests;
import artistenverein.Lagerverwaltung.Artikel;
import artistenverein.Lagerverwaltung.ArtikelKatalog;
import artistenverein.Personenverwaltung.RepositoryUser;
import artistenverein.Personenverwaltung.User;
import artistenverein.Veranstaltungen.Bewertung;
import artistenverein.Veranstaltungen.ControllerVeranstaltungen;
import artistenverein.Veranstaltungen.EntityVeranstaltung;
import artistenverein.Veranstaltungen.Zusatzkosten;
import artistenverein.Veranstaltungen.ZusatzkostenRepository;
import artistenverein.Veranstaltungen.EntityVeranstaltung.VeranstaltungsType;

public class ControllerVeranstaltungenTest extends AbstractIntegrationTests {
	
	@Autowired 
	private ArtikelKatalog artikelKatalog;
	@Autowired
	private ZusatzkostenRepository zusatzkostenRepository;
	@Autowired
	private Inventory<InventoryItem> inventar;
	@Autowired
	private ControllerVeranstaltungen testController;
	@Autowired
	private RepositoryUser userRepository;
	
	private static  Artikel artikel1; 
	private static Artikel artikel2;
	private static Zusatzkosten zusatzkosten;
	private List<Zusatzkosten> zusatzkostenListe = new ArrayList<>();
	private final Set<Artikel> artikelset = new HashSet<>();
	private final LocalDateTime start = LocalDateTime.of(2017, 12, 24, 19, 30);
	private final LocalDateTime end = LocalDateTime.of(2017, 12, 31, 23, 59);
	private Set<UserAccount> artistengruppe = new HashSet<>();
	User artist = new User(new UserAccount());
	private final EntityVeranstaltung veranstaltung = new EntityVeranstaltung("TestVeranstaltung", Money.of(50, EURO),
			"Test Beschreibung", 120, VeranstaltungsType.WORKSHOP, start, end, artistengruppe, 1, "");
	private EntityVeranstaltung veranstaltung2;
	

	
	@Before
	public void setUp() {
		
		zusatzkostenRepository.deleteAll();
		zusatzkosten = new Zusatzkosten(30);
		zusatzkostenRepository.save(zusatzkosten);
		zusatzkostenListe.add(zusatzkosten);
		inventar.deleteAll();
		testController = new ControllerVeranstaltungen(artikelKatalog,
				zusatzkostenRepository);
		artikel1 =  new Artikel("Test Artikel", "Test Bild", 
				Money.of(20,EURO), "Test Beschreibung");
		artikel2 = new Artikel("Test Artikel2", "Test Bild", 
				Money.of(20,EURO), "Test Beschreibung");
		artikelKatalog.deleteAll();
		artikelKatalog.save(artikel1);
		artikelKatalog.save(artikel2);
		
		for(User userRepo: userRepository.findAll()) {
			if(userRepo.getUserAccount().getUsername().equals("genji")) {
				artist = userRepo;
			}
		}
		userRepository.save(artist);
		
		Set<UserAccount> artistSet = new HashSet<>();
		artistSet.add(artist.getUserAccount());
		veranstaltung2 = new EntityVeranstaltung("TestVeranstaltung", Money.of(50, EURO),
				"Test Beschreibung", 120, VeranstaltungsType.WORKSHOP, start, end, artistSet, 1, "");
		artikelset.add(artikel1);
		veranstaltung.addArtikel(artikel1);
		veranstaltung2.addArtikel(artikel1);
		
	}
	
	
	@Test
	public void erstelleControllerVeranstaltungTest() {
		try {
			new ControllerVeranstaltungen(null, zusatzkostenRepository);
			fail("ControllerVeranstaltungen() sollte eine IllegalArgumentException werfen, "
					+ "wenn das Argument artikelKatalog null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals("artikelKatalog darf nicht null sein!", e.getMessage());
		}
		
		try {
			new ControllerVeranstaltungen(artikelKatalog, null);
			fail("ControllerVeranstaltungen() sollte eine IllegalArgumentException werfen, "
					+ "wenn das Argument zusatzkostenRepository null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals("zusatzkostenRepository darf nicht null sein!", e.getMessage());
		}
	}
	
	@Test
	public void detail() {
		ExtendedModelMap model = new ExtendedModelMap();
		UserAccount user = new UserAccount();
		for(User userRepo: userRepository.findAll()) {
			if(userRepo.getUserAccount().getUsername().equals("genji")) {
				user = userRepo.getUserAccount();
			}
		}
		String retString = testController.detail(veranstaltung2, model, Optional.of(user));
		assertEquals("Veranstaltungen/detail", retString);
		assertEquals("ControllerVeranstaltungen.detail() sollte die korrekte Verantaltung"
				+ "wiedergeben", veranstaltung2,(EntityVeranstaltung) model.get("veranstaltung"));
		assertEquals("ControllerVeranstaltungen.details() sollte die korrekten Artikel zur Veranstaltung"
				+ "wiedergeben", artikelset, model.get("artikellist"));
		assertEquals("ControllerVeranstaltungen.details() sollte die korrekten Zusatzkosten"
				+ "wiedergeben", zusatzkostenListe , model.get("zusatzkosten"));
		assertEquals("ControllerVeranstaltungen.details() sollte beim korrekten Artisten 1"
				+ "wiedergeben", 1 , model.get("artist"));
		
		ExtendedModelMap model2 = new ExtendedModelMap();
		testController.detail(veranstaltung, model2, Optional.of(user));
		assertEquals("ControllerVeranstaltungen.details() sollte beim falschen Artisten null (Veranstworlich für "
				+ "diese Veranstaltung)wiedergeben", null , model2.get("artist"));
		assertEquals("ControllerVeranstaltungen.details() sollte beim falsche Kunden null(noch nicht bewertet)"
				+ "wiedergeben", null , model2.get("darfNochBewerten"));
		
		for(User userRepo: userRepository.findAll()) {
			if(userRepo.getUserAccount().getUsername().equals("genji")) {
				user = userRepo.getUserAccount();
			}
		}
		ExtendedModelMap model3 = new ExtendedModelMap();
		veranstaltung.addBewertung(new Bewertung("TestText", 3, start, user));
		testController.detail(veranstaltung, model3, Optional.of(user));
		assertEquals("ControllerVeranstaltungen.details() sollte beim richtigen Kunden 1(schon bewertet)"
				+ "wiedergeben", 1 , model3.get("darfNochBewerten"));
	}
	
	@Test
	public void detailsTest() {
		//sollte leere BuchungsValidation erzeugen?
		ExtendedModelMap model = new ExtendedModelMap();
		String retString = testController.details(model, veranstaltung);
		assertEquals("Veranstaltungen/formVeranstaltungBuchen", retString);
		assertEquals("ControllerVeranstaltungen.details() sollte die korrekte Verantaltung"
				+ "wiedergeben", veranstaltung,(EntityVeranstaltung) model.get("veranstaltung"));
		assertEquals("ControllerVeranstaltungen.details() sollte die korrekten Artikel zur Veranstaltung"
				+ "wiedergeben", artikelset, model.get("artikellist"));
		
	}
	
	@Test
	public void vbearbeiten() {
		//sollte leere BuchungsValidation erzeugen?
		//Berechtigung prüfen?
		ExtendedModelMap model = new ExtendedModelMap();
		String retString = testController.vbearbeiten(veranstaltung, model);
		
		assertEquals("Veranstaltungen/editFormular", retString);
		assertEquals("ControllerVeranstaltungen.vbearbeiten sollte die korrekte Verantaltung"
				+ "wiedergeben", veranstaltung,(EntityVeranstaltung) model.get("veranstaltung"));
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void artikel() {
		ExtendedModelMap model = new ExtendedModelMap();

		String retString = testController.artikel(model, veranstaltung);
		//String title = "Artikel-Katalog";
		
		assertEquals("Veranstaltungen/addArtikel", retString);
		assertEquals("ControllerVeranstaltungen.artikel sollte die korrekte Verantaltung"
				+ "wiedergeben", veranstaltung,(EntityVeranstaltung) model.get("veranstaltung"));
		/*assertEquals("ControllerVeranstaltungen.artikel sollte den korrekten Titel"
				+ "wiedergeben", title,(String) model.get("title"));*/
		assertNotNull("testController.artikel() sollte die vorhandenen Artikel auflisten", model.get("katalog"));
		assertEquals("ControllerVeranstaltungen.artikel sollte den korrekten ArtikelKatalog "
				+ "wiedergeben", artikelKatalog.findAll(), model.get("katalog"));
		int i = 0;
		for (Artikel artikel : (Iterable<Artikel>) model.get("katalog")) {
			i++;
			artikel.getId();
		}
		assertEquals("ControllerInventar.vorrat() sollte alle Artikel zurückgeben", 
				2, i);
	}
	
}