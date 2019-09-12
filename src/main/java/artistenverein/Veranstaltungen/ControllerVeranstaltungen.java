package artistenverein.Veranstaltungen;

import artistenverein.Lagerverwaltung.ArtikelKatalog;
import artistenverein.Veranstaltungen.EntityVeranstaltung.VeranstaltungsType;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Optional;

import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Der Controller ControllerVeranstaltungen ist ein Spring-Controller zuständig
 * für alle Anfrage im Bezug auf den VeranstaltungsKatalog des Artistenvereins
 * 
 * @author Luisa Leopold
 */
@Controller
class ControllerVeranstaltungen {

	private final ArtikelKatalog artikelKatalog;
	private final ZusatzkostenRepository zusatzkostenRepository;

	/**
	 * Erstellt einen neuen Controller mit dem gegebenen Katalog und ZusatzkostenRepository
	 *
	 * @param artikelKatalog
	 *            der ArtikelKatalog, normalerweise von Spring automatisch verknüpft
	 * @param zusatzkostenRepository
	 *            ein zusatzkostenRepository, normalerweise von Spring automatisch verknüpft
	 */
	@Autowired
	public ControllerVeranstaltungen(ArtikelKatalog artikelKatalog, ZusatzkostenRepository zusatzkostenRepository) {
		Assert.notNull(artikelKatalog, "artikelKatalog darf nicht null sein!");
		Assert.notNull(zusatzkostenRepository, "zusatzkostenRepository darf nicht null sein!");
		
		this.artikelKatalog = artikelKatalog;
		this.zusatzkostenRepository = zusatzkostenRepository;
	}

	/**
	 * Fügt dem übergebenen Model die Attribute zur Darstellung der Detailansicht einer Veranstaltung
	 * hinzu
	 * <p>
	 * Modelparameter:
	 * <ul>
	 * <li>Iterable<zusatzkosten>zusatzkosten
	 * <li>EntityVeranstaltung veranstaltung
	 * <li>Iterable<Artikel> artikellist
	 * </ul>
	 *
	 * @param veranstaltung
	 * 				Veranstalung, deren DetailAnsicht angezeigt werden soll, als EntityVeranstaltung
	 * @param model
	 * 				ein Objekt vom Typ Spring-Model
	 * @param userAccount
	 * 				Nutzer, der auf die Detailansicht zugreifen möchte als UserAccount
	 * @return	 ein String zur Identifizierung des Templates ("Veranstaltungen/detail")
	 */
	@RequestMapping("/detail/{id}")
	public String detail(@PathVariable("id") EntityVeranstaltung veranstaltung, Model model,
			@LoggedIn Optional<UserAccount> userAccount) {
		if (userAccount.isPresent()) {
			for (Iterator<UserAccount> it = veranstaltung.getArtisten().iterator(); it.hasNext();) {
				UserAccount artist = it.next();
				if (artist.equals(userAccount.get())) {
					model.addAttribute("artist", 1);
				}
			}
			for(Iterator<Bewertung> it = veranstaltung.getBewertungen().iterator(); it.hasNext();) {
				Bewertung bewertung = it.next();
				if(bewertung.getAutor().equals(userAccount.get())) {
					model.addAttribute("darfNochBewerten", 1);
				}
			}
		}
		if (veranstaltung.getEndDatum().isBefore(LocalDateTime.now())) {
			model.addAttribute("buchbar", false);
		} else {
			model.addAttribute("buchbar", true);
		}
		model.addAttribute("zusatzkosten", zusatzkostenRepository.findAll());
		model.addAttribute("veranstaltung", veranstaltung);
		model.addAttribute("artikellist", veranstaltung.getArtikel());
		return "Veranstaltungen/detail";
	}

	/**
	 * Fügt dem übergebenen Model die Attribute zur Darstellung der Buchungsansicht einer Veranstaltung
	 * hinzu
	 *
	 * @param model
	 * 				ein Objekt vom Typ Spring-Model
	 *
	 * @param veranstaltung
	 * 				Veranstalung, deren Buchungsansicht angezeigt werden soll, als EntityVeranstaltung
	 *
	 * @return	 ein String zur Identifizierung des Templates ("Veranstaltungen/detail")
	 */
	@PostMapping("/detail/{id}")
	public String details(Model model, @PathVariable("id") EntityVeranstaltung veranstaltung) {
		model.addAttribute("BuchungsValidation", new BuchungsValidation());
		model.addAttribute("veranstaltung", veranstaltung);
		model.addAttribute("artikellist", veranstaltung.getArtikel());
		return "Veranstaltungen/formVeranstaltungBuchen";
	}
	
	/**
	 * Fügt dem übergebenen Model die Attribute zur Darstellung des Formulars zur
	 * Bearbeitung einer existierenden Veranstaltug hinzu
	 * <p>
	 * Modelparameter:
	 * <ul>
	 * <li>Veranstaltung veranstaltung
	 * <li>FormNeueVeranstaltung neueVeranstaltung
	 * </ul>
	 * 
	 * @param veranstaltung
	 * 					Veranstaltung, die bearbeitet werden soll, als EntityVeranstaltung
	 * @param model
	 * 				ein Objekt vom Typ Spring-Model
	 * @return	ein String zur Identifizierung des Templates ("Veranstaltungen/editFormular")
	 */
	@PreAuthorize("hasRole('ROLE_ARTIST')")
	@GetMapping(value = "/detail/{veranstaltung}/bearbeiten")
	String vbearbeiten(@PathVariable EntityVeranstaltung veranstaltung, Model model) {
		model.addAttribute("veranstaltung", veranstaltung);
		model.addAttribute("neueVeranstaltung", new FormNeueVeranstaltung());
		if(veranstaltung.getType()== VeranstaltungsType.WORKSHOP) {
			model.addAttribute("workshop", 1);
		} else {
			model.addAttribute("workshop",  0);
		}

		return "Veranstaltungen/editFormular";
	}

	/**
	 * Fügt dem übergebenen Model die Attribute zur Darstellung der Artikelliste des Shops, um diese
	 * zur Veranstaltung hinzufügen zu können
	 * <p>
	 * Modelparameter:
	 * <ul>
	 * <li>Iterator<Artikel>katalog
	 * <li>EntityVeranstaltung veranstaltung
	 * </ul>
	 * 
	 * @param model
	 * 				ein Objekt vom Typ Spring-Model
	 * @param veranstaltung
	 * 				die Veranstaltung, welcher Artikel zugeordnet werden sollen als EntityVeranstaltung
	 * @return		ein String zur Identifizierung des Templates ("Veranstaltungen/addArtikel")
	 */
	@PreAuthorize("hasRole('ROLE_ARTIST')")
	@GetMapping(value = "/detail/{veranstaltung}/artikel")
	public String artikel(Model model, @PathVariable EntityVeranstaltung veranstaltung) {
		model.addAttribute("katalog", artikelKatalog.findAllListed());
		model.addAttribute("title", "catalog.item.title");
		model.addAttribute("veranstaltung", veranstaltung);
		
		return "Veranstaltungen/addArtikel";
	}

}
