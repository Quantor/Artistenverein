package artistenverein.Personenverwaltung;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import javax.validation.Valid;

import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import artistenverein.Zeitverwaltung.ArtistSperrZeit;
import artistenverein.Zeitverwaltung.Haeufigkeit;
import artistenverein.Zeitverwaltung.ZeitRepository;
import artistenverein.Zeitverwaltung.Zeitverwaltung;

/**
 * 
 * Der Controller ControllerPersonenverwaltung ist ein Spring-Controller. Er ist
 * zuständig für alle Anfrage im Bezug auf User und Daten die direkt mit deren
 * Konten verküpft sind
 * 
 * @author Tizian Fischer
 * @version 1.0
 */
@Controller
public class ControllerPersonenverwaltung {

	private final ManagerUser managerUser;
	private final ZeitRepository sperrzeiten;
	private final Zeitverwaltung zeitverwaltung;
	
	private final static int MIN_PRO_STUNDE = 60;

	/**
	 * Konstruktor
	 *
	 * @param artistManagement
	 *            Spring Autowired - Instanz des MangerUser
	 *
	 * @param sperrzeiten
	 *            Spring Autowired - Instanz des ZeitRepository
	 *
	 * @param zeitverwaltung
	 *            Spring Autowired - Instanz der Zeitverwaltung
	 **/
	@Autowired
	ControllerPersonenverwaltung(ManagerUser artistManagement, ZeitRepository sperrzeiten,
			Zeitverwaltung zeitverwaltung) {

		Assert.notNull(artistManagement, "artistManagement must not be null!");

		this.managerUser = artistManagement;
		this.sperrzeiten = sperrzeiten;
		this.zeitverwaltung = zeitverwaltung;
	}

	/**
	 * Mapping Übersichtsseite für Artisten Typ: Get
	 * 
	 * @param model
	 */
	@GetMapping("/artisten")
	public String getArtisten(Model model) {

		model.addAttribute("userList", managerUser.findArtist());

		return "Artisten/artisten";
	}

	/**
	 * Mapping Übersichtsteite für Artistengruppen Typ: Get
	 * 
	 * @param model
	 */
	@GetMapping("/artistengruppen")
	public String getGruppen(Model model) {

		model.addAttribute("gruppen", managerUser.findAllGruppen());

		return "Artisten/gruppen";
	}

	/**
	 * Mapping Übersicht über alle Angestellten Artisten für Verwaltung bzw Boss
	 * Typ: Get Model: Fügt Model alle Artisten hinzu
	 * 
	 * @param model
	 * @return
	 */
	//@PreAuthorize("hasRole('ROLE_BOSS')")
	@GetMapping("/personenverwaltung/artistenverwaltung")
	public String getPersonenverwaltung(Model model) {

		model.addAttribute("userList", managerUser.findArtist());

		return "Personenverwaltung/artistenverwaltung";
	}

	/**
	 * Mapping Übersicht über alle registriereten Kunden Typ: Get Model: Fügt Model
	 * alle Kunden hinzu
	 * 
	 * @param model
	 * @return
	 */
	//@PreAuthorize("hasRole('ROLE_BOSS')")
	@GetMapping("/personenverwaltung/kundenverwaltung")
	public String getKundenverwaltung(Model model) {
		model.addAttribute("userList", managerUser.findKunde());

		return "Personenverwaltung/kundenverwaltung";
	}

	/**
	 * Mapping Übersicht über alle erstellten Typ: Get Model: Fügt Model alle
	 * Gruppen hinzu
	 * 
	 * @param model
	 */
	//@PreAuthorize("hasRole('ROLE_BOSS')")
	@GetMapping("personenverwaltung/gruppenverwaltung")
	public String getGruppenverwaltung(Model model) {

		model.addAttribute("gruppenList", managerUser.findAllGruppen());
		model.addAttribute("artistenList", managerUser.findArtist());
		return "/Personenverwaltung/gruppenverwaltung";

	}

	/**
	 * Mapping für die Bearbeitung einen Artisten durch die Verwaltung Typ: Get
	 * Model: fügt dem Model einen User hinzu, sowie eine Form für die Überprüfung
	 * der Daten
	 * 
	 * @param username
	 *            Username des zu bearbeitenden Users
	 * @param model
	 * @return
	 */
	//@PreAuthorize("hasRole('ROLE_BOSS')")
	@GetMapping("personenverwaltung/artist/bearbeiten/{username}")
	public String getArtistBearbeiten(@PathVariable String username, Model model) {
		model.addAttribute("user", managerUser.sucheUserMitUsername(username));

		model.addAttribute("userForm", new FormUserValidation());
		return "/Personenverwaltung/bearbeiten/beaArtist";
	}

	/**
	 * Mapping für die Bearbeitung einen Artisten durch die Verwaltung Typ:Post
	 * 
	 * @param userForm
	 *            UserForm zur berreinigung und validierung des Userinputs für die
	 *            Registrations eines Artisten
	 * @param result
	 *            Die aus der UserForm enstehnde Überprüfung der Eingaben
	 */
	@PreAuthorize("hasRole('ROLE_BOSS')")
	@PostMapping("/personenverwaltung/artist/bearbeiten/submit")
	public String postArtist(@ModelAttribute("userForm") @Valid FormUserValidation userForm, BindingResult result) {

		if (result.hasErrors()) {
			return "/Personenverwaltung/bearbeiten/beaArtist";
		}

		System.out.println(userForm.getUsername());
		managerUser.editArtist(userForm);

		return "redirect:/personenverwaltung/artistenverwaltung";
	}

	/**
	 * Mapping für das Erstellen einer Gruppe durch die Verwaltung Typ: Get Model:
	 * Fügt dem Model eine leere UserForm für die validierung der Eingabe hinzu
	 * 
	 * @param model
	 * @return
	 */
	@PreAuthorize("hasRole('ROLE_BOSS')")
	@GetMapping("/personenverwaltung/gruppen/erstellen")
	public String getGruppeErstellen(Model model) {

		model.addAttribute("userForm", new FormGruppenValidation());
		return "Personenverwaltung/gruppe/erstellen";

	}

	/**
	 * Mapping für das Erstellen einer Gruppe durch die Verwaltung Typ:Post
	 * 
	 * @param userForm
	 *            UserForm zur berreinigung und validierung des Userinputs für die
	 *            Registrations eines Artisten
	 * @param result
	 *            Die aus der UserForm enstehnde Überprüfung der Eingaben
	 * @return
	 */
	@PreAuthorize("hasRole('ROLE_BOSS')")
	@PostMapping("/personenverwaltung/gruppen/erstellen")
	public String postGruppeErstellen(@ModelAttribute("userForm") @Valid FormGruppenValidation userForm,
			BindingResult result) {

		if (result.hasErrors()) {
			return "Personenverwaltung/gruppe/erstellen";
		}

		managerUser.createGruppe(userForm);
		return "redirect:/personenverwaltung/gruppenverwaltung";
	}

	/**
	 * Mapping für die Erstellung eines Artisten durch die Verwaltung Typ: Get
	 * Model: Fügt dem Model eine leere UserForm für die validierung der Eingabe
	 * hinzu
	 * 
	 * @param model
	 * @return
	 */
	@PreAuthorize("hasRole('ROLE_BOSS')")
	@GetMapping("personenverwaltung/personenverwaltung/artist/registrieren")
	public String getArtistErstellen(Model model) {
		model.addAttribute("userForm", new FormUserValidation());
		return "/Personenverwaltung/register/regArtist";
	}

	/**
	 * Mapping für die Erstellung eines Artisten durch die Verwaltung Typ:Post
	 * 
	 * @param userForm
	 *            UserForm zur berreinigung und validierung des Userinputs für die
	 *            Registrations eines Artisten
	 * @param result
	 *            Die aus der UserForm enstehnde Überprüfung der Eingaben
	 * @return
	 */
	@PreAuthorize("hasRole('ROLE_BOSS')")
	@PostMapping("/personenverwaltung/artist/registrieren")
	public String postArtistErstellen(@ModelAttribute("userForm") @Valid FormUserValidation userForm,
			 BindingResult result) {

		if (result.hasErrors()) {
			return "/Personenverwaltung/register/regArtist";
		}

		if (!managerUser.createArtist(userForm)) {
			result.addError(new FieldError(result.getObjectName(), "username", "Username existiert bereits"));
			return "/Personenverwaltung/register/regArtist";
		}

		return "redirect:/personenverwaltung/artistenverwaltung";
	}

	/**
	 * Mapping für die Entfernung von Artisten durch die Verwaltung
	 * Typ:Post
	 * @param artistUsername 
	 * 		Name des Artisten
	 * @return
	 */
	@PreAuthorize("hasRole('ROLE_BOSS')")
	@PostMapping("personenverwaltung/personenverwaltung/artist/entfernen")
	public String postUserLoeschen(@RequestParam("username") String artistUsername) {

		User user = managerUser.sucheUserMitUsername(artistUsername);
		managerUser.deleteUser(user);

		return "redirect:/personenverwaltung/artistenverwaltung";
	}

	/**
	 * Mapping für die Übersicht über eine Gruppe und deren Artisten
	 * Typ: Get
	 * Model: Fügt dem Model die Gruppe, die Mitglieder der Gruppen und alle Artisten hinzu
	 * @param gruppenname
	 * 		Name der Gruppe die bearbeitet werden soll
	 * @param model
	 * 		
	 * @return
	 */
	@PreAuthorize("hasRole('ROLE_BOSS')")
	@GetMapping("personenverwaltung/personenverwaltung/gruppen/{gruppenname}/mitglieder")
	public String getGruppeMitglieder(@PathVariable String gruppenname, Model model) {
		Artistengruppe gruppe = managerUser.sucheGruppeMitGruppenname(gruppenname);
		Set<User> mitglieder = gruppe.getMitglieder();
		Set<User> artisten = new HashSet<>();

		for (User artist : managerUser.findArtist()) {
			if (!mitglieder.contains(artist)) {
				artisten.add(artist);
			}
		}
		model.addAttribute("gruppenname", gruppe.getGruppenname());
		model.addAttribute("mitglieder", mitglieder);
		model.addAttribute("artisten", artisten);

		return "/Personenverwaltung/gruppe/gruppe";
	}

	/**
	 * Mapping für das Hinzufügen und Entfernen von Artisten aus Artistengruppen
	 * Typ:Post
	 * @param gruppenname
	 *		 Name der Gruppe die bearbeitet werden soll
	 * @param username
	 * 		Der User oder entfernt werden soll
	 * @param model
	 * @return
	 */
	@PreAuthorize("hasRole('ROLE_BOSS')")
	@PostMapping("/personenverwaltung/gruppen/{gruppenname}/entfernen")
	public String postGruppeMitgliedEntfernen(@PathVariable String gruppenname,
			@RequestParam("username") String username, Model model) {

		managerUser.removeFromGroup(managerUser.sucheGruppeMitGruppenname(gruppenname),
				managerUser.sucheUserMitUsername(username));

		return getGruppeMitglieder(gruppenname, model);
	}

	/**
	 * Äquivalent zu Entfernen von Gruppenmitgliedern
	 * @param gruppenname
	 * @param username
	 * @param model
	 * @return
	 */
	@PreAuthorize("hasRole('ROLE_BOSS')")
	@PostMapping("/personenverwaltung/gruppen/{gruppenname}/hinzufuegen")
	public String postGruppeMitgliedHinzfuegen(@PathVariable String gruppenname,
			@RequestParam("username") String username, Model model) {

		managerUser.addToGroup(managerUser.sucheGruppeMitGruppenname(gruppenname),
				managerUser.sucheUserMitUsername(username));

		return getGruppeMitglieder(gruppenname, model);
	}

	/**
	 * Äquivalent zu bearbeiten von Aritsten -> Kunden
	 * @param userForm
	 * @param result
	 * @return
	 */
	@PreAuthorize("hasRole('ROLE_BOSS')")
	@PostMapping("personenverwaltung/artist/user/bearbeiten")
	public String postArtistBearbeiten(@ModelAttribute("userForm") @Valid FormUserValidation userForm,
			BindingResult result) {

		if (result.hasErrors()) {
			return "user/bearbeiten";
		}

		managerUser.editArtist(userForm);
		return "Personenverwaltung/ok";
	}

	/**
	 * Äquivalent zu bearbeiten von Aritsten -> Kunden
	 * @param userForm
	 * @param result
	 * @return
	 */
	@GetMapping("personenverwaltung/artist/user/bearbeiten")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	@PostMapping("personenverwaltung/kunde/user/bearbeiten")
	public String postKundeAlsVorstandBearbeiten(@ModelAttribute("userForm") @Valid FormUserValidation userForm,
			BindingResult result) {

		if (result.hasErrors()) {
			return "user/bearbeiten";
		}

		managerUser.editArtist(userForm);
		return "Personenverwaltung/ok";
	}

	/**
	 * Äquivalent zu entfernen von Aritsten -> Kunden
	 * @param artistUsername
	 * @return
	 */
	@PreAuthorize("hasRole('ROLE_BOSS')")
	@PostMapping("personenverwaltung/kunde/entfernen")
	public String postKundeEntfernen(@RequestParam("username") String artistUsername) {

		for (User user : managerUser.findAllUser()) {

			if (user.getUserAccount().getUsername().equals(artistUsername)) {
				managerUser.deleteUser(user);
			}
		}
		return "Personenverwaltung/ok";
	}

	/**
	 * 
	 * @param model
	 * @param boss
	 * @return
	 */
	@PreAuthorize("hasRole('ROLE_BOSS')")
	@GetMapping("personenverwaltung/sperrzeiten")
	public String sperrzeiten(Model model, @LoggedIn UserAccount boss) {
		model.addAttribute("sperrzeiten", sperrzeiten.getSperrzeiten(boss));
		return "Personenverwaltung/sperrzeiten";
	}

	/**
	 * 
	 * @return
	 */
	@GetMapping("personenverwaltung/sperrzeiten/anlegen")
	public String allgemeineSperrzeitanlegen() {
		return "redirect:/personenverwaltung/sperrzeiten";
	}

	/**
	 * 
	 * @param boss
	 * @param datum
	 * @param zeit
	 * @param dauer
	 * @param haeuf
	 * @param nameInput
	 * @param kommentarInput
	 * @return
	 */
	@PreAuthorize("hasRole('ROLE_BOSS')")
	@PostMapping("personenverwaltung/sperrzeiten/anlegen")
	public String allgemeineSperrzeitanlegen(@LoggedIn UserAccount boss, @RequestParam("datum") String datum,
			@RequestParam("zeit") String zeitString, @RequestParam("dauer") String dauerString,
			@RequestParam("haeufigkeit") String haeuf, @RequestParam("name") String nameInput,
			@RequestParam("kommentar") String kommentarInput) {

		Haeufigkeit h = Haeufigkeit.of(haeuf);
		if (h == null) {
			return "/error";
		}
		
		LocalTime zeit = LocalTime.parse(zeitString);
		LocalTime dauer = LocalTime.parse(dauerString);

		LocalDate d = LocalDate.of(Integer.parseInt(datum.split("-")[0]), // jahr
				Integer.parseInt(datum.split("-")[1]), // monat
				Integer.parseInt(datum.split("-")[2]));

		String name;
		String kommentar;
		if (nameInput == null) {
			name = "";
		} else {
			name = nameInput;
		}
		if (kommentarInput == null) {
			kommentar = "";
		} else {
			kommentar = kommentarInput;
		}

		zeitverwaltung.sperrzeitEintragen(LocalDateTime.of(d, zeit),
				Duration.ofMinutes((long) dauer.getHour() * MIN_PRO_STUNDE + dauer.getMinute()), h, name, kommentar, boss);
		return "redirect:/personenverwaltung/sperrzeiten";
	}
	/**
	 * 
	 * @return
	 */
	@GetMapping("personenverwaltung/sperrzeiten/loeschen")
	public String allgemeineSperrzeitLoeschen() {
		return "redirect:/personenverwaltung/sperrzeiten";
	}
 /**
  * 
  * @param id
  * @return
  */
	@PreAuthorize("hasRole('ROLE_BOSS')")
	@PostMapping("personenverwaltung/sperrzeiten/loeschen")
	public String allgemeineSperrzeitLoeschen(@RequestParam("item") long id) {
		ArtistSperrZeit sperrzeit = sperrzeiten.findOne(id);
		sperrzeiten.delete(sperrzeit);
		return "redirect:/personenverwaltung/sperrzeiten";
	}
	
}
