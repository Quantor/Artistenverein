
package artistenverein.DataInitializer;

import org.apache.commons.io.FileUtils;
import org.javamoney.moneta.Money;
import org.salespointframework.core.DataInitializer;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.quantity.Quantity;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import artistenverein.Lagerverwaltung.Artikel;
import artistenverein.Lagerverwaltung.ArtikelKatalog;
import artistenverein.Lagerverwaltung.ControllerArtikelKatalog;

import static org.salespointframework.core.Currencies.EURO;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Component
// @Order(20)
class InitializerArtikel implements DataInitializer {

	private final ArtikelKatalog artikelKatalog;
	private final Inventory<InventoryItem> inventory;

	InitializerArtikel(ArtikelKatalog artikelKatalog, Inventory<InventoryItem> inventory) {

		Assert.notNull(artikelKatalog, "ItemCatalog must not be null!");
		Assert.notNull(inventory, "Inventory must not be null!");

		this.artikelKatalog = artikelKatalog;
		this.inventory = inventory;
	}

	@Override
	public void initialize() {

		if (initialisiereImgOrdner() || getImgDir().listFiles().length == 0) {
			try {
				kopiereBilder();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (artikelKatalog.findAll().iterator().hasNext()) {
			return;
		}

		artikelKatalog.save(new Artikel("Fackel", "fac", Money.of(9.99, EURO),
				"Einfache Fackel, zum Jonglieren geeignet oder um Leute und Dinge anzuzünden"));
		artikelKatalog.save(new Artikel("Pois", "poi", Money.of(14.99, EURO), "Feuer-Poi"));
		artikelKatalog.save(new Artikel("Diabolo", "dia", Money.of(12.99, EURO), "Brennendes Jongliergerät"));
		artikelKatalog.save(new Artikel("Blaues Licht", "bla", Money.of(19.82, EURO), "Es leuchtet blau."));
		artikelKatalog.save(new Artikel("Jonglier Hut", "hut", Money.of(69.99, EURO),
				"Perfekt zum jonglieren und gleichzeitig modisch - Zumindest sagt das meine Mutter "));
		artikelKatalog
				.save(new Artikel("Bälle", "ball", Money.of(3.99, EURO), "Bälle. Sie machen was Bälle eben so tuen "));

		for (Artikel artikel : artikelKatalog.findAll()) {
			InventoryItem inventoryItem = new InventoryItem(artikel, Quantity.of(10));
			inventory.save(inventoryItem);
		}

	}

	private boolean initialisiereImgOrdner() {
		File ordner = getImgDir();
		try {
			if (ordner.mkdir()) {
				System.out.println("Directory Created");
				return true;
			} else {
				System.out.println("Directory is not created");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private void kopiereBilder() throws IOException, URISyntaxException {
		File ziel = getImgDir();
		if (wirdAlsJarAusgefuehrt()) {
			String[] webimages = getImagesInJar();
			for (String image : webimages) {
				String imageName = image.substring(image.lastIndexOf("/") + 1);
				File dest = new File(ziel.getPath().concat("/" + imageName));
				URL inputUrl = getClass().getResource("/" + image);
				FileUtils.copyURLToFile(inputUrl, dest);
			}

		} else {
			Path pfad = Paths.get("static", "resources", "img");
			pfad = Paths.get(ControllerArtikelKatalog.class.getProtectionDomain().getCodeSource().getLocation().toURI())
					.resolve(pfad);
			File quelle = new File(pfad.toUri());
			System.out.println(quelle.getAbsolutePath());
			try {
				FileUtils.copyDirectory(quelle, ziel);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean wirdAlsJarAusgefuehrt() {
		URL pfadURL = InitializerArtikel.class.getResource("InitializerArtikel.class");
		if (pfadURL.toString().startsWith("jar:")) {
			return true;
		}
		return false;
	}

	private File getImgDir() {
		File ordner = null;
		String pfad = "";
		try {
			URI pfadURI = ControllerArtikelKatalog.class.getProtectionDomain().getCodeSource().getLocation().toURI();
			if (wirdAlsJarAusgefuehrt()) {
				pfad = pfadURI.toString();
				int index = pfad.lastIndexOf('/');
				pfad = pfad.substring(0, index);
				pfad = pfad.concat("/img/");
				ordner = new File(new URI(pfad));
			} else {
				pfad = pfadURI.toString();
				pfad = pfad.replace("target/classes/", "");
				pfad = pfad.concat("img/");
				ordner = new File(new URI(pfad));
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		return ordner;
	}

	private String[] getImagesInJar() throws IOException {
		CodeSource src = InitializerArtikel.class.getProtectionDomain().getCodeSource();
		List<String> list = new ArrayList<String>();

		if (src != null) {
			URL jar = src.getLocation();
			ZipInputStream zip = new ZipInputStream(jar.openStream());
			ZipEntry ze = null;

			while ((ze = zip.getNextEntry()) != null) {
				String entryName = ze.getName();
				if (entryName.startsWith("static/resources/img/") && entryName.contains(".")) {
					list.add(entryName);
				}
			}
		}

		return list.toArray(new String[list.size()]);
	}

}
