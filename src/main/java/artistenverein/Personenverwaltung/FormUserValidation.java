/*
 * Copyright 2013-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package artistenverein.Personenverwaltung;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.util.Assert;

/**
 * Eine Validation Form die Eingaben beim Erstellen und Bearbeiten eines Users überprüft
 * @author Tizian Fischer
 * @version 1.0
 */
public class FormUserValidation {

	@NotEmpty(message= "Username darf nicht leer sein!")
	private String username;
	
	@NotEmpty
	private String password;
	
	@NotEmpty
	private String firstname;
	
	@NotEmpty
	private String lastname;
	
	@Email
	private String email;
	
	private String adress;
	
	private String beschreibung;
	
	private String telefon;
	
	/**
	 * gibt den Username im Formular zurück
	 * 
	 * @return username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * dient zum setzen des Username im Formular
	 * 
	 * @param username
	 *            der zu setzende Username
	 */
	public void setUsername(String username) {
		Assert.notNull(username, "Username darf nicht Null sein!");
		Assert.isTrue(!username.matches(""), "Username darf nicht leer sein!");
		this.username = username;
	}
	
	/**
	 * gibt die Adresse im Formular zurück
	 * 
	 * @return Adresse
	 */
	public String getAdress() {
		
		return adress;
	}

	/**
	 * dient zum setzen der Adresse im Formular
	 * 
	 * @param adress
	 *            die zu setzende Adresse
	 */
	public void setAdress(String adress) {
		this.adress = adress;
	}
	
	/**
	 * gibt die Beschreibung im Formular zurück
	 * 
	 * @return beschreibung
	 */
	public String getBeschreibung() {
		return beschreibung;
	}

	/**
	 * dient zum setzen der beschreibung im Formular
	 * 
	 * @param beschreibung
	 *            die zu setzende beschreibung
	 */
	public void setBeschreibung(String beschreibung) {
		this.beschreibung = beschreibung;
	}
	
	/**
	 * gibt das Passwort im Formular zurück
	 * 
	 * @return passwort
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * dient zum setzen des Passwort im Formular
	 * 
	 * @param password
	 *            das zu setzende Passwort
	 */
	public void setPassword(String password) {
		Assert.notNull(password, "Passwort darf nicht Null sein!");
		Assert.isTrue(!password.matches(""), "Passwort darf nicht leer sein!");
		this.password = password;
	}

	/**
	 * gibt den Vorname im Formular zurück
	 * 
	 * @return vorname
	 */
	public String getFirstname() {
		return firstname;
	}

	/**
	 * dient zum setzen des Vornamen im Formular
	 * 
	 * @param firstname
	 *            der zu setzende Vorname
	 */
	public void setFirstname(String firstname) {
		Assert.notNull(firstname, "Vorname darf nicht Null sein!");
		Assert.isTrue(!firstname.matches(""), "Vorname darf nicht leer sein!");
		this.firstname = firstname;
	}

	/**
	 * gibt den Nachnamen im Formular zurück
	 * 
	 * @return nachname
	 */
	public String getLastname() {
		return lastname;
	}

	/**
	 * dient zum setzen des Nachnamen im Formular
	 * 
	 * @param lastname
	 *            der zu setzende Nachname
	 */
	public void setLastname(String lastname) {
		Assert.notNull(lastname, "Nachname darf nicht Null sein!");
		Assert.isTrue(!lastname.matches(""), "Nachname darf nicht leer sein!");
		this.lastname = lastname;
	}

	/**
	 * gibt die Email im Formular zurück
	 * 
	 * @return email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * dient zum setzen der Email im Formular
	 * 
	 * @param email
	 *            die zu setzende Email
	 */
	public void setEmail(String email) {
		Assert.notNull(email, "Email darf nicht Null sein!");
		Assert.isTrue(!email.matches(""), "Email darf nicht leer sein!");
	
		this.email = email;
	}
	
	/**
	 * gibt die Telefonnummer im Formular zurück
	 * 
	 * @return telefonnummer
	 */
	public String getTelefon() {
		return telefon;
	}

	/**
	 * dient zum setzen der Telefonnummer im Formular
	 * 
	 * @param telefon
	 *            die zu setzende Telefonnummer
	 */
	public void setTelefon(String telefon) {
		this.telefon = telefon;
	}
	
}

