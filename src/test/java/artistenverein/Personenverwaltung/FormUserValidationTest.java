package artistenverein.Personenverwaltung;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import artistenverein.AbstractIntegrationTests;
import artistenverein.Personenverwaltung.FormUserValidation;

public class FormUserValidationTest extends AbstractIntegrationTests {

	private final FormUserValidation form = new FormUserValidation();

	@Test
	public void setUsernameNullTest() {
		try {
			form.setUsername(null);
			fail("FormUserValidationTest.setName() sollte eine IllegalArgumentException werfen, wenn das Argument Username null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "Username darf nicht Null sein!");
		}

		try {
			form.setUsername("");
			fail("FormUserValidationTest.setName() sollte eine IllegalArgumentException werfen, wenn das Argument Username leer ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "Username darf nicht leer sein!");
		}
	}

	@Test
	public void setFristNullTest() {
		try {
			form.setFirstname(null);
			fail("FormUserValidationTest.setName() sollte eine IllegalArgumentException werfen, wenn das Argument Vorname null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "Vorname darf nicht Null sein!");
		}

		try {
			form.setFirstname("");
			fail("FormUserValidationTest.setName() sollte eine IllegalArgumentException werfen, wenn das Argument Vorname leer ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "Vorname darf nicht leer sein!");
		}

	}
	
	@Test
	public void setLastnameNullTest() {
		try {
			form.setLastname(null);
			fail("FormUserValidationTest.setName() sollte eine IllegalArgumentException werfen, wenn das Argument Nachname null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "Nachname darf nicht Null sein!");
		}

		try {
			form.setLastname("");
			fail("FormUserValidationTest.setName() sollte eine IllegalArgumentException werfen, wenn das Argument Nachname leer ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "Nachname darf nicht leer sein!");
		}

	}
	
	@Test
	public void setEmailNullTest() {
		try {
			form.setEmail(null);
			fail("FormUserValidationTest.setName() sollte eine IllegalArgumentException werfen, wenn das Argument Email null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "Email darf nicht Null sein!");
		}

		try {
			form.setEmail("");
			fail("FormUserValidationTest.setName() sollte eine IllegalArgumentException werfen, wenn das Argument Email leer ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "Email darf nicht leer sein!");
		}

	}
	
	

}
