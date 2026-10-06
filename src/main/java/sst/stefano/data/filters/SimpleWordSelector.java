package sst.stefano.data.filters;

import sst.stefano.data.Word;

public class SimpleWordSelector extends WordSelector {

    public SimpleWordSelector(double exerciseAvg, double successAvg) {
        super(exerciseAvg, successAvg);
    }

    @Override
    public boolean isWordUnknown(Word word) {
        return ((word.getSuccess() + 2) < exerciseAvg);
    }
}
