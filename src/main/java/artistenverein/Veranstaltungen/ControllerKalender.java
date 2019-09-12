/*
 * Copyright 2014-2015 the original author or authors.
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
package artistenverein.Veranstaltungen;

import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Der Controller ControllerKalender ist ein Spring-Controller und zuständig für die Anfragen von
 * einem Kunden an seinen Kalender.
 *
 * @author Oliver
 *
 */
@Controller
public class ControllerKalender {

	private final BuchungRepository buchungRepository;
	private Kalender kalender = new Kalender();

	/**
	 * Erstellt einen neuen Controller mit einem Kalender.
	 * @param buchungRepository
	 * 					das Buchungsrepository des Kalenders, normalerweise von Spring
	 * 					automatisch verknüpft
	 */
	@Autowired
	ControllerKalender(BuchungRepository buchungRepository) {

		this.buchungRepository = buchungRepository;
	}

	/**
	 *
	 * @param model
	 * 			ein Objekt vom Typ Spring-Model
	 * @param monatZurueck
	 * 			String der true ist, wenn der Benutzer einen Monat zurück gehen will
	 * @param monatVor
	 * 			String der true ist, wenn der Benutzer einen Monat vor gehen will
	 * @param nutzerAccount
	 * 			Optional:der Nutzeraccount des eingeloggten Nutzers
	 * @return	ein String zur indentifizierung des Templates
	 */
	@RequestMapping("/kalender")
	public String getKalenderKunde(Model model,
			@RequestParam(value = "monatZurueck", defaultValue = "false") String monatZurueck,
			@RequestParam(value = "monatVor", defaultValue = "false") String monatVor,
			@LoggedIn Optional<UserAccount> nutzerAccount) {
		if (nutzerAccount.isPresent()) {
			UserAccount userAccount = nutzerAccount.get();

			if (monatZurueck.equals("true")) {
				kalender.setDatum(kalender.getDatum().minusMonths(1));
			}
			if (monatVor.equals("true")) {
				kalender.setDatum(kalender.getDatum().plusMonths(1));
			}

			Iterable<Buchung> buchungen = this.buchungRepository.findAll();
			List<Buchung> buchungenZuKunde = new ArrayList<Buchung>();
			for (Iterator<Buchung> it = buchungen.iterator(); it.hasNext();) {
				Buchung buchung = it.next();
				if (buchung.getKunde().getId().equals(userAccount.getId())) {
					buchungenZuKunde.add(buchung);
				}
			}

			List<Buchung> buchungenZuKundeImMonat = kalender.getBuchungenZuKundeImMonat(buchungRepository, userAccount);
			String[][] eintraege = kalender.getEintraegeFuerMonat(buchungenZuKundeImMonat);
			model.addAttribute("buchungenZuKunde", buchungenZuKunde);
			model.addAttribute("eintraege", eintraege);
			model.addAttribute("jahr", kalender.getDatum().getYear());
			model.addAttribute("monat", kalender.getDatum().getMonth().getDisplayName(TextStyle.FULL, Locale.GERMANY));

			return "/Kalender/kalender";
		}
		return "redirect:/";
	}
}
