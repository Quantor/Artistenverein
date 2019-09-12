
package artistenverein.Zeitverwaltung;

import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.DayOfWeek;

import static artistenverein.Zeitverwaltung.Haeufigkeit.*;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;

import artistenverein.AbstractIntegrationTests;

/*
* Created by Ruby on 12/4/17.
*/
public class ZeitTests extends AbstractIntegrationTests {

    private static LocalDateTime TestZeitpunkt1;

    private static Duration TestDauer1;

    private static Zeit TestZeit1;
    private static Zeit TestZeit2;
    private static Zeit TestZeit3;
    private static Zeit TestZeit4;
    private static Zeit TestZeit5;

    @Before
    public void setUp() {
        TestZeitpunkt1 = LocalDateTime.of(2017, 1, 1, 12, 0);
        LocalDateTime TestZeitpunkt2 = LocalDateTime.of(2016, 1, 1, 10, 30);
        LocalDateTime TestZeitpunkt3 = LocalDateTime.of(2017, 4, 1, 11, 45);

        TestDauer1 = Duration.ofHours(1);
        Duration TestDauer2 = Duration.ofHours(5);
        Duration TestDauer3 = Duration.ofHours(10);

        TestZeit1 = new Zeit(TestZeitpunkt1, TestDauer1, EINMAL, "Name", "Kommentar");
        TestZeit2 = new Zeit(TestZeitpunkt2, TestDauer2, Haeufigkeit.WOECHENTLICH, "Name2", "Kommentar2");
        TestZeit3 = new Zeit(TestZeitpunkt3, TestDauer3, Haeufigkeit.MONATLICH, "Name3", "Kommentar3");
        TestZeit4 = new Zeit(TestZeitpunkt1, TestDauer1, Haeufigkeit.TAEGLICH, "Name", "Kommentar");
        TestZeit5 = new Zeit(TestZeitpunkt1, TestDauer1, Haeufigkeit.JAEHRLICH, "Name", "Kommentar");
    }

    @Test
    public void ZeitErstellNullPointerTest() {
        try {
            Zeit z = new Zeit(null, TestDauer1, Haeufigkeit.TAEGLICH);
            z.getClass();
            fail("Zeit.Zeit() sollte eine IllegalArgumentException werfen, wenn das Argument start null ist!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "start darf nicht null sein!");
        }

        try {
            Zeit z = new Zeit(TestZeitpunkt1, null, Haeufigkeit.TAEGLICH);
            z.getClass();
            fail("Zeit.Zeit() sollte eine IllegalArgumentException werfen, wenn das Argument dauer null ist!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "dauer darf nicht null sein!");
        }

        try {
            Zeit z = new Zeit(TestZeitpunkt1, TestDauer1, null);
            z.getClass();
            fail("Zeit.Zeit() sollte eine IllegalArgumentException werfen, wenn das Argument haeufigkeit null ist!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "haeufigkeit darf nicht null sein!");
        }

    }

    @Test
    public void konstruktorTests() {
        Zeit TestZeit = new Zeit(TestZeitpunkt1, TestDauer1, Haeufigkeit.TAEGLICH);
        assertSame(TestZeit.getStart(), TestZeitpunkt1);
        assertSame(TestZeit.getDauer(), TestDauer1);
        assertSame(TestZeit.getHaeufigkeit(), Haeufigkeit.TAEGLICH);

        TestZeit = new Zeit(TestZeitpunkt1, TestDauer1, Haeufigkeit.TAEGLICH, "Name", "Kommentar");
        assertEquals(TestZeit.getStart(), TestZeitpunkt1);
        assertEquals(TestZeit.getDauer(), TestDauer1);
        assertEquals(TestZeit.getHaeufigkeit(), Haeufigkeit.TAEGLICH);
        assertEquals(TestZeit.getName(), "Name");
        assertEquals(TestZeit.getKommentar(), "Kommentar");
    }

    @Test
    public void endTests() {
        LocalDateTime Zeit = LocalDateTime.of(2017, 1, 1, 13, 0);
        assertEquals(TestZeit1.getEnd(), Zeit);
        assertEquals(TestZeit1.getEndTime(), Zeit.toLocalTime());
    }

    @Test
    public void getTests() {
        assertEquals((long) TestZeit1.getDayOfMonth(), (long) 1);
        assertEquals(TestZeit1.getDayOfWeek(), DayOfWeek.SUNDAY);
        assertEquals(TestZeit1.getMonth(), Month.JANUARY);
        assertEquals(TestZeit1.getStartTime(), LocalTime.of(12, 0));
        assertEquals(TestZeit1.getEndTime(), LocalTime.of(13, 0));
        assertEquals((int)(TestZeit1.getMonthValue()), 1);
    }

    @Test
    public void stringTests() {
        try {
            TestZeit1.setName(null);
            fail("Zeit.setName() sollte eine IllegalArgumentException werfen, wenn das Argument name null ist!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "name darf nicht null sein!");
        }

        try {
            TestZeit1.setKommentar(null);
            fail("Zeit.setKommentar() sollte eine IllegalArgumentException werfen, wenn das kommentar name null ist!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "kommentar darf nicht null sein!");
        }
        TestZeit1.setName("TestName");
        assertEquals(TestZeit1.getName(), "TestName");
        TestZeit1.setKommentar("TestKommentar");
        assertEquals(TestZeit1.getKommentar(), "TestKommentar");
    }

    @Test
    public void selberTagTests() {
//TestZeit1 ist Einmalig am 1.1.17, 12:00 eine Stunde
        assertEquals(TestZeit1.amSelbenTag(LocalDate.of(2017, 1, 1)), true);
        assertEquals(TestZeit1.amSelbenTag(LocalDate.of(2017, 3, 1)), false);
        assertEquals(TestZeit1.amSelbenTag(LocalDate.of(2017, 1, 8)), false);

//TestZeit2 ist wöchentlich am 1.1.16, 10:30 5 Stunden
        assertEquals(TestZeit2.amSelbenTag(LocalDate.of(2016, 1, 1)), true);
        assertEquals(TestZeit2.amSelbenTag(LocalDate.of(2016, 1, 8)), true);
        assertEquals(TestZeit2.amSelbenTag(LocalDate.of(2016, 2, 1)), false);

//TestZeit3 ist monatlich am 1.4.17, 11:45 10 Stunden
        assertEquals(TestZeit3.amSelbenTag(LocalDate.of(2017, 4, 1)), true);
        assertEquals(TestZeit3.amSelbenTag(LocalDate.of(2017, 7, 1)), true);
        assertEquals(TestZeit3.amSelbenTag(LocalDate.of(2018, 2, 1)), true);
        assertEquals(TestZeit3.amSelbenTag(LocalDate.of(2017, 4, 8)), false);
        assertEquals(TestZeit3.amSelbenTag(LocalDate.of(1998, 11, 21)), false);

        assertEquals(TestZeit4.amSelbenTag(LocalDate.of(2017, 12, 4)), true);
        assertEquals(TestZeit4.amSelbenTag(LocalDate.of(1234, 1, 12)), true);

        assertEquals(TestZeit5.amSelbenTag(LocalDate.of(2012, 1, 1)), true);
        assertEquals(TestZeit5.amSelbenTag(LocalDate.of(2014, 10, 12)), false);
    }

    @Test
    public void selbenZeitTests() {
        LocalTime Zeitpunkt1 = LocalTime.of(10, 0);
        LocalTime Zeitpunkt2 = LocalTime.of(13, 0);
        LocalTime Zeitpunkt3 = LocalTime.of(2, 0);
        Duration Dauer1 = Duration.ofHours(2);
        Duration Dauer2 = Duration.ofHours(4);
        Duration Dauer3 = Duration.ofHours(20);
        Duration Dauer4 = Duration.ofMinutes(30);

//TestZeit1 ist Einmalig am 1.1.17, 12:00 eine Stunde
        assertFalse(TestZeit1.zurSelbenZeit(Zeitpunkt1, Dauer1));
        assertTrue(TestZeit1.zurSelbenZeit(Zeitpunkt1, Dauer2));
        assertFalse(TestZeit1.zurSelbenZeit(Zeitpunkt2, Dauer2));
        assertTrue(TestZeit1.zurSelbenZeit(Zeitpunkt3, Dauer3));

//TestZeit2 ist wöchentlich am 1.1.16, 10:30 5 Stunden
        assertTrue(TestZeit2.zurSelbenZeit(Zeitpunkt2, Dauer1));
        assertFalse(TestZeit2.zurSelbenZeit(Zeitpunkt1, Dauer4));
        assertFalse(TestZeit2.zurSelbenZeit(Zeitpunkt3, Dauer2));
    }

    @Test
    public void compareTests() {
        assertTrue(TestZeit1.compareTo(TestZeit2) > 0);
        assertTrue(TestZeit2.compareTo(TestZeit3) < 0);
    }

    @Test
    public void formatierungsTests() {
        assertEquals(TestZeit1.getFormatierteDauer(), "Von 12:00 bis 13:00");
        assertEquals(TestZeit2.getFormatierteDauer(), "Von 10:30 bis 15:30");

        assertEquals(TestZeit1.getFormatiertesDatum(), "1.1.2017");
        assertEquals(TestZeit2.getFormatiertesDatum(), "1.1.2016");

        assertEquals(TestZeit1.getFormatierteStartZeit(), "12:00");
        assertEquals(TestZeit1.getFormatierteEndZeit(), "13:00");

        assertEquals(TestZeit1.getFormatierteHaeufigkeit(), " einmalig am 1.1.2017");
        assertEquals(TestZeit2.getFormatierteHaeufigkeit(), " woechentlich Freitags");
        assertEquals(TestZeit3.getFormatierteHaeufigkeit(), " monatlich am 1ten");
        assertEquals(TestZeit4.getFormatierteHaeufigkeit(), " taeglich");
        assertEquals(TestZeit5.getFormatierteHaeufigkeit(), " jaehrlich am 1. Januar");

        assertEquals(TestZeit1.toString(), "2017-01-01T12:00PT1H");
    }

    @Test
    public void HaeufigkeitsTests() {
        assertEquals(EINMAL, Haeufigkeit.of("einmalig"));
        assertEquals(JAEHRLICH, Haeufigkeit.of("jaehrlich"));
        assertEquals(MONATLICH, Haeufigkeit.of("monatlich"));
        assertEquals(WOECHENTLICH, Haeufigkeit.of("woechentlich"));
        assertEquals(TAEGLICH, Haeufigkeit.of("taeglich"));
    }
}

