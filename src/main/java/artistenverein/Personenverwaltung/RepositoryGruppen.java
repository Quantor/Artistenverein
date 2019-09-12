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

import org.springframework.data.repository.CrudRepository;

/**
 * Das Interface RepositoryGruppen ermöglicht das Speichern der Klasse
 * Artistengruppe
 * 
 */
public interface RepositoryGruppen extends CrudRepository<Artistengruppe, Long> {

	/**
	 * Gibt die Artistengruppe mit dem übergebenen Namen zurück
	 * 
	 * @param name
	 *            der zu suchende name
	 * 
	 * @return die Artistengruppe mit dem übergebenen Namen oder null
	 */
	public default Artistengruppe findGruppeByName(String name) {
		for (Artistengruppe gruppe : findAll()) {
			if (gruppe.getGruppenname().equals(name)) {
				return gruppe;
			}
		}
		return null;
	}
}
