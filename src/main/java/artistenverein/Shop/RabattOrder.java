package artistenverein.Shop;

import javax.money.MonetaryAmount;
import javax.persistence.Entity;

import org.salespointframework.order.Order;
import org.salespointframework.payment.PaymentMethod;
import org.salespointframework.useraccount.UserAccount;

/**
 * Die Klasse RabattOrder erweitert die Salespointklasse Order um die
 * Rabattfunktionalität
 * 
 * @author Emanuel Kern
 */
@Entity
public class RabattOrder extends Order {

	private static final long serialVersionUID = 2717699022361139920L;
	private boolean rabattStatus = false;
	private int rabatt;
	private static final double PROZENT_100 = 100.0;
	private static final int PROZENT_MAX = 99;
	private static final int STRING_PRECISION = 2;

	/**
	 * Erstellt ein neues Objekt vom Typ RabattOrder. Der leere Konstruktor wird von
	 * Spring/Hibernate benötigt uns sollte NICHT verwendet werden
	 */
	public RabattOrder() {
		super();
		// required by Spring
	}

	/**
	 * Erstellt ein neues Objekt vom Typ RabattOrder mit den gegebenen Parametern
	 * 
	 * @param userAccount
	 *            der Nutzeraccount der die Bestellung aufgegeben hat
	 * @param paymentMethod
	 *            die Bezahlmethode die der Nutzer verwendet
	 * @param rabatt
	 *            der Rabatt in Prozent, den der Nutzer auf seine Bestellung erhält.
	 *            wird nur angewendet wenn rabattStatus auf true steht
	 * @param rabattStatus
	 *            gibt an ob der Nutzer Rabatt auf seinen Einkauf erhält
	 */
	public RabattOrder(UserAccount userAccount, PaymentMethod paymentMethod, int rabatt, boolean rabattStatus) {
		super(userAccount, paymentMethod);
		if (rabatt < 0) {
			throw new IllegalArgumentException("rabatt darf nicht kleiner 0 sein!");
		} else if (rabatt > PROZENT_MAX) {
			throw new IllegalArgumentException("rabatt darf nicht größer 99 sein!");
		}
		this.rabatt = rabatt;
		this.rabattStatus = rabattStatus;
	}

	/**
	 * gibt den Kompletten Preis der Bestellung mit angewandtem Rabatt falls
	 * aktiviert zurück
	 *
	 * @return der Gesamtpreis der Bestellung als MonetaryAmount
	 */
	@Override
	public MonetaryAmount getTotalPrice() {
		if (rabattStatus) {
			double prozent = 1 - (rabatt / PROZENT_100);
			return super.getTotalPrice().multiply(prozent);
		} else {
			return super.getTotalPrice();
		}
	}
	
	/**
	 * gibt den Kompletten Preis der Bestellung mit angewandtem Rabatt falls
	 * aktiviert als gerundeten String zurück
	 *
	 * @return der Gesamtpreis der Bestellung als String
	 */
	public String getTotalPriceString() {
		if (rabattStatus) {
			if (getTotalPrice().toString().contains(".")) {
				String ret = getTotalPrice().toString();
				String ret2 = ret.substring(ret.lastIndexOf(".") + 1);
				ret = ret.substring(0, ret.lastIndexOf(".") + 1);
				ret2 = ret2.substring(0, STRING_PRECISION);
				return ret + ret2;
			} else {
				return getTotalPrice().toString();
			}
		} else {
			return super.getTotalPrice().toString();
		}
	}

	/**
	 * Gibt den Rabattstatus der Bestellung zurück
	 * 
	 * @return der Rabattstatus der Bestellung als boolean
	 */
	public boolean getRabattStatus() {
		return rabattStatus;
	}

	/**
	 * Gibt den den Wert des Rabatts in Prozent zurück
	 * 
	 * @return der Rabatt der Bestellung in Prozent als int
	 */
	public int getRabatt() {
		return rabatt;
	}

}
