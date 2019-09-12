package artistenverein.Personenverwaltung;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import artistenverein.AbstractIntegrationTests;
import artistenverein.Personenverwaltung.ControllerVerwaltungArtist;

public class ControllerVerwaltungArtistTest  extends AbstractIntegrationTests {

	@Autowired
	private UserAccountManager accounts;
	@Autowired
	private ControllerVerwaltungArtist con;
	
	@Test
	public void getArtistverwaltungTermine()
	{
		Model model = new ExtendedModelMap();
		UserAccount u = accounts.findByUsername("genji").get();
		final String monatZurueck = "true";
		final String monatVor = "false";
		assertEquals("VerwaltungArtist/termine",con.getArtistverwaltungTermine(u, model, monatZurueck, monatVor));
		
	}
	
	@Test
	public void getArtistverwaltungMain() {
		Model model = new ExtendedModelMap();
		assertEquals("VerwaltungArtist/main",con.getArtistverwaltungMain(new UserAccount(), model));
		
		
	}
	
	@Test
	public void getArtistverwaltungGruppe() {
		Model model = new ExtendedModelMap();
		assertEquals("VerwaltungArtist/gruppen",con.getArtistverwaltungGruppe(new UserAccount(), model));
		
	}

	@Test
	public void getArtistverwaltungSperrzeiten() {
		Model model = new ExtendedModelMap();
		assertEquals("VerwaltungArtist/main",con.getArtistverwaltungSperrzeiten(new UserAccount(), model));
		
	}
	
	@Test
	public void getArtistverwaltungVeranstaltungen() {
		Model model = new ExtendedModelMap();
		assertEquals("VerwaltungArtist/veranstaltungen",con.getArtistverwaltungVeranstaltungen(new UserAccount(), model));
		
	}

}