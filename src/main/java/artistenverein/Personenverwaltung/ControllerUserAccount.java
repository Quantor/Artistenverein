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
package artistenverein.Personenverwaltung;

import javax.validation.Valid;

import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Tizian Fischer
 * @version 1.0
 */
@Controller
public class ControllerUserAccount {
	
	private final ManagerUser managerUser;

	/**
	 * Konstruktor
	 *
	 **/
	@Autowired
	ControllerUserAccount(ManagerUser artistManagement) {

		Assert.notNull(artistManagement, "artistManagement must not be null!");

		this.managerUser = artistManagement;
	}
	/**
	 * Übersicht über Benutzerdaten
	 * @param userAccount
	 * @param model
	 * @return
	 */
	@RequestMapping("user/userpage")
	public String detail(@LoggedIn UserAccount userAccount, Model model) {
		model.addAttribute("user", managerUser.findeUserAccount(userAccount));
		return "/User/userpage";
	}

	/**
	 * Bearbeiten von Benutzerdaten durch Benutzer selbst
	 * Typ:Get
	 * @param userAccount
	 * @param model
	 * @return
	 */
	@GetMapping("user/bearbeiten")
	public String getProfilBearbeiten(@LoggedIn UserAccount userAccount, Model model) {
		model.addAttribute("user", managerUser.findeUserAccount(userAccount));
		model.addAttribute("userForm", new FormUserValidation());

		return "User/bearbeiten";
	}

	/**
	 * Bearbeiten von Benutzerdaten durch Benutzer selbst
	 * Typ:Post
	 * @param userForm
	 * @param result
	 * @return
	 */
	@PostMapping("user/bearbeiten")
	public String postProfilBearbeiten(@ModelAttribute("userForm") @Valid FormUserValidation userForm,
			BindingResult result) {

		if (result.hasErrors()) {
			return "user/bearbeiten";
		}
		managerUser.editArtist(userForm);
		return "index";
	}
	
	/**
	 * Registrierung eines User durch User selbst
	 * @param model
	 * @return
	 */
	@GetMapping("/registration")
	public String getUserErstellen(Model model) {
		model.addAttribute("userForm", new FormUserValidation());
		return "User/registration";
	}
	
	
	
	/**
	 * Registrierung eines User durch User selbst
	 * @param userForm
	 * @param result
	 * @return
	 */
	@PostMapping("/registration")
	public String postKundeErstellen(@ModelAttribute("userForm") @Valid FormUserValidation userForm,
			BindingResult result) {

		
		
		if (result.hasErrors()) {
			return "User/registration";
		}
		
		if (!managerUser.createUser(userForm)) {
			
			result.addError(new FieldError(result.getObjectName(),"username","Username existiert bereits"));
			return "User/registration";
		}
		

		return "index";
	}

}
