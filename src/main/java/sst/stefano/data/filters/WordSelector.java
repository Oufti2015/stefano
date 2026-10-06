package sst.stefano.data.filters;

import sst.stefano.data.Word;

public abstract class WordSelector {

    protected final double exerciseAvg;
    protected final double successAvg;

    public WordSelector(double exerciseAvg, double successAvg) {
        super();
        this.exerciseAvg = exerciseAvg;
        this.successAvg = successAvg;
    }

    public abstract boolean isWordUnknown(Word word);
}
