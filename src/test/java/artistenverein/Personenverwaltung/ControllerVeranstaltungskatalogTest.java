package artistenverein.Personenverwaltung;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.salespointframework.core.Currencies.EURO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.javamoney.moneta.Money;
import org.junit.Before;
import org.junit.Test;
import org.salespointframework.time.BusinessTime;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.validation.FieldError;

import artistenverein.AbstractIntegrationTests;
import artistenverein.Personenverwaltung.Artistengruppe;
import artistenverein.Personenverwaltung.ControllerVeranstaltungskatalog;
import artistenverein.Personenverwaltung.RepositoryGruppen;
import artistenverein.Personenverwaltung.RepositoryUser;
import artistenverein.Personenverwaltung.User;
import artistenverein.Veranstaltungen.BuchungRepository;
import artistenverein.Veranstaltungen.EntityVeranstaltung;
import artistenverein.Veranstaltungen.FormNeueVeranstaltung;
import artistenverein.Veranstaltungen.VeranstaltungsKatalog;
import artistenverein.Veranstaltungen.VeranstaltungsManager;
import artistenverein.Veranstaltungen.Zusatzkosten;
import artistenverein.Veranstaltungen.ZusatzkostenRepository;
import artistenverein.Veranstaltungen.EntityVeranstaltung.VeranstaltungsType;
import artistenverein.Zeitverwaltung.ZeitRepository;
import artistenverein.Zeitverwaltung.Zeitverwaltung;

public class ControllerVeranstaltungskatalogTest extends AbstractIntegrationTests {
	
	@Autowired
	private VeranstaltungsKatalog veranstaltungsKatalog;
	@Autowired
	private BuchungRepository buchungRepository;
	@Autowired
	private RepositoryGruppen gruppenrepository;
	@Autowired
	private RepositoryUser userRepository;
	@Autowired
	private ZusatzkostenRepository zusatzkostenRepository;
	@Autowired
	private ZeitRepository zeitRepository;
	@Autowired
	private UserAccountManager userAccountManager;
	@Autowired
	private MessageSource messageSource;
	
	private BusinessTime businessTime;
	private VeranstaltungsManager veranstaltungsManager;
	private Zeitverwaltung zeitverwaltung;
	private EntityVeranstaltung veranstaltung1; 
	private EntityVeranstaltung veranstaltung2; 
	private final LocalDateTime start = LocalDateTime.of(LocalDateTime.now().getYear() + 1, 12, 24, 19, 30);
	private final LocalDateTime end = LocalDateTime.of(LocalDateTime.now().getYear() + 1, 12, 31, 23, 59);
	private Set<UserAccount> artistengruppe = new HashSet<>();
	private ControllerVeranstaltungskatalog testController;
	private User artist1 = new User(new UserAccount());
	private Artistengruppe gruppe1 = new Artistengruppe("TestGruppe");
	private Zusatzkosten zusatzkosten;
	
	@Before
	public void setUp() {
		veranstaltungsManager = new VeranstaltungsManager(veranstaltungsKatalog);
		zeitverwaltung = new Zeitverwaltung(zeitRepository, userAccountManager);
		
		testController = new ControllerVeranstaltungskatalog(veranstaltungsKatalog, 
				messageSource,businessTime,  veranstaltungsManager,  buchungRepository,
				 gruppenrepository, zeitverwaltung, zusatzkostenRepository);
		
		buchungRepository.deleteAll();
		veranstaltungsKatalog.deleteAll();
		gruppenrepository.deleteAll();
		zusatzkostenRepository.deleteAll();
		
		for(User userRepo: userRepository.findAll()) {
			if(userRepo.getUserAccount().getUsername().equals("genji")) {
				artist1 = userRepo;
			}
		}
		
		zusatzkosten = new Zusatzkosten(50);
		zusatzkostenRepository.save(zusatzkosten);
		gruppenrepository.save(gruppe1);
		gruppe1.addMitglied(artist1);
		veranstaltung1 = new EntityVeranstaltung("TestWorkshop", Money.of(50, EURO),
				"Test Beschreibung", 120, VeranstaltungsType.WORKSHOP, start, end, artistengruppe, 1, "");
		veranstaltung2 = new EntityVeranstaltung("TestShow", Money.of(50, EURO),
				"Test Beschreibung", 120, VeranstaltungsType.SHOW, start, end, artistengruppe, 1, "Tanzbären");
		
		veranstaltungsKatalog.save(veranstaltung1);
		veranstaltungsKatalog.save(veranstaltung2);
		
		
	}
	
	@Test
	public void erstelleControllerVeranstaltungsKatalogTest() {
		try {
			new ControllerVeranstaltungskatalog(null,  messageSource,
					 businessTime,  veranstaltungsManager,  buchungRepository,
					 gruppenrepository, zeitverwaltung,
					 zusatzkostenRepository);
			fail("ControllerVeranstaltungsKatalog() sollte eine IllegalArgumentException werfen, "
					+ "wenn das Argument veranstaltungskatalog null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals("veranstaltungsKatalog darf nicht null sein!", e.getMessage());
		}
		
		try {
			new ControllerVeranstaltungskatalog(veranstaltungsKatalog,  messageSource,
					 businessTime,  veranstaltungsManager,  null,
					 gruppenrepository, zeitverwaltung,
					 zusatzkostenRepository);
			fail("ControllerVeranstaltungsKatalog() sollte eine IllegalArgumentException werfen, "
					+ "wenn das Argument buchungRepository null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals("buchungRepository darf nicht null sein!", e.getMessage());
		}
		
		try {
			new ControllerVeranstaltungskatalog(veranstaltungsKatalog,  messageSource,
					 businessTime,  veranstaltungsManager,  buchungRepository,
					 null, zeitverwaltung,
					 zusatzkostenRepository);
			fail("ControllerVeranstaltungsKatalog() sollte eine IllegalArgumentException werfen, "
					+ "wenn das Argument gruppenRepository null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals("gruppenrepository darf nicht null sein!", e.getMessage());
		}
		
//		try {
//			new ControllerVeranstaltungskatalog(veranstaltungsKatalog,  messageSource,
//					 businessTime,  veranstaltungsManager,  buchungRepository,
//					 gruppenrepository, null,  zeitverwaltung,
//					 zusatzkostenRepository);
//			fail("ControllerVeranstaltungsKatalog() sollte eine IllegalArgumentException werfen, "
//					+ "wenn das Argument userRepository null ist!");
//		} catch (IllegalArgumentException e) {
//			assertEquals("userRepository darf nicht null sein!", e.getMessage());
//		}
		
		try {
			new ControllerVeranstaltungskatalog(veranstaltungsKatalog,  messageSource,
					 businessTime,  veranstaltungsManager,  buchungRepository,
					 gruppenrepository, null,
					 zusatzkostenRepository);
			fail("ControllerVeranstaltungsKatalog() sollte eine IllegalArgumentException werfen, "
					+ "wenn das Argument zeitverwaltung null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals("zeitverwaltung darf nicht null sein!", e.getMessage());
		}
		
		try {
			new ControllerVeranstaltungskatalog(veranstaltungsKatalog,  messageSource,
					 businessTime,  veranstaltungsManager,  buchungRepository,
					 gruppenrepository, zeitverwaltung,
					 null);
			fail("ControllerVeranstaltungsKatalog() sollte eine IllegalArgumentException werfen, "
					+ "wenn das Argument zusatzkostenRepository null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals("zusatzkostenRepository darf nicht null sein!", e.getMessage());
		}
	}
	
	@Test
	public void workshopsTest() {
		ExtendedModelMap model = new ExtendedModelMap();
		String retString = testController.workshops(model);
		
		List<EntityVeranstaltung> veranstaltungen = new ArrayList<>();
		veranstaltungen.add(veranstaltung1);
		
		assertEquals("Veranstaltungen/veranstaltungskatalog", retString);
		assertEquals("ControllerVeranstaltungskatalog.workshops() sollte die korrekte Verantaltung"
				+ "wiedergeben", veranstaltungen , model.get("katalog"));
		
		List<EntityVeranstaltung> empfehlungen = veranstaltungsManager.getBesteBewertungen();
		Iterator<EntityVeranstaltung> iter = empfehlungen.iterator();
		while (iter.hasNext()) {
			EntityVeranstaltung v = iter.next();
		    if (v.getEndDatum().isBefore(LocalDateTime.now()))
		        iter.remove();
		}
		assertEquals("ControllerVeranstaltungskatalog.workshops() sollte die korrekten Empfehlungen"
				+ "wiedergeben", empfehlungen , model.get("empfehlungen"));
	}
	
	@Test
	public void showsTest() {
		ExtendedModelMap model = new ExtendedModelMap();
		String retString = testController.shows(model);
		
		List<EntityVeranstaltung> veranstaltungen = new ArrayList<>();
		veranstaltungen.add(veranstaltung2);
		
		assertEquals("Veranstaltungen/veranstaltungskatalog", retString);
		assertEquals("ControllerVeranstaltungskatalog.shows() sollte den korrekten Veranstaltungskatalog"
				+ "wiedergeben", veranstaltungen , model.get("katalog"));
		
		List<EntityVeranstaltung> empfehlungen = veranstaltungsManager.getBesteBewertungen();
		Iterator<EntityVeranstaltung> iter = empfehlungen.iterator();
		while (iter.hasNext()) {
			EntityVeranstaltung v = iter.next();
		    if (v.getEndDatum().isBefore(LocalDateTime.now()))
		        iter.remove();
		}
		assertEquals("ControllerVeranstaltungskatalog.shows() sollte die korrekten Empfehlungen"
				+ "wiedergeben", empfehlungen , model.get("empfehlungen"));
	}
	
	@Test
	public void registerWorkshopTest() {
		ExtendedModelMap model = new ExtendedModelMap();
		String retString = testController.registerWorkshop(model);
		
		assertEquals("Veranstaltungen/addFormular", retString);
		assertNotNull("ControllerVeranstaltungskatalog.registerShow() sollte"
				+ "eine Instanz von FormNeueVeranatltung erstellen.",model.get("neueVeranstaltung"));
		assertNotNull("ControllerVeranstaltungskatalog.registerShow() sollte"
				+ "eine Instanz von Set<Artistengruppe> erstellen.",model.get("artistengruppen"));
		//FormNeueVeranstaltung testFormNeueVeranstaltung = (FormNeueVeranstaltung) model.get("neueVeranstaltung");
		
	}

	@Test
	public void registerShowTest() {
		ExtendedModelMap model = new ExtendedModelMap();
		String retString = testController.registerShow(model, artist1.getUserAccount());
		
		Set<Artistengruppe> gruppe = new HashSet<>();
		gruppe.add(gruppe1);
		
		assertEquals("Veranstaltungen/addFormular", retString);
		assertEquals("ControllerVeranstaltungskatalog.registerShow() sollte die korrekten Gruppen"
				+ " des Artists anzeigen", gruppe ,
				model.get("artistengruppen"));
		
	}
	
	@Test
	public void erstelleNeueVeranstaltungTest() {
		FormNeueVeranstaltung formNeueVeranstaltung = new FormNeueVeranstaltung();
		BindingResult ergebnis = new DirectFieldBindingResult(formNeueVeranstaltung, "formNeu");
		ExtendedModelMap model = new ExtendedModelMap();
		
		formNeueVeranstaltung.setName("");
		formNeueVeranstaltung.setGruppe("TestGruppe");
		formNeueVeranstaltung.setPreis("12");
		formNeueVeranstaltung.setDauer("200");
		formNeueVeranstaltung.setBeschreibung("TestBeschreibung");
		ergebnis.addError(new FieldError("formular", "Name", "Der Name darf nicht leer sein."));
		
		String retString = testController.erstelleNeueVeranstaltung(formNeueVeranstaltung ,ergebnis, 
				artist1.getUserAccount() , model);
		assertEquals("Veranstaltungen/addFormular", retString);
		
		formNeueVeranstaltung = new FormNeueVeranstaltung();
		ergebnis = new DirectFieldBindingResult(formNeueVeranstaltung, "formNeu");
		model = new ExtendedModelMap();
		
		formNeueVeranstaltung.setName("TestName");
		formNeueVeranstaltung.setGruppe("TestGruppe");
		formNeueVeranstaltung.setPreis("12");
		formNeueVeranstaltung.setDauer("-200");
		formNeueVeranstaltung.setBeschreibung("TestBeschreibung");
		formNeueVeranstaltung.setEndDatum("2017-12-24");
		formNeueVeranstaltung.setEndZeit("19:30");
		formNeueVeranstaltung.setStartDatum("2017-11-01");
		formNeueVeranstaltung.setStartZeit("19:30");
		
		retString = testController.erstelleNeueVeranstaltung(formNeueVeranstaltung ,ergebnis, 
				artist1.getUserAccount() , model);
		assertEquals("Veranstaltungen/addFormular", retString);
		
		//je nachdem wie vt gesetzt wurde...
		//assertEquals("Veranstaltungen/VeranstaltungErstellen", retString.startsWith("redirect://workshops"));
		
	}
	
	@Test
	public void bearbeitenTest() {
		FormNeueVeranstaltung formNeueVeranstaltung = new FormNeueVeranstaltung();
		BindingResult ergebnis = new DirectFieldBindingResult(formNeueVeranstaltung, "formNeu");
		//ExtendedModelMap model = new ExtendedModelMap();
		ergebnis.addError(new FieldError("formular", "Name", "Der Name darf nicht leer sein."));
		
		String retString = testController.bearbeiten(veranstaltung1, formNeueVeranstaltung ,ergebnis);
		assertEquals("Veranstaltungen/addFormular", retString);	
		
		formNeueVeranstaltung = new FormNeueVeranstaltung();
		ergebnis = new DirectFieldBindingResult(formNeueVeranstaltung, "formNeu");
		
		formNeueVeranstaltung.setName("TestName");
		formNeueVeranstaltung.setGruppe("TestGruppe");
		formNeueVeranstaltung.setPreis("12");
		formNeueVeranstaltung.setDauer("200");
		formNeueVeranstaltung.setBeschreibung("TestBeschreibung");
		formNeueVeranstaltung.setEndDatum("2017-12-24");
		formNeueVeranstaltung.setEndZeit("19:30");
		formNeueVeranstaltung.setStartDatum("2017-11-01");
		formNeueVeranstaltung.setStartZeit("19:30");
		retString = testController.bearbeiten(veranstaltung1, formNeueVeranstaltung ,ergebnis);
		assertTrue("Veranstaltungen/VeranstaltungBearbeiten", retString.startsWith("redirect:/detail"));
		//assertTrue("", retString.endsWith(veranstaltung1.getId()));
		//überprüfen, ob {veranstaltung}= veranstaltung1.getId();
		
				//prüfen ob werte übernommen wurden?
	}
	
	@Test
	public void bewerten() {
		ExtendedModelMap model = new ExtendedModelMap();
		String retString = testController.bewerten(veranstaltung1, model);
		
		assertEquals("Veranstaltungen/bewertungFormular", retString);
		assertEquals("ControllerVeranstaltungskatalog.shows() sollte die korrekte Verantaltung"
				+ "wiedergeben", veranstaltung1 , model.get("veranstaltung"));	
	}
	
	@Test
	public void zeigeAlleBuchungenTest() {
		List<Zusatzkosten>zusatzkostenset = new ArrayList<>();
		zusatzkostenset.add(zusatzkosten);
		ExtendedModelMap model = new ExtendedModelMap();
		String retString = testController.zeigeAlleBuchungen(model);
		
		assertEquals("Veranstaltungen/alleBuchungen", retString);
		assertEquals("ControllerVeranstaltungskatalog.zeigeAlleBuchungen() sollte die korrekten Zusatzkosten"
				+ "wiedergeben", zusatzkostenset , model.get("zusatzkosten"));
	}
}
