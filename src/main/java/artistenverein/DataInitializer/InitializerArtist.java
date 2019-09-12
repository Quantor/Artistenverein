
/*
q * Copyright 2017 the original author or authors.
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
package artistenverein.DataInitializer;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;

import artistenverein.Personenverwaltung.Artistengruppe;
import artistenverein.Personenverwaltung.RepositoryGruppen;
import artistenverein.Personenverwaltung.RepositoryUser;
import artistenverein.Personenverwaltung.User;
import artistenverein.Zeitverwaltung.Haeufigkeit;
import artistenverein.Zeitverwaltung.Zeitverwaltung;

import org.salespointframework.core.DataInitializer;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Initalizes {@link Customer}s.
 * 
 * @author Oliver Gierke
 */

@Component
@Order(10)
class InitializerArtist implements DataInitializer {

	private final UserAccountManager userAccountManager;
	private final RepositoryUser userRepository;
	private final RepositoryGruppen gruppenRepository;
	private final Zeitverwaltung zeitverwaltung;

	/**
	 * @param userAccountManager
	 * @param customerRepository
	 */
	InitializerArtist(UserAccountManager userAccountManager, RepositoryUser userRepository,
			RepositoryGruppen gruppenRepository, Zeitverwaltung zeitverwaltung) {

		Assert.notNull(userAccountManager, "UserAccountManager must not be null!");

		this.gruppenRepository = gruppenRepository;
		this.userAccountManager = userAccountManager;
		this.userRepository = userRepository;
		this.zeitverwaltung = zeitverwaltung;
	}

	@Override
	public void initialize() {

		if (userAccountManager.findByUsername("boss").isPresent()) {
			return;
		}

		UserAccount bossAccount = userAccountManager.create("boss", "123", Role.of("ROLE_BOSS"));
		userAccountManager.save(bossAccount);

		Role customerRole = Role.of("ROLE_ARTIST");

		UserAccount ua1 = userAccountManager.create("genji", "123", customerRole);
		ua1.setEmail("NeedHealing@gmail.de");
		ua1.setLastname("Shimada");
		ua1.setFirstname("Genji");

		UserAccount ua2 = userAccountManager.create("lucio", "123", customerRole);
		ua2.setEmail("boostio@web.de");
		ua2.setLastname("Correia ");
		ua2.setFirstname("Lúcio");

		UserAccount ua3 = userAccountManager.create("beepBop", "123", customerRole);
		ua3.setEmail("playOfTheGame@gmail.de");
		ua3.setLastname("Bastion");
		ua3.setFirstname("Sepp");

		UserAccount ua4 = userAccountManager.create("cowboy", "123", customerRole);
		ua4.setEmail("highN00n@gmail.com");
		ua4.setLastname("McCree");
		ua4.setFirstname("Jesse");

		UserAccount ua5 = userAccountManager.create("mercy", "123", customerRole);
		ua5.setEmail("guardianAngel@web.de");
		ua5.setLastname("Ziegler");
		ua5.setFirstname("Angela");

		UserAccount ua6 = userAccountManager.create("ananas", "123", customerRole);
		ua6.setEmail("NanoGranny@gmail.de");
		ua6.setLastname("Amari");
		ua6.setFirstname("Ana ");

		UserAccount ua7 = userAccountManager.create("mecha", "123", customerRole);
		ua7.setEmail("progamer@gmail.de");
		ua7.setLastname("Song");
		ua7.setFirstname("Hana ");

		UserAccount ua8 = userAccountManager.create("fatguy", "123", customerRole);
		ua8.setEmail("wholehog@freemail.de");
		ua8.setLastname("Rutledge");
		ua8.setFirstname("Mako ");

		UserAccount ua9 = userAccountManager.create("MrRobot", "123", customerRole);
		ua9.setEmail("MrRobot@evilcorp.de");
		ua9.setLastname("Zenyatta");
		ua9.setFirstname("Tekhartha");

		UserAccount ua10 = userAccountManager.create("doom", "123", customerRole);
		ua10.setEmail("doom@gmail.de");
		ua10.setLastname("Ogundimu");
		ua10.setFirstname("Akande ");

		User c1 = new User(ua1);
		User c2 = new User(ua2);
		User c3 = new User(ua3);
		User c4 = new User(ua4);
		User c5 = new User(ua5);
		User c6 = new User(ua6);
		User c7 = new User(ua7);
		User c8 = new User(ua8);
		User c9 = new User(ua9);
		User c10 = new User(ua10);

		c1.setBeschreibung("Geübter Schwertschlucker");
		c1.setAdress("Walgerweg 2 01277 Dresden");
		zeitverwaltung.sperrzeitEintragen(LocalDateTime.of(2017, 12, 24, 12, 0), Duration.ofHours(12),
				Haeufigkeit.EINMAL, "Weihnachten", "Da will man ja mal frei haben, ne?!", ua1);
		c2.setBeschreibung("Musik und Tanz sind sein Fachgebiet");
		c2.setAdress("Bachstraße 1 01274 Dresden");
		zeitverwaltung.sperrzeitEintragen(LocalDateTime.of(2017, 12, 24, 12, 0), Duration.ofHours(12),
				Haeufigkeit.EINMAL, "Weihnachten", "Da will man ja mal frei haben, ne?!", ua2);
		c3.setBeschreibung("Ein Roboter und Meister der Verwandlung");
		c3.setAdress("Holunderbach 9 01275 Dresden");
		c4.setBeschreibung("Cowboy und schnellster Schütze im Land");
		c4.setAdress("Funzelweg 12 01269 Dresden");
		c5.setBeschreibung("Akrobatin mit heilenden Händen");
		c5.setAdress("Falsche Adresse!");
		c6.setBeschreibung("Alte Dame und Hellseherin ");
		c6.setAdress("Hofstraße 19 01275 Dresden");
		c7.setBeschreibung("Koreanische Diva und Experting für Akrobatik");
		c7.setAdress("Giechstraße 12 01275 Dresden");
		c8.setBeschreibung("Magen aus Eisen und genug Platz um ein ganzes Wildschwein zu verschlingen");
		c8.setAdress("Bermanstraße 11 01277 Dresden");
		c9.setBeschreibung("Experte für den Robot Dance");
		c9.setAdress("Dorfstraße 1 01273 Dresden");
		c10.setBeschreibung("Mit seiner Faust zertrümmert er selbst Beton");
		c10.setAdress("Badstraße 2 01275 Dresden");

		userRepository.save(Arrays.asList(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10));

		customerRole = Role.of("ROLE_CUSTOMER");

		UserAccount u1 = userAccountManager.create("hans", "123", customerRole);
		u1.setEmail("hans@web.de");
		u1.setFirstname("Hans");
		u1.setLastname("Schneider");
		
		UserAccount u2 = userAccountManager.create("dextermorgan", "123", customerRole);
		u2.setEmail("dextermorgan@web.de");
		u2.setFirstname("Dexter");
		u2.setLastname("Morgan");
		
		UserAccount u3 = userAccountManager.create("earlhickey", "123", customerRole);
		u3.setEmail("earlybird@web.de");
		u3.setFirstname("Earl");
		u3.setLastname("Hickey");
		

		UserAccount u4 = userAccountManager.create("mclovinfogell", "123", customerRole);
		u4.setEmail("partyanimal@web.de");
		u4.setFirstname("Mc");
		u4.setLastname("Lovin");
		
		
		User k1 = new User(u1);
		User k2 = new User(u2);
		User k3 = new User(u3);
		User k4 = new User(u4);
		
		k1.setAdress("Dorfstraße 1 01273 Dresden");
		k2.setAdress("Hallenweg 5 01276 Dresden");
		k3.setAdress("Gutstraße 15 01275 Dresden");
		k4.setAdress("Kögerweg 12 01269 Dresden");
		
		

		userRepository.save(Arrays.asList(k1, k2, k3, k4));

		Artistengruppe gruppe1 = new Artistengruppe("Tanzbären");
		Artistengruppe gruppe2 = new Artistengruppe("Feuerspucker");
		Artistengruppe gruppe3 = new Artistengruppe("Jungler");
		Artistengruppe gruppe4 = new Artistengruppe("Feeder");

		gruppenRepository.save(Arrays.asList(gruppe1, gruppe2, gruppe3, gruppe4));
		gruppe1.addMitglied(c1);
		gruppe1.addMitglied(c2);
		gruppe1.addMitglied(c3);
		gruppe1.addMitglied(c4);
		gruppe1.addMitglied(c5);

		gruppe2.addMitglied(c1);
		gruppe2.addMitglied(c8);
		gruppe2.addMitglied(c10);

		gruppe3.addMitglied(c9);

		gruppe4.addMitglied(c1);
		gruppe4.addMitglied(c6);
		gruppe4.addMitglied(c3);
		gruppe4.addMitglied(c10);

	}
}
