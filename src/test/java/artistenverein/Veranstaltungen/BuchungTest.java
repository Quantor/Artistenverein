package artistenverein.Veranstaltungen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.javamoney.moneta.Money;
import org.junit.Before;
import org.junit.Test;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;

import artistenverein.AbstractIntegrationTests;
import artistenverein.Veranstaltungen.Buchung;
import artistenverein.Veranstaltungen.EntityVeranstaltung;
import artistenverein.Veranstaltungen.EntityVeranstaltung.VeranstaltungsType;

import static org.salespointframework.core.Currencies.*;

public class BuchungTest extends AbstractIntegrationTests {

	private final UserAccount user = new UserAccount();
	Set<UserAccount> artisten = new HashSet<>();
	private final LocalDateTime datum = LocalDateTime.of(2018, 05, 24, 19, 30);
	private final LocalDateTime endDatum = LocalDateTime.of(2018, 05, 24, 22, 50);
	private final EntityVeranstaltung veranstaltung = new EntityVeranstaltung("Test", Money.of(200, EURO),
			"TestBeschreibung", 200, VeranstaltungsType.SHOW, datum.minusHours(50), datum.plusHours(200), artisten, 1, "Tanzbären");
	private final EntityVeranstaltung veranstaltung2 = new EntityVeranstaltung("Test2", Money.of(300, EURO),
			"TestBeschreibung2", 200, VeranstaltungsType.WORKSHOP, datum.minusHours(40), datum.plusHours(300), artisten, 1, "");

	private Buchung testBuchung;
	private UserAccount hans;
	
	@Autowired
	private UserAccountManager accounts;

	@Before
	public void setUp() {
		testBuchung = new Buchung("TestOrt", datum, veranstaltung, user, veranstaltung.getPrice(), UUID.randomUUID());
		hans = accounts.findByUsername("hans").get();
	}

	@Test
	public void getEndDatumTest() {
		assertTrue("buchung.getEndDatum() sollte das EndDatum richtig zurückgeben.",
				testBuchung.getEndDatum().isEqual(endDatum));
		assertTrue("buchung.getEndDatum() sollte das EndDatum richtig zurückgeben.",
				(!testBuchung.getEndDatum().isEqual(endDatum.plusMinutes(1))));
	}

	@Test
	public void erzeugeBuchungNullPointerTest() {
		try {
			new Buchung(null, datum, veranstaltung, user, veranstaltung.getPrice(), UUID.randomUUID());
			fail("Buchung() sollte eine IllegalArgumentException werfen, wenn das Argument ort null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "ort darf nicht null sein!");
		}

		try {
			new Buchung("TestOrt", null, veranstaltung, user, veranstaltung.getPrice(), UUID.randomUUID());
			fail("Buchung() sollte eine IllegalArgumentException werfen, wenn das Argument datum null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "datum darf nicht null sein!");
		}

		try {
			new Buchung("TestOrt", datum, null, user, veranstaltung.getPrice(), UUID.randomUUID());
			fail("Buchung() sollte eine IllegalArgumentException werfen, wenn das Argument veranstaltung null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "veranstaltung darf nicht null sein!");
		}

		try {
			new Buchung("TestOrt", datum, veranstaltung, null, veranstaltung.getPrice(), UUID.randomUUID());
			fail("Buchung() sollte eine IllegalArgumentException werfen, wenn das Argument User null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "kunde darf nicht null sein!");
		}
		
		try {
			new Buchung("TestOrt", datum, veranstaltung, user, veranstaltung.getPrice(), null);
			fail("Buchung() sollte eine IllegalArgumentException werfen, wenn das Argument buchungId null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "buchungId darf nicht null sein!");
		}
	}

	@Test
	public void setOrtTest() {
		try {
			testBuchung.setOrt(null);
			fail("Buchung.setOrt() sollte eine IllegalArgumentException werfen, wenn das Argument ort null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "ort darf nicht null sein!");
		}
		
		testBuchung.setOrt("Halle");
		assertEquals("Halle", testBuchung.getOrt());
	}

	public void setDatumTest() {
		try {
			testBuchung.setDatum(null);
			fail("Buchung.setDatum() sollte eine IllegalArgumentException werfen, wenn das Argument datum null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "datum darf nicht null sein!");
		}
		
		LocalDateTime now = LocalDateTime.now();
		testBuchung.setDatum(now);
		assertEquals(now, testBuchung.getDatum());
	}

	public void setVeranstaltungTest() {
		try {
			testBuchung.setVeranstaltung(null);
			fail("Buchung.setVeranstaltung() sollte eine IllegalArgumentException werfen, wenn das Argument veranstaltung null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "veranstaltung darf nicht null sein!");
		}
		
		testBuchung.setVeranstaltung(veranstaltung2);
		assertEquals(veranstaltung2, testBuchung.getVeranstaltung());
	}

	public void setKundeTest() {
		try {
			testBuchung.setKunde(null);
			fail("Buchung.setKunde() sollte eine IllegalArgumentException werfen, wenn das Argument kunde null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "kunde darf nicht null sein!");
		}
		
		testBuchung.setKunde(hans);
		assertEquals(hans, testBuchung.getKunde());
	}
}