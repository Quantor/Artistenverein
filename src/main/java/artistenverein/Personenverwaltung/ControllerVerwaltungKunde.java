
package artistenverein.Personenverwaltung;

import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Tizian Fischer
 * @version 1.0
 */
@Controller
public class ControllerVerwaltungKunde {
	
	private final ManagerUser artistManagement;
	/**
	 * Konstrukor
	 * @param artistManagement
	 * 		Spring Autowirded - Instanz des MangerUser
	 */
	@Autowired
	public ControllerVerwaltungKunde(ManagerUser artistManagement) {

		Assert.notNull(artistManagement, "artistManagement must not be null!");

		this.artistManagement = artistManagement;
	
	}
	
	/**
	 * Übersicht über die Buchen des aktuell eingeloggten Kunden
	 * @param userAccount
	 * @param model
	 * @return
	 */
	@GetMapping("/kundenverwaltung/termine")
	public String getKundenVerwaltungTermine(@LoggedIn UserAccount userAccount,Model model) {
		User user= artistManagement.findeUserAccount(userAccount);
		model.addAttribute("user", user );
		model.addAttribute("buchungen", artistManagement.getBuchungenZuKunde(userAccount));
		
		return ("VerwaltungKunde/termine");
	}
	/**
	 * Übersicht über die Rechnungen des aktuell eingeloggten Kunden
	 * @param userAccount
	 * @param model
	 * @return
	 */
	@GetMapping("/kundenverwaltung/rechnungen")
	public String getKundenRechnungen(@LoggedIn UserAccount userAccount,Model model) {
		User user= artistManagement.findeUserAccount(userAccount);
		model.addAttribute("user", user );
		
		
		return ("VerwaltungKunde/rechnungen");
	}
	
}