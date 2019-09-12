/*
 * Copyright 2014.2015 the original author or authors.
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
package artistenverein;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.salespointframework.EnableSalespoint;
import org.salespointframework.SalespointSecurityConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import artistenverein.Lagerverwaltung.ControllerArtikelKatalog;

/**
 * Diese Klasse übernimmt die Konfiguration der Anwendung.
 */
@EnableSalespoint
@EnableScheduling
public class Application {

	/**
	 * Die Startfunktion der Anwendung.
	 *
	 * @param args
	 * 	Kommandozeilen Optionen
	 */
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	/**
	 * Diese Klasse übernimmt die Websicherheit der Anwendung.
	 */
	@Configuration
	static class WebSecurityConfiguration extends SalespointSecurityConfiguration {

        /**
         * Diese Funktion konfiguriert die http Sicherheit.
         *
         * @param http
         * @throws Exception
         */
		@Override
		protected void configure(HttpSecurity http) throws Exception {

			http.csrf().disable();

			http.authorizeRequests().antMatchers("/**").permitAll().and().formLogin().loginProcessingUrl("/login").and()
					.logout().logoutUrl("/logout").logoutSuccessUrl("/");
		}
	}

    /**
     * Diese Klasse übernimmt die Ressourcenverwaltung der Anwendung.
     */
	@Configuration
	public class StaticResourceConfiguration extends WebMvcConfigurerAdapter {

        /**
         * Diese Funktion fügt einen Handler für die Ressourcenverwaltung hinzu.
         *
         * @param registry
         */
	    @Override
	    public void addResourceHandlers(ResourceHandlerRegistry registry) {
	    	URL pfadURL = Application.class.getResource("Application.class");
			URI pfadURI;
			String pfad = "";
			try {
				pfadURI = ControllerArtikelKatalog.class.getProtectionDomain().getCodeSource().getLocation().toURI();
				if (pfadURL.toString().startsWith("jar:")) {
					pfad = pfadURI.toString();
					int index = pfad.lastIndexOf('/');
				    pfad = pfad.substring(0,index);
					pfad = pfad.concat("/img/");
				} else {
					pfad = pfadURI.toString();
					pfad = pfad.replace("target/classes/", "");
					pfad = pfad.concat("img/");
				}
				registry.addResourceHandler("/img/**").addResourceLocations(pfad);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} 
	    } 
	}
}
