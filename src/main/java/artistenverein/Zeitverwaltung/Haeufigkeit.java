package artistenverein.Zeitverwaltung;

import org.springframework.util.Assert;

/**
 * Ein Enum für verschiedene Häufigkeiten von Sperrzeiten.
 * Hat eine statische Funktion als Möglichkeit, eine Haeufigkeit aus einem String zu erstellen.
 *
 * @author Anton Reinhard
 */
public enum Haeufigkeit {
    EINMAL,
    TAEGLICH,
    WOECHENTLICH,
    MONATLICH,
    JAEHRLICH;

    /**
     * Statische Funktion, die eine Häufigkeit zurückgibt, die zu einem übergebenen String passt.
     *
     * @param haeuf
     *  Der String, anhand dessen eine Häufigkeit erzeugt wird. Zum Beispiel "einmalig".
     *
     * @return
     *  Die erstellte Häufigkeit.
     */
    public static Haeufigkeit of(String haeuf) {
    	Assert.notNull(haeuf, "haeuf darf nicht null sein!");
        Haeufigkeit h = null;
        switch (haeuf) {
            case "einmalig":
                h = Haeufigkeit.EINMAL;
                break;
            case "jaehrlich":
                h = Haeufigkeit.JAEHRLICH;
                break;
            case "monatlich":
                h = Haeufigkeit.MONATLICH;
                break;
            case "woechentlich":
                h = Haeufigkeit.WOECHENTLICH;
                break;
            case "taeglich":
                h = Haeufigkeit.TAEGLICH;
                break;
            default:
            	h = null;
        }
        return h;
    }
}
