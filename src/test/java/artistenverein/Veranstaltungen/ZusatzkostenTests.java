package artistenverein.Veranstaltungen;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import artistenverein.AbstractIntegrationTests;
import artistenverein.Veranstaltungen.Zusatzkosten;

public class ZusatzkostenTests extends AbstractIntegrationTests {
	
	private Zusatzkosten testZk;
	private double testZkValue = 20;
	
	@Before
	public void setUp() {
		testZk = new Zusatzkosten(testZkValue);
	}
	
	@Test
	public void setZusatzkostenTest() {
		testZk.setZusatzKostenWert(10);
		double testValue = 10;
		assertEquals(String.valueOf(testValue), String.valueOf(testZk.getZusatzKostenWert()));
	}
	
	@Test
	public void toStringTest() {
		assertEquals(String.valueOf(testZkValue), String.valueOf(testZk.getZusatzKostenWert()));
	}
}
