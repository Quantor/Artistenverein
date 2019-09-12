package artistenverein.Veranstaltungen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import artistenverein.AbstractIntegrationTests;
import artistenverein.Veranstaltungen.BewertungsValidation;

public class BewertungsValidationTest extends AbstractIntegrationTests {
	
	private final BewertungsValidation bewertungsValidation = new BewertungsValidation();
	
	@Test
	public void setBewertungNullPointerTest() {
		try {
			bewertungsValidation.setBewertung(null);
			fail("BewertungsValidation.setBewertung() sollte eine IllegalArgumentException werfen, wenn das Argument Bewertung null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "bewertung darf nicht null sein!");
		}
		
		bewertungsValidation.setBewertung("test");
		assertEquals("test", bewertungsValidation.getBewertung());
	}
}