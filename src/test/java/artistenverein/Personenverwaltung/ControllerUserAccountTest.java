package artistenverein.Personenverwaltung;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DirectFieldBindingResult;

import artistenverein.AbstractIntegrationTests;
import artistenverein.Personenverwaltung.ControllerUserAccount;
import artistenverein.Personenverwaltung.FormUserValidation;
import artistenverein.Personenverwaltung.ManagerUser;

public class ControllerUserAccountTest extends AbstractIntegrationTests {


	@Autowired
	private ControllerUserAccount con;

	@Autowired
	private ManagerUser mangerUser;
	
	@Test
	public void detail() {
		Model model = new ExtendedModelMap();
		assertEquals( "/User/userpage",con.detail(new UserAccount(), model));
	}
	
	@Test
	public void getProfilBearbeiten() {
		Model model = new ExtendedModelMap();
		assertEquals( "User/bearbeiten",con.getProfilBearbeiten(new UserAccount(), model));
	}
	
	@Test
	public void postProfilBearbeiten() {
		
		FormUserValidation form = new FormUserValidation();
		form.setAdress("adrress");
		form.setBeschreibung("beschreibung");
		form.setEmail("email");
		form.setFirstname("firstname");
		form.setLastname("lastname");
		form.setPassword("password");
		form.setTelefon("telefon");
		form.setUsername("username");
		
		mangerUser.createArtist(form);
		BindingResult result = new DirectFieldBindingResult(form, "form");
		
	   assertEquals( "index",con.postProfilBearbeiten(form, result));
		
	}
	
	@Test
	public void getUserErstellen() {
		Model model = new ExtendedModelMap();
		assertEquals( "User/registration",con.getUserErstellen( model));
	}
	
	@Test
	public void postKundeErstellen() {
		
		FormUserValidation form = new FormUserValidation();
		form.setAdress("adrress");
		form.setBeschreibung("beschreibung");
		form.setEmail("email@web.de");
		form.setFirstname("firstname");
		form.setLastname("lastname");
		form.setPassword("password");
		form.setTelefon("telefon");
		form.setUsername("UsedUsername");
		
		mangerUser.createArtist(form);
		BindingResult result = new DirectFieldBindingResult(form, "form");
		assertEquals( "User/registration",con.postKundeErstellen( form,result));
		
		form.setUsername("UnusedUsername");
		assertEquals( "User/registration",con.postKundeErstellen( form,result));
	}
}
