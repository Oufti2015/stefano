package sst.stefano.data;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sst.stefano.data.filters.ClassicWordFilter;
import sst.stefano.data.filters.HundredFirstRareWordsFilter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class WordList {
    private static final int SPECIAL_WORD = 5;

    private static final Logger logger = LoggerFactory.getLogger(WordList.class);
    private static final DecimalFormat decimalFormat = new DecimalFormat("#0.00");
    private final HashMap<Integer, Word> list = new HashMap<>();
    private final ArrayList<String> frenchList = new ArrayList<>();
    private final ArrayList<String> italianList = new ArrayList<>();
    private HashMap<Integer, Word> unknownWordList = new HashMap<>();
    private HashMap<Integer, Word> knownWordList = new HashMap<>();
    private Word currentWord = null;
    @Getter
    private int used = 0, failed = 0, success = 0;

    @Getter
    @Setter
    private int bestStraight = 0, currentStraight = 0;

    public void addWord(Word word) {
        if (checkForDuplicate(word)) {
            list.put(getWordsListSize() + 1, word);
            frenchList.add(word.getFrancais());
            italianList.add(word.getItalien());
        }
    }

    private boolean checkForDuplicate(Word word) {
        if (frenchList.contains(word.getFrancais()) || italianList.contains(word.getItalien())) {
            logger.warn("Word <{}> is duplicate.", word);
            return false;
        }
        return true;
    }

    public Word random() {
        filterWords();

        int j = (((int) (Math.random() * 1000)) % 10) + 1;

        if (null == currentWord && 1 == getUnknownWordsListSize() && unknownWordList.containsValue(currentWord)) {
            j = SPECIAL_WORD;
        }

        if (SPECIAL_WORD == j) {
            Integer i = (((int) (Math.random() * 1000)) % knownWordList.size()) + 1;
            currentWord = knownWordList.get(i);
            logger.info("******** SPECIAL WORD ********");
            currentWord.setSpecialWord(true);
        } else if (0 < getUnknownWordsListSize()) {
            Integer i = (((int) (Math.random() * 1000)) % getUnknownWordsListSize()) + 1;
            currentWord = unknownWordList.get(i);
        } else {
            Integer i = (((int) (Math.random() * 1000)) % getWordsListSize()) + 1;
            currentWord = list.get(i);
        }

        return currentWord;
    }

    public Collection<Word> getWords() {
        return list.values();
    }

    public void updateStat() {
        used = 0;
        failed = 0;
        success = 0;
        for (Word word : getWords()) {
            used += word.getUsed();
            failed += word.getFailed();
            success += word.getSuccess();
        }
    }

    private void filterWords() {
        updateStat();
        unknownWordList = new HashMap<>(list.size());
        knownWordList = new HashMap<>(list.size());

        double exerciseAvg = new ClassicWordFilter().filter(this);
        if (unknownWordList.isEmpty()) {
            new HundredFirstRareWordsFilter().filter(this);
        }
        logger.info("Average usage = {} ({}) / Words in List = {} / Unknown words in List = {}", decimalFormat.format(exerciseAvg), decimalFormat.format(exerciseAvg / 2), getWordsListSize(), getUnknownWordsListSize());
    }

    public void addKnownWord(Word word) {
        knownWordList.put(knownWordList.size() + 1, word);
    }

    public void addUnknownWord(Word word) {
        unknownWordList.put(getUnknownWordsListSize() + 1, word);
    }

    public int getWordsListSize() {
        return list.size();
    }

    public int getUnknownWordsListSize() {
        return unknownWordList.size();
    }

    public void success(boolean realSuccess) {
        currentStraight++;
        if (bestStraight < currentStraight) {
            bestStraight = currentStraight;
        }
        if (realSuccess) {
            currentWord.used();
            currentWord.success();
        }
    }

    public void failed() {
        currentStraight = 0;
        currentWord.used();
        currentWord.failed();
    }
}
