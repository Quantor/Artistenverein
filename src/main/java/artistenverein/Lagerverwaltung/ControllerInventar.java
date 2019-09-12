package artistenverein.Lagerverwaltung;

import java.util.Optional;

import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.quantity.Quantity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Der Controller ControllerInventar ist ein Spring-Controller zuständig für
 * alle Anfrage im Bezug auf das Lager(Inventar) des Shops des Artistenvereins
 * 
 * @author Anton Reinhard
 * @author Emanuel Kern
 */
@Controller
public class ControllerInventar {

	private final Inventory<InventoryItem> inventar;
	private KonfigurationsRepository konfigurationsRep;
	private static final int DEFAULT_MENGE = 10;
	private static final int DEFAULT_RABATT = 20;
	private static final int PROZENT_100 = 100;

	/**
	 * Erstellt einen neuen Controller mit dem gegebenen Inventar
	 *
	 * @param inventar
	 *            das Salespoint-Inventory des Shops, normalerweise von Spring
	 *            automatisch verknüpft
	 */
	@Autowired
	public ControllerInventar(Inventory<InventoryItem> inventar, KonfigurationsRepository konfigurationsRep) {
		Assert.notNull(inventar, "inventar darf nicht null sein!");
		Assert.notNull(konfigurationsRep, "konfigurationsRep darf nicht null sein!");
		
		this.inventar = inventar;
		this.konfigurationsRep = konfigurationsRep;
		if (!this.konfigurationsRep.enthaeltMindestMenge()) {
			this.konfigurationsRep.save(
					new InventarKonfiguration(DEFAULT_MENGE, InventarKonfiguration.KonfigurationsTyp.MINDEST_MENGE));
		}
		if (!this.konfigurationsRep.enthaeltRabatt()) {
			this.konfigurationsRep
					.save(new InventarKonfiguration(DEFAULT_RABATT, InventarKonfiguration.KonfigurationsTyp.RABATT));
		}
	}

	/**
	 * Fügt dem übergebenen Model die Attribute zur Darstellung des Inventars hinzu
	 * <p>
	 * Modelparameter:
	 * <ul>
	 * <li>Iterable<InventoryItem> stock
	 * <li>Quantity mindestmenge
	 * </ul>
	 *
	 * @param model
	 *            ein Objekt vom Typ Spring-Model
	 * @return ein String zur Identifizierung des Templates
	 *         ("Inventarverwaltung/stock")
	 */
	@GetMapping("/inventarverwaltung")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	public String vorrat(Model model) {

		model.addAttribute("mindestMenge", konfigurationsRep.getMindestMenge().getQuantity());
		model.addAttribute("rabatt", konfigurationsRep.getRabatt().getRabatt());
		model.addAttribute("stock", inventar.findAll());

		return "Inventarverwaltung/stock";
	}

	/**
	 * Diese Funktion dient dem Nachkauf der spezifizierten Menge von Artikeln im
	 * Inventar für alle Artikel
	 *
	 * @param artikel
	 *            ein Array von Artikeln, auf die die Funktion ausgeführt wird
	 * @param number
	 *            ein Array von long-Werten zur Bestimmung der zu bestellenden
	 *            Artikel
	 * @return ein String zur rückführung auf die Funktion stock() dieser Klasse
	 */
	@RequestMapping(value = "/nachbestellungAlle", method = RequestMethod.POST)
	@PreAuthorize("hasRole('ROLE_BOSS')")
	public String vorrat(@RequestParam("item") Artikel[] artikel, @RequestParam("number") String[] number) {
		for (InventoryItem i : inventar.findAll()) {
			for (int j = 0; j < artikel.length; j++) {
				if (i.getProduct().equals(artikel[j])) {
					i.increaseQuantity(Quantity.of(Integer.parseInt(number[j])));
					inventar.save(i);
					break;
				}
			}
		}

		return "redirect:/inventarverwaltung";
	}

	/**
	 * Diese Funktion dient dem Nachkauf der spezifizierten Menge von Artikeln im
	 * Inventar für den spezifizierten Artikel
	 *
	 * @param pid
	 *            der spezifische Artikel, für den die Aktion ausgeführt wird
	 * @param artikel
	 *            ein Array von Artikeln, auf die die Funktion ausgeführt wird
	 * @param number
	 *            ein Array von long-Werten zur Bestimmung der zu bestellenden
	 *            Artikel
	 * @return ein String zur rückführung auf die Funktion stock() dieser Klasse
	 */
	@RequestMapping(value = "/nachbestellungEinzeln", method = RequestMethod.POST)
	@PreAuthorize("hasRole('ROLE_BOSS')")
	public String vorrat(@RequestParam Artikel pid, @RequestParam("item") Artikel[] artikel,
			@RequestParam("number") String[] number) {

		Optional<InventoryItem> item = inventar.findByProduct(pid);
		if (item.isPresent()) {
			InventoryItem invItem = item.get();
			for (int j = 0; j < artikel.length; j++) {
				if (artikel[j] == null) {
					continue;
				}

				if (artikel[j].equals(pid)) {
					invItem.increaseQuantity(Quantity.of(Integer.parseInt(number[j])));
					inventar.save(invItem);
				}
			}
		}

		return "redirect:/inventarverwaltung";
	}

	/**
	 * Setzt die Mindestbestellmenge auf den übergebenen Wert
	 *
	 * @param mindestMenge
	 *            ein Objekt vom Typ Spring-Model
	 * @return ein String zur rückführung auf die Funktion stock() dieser Klasse
	 */
	@PostMapping("/inventarverwaltung/mindestMenge")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	public String setMindestMenge(@RequestParam String mindestMenge) {
		int mindestMengeInt = konfigurationsRep.getMindestMenge().getQuantity().getAmount().intValue();
		try {
			mindestMengeInt = Integer.parseInt(mindestMenge);
		} catch (NumberFormatException e) {
			return "redirect:/inventarverwaltung";
		}
		if (mindestMengeInt > 0) {
			InventarKonfiguration mindestMengeKonfiguration = konfigurationsRep.getMindestMenge();
			mindestMengeKonfiguration.setWert(mindestMengeInt);
			konfigurationsRep.save(mindestMengeKonfiguration);
			
			autoBuyer();
		}

		return "redirect:/inventarverwaltung";
	}

	/**
	 * Setzt die Mindestbestellmenge auf den übergebenen Wert
	 *
	 * @param mindestMenge
	 *            ein Objekt vom Typ Spring-Model
	 * @return ein String zur rückführung auf die Funktion stock() dieser Klasse
	 */
	@PostMapping("/inventarverwaltung/rabatt")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	public String setRabatt(@RequestParam String rabatt) {
		int rabattInt = konfigurationsRep.getRabatt().getRabatt();
		try {
			rabattInt = Integer.parseInt(rabatt);
		} catch (NumberFormatException e) {
			return "redirect:/inventarverwaltung";
		}
		if (rabattInt > -1 && rabattInt < PROZENT_100) {
			InventarKonfiguration rabattKonfiguration = konfigurationsRep.getRabatt();
			rabattKonfiguration.setWert(rabattInt);
			konfigurationsRep.save(rabattKonfiguration);
		}
		
		return "redirect:/inventarverwaltung";
	}

	/**
	 * Wird von Spring automatisch ca. alle 20 Sekunden ausgeführt und kauft Artikel
	 * nach deren Anzahl unter die Mindestmenge fällt
	 */
	@Scheduled(fixedDelay = 20000)
	public void autoBuyer() {
		for (InventoryItem item : inventar.findAll()) {
			if (item.getQuantity().isLessThan(konfigurationsRep.getMindestMenge().getQuantity())) {
				int newQuantity = konfigurationsRep.getMindestMenge().getQuantity().getAmount()
						.subtract(item.getQuantity().getAmount()).intValue();
				item.increaseQuantity(Quantity.of(newQuantity));
				inventar.save(item);
			}
		}
	}

}
