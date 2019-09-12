package artistenverein.Lagerverwaltung;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

/**
 * Ein Formular zur Validierung der Eingaben beim Erstellen oder Bearbeiten
 * eines Artikels
 * 
 * @author Emanuel Kern
 */
public class ArtikelErstellFormular {

	@NotEmpty(message = "{ItemCreationForm.name.NotEmpty}") //
	private String name;

	@NotEmpty(message = "{ItemCreationForm.price.NotEmpty}") //
	private String preis;

	private MultipartFile bild;

	@NotEmpty(message = "{ItemCreationForm.description.NotEmpty}") //
	private String beschreibung;

	/**
	 * Gibt den Namen des Artikels als String zurück
	 *
	 * @return der Namen des Artikels als String
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setzt den Namen des Artikels auf den übergebenen String
	 * 
	 * @param name
	 *            der Name des Artikels als String
	 */
	public void setName(String name) {
		Assert.notNull(name, "name darf nicht null sein!");
		this.name = name;
	}

	/**
	 * Gibt den Preis als String zurück
	 *
	 * @return der Preis des Artikels als String
	 */
	public String getPreis() {
		return preis;
	}

	/**
	 * Setzt den Preis des Artikels auf den übergebenen String
	 * 
	 * @param preis
	 *            der Preis des Artikels als String
	 */
	public void setPreis(String preis) {
		Assert.notNull(preis, "preis darf nicht null sein!");
		this.preis = preis;
	}

	/**
	 * Gibt das Bild des Artikels als MultipartFile zurück
	 *
	 * @return das Bild als MultipartFile
	 */
	public MultipartFile getBild() {
		return bild;
	}

	/**
	 * Setzt das Bild des Artikels auf die übergebene Datei
	 * 
	 * @param bild
	 *            das Bild als MultipartFile
	 */
	public void setBild(MultipartFile bild) {
		Assert.notNull(bild, "bild darf nicht null sein!");
		this.bild = bild;
	}

	/**
	 * Gibt die Beschreibung des Artikels als String zurück
	 *
	 * @return die Beschreibung des Artikels als String
	 */
	public String getBeschreibung() {
		return beschreibung;
	}

	/**
	 * Setzt die Beschreibung des Artikels auf den übergebenen String
	 * 
	 * @param beschreibung
	 *            die Beschreibung des Artikels als String
	 */
	public void setBeschreibung(String beschreibung) {
		Assert.notNull(beschreibung, "beschreibung darf nicht null sein!");
		this.beschreibung = beschreibung;
	}
}
