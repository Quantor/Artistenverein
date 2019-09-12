package artistenverein.Zeitverwaltung;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import artistenverein.AbstractIntegrationTests;

public class HaeufigkeitTests extends AbstractIntegrationTests {
	
	@Test
	public void haeufigkeitOfTest() {
		try {
			Haeufigkeit.of(null);
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), "haeuf darf nicht null sein!");
		}
		
		assertNotNull("Haeufigkeit.of() sollte einen Enum zurückgeben, wenn der String korrekt ist", Haeufigkeit.of("einmalig"));
		assertNotNull("Haeufigkeit.of() sollte einen Enum zurückgeben, wenn der String korrekt ist", Haeufigkeit.of("jaehrlich"));
		assertNotNull("Haeufigkeit.of() sollte einen Enum zurückgeben, wenn der String korrekt ist", Haeufigkeit.of("monatlich"));
		assertNotNull("Haeufigkeit.of() sollte einen Enum zurückgeben, wenn der String korrekt ist", Haeufigkeit.of("woechentlich"));
		assertNotNull("Haeufigkeit.of() sollte einen Enum zurückgeben, wenn der String korrekt ist", Haeufigkeit.of("taeglich"));
		assertEquals("Haeufigkeit.of() sollte einen Enum zurückgeben, wenn der String korrekt ist", Haeufigkeit.EINMAL, Haeufigkeit.of("einmalig"));
		assertEquals("Haeufigkeit.of() sollte einen Enum zurückgeben, wenn der String korrekt ist", Haeufigkeit.JAEHRLICH, Haeufigkeit.of("jaehrlich"));
		assertEquals("Haeufigkeit.of() sollte einen Enum zurückgeben, wenn der String korrekt ist", Haeufigkeit.MONATLICH, Haeufigkeit.of("monatlich"));
		assertEquals("Haeufigkeit.of() sollte einen Enum zurückgeben, wenn der String korrekt ist", Haeufigkeit.WOECHENTLICH, Haeufigkeit.of("woechentlich"));
		assertEquals("Haeufigkeit.of() sollte einen Enum zurückgeben, wenn der String korrekt ist", Haeufigkeit.TAEGLICH, Haeufigkeit.of("taeglich"));
		assertNull("Haeufigkeit.of() sollte null zurückgeben, wenn der String nicht korrekt ist", Haeufigkeit.of("einmali"));
		assertNull("Haeufigkeit.of() sollte null zurückgeben, wenn der String nicht korrekt ist", Haeufigkeit.of(""));
		assertNull("Haeufigkeit.of() sollte null zurückgeben, wenn der String nicht korrekt ist", Haeufigkeit.of("asdf"));
	}
}
