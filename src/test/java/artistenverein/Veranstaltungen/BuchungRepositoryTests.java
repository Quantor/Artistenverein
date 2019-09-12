package artistenverein.Veranstaltungen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.salespointframework.core.Currencies.EURO;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import org.javamoney.moneta.Money;
import org.junit.Before;
import org.junit.Test;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;

import artistenverein.AbstractIntegrationTests;
import artistenverein.Zeitverwaltung.Zeit;

public class BuchungRepositoryTests extends AbstractIntegrationTests {

	@Autowired
	private BuchungRepository testRep;
	@Autowired
	private UserAccountManager accounts;
	@Autowired
	private VeranstaltungsKatalog vKatalog;
	private static UserAccount testAccount1;
	private static UserAccount testAccount2;
	private static UserAccount testAccount3;
	private static UserAccount testAccount4;
	private static final UUID testUUID = UUID.randomUUID();
	private static Buchung testBuchung;

	@Before
	public void setUp() {
		testRep.deleteAll();
		vKatalog.deleteAll();
		UserAccount genji = accounts.findByUsername("genji").get();
		testAccount1 = accounts.findByUsername("hans").get();
		testAccount2 = accounts.findByUsername("dextermorgan").get();
		testAccount3 = accounts.findByUsername("earlhickey").get();
		testAccount4 = accounts.findByUsername("mclovinfogell").get();
		Set<UserAccount> artists = new HashSet<UserAccount>();
		artists.add(genji);
		EntityVeranstaltung neuerWorkshop = new EntityVeranstaltung("test", Money.of(1.99, EURO), "test", 120,
				EntityVeranstaltung.VeranstaltungsType.WORKSHOP, LocalDateTime.now().minusDays(5),
				LocalDateTime.now().plusDays(20), artists, 1, "");
		vKatalog.save(neuerWorkshop);
		EntityVeranstaltung neueShow = new EntityVeranstaltung("test", Money.of(1.99, EURO), "test", 120,
				EntityVeranstaltung.VeranstaltungsType.SHOW, LocalDateTime.now().minusDays(5),
				LocalDateTime.now().plusDays(20), artists, 1, "");
		vKatalog.save(neueShow);
		
		testBuchung = new Buchung("Halle", LocalDateTime.now().plusDays(4), neuerWorkshop, testAccount1,
				Money.of(1.99, EURO), testUUID);
		testRep.save(testBuchung);
		Buchung neueB = new Buchung("außerhalb", LocalDateTime.now().plusDays(4), neueShow, testAccount2, Money.of(1.99, EURO),
				testUUID);
		testRep.save(neueB);
		neueB = new Buchung("Halle", LocalDateTime.now().minusDays(4), neuerWorkshop, testAccount3,
				Money.of(1.99, EURO), UUID.randomUUID());
		testRep.save(neueB);
	}

	@Test
	public void bekommtRabattTest() {
		assertTrue("BuchungRepository.bekommtRabatt() sollte den Korrekten Wert zurückgeben",
				testRep.bekommtRabatt(testAccount1));
		assertFalse("BuchungRepository.bekommtRabatt() sollte den Korrekten Wert zurückgeben",
				testRep.bekommtRabatt(testAccount2));
		assertFalse("BuchungRepository.bekommtRabatt() sollte den Korrekten Wert zurückgeben",
				testRep.bekommtRabatt(testAccount3));
		assertFalse("BuchungRepository.bekommtRabatt() sollte den Korrekten Wert zurückgeben",
				testRep.bekommtRabatt(testAccount4));
	}

	@Test
	public void getHallenZeitenTest() {
		Iterable<Zeit> testZeiten = testRep.getHallenZeiten();
		Iterator<Zeit> it = testZeiten.iterator();
		int sum = 0;
		while (it.hasNext()) {
			it.next();
			sum++;
		}
		assertEquals("BuchungRepository.getHallenZeiten() sollte die korrekten Hallenzeiten zurückgeben", 2, sum);
	}

	@Test
	public void getHallenBuchungenTest() {
		Iterable<Buchung> testBuchungen = testRep.getHallenBuchungen();
		Iterator<Buchung> it = testBuchungen.iterator();
		int sum = 0;
		while (it.hasNext()) {
			it.next();
			sum++;
		}
		assertEquals("BuchungRepository.getHallenBuchungen() sollte die korrekten Hallenzeiten zurückgeben", 2, sum);
	}
	
	@Test
	public void deleteAllUUIDTest() {
		testRep.deleteAllUUID(testBuchung);
		Iterator<Buchung> it = testRep.findAll().iterator();
		int sum = 0;
		while (it.hasNext()) {
			it.next();
			sum++;
		}
		assertEquals("BuchungRepository.deleteAlUUID() sollte alle entsprechenden Buchungen löschen", 1, sum);
	}
}
