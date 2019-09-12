package artistenverein.Highlights;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import artistenverein.AbstractIntegrationTests;
import artistenverein.Highlights.ControllerHighlights;

public class ControllerHiglightsTests extends AbstractIntegrationTests {

	@Autowired
	private ControllerHighlights con;

	@Test
	public void index() {

		Model model = new ExtendedModelMap();
		assertEquals("index", con.index(model));

	}

}
