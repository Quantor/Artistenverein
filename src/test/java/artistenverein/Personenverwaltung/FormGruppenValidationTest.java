package artistenverein.Personenverwaltung;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import artistenverein.AbstractIntegrationTests;
import artistenverein.Personenverwaltung.FormGruppenValidation;

public class FormGruppenValidationTest extends AbstractIntegrationTests {

	private final FormGruppenValidation form = new FormGruppenValidation();
	
	@Test
	public void setGruppennameNullTest() {
		try {
			form.setGruppenname(null);
			fail("FormGruppenValidation.setGruppenname() sollte eine IllegalArgumentException werfen, wenn das Argument Gruppenname null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "Gruppenname darf nicht Null sein!");
		}

		try {
			form.setGruppenname("");
			fail("FormGruppenValidation.setGruppenname() sollte eine IllegalArgumentException werfen, wenn das Argument Gruppenname leer ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "Gruppenname darf nicht leer sein!");
		}
	}
}
