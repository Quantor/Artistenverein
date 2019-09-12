package artistenverein.Veranstaltungen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Test;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;

import artistenverein.AbstractIntegrationTests;
import artistenverein.Veranstaltungen.Bewertung;

public class BewertungTest extends AbstractIntegrationTests {
	
	@Autowired
	private UserAccountManager accounts;
	
	private final UserAccount user = new UserAccount();
	private final LocalDateTime datum = LocalDateTime.of(2018, 05,24, 19, 30);
	private UserAccount hans;
	private Bewertung testBewertung;

	@Before
	public void setUp() {
		hans = accounts.findByUsername("hans").get();
		testBewertung = new Bewertung("test", 2, datum, user);
	}
	
	@Test
	public void ErstelleBewertungNullPointerTest() {
		try {
			new Bewertung(null, 2, datum, user);
			fail("Bewertung() sollte eine IllegalArgumentException werfen, wenn das Argument Text null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "text darf nicht null sein!");
		}
		
		try {
			new Bewertung("", 2, null, user);
			fail("Bewertung() sollte eine IllegalArgumentException werfen, wenn das Argument Datum null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "datum darf nicht null sein!");
		}
		
		try {
			new Bewertung("", 2, datum, null);
			fail("Bewertung() sollte eine IllegalArgumentException werfen, wenn das Argument Autor null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "autor darf nicht null sein!");
		}
	}
	
	@Test
	public void setAutorTest() {
		try {
			Bewertung bewertung = new Bewertung("", 2, datum, user);
			bewertung.setAutor(null);
			fail("Bewertung.setAutor() sollte eine IllegalArgumentException werfen, wenn das Argument Autor null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "autor darf nicht null sein!");
		}
		testBewertung.setAutor(hans);
		assertEquals(hans, testBewertung.getAutor());
	}
	
	@Test
	public void toStringTest() {
		assertEquals("test", testBewertung.toString());
	}
}