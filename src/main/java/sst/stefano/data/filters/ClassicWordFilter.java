package sst.stefano.data.filters;

import sst.stefano.data.Word;
import sst.stefano.data.WordList;

public class ClassicWordFilter implements WordFilter {
    // private static Logger logger =
    // LoggerFactory.getLogger(ClassicWordFilter.class);

    @Override
    public double filter(WordList wordList) {
        double exerciceAvg = (double) wordList.getUsed() / (double) wordList.getWordsListSize();
        double successAvg = (double) wordList.getSuccess() / (double) wordList.getUsed();

        // logger.info("exerciceAvg = " + exerciceAvg);
        // logger.info("successAvg = " + successAvg);

        WordSelector selector = WordSelectorModule.getWordSelector(exerciceAvg, successAvg);

        for (Word word : wordList.getWords()) {
            if (selector.isWordUnkown(word)) {
                wordList.addUnknownWord(word);
            } else {
                wordList.addKnownWord(word);
            }
        }

        return exerciceAvg;
    }

}
