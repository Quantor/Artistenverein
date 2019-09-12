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

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.util.Assert;

/**
 * Valdierungs Formular für die Erstellung und Bearbeitung einer Gruppe
 * 
 * @author Tizian Fischer
 * @version 1.0
 *        
 */
public class FormGruppenValidation {

	@NotEmpty
	private String gruppenname;

	/**
	 * gibt den Gruppennamen im Formular zurück
	 * 
	 * @return gruppenname
	 */
	public String getGruppenname() {
		return gruppenname;
	}

	/**
	 * dient zum setzen des Gruppennamen im Formular
	 * 
	 * @param gruppenname
	 *            der zu setzende Gruppenname
	 */
	public void setGruppenname(String gruppenname) {
		Assert.notNull(gruppenname, "Gruppenname darf nicht Null sein!");
		Assert.isTrue(!gruppenname.matches(""), "Gruppenname darf nicht leer sein!");
		this.gruppenname = gruppenname;
	}

}
