package sst.stefano.data.filters;

import sst.stefano.data.Word;

public abstract class WordSelector {

    protected double exerciceAvg = 0.0;
    protected double successAvg = 0.0;

    public WordSelector(double exerciceAvg, double successAvg) {
        super();
        this.exerciceAvg = exerciceAvg;
        this.successAvg = successAvg;
    }

    public abstract boolean isWordUnkown(Word word);
}
