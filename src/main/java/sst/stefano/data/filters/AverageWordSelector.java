package sst.stefano.data.filters;

import sst.stefano.data.Word;

public class AverageWordSelector extends WordSelector {

    public AverageWordSelector(double exerciseAvg, double successAvg) {
        super(exerciseAvg, successAvg);
    }

    @Override
    public boolean isWordUnknown(Word word) {
        return (word.getStat() < successAvg || word.getUsed() < (exerciseAvg / 2));
    }
}
