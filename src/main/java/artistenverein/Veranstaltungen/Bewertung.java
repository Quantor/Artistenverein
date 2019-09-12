package artistenverein.Veranstaltungen;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.salespointframework.useraccount.UserAccount;
import org.springframework.util.Assert;

/**
 * Die Klasse Bewertung erstellt Bewertungen für Veranstaltungen mit Erstelldatum, der jeweiligen 
 * Bewertung und einem Kommentar
 * 
 * @author Luisa Leopold
 */
@Entity
@Table(name = "BEWERTUNG")
public class Bewertung implements Serializable {

	private static final long serialVersionUID = -7114101035786254953L;
	private @Id @GeneratedValue long id;

	private String text;
	private int bewertungInt;
	private LocalDateTime datum;
	@OneToOne
	private UserAccount autor;

	@SuppressWarnings("unused")
	private Bewertung() {}

	/**
	 * Erstellt eine neue Bewertung mit den angegebenen Parametern
	 * 
	 * @param text
	 * 			ein Kommentar zur Bewertung des Nutzers (kann auch leer sein) als String
	 * @param bewertung
	 * 			die Bewertung als Integer (von 1 bis 5)
	 * @param datum
	 * 			das datum an dem die Bewertung geschrieben wurde als LocalDateTime Objekt
	 * @param autor
	 * 			der Autor der Bewertung als UserAccount
	 */
	public Bewertung(String text, int bewertung, LocalDateTime datum, UserAccount autor) {
		
		Assert.notNull(text, "text darf nicht null sein!");
		Assert.notNull(datum, "datum darf nicht null sein!");
		Assert.notNull(autor, "autor darf nicht null sein!");

		this.text = text;
		this.bewertungInt = bewertung;
		this.datum = datum;
		this.autor = autor;
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

	public int getBewertung() {
		return bewertungInt;
	}
	
	public UserAccount getAutor() {
		return autor;
	}
	
	/**
	 * Setzt den Autor der Bewertung auf den Übergeben UserAccount
	 * 
	 * @param autor
	 * 			der Autor der Bewertung als UserAccount
	 */
	public void setAutor(UserAccount autor) {
		Assert.notNull(autor, "autor darf nicht null sein!");
		this.autor = autor;
	}
	
	@Override
	public String toString() {
		return text;
	}
}