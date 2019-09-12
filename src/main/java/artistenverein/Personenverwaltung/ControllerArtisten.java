package artistenverein.Personenverwaltung;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import artistenverein.Veranstaltungen.EntityVeranstaltung;
import artistenverein.Veranstaltungen.VeranstaltungsKatalog;
import artistenverein.Veranstaltungen.EntityVeranstaltung.VeranstaltungsType;

/**
 * 
 * Der Controller ControllerArtisten ist ein Spring-Controller. Er ist zuständig
 * für das Anzeigen der Termine des Artisten.
 * 
 */
@Controller
public class ControllerArtisten {

	private final VeranstaltungsKatalog veranstaltungsKatalog;
	private final MessageSourceAccessor messageSourceAccessor;

	/**
	 * Konstruktor
	 *
	 * @param veranstaltungsKatalog
	 *            Spring Autowired - Instanz des VeranstaltungsKatalog
	 *
	 * @param messageSource
	 *            Spring Autowired - Instanz von MessageSource
	 *
	 **/
	@Autowired
	public ControllerArtisten(VeranstaltungsKatalog veranstaltungsKatalog, MessageSource messageSource) {
		this.veranstaltungsKatalog = veranstaltungsKatalog;
		this.messageSourceAccessor = new MessageSourceAccessor(messageSource);

	}

	/**
	 * fügt dem übergebenen Model die Termine des Artisten hinzu
	 * 
	 * @param model
	 *            ein Spring-Model
	 * 
	 * @param userAccount
	 *            der UserAcoount des Artisten
	 * 
	 * @return String zur Bestimmung des Templates
	 */
	@GetMapping("/meineVeranstaltungen")
	public String termine(Model model, @LoggedIn UserAccount userAccount) {
		Iterable<EntityVeranstaltung> veranstaltungen = veranstaltungsKatalog.findByType(VeranstaltungsType.WORKSHOP);
		List<EntityVeranstaltung> veranstaltungenZuArtist = new ArrayList<>();
		for (Iterator<EntityVeranstaltung> it = veranstaltungen.iterator(); it.hasNext();) {
			EntityVeranstaltung veranstaltung = it.next();
			if (veranstaltung.getArtisten() == userAccount) {
				veranstaltungenZuArtist.add(veranstaltung);
			}
		}
		model.addAttribute("katalog", veranstaltungenZuArtist);
		model.addAttribute("title", messageSourceAccessor.getMessage("katalog.workshop.title"));
		model.addAttribute("artist", 1);
		return "Veranstaltungen/veranstaltungskatalog";
	}

}