package artistenverein.Personenverwaltung;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.util.Assert;

/**
 * Aristengruppen: Representiert einen Verbund mehrerer Artisten
 * 
 * @author Tizian Fischer
 * @version 1.0
 */
@Entity
public class Artistengruppe {

	private @Id @GeneratedValue long id;
	private String gruppenname;

	@ManyToMany
	private Set<User> mitglieder = new HashSet<User>();

	@OneToOne
	private UserAccount userAccount;

	@SuppressWarnings("unused")
	private Artistengruppe() {
	}

	/**
	 * Konstruktor
	 * 
	 * @param gruppenname
	 *            Name der Gruppe
	 */
	public Artistengruppe(String gruppenname) {
		Assert.notNull(gruppenname, "Gruppenname darf nicht null sein!");
		Assert.hasText(gruppenname, "Gruppenname darf nicht leer sein!");
		this.gruppenname = gruppenname;
	}

	/**
	 * Fügt dieser Gruppe eine Mitglied hinzu
	 * 
	 * @param user
	 *            User der Hinzugefügt werden soll
	 * @return
	 */
	public boolean addMitglied(User user) {
		// Add Type Check
		return mitglieder.add(user);
	}
	/**
	 * 
	 * @return
	 */
	public String getGruppenname() {
		return gruppenname;
	}
	/**
	 * 
	 * @return
	 */
	public Set<User> getMitglieder() {
		return mitglieder;
	}

	/**
	 * Entfernt ein Mitglied aus der Gruppe
	 * 
	 * @param user
	 *            User der entfernt werden soll
	 * @return
	 */
	public boolean entferneMitglied(User user) {
		return mitglieder.remove(user);
	}
}
