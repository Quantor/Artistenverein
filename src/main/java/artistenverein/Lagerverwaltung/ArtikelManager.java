package artistenverein.Lagerverwaltung;

import static org.salespointframework.core.Currencies.EURO;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

import org.javamoney.moneta.Money;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.quantity.Quantity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * Die Klasse ArtikelManager dient zum Erstellen, Bearbeiten und Löschen von
 * Artikeln im Artikelkatalog
 * 
 * @author Emanuel Kern
 */
@Service
public class ArtikelManager {

	private final ArtikelKatalog katalog;
	private final Inventory<InventoryItem> inventar;

	/**
	 * Erstellt einen neuen Controller mit dem gegebenen Inventar
	 *
	 * @param katalog
	 *            der ArtikelKatalog des Shops, normalerweise von Spring automatisch
	 *            verknüpft
	 * @param inventar
	 *            das Salespoint-Inventory des Shops, normalerweise von Spring
	 *            automatisch verknüpft
	 */
	@Autowired
	public ArtikelManager(ArtikelKatalog katalog, Inventory<InventoryItem> inventar) {

		Assert.notNull(katalog, "katalog darf nicht null sein!");
		Assert.notNull(inventar, "inventar darf nicht null sein!");

		this.katalog = katalog;
		this.inventar = inventar;
	}

	/**
	 * erstellt einen neuen Artikel und fügt diesen dem Katalog und dem Inventar
	 * (mit Menge 0) hinzu
	 *
	 * @param formular
	 *            ein (valides) ArtikelErstellFormular das die Eigenschaften des
	 *            Artikels enthält
	 * @return der erstellte Artikel
	 */
	public Artikel erstelleArtikel(ArtikelErstellFormular formular) {

		Assert.notNull(formular, "formular darf nicht null sein!");

		String bild = formular.getBild().getOriginalFilename();
		String[] tokens = bild.split("\\.(?=[^\\.]+$)");

		Path pfad = getImgDir().resolve(bild);
		try {
			Files.copy(formular.getBild().getInputStream(), pfad, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			tokens[0] = "noimage";
			e.printStackTrace();
		}

		Artikel artikel = new Artikel(formular.getName(), tokens[0],
				Money.of(new BigDecimal(formular.getPreis()), EURO), formular.getBeschreibung());
		InventoryItem inventoryItem = new InventoryItem(artikel, Quantity.of(0));
		inventar.save(inventoryItem);
		return katalog.save(artikel);

	}

	/**
	 * bearbeitet den übergebenen Artikel
	 * 
	 * @param artikel
	 *            der zu bearbeitende Artikel
	 * @param formular
	 *            ein (valides) ArtikelErstellFormular das die neuen Eigenschaften
	 *            des Artikels enthält
	 * @return der bearbeitete Artikel
	 */
	public Artikel bearbeiteArtikel(Artikel artikel, ArtikelErstellFormular formular) {

		Assert.notNull(formular, "formular darf nicht null sein!");
		Assert.notNull(artikel, "artikel darf nicht null sein!");
		
		if (!(formular.getBild().getOriginalFilename().isEmpty())) {
			String bild = formular.getBild().getOriginalFilename();
			String[] tokens = bild.split("\\.(?=[^\\.]+$)");

			Path pfad = getImgDir().resolve(Paths.get(artikel.getBild() + ".jpg"));

			try {
				Files.deleteIfExists(pfad);
			} catch (IOException e) {
				e.printStackTrace();
			}

			artikel.setBild(tokens[0]);
			pfad = getImgDir().resolve(bild);
			try {
				Files.copy(formular.getBild().getInputStream(), pfad, StandardCopyOption.REPLACE_EXISTING);
				artikel.setBild(tokens[0]);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		artikel.setName(formular.getName());
		artikel.setPrice(Money.of(new BigDecimal(formular.getPreis()), EURO));
		artikel.setBeschreibung(formular.getBeschreibung());
		return katalog.save(artikel);
	}

	/**
	 * löscht den übergebenen Artikel aus Katalog und Inventar
	 * 
	 * @param artikel
	 *            der zu löschende Artikel
	 */
	public void loesche(Artikel artikel) {

		Assert.notNull(artikel, "artikel darf nicht null sein!");
		
//		Path pfad = getImgDir().resolve(Paths.get(artikel.getBild() + ".jpg"));
//
//		try {
//			Files.deleteIfExists(pfad);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		Optional<InventoryItem> invItem = inventar.findByProduct(artikel);
		invItem.ifPresent(item -> inventar.delete(item));

		artikel.setGelisted(false);
		katalog.save(artikel);
	}

	/**
	 * Gibt den Pfad zum Speicherverzeichnis der Bilder als Path-Objekt zurück
	 * 
	 * @return ein Objekt vom typ Path, das das Speicherverzeichnis des
	 *         Bilderordners enthält
	 */
	private Path getImgDir() {
		URI ordner = null;
		String pfad = "";
		try {
			URI pfadURI = ControllerArtikelKatalog.class.getProtectionDomain().getCodeSource().getLocation().toURI();
			if (wirdAlsJarAusgefuehrt()) {
				pfad = pfadURI.toString();
				int index = pfad.lastIndexOf('/');
				pfad = pfad.substring(0, index);
				pfad = pfad.concat("/img/");
				ordner = new URI(pfad);
			} else {
				pfad = pfadURI.toString();
				pfad = pfad.replace("target/classes/", "");
				pfad = pfad.concat("img/");
				ordner = new URI(pfad);
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		return Paths.get(ordner);
	}

	/**
	 * Bestimmt, ob das Programm in einer Jar ausgeführt wird
	 * 
	 * @return true, wenn das Programm in einer Jar ausgeführt wird, sonst false
	 */
	private boolean wirdAlsJarAusgefuehrt() {
		URL pfadURL = ArtikelManager.class.getResource("ArtikelManager.class");
		if (pfadURL.toString().startsWith("jar:")) {
			return true;
		}
		return false;
	}
}
