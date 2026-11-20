package sst.stefano.data.filters;

import com.google.common.collect.Ordering;
import sst.stefano.data.Word;
import sst.stefano.data.WordList;

import java.util.List;

public class HundredFirstRareWordsFilter implements WordFilter {
    @Override
    public double filter(WordList wordList) {
        return filter(wordList, 100);
    }

    public double filter(WordList wordList, int listSize) {
        Ordering<Word> usedOrdering = new Ordering<>() {
            public int compare(Word left, Word right) {
                assert left != null;
                assert right != null;
                int compare = Integer.compare(left.getUsed(), right.getUsed());
                if (compare == 0) {
                    compare = Integer.compare(left.getStraight(), right.getStraight());
                }
                return compare;
            }
        };
        int i = 0;
        List<Word> list = usedOrdering.sortedCopy(wordList.getWords());
        for (Word word : list) {
            if (i < listSize) {
                if (!wordList.isWordInUnknownList(word)) {
                    wordList.addUnknownWord(word);
                    i++;
                }
            } else {
                wordList.addKnownWord(word);
            }
        }

        return 0;
    }
}
