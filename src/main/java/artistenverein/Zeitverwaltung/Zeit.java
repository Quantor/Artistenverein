package artistenverein.Zeitverwaltung;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import java.time.LocalTime;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Duration;
import java.time.DayOfWeek;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

import org.springframework.util.Assert;

/**
 * Die Zeit-Klasse verwaltet eine Zeitspanne mit einer Häufigkeit zusammen.
 *
 * @author Anton Reinhard
 */
@Entity
public class Zeit implements Comparable<Zeit>{

    private @Id @GeneratedValue long id;

    private LocalDateTime start;
    private Duration dauer;
    private Haeufigkeit haeufigkeit;
    private String name;
    private String kommentar;

    /**
     * Dieser Konstruktor ist nur hier, weil ein Standardkonstruktor gebraucht wird. Er sollte ansonsten
     * nicht verwendet werden.
     */
    @SuppressWarnings("unused")
    protected Zeit() {
    }

    /**
     * Eine neue Zeitspanne erstellen. Wenn ein Name oder Kommentar gebraucht wird kann der entsprechende
     * andere Konstruktor verwendet werden.
     *
     * @param start
     *  Der Startzeitpunkt der neuen Zeitspanne.
     *
     * @param dauer
     *  Die Dauer der neuen Zeitspanne.
     *
     * @param haeufigkeit
     *  Die Häufigkeit der neuen Zeitspanne als Enum.
     *
     */
    public Zeit(LocalDateTime start, Duration dauer, Haeufigkeit haeufigkeit) {
        Assert.notNull(start, "start darf nicht null sein!");
        Assert.notNull(dauer, "dauer darf nicht null sein!");
        Assert.notNull(haeufigkeit, "haeufigkeit darf nicht null sein!");
        
        this.dauer = dauer;
        this.start = start;
        this.haeufigkeit = haeufigkeit;
        this.name = "";
        this.kommentar = "";
    }

    /**
     * Eine neue Zeitspanne erstellen. Wenn kein Name oder Kommentar gebraucht wird kann der entsprechende
     * andere Konstruktor verwendet werden.
     *
     * @param start
     *  Der Startzeitpunkt der neuen Zeitspanne.
     *
     * @param dauer
     *  Die Dauer der neuen Zeitspanne.
     *
     * @param haeufigkeit
     *  Die Häufigkeit der neuen Zeitspanne als Enum.
     *
     * @param name
     *  Der Name der neuen Zeitspanne.
     *
     * @param kommentar
     *  Ein Kommentar zur neuen Zeitspanne.
     *
     */
    public Zeit(LocalDateTime start, Duration dauer, Haeufigkeit haeufigkeit, String name, String kommentar) {
        Assert.notNull(start, "start darf nicht null sein!");
        Assert.notNull(dauer, "dauer darf nicht null sein!");
        Assert.notNull(haeufigkeit, "haeufigkeit darf nicht null sein!");
        Assert.notNull(name, "name darf nicht null sein!");
        Assert.notNull(kommentar, "kommentar darf nicht null sein!");

        this.dauer = dauer;
        this.start = start;
        this.haeufigkeit = haeufigkeit;
        this.name = name;
        this.kommentar = kommentar;
    }

    /**
     * Die ID der Zeit wird zurückgegeben.
     *
     * @return
     *  Die ID
     */
    public long getId() {
        return id;
    }

    /**
     * Die Dauer der Zeitspanne wird zurückgegeben.
     *
     * @return
     *  Die Dauer der Zeitspanne.
     */
    public Duration getDauer() {
        return dauer;
    }

    /**
     * Der Startzeitpunkt der Zeitspanne wird zurückgegeben. Hier ist der eingetragene Wert gemeint, mit Datum.
     *
     * @return
     *  Der Startzeitpunkt der Zeitspanne.
     */
    public LocalDateTime getStart() {
        return start;
    }

    /**
     * Der Endzeitpunkt der Zeitspanne wird zurückgegeben. Das eingetragene Datum ist mit dabei.
     *
     * @return
     *  Der Endzeitpunkt der Zeitspanne.
     */
    public LocalDateTime getEnd() {
        return start.plus(dauer);
    }

    /**
     * Der Monat wird als 'Integer' zurückgegeben. Dieser Wert bezieht sich auf den eingetragenen Wert.
     * Wenn die Zeit wiederholt stattfindet ist das trotzdem immer der am Anfang eingetragene Monat.
     *
     * @return
     *  Der Monat der Zeitspanne.
     */
    public Integer getMonthValue() {
        return start.getMonthValue();
    }

    /**
     * Der Monat wird als 'Month' zurückgegeben. Dieser Wert bezieht sich auf den eingetragenen Wert.
     * Wenn die Zeit wiederholt stattfindet ist das trotzdem immer der am Anfang eingetragene Monat.
     *
     * @return
     *  Der Monat der Zeitspanne.
     */
    public Month getMonth() {
        return start.getMonth();
    }

    /**
     * Der Wochentag, an dem die Zeit ist wird zurückgegeben.
     *
     * @return
     *  Der Wochentag.
     */
    public DayOfWeek getDayOfWeek() {
        return start.getDayOfWeek();
    }

    /**
     * Der Tag des Monats wird zurückgegeben.
     *
     * @return
     *  Der Tag des Monats (1-31)
     */
    public Integer getDayOfMonth() {
        return start.getDayOfMonth();
    }

    /**
     * Der Startzeitpunkt als bloße Uhrzeit wird zurückgegeben.
     *
     * @return
     *  Der Startzeitpunkt.
     */
    public LocalTime getStartTime() {
        return start.toLocalTime();
    }

    /**
     * Der Endzeitpunkt als bloße Uhrzeit wird zurückgegeben.
     *
     * @return
     *  Der Endzeitpunkt.
     */
    public LocalTime getEndTime() {
        return getEnd().toLocalTime();
    }

    /**
     * Der Name der Zeit. Kann auch ein leerer String sein.
     *
     * @return
     *  Der Name der Zeit.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Der Name der Zeit wird gesetzt. Ein leerer String ist zulässig, null nicht.
     *
     * @param name
     *  Der zukünftige Name der Zeit.
     */
    public void setName(String name) {
    	Assert.notNull(name, "name darf nicht null sein!");
        this.name = name;
    }

    /**
     * Der Kommentar der Zeit. Kann auch ein leerer String sein.
     *
     * @return
     *  Der Kommentar der Zeit.
     */
    public String getKommentar() {
        return this.kommentar;
    }

    /**
     * Der Kommentar der Zeit wird gesetzt. Ein leerer String ist zulässig, null nicht.
     *
     * @param kommentar
     *  Der zukünftige Kommentar der Zeit.
     */
    public void setKommentar(String kommentar) {
    	Assert.notNull(kommentar, "kommentar darf nicht null sein!");
        this.kommentar = kommentar;
    }

    /**
     * Überprüft, ob ein gegebenes Datum am gleichen Tag ist, wie diese Zeit. Da die Zeit wiederholt werden kann
     * ist das nicht trivial.
     *
     * @param tag
     *  Der Tag, für den getestet werden soll, ob die Zeit am selben Tag vorkommt.
     *
     * @return
     *  Falls die Zeit am selben Tag auftritt, wird 'true' zurückgegeben, sonst 'false'.
     */
    public Boolean amSelbenTag(LocalDate tag) {
    	Assert.notNull(tag, "tag darf nicht null sein!");

        Integer monat = tag.getMonthValue();
        Integer tag_des_monats = tag.getDayOfMonth();
        DayOfWeek wochentag = tag.getDayOfWeek();

        boolean returnValue = false;
        switch (haeufigkeit) {
            case EINMAL:
                //wenn entweder der Anfang oder das Ende am selben Tag sind
            	returnValue = start.toLocalDate().equals(tag);
            	break;
            case JAEHRLICH:
                //wenn Monat und Tag des Monats gleich sind
            	returnValue = getMonthValue().equals(monat) && getDayOfMonth().equals(tag_des_monats);
            	break;
            case MONATLICH:
                //wenn Tag des Monats gleich ist
            	returnValue = getDayOfMonth().equals(tag_des_monats);
            	break;
            case WOECHENTLICH:
                //wenn der Wochentag gleich ist
            	returnValue = getDayOfWeek().equals(wochentag);
            	break;
            case TAEGLICH:
                //wenn es täglich ist, ist es immer am selben Tag
            	returnValue = true;
            	break;
            default:
                //vorsichtshalber ansonsten false
            	returnValue = false;
            	break;
        }
        return returnValue;
    }

    /**
     * Überprüft, ob eine gegebene Zeitspanne sich mit dieser Zeit überschneidet, ohne Beachtung des Datums.
     *
     * @param start
     *  Der Startzeitpunkt der zu überprüfenden Zeitspanne.
     *
     * @param dauer
     *  Die Dauer der zu überprüfenden Zeitspanne.
     *
     * @return
     *  Falls die Zeiten sich überschneiden wird 'true' zurückgegeben. Ansonsten 'false'.
     */
    public Boolean zurSelbenZeit(LocalTime start, Duration dauer) {
    	Assert.notNull(start, "startzeit darf nicht null sein!");
    	Assert.notNull(dauer, "dauer darf nicht null sein!");
    	
    	LocalDateTime startzeit = LocalDateTime.of(LocalDate.now(), start);
        LocalDateTime endzeit = LocalDateTime.of(LocalDate.now(), start).plus(dauer);
        LocalDateTime sperrstartzeit = LocalDateTime.of(LocalDate.now(), getStartTime());
        LocalDateTime sperrendzeit = LocalDateTime.of(LocalDate.now(), getStartTime()).plus(getDauer());
        
        boolean returnValue = false;
        //wenn die übergebene Startzeit ist zwischen start und ende dieser Zeit
        if ((startzeit.isAfter(sperrstartzeit)
                && startzeit.isBefore(sperrendzeit))
                || startzeit.isEqual(sperrstartzeit)) {
            returnValue = true;
        //wenn die übergebene Endzeit ist zwischen start und ende dieser Zeit
        } else if ((endzeit.isAfter(sperrstartzeit)
                && endzeit.isBefore(sperrendzeit))
                || endzeit.isEqual(sperrendzeit)) {
        	returnValue = true;
        } else if (startzeit.isBefore(sperrstartzeit) && endzeit.isAfter(sperrendzeit)) {
        	returnValue = true;
        }
        
        //Wenn es sonst nix ist.
        return returnValue;
    }

    /**
     * Gibt die Häufigkeit der Zeit als Enum zurück.
     *
     * @return
     *  Die Häufigkeit der Zeit.
     */
    public Haeufigkeit getHaeufigkeit() {
        return haeufigkeit;
    }

    /**
     * Die Eingetragenen Startzeiten mit Datumsbeachtung werden verglichen.
     *
     * @param zeit
     *  Die Zeit, mit der verglichen werden soll.
     *
     * @return
     *  Wie jede compareTo Funktion wird ein Vergleichswert als Integer zurückgegeben,
     *  anhand dessen sortiert werden kann.
     */
    public int compareTo(Zeit zeit) {
        return start.compareTo(zeit.getStart());
    }

    /**
     * Gibt einen String zurück, der aus vollständiger eingetragener Startzeit und Dauer besteht.
     *
     * @return
     *  Der String aus Startzeit und Dauer.
     */
    @Override
    public String toString() {
    	return  start.toString() + dauer.toString(); 
    }

    /**
     * Gibt einen String zurück, der das Datum in Deutsch formatierter Weise zurückgibt.
     *
     * @return
     *  Das formatierte Datum als String. Zum Beispiel 12.4.2012
     */
    public String getFormatiertesDatum() {
        String s;
        s = String.valueOf(this.start.getDayOfMonth());
        s += ".";
        s += String.valueOf(this.getMonthValue());
        s += ".";
        s += String.valueOf(this.getStart().getYear());
        return s;
    }

    /**
     * Gibt einen String zurück, der die Laufzeit in formatierter Weise zurückgibt.
     *
     * @return
     *  Die formatierte Laufzeit.
     */
    public String getFormatierteDauer() {
        return "Von " + this.getFormatierteStartZeit() + " bis " + this.getFormatierteEndZeit();
    }

    /**
     * Gibt einen String zurück, der die Startzeit als String zurückgibt.
     *
     * @return
     *  Die formatierte Startzeit.
     */
    public String getFormatierteStartZeit() {
        return this.getStartTime().toString();
    }

    /**
     * Gibt einen String zurück, der die Endzeit als String zurückgibt.
     *
     * @return
     *  Die formatierte Endzeit.
     */
    public String getFormatierteEndZeit() {
        return this.getEndTime().toString();
    }

    /**
     * Gibt einen String zurück, der die Häufigkeit als formatierten String zurückgibt.
     *
     * @return
     *  Die formatierte Häufigkeit. Zum Beispiel " einmalig am 12.04.2012"
     */
    public String getFormatierteHaeufigkeit() {
    	String returnValue;
        switch (this.haeufigkeit) {
            case EINMAL:
                returnValue = " einmalig am " + getFormatiertesDatum();
                break;
            case JAEHRLICH:
                returnValue = " jaehrlich am " + start.getDayOfMonth() + ". " +
                       start.getMonth().getDisplayName(TextStyle.FULL, Locale.GERMANY);
                break;
            case MONATLICH:
                returnValue = " monatlich am " + start.getDayOfMonth() + "ten";
                break;
            case WOECHENTLICH:
                returnValue = " woechentlich " +
                       start.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.GERMANY) +
                       "s";
                break;
            case TAEGLICH:
                returnValue = " taeglich";
                break;
            default:
            	returnValue = "Unbekannte Häufigkeit";
            	break;
        }
        return returnValue;
    }
}
