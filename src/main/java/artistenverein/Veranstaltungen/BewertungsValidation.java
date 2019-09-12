package artistenverein.Veranstaltungen;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.util.Assert;

/**
 * Ein Formular zur Validierung der Eingaben beim Erstellen
 * einer Bewertung
 * 
 * @author Luisa Leopold
 */
public class BewertungsValidation {
	
	@NotEmpty(message = "{BewertungsValidation.bewertung.NotEmpty}")
	private String bewertung;
	
	public String getBewertung() {
		return bewertung;
	}

	/**
	 * Setzt die Bewertung im Formular auf den übergebenen Wert
	 * 
	 * @param bewertung
	 * 			die zu setzende Bewertung
	 */
	public void setBewertung(String bewertung) {
		Assert.notNull(bewertung, "bewertung darf nicht null sein!");
		this.bewertung = bewertung;
	}
}