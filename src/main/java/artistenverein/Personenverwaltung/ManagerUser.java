/*
 * Copyright 2017 the original author or authors.
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.salespointframework.core.Streamable;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import artistenverein.Veranstaltungen.Buchung;
import artistenverein.Veranstaltungen.BuchungRepository;
import artistenverein.Veranstaltungen.EntityVeranstaltung;
import artistenverein.Veranstaltungen.VeranstaltungsKatalog;

/**
 * 
 * @author Tizian Fischer
 * @version 1.0
 */

@Service
@Transactional
public class ManagerUser {

	private final RepositoryUser userRepository;
	private final RepositoryGruppen gruppenRepository;
	private final UserAccountManager userAccounts;
	private final VeranstaltungsKatalog veranstaltungen;
	private final BuchungRepository buchungen;

	/**
	 * 
	 * @param artists
	 *            Spring Autowired - Instanz des RepostioryUsers
	 * @param userAccounts
	 *            Spring Autowired - Instanz des UserAccountManager
	 * @param veranstaltungen
	 *            Spring Autowired - Instanz des VeranstaltungsKatalog
	 * @param gruppenRepository
	 *            Spring Autowired - Instanz des RepositoryGruppen
	 * @param buchungen
	 *            Spring Autowired - Instanz des BuchungRepository
	 */
	public ManagerUser(RepositoryUser artists, UserAccountManager userAccounts, VeranstaltungsKatalog veranstaltungen,
			RepositoryGruppen gruppenRepository, BuchungRepository buchungen) {

		this.userRepository = artists;
		this.userAccounts = userAccounts;
		this.veranstaltungen = veranstaltungen;
		this.gruppenRepository = gruppenRepository;
		this.buchungen = buchungen;

	}

	/**
	 * Erstellt einen Spring UserAccount
	 * 
	 * @param form
	 *            FormUSerValidation zur Validierung der Eingaben
	 * @param role
	 *            Die Rolle die dem UserAccount Zugewiesen wird
	 * @return boolean gibt True zurück wenn der Account Erfolgreich erstellt wurde
	 */
	private boolean createUserFactory(FormUserValidation form, String role) {

		if (sucheUserMitUsername(form.getUsername()) == null) {

			UserAccount userAccount = userAccounts.create(form.getUsername(), form.getPassword(), Role.of(role));
			userAccount.setEmail(form.getEmail());
			userAccount.setLastname(form.getLastname());
			userAccount.setFirstname(form.getFirstname());
			User user = new User(userAccount);
			user.setAdress(form.getAdress());
			user.setBeschreibung(form.getBeschreibung());
			user.setTelefon(form.getTelefon());
			userRepository.save(user);
			return true;
		}

		return false;

	}

	/**
	 * Erstellt eine Artistengruppe
	 * 
	 * @param form
	 *            FormGruppenValidation zur Validerung der Eingaben
	 * @return void
	 */
	public Artistengruppe createGruppe(FormGruppenValidation form) {

		return gruppenRepository.save(new Artistengruppe(form.getGruppenname()));

	}

	// Rechte BOSS einfügen
	/**
	 * Erstellt mithilfe der createUserFactory einen User
	 * 
	 * @param form
	 * @return
	 */
	public boolean createArtist(FormUserValidation form) {

		final String role = "ROLE_ARTIST";
		return createUserFactory(form, role);
	}

	/**
	 * Erstellt mithilfe der createUserFactory einen User
	 * 
	 * @param form
	 * @return
	 */
	// Rechte unregisterd einfügen
	public boolean createUser(FormUserValidation form) {

		final String role = "ROLE_CUSTOMER";
		return createUserFactory(form, role);
	}

	/**
	 * Erstellt mithilfe der createUserFactory einen User
	 * 
	 * @param form
	 * @return
	 */
	// Rechte BOSS einfügen
	public boolean createVorstand(FormUserValidation form) {

		final String role = "ROLE_BOSS";
		return createUserFactory(form, role);
	}

	/**
	 * Löscht einen User
	 * 
	 * @param form
	 * @return true wenn User erfolgreich gelöscht wurde
	 */
	public boolean deleteUser(User user) {

		for (Artistengruppe gruppe : findAllGruppen()) {
			if (gruppe.getMitglieder().contains(user)) {
				gruppe.entferneMitglied(user);
			}
		}
		userRepository.delete(user);
		return true;

	}

	/**
	 * Entfernt einen Artisten aus einer Artistengruppe
	 * 
	 * @param gruppe
	 *            Die Gruppe, aus welcher der Artiste entfernt wird
	 * @param user
	 *            Der Artist der entfernt werden soll
	 * @return gibt True zurück wenn Artist erfolgreich entfernt wurde
	 */
	public boolean removeFromGroup(Artistengruppe gruppe, User user) {
		return gruppe.entferneMitglied(user);
	}

	/**
	 * Fügt einen Artisten aus einer Artistengruppe hinzu
	 * 
	 * @param gruppe
	 *            Die Gruppe, aus welcher der Artiste hinzugefügt wird
	 * @param user
	 *            Der Artist der hinzugefügt werden soll
	 * @return gibt True zurück wenn Artist erfolgreich hinzugefügt wurde
	 */
	public boolean addToGroup(Artistengruppe gruppe, User user) {
		return gruppe.addMitglied(user);
	}

	/**
	 * Bearbeitet die Daten eines Artisten mit den Username aus der Form
	 * 
	 * @param userForm
	 * @return gibt den Bearbeiten Artisten zurück
	 */
	public User editArtist(FormUserValidation userForm) {

		User user = sucheUserMitUsername(userForm.getUsername());

		user.getUserAccount().setEmail(userForm.getEmail());
		user.getUserAccount().setLastname(userForm.getLastname());
		user.getUserAccount().setFirstname(userForm.getFirstname());
		user.setAdress(userForm.getAdress());
		user.setBeschreibung(userForm.getBeschreibung());
		user.setTelefon(userForm.getTelefon());

		return userRepository.save(user);
	}

	/**
	 * Findet den User zu einem Spring UserAccount
	 * 
	 * @param userAccount
	 *            Spring UserAccount der einem User zugrunde liegt
	 * @return den Gesuchten User
	 */
	public User findeUserAccount(UserAccount userAccount) {
		Iterator<User> users = userRepository.findAll().iterator();
		User user = null;
		while (users.hasNext()) {
			user = users.next();
			if (user.getUserAccount().getId().equals(userAccount.getId())) {
				return user;
			}

		}
		return user;

	}

	/**
	 * Gibt alle User zurück
	 * 
	 * @return
	 */
	public Streamable<User> findAllUser() {
		return Streamable.of(userRepository.findAll());
	}

	/**
	 * Gibt alle Artistengruppen zurück
	 * 
	 * @return
	 */
	public Streamable<Artistengruppe> findAllGruppen() {
		return Streamable.of(gruppenRepository.findAll());
	}

	/**
	 * Gibt alle Artisten zurück
	 * 
	 * @return
	 */
	public Streamable<User> findArtist() {
		ArrayList<User> temp = new ArrayList<User>();

		for (User user : userRepository.findAll()) {
			if (user.getUserAccount().hasRole(Role.of("ROLE_ARTIST"))) {
				temp.add(user);
			}
		}

		return Streamable.of(temp);

	}

	/**
	 * Findet einen User mit dem Namen welcher dem String gleicht
	 * 
	 * @param username
	 * @return
	 */
	public User sucheUserMitUsername(String username) {

		for (User user : findAllUser()) {

			if (user.getUserAccount().getUsername().equals(username)) {
				return user;
			}
		}

		return null;

	}

	/**
	 * Gibt die Gruppe mit dem Namen welcher dem String gleicht
	 * 
	 * @param gruppenname
	 * @return
	 */
	public Artistengruppe sucheGruppeMitGruppenname(String gruppenname) {

		for (Artistengruppe gruppe : findAllGruppen()) {

			if (gruppe.getGruppenname().equals(gruppenname)) {
				return gruppe;
			}
		}

		return null;

	}

	/**
	 * Findet alle Gruppen bei dem der User Mitglied ist
	 * 
	 * @param user
	 * @return
	 */
	public Streamable<Artistengruppe> findeGruppenZuArtist(User user) {
		ArrayList<Artistengruppe> temp = new ArrayList<Artistengruppe>();

		for (Artistengruppe gruppe : gruppenRepository.findAll()) {
			if (gruppe.getMitglieder().contains(user)) {
				temp.add(gruppe);

			}
		}

		return Streamable.of(temp);

	}

	/**
	 * Findet alle Veranstaltungen für die der User verantwortlich ist
	 * 
	 * @param user
	 * @return
	 */
	public Streamable<EntityVeranstaltung> findeVeranstaltungenZuUser(User user) {
		ArrayList<EntityVeranstaltung> temp = new ArrayList<EntityVeranstaltung>();

		for (EntityVeranstaltung veranstaltung : veranstaltungen.findAll()) {

			if (veranstaltung.getArtisten().contains(user.getUserAccount())) {
				temp.add(veranstaltung);
			}

		}

		return Streamable.of(temp);

	}

	/**
	 * Findet alle Buchungen des Kunden
	 * 
	 * @param user
	 * 	der Kunde, dessen Buchungen gesucht werden
	 * @return
	 * 	die Buchungen des Kunden
	 */
	public Streamable<Buchung> getBuchungenZuKunde(UserAccount user) {
		ArrayList<Buchung> temp = new ArrayList<Buchung>();
		if (user.hasRole(Role.of("ROLE_CUSTOMER"))) {
			for (Buchung buchung : buchungen.findAll()) {

				if (buchung.getKunde().equals(user)) {
					temp.add(buchung);
				}
			}

		}

		return Streamable.of(temp);
	}

	/**
	 * Finde Alle Buchungen für Veranstaltungen des Users
	 * 
	 * @param user
	 * @return
	 */
	public List<Buchung> findeBuchungenZuUser(User user) {
		ArrayList<Buchung> temp = new ArrayList<Buchung>();

		for (Buchung buchung : buchungen.findAll()) {
			if (buchung.getVeranstaltung().getArtisten().contains(user.getUserAccount())) {
				temp.add(buchung);
			}
		}
		return temp;

	}

	/**
	 * Findet alle Kunden die eine Veranstaltung des Users gebucht haben
	 * 
	 * @param user
	 * @return
	 */
	public Streamable<UserAccount> findeKundenZuArtist(User user) {
		Set<UserAccount> temp = new HashSet<UserAccount>();

		for (Buchung buchung : buchungen.findAll()) {

			if (buchung.getVeranstaltung().getArtisten().contains(user.getUserAccount())) {
				temp.add(buchung.getKunde());
			}
		}
		return Streamable.of(temp);

	}

	/**
	 * Findet alle Kunden
	 * 
	 * @return
	 */
	public Streamable<User> findKunde() {
		ArrayList<User> temp = new ArrayList<User>();

		for (User user : userRepository.findAll()) {
			if (user.getUserAccount().hasRole(Role.of("ROLE_CUSTOMER"))) {
				temp.add(user);
			}
		}

		return Streamable.of(temp);

	}

	/**
	 * Speichtert einen User im Repository
	 * 
	 * @param user
	 */
	public void save(User user) {
		userRepository.save(user);

	}

	/**
	 * findet alle Buchungen
	 * 
	 * @return
	 */
	public BuchungRepository getBuchungen() {
		return this.buchungen;
	}

}
