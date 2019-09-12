package artistenverein.Veranstaltungen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import artistenverein.AbstractIntegrationTests;
import artistenverein.Veranstaltungen.BuchungsValidation;

public class BuchungsValidationTest extends AbstractIntegrationTests {
	
	private final BuchungsValidation buchungsValidation = new BuchungsValidation();
	private BuchungsValidation testValidation;
	
	@Before
	public void setUp() {
		testValidation = new BuchungsValidation();
	}
	
	@Test
	public void validierungOrtTest() {
		buchungsValidation.setOrtwahl("");
		assertTrue("BuchungsValidation.validierungOrt() sollte true ausgeben, wenn der ort nicht 'außerhalb' ist",
				buchungsValidation.validierungOrt() == true );
		buchungsValidation.setOrtwahl("außerhalb");
		buchungsValidation.setOrt("");
		assertTrue("BuchungsValidation.validierungOrt() sollte false ausgeben, wenn die ortwahl 'außerhalb' ist und"
				+ "der ort nicht angegeben wurde",
				buchungsValidation.validierungOrt() == false );
		buchungsValidation.setOrtwahl("außerhalb");
		buchungsValidation.setOrt("TestOrt");
		assertTrue("BuchungsValidation.validierungOrt() sollte true ausgeben, wenn die ortwahl 'außerhalb' ist und"
				+ "der ort angegeben wurde",
				buchungsValidation.validierungOrt() == true );
	}
	
	@Test
	public void setDatumNullPointerTest() {
		try {
			buchungsValidation.setDatum(null);
			fail("BuchungsValidation.setDatum() sollte eine IllegalArgumentException werfen, wenn das Argument Datum null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "datum darf nicht null sein!");
		}
	}
	
	@Test
	public void setOrtNullPointerTest() {
		try {
			buchungsValidation.setOrt(null);
			fail("BuchungsValidation.setOrt() sollte eine IllegalArgumentException werfen, wenn das Argument Ort null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "ort darf nicht null sein!");
		}
	}
	
	@Test
	public void setZeitNullPointerTest() {
		try {
			buchungsValidation.setZeit(null);
			fail("BuchungsValidation.setZeit() sollte eine IllegalArgumentException werfen, wenn das Argument Zeit null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "zeit darf nicht null sein!");
		}
	}
	
	@Test
	public void setOrtwahlNullPointerTest() {
		try {
			buchungsValidation.setOrtwahl(null);
			fail("BuchungsValidation.setOrtwahl() sollte eine IllegalArgumentException werfen, wenn das Argument Ortwahl null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "ortwahl darf nicht null sein!");
		}
	}
	
	@Test
	public void setDatumTest() {
		testValidation.setDatum("test");
		assertEquals("test", testValidation.getDatum());
	}
	
	@Test
	public void setZeitTest() {
		testValidation.setZeit("test");
		assertEquals("test", testValidation.getZeit());
	}
	
	@Test
	public void setOrtwahlTest() {
		testValidation.setOrtwahl("test");
		assertEquals("test", testValidation.getOrtwahl());
	}
}