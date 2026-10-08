package sst.stefano.data.filters;

import sst.stefano.data.Word;
import sst.stefano.data.WordList;

public class ClassicWordFilter implements WordFilter {
    @Override
    public double filter(WordList wordList) {
        double exerciseAvg = (double) wordList.getUsed() / (double) wordList.getWordsListSize();
        double successAvg = (double) wordList.getSuccess() / (double) wordList.getUsed();

        WordSelector selector = WordSelectorModule.getWordSelector(exerciseAvg, successAvg);

        for (Word word : wordList.getWords()) {
            if (selector.isWordUnknown(word)) {
                wordList.addUnknownWord(word);
            } else {
                word.checkResult();
                wordList.addKnownWord(word);
            }
        }

        return exerciseAvg;
    }
}
