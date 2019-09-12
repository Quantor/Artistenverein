package artistenverein.Personenverwaltung;

import static org.junit.Assert.assertEquals;


import java.time.LocalTime;


import org.junit.Test;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.security.test.context.support.WithMockUser;

import artistenverein.AbstractIntegrationTests;
import artistenverein.Personenverwaltung.ControllerPersonenverwaltung;
import artistenverein.Personenverwaltung.FormGruppenValidation;
import artistenverein.Personenverwaltung.FormUserValidation;
import artistenverein.Personenverwaltung.ManagerUser;

public class ControllerPersonenverwaltungTest extends AbstractIntegrationTests {
	@Autowired
	private ControllerPersonenverwaltung con;
	
	@Autowired
	private ManagerUser managerUser;

	@Test
	public void authorization()
	{
		Model model = new ExtendedModelMap();
		
		try{
			con.getPersonenverwaltung(model);
		}catch(Exception e)
		{
			assertEquals("An Authentication object was not found in the SecurityContext",e.getMessage());
		}
		
		try{
			con.getKundenverwaltung(model);
		}catch(Exception e)
		{
			assertEquals("An Authentication object was not found in the SecurityContext",e.getMessage());
		}
		
		try{
			con.getGruppenverwaltung(model);
		}catch(Exception e)
		{
			assertEquals("An Authentication object was not found in the SecurityContext",e.getMessage());
		}
		

		try{
			con.getGruppeErstellen(model);
		}catch(Exception e)
		{
			assertEquals("An Authentication object was not found in the SecurityContext",e.getMessage());
		}
		
		//Äquivalent für restlichen geschütze Methoden
		
	}
	
	@WithMockUser(authorities = "ROLE_BOSS")
	@Test
	public void postArtist()
	{

		FormUserValidation form = new FormUserValidation();
		form.setAdress("adrress");
		form.setBeschreibung("beschreibung");
		form.setEmail("email");
		form.setFirstname("firstname");
		form.setLastname("lastname");
		form.setPassword("password");
		form.setTelefon("telefon");
		form.setUsername("username");
		managerUser.createArtist(form);
		BindingResult result = new DirectFieldBindingResult(form, "form");
		
	   assertEquals( "redirect:/personenverwaltung/artistenverwaltung",con.postArtist(form, result));
	}
	
	@WithMockUser(authorities = "ROLE_BOSS")
	@Test
	public void postGruppeErstellen()
	{
		FormGruppenValidation form = new FormGruppenValidation();
		form.setGruppenname("gruppenname");
		BindingResult result = new DirectFieldBindingResult(form, "form");
		assertEquals( "redirect:/personenverwaltung/gruppenverwaltung",con.postGruppeErstellen(form, result));
	}
	
	@WithMockUser(authorities = "ROLE_BOSS")
	@Test
	public void allgemeineSperrzeitanlegenWithParameters()
	{
		final UserAccount account = new UserAccount();
		final String datum = "1993-11-3";
		final LocalTime zeit = LocalTime.of(10,10,10);
		final LocalTime dauer = LocalTime.of(11,11,11);
		final String zeitString = zeit.toString();
		final String dauerString = dauer.toString();
		final String haeuf = "monatlich";
		final String nameInput = "username";
		final  String kommentarInput = "Unittest nerven";
		
		assertEquals( "redirect:/personenverwaltung/sperrzeiten", con.allgemeineSperrzeitanlegen(account,datum,zeitString,dauerString,haeuf,nameInput,kommentarInput));
	}
	
	@WithMockUser(authorities = "ROLE_BOSS")
	@Test
	public void getPersonenverwaltung()
	{
		
		Model model = new ExtendedModelMap();
		assertEquals( "Personenverwaltung/artistenverwaltung",con.getPersonenverwaltung(model));
	}
	
	@WithMockUser(authorities = "ROLE_BOSS")
	@Test
	public void getGruppeErstellen()
	{
		
		Model model = new ExtendedModelMap();
		assertEquals( "Personenverwaltung/gruppe/erstellen",con.getGruppeErstellen(model));
	}
	
	@WithMockUser(authorities = "ROLE_BOSS")
	@Test
	public void getKundenverwaltung()
	{
		
		Model model = new ExtendedModelMap();
		assertEquals( "Personenverwaltung/kundenverwaltung",con.getKundenverwaltung(model));
	}
	
	@WithMockUser(authorities = "ROLE_BOSS")
	@Test
	public void getGruppenverwaltung()
	{
		
		Model model = new ExtendedModelMap();
		assertEquals( "/Personenverwaltung/gruppenverwaltung",con.getGruppenverwaltung(model));
	}
	
	@WithMockUser(authorities = "ROLE_BOSS")
	@Test
	public void getArtist() {
		Model model = new ExtendedModelMap();
		assertEquals( "Artisten/artisten",con.getArtisten(model));
	}
	
	@WithMockUser(authorities = "ROLE_BOSS")
	@Test
	public void getGruppen() {
		Model model = new ExtendedModelMap();
		assertEquals( "Artisten/gruppen",con.getGruppen(model));
	}
	
	@WithMockUser(authorities = "ROLE_BOSS")
	@Test
	public void allgemeineSperrzeitLoeschen() {
		
		assertEquals( "redirect:/personenverwaltung/sperrzeiten",con.allgemeineSperrzeitLoeschen());
	}
	
	@WithMockUser(authorities = "ROLE_BOSS")
	@Test
	public void allgemeineSperrzeitanlegen() {
		
		assertEquals( "redirect:/personenverwaltung/sperrzeiten",con.allgemeineSperrzeitanlegen());
	}
	
	@WithMockUser(authorities = "ROLE_BOSS")
	@Test
	public void getArtistErstellen() {
		Model model = new ExtendedModelMap();
		assertEquals( "/Personenverwaltung/register/regArtist" ,con.getArtistErstellen(model));
	}
	
	@WithMockUser(authorities = "ROLE_BOSS")
	@Test
	public void postArtistErstellen()
	{

		FormUserValidation form = new FormUserValidation();
		form.setAdress("adrress");
		form.setBeschreibung("beschreibung");
		form.setEmail("email");
		form.setFirstname("firstname");
		form.setLastname("lastname");
		form.setPassword("password");
		form.setTelefon("telefon");
		form.setUsername("username");
	
		BindingResult result = new DirectFieldBindingResult(form, "form");
		
	   assertEquals( "redirect:/personenverwaltung/artistenverwaltung",con.postArtistErstellen(form, result));
	}

	@WithMockUser(authorities = "ROLE_BOSS")
	@Test
	public void postUserLoeschen()
	{

		FormUserValidation form = new FormUserValidation();
		form.setAdress("adrress");
		form.setBeschreibung("beschreibung");
		form.setEmail("email@web.de");
		form.setFirstname("firstname");
		form.setLastname("lastname");
		form.setPassword("password");
		form.setTelefon("telefon");
		form.setUsername("username2");
	
		managerUser.createArtist(form);
	   assertEquals( "redirect:/personenverwaltung/artistenverwaltung",con.postUserLoeschen(form.getUsername()));
	}

	@WithMockUser(authorities = "ROLE_BOSS")
	@Test
	public void getGruppeMitglieder() {
		Model model = new ExtendedModelMap();
		assertEquals( "/Personenverwaltung/gruppe/gruppe" ,con.getGruppeMitglieder("Jungler",model));
	}
	
	@WithMockUser(authorities = "ROLE_BOSS")
	@Test
	public void postGruppeMitgliedEntfernen() {
		Model model = new ExtendedModelMap();
		assertEquals( "/Personenverwaltung/gruppe/gruppe" ,con.postGruppeMitgliedEntfernen("Tanzbären","cowboy",model));
	}
	
	@WithMockUser(authorities = "ROLE_BOSS")
	@Test
	public void postGruppeMitgliedHinzfuegen() {
		Model model = new ExtendedModelMap();
		assertEquals( "/Personenverwaltung/gruppe/gruppe" ,con.postGruppeMitgliedHinzfuegen("Tanzbären","cowboy",model));
	}
		
	@WithMockUser(authorities = "ROLE_BOSS")
	@Test
	public void postArtistBearbeiten()
	{

		FormUserValidation form = new FormUserValidation();
		form.setAdress("adrress");
		form.setLastname("lastname");
		form.setPassword("password");
		form.setBeschreibung("beschreibung");
		form.setEmail("email@web.de");
		form.setFirstname("firstname");
		form.setTelefon("telefon");
		form.setUsername("username2");
	
		BindingResult result = new DirectFieldBindingResult(form, "form");
		managerUser.createArtist(form);
	   assertEquals( "Personenverwaltung/ok",con.postArtistBearbeiten(form, result));
	}
	
	@WithMockUser(authorities = "ROLE_BOSS")
	@Test
	public void postKundeAlsVorstandBearbeiten()
	{

		FormUserValidation form = new FormUserValidation();
		form.setAdress("adrress");
		form.setBeschreibung("beschreibung");
		form.setEmail("email@web.de");
		form.setFirstname("firstname");
		form.setLastname("lastname");
		form.setPassword("password");
		form.setTelefon("telefon");
		form.setUsername("username3");
	
		BindingResult result = new DirectFieldBindingResult(form, "form");
		managerUser.createUser(form);
	   assertEquals( "Personenverwaltung/ok",con.postKundeAlsVorstandBearbeiten(form, result));
	}
	
	@WithMockUser(authorities = "ROLE_BOSS")
	@Test
	public void postKundeEntfernen()
	{

		FormUserValidation form = new FormUserValidation();
		form.setAdress("adrress");
		form.setBeschreibung("beschreibung");
		form.setEmail("email@web.de");
		form.setFirstname("firstname");
		form.setLastname("lastname");
		form.setPassword("password");
		form.setTelefon("telefon");
		form.setUsername("username2");
	
		managerUser.createArtist(form);
	   assertEquals( "Personenverwaltung/ok",con.postKundeEntfernen(form.getUsername()));
	}
		
}
