package artistenverein.Zeitverwaltung;

import static artistenverein.Zeitverwaltung.Haeufigkeit.EINMAL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.time.Duration;
import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Test;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;

import artistenverein.AbstractIntegrationTests;

public class ArtistSperrZeitTests extends AbstractIntegrationTests {
	
	@Autowired
	private UserAccountManager accounts;
	private ArtistSperrZeit testZeit1;
	private LocalDateTime TestZeitpunkt1;
	private Duration TestDauer1;
	private UserAccount genji;
	private UserAccount lucio;

	@Before
	public void setUp() {
		genji = accounts.findByUsername("genji").get();
		lucio = accounts.findByUsername("lucio").get();
		TestZeitpunkt1 = LocalDateTime.of(2017, 1, 1, 12, 0);
		TestDauer1 = Duration.ofHours(1);
		testZeit1 = new ArtistSperrZeit(TestZeitpunkt1, TestDauer1, EINMAL, "Name1", "Kommentar1", genji);
	}

	@Test
	public void ArtistSperrZeitErstellTest() {
		try {
			ArtistSperrZeit z = new ArtistSperrZeit(TestZeitpunkt1, TestDauer1, EINMAL, null);
			z.getClass();
			fail("ArtistSperrZeit.ArtistSperrZeit() sollte eine IllegalArgumentException werfen, wenn das Argument artist null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "artist darf nicht null sein!");
		}
		
		try {
			ArtistSperrZeit z = new ArtistSperrZeit(TestZeitpunkt1, TestDauer1, EINMAL, "name", "kommentar", null);
			z.getClass();
			fail("ArtistSperrZeit.ArtistSperrZeit() sollte eine IllegalArgumentException werfen, wenn das Argument artist null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "artist darf nicht null sein!");
		}
		testZeit1 = new ArtistSperrZeit(TestZeitpunkt1, TestDauer1, EINMAL, genji);
		assertEquals("ArtistSperrZeit.setArtist() sollte das Argument korrekt übernehmen", genji, testZeit1.getArtist());
		testZeit1 = new ArtistSperrZeit(TestZeitpunkt1, TestDauer1, EINMAL, "Name1", "Kommentar1", genji);
		assertEquals("ArtistSperrZeit.setArtist() sollte das Argument korrekt übernehmen", genji, testZeit1.getArtist());
	}
	
	@Test
	public void setArtistTest() {
		try {
			testZeit1.setArtist(null);
			fail("ArtistSperrZeit.setArtist() sollte eine IllegalArgumentException werfen, wenn das Argument artist null ist!");
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "artist darf nicht null sein!");
		}
		
		testZeit1.setArtist(lucio);
		assertEquals("ArtistSperrZeit.setArtist() sollte das Argument korrekt übernehmen", lucio, testZeit1.getArtist());
	}
}
