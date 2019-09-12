package artistenverein.Shop;

import java.util.Iterator;
import java.util.Optional;

import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.order.Cart;
import org.salespointframework.order.CartItem;
import org.salespointframework.order.Order;
import org.salespointframework.order.OrderManager;
import org.salespointframework.order.OrderStatus;
import org.salespointframework.payment.Cash;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import artistenverein.Lagerverwaltung.Artikel;
import artistenverein.Lagerverwaltung.KonfigurationsRepository;
import artistenverein.Veranstaltungen.BuchungRepository;

/**
 * Der Controller ControllerBestellungen ist ein Spring-Controller zuständig für
 * alle Anfrage im Bezug auf den Kauf von Artikel bzw. den Warenkorb des
 * Artistenvereins
 * 
 * @author Emanuel Kern
 */
@Controller
@SessionAttributes("rabattCart")
class ControllerBestellungen {

	private final KonfigurationsRepository konfigurationsRep;
	private final OrderManager<Order> orderManager;
	private final BuchungRepository buchungRep;
	private final Inventory<InventoryItem> inventar;
	private static final int DEFAULT_BESTELLUNG = 1;
	private static final Quantity NONE = Quantity.of(1);

	/**
	 * Erstellt einen neuen Controller mit dem gegebenen Katalog und ArtikelManager
	 *
	 * @param orderManager
	 *            der Salespoint-OrderManager des Shops, normalerweise von Spring
	 *            automatisch verknüpft
	 * @param konfigurationsRep
	 *            das Konfigurationsrepository des Shops, normalerweise von Spring
	 *            automatisch verknüpft
	 * @param buchungRep
	 *            das Buchungsrepository des Shops, normalerweise automatisch von
	 *            Spring verknüpft
	 * @param inventar
	 *            das Inventar des Shops, normalerweise von Spring automatisch
	 *            verknüpft
	 */
	@Autowired
	public ControllerBestellungen(OrderManager<Order> orderManager, KonfigurationsRepository konfigurationsRep,
			BuchungRepository buchungRep, Inventory<InventoryItem> inventar) {

		Assert.notNull(orderManager, "orderManager darf nicht null sein!");
		Assert.notNull(konfigurationsRep, "konfigurationsRep darf nicht null sein!");
		Assert.notNull(buchungRep, "buchungRep darf nicht null sein!");
		Assert.notNull(inventar, "inventar darf nicht null sein!");
		this.orderManager = orderManager;
		this.konfigurationsRep = konfigurationsRep;
		this.buchungRep = buchungRep;
		this.inventar = inventar;
	}

	/**
	 * Erstellt ein neues Spring-SessionAttribut cart, den Warenkorb des Nutzers
	 *
	 * @return eine neue Instanz der Salespoint-Klasse Cart
	 */
	@ModelAttribute("rabattCart")
	public Cart initialisiereWarenkorb() {
		return new RabattCart(konfigurationsRep);
	}

	/**
	 * Fügt dem Warenkorb des Nutzers den übergebenen Artikel mit der spezifizierten
	 * Menge hinzu
	 *
	 * @param artikel
	 *            der hinzuzufügende Artikel
	 * @param anzahl
	 *            die Anzahl die hinzugefügt werden soll als String
	 * @param rabattCart
	 *            der Warenkorb des Nutzers, zu dem der Artikel hinzugefügt werden
	 *            soll
	 * @param nutzerAccount
	 *            Optional: der Nutzeraccount des eingeloggten Nutzers
	 * @return ein String zur rückführung auf die Funktion warenkorb() dieser Klasse
	 */
	@PostMapping("/warenkorb/hinzufuegen")
	public String artikelHinzufuegen(@RequestParam("pid") Artikel artikel, @RequestParam("number") String anzahl,
			@ModelAttribute RabattCart rabattCart, Optional<UserAccount> nutzerAccount) {
		int anzahlInt = DEFAULT_BESTELLUNG;
		try {
			anzahlInt = Integer.parseInt(anzahl);
		} catch (NumberFormatException e) {
			// just add one item to the cart
		}
		ungelisteteArtikelEntfernen(rabattCart);

		Optional<InventoryItem> item = inventar.findByProductIdentifier(artikel.getId());
		Quantity quantity = item.map(InventoryItem::getQuantity).orElse(NONE);

		int amount = anzahlInt <= 0 || anzahlInt > quantity.getAmount().intValue() ? 1 : anzahlInt;
		if (nutzerAccount.isPresent()) {
			rabattAnwenden(rabattCart, nutzerAccount.get());
		}
		rabattCart.addOrUpdateItem(artikel, Quantity.of(amount));
		return "redirect:/warenkorb";
	}

	/**
	 * Übergibt das Template zur Darstellung des Warenkorbs und fügt dem model die
	 * entsprechenden Parameter hinzu. Hat der Parameter kaufen den Wert "true" so
	 * wird überprüft ob der Warenkorb im aktuellen Zustand gekauft werden kann und
	 * leitet entsprechend weiter, ansonsten wird eine Fehlermeldung angezeigt
	 * 
	 * @param model
	 *            ein Objekt vom Typ Spring-Model
	 * @param rabattCart
	 *            der Warenkorb des Nutzers, der angezeigt bzw. gekauft werden soll
	 * @param nutzerAccount
	 *            Optional:der Nutzeraccount des eingeloggten Nutzers
	 * @param kaufen
	 *            ein String, der angibt ob der Kauf ausgeführt werden soll indem er
	 *            den Wert "true" annimmt
	 *
	 * @return ein String zur identifizierung des Templates ("Shop/warenkorb")
	 */
	@PostMapping("/warenkorb")
	public String warenkorb(Model model, @ModelAttribute RabattCart rabattCart,
			@LoggedIn Optional<UserAccount> nutzerAccount, String kaufen) {

		System.out.println("got called");

		if (nutzerAccount.isPresent()) {
			rabattAnwenden(rabattCart, nutzerAccount.get());
		}

		if (kaufen.equals("true")) {
			for (CartItem item : rabattCart) {
				Optional<InventoryItem> optInvItem = inventar.findByProduct(item.getProduct());
				if (!optInvItem.isPresent()
						|| !optInvItem.get().getQuantity().isGreaterThanOrEqualTo(item.getQuantity())) {
					model.addAttribute("fehler", true);
					break;
				}
			}
		}

		if (!model.containsAttribute("fehler")) {
			String success = nutzerAccount.map(account -> {

				if (!account.hasRole(Role.of("ROLE_CUSTOMER"))) {
					return "false";
				}

				RabattOrder order = new RabattOrder(account, Cash.CASH, konfigurationsRep.getRabatt().getRabatt(),
						rabattCart.getRabattStatus());

				rabattCart.addItemsTo(order);

				orderManager.payOrder(order);
				orderManager.completeOrder(order);

				rabattCart.clear();

				return "true";

			}).orElse("false");

			model.addAttribute("success", Boolean.parseBoolean(success));
		}

		if (!model.containsAttribute("success")) {
			model.addAttribute("success", false);
		}
		if (!model.containsAttribute("fehler")) {
			model.addAttribute("fehler", false);
		}

		model.addAttribute("rabatt", konfigurationsRep.getRabatt().getRabatt());

		return "Shop/warenkorb";
	}

	/**
	 * Übergibt das Template zur Darstellung des Warenkorbs
	 * 
	 * @param model
	 *            ein Objekt vom Typ Spring-Model
	 * @param rabattCart
	 *            der Warenkorb des Nutzers, der angezeigtwerden soll
	 * @param nutzerAccount
	 *            Optional:der Nutzeraccount des eingeloggten Nutzers
	 *
	 * @return ein String zur identifizierung des Templates ("Shop/warenkorb")
	 */
	@GetMapping("/warenkorb")
	public String warenkorb(Model model, @ModelAttribute RabattCart rabattCart,
			@LoggedIn Optional<UserAccount> nutzerAccount) {
		if (nutzerAccount.isPresent()) {
			rabattAnwenden(rabattCart, nutzerAccount.get());
		}
		ungelisteteArtikelEntfernen(rabattCart);

		model.addAttribute("fehler", false);

		model.addAttribute("rabatt", konfigurationsRep.getRabatt().getRabatt());

		return "Shop/warenkorb";
	}

	/**
	 * Fügt dem übergebenen Model die Attribute zur Darstellung der vollendeten
	 * Bestellungen (des Nutzers/ aller Bestellungen für den Vorstand) hinzu
	 * <p>
	 * Modelparameter:
	 * <ul>
	 * <li>Iterable<Order> ordersCompleted
	 * </ul>
	 *
	 * @param model
	 *            ein Objekt vom Typ Spring-Model
	 * @param userAccount
	 *            Optional: der zurzeit eingeloggte Nutzer
	 * @return ein String zur Identifizierung des Templates ("Shop/bestellungen")
	 *         bzw. zur Rückführung auf die Hauptseite, wenn der Nutzer nicht die
	 *         nötigen Berechtigungen hat
	 */
	@GetMapping(value = { "/bestellungen", "/kundenverwaltung/rechnungen" })
	public String bestellungen(Model model, @LoggedIn Optional<UserAccount> userAccount) {
		if (!userAccount.isPresent()) {
			return "redirect:/";
		} else {
			UserAccount account = userAccount.get();
			if (account.hasRole(Role.of("ROLE_CUSTOMER"))) {
				model.addAttribute("ordersCompleted", orderManager.findBy(account));
			} else if (account.hasRole(Role.of("ROLE_BOSS"))) {
				model.addAttribute("ordersCompleted", orderManager.findBy(OrderStatus.COMPLETED));
			} else {
				return "redirect:/";
			}

		}

		return "Shop/bestellungen";
	}

	/**
	 * Fügt dem übergebenen Model die Attribute zur Darstellung der Details der
	 * ausgewählten Bestellung hinzu
	 * <p>
	 * Modelparameter:
	 * <ul>
	 * <li>Order order
	 * </ul>
	 *
	 * @param model
	 *            ein Objekt vom Typ Spring-Model
	 * @param order
	 *            die Bestellung, zu der Details angezeigt werden sollen
	 * @param nutzerAccount
	 *            der zurzeit eingeloggte Nutzer
	 * @return ein String zur Identifizierung des Templates
	 *         ("Shop/bestellungDetail") bzw. zur Rückführung auf die Hauptseite,
	 *         wenn der Nutzer nicht die nötigen Berechtigungen hat
	 */
	@GetMapping("/bestellungen/{order}")
	public String detail(@PathVariable Order order, Model model, @LoggedIn Optional<UserAccount> nutzerAccount) {
		String retString = "redirect:/";
		if (!nutzerAccount.isPresent()) {
			retString =  "redirect:/";
		} else {
			UserAccount account = nutzerAccount.get();
			if (account.hasRole(Role.of("ROLE_CUSTOMER"))) {
				if (order.getUserAccount().equals(account)) {
					model.addAttribute("order", order);
					retString = "Shop/bestellungDetail";
				} else {
					retString = "redirect:/";
				}
			} else if (account.hasRole(Role.of("ROLE_BOSS"))) {
				model.addAttribute("order", order);
				retString = "Shop/bestellungDetail";
			} else {
				retString = "redirect:/";
			}
		}

		return retString;
	}

	/**
	 * leert den Warenkorb des Nutzers
	 * 
	 * @param cart
	 *            der zu leerende Warenkorb
	 * @return ein String zur Rückführung auf den Warenkorb
	 */
	@PostMapping("/warenkorb/leeren")
	public String leeren(@ModelAttribute RabattCart rabattCart) {

		rabattCart.clear();

		return "redirect:/warenkorb";
	}

	/**
	 * verhindert ein unbeabsichtigtes löschen des warenkorbs durch eine kopieren
	 * des Links /warenkorb/leeren in die Adresszeile des Browsers
	 * 
	 * @return ein String zur Rückführung auf den Warenkorb
	 */
	@GetMapping("/warenkorb/leeren")
	public String leeren() {

		return "redirect:/warenkorb";
	}

	/**
	 * Verändert die Anzahl des übergebenen Artikels im Warenkorb bzw. löscht
	 * diesen, wenn die Zahl gleich 0 ist
	 *
	 * @param artikel
	 *            der Artikel, dessen Anzahl geändert werden soll
	 * @param cartItem
	 *            das Element des Warenkorbs das bearbeitet werden soll
	 * @param anzahl
	 *            die neue Anzahl des Artikels
	 * @param cart
	 *            der Warenkorb des Nutzers
	 * @return ein String zur Identifizierung des Templates
	 *         ("Shop/bestellungDetail") bzw. zur Rückführung auf die Hauptseite,
	 *         wenn der Nutzer nicht die nötigen Berechtigungen hat
	 */
	@PostMapping("warenkorb/bearbeiten")
	public String bearbeiten(@RequestParam("pid") Artikel artikel, @RequestParam("cid") String cartItem,
			@RequestParam("number") String anzahl, @ModelAttribute RabattCart rabattCart) {
		if (!artikel.getGelisted()) {
			ungelisteteArtikelEntfernen(rabattCart);
			return "redirect:/warenkorb";
		}
		
		int anzahlInt = DEFAULT_BESTELLUNG;
		try {
			anzahlInt = Integer.parseInt(anzahl);
		} catch (NumberFormatException e) {
			return "redirect:/warenkorb";
		}

		Optional<InventoryItem> item = inventar.findByProductIdentifier(artikel.getId());
		Quantity quantity = item.map(InventoryItem::getQuantity).orElse(NONE);

		int amount = anzahlInt;

		if (anzahlInt < 0) {
			amount = 1;
		} else if (anzahlInt > quantity.getAmount().intValue()) {
			amount = quantity.getAmount().intValue();
		}
		
		rabattCart.removeItem(cartItem);
		if (amount > 0) {
			rabattCart.addOrUpdateItem(artikel, Quantity.of(amount));
		}
		return "redirect:/warenkorb";
	}

	/**
	 * leitet den Nutzer zum Warenkorb weiter, sollte er den Link
	 * /warenkorb/bearbeiten in die Adresszeile kopieren
	 * 
	 * @return ein String zur Rückführung auf den Warenkorb
	 */
	@GetMapping("warenkorb/bearbeiten")
	public String bearbeiten() {
		return "redirect:/warenkorb";
	}

	/**
	 * setzt den Rabatstatus des Warenkorbs des Nutzers auf true, wenn dieser
	 * berechtigt ist Rabatt auf seinen Einkauf zu bekommen
	 */
	private void rabattAnwenden(@ModelAttribute RabattCart rabattCart, UserAccount nutzerAccount) {
		if (buchungRep.bekommtRabatt(nutzerAccount)) {
			rabattCart.setRabattStatus(true);
		} else {
			rabattCart.setRabattStatus(false);
		}
	}
	
	/**
	 * entfernt ungelistete Artikel aus dem Warenkorb
	 */
	private void ungelisteteArtikelEntfernen(@ModelAttribute RabattCart rabattCart) {
		for (Iterator<CartItem> it = rabattCart.iterator(); it.hasNext();) {
			CartItem ci = it.next();
			Artikel a = (Artikel) ci.getProduct();
			Optional<InventoryItem> item = inventar.findByProductIdentifier(a.getId());
			boolean success = item.map(account -> {return true;}).orElse(false);
			if (!success) {
				it.remove();
			}
		}
	}
}
