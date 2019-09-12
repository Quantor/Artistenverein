package artistenverein.Veranstaltungen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.salespointframework.core.Currencies.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

import org.javamoney.moneta.Money;
import org.junit.Test;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;

import artistenverein.AbstractIntegrationTests;
import artistenverein.Lagerverwaltung.Artikel;
import artistenverein.Personenverwaltung.Artistengruppe;
import artistenverein.Personenverwaltung.RepositoryGruppen;
import artistenverein.Personenverwaltung.RepositoryUser;
import artistenverein.Personenverwaltung.User;
import artistenverein.Veranstaltungen.Bewertung;
import artistenverein.Veranstaltungen.BuchungRepository;
import artistenverein.Veranstaltungen.EntityVeranstaltung;
import artistenverein.Veranstaltungen.FormNeueVeranstaltung;
import artistenverein.Veranstaltungen.VeranstaltungsKatalog;
import artistenverein.Veranstaltungen.VeranstaltungsManager;
import artistenverein.Veranstaltungen.EntityVeranstaltung.VeranstaltungsType;

public class VeranstaltungsManagerTest extends AbstractIntegrationTests {
	@Autowired
	VeranstaltungsKatalog veranstaltungsKatalog;
	@Autowired
	RepositoryUser repositoryUser;
	@Autowired
	RepositoryGruppen gruppenRepo;
	@Autowired
	private BuchungRepository buchungRep;
	
	private final static Set<UserAccount> artisten = new HashSet<>();
//	private final static UserAccount user = new UserAccount();
	private final static LocalDateTime datum1 = LocalDateTime.of(2017, 12, 1, 19, 30);
	private final static LocalDateTime datum2 = LocalDateTime.of(2017, 12, 31, 23, 59);
	Set<UserAccount> artistengruppe = new HashSet<>();
	Artistengruppe gruppe1 = new Artistengruppe("TestGruppe");
	EntityVeranstaltung veranstaltung1 = new EntityVeranstaltung("TestShow", 
			Money.of(100, EURO),"Test Beschreibung", 200, VeranstaltungsType.SHOW, 
			datum1, datum2, artistengruppe, 1, "Tanzbären");
	EntityVeranstaltung veranstaltung2 = new EntityVeranstaltung("TestWorkshop",
			Money.of(50, EURO), "Test Beschreibung", 120, VeranstaltungsType.WORKSHOP, 
			datum1 , datum2 , artistengruppe, 1, "");
	EntityVeranstaltung veranstaltung3 = new EntityVeranstaltung("TestWorkshop2", 
			Money.of(50, EURO),"Test Beschreibung", 120, VeranstaltungsType.WORKSHOP, 
			datum1, datum2, artistengruppe, 1, "");
	EntityVeranstaltung veranstaltung4 = new EntityVeranstaltung("TestShow2", 
			Money.of(50, EURO),"Test Beschreibung", 120, VeranstaltungsType.SHOW, 
			datum1, datum2, artistengruppe, 1, "Tanzbären");
	
	@Test
	public void erstelleVeranstaltungsManagerNullPointerTest() {
		try {
			new VeranstaltungsManager(null);
			fail("VeranstaltungsManager() sollte eine IllegalArgumentException werfen, "
					+ "wenn das Argument veranstaltungsKatalog null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "veranstaltungsKatalog darf nicht null sein!");
		}
	}
	
	@Test
	public void erstelleNeueVeranstaltungTest() {
		gruppenRepo.save(gruppe1);
		VeranstaltungsManager veranstaltungsManager = new VeranstaltungsManager(veranstaltungsKatalog);
		FormNeueVeranstaltung neueVeranstaltung = new FormNeueVeranstaltung();
		Set<UserAccount> user = new HashSet<>();
		for(User mitglied : gruppe1.getMitglieder()) {
			user.add(mitglied.getUserAccount());
		}
		EntityVeranstaltung veranstaltung = new EntityVeranstaltung("TestShow", 
				Money.of(100, EURO),"Test Beschreibung", 120, VeranstaltungsType.SHOW, 
				datum1, datum2, user, 1, "Tanzbären");
		neueVeranstaltung.setGruppe("TestGruppe");
		neueVeranstaltung.setName("TestShow");
		neueVeranstaltung.setPreis("100");
		neueVeranstaltung.setBeschreibung("Test Beschreibung");
		neueVeranstaltung.setDauer("120");
		neueVeranstaltung.setStartDatum("2017-12-01");
		neueVeranstaltung.setStartZeit("19:30");
		neueVeranstaltung.setEndZeit("23:59");
		neueVeranstaltung.setEndDatum("2017-12-31");
		
		EntityVeranstaltung erstellteVeranstaltung = veranstaltungsManager.erstelleNeueVeranstaltung(neueVeranstaltung, 
				VeranstaltungsType.SHOW, user, "Tanzbären");
		
		assertEquals(erstellteVeranstaltung.getArtisten(), veranstaltung.getArtisten());
		assertEquals(erstellteVeranstaltung.getArtikellist(), veranstaltung.getArtikellist());
		assertEquals(erstellteVeranstaltung.getBeschreibung(), veranstaltung.getBeschreibung());
		assertEquals(erstellteVeranstaltung.getDauer(), veranstaltung.getDauer());
		assertEquals(erstellteVeranstaltung.getType(), veranstaltung.getType());
		assertEquals(erstellteVeranstaltung.getArtikel(), veranstaltung.getArtikel());
		assertEquals(erstellteVeranstaltung.getStartDatum(), veranstaltung.getStartDatum());
		assertEquals(erstellteVeranstaltung.getEndDatum(), veranstaltung.getEndDatum());
	}
	
	@Test
	public void erstelleNeueVeranstaltungNullPointerTest() {
		VeranstaltungsManager veranstaltungsManager = new VeranstaltungsManager(veranstaltungsKatalog);
		
		try {
			veranstaltungsManager.erstelleNeueVeranstaltung(null,
					VeranstaltungsType.SHOW, artisten, "Tanzbären");
			fail("VeranstaltungsManager.erstelleNeueVeranstaltung() sollte eine IllegalArgumentException werfen, "
					+ "wenn das Argument neueVeranstaltung null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "neueVeranstaltung darf nicht null sein!");
		}
		
		try {
			veranstaltungsManager.erstelleNeueVeranstaltung(new FormNeueVeranstaltung(),
					null, artisten, "Tanzbären");
			fail("VeranstaltungsManager.erstelleNeueVeranstaltung() sollte eine IllegalArgumentException werfen, "
					+ "wenn das Argument veranstaltungsType null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "vt darf nicht null sein!");
		}
	}
	
	@Test
	public void addArtikelTest() {
		Artikel artikel = new Artikel("TestArtikel", "TestBild", Money.of(20, EURO),
				"TestBeschreibung"); 
		Artikel[] a = {artikel};
		Set<Artikel> artikellist = new HashSet<>();
		artikellist.add(artikel);
		Set<UserAccount> artistengruppe = new HashSet<>();
		EntityVeranstaltung veranstaltung = new EntityVeranstaltung("TestVeranstaltung",
				Money.of(100, EURO),"TestBeschreibung", 200, VeranstaltungsType.SHOW, 
				datum1, datum2, artistengruppe, 1, "Tanzbären");
		VeranstaltungsManager vM = new VeranstaltungsManager(veranstaltungsKatalog);
		vM.addArtikel(veranstaltung, a);
		assertEquals(veranstaltung.getArtikellist(), artikellist);
		
	}
	
	@Test
	public void addArtikelNullPointerTest() {
		VeranstaltungsManager veranstaltungsManager = new VeranstaltungsManager(veranstaltungsKatalog);
		try {
			
			Artikel artikel = new Artikel("TestArtikel", "TestBild", Money.of(20, EURO),
					"TestBeschreibung"); 
			Artikel[] a = {artikel};
			veranstaltungsManager.addArtikel(null, a);
			fail("VeranstaltungsManager.addArtikel() sollte eine IllegalArgumentException werfen, "
					+ "wenn das Argument veranstaltung null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "veranstaltung darf nicht null sein!");
		}
		
		try {
			Set<UserAccount> artistengruppe = new HashSet<>();
			EntityVeranstaltung veranstaltung = new EntityVeranstaltung("TestVeranstaltung",
					Money.of(100, EURO),"TestBeschreibung", 200, VeranstaltungsType.SHOW, 
					datum1, datum2, artistengruppe, 1, "Tanzbären");
			veranstaltungsKatalog.save(veranstaltung);
			veranstaltungsManager.addArtikel(veranstaltung, null);
			fail("VeranstaltungsManager.addArtikel() sollte eine IllegalArgumentException werfen, "
					+ "wenn das Argument artikel null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "artikel darf nicht null sein!");
		}
	}
	
	@Test
	
	public void getDatumTest() {
		VeranstaltungsManager veranstaltungsManager = new VeranstaltungsManager(veranstaltungsKatalog);
		LocalDateTime datum3 = veranstaltungsManager.getDatum("2017-12-01", "19:30");
		assertTrue("VeranstaltungsManager.getDatum() sollte das richtige Datum zurückgeben.",
				datum3.isEqual(datum1));
	}
	
	@Test
	public void getDatumNullPointerTest() {
		VeranstaltungsManager veranstaltungsManager = new VeranstaltungsManager(veranstaltungsKatalog);
		try {
			veranstaltungsManager.getDatum(null, "");
			fail("VeranstaltungsManager.getDatum() sollte eine IllegalArgumentException werfen, "
					+ "wenn das Argument datum null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "datum darf nicht null sein!");
		}
		
		try {
			veranstaltungsManager.getDatum("", null);
			fail("VeranstaltungsManager.getDatum() sollte eine IllegalArgumentException werfen, "
					+ "wenn das Argument zeit null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "zeit darf nicht null sein!");
		}
	}
	
	@Test
	public void getBesteVeranstaltungenTest() {
		VeranstaltungsManager vM = new VeranstaltungsManager(veranstaltungsKatalog);
		//Set<UserAccount> artistengruppe = new HashSet<>();
		
		User user = new User(new UserAccount());
		for(User userRepo: repositoryUser.findAll()) {
			if(userRepo.getUserAccount().getUsername().equals("hans")) {
				user = userRepo;
			}
		}
		repositoryUser.save(user);
	
		buchungRep.deleteAll();
		veranstaltungsKatalog.deleteAll();
		veranstaltungsKatalog.save(veranstaltung1);
		veranstaltungsKatalog.save(veranstaltung2);
		veranstaltungsKatalog.save(veranstaltung3);
		veranstaltungsKatalog.save(veranstaltung4);
		
		List<EntityVeranstaltung> empfehlungen = new ArrayList<>();
		veranstaltung1.addBewertung(new Bewertung("", 3, datum1, user.getUserAccount()));
		veranstaltung2.addBewertung(new Bewertung("", 2, datum1, user.getUserAccount()));
		empfehlungen.add(veranstaltung1);
		empfehlungen.add(veranstaltung2);
		
		assertEquals(vM.getBesteBewertungen().get(0), empfehlungen.get(0));
		assertEquals(vM.getBesteBewertungen().get(1), empfehlungen.get(1));
		
		empfehlungen.clear();
		empfehlungen.add(veranstaltung3);
		empfehlungen.add(veranstaltung1);
		empfehlungen.add(veranstaltung2);
		
		veranstaltung3.addBewertung(new Bewertung("", 4, datum1, user.getUserAccount()));
		assertEquals(vM.getBesteBewertungen().get(0), empfehlungen.get(0));
		assertEquals(vM.getBesteBewertungen().get(1), empfehlungen.get(1));
		assertEquals(vM.getBesteBewertungen().get(2), empfehlungen.get(2));
		
		veranstaltung4.addBewertung(new Bewertung("", 1, datum1, user.getUserAccount()));
		assertEquals(vM.getBesteBewertungen().get(0), empfehlungen.get(0));
		assertEquals(vM.getBesteBewertungen().get(1), empfehlungen.get(1));
		assertEquals(vM.getBesteBewertungen().get(2), empfehlungen.get(2));
		
		veranstaltungsKatalog.deleteAll();
	}
}