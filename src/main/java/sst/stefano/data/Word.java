package sst.stefano.data;

import java.util.StringTokenizer;

import lombok.Getter;
import lombok.Setter;
import sst.stefano.main.StefanoConstants;

public class Word {
    @Getter
    private String francais;
    @Getter
    private String italien;

    @Getter
    private int used = 0, failed = 0, success = 0, straight = 0;

    @Getter
    @Setter
    private boolean specialWord = false;

    public Word(String line) {
        init(line);
    }

    private void init(String line) {
        boolean isFrenchItalian = true;
        StringTokenizer st = new StringTokenizer(line, "/");
        while (st.hasMoreElements()) {
            if (isFrenchItalian) {
                extractFrenchItalian(st.nextToken());
                isFrenchItalian = false;
            } else {
                used = Integer.parseInt(st.nextToken());
                failed = Integer.parseInt(st.nextToken());
                success = Integer.parseInt(st.nextToken());
                if (st.hasMoreElements()) {
                    straight = Integer.parseInt(st.nextToken());
                } else {
                    straight = 0;
                }
                used = failed + success;
                if (0 == failed) {
                    straight = success;
                }
            }
        }
    }

    private void extractFrenchItalian(String line) {
        StringTokenizer st = new StringTokenizer(line, "=");
        boolean isFrancais = true;
        while (st.hasMoreElements()) {
            if (isFrancais) {
                francais = st.nextToken();
                isFrancais = false;
            } else {
                italien = st.nextToken();
            }
        }
    }

    public void used() {
        used++;
        specialWord = false;
    }

    public void failed() {
        failed++;
        straight = 0;
    }

    public void success() {
        success++;
        straight++;
        if (StefanoConstants.getInstance().getStraightPivot() < straight && failed > 0) {
            failed--;
            success++;
        }

        if (0 == failed) {
            straight = success;
        }
    }

    public String getWordStat() {
        double stat = getStat();
        return "" + used + " (" + straight + ") - " + StefanoConstants.decimalPercentFormat.format(stat);
    }

    public double getStat() {
        return (0 == used) ? 0.00 : (double) success / (double) used;
    }

    public String toString() {
        return francais + "=" + italien + "/" + used + "/" + failed + "/" + success + "/" + straight;
    }
}
