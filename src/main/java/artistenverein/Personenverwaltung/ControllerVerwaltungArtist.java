
package artistenverein.Personenverwaltung;

import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import artistenverein.Veranstaltungen.Buchung;
import artistenverein.Veranstaltungen.BuchungRepository;
import artistenverein.Veranstaltungen.Kalender;


/**
 * @author Tizian Fischer
 * @version 1.0
 */
@Controller
public class ControllerVerwaltungArtist {

	private final ManagerUser artistManagement;
	private Kalender kalender = new Kalender();
	private final BuchungRepository buchungRepository;
	/**
	 * Konstruktor
	 * @param artistManagement
	 * 		Spring Autowired - Instanz des MangerUser
	 * @param buchungRepository
	 * 	Soring Autowired - Instanz des BuchungsRepository
	 */
	@Autowired
	ControllerVerwaltungArtist(ManagerUser artistManagement, BuchungRepository buchungRepository) {

		Assert.notNull(artistManagement, "artistManagement must not be null!");

		this.artistManagement = artistManagement;
		this.buchungRepository = buchungRepository;
	
	}
	/**
	 * Verwaltungsseite für den Artisten
	 * @param userAccount
	 * @param model
	 * @return
	 */
	@GetMapping("/artistverwaltung/main")
	public String getArtistverwaltungMain(@LoggedIn UserAccount userAccount, Model model) {
		User user = artistManagement.findeUserAccount(userAccount);
		model.addAttribute("user", user);
		model.addAttribute("veranstaltungen", artistManagement.findeVeranstaltungenZuUser(user));
		model.addAttribute("buchungen", artistManagement.findeBuchungenZuUser(user));

		return ("VerwaltungArtist/main");

	}
	/**
	 * Übersicht über die Gruppen des Artisten
	 * @param userAccount
	 * @param model
	 * @return
	 */
	@GetMapping("/artistverwaltung/gruppen")
	public String getArtistverwaltungGruppe(@LoggedIn UserAccount userAccount, Model model) {
		User user = artistManagement.findeUserAccount(userAccount);
		model.addAttribute("user", user);
		model.addAttribute("gruppen", artistManagement.findeGruppenZuArtist(user));

		return ("VerwaltungArtist/gruppen");

	}
/**
 * Übersicht über alle Buchungen von Veranstaltungen des Artisten
 * Fügt den Model relvente Zeitangaben, Buchungen,Kunden, sowie den aktuell eingeloggten User hinzu
 * @param userAccount
 * @param model
 
 * @param monatZurueck
 * @param monatVor
 * @return
 */
	@GetMapping("/artistverwaltung/termine")
	public String getArtistverwaltungTermine(@LoggedIn UserAccount userAccount, Model model,
			@RequestParam(value = "monatZurueck", defaultValue = "false") String monatZurueck,
			@RequestParam(value = "monatVor", defaultValue = "false") String monatVor) {
		if (monatZurueck.equals("true")) {
			kalender.setDatum(kalender.getDatum().minusMonths(1));
		}
		if (monatVor.equals("true")) {
			kalender.setDatum(kalender.getDatum().plusMonths(1));
		}
		User user = artistManagement.findeUserAccount(userAccount);
		List<Buchung> buchungenZuArtistImMonat = kalender.getBuchungenZuArtistImMonat(buchungRepository, userAccount);
		String[][] eintraege = kalender.getEintraegeFuerMonat(buchungenZuArtistImMonat);
		
		model.addAttribute("eintraege", eintraege);
		model.addAttribute("user", user);
		model.addAttribute("jahr", kalender.getDatum().getYear());
		model.addAttribute("monat", kalender.getDatum().getMonth().getDisplayName(TextStyle.FULL, Locale.GERMANY));
		System.out.println(artistManagement.findeBuchungenZuUser(user).size());
		model.addAttribute("buchungen", artistManagement.findeBuchungenZuUser(user));
		model.addAttribute("kunden", artistManagement.findeKundenZuArtist(user));
		return ("VerwaltungArtist/termine");

	}
/**
 * Alle Veranstaltungen des Artisten
 * Model: fügt dem Model den aktuellen User, dessen Veranstaltungen und Buchungen hinzu
 * @param userAccount
 * @param model
 * @return
 */
	@GetMapping("/artistverwaltung/veranstaltungen")
	public String getArtistverwaltungVeranstaltungen(@LoggedIn UserAccount userAccount, Model model) {
		User user = artistManagement.findeUserAccount(userAccount);
		model.addAttribute("user", user);
		model.addAttribute("veranstaltungen", artistManagement.findeVeranstaltungenZuUser(user));
		model.addAttribute("buchungen", artistManagement.findeBuchungenZuUser(user));
		return ("VerwaltungArtist/veranstaltungen");

	}
/**
 * Übersicht über die Sperrzeiten des aktuell eingeloggten Users
 * @param userAccount
 * @param model
 * @return
 */
	@GetMapping("/artistverwaltung/sperrzeiten")
	public String getArtistverwaltungSperrzeiten(@LoggedIn UserAccount userAccount, Model model) {
		User user = artistManagement.findeUserAccount(userAccount);
		model.addAttribute("user", user);
		model.addAttribute("veranstaltungen", artistManagement.findeVeranstaltungenZuUser(user));
		model.addAttribute("buchungen", artistManagement.findeBuchungenZuUser(user));
		return ("VerwaltungArtist/main");

	}

}
