package sst.stefano.data;

import lombok.Getter;
import lombok.Setter;
import sst.stefano.main.StefanoConstants;

import java.util.StringTokenizer;

public class Word {
    @Getter
    private String francais;
    @Getter
    private String italien;

    @Getter
    private int used = 0, failed = 0, success = 0, straight = 0;
    private int prevUsed = 0, prevFailed = 0, prevSuccess = 0, prevStraight = 0;

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
                prevUsed = -1;
                prevFailed = -1;
                prevSuccess = -1;
                prevStraight = -1;

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
        prevUsed = used;
        used++;
        specialWord = false;
    }

    public void failed() {
        prevSuccess = success;
        prevFailed = failed;
        prevStraight = straight;
        failed++;
        straight = 0;
    }

    public void success() {
        prevSuccess = success;
        prevFailed = failed;
        prevStraight = straight;
        success++;
        straight++;
        if (StefanoConstants.getINSTANCE().getStraightPivot() < straight && failed > 0) {
            failed--;
            success++;
        }

        if (0 == failed) {
            straight = success;
        }
    }

    public String getWordStat() {
        double stat = getStat();
        return StefanoConstants.decimalPercentFormat.format(stat);
    }

    public double getStat() {
        return (0 == used) ? 0.00 : (double) success / (double) used;
    }

    @Override
    public String toString() {
        return toString(true);
    }

    public String toString(boolean toSave) {
        String word = francais + "=" + italien;
        String stat = used + "/" + failed + "/" + success + "/" + straight;
        String previousStat = prevUsed + "/" + prevFailed + "/" + prevSuccess + "/" + prevStraight;
        if (prevUsed != -1 && !toSave) {
            stat += " (" + previousStat + ")";
        }
        return word + "/" + stat;
    }

    public void checkResult() {
        if (failed > 0 && straight >= StefanoConstants.getINSTANCE().getStraightPivot()) {
            success = used;
            failed = 0;
        }
    }
}
