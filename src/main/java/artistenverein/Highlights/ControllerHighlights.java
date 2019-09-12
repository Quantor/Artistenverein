package artistenverein.Highlights;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import artistenverein.Veranstaltungen.Bewertung;
import artistenverein.Veranstaltungen.EntityVeranstaltung;
import artistenverein.Veranstaltungen.VeranstaltungsKatalog;

/**
 * Die Klasse ControllerHighlights dient zur Anzeige der Startseite mit
 * Highlights aus der Vergangenheit des Vereins
 * 
 */
@Controller
public class ControllerHighlights {

	private final VeranstaltungsKatalog veranstaltungen;
	private static final int HIGHLIGHT_THRESHOLD = 4;

	/**
	 * Erstellt einen neuen ControllerHighlights mit den angegeben Parametern
	 *
	 * @param veranstaltungen
	 *            der Veranstaltungskatalog der Website, Autowired
	 */
	@Autowired
	public ControllerHighlights(VeranstaltungsKatalog veranstaltungen) {
		this.veranstaltungen = veranstaltungen;
	}

	
	/**
	 * Fügt dem übergebenen Model die Highlights zur Darstellung auf der Startseite hinzu
	 *
	 * @param model
	 *            ein Objekt vom Typ Spring-Model
	 * @return ein String zur Identifizierung des Templates
	 *         ("index")
	 */
	@RequestMapping("/")
	public String index(Model model) {
		List<Highlight> tempList = new ArrayList<Highlight>();
		for (EntityVeranstaltung v : veranstaltungen.findAll()) {
			int max = 0;
			Bewertung best = null;
			for (Bewertung b : v.getBewertungen()) {
				if (b.getBewertung() > max && v.getEndDatum().isBefore(LocalDateTime.now())) {
					best = b;
					max = b.getBewertung();
				}
			}

			if (best != null && max >= HIGHLIGHT_THRESHOLD && v.getEndDatum().isBefore(LocalDateTime.now())) {
				tempList.add(new Highlight(v, best));
			}
		}

		model.addAttribute("highlights", tempList);
		return "index";
	}
}
