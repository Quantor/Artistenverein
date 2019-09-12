package artistenverein.Personenverwaltung;

import static org.junit.Assert.*;

import java.time.LocalDateTime;

import java.util.HashSet;
import java.util.Iterator;

import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;

import org.springframework.beans.factory.annotation.Autowired;

import artistenverein.AbstractIntegrationTests;
import artistenverein.Highlights.Highlight;
import artistenverein.Personenverwaltung.Artistengruppe;
import artistenverein.Personenverwaltung.FormGruppenValidation;
import artistenverein.Personenverwaltung.FormUserValidation;
import artistenverein.Personenverwaltung.ManagerUser;
import artistenverein.Personenverwaltung.RepositoryGruppen;
import artistenverein.Personenverwaltung.RepositoryUser;
import artistenverein.Personenverwaltung.User;
import artistenverein.Veranstaltungen.Bewertung;
import artistenverein.Veranstaltungen.Buchung;
import artistenverein.Veranstaltungen.EntityVeranstaltung;

public class ManagerUserTest extends AbstractIntegrationTests {

	@Autowired
	private ManagerUser managerUser;
	@Autowired
	private RepositoryGruppen gruppenRepository;
	@Autowired
	private RepositoryUser userRepository;

	static FormUserValidation form = new FormUserValidation();

	@BeforeClass
	public static void setUp() {
		form.setAdress("adrress");
		form.setBeschreibung("beschreibung");
		form.setEmail("email");
		form.setFirstname("firstname");
		form.setLastname("lastname");
		form.setPassword("password");
		form.setTelefon("telefon");
		form.setUsername("username");
	}

	@Test
	public void createGruppeTest() {
		FormGruppenValidation form = new FormGruppenValidation();
		form.setGruppenname("gruppenname");
		managerUser.createGruppe(form);
		Iterator<Artistengruppe> it = gruppenRepository.findAll().iterator();
		Artistengruppe a = null;
		while (it.hasNext()) {
			a = it.next();
			if (a.getGruppenname().equals("gruppenname")) {
				break;
			}
		}
		assertEquals(a.getGruppenname(), "gruppenname");
	}

	@Test
	public void createArtistTest() {
		managerUser.createArtist(form);
		Iterator<User> it = userRepository.findAll().iterator();
		User u = new User(new UserAccount());
		while (it.hasNext()) {
			u = it.next();
			if (u.getUserAccount().getUsername().equals("username")) {
				break;
			}
		}
		assertEquals(u.getUserAccount().getRoles().iterator().next(), Role.of("ROLE_ARTIST"));
		assertEquals(u.getUserAccount().getUsername(), "username");
	}

	@Test
	public void createUserTest() {
		managerUser.createUser(form);
		Iterator<User> it = userRepository.findAll().iterator();
		User u = new User(new UserAccount());
		while (it.hasNext()) {
			u = it.next();
			if (u.getUserAccount().getUsername().equals("username")) {
				break;
			}
		}
		assertEquals(u.getUserAccount().getRoles().iterator().next(), Role.of("ROLE_CUSTOMER"));
		assertEquals(u.getUserAccount().getUsername(), "username");
	}

	@Test
	public void createVorstandTest() {
		managerUser.createVorstand(form);
		Iterator<User> it = userRepository.findAll().iterator();
		User u = new User(new UserAccount());
		while (it.hasNext()) {
			u = it.next();
			if (u.getUserAccount().getUsername().equals("username")) {
				break;
			}
		}
		assertEquals(u.getUserAccount().getRoles().iterator().next(), Role.of("ROLE_BOSS"));
		assertEquals(u.getUserAccount().getUsername(), "username");
	}

	@Test
	public void deleteUserTest() {
		User user = null;
		managerUser.createUser(form);

		for (User u : managerUser.findAllUser()) {
			if (u.getUserAccount().getUsername().equals("username")) {
				user = u;
			}
		}
		assertTrue(user != null);

		managerUser.deleteUser(user);

		Set<User> tempList = new HashSet<User>();
		for (User u : managerUser.findAllUser()) {
			tempList.add(u);
		}

		assertTrue("Liste darf Test User nach Löschen nicht enthalten!", !tempList.contains(user));

	}

	@Test
	public void GroupFunction() {
		managerUser.createArtist(form);
		FormGruppenValidation form = new FormGruppenValidation();
		form.setGruppenname("gruppenname");
		managerUser.createGruppe(form);

		Artistengruppe gruppe = null;
		;
		User user = null;
		for (Artistengruppe g : managerUser.findAllGruppen()) {
			if (g.getGruppenname() == "gruppenname")
				;
			{
				gruppe = g;
			}

		}

		for (User u : managerUser.findAllUser()) {
			if (u.getUserAccount().getUsername().equals("username")) {
				user = u;
			}
		}
		managerUser.save(user);

		managerUser.addToGroup(gruppe, user);
		assertTrue("Artist muss Gruppe hinzufügt werden können", gruppe.getMitglieder().contains(user));

		Set<Artistengruppe> gset = new HashSet<Artistengruppe>();
		for (Artistengruppe g : managerUser.findeGruppenZuArtist(user)) {
			gset.add(g);

		}
		assertTrue(gset.contains(gruppe));

		managerUser.removeFromGroup(gruppe, user);
		assertTrue("Artist muss Gruppe hinzufügt werden können", !gruppe.getMitglieder().contains(user));

		Artistengruppe searchg = managerUser.sucheGruppeMitGruppenname("gruppenname");

		assertTrue(gruppe.equals(searchg));
	}

	@Test
	public void editArtist() {
		managerUser.createArtist(form);
		User user = null;
		for (User u : managerUser.findAllUser()) {
			if (u.getUserAccount().getUsername().equals("username")) {
				user = u;
			}
		}

		assertTrue(user.getUserAccount().getUsername() == "username");
		assertTrue(user.getUserAccount().getFirstname() == "firstname");
		assertTrue(user.getUserAccount().getLastname() == "lastname");
		assertTrue(user.getUserAccount().getEmail() == "email");

		FormUserValidation newform = new FormUserValidation();

		newform.setAdress("adrress");
		newform.setBeschreibung("beschreibung");
		newform.setEmail("newemail");
		newform.setFirstname("newfirstname");
		newform.setLastname("newlastname");
		newform.setPassword("password");
		newform.setTelefon("telefon");
		newform.setUsername("username");

		managerUser.editArtist(newform);

		assertTrue(user.getUserAccount().getUsername() == "username");
		assertTrue(user.getUserAccount().getFirstname() == "newfirstname");
		assertTrue(user.getUserAccount().getLastname() == "newlastname");
		assertTrue(user.getUserAccount().getEmail() == "newemail");
	}

	@Test
	public void SearchCommon() {
		// Methode geerbt von Super, annahme auf korrektheit
		managerUser.findAllUser();
		managerUser.findAllGruppen();
		managerUser.getBuchungen();

		for (User a : managerUser.findArtist()) {
			assertTrue("findArtist() sollte nur User zurückgeben die Artisten sind",
					a.getUserAccount().hasRole(Role.of("ROLE_ARTIST")));
		}

		for (User a : managerUser.findKunde()) {
			assertTrue("findKunde() sollte nur User zurückgeben die Kunden sind",
					a.getUserAccount().hasRole(Role.of("ROLE_CUSTOMER")));
		}

	}

	@Test
	public void findUser() {

		User user = null;
		managerUser.createUser(form);

		for (User u : managerUser.findAllUser()) {
			if (u.getUserAccount().getUsername().equals("username")) {
				user = u;
			}
		}
		assertTrue(user != null);

		User search = managerUser.findeUserAccount(user.getUserAccount());

		assertTrue(user.equals(search));
	}

	@Test
	public void Veranstaltungen() {
		User user = null;
		for (User u : managerUser.findAllUser()) {
			if (u.getUserAccount().getUsername().equals("genji")) {
				user = u;
			}
		}
		managerUser.findeVeranstaltungenZuUser(user);
		Set<EntityVeranstaltung> vset = new HashSet<EntityVeranstaltung>();
		for (EntityVeranstaltung v : managerUser.findeVeranstaltungenZuUser(user)) {
			vset.add(v);
		}

		assertTrue(!vset.isEmpty());

		Set<Buchung> bset = new HashSet<Buchung>();
		for (Buchung v : managerUser.findeBuchungenZuUser(user)) {
			bset.add(v);
		}

		Bewertung bw = new Bewertung("test", 5, LocalDateTime.of(2017, 11, 27, 19, 30), user.getUserAccount());

		Iterator<EntityVeranstaltung> i = vset.iterator();
		EntityVeranstaltung v = i.next();
		Highlight h = new Highlight(v, bw);
		assertTrue(h != null);
		
		assertEquals(h.getVeranstaltung(),v);
		assertEquals(h.getKunde(), bw.getAutor());
		assertEquals(h.getBewertung(),bw);



	}
	
	@Test 
	public void Buchungen()
	{
		 final UserAccount user = new UserAccount();
		 
		Set<UserAccount> artisten = new HashSet<>();
		 managerUser.createArtist(form);
		 User a = managerUser.sucheUserMitUsername(form.getUsername());
		 artisten.add(a.getUserAccount());
//		 final LocalDateTime datum = LocalDateTime.of(2018, 05,24, 19, 30);
//		 final LocalDateTime endDatum = LocalDateTime.of(2018, 05, 24, 22, 50);
//		 final EntityVeranstaltung veranstaltung = new EntityVeranstaltung("Test", 
//				Money.of(200, EURO), "TestBeschreibung", 200, VeranstaltungsType.SHOW,
//				datum.minusHours(50), datum.plusHours(200), artisten, 1);
		
//		 final Buchung buchung = new Buchung("TestOrt", datum, veranstaltung, 
//				user, veranstaltung.getPrice(), UUID.randomUUID());
		
		 managerUser.findeKundenZuArtist(a);
		for(Buchung b: managerUser.findeBuchungenZuUser(managerUser.findeUserAccount(user)))
		{
			assertTrue(b.getKunde().equals(user));
		}
		
		for(Buchung b: managerUser.getBuchungenZuKunde(user))
		{
			assertTrue(b.getKunde().equals(user));
		}

	}
		
}

