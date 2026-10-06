package sst.stefano.data.filters;

import com.google.common.collect.Ordering;
import sst.stefano.data.Word;
import sst.stefano.data.WordList;

import java.util.Collection;

public class HundredFirstRareWordsFilter implements WordFilter {

    @Override
    public double filter(WordList wordList) {
        Ordering<Word> usedOrdering = new Ordering<>() {
            public int compare(Word left, Word right) {
                assert left != null;
                assert right != null;
                return Integer.compare(left.getUsed(), right.getUsed());
            }
        };
        int i = 0;
        Collection<Word> list = usedOrdering.sortedCopy(wordList.getWords());
        for (Word word : list) {
            if (i < 100) {
                wordList.addUnknownWord(word);
            } else {
                wordList.addKnownWord(word);
            }
            i++;
        }
        return 0;
    }

}
