package sst.stefano.data.filters;

import sst.stefano.data.Word;

public class AverageWordSelector extends WordSelector {

    public AverageWordSelector(double exerciceAvg, double successAvg) {
        super(exerciceAvg, successAvg);
    }

    @Override
    public boolean isWordUnkown(Word word) {
        return (word.getStat() < successAvg || word.getUsed() < (exerciceAvg / 2));
    }

}
