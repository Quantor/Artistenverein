package artistenverein.Personenverwaltung;

import artistenverein.Lagerverwaltung.Artikel;
import artistenverein.Veranstaltungen.Bewertung;
import artistenverein.Veranstaltungen.BewertungsValidation;
import artistenverein.Veranstaltungen.Buchung;
import artistenverein.Veranstaltungen.BuchungRepository;
import artistenverein.Veranstaltungen.BuchungsValidation;
import artistenverein.Veranstaltungen.EntityVeranstaltung;
import artistenverein.Veranstaltungen.Fehler;
import artistenverein.Veranstaltungen.FormNeueVeranstaltung;
import artistenverein.Veranstaltungen.Kalender;
import artistenverein.Veranstaltungen.Kommentar;
import artistenverein.Veranstaltungen.VeranstaltungsKatalog;
import artistenverein.Veranstaltungen.VeranstaltungsManager;
import artistenverein.Veranstaltungen.Zusatzkosten;
import artistenverein.Veranstaltungen.ZusatzkostenRepository;
import artistenverein.Veranstaltungen.EntityVeranstaltung.VeranstaltungsType;
import artistenverein.Zeitverwaltung.Zeitverwaltung;

import static org.salespointframework.core.Currencies.EURO;

import javax.validation.Valid;

import org.javamoney.moneta.Money;
import org.salespointframework.time.BusinessTime;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Der Controller für den Veranstaltungskatalog.
 *
 * @author Luisa Leopold
 */
@Controller
class ControllerVeranstaltungskatalog {

	private final VeranstaltungsKatalog veranstaltungsKatalog;
	private final MessageSourceAccessor messageSourceAccessor;
	private final BusinessTime businessTime;
	private final VeranstaltungsManager veranstaltungsManager;
	private VeranstaltungsType vt;
	private BuchungRepository buchungRepository;
	private final RepositoryGruppen gruppenrepository;
	private final ZusatzkostenRepository zusatzkostenRepository;
	private final Zeitverwaltung zeitverwaltung;
	private Kalender kalender = new Kalender();

	/**
	 * Autowired Konstruktor
	 *
	 * @param veranstaltungsKatalog
	 *            Autowired
	 *
	 * @param messageSource
	 *            Autowired
	 *
	 * @param businessTime
	 *            Autowired
	 *
	 * @param veranstaltungsManager
	 *            Autowired
	 *
	 * @param buchungRepository
	 *            Autowired
	 *
	 * @param gruppenrepository
	 *            Autowired
	 *
	 * @param userRepository
	 *            Autowired
	 *
	 * @param zeitverwaltung
	 *            Autowired
	 *
	 * @param zusatzkostenRepository
	 *            Autowired
	 */
	@Autowired
	public ControllerVeranstaltungskatalog(VeranstaltungsKatalog veranstaltungsKatalog, MessageSource messageSource,
			BusinessTime businessTime, VeranstaltungsManager veranstaltungsManager, BuchungRepository buchungRepository,
			RepositoryGruppen gruppenrepository, Zeitverwaltung zeitverwaltung,
			ZusatzkostenRepository zusatzkostenRepository) {
		Assert.notNull(veranstaltungsKatalog, "veranstaltungsKatalog darf nicht null sein!");
		Assert.notNull(buchungRepository, "buchungRepository darf nicht null sein!");
		Assert.notNull(gruppenrepository, "gruppenrepository darf nicht null sein!");
		Assert.notNull(zusatzkostenRepository, "zusatzkostenRepository darf nicht null sein!");
		Assert.notNull(zeitverwaltung, "zeitverwaltung darf nicht null sein!");

		this.veranstaltungsKatalog = veranstaltungsKatalog;
		this.messageSourceAccessor = new MessageSourceAccessor(messageSource);
		this.businessTime = businessTime;
		this.veranstaltungsManager = veranstaltungsManager;
		this.buchungRepository = buchungRepository;
		this.gruppenrepository = gruppenrepository;
		this.zusatzkostenRepository = zusatzkostenRepository;
		this.zeitverwaltung = zeitverwaltung;
		this.vt = VeranstaltungsType.SHOW;
	}

	/**
	 * Die Funktion, die aufgerufen wird, wenn /workshops besucht wird.
	 *
	 * @param model
	 *            Das Model, zu dem die Attribute hinzugefügt werden, die auf der
	 *            Seite angezeigt werden.
	 *
	 * @return Der String, der die anzuzeigende html-Seite identifiziert.
	 */
	@RequestMapping("/workshops")
	public String workshops(Model model) {
		List<EntityVeranstaltung> empfehlungen = veranstaltungsManager.getBesteBewertungen();
		Iterator<EntityVeranstaltung> iter = empfehlungen.iterator();
		while (iter.hasNext()) {
			EntityVeranstaltung v = iter.next();
			if (v.getEndDatum().isBefore(LocalDateTime.now())) {
				iter.remove();
			}
		}
		model.addAttribute("empfehlungen", empfehlungen);

		Iterable<EntityVeranstaltung> workshops = veranstaltungsKatalog.findByType(VeranstaltungsType.WORKSHOP);
		iter = workshops.iterator();
		while (iter.hasNext()) {
			EntityVeranstaltung v = iter.next();
			if (v.getEndDatum().isBefore(LocalDateTime.now())) {
				iter.remove();
			}
		}
		model.addAttribute("katalog", workshops);

		model.addAttribute("title", messageSourceAccessor.getMessage("katalog.workshop.title"));
		this.vt = VeranstaltungsType.WORKSHOP;

		return "Veranstaltungen/veranstaltungskatalog";
	}

	/**
	 * Die Funktion, die aufgerufen wird, wenn /shows besucht wird.
	 *
	 * @param model
	 *            Das Model, zu dem die Attribute hinzugefügt werden, die auf der
	 *            Seite angezeigt werden.
	 *
	 * @return Der String, der die anzuzeigende html-Seite identifiziert.
	 */
	@RequestMapping("/shows")
	public String shows(Model model) {
		List<EntityVeranstaltung> empfehlungen = veranstaltungsManager.getBesteBewertungen();
		Iterator<EntityVeranstaltung> iter = empfehlungen.iterator();
		while (iter.hasNext()) {
			EntityVeranstaltung v = iter.next();
			if (v.getEndDatum().isBefore(LocalDateTime.now())) {
				iter.remove();
			}
		}
		model.addAttribute("empfehlungen", empfehlungen);

		Iterable<EntityVeranstaltung> shows = veranstaltungsKatalog.findByType(VeranstaltungsType.SHOW);
		iter = shows.iterator();
		while (iter.hasNext()) {
			EntityVeranstaltung v = iter.next();
			if (v.getEndDatum().isBefore(LocalDateTime.now())) {
				iter.remove();
			}
		}
		model.addAttribute("katalog", shows);

		model.addAttribute("title", messageSourceAccessor.getMessage("katalog.show.title"));
		this.vt = VeranstaltungsType.SHOW;

		return "Veranstaltungen/veranstaltungskatalog";
	}

	/**
	 * Die Funktion, die aufgerufen wird, wenn der Benutzer als Artist angemeldet
	 * ist und einen neuen Workshop erstellen will.
	 *
	 * @param model
	 *            Das Model, zu dem die Attribute hinzugefügt werden, die auf der
	 *            Seite angezeigt werden.
	 *
	 * @return Der String, der die anzuzeigende html-Seite identifiziert.
	 */
	@PreAuthorize("hasRole('ROLE_ARTIST')")
	@RequestMapping("/neuWorkshop")
	public String registerWorkshop(Model model) {
		Set<Artistengruppe> gruppenZuArtist = new HashSet<>();
		this.vt = VeranstaltungsType.WORKSHOP;
		model.addAttribute("neueVeranstaltung", new FormNeueVeranstaltung());
		model.addAttribute("artistengruppen", gruppenZuArtist);
		model.addAttribute("workshop", 1);
		return "Veranstaltungen/addFormular";
	}

	/**
	 * Die Funktion, die aufgerufen wird, wenn der Benutzer als Artist angemeldet
	 * ist und eine neue Show erstellen will.
	 *
	 * @param userAccount
	 *            Der zur Zeit eingeloggte User.
	 *
	 * @param model
	 *            Das Model, zu dem die Attribute hinzugefügt werden, die auf der
	 *            Seite angezeigt werden.
	 *
	 * @return Der String, der die anzuzeigende html-Seite identifiziert.
	 */
	@PreAuthorize("hasRole('ROLE_ARTIST')")
	@RequestMapping("/neuShow")
	public String registerShow(Model model, @LoggedIn UserAccount userAccount) {

		Set<Artistengruppe> gruppenZuArtist = new HashSet<>();
		this.vt = VeranstaltungsType.SHOW;
		Iterable<Artistengruppe> alleGruppen = this.gruppenrepository.findAll();
		for (Iterator<Artistengruppe> it = alleGruppen.iterator(); it.hasNext();) {
			Artistengruppe gruppe = it.next();
			for (Iterator<User> iterator = gruppe.getMitglieder().iterator(); iterator.hasNext();) {
				User artist = iterator.next();
				if (userAccount.equals(artist.getUserAccount())) {
					gruppenZuArtist.add(gruppe);
				}
			}
		}
		model.addAttribute("neueVeranstaltung", new FormNeueVeranstaltung());
		model.addAttribute("artistengruppen", gruppenZuArtist);
		model.addAttribute("workshop", 0);
		return "Veranstaltungen/addFormular";
	}

	/**
	 * Die Funktion, die aufgerufen wird, wenn der Benutzer als Artist angemeldet
	 * ist und gerade eine neuen Veranstaltung erstellt hat.
	 *
	 * @param model
	 *            Das Model, zu dem die Attribute hinzugefügt werden, die auf der
	 *            Seite angezeigt werden.
	 *
	 * @param neueVeranstaltung
	 *            Wird im Model mit übergeben.
	 *
	 * @param result
	 *            Gibt an, ob Fehler im Formular vorlagen.
	 *
	 * @param userAccount
	 *            Der eingeloggte User.
	 *
	 * @return Der String, der die anzuzeigende html-Seite identifiziert oder ein
	 *         redirect auf einen anderen Controller.
	 */
	@PreAuthorize("hasRole('ROLE_ARTIST')")
	@RequestMapping("/add")
	public String erstelleNeueVeranstaltung(
			@ModelAttribute("neueVeranstaltung") @Valid FormNeueVeranstaltung neueVeranstaltung, BindingResult result,
			@LoggedIn UserAccount userAccount, Model model) {
		if (result.hasErrors()) {
			return "Veranstaltungen/addFormular";
		}
		Fehler fehler = new Fehler();

		Duration dauer = Duration.ofMinutes(Integer.parseInt(neueVeranstaltung.getDauer()));

		// fehler.pruefeArtistenVerbucht(veranstaltung, buchungRepository, datum,
		// dauer);
		fehler.pruefeDatum(
				veranstaltungsManager.getDatum(neueVeranstaltung.getStartDatum(), neueVeranstaltung.getStartZeit()),
				veranstaltungsManager.getDatum(neueVeranstaltung.getEndDatum(), neueVeranstaltung.getEndZeit()));
		fehler.pruefeDauer(dauer);

		List<Integer> fehlerliste = fehler.getFehlerliste();

		if (!fehlerliste.isEmpty()) {
			model.addAttribute("fehlerliste", fehlerliste);
			return "Veranstaltungen/addFormular";
		}

		Set<UserAccount> artisten = new HashSet<>();
		if (!neueVeranstaltung.getGruppe().isEmpty()) {
			for (User artist : gruppenrepository.findGruppeByName(neueVeranstaltung.getGruppe()).getMitglieder()) {
				artisten.add(artist.getUserAccount());
			}
		} else {
			artisten.add(userAccount);
		}
		veranstaltungsManager.erstelleNeueVeranstaltung(neueVeranstaltung, vt, artisten, neueVeranstaltung.getGruppe());

		String returnvalue;
		if (vt == VeranstaltungsType.WORKSHOP) {
			returnvalue = "redirect:/workshops";
		} else {
			returnvalue = "redirect:/shows";
		}
		return returnvalue;
	}

	/**
	 * Die Funktion, die aufgerufen wird, wenn der Benutzer als Artist angemeldet
	 * ist und gerade eine Veranstaltung bearbeitet hat. Die Veranstaltung wird hier
	 * bearbeitet und danach wird auf die Detailansicht weitergeleitet.
	 *
	 * @param veranstaltung
	 *            Die Veranstaltung, die bearbeitet wird. Wird im Pfad mit
	 *            übergeben.
	 *
	 * @param neueVeranstaltung
	 *            Wird im Model mit übergeben.
	 *
	 * @param result
	 *            Gibt an, ob Fehler im Formular vorlagen.
	 *
	 * @return Ein String, der ein redirect auf einen anderen Controller beschreibt.
	 */
	@PreAuthorize("hasRole('ROLE_ARTIST')")
	@PostMapping(value = "/detail/{veranstaltung}/bearbeiten")
	String bearbeiten(@PathVariable EntityVeranstaltung veranstaltung,
			@ModelAttribute("neueVeranstaltung") @Valid FormNeueVeranstaltung neueVeranstaltung, BindingResult result) {
		if (result.hasErrors()) {
			return "Veranstaltungen/addFormular";
		}
		LocalDateTime start = veranstaltungsManager.getDatum(neueVeranstaltung.getStartDatum(),
				neueVeranstaltung.getStartZeit());
		LocalDateTime end = veranstaltungsManager.getDatum(neueVeranstaltung.getEndDatum(),
				neueVeranstaltung.getEndZeit());
		int preis = Integer.parseInt(neueVeranstaltung.getPreis());
		int dauer = Integer.parseInt(neueVeranstaltung.getDauer());
		veranstaltung.setName(neueVeranstaltung.getName());
		veranstaltung.setPrice(Money.of(preis, EURO));
		veranstaltung.setBeschreibung(neueVeranstaltung.getBeschreibung());
		veranstaltung.setDauer(dauer);
		veranstaltung.setStartDatum(start);
		veranstaltung.setEndDatum(end);

		veranstaltungsKatalog.save(veranstaltung);
		return "redirect:/detail/{veranstaltung}";
	}

	/**
	 * Die Funktion, die aufgerufen wird, wenn der Benutzer als Artist angemeldet
	 * ist und gerade eine Veranstaltung gelöscht hat. Danach wird entweder zu den
	 * Shows oder zu den Workshops weitergeleitet.
	 *
	 * @param veranstaltung
	 *            Die zu löschende Veranstaltung.
	 *
	 * @return Ein String, der ein redirect auf einen anderen Controller beschreibt.
	 */
	@PreAuthorize("hasRole('ROLE_ARTIST')")
	@GetMapping("/detail/{veranstaltung}/loeschen")
	String loeschen(@PathVariable EntityVeranstaltung veranstaltung) {
		veranstaltungsKatalog.delete(veranstaltung);
		if (vt == VeranstaltungsType.WORKSHOP) {
			return "redirect:/workshops";
		}
		return "redirect:/shows";
	}

	/**
	 * Die Funktion, die aufgerufen wird, wenn der Benutzer als Artist angemeldet
	 * ist und einen Artikel zum Katalog hinzufügen möchte.
	 *
	 * @param veranstaltung
	 *            Die Veranstaltung, zu der der Artikel gehört. Wird im Pfad mit
	 *            übergeben.
	 *
	 * @param artikel
	 *            Der Artikel, der hinzugefügt wird.
	 *
	 * @return Ein String, der ein redirect auf die Veranstaltungsdetailseite
	 *         darstellt.
	 */
	@PreAuthorize("hasRole('ROLE_ARTIST')")
	@GetMapping(value = "/addArtikel/{veranstaltung}")
	public String addArtikel(@PathVariable EntityVeranstaltung veranstaltung,
			@RequestParam("artikel") Artikel[] artikel) {
		veranstaltungsManager.addArtikel(veranstaltung, artikel);
		return "redirect:/detail/" + veranstaltung.getId();
	}

	/**
	 * Die Funktion, die aufgerufen wird wenn ein Benutzer zu einer Veranstaltung
	 * einen Kommentar hinterlassen möchte.
	 *
	 * @param veranstaltung
	 *            Die Veranstaltung, bei der der Kommentar abgegeben werden soll.
	 *            Wird im Pfad mit übergeben.
	 *
	 * @param kommentar
	 *            Der Kommentar, der hinterlassen werden soll.
	 *
	 * @return Ein String, der einen Redirect auf die Detailseite der Veranstaltung
	 *         beschreibt.
	 */
	@RequestMapping(value = "/kommentar", method = RequestMethod.POST)
	public String comment(@RequestParam("vid") EntityVeranstaltung veranstaltung,
			@RequestParam("kommentar") String kommentar) {

		veranstaltung.addKommentar(new Kommentar(kommentar, businessTime.getTime()));
		veranstaltungsKatalog.save(veranstaltung);
		return "redirect:/detail/" + veranstaltung.getId();
	}

	/**
	 * Die Funktion, die aufgerufen wird wenn ein eingeloggter Benutzer eine
	 * Bewertung für eine Veranstaltung abgeben möchte.
	 *
	 * @param veranstaltung
	 *            Die Veranstaltung, für die die Bewertung gelten soll. Wird im Pfad
	 *            mit übergeben.
	 *
	 * @param model
	 *            Das model um die Attribute, die angezeigt werden sollen
	 *            hinzuzufügen.
	 *
	 * @return Ein String, der die anzuzeigende html-Seite beschreibt.
	 */
	@PreAuthorize("hasRole('ROLE_CUSTOMER')")
	@RequestMapping(value = "/bewerten", method = RequestMethod.POST)
	public String bewerten(@RequestParam("vid") EntityVeranstaltung veranstaltung, Model model) {

		model.addAttribute("veranstaltung", veranstaltung);
		model.addAttribute("bewertungsValidation", new BewertungsValidation());
		return "Veranstaltungen/bewertungFormular";
	}

	/**
	 * Die Funktion, die aufgerufen wird, wenn ein eingeloggter Benutzer eine
	 * Bewertung abgegeben hat.
	 *
	 * @param veranstaltung
	 *            Die Veranstaltung, für die die Bewertung gilt.
	 *
	 * @param bewertung
	 *            Die Bewertung selbst.
	 *
	 * @param kommentar
	 *            Der Kommentar zu der Bewertung.
	 *
	 * @param autor
	 *            Der Autor der Bewertung.
	 *
	 * @param bewertungsValidation
	 *            Die Validiarung für die Bewertung, die Einschränkungen durchsetzt.
	 *
	 * @param result
	 *            Enthält ggf. Fehler im Formular.
	 *
	 * @param model
	 *            Das Model, um die anzuzeigenden Attribute hinzuzufügen.
	 *
	 * @return Der String, der die anzuzeigende html-Seite identifiziert oder ein
	 *         redirect auf einen anderen Controller.
	 */
	@PreAuthorize("hasRole('ROLE_CUSTOMER')")
	@RequestMapping(value = "/bewertet", method = RequestMethod.POST)
	public String bewertet(@RequestParam("vid") EntityVeranstaltung veranstaltung,
			@RequestParam("bewertung") int bewertung, @RequestParam("kommentar") String kommentar,
			@LoggedIn UserAccount autor,
			@ModelAttribute("bewertungsValidation") @Valid BewertungsValidation bewertungsValidation,
			BindingResult result, Model model) {

		if (result.hasErrors()) {
			return "Veranstaltungen/bewertungFormular";
		}

		Fehler fehler = new Fehler();
		fehler.pruefeBewertung(Double.parseDouble(bewertungsValidation.getBewertung()));
		if (fehler.getFehlerliste().isEmpty()) {
			veranstaltung.addBewertung(new Bewertung(kommentar, bewertung, businessTime.getTime(), autor));
			veranstaltungsKatalog.save(veranstaltung);
			return "redirect:/detail/" + veranstaltung.getId();
		} else {
			model.addAttribute("veranstaltung", veranstaltung);
			model.addAttribute("fehlerliste", fehler.getFehlerliste());
			return "Veranstaltungen/bewertungFormular";
		}
	}

	/**
	 * Die Funktion, die aufgerufen wird, wenn der eingeloggte Vorstand die
	 * Hallentermine einsehen möchte.
	 *
	 * @param model
	 *            Das Model, zu dem Attribute hinzugefügt werden, die angezeigt
	 *            werden sollen.
	 *
	 * @param monatZurueck
	 *            Gibt an, ob einen Monat zurückgeblättert werden soll, oder nicht.
	 *
	 * @param monatVor
	 *            Gibt an, ob einen Monat weitergeblättert werden soll, oder nicht.
	 *
	 * @return Der String, der die anzuzeigende html-Seite identifiziert.
	 */
	@PreAuthorize("hasRole('ROLE_BOSS')")
	@GetMapping("/hallenverwaltung")
	public String zeigeHallenTermine(Model model,
			@RequestParam(value = "monatZurueck", defaultValue = "false") String monatZurueck,
			@RequestParam(value = "monatVor", defaultValue = "false") String monatVor) {
		if (monatZurueck.equals("true")) {
			kalender.setDatum(kalender.getDatum().minusMonths(1));
		}
		if (monatVor.equals("true")) {
			kalender.setDatum(kalender.getDatum().plusMonths(1));
		}
		// ArrayList<Zeit> zeiten = new ArrayList<Zeit>();

		String[][] eintraege = kalender.getEintraegeFuerMonat(buchungRepository.getHallenBuchungen());
		model.addAttribute("zeitenListe", buchungRepository.getHallenZeiten());
		model.addAttribute("eintraege", eintraege);
		model.addAttribute("jahr", kalender.getDatum().getYear());
		model.addAttribute("monat", kalender.getDatum().getMonth().getDisplayName(TextStyle.FULL, Locale.GERMANY));
		return "Veranstaltungen/hallenverwaltung";
	}

	/**
	 * Die Funktion, die aufgerufen wird, wenn alle Buchungen angezeigt werden
	 * sollen.
	 *
	 * @param model
	 *            Das Model, zu dem die Attribute hinzugefügt werden, die angezeigt
	 *            werden sollen.
	 *
	 * @return Der String, der die anzuzeigende html-Seite identifiziert oder ein
	 *         redirect auf einen anderen Controller.
	 */
	@RequestMapping("/alleBuchungen")
	public String zeigeAlleBuchungen(Model model) {

		model.addAttribute("zusatzkosten", zusatzkostenRepository.findAll());
		model.addAttribute("buchungRepository", buchungRepository.findAll());
		return "Veranstaltungen/alleBuchungen";
	}

	/**
	 * Die Funktion, die aufgerufen wird, wenn die Zusatzkosten bearbeitet werden
	 * sollen
	 *
	 * @param model
	 *            Das Model, zu dem die Attribute hinzugefügt werden, die angezeigt
	 *            werden sollen.
	 * 
	 * @param zusatzkosten
	 *            die zu speichernden Zusatzkosten
	 * 
	 * @param bestaetigen
	 *            dient zur Bestätigung der Änderung der Zusatzkosten
	 *
	 * @return Der String, der die anzuzeigende html-Seite identifiziert oder ein
	 *         redirect auf einen anderen Controller.
	 */
	@PostMapping("/alleBuchungen")
	public String zusatzkosten(Model model, @RequestParam("zusatzkosten") String zusatzkosten,
			@RequestParam(value = "bestaetigen", defaultValue = "false") String bestaetigen) {
		/*
		 * if(result.hasErrors()) { return "Veranstaltung/alleBuchungen"; }
		 */
		if (bestaetigen.equals("true")) {
			Fehler fehler = new Fehler();
			fehler.pruefeZusatzkosten(zusatzkosten);
			if (fehler.getFehlerliste().isEmpty()) {
				zusatzkostenRepository.deleteAll();
				double zusatzkostenDouble = Double.parseDouble(zusatzkosten);
				zusatzkostenRepository.save(new Zusatzkosten(zusatzkostenDouble));
			} else {
				model.addAttribute("fehlerliste", fehler.getFehlerliste());
			}
		}
		model.addAttribute("zusatzkosten", zusatzkostenRepository.findAll());
		model.addAttribute("buchungRepository", buchungRepository.findAll());
		return "Veranstaltungen/alleBuchungen";
	}

	/**
	 * Die Funktion, die aufgerufen wird, wenn ein eingeloggter Kunde eine
	 * Veranstaltung buchen möchte.
	 *
	 * @param buchungsValidation
	 *            Die Validierung für die Eingaben auf der Seite.
	 *
	 * @param result
	 *            Eventuelle Fehler in der Eingabe.
	 *
	 * @param veranstaltung
	 *            Die Veranstaltung, die gebucht werden soll.
	 *
	 * @param model
	 *            Das Model, zu dem Attribute hinzugefügt werden können, die
	 *            angezeigt werden sollen.
	 *
	 * @param ort
	 *            Gibt an, wo die Veranstaltung stattfinden soll.
	 *
	 * @param userAccount
	 *            Der Kunde, der die Veranstaltung buchen möchte.
	 *
	 * @return Der String, der die anzuzeigende html-Seite identifiziert oder ein
	 *         redirect auf einen anderen Controller.
	 */
	@PreAuthorize("hasRole('ROLE_CUSTOMER')")
	@PostMapping("/buchen/{id}")
	public String bucheVeranstaltung(@ModelAttribute("BuchungsValidation") @Valid BuchungsValidation buchungsValidation,
			BindingResult result, @PathVariable("id") EntityVeranstaltung veranstaltung, Model model,
			@RequestParam("ortwahl") String ort, @LoggedIn UserAccount userAccount) {
		if (result.hasErrors()) {
			if (buchungsValidation.validierungOrt() == false) {
				model.addAttribute("veranstaltung", veranstaltung);
			}
			model.addAttribute("zusatzkosten", zusatzkostenRepository.findAll());

			return "Veranstaltungen/formVeranstaltungBuchen";
		}

		Fehler fehler = new Fehler();
		for (int i = 0; i < veranstaltung.getTage(); i++) {
			LocalDateTime datum = veranstaltungsManager.getDatum(buchungsValidation.getDatum(),
					buchungsValidation.getZeit());
			datum = datum.plusDays(i);
			Duration dauer = Duration.ofMinutes(veranstaltung.getDauer());

			if (ort.equals("Halle")) {
				fehler.pruefeHalle(datum, dauer, buchungRepository);
			}

			fehler.pruefeVergangenheit(datum);
			fehler.pruefeZeitraum(veranstaltung, datum);
			fehler.pruefeSperrzeit(veranstaltung, datum, dauer, zeitverwaltung);
			fehler.pruefeArtistenVerbucht(veranstaltung, buchungRepository, datum, dauer);
		}

		List<Integer> fehlerliste = fehler.getFehlerliste();
		System.out.println(fehlerliste);

		if (!fehlerliste.isEmpty()) {
			model.addAttribute("fehlerliste", fehlerliste);
			model.addAttribute("veranstaltung", veranstaltung);
			model.addAttribute("zusatzkosten", zusatzkostenRepository.findAll());
			return "Veranstaltungen/formVeranstaltungBuchen";
		} else {
			// vereinshalle.bucheHalle(datum, dauer);
			Set<LocalDateTime> buchungsDaten = new HashSet<>();
			Buchung buchung = new Buchung();
			// LocalDateTime datum =
			// veranstaltungsManager.getDatum(buchungsValidation.getDatum(),
			// buchungsValidation.getZeit());
			UUID buchungId = UUID.randomUUID();
			for (int i = 0; i < veranstaltung.getTage(); i++) {
				LocalDateTime datum = veranstaltungsManager.getDatum(buchungsValidation.getDatum(),
						buchungsValidation.getZeit());
				datum = datum.plusDays(i);
				buchungsDaten.add(datum);
				if (ort.equals("außerhalb")) {
					Iterator<Zusatzkosten> it = this.zusatzkostenRepository.findAll().iterator();
					Zusatzkosten zusatzkosten = it.next();
					buchung = new Buchung(ort, datum, veranstaltung, userAccount,
							veranstaltung.getPrice().add(Money.of(zusatzkosten.getZusatzKostenWert(), EURO)),
							buchungId);
				} else {
					buchung = new Buchung(ort, datum, veranstaltung, userAccount, veranstaltung.getPrice(), buchungId);
				}

				// buchungRepository.save(buchung);
				// System.out.println(buchung);
				buchungRepository.save(buchung);

			}
			model.addAttribute("buchungsDaten", buchungsDaten);
			model.addAttribute("buchung", buchung);
			return "Veranstaltungen/buchungsBestaetigung";
		}
	}

	/**
	 * Die Funktion, die aufgerufen wird, wenn eine Buchung von einem Kunden
	 * bestätigt wurde.
	 *
	 * @param buchung
	 *            Die Buchung, die bestätigt werden soll.
	 *
	 * @param model
	 *            Das Model, in das Attribute geschrieben werden, die angezeigt
	 *            werden sollen.
	 *
	 * @return Der String, der die anzuzeigende html-Seite identifiziert oder ein
	 *         redirect auf einen anderen Controller.
	 */
	@PostMapping(value = "/buchungsbestaetigung")
	public String buchungBestaetigen(@RequestParam Buchung buchung, Model model) {
		model.addAttribute("veranstaltung", buchung.getVeranstaltung());
		return "redirect:/detail/" + buchung.getVeranstaltung().getId();
	}

	/**
	 * Eine Redirect Funktion.
	 *
	 * @return Der String, ein redirect auf einen anderen Controller darstellt.
	 */
	@GetMapping("/buchungsbestaetigung")
	public String buchungBestaetigen() {
		return "redirect: /Veranstaltungen/buchungsBestaetigung";
	}

	/**
	 * Die Funktion, die aufgerufen wird, wenn eine Buchung abgelehnt werden soll.
	 *
	 * @param buchung
	 *            Die Buchung, die abgelehnt werden soll.
	 *
	 * @param model
	 *            Das Model, in das die Attribute eingetragen werden, die angezeigt
	 *            werden sollen.
	 *
	 * @return Der String, der die anzuzeigende html-Seite identifiziert oder ein
	 *         redirect auf einen anderen Controller.
	 */
	@PostMapping(value = "/buchungablehnen")
	public String buchungAblehnen(@RequestParam Buchung buchung, Model model) {
		buchungRepository.delete(buchung);
		model.addAttribute("veranstaltung", buchung.getVeranstaltung());
		return "redirect:/detail/" + buchung.getVeranstaltung().getId();
	}

	/**
	 * Die Funktion, die aufgerufen wird, wenn ein Kunde eine Buchung ablehnt.
	 *
	 * @param buchung
	 *            Die Buchung, die abgelehnt werden soll.
	 *
	 * @param nutzerAccount
	 *            Der Nutzer, der die Buchung abgelehnt hat.
	 *
	 * @return Der String, der die anzuzeigende html-Seite identifiziert oder ein
	 *         redirect auf einen anderen Controller.
	 */
	@PostMapping("/kalender/absagen")
	public String buchungAbsagenKunde(@RequestParam Buchung buchung, @LoggedIn Optional<UserAccount> nutzerAccount) {
		if (nutzerAccount.isPresent()) {
			UserAccount account = nutzerAccount.get();
			if (account.hasRole(Role.of("ROLE_CUSTOMER")) && buchung.getKunde().equals(account)) {
				buchungRepository.deleteAllUUID(buchung);
			}
		}
		return "redirect:/kalender";
	}

	/**
	 * Eine Redirect Funktion.
	 *
	 * @return Der String, ein redirect auf einen anderen Controller darstellt.
	 */
	@GetMapping("/kalender/absagen")
	public String buchungAbsagenKunde() {
		return "redirect:/kalender";
	}

	/**
	 * Die Funktion, die aufgerufen wird, wenn ein Artist eine Buchung absagt.
	 *
	 * @param buchung
	 *            Die Buchung, die abgesagt werden soll.
	 *
	 * @param nutzerAccount
	 *            Der Nutzer, der die Buchung absagen möchte.
	 *
	 * @return Der String, der die anzuzeigende html-Seite identifiziert oder ein
	 *         redirect auf einen anderen Controller.
	 */
	@PostMapping("/artistenverwaltung/termine/absagen")
	public String buchungAbsagenArtist(@RequestParam Buchung buchung, @LoggedIn Optional<UserAccount> nutzerAccount) {
		if (!nutzerAccount.isPresent()) {
			return "redirect:/artistenverwaltung/termine";
		}

		UserAccount account = nutzerAccount.get();
		if (nutzerAccount.isPresent() && account.hasRole(Role.of("ROLE_ARTIST"))) {
			for (UserAccount artist : buchung.getVeranstaltung().getArtisten()) {
				if (artist.equals(account)) {
					buchungRepository.deleteAllUUID(buchung);
					return "redirect:/kalender";
				}
			}
		}
		return "redirect:/artistenverwaltung/termine";
	}

	/**
	 * Eine Redirect Funktion.
	 *
	 * @return Der String, ein redirect auf einen anderen Controller darstellt.
	 */
	@GetMapping("/artistenverwaltung/termine/absagen")
	public String buchungAbsagenArtist() {
		return "redirect:/artistenverwaltung/termine";
	}

	/**
	 * Die Funktion, die aufgerufen wird, wenn ein Kunde eine Veranstaltung buchen
	 * möchte.
	 *
	 * @param veranstaltung
	 *            Die Veranstaltung, die gebucht werden können soll.
	 *
	 * @param model
	 *            Das Model, in das die Attribute eingetragen werden, die angezeigt
	 *            werden sollen.
	 *
	 * @return Der String, der die anzuzeigende html-Seite identifiziert oder ein
	 *         redirect auf einen anderen Controller.
	 */
	@PreAuthorize("hasRole('ROLE_CUSTOMER')")
	@GetMapping("/buchen/{id}")
	public String bucheVeranstaltung(@PathVariable("id") EntityVeranstaltung veranstaltung, Model model) {
		model.addAttribute("BuchungsValidation", new BuchungsValidation());
		model.addAttribute("veranstaltung", veranstaltung);
		model.addAttribute("artikellist", veranstaltung.getArtikel());
		model.addAttribute("zusatzkosten", zusatzkostenRepository.findAll());
		if (veranstaltung.getType() == VeranstaltungsType.WORKSHOP) {
			model.addAttribute("workshop", 1);
		} else {
			model.addAttribute("workshop", 0);
		}
		return "Veranstaltungen/formVeranstaltungBuchen";
	}

}
