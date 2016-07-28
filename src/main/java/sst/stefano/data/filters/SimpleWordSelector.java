package sst.stefano.data.filters;

import sst.stefano.data.Word;

public class SimpleWordSelector extends WordSelector {

    public SimpleWordSelector(double exerciceAvg, double successAvg) {
        super(exerciceAvg, successAvg);
    }

    @Override
    public boolean isWordUnkown(Word word) {
        return ((word.getSuccess() + 2) < exerciceAvg);
    }

}
