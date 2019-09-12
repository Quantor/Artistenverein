package artistenverein.Lagerverwaltung;

import java.util.Optional;

import javax.validation.Valid;

import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.quantity.Quantity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Der Controller ControllerArtikelKatalog ist ein Spring-Controller zuständig
 * für alle Anfrage im Bezug auf den Shop bzw. den Katalog des Artistenvereins
 * 
 * @author Emanuel Kern
 */
@Controller
public class ControllerArtikelKatalog {

	private final ArtikelKatalog katalog;
	private final ArtikelManager manager;
	private final Inventory<InventoryItem> inventar;
	private static final Quantity NONE = Quantity.of(0);

	/**
	 * Erstellt einen neuen Controller mit dem gegebenen Katalog und ArtikelManager
	 *
	 * @param katalog
	 *            der ArtikelKatalog, normalerweise von Spring automatisch verknüpft
	 * @param manager
	 *            ein ArtikelManager, normalerweise von Spring automatisch verknüpft
	 */
	public ControllerArtikelKatalog(ArtikelKatalog katalog, ArtikelManager manager, Inventory<InventoryItem> inventar) {
		Assert.notNull(katalog, "katalog darf nicht null sein!");
		Assert.notNull(manager, "manager darf nicht null sein!");
		Assert.notNull(inventar, "inventar darf nicht null sein!");
		
		this.inventar = inventar;
		this.katalog = katalog;
		this.manager = manager;
	}

	/**
	 * Fügt dem übergebenen Model die Attribute zur Darstellung des Katalogs hinzu
	 * <p>
	 * Modelparameter:
	 * <ul>
	 * <li>Iterable<Artikel>katalog
	 * </ul>
	 *
	 * @param model
	 *            ein Objekt vom Typ Spring-Model
	 * @return ein String zur Identifizierung des Templates ("Shop/artikelKatalog")
	 */
	@GetMapping("/artikel")
	public String artikelKatalog(Model model) {
		model.addAttribute("katalog", katalog.findAllListed());
		return "Shop/artikelKatalog";
	}

	/**
	 * Fügt dem übergebenen Model die Attribute zur Darstellung des Katalogs hinzu,
	 * es werden nur Artikel übergeben, die in Name oder Beschreibung den String
	 * suche enthalten.
	 * <p>
	 * Modelparameter:
	 * <ul>
	 * <li>Iterable<Artikel> katalog
	 * <li>String suche
	 * </ul>
	 * 
	 * <p>
	 * Sortierungen:
	 * <ul>alpha: Alphabetisch A-Z
	 * <ul>rev_alpha: Alphabetisch Z-A
	 * <ul>price: nach Preis aufsteigend
	 * <ul>rev_price: nach Preis absteigend
	 * <li>
	 *
	 * @param model
	 *            ein Objekt vom Typ Spring-Model
	 * @param suche
	 *            ein String, nach dem im Katalog gesucht werden soll
	 * @param sortierung
	 *            die gewünschte sortierung als String
	 * @return ein String zur Identifizierung des Templates ("Shop/artikelKatalog")
	 */
	@PostMapping("/artikel")
	public String artikelKatalog(Model model, @RequestParam String suche, @RequestParam String sortierung) {
		model.addAttribute("katalog", katalog.findAndSort(suche, sortierung));
		model.addAttribute("suche", suche);
		model.addAttribute("sortierung", sortierung);
		return "Shop/artikelKatalog";
	}

	/**
	 * Fügt dem übergebenen Model die Attribute zur Darstellung des Katalogs hinzu,
	 * es werden nur Artikel übergeben, die in Name oder BEschreibung den String
	 * suche enthalten
	 * <p>
	 * Modelparameter:
	 * <ul>
	 * <li>Artikel artikel
	 * </ul>
	 *
	 * @param artikel
	 *            ein Artikel, dessen Details angezeigt werden sollen
	 * @param model
	 *            ein Objekt vom Typ Spring-Model
	 * @return ein String zur Identifizierung des Templates ("Shop/artikelDetail")
	 */
	@GetMapping("/artikel/{artikel}")
	public String detail(@PathVariable Artikel artikel, Model model) {
		
		Optional<InventoryItem> item = inventar.findByProductIdentifier(artikel.getId());
		Quantity quantity = item.map(InventoryItem::getQuantity).orElse(NONE);
		
		if (quantity.isGreaterThan(Quantity.of(0)) && artikel.getGelisted()) {
			model.addAttribute("orderable", true);
		} else {
			model.addAttribute("orderable", false);
		}
		
		model.addAttribute("artikel", artikel);
		model.addAttribute("quantity", quantity);
		return "Shop/artikelDetail";
	}

	/**
	 * Erstellt einen neuen Artikel
	 *
	 * @param formular
	 *            ein (valides) ArtikelErstellFormular, das die Eigenschaften des zu
	 *            erstellenden Artikels enthält
	 * @return ein String zur rückführung auf die Funktion artikelKatalog() dieser
	 *         Klasse
	 */
	@PreAuthorize("hasRole('ROLE_BOSS')")
	@PostMapping("/artikelErstellen")
	public String erstelleNeu(@ModelAttribute("formular") @Valid ArtikelErstellFormular formular,
			BindingResult ergebnis) {
		if (ergebnis.hasErrors()) {
			return "Shop/artikelErstellen";
		}
		Artikel neu = manager.erstelleArtikel(formular);
		return "redirect:/artikel/" + neu.getId().toString();
	}

	/**
	 * Fügt dem übergebenen Model die Attribute zur Darstellung des Formulars zur
	 * Erstellung eines neuen Artikels hinzu
	 * <p>
	 * Modelparameter:
	 * <ul>
	 * <li>ArtikelErstellFormular formular
	 * </ul>
	 *
	 * @param model
	 *            ein Objekt vom Typ Spring-Model
	 * @return ein String zur Identifizierung des Templates
	 *         ("Shop/artikelErstellen")
	 */
	@PreAuthorize("hasRole('ROLE_BOSS')")
	@GetMapping("/artikelErstellen")
	public String erstelle(Model model) {
		model.addAttribute("formular", new ArtikelErstellFormular());
		return "Shop/artikelErstellen";
	}

	/**
	 * Fügt dem übergebenen Model die Attribute zur Darstellung des Formulars zur
	 * Bearbeitung eines existierenden Artikels hinzu
	 * <p>
	 * Modelparameter:
	 * <ul>
	 * <li>Artikel artikel
	 * <li>ArtikelErstellFormular formular
	 * </ul>
	 *
	 * @param artikel
	 *            der zu bearbeitende Artikel
	 * @param model
	 *            ein Objekt vom Typ Spring-Model
	 * @return ein String zur Identifizierung des Templates
	 *         ("Shop/artikelErstellen")
	 */
	@PreAuthorize("hasRole('ROLE_BOSS')")
	@GetMapping("/artikel/{artikel}/bearbeiten")
	public String bearbeiteExistierend(@PathVariable Artikel artikel, Model model) {
		model.addAttribute("artikel", artikel);
		model.addAttribute("formular", new ArtikelErstellFormular());
		return "Shop/artikelBearbeiten";
	}

	/**
	 * Fügt dem übergebenen Model die Attribute zur Darstellung des Formulars zur
	 * Bearbeitung eines existierenden Artikels hinzu
	 *
	 * @param artikel
	 *            der zu bearbeitende Artikel
	 * @param formular
	 *            ein (valides) ArtikelErstellFormular
	 * @param ergebnis
	 *            ein Objekt vom Typ Bindingresult, das die Ergebnisse der
	 *            Auswertung des Formulars enthält
	 * @return ein String zur Identifizierung des Templates
	 *         ("Shop/artikelBearbeiten") bzw. zur rückführung auf die Funktion
	 *         detail() dieser Klasse
	 */
	@PreAuthorize("hasRole('ROLE_BOSS')")
	@PostMapping("/artikel/{artikel}/bearbeiten")
	public String bearbeite(@PathVariable Artikel artikel,
			@ModelAttribute("formular") @Valid ArtikelErstellFormular formular, BindingResult ergebnis) {
		if (ergebnis.hasErrors()) {
			return "Shop/artikelBearbeiten";
		}
		Artikel bearbeitet = manager.bearbeiteArtikel(artikel, formular);
		return "redirect:/artikel/" + bearbeitet.getId().toString();
	}

	/**
	 * löscht den übergebenen Artikel aus Katalog und Inventar
	 * 
	 * @param artikel
	 *            der zu löschende Artikel
	 * @return ein String zur Rückführung auf die Funktion artikelKatalog() dieser
	 *         Klasse
	 */
	@PreAuthorize("hasRole('ROLE_BOSS')")
	@GetMapping("/artikel/{artikel}/loeschen")
	public String loeschen(@PathVariable Artikel artikel) {
		manager.loesche(artikel);
		return "redirect:/inventarverwaltung";
	}
}
