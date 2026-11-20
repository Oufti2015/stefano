package sst.stefano.data;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordListTest {

    private static WordList wordListWithKnownAndUnknownWords() {
        WordList wordList = new WordList();
        wordList.addWord(new Word("connu=noto/10/0/10/10"));
        wordList.addWord(new Word("nouveau=nuovo"));
        return wordList;
    }

    @Test
    void addWordRejectsDuplicateFrenchAndItalianWords() {
        WordList wordList = new WordList();
        Word first = new Word("bonjour=ciao");

        wordList.addWord(first);
        wordList.addWord(new Word("bonjour=salve"));
        wordList.addWord(new Word("salut=ciao"));

        assertEquals(1, wordList.getWordsListSize());
        assertEquals(List.of(first), List.copyOf(wordList.getWords()));
    }

    @Test
    void exposesWordsAndTracksListSizes() {
        WordList wordList = new WordList();
        Word first = new Word("bonjour=ciao");
        Word second = new Word("merci=grazie");

        wordList.addWord(first);
        wordList.addWord(second);
        Collection<Word> words = wordList.getWords();
        wordList.addUnknownWord(first);

        assertEquals(2, wordList.getWordsListSize());
        assertEquals(2, words.size());
        assertEquals(1, wordList.getUnknownWordsListSize());
        assertTrue(wordList.isWordInUnknownList(first));
        assertFalse(wordList.isWordInUnknownList(second));
    }

    @Test
    void updateStatAggregatesWordStatistics() {
        WordList wordList = new WordList();
        wordList.addWord(new Word("un=uno/3/1/2/1"));
        wordList.addWord(new Word("deux=due/5/2/3/1"));

        wordList.updateStat();

        assertEquals(8, wordList.getUsed());
        assertEquals(3, wordList.getFailed());
        assertEquals(5, wordList.getSuccess());
    }

    @Test
    void randomFiltersWordsAndReturnsOneOfTheListWords() {
        WordList wordList = wordListWithKnownAndUnknownWords();
        Word known = wordList.getWords().stream()
                .filter(word -> word.getUsed() > 0)
                .findFirst()
                .orElseThrow();
        Word unknown = wordList.getWords().stream()
                .filter(word -> word.getUsed() == 0)
                .findFirst()
                .orElseThrow();

        Word selected = wordList.random();

        assertNotNull(selected);
        assertTrue(wordList.getWords().stream().anyMatch(word -> word == selected));
        assertTrue(wordList.isWordInUnknownList(unknown));
        assertTrue(wordList.isWordInUnknownList(known));
        assertEquals(10, wordList.getUsed());
        assertEquals(10, wordList.getSuccess());
    }

    @Test
    void successTracksStreakAndOptionallyUpdatesCurrentWord() {
        WordList wordList = wordListWithKnownAndUnknownWords();
        wordList.setCurrentStraight(2);
        wordList.setBestStraight(2);

        Word withoutRealSuccess = wordList.random();
        int usedBefore = withoutRealSuccess.getUsed();
        int successBefore = withoutRealSuccess.getSuccess();
        wordList.success(false);

        assertEquals(3, wordList.getCurrentStraight());
        assertEquals(3, wordList.getBestStraight());
        assertEquals(usedBefore, withoutRealSuccess.getUsed());
        assertEquals(successBefore, withoutRealSuccess.getSuccess());

        Word withRealSuccess = wordList.random();
        int usedBeforeRealSuccess = withRealSuccess.getUsed();
        int successBeforeRealSuccess = withRealSuccess.getSuccess();
        wordList.success(true);

        assertEquals(4, wordList.getCurrentStraight());
        assertEquals(4, wordList.getBestStraight());
        assertEquals(usedBeforeRealSuccess + 1, withRealSuccess.getUsed());
        assertEquals(successBeforeRealSuccess + 1, withRealSuccess.getSuccess());
    }

    @Test
    void failedResetsStreakAndUpdatesCurrentWord() {
        WordList wordList = wordListWithKnownAndUnknownWords();
        wordList.setCurrentStraight(4);
        wordList.setBestStraight(5);
        Word selected = wordList.random();
        int usedBefore = selected.getUsed();
        int failedBefore = selected.getFailed();

        wordList.failed();

        assertEquals(0, wordList.getCurrentStraight());
        assertEquals(5, wordList.getBestStraight());
        assertEquals(usedBefore + 1, selected.getUsed());
        assertEquals(failedBefore + 1, selected.getFailed());
    }

    @Test
    void showUnknownWordsHandlesUnknownWords() {
        WordList wordList = new WordList();
        Word unknown = new Word("nouveau=nuovo");
        wordList.addUnknownWord(unknown);

        wordList.showUnknownWords();

        assertTrue(wordList.isWordInUnknownList(unknown));
    }

    @Test
    void straightStreakAccessorsStoreValues() {
        WordList wordList = new WordList();

        wordList.setBestStraight(12);
        wordList.setCurrentStraight(7);

        assertEquals(12, wordList.getBestStraight());
        assertEquals(7, wordList.getCurrentStraight());
    }
}
