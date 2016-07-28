package sst.stefano.data.filters;

import java.util.Collection;
import java.util.Iterator;

import sst.stefano.data.Word;
import sst.stefano.data.WordList;

import com.google.common.collect.Ordering;

public class HundredFirstRareWordsFilter implements WordFilter {

    @Override
    public double filter(WordList wordList) {
        Ordering<Word> usedOrdering = new Ordering<Word>() {
            public int compare(Word left, Word right) {
                return new Integer(left.getUsed()).compareTo(new Integer(right.getUsed()));
            }
        };
        int i = 0;
        Collection<Word> list = usedOrdering.sortedCopy(wordList.getWords());
        for (Iterator<Word> iterator = list.iterator(); iterator.hasNext();) {
            Word word = (Word) iterator.next();
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
