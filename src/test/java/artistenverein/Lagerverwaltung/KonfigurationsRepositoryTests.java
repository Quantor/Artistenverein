package artistenverein.Lagerverwaltung;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import artistenverein.AbstractIntegrationTests;

public class KonfigurationsRepositoryTests extends AbstractIntegrationTests {

	@Autowired
	private KonfigurationsRepository konfRep;

	private static InventarKonfiguration invKonfMindestMenge;
	private static InventarKonfiguration invKonfRabatt;
	private static final int DEFAULT_MENGE = 10;
	private static final int DEFAULT_RABATT = 20;
	
	@Before
	public void setUp() {
		invKonfMindestMenge = new InventarKonfiguration(DEFAULT_MENGE, InventarKonfiguration.KonfigurationsTyp.MINDEST_MENGE);
		invKonfRabatt = new InventarKonfiguration(DEFAULT_RABATT, InventarKonfiguration.KonfigurationsTyp.RABATT);
		konfRep.deleteAll();
	}

	@Test
	public void enthaeltRabattTest() {
		assertFalse(
				"KonfigurationsRepository.enthaeltRabatt() sollte false zurückgeben, wenn das Repo keine RabattKonfiguration enthält!",
				konfRep.enthaeltRabatt());
		konfRep.save(invKonfMindestMenge);
		assertFalse(
				"KonfigurationsRepository.enthaeltRabatt() sollte false zurückgeben, wenn das Repo keine RabattKonfiguration enthält!",
				konfRep.enthaeltRabatt());
		konfRep.save(invKonfRabatt);
		assertTrue(
				"KonfigurationsRepository.enthaeltRabatt() sollte true zurückgeben, wenn das Repo eine RabattKonfiguration enthält!",
				konfRep.enthaeltRabatt());
	}

	@Test
	public void enthaeltMindestMengeTest() {
		assertFalse(
				"KonfigurationsRepository.enthaeltMindestMenge() sollte false zurückgeben, wenn das Repo keine MindestMengeKonfiguration enthält!",
				konfRep.enthaeltMindestMenge());
		konfRep.save(invKonfRabatt);
		assertFalse(
				"KonfigurationsRepository.enthaeltMindestMenge() sollte false zurückgeben, wenn das Repo keine MindestMengeKonfiguration enthält!",
				konfRep.enthaeltMindestMenge());
		konfRep.save(invKonfMindestMenge);
		assertTrue(
				"KonfigurationsRepository.enthaeltMindestMenge() sollte true zurückgeben, wenn das Repo eine MindestMengeKonfiguration enthält!",
				konfRep.enthaeltMindestMenge());
	}

	@Test
	public void getMindestMengeTest() {
		assertNull(
				"KonfigurationsRepository.getMindestMenge() sollte null zurückgeben, wenn kein entsprechender Wert vorhanden ist",
				konfRep.getMindestMenge());
		konfRep.save(invKonfRabatt);
		konfRep.save(invKonfMindestMenge);
		assertEquals(
				"KonfigurationsRepository.getMindestMenge() sollte den entsprechenden Wert zurückgeben, wenn er vorhanden ist",
				invKonfMindestMenge, konfRep.getMindestMenge());
	}

	@Test
	public void getRabattTest() {
		assertNull(
				"KonfigurationsRepository.getRabatt() sollte null zurückgeben, wenn kein entsprechender Wert vorhanden ist",
				konfRep.getRabatt());
		konfRep.save(invKonfRabatt);
		konfRep.save(invKonfMindestMenge);
		assertEquals(
				"KonfigurationsRepository.getRabatt() sollte den entsprechenden Wert zurückgeben, wenn er vorhanden ist",
				invKonfRabatt, konfRep.getRabatt());

	}

	@After
	public void cleanUp() {
		invKonfMindestMenge = new InventarKonfiguration(DEFAULT_MENGE, InventarKonfiguration.KonfigurationsTyp.MINDEST_MENGE);
		invKonfRabatt = new InventarKonfiguration(DEFAULT_RABATT, InventarKonfiguration.KonfigurationsTyp.RABATT);
		konfRep.deleteAll();
		konfRep.save(invKonfRabatt);
		konfRep.save(invKonfMindestMenge);
	}

}
