package artistenverein.Lagerverwaltung;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Random;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;

import artistenverein.AbstractIntegrationTests;

public class ArtikelErstellFormularTests extends AbstractIntegrationTests {
	
	private final ArtikelErstellFormular testFormular = new ArtikelErstellFormular();
	
	private static MockMultipartFile mockFile;
	
	@BeforeClass
	public static void setUp() {
		byte[] b = new byte[20];
		Random rand = new Random();
		rand.nextBytes(b);
		mockFile = new MockMultipartFile("formular.jpg", b);
	}
	
	@Test
	public void setNameTest() {
		try {
			testFormular.setName(null);
			fail("ArtikelErstellFormular.setName() sollte keinen Nullpointer akzeptieren");
		} catch (IllegalArgumentException e) {
			assertEquals("name darf nicht null sein!", e.getMessage());
		}
		testFormular.setName("Name");
		assertEquals("Name", testFormular.getName());
	}
	
	@Test
	public void setPreisTest() {
		try {
			testFormular.setPreis(null);
			fail("ArtikelErstellFormular.setPreis() sollte keinen Nullpointer akzeptieren");
		} catch (IllegalArgumentException e) {
			assertEquals("preis darf nicht null sein!", e.getMessage());
		}
		testFormular.setPreis("1,99");
		assertEquals("1,99", testFormular.getPreis());
	}
	
	@Test
	public void setBildTest() {
		try {
			testFormular.setBild(null);
			fail("ArtikelErstellFormular.setBild() sollte keinen Nullpointer akzeptieren");
		} catch (IllegalArgumentException e) {
			assertEquals("bild darf nicht null sein!", e.getMessage());
		}
		testFormular.setBild(mockFile);
		assertEquals(mockFile, testFormular.getBild());
	}
	
	@Test
	public void setBeschreibungTest() {
		try {
			testFormular.setBeschreibung(null);
			fail("ArtikelErstellFormular.setBeschreibung() sollte keinen Nullpointer akzeptieren");
		} catch (IllegalArgumentException e) {
			assertEquals("beschreibung darf nicht null sein!", e.getMessage());
		}
		testFormular.setBeschreibung("Beschreibung");
		assertEquals("Beschreibung", testFormular.getBeschreibung());
	}
}