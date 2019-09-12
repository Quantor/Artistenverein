package artistenverein.Personenverwaltung;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import artistenverein.AbstractIntegrationTests;
import artistenverein.Personenverwaltung.ControllerVerwaltungKunde;

public class ControllerVerwaltungKundeTest extends AbstractIntegrationTests {


	@Autowired
	private ControllerVerwaltungKunde con;
	
	@Test
	public void getKundenVerwaltungTermine() {
		Model model = new ExtendedModelMap();
		assertEquals("VerwaltungKunde/termine",con.getKundenVerwaltungTermine(new UserAccount(), model));
	 
		
	}

	@Test
	public void getKundenRechnungen() {
		Model model = new ExtendedModelMap();
		assertEquals("VerwaltungKunde/rechnungen",con.getKundenRechnungen(new UserAccount(), model));
		
	}
	
}

