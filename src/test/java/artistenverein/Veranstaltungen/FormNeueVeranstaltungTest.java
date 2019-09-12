package artistenverein.Veranstaltungen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import artistenverein.AbstractIntegrationTests;
import artistenverein.Veranstaltungen.FormNeueVeranstaltung;

public class FormNeueVeranstaltungTest extends AbstractIntegrationTests {
	
	private final FormNeueVeranstaltung neueVeranstaltung = new FormNeueVeranstaltung();
	
	@Test
	public void getGruppeTest() {
		neueVeranstaltung.setGruppe(null);
		assertTrue("FormNeueVeranstaltung.getGruppe() sollte bei null '' zurückgeben.",
				neueVeranstaltung.getGruppe() == "" );
	}
	
	/*@Test
	public void setGruppeNullPointerTest() {
		try {
			neueVeranstaltung.setGruppe(null);
			fail("FormNeueVeranstaltung.setGruppe() sollte eine IllegalArgumentException werfen, wenn das Argument Grupppe null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "gruppe darf nicht null sein!");
		}
	}*/
	
	@Test
	public void setNameNullPointerTest() {
		try {
			neueVeranstaltung.setName(null);
			fail("FormNeueVeranstaltung.setName() sollte eine IllegalArgumentException werfen, wenn das Argument Name null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "name darf nicht null sein!");
		}
	}
	
	@Test
	public void setPreisNullPointerTest() {
		try {
			neueVeranstaltung.setPreis(null);
			fail("FormNeueVeranstaltung.setPreis() sollte eine IllegalArgumentException werfen, wenn das Argument Preis null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "preis darf nicht null sein!");
		}
	}
	
	@Test
	public void setBeschreibungNullPointerTest() {
		try {
			neueVeranstaltung.setBeschreibung(null);
			fail("FormNeueVeranstaltung.setBeschreibung() sollte eine IllegalArgumentException werfen, wenn das Argument Beschreibung null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "beschreibung darf nicht null sein!");
		}
	}
	
	@Test
	public void setDauerNullPointerTest() {
		try {
			neueVeranstaltung.setDauer(null);
			fail("FormNeueVeranstaltung.setDauer() sollte eine IllegalArgumentException werfen, wenn das Argument Dauer null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "dauer darf nicht null sein!");
		}
	}
	
	@Test
	public void setStartDatumNullPointerTest() {
		try {
			neueVeranstaltung.setStartDatum(null);
			fail("FormNeueVeranstaltung.setStartDatum() sollte eine IllegalArgumentException werfen, wenn das Argument StartDatum null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "startDatum darf nicht null sein!");
		}
	}
	
	@Test
	public void setStartZeitNullPointerTest() {
		try {
			neueVeranstaltung.setStartZeit(null);
			fail("FormNeueVeranstaltung.setStartZeit() sollte eine IllegalArgumentException werfen, wenn das Argument startZeit null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "startZeit darf nicht null sein!");
		}
	}
	
	@Test
	public void setEndDatumNullPointerTest() {
		try {
			neueVeranstaltung.setEndDatum(null);
			fail("FormNeueVeranstaltung.setEndDatum() sollte eine IllegalArgumentException werfen, wenn das Argument endDatum null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "endDatum darf nicht null sein!");
		}
	}
	
	@Test
	public void setEndZeitNullPointerTest() {
		try {
			neueVeranstaltung.setEndZeit(null);
			fail("FormNeueVeranstaltung.setEndZeit() sollte eine IllegalArgumentException werfen, wenn das Argument endZeit null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "endZeit darf nicht null sein!");
		}
	}
}