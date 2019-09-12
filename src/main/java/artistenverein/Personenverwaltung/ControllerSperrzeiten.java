package artistenverein.Personenverwaltung;

import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import artistenverein.Zeitverwaltung.ArtistSperrZeit;
import artistenverein.Zeitverwaltung.Haeufigkeit;
import artistenverein.Zeitverwaltung.ZeitRepository;
import artistenverein.Zeitverwaltung.Zeitverwaltung;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Created by Ruby on 12/5/17.
 */
@Controller
public class ControllerSperrzeiten {

	private final ManagerUser artistManagement;
	private final Zeitverwaltung zeitverwaltung;
	private final ZeitRepository zeitRepository;

	private static final int JAHR_ID = 0;
	private static final int MONAT_ID = 1;
	private static final int TAG_ID = 2;

	private final static int MIN_PRO_STUNDE = 60;

	/**
	 * Konstruktor
	 *
	 * @param artistManagement
	 *            Spring Autowired - Instanz des MangerUser
	 *
	 * @param zeitverwaltung
	 *            Spring Autowired - Instanz der Zeitverwaltung
	 * 
	 * @param zeitRepository
	 *            Spring Autowired - Instanz des ZeitRepository
	 **/
	@Autowired
	ControllerSperrzeiten(ManagerUser artistManagement, Zeitverwaltung zeitverwaltung, ZeitRepository zeitRepository) {
		Assert.notNull(artistManagement, "artistManagement darf nicht null sein!");
		Assert.notNull(zeitverwaltung, "zeitverwaltung darf nicht null sein!");
		Assert.notNull(zeitRepository, "zeitRepository darf nicht null sein!");

		this.artistManagement = artistManagement;
		this.zeitverwaltung = zeitverwaltung;
		this.zeitRepository = zeitRepository;
	}

	/**
	 * dient zum Anzeigen der Sperrzeiten des Artisten
	 *
	 * @param userAccount
	 *            der eingeloggte Nutzer
	 * 
	 * @param model
	 *            Spring Model
	 * 
	 * @return String zur Templatebestimmung
	 **/
	@GetMapping("/sperrzeiten")
	public String sperrzeiten(@LoggedIn UserAccount userAccount, Model model) {
		User user = artistManagement.findeUserAccount(userAccount);
		model.addAttribute("user", user);

		model.addAttribute("zeiten", zeitRepository.getSperrzeiten(userAccount));

		return "Zeitverwaltung/sperrzeiten";
	}

	/**
	 * dient zum löschen einer Sperrzeite des Artisten
	 *
	 * @param userAccount
	 *            der eingeloggte Nutzer
	 * 
	 * @param model
	 *            Spring Model
	 * 
	 * @param Id
	 *            die id der zu löschenden Sperrzeit
	 * 
	 * @return String zur Templatebestimmung
	 **/
	@PostMapping("/sperrzeitloeschen")
	public String sperrzeitloeschen(@LoggedIn UserAccount userAccount, Model model, @RequestParam("item") long Id) {
		User user = artistManagement.findeUserAccount(userAccount);

		model.addAttribute("user", user);

		ArtistSperrZeit sperrzeit = zeitRepository.findOne(Id);
		zeitRepository.delete(sperrzeit);

		model.addAttribute("zeiten", zeitRepository.getSperrzeiten(userAccount));

		return "Zeitverwaltung/sperrzeiten";
	}

	/**
	 * dient zum Erstellen einer Sperrzeit des Artisten
	 *
	 * @param userAccount
	 *            der eingeloggte Nutzer
	 *            
	 * @param model
	 *            Spring Model
	 *            
	 * @param datum
	 *            das Datum der Sperrzeit          
	 *            
	 * @param startzeitString
	 *            die Startzeit der Sperrzeit
	 *            
	 * @param dauerString
	 *            die Dauer der Sperrzeit
	 *            
	 * @param haeuf
	 *            die Haeufigkeit der Sperrzeit
	 *            
	 * @param nameInput
	 *            der Name der Sperrzeit
	 *            
	 * @param kommentarInput
	 *            der Kommentar der Sperrzeit          
	 *            
	 * @return String zur Templatebestimmung
	 **/
	@PostMapping("/sperrzeiterstellen")
	public String sperrzeiterstellen(@LoggedIn UserAccount userAccount, Model model,
			@RequestParam("datum") String datum, @RequestParam("zeitvon") String startzeitString,
			@RequestParam("dauer") String dauerString, @RequestParam("haeufigkeit") String haeuf,
			@RequestParam("name") String nameInput, @RequestParam("kommentar") String kommentarInput) {

		User user = artistManagement.findeUserAccount(userAccount);
		model.addAttribute("user", user);

		Haeufigkeit h = Haeufigkeit.of(haeuf);
		if (h == null) {
			return "/error";
		}

		LocalTime startzeit = LocalTime.parse(startzeitString);
		LocalTime dauer = LocalTime.parse(dauerString);

		LocalDate d = LocalDate.of(Integer.parseInt(datum.split("-")[JAHR_ID]), // jahr
				Integer.parseInt(datum.split("-")[MONAT_ID]), // monat
				Integer.parseInt(datum.split("-")[TAG_ID])); // tag
		/*
		 * LocalTime t = LocalTime.of(Integer.parseInt(zeit.split(":")[0]),
		 * Integer.parseInt(zeit.split(":")[1])); Duration dur =
		 * Duration.ofMinutes(Integer.parseInt(dauer.split(":")[0]) * 60 +
		 * Integer.parseInt(dauer.split(":")[1]));
		 */
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

		// Duration dauer = Duration.between(startzeit, endzeit);

		zeitverwaltung.sperrzeitEintragen(LocalDateTime.of(d, startzeit),
				Duration.ofMinutes((long) dauer.getHour() * MIN_PRO_STUNDE + dauer.getMinute()), h, name, kommentar,
				userAccount);

		model.addAttribute("zeiten", zeitRepository.getSperrzeiten(userAccount));

		return "redirect:/sperrzeiten";
	}
}
