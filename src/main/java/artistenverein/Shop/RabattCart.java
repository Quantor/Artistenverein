package artistenverein.Shop;

import javax.money.MonetaryAmount;

import org.salespointframework.order.Cart;
import org.springframework.util.Assert;

import artistenverein.Lagerverwaltung.KonfigurationsRepository;

/**
 * Die Klasse RabattCart erweitert die Salespointklasse Cart um die
 * Rabattfunktionalität
 * 
 * @author Emanuel Kern
 */
public class RabattCart extends Cart {
	
	private static final double PROZENT_100 = 100.0;

	private KonfigurationsRepository konfigurationsRep;
	private boolean rabatt = false;

	/**
	 * Erstellt ein neues Objekt vom Typ RabattCart. Der leere Konstruktor wird von
	 * Spring/Hibernate benötigt uns sollte NICHT verwendet werden
	 */
	@SuppressWarnings("unused")
	public RabattCart() {
		super();
		// required by Spring
	}

	/**
	 * Erstellt ein neues Objekt vom Typ RabattCart mit den gegebenen Parametern
	 * 
	 * @param konfigurationsRep
	 *            das Konfigurationsrepository des Shops zur Ermittelung des
	 *            aktuellen Rabatts falls dieser aktiviert ist
	 */
	public RabattCart(KonfigurationsRepository konfigurationsRep) {
		Assert.notNull(konfigurationsRep, "konfigurationsRep darf nicht null!");
		this.konfigurationsRep = konfigurationsRep;
	}

	/**
	 * gibt den Kompletten Preis des Warenkorbs mit angewandtem Rabatt falls
	 * aktiviert zurück
	 *
	 * @return der Gesamtpreis des Warenkorbs als MonetaryAmount
	 */
	public MonetaryAmount getPrice() {
		if (rabatt) {
			double prozent = 1 - (konfigurationsRep.getRabatt().getRabatt() / PROZENT_100);
			return super.getPrice().multiply(prozent);
		} else {
			return super.getPrice();
		}
	}

	/**
	 * Setzt den RabattStatus des Warenkorbs auf den übergeben Wert
	 * 
	 * @param rabatt
	 *            der Rabattstatus als boolean
	 */
	public void setRabattStatus(boolean rabatt) {
		this.rabatt = rabatt;
	}

	/**
	 * Gibt den Rabattstatus der Bestellung zurück
	 * 
	 * @return der Rabattstatus der Bestellung als boolean
	 */
	public boolean getRabattStatus() {
		return rabatt;
	}
}
