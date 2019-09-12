package artistenverein.Veranstaltungen;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.util.Assert;

/**
 * Die Klasse Kommentar erzeugt einen Kommentar mit Text und Erstelldatum
 * 
 * @author Luisa Leopold
 */
@Entity
@Table(name = "KOMMENTARE")
public class Kommentar implements Serializable {

	private static final long serialVersionUID = -7114101035786254953L;

	private @Id @GeneratedValue long id;

	private String text;
	private LocalDateTime datum;

	@SuppressWarnings("unused")
	private Kommentar() {}

	/**
	 * Erstellt einen neuen Kommentar mit folgenden Parametern
	 * @param text
	 * 			eingegebener Kommentar als String
	 * @param datum
	 * 			Erstelldatum des Kommentar als LocalDateTime Objekt
	 */
	public Kommentar(String text, LocalDateTime datum) {
		
		Assert.notNull(text, "text darf nicht null sein!");
		Assert.notNull(datum, "datum darf nicht null sein!");

		this.text = text;
		this.datum = datum;
	}

	public long getId() {
		return id;
	}

	public String getText() {
		return text;
	}

	public LocalDateTime getDatum() {
		return datum;
	}
	
	@Override
	public String toString() {
		return text;
	}
	
}
