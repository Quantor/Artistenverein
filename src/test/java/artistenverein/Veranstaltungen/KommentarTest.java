package artistenverein.Veranstaltungen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.time.LocalDateTime;

import org.junit.Test;

import artistenverein.AbstractIntegrationTests;
import artistenverein.Veranstaltungen.Kommentar;

public class KommentarTest extends AbstractIntegrationTests {
	
	private final LocalDateTime datum = LocalDateTime.of(2017, 12, 24, 19, 30);
	
	@Test
	public void erstelleKommentarNullPointerTest() {
		try {
			new Kommentar(null, datum);
			fail("Kommentar() sollte eine IllegalArgumentException werfen, wenn das Argument Text null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "text darf nicht null sein!");
		}
		
		try {
			new Kommentar("", null);
			fail("Kommentar() sollte eine IllegalArgumentException werfen, wenn das Argument Datum null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "datum darf nicht null sein!");
		}
	}
}