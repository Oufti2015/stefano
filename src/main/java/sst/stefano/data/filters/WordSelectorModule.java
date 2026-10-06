package sst.stefano.data.filters;

public class WordSelectorModule {
    private static final boolean USE_AVERAGE_FILTER = true;

    public static WordSelector getWordSelector(double exerciseAvg, double successAvg) {
        return (USE_AVERAGE_FILTER) ? new AverageWordSelector(exerciseAvg, successAvg) : new SimpleWordSelector(exerciseAvg, successAvg);
    }
}
