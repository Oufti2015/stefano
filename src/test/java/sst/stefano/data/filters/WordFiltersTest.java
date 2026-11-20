package sst.stefano.data.filters;

import org.junit.jupiter.api.Test;
import sst.stefano.data.Word;
import sst.stefano.data.WordList;
import sst.stefano.main.StefanoConstants;

import static org.junit.jupiter.api.Assertions.*;

class WordFiltersTest {

    private static void withStraightPivot(int pivot, Runnable test) {
        StefanoConstants constants = StefanoConstants.getINSTANCE();
        int previousPivot = constants.getStraightPivot();
        constants.setStraightPivot(pivot);
        try {
            test.run();
        } finally {
            constants.setStraightPivot(previousPivot);
        }
    }

    @Test
    void simpleWordSelectorComparesSuccessWithExerciseAverage() {
        SimpleWordSelector selector = new SimpleWordSelector(5, 0.5);

        assertTrue(selector.isWordUnknown(new Word("peu=poco/3/1/2/0")));
        assertFalse(selector.isWordUnknown(new Word("assez=abbastanza/3/0/3/0")));
    }

    @Test
    void averageWordSelectorUsesSuccessAverageAndStraightPivot() {
        withStraightPivot(3, () -> {
            AverageWordSelector selector = new AverageWordSelector(10, 0.9);

            assertTrue(selector.isWordUnknown(new Word("faible=debole/10/2/8/8")));
            assertTrue(selector.isWordUnknown(new Word("court=corto/10/1/9/2")));
            assertFalse(selector.isWordUnknown(new Word("forte=forte/10/1/9/3")));
        });
    }

    @Test
    void selectorModuleCreatesAverageWordSelector() {
        assertInstanceOf(AverageWordSelector.class, WordSelectorModule.getWordSelector(4, 0.75));
    }

    @Test
    void rareWordsFilterSelectsLeastUsedWordsWithinLimit() {
        WordList wordList = new WordList();
        Word mostRare = new Word("rare=raro");
        Word secondRare = new Word("second=secondo/1/1/0/0");
        Word common = new Word("common=comune/2/1/1/1");
        Word mostCommon = new Word("frequent=frequente/3/1/2/2");
        wordList.addWord(mostCommon);
        wordList.addWord(common);
        wordList.addWord(secondRare);
        wordList.addWord(mostRare);

        double result = new HundredFirstRareWordsFilter().filter(wordList, 2);

        assertEquals(0, result);
        assertEquals(2, wordList.getUnknownWordsListSize());
        assertTrue(wordList.isWordInUnknownList(mostRare));
        assertTrue(wordList.isWordInUnknownList(secondRare));
        assertFalse(wordList.isWordInUnknownList(common));
        assertFalse(wordList.isWordInUnknownList(mostCommon));
    }

    @Test
    void rareWordsFilterDefaultsToSelectingUpToOneHundredWords() {
        WordList wordList = new WordList();
        Word first = new Word("un=uno");
        Word second = new Word("deux=due");
        wordList.addWord(first);
        wordList.addWord(second);

        double result = new HundredFirstRareWordsFilter().filter(wordList);

        assertEquals(0, result);
        assertEquals(2, wordList.getUnknownWordsListSize());
        assertTrue(wordList.isWordInUnknownList(first));
        assertTrue(wordList.isWordInUnknownList(second));
    }

    @Test
    void classicWordFilterSelectsUnknownWordsAndChecksKnownWordResults() {
        withStraightPivot(3, () -> {
            WordList wordList = new WordList();
            Word recoverable = new Word("recoverable=recuperabile/11/1/10/3");
            wordList.addWord(recoverable);
            for (int i = 0; i < 26; i++) {
                wordList.addWord(new Word("word" + i + "=parola" + i));
            }
            wordList.updateStat();

            double exerciseAverage = new ClassicWordFilter().filter(wordList);

            assertEquals(11.0 / 27, exerciseAverage, 0.000001);
            assertEquals(26, wordList.getUnknownWordsListSize());
            assertFalse(wordList.isWordInUnknownList(recoverable));
            assertEquals(0, recoverable.getFailed());
            assertEquals(11, recoverable.getSuccess());
        });
    }
}
