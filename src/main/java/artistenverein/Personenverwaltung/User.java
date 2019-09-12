package artistenverein.Personenverwaltung;

import javax.persistence.*;

import org.salespointframework.useraccount.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Die Klasse User stellt zusammen mit der Salespointklasse UserAccount einen
 * Nutzer dar.
 * 
 */
@Entity
public class User {

	private @Id @GeneratedValue long id;
	private String beschreibung = "";
	private String adress;
	private String telefon;
	@OneToOne
	private UserAccount userAccount;

	/**
	 * leerer Konstruktor, für Spring/Hibernate notwendig, sollte nicht verwendet
	 * werden
	 * 
	 */
	@SuppressWarnings("unused")
	private User() {
	}

	/**
	 * Konstuktor
	 * 
	 * @param userAccount
	 *            Der Spring UserAccount der dem User zugrunde liegt
	 */
	@Autowired
	public User(UserAccount userAccount) {
		this.userAccount = userAccount;
		// this.sperrzeiten = new Zeitverwaltung(repozeit);
	}

	/**
	 * gibt die Id der Klasse zurück
	 * 
	 * @return id
	 */
	public long getId() {
		return id;
	}

	/**
	 * gibt den mit der Klase vernknüpften UserAccount zurück
	 * 
	 * @return UserAccount
	 */
	public UserAccount getUserAccount() {
		return userAccount;
	}

	/**
	 * gibt die Adresse des Nutzers zurück
	 * 
	 * @return id
	 */
	public String getAdress() {
		return adress;
	}

	/**
	 * setzt die Adresse des Nutzers auf den übergebenen Wert
	 * 
	 * @param adress
	 *            die neue Adresse des Nutzers
	 */
	public void setAdress(String adress) {
		this.adress = adress;

	}

	/**
	 * gibt die Beschreibung des Nutzers zurück
	 * 
	 * @return beschreibung
	 */
	public String getBeschreibung() {
		return beschreibung;
	}

	/**
	 * setzt die Beschreibung des Nutzers auf den übergebenen Wert
	 * 
	 * @param beschreibung
	 *            die neue Beschreibung des Nutzers
	 */
	public void setBeschreibung(String beschreibung) {
		this.beschreibung = beschreibung;

	}

	/**
	 * setzt die Telefonnummer des Nutzers auf den übergebenen Wert
	 * 
	 * @param telefon
	 *            die neue Telefonnummer des Nutzers
	 */
	public void setTelefon(String telefon) {
		this.telefon = telefon;
	}

	/**
	 * gibt die Telefonnummer des Nutzers zurück
	 * 
	 * @return Telefonnummer
	 */
	public String getTelefon() {
		return this.telefon;
	}
}
