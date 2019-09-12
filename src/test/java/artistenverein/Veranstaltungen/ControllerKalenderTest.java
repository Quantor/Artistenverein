package artistenverein.Veranstaltungen;

import static org.junit.Assert.*;
import static org.salespointframework.core.Currencies.EURO;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.javamoney.moneta.Money;
import org.junit.Before;
import org.junit.Test;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import artistenverein.AbstractIntegrationTests;
import artistenverein.Personenverwaltung.Artistengruppe;
import artistenverein.Personenverwaltung.RepositoryGruppen;
import artistenverein.Personenverwaltung.RepositoryUser;
import artistenverein.Personenverwaltung.User;
import artistenverein.Veranstaltungen.Buchung;
import artistenverein.Veranstaltungen.BuchungRepository;
import artistenverein.Veranstaltungen.ControllerKalender;
import artistenverein.Veranstaltungen.EntityVeranstaltung;
import artistenverein.Veranstaltungen.VeranstaltungsKatalog;
import artistenverein.Veranstaltungen.EntityVeranstaltung.VeranstaltungsType;

public class ControllerKalenderTest extends AbstractIntegrationTests {

	@Autowired
	private ControllerKalender controllerKalender;
	@Autowired
	private RepositoryUser repositoryUser;
	@Autowired
	private BuchungRepository buchungRepository;
	@Autowired
	private VeranstaltungsKatalog veranstaltungskatalog;
	@Autowired
	private RepositoryGruppen gruppenRepository;
	@Autowired
	private UserAccountManager userAccountManager;
	
	LocalDateTime start = LocalDateTime.of(2017, 12, 24, 19, 30);
	LocalDateTime end = LocalDateTime.of(2018, 12, 31, 23, 59);
	
	@Before
	public void setUp() {
		buchungRepository.deleteAll();
		veranstaltungskatalog.deleteAll();
		gruppenRepository.deleteAll();
		repositoryUser.deleteAll();
		
		Role customerRole = Role.of("ROLE_ARTIST");
		
		UserAccount ua1 = userAccountManager.create("genji2", "123", customerRole);
		ua1.setEmail("NeedHealing@gmail.de");
		ua1.setLastname("Shimada");
		ua1.setFirstname("Genji");
		userAccountManager.save(ua1);

		UserAccount ua2 = userAccountManager.create("lucio2", "123", customerRole);
		ua2.setEmail("boostio@web.de");
		ua2.setLastname("Correia ");
		ua2.setFirstname("Lúcio");
		userAccountManager.save(ua2);

		UserAccount ua3 = userAccountManager.create("beepBop2", "123", customerRole);
		ua3.setEmail("playOfTheGame@gmail.de");
		ua3.setLastname("Bastion");
		ua3.setFirstname("Sepp");
		userAccountManager.save(ua3);
		
		User c1 = new User(ua1);
		User c2 = new User(ua2);
		User c3 = new User(ua3);
		
		repositoryUser.save(Arrays.asList(c1, c2, c3));
		
		Artistengruppe gruppe1 = new Artistengruppe("Tanzbären");
		gruppe1.addMitglied(c1);
		gruppe1.addMitglied(c2);
		gruppe1.addMitglied(c3);
		
		gruppenRepository.save(gruppe1);
		
		Set<UserAccount> artistengruppe = new HashSet<>();
		artistengruppe.add(ua1);
		artistengruppe.add(ua2);
		artistengruppe.add(ua3);
		
		veranstaltungskatalog.save(new EntityVeranstaltung("Jongliershow", Money.of(100, EURO),
				"1-2-3 jonglieren ist Zauberei", 200, VeranstaltungsType.SHOW, start, end, artistengruppe, 1, "Tanzbären"));
	}
	
	@Test
	public void getKalenderKundeTest() {

		Model model = new ExtendedModelMap();
		UserAccount userAccount = repositoryUser.findAll().iterator().next().getUserAccount();
		Optional<UserAccount> optionalUserAccount = Optional.of(userAccount);	
		EntityVeranstaltung v1 = veranstaltungskatalog.findByName("Jongliershow").iterator().next();
		Buchung b1 = new Buchung("Halle", v1.getStartDatum(), v1, userAccount, v1.getPrice(), UUID.randomUUID());
		buchungRepository.save(b1);
		String returnedView = controllerKalender.getKalenderKunde(model, "true", "true", optionalUserAccount);
		assertEquals(returnedView, "/Kalender/kalender");
		returnedView = controllerKalender.getKalenderKunde(model, "false", "false", optionalUserAccount);
		assertEquals(returnedView, "/Kalender/kalender");
	}
}
