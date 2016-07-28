package sst.stefano.data;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sst.stefano.data.filters.ClassicWordFilter;
import sst.stefano.data.filters.HundredFirstRareWordsFilter;

public class WordList {
    private static final int SPECIAL_WORD = 5;

    private static Logger logger = LoggerFactory.getLogger(WordList.class);

    private HashMap<Integer, Word> list = new HashMap<>();
    private HashMap<Integer, Word> unknownWordList = new HashMap<>();
    private HashMap<Integer, Word> knownWordList = new HashMap<>();
    private ArrayList<String> frenchList = new ArrayList<>();
    private ArrayList<String> italianList = new ArrayList<>();
    private static final DecimalFormat decimalFormat = new DecimalFormat("#0.00");
    private Word currentWord = null;
    @Getter
    private int used = 0, failed = 0, success = 0;

    @Getter
    @Setter
    private int bestStraight = 0, currentStraight = 0;

    public void addWord(Word word) {
        if (checkForDuplicate(word)) {
            list.put(Integer.valueOf(getWordsListSize() + 1), word);
            frenchList.add(word.getFrancais());
            italianList.add(word.getItalien());
        }
    }

    private boolean checkForDuplicate(Word word) {
        if (frenchList.contains(word.getFrancais()) || italianList.contains(word.getItalien())) {
            logger.warn("Word <" + word + "> is duplicate.");
            return false;
        }
        return true;
    }

    public Word random() {
        filterWords();

        Integer j = Integer.valueOf((((int) (Math.random() * 1000)) % 10) + 1);

        if (null == currentWord && 1 == getUnknownWordsListSize() && unknownWordList.containsValue(currentWord)) {
            j = SPECIAL_WORD;
        }

        if (SPECIAL_WORD == j) {
            Integer i = Integer.valueOf((((int) (Math.random() * 1000)) % knownWordList.size()) + 1);
            currentWord = knownWordList.get(i);
            logger.info("******** SPECIAL WORD ********");
            currentWord.setSpecialWord(true);
        } else if (0 < getUnknownWordsListSize()) {
            Integer i = Integer.valueOf((((int) (Math.random() * 1000)) % getUnknownWordsListSize()) + 1);
            currentWord = unknownWordList.get(i);
        } else {
            Integer i = Integer.valueOf((((int) (Math.random() * 1000)) % getWordsListSize()) + 1);
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

        double exerciceAvg = new ClassicWordFilter().filter(this);
        if (0 == unknownWordList.size()) {
            new HundredFirstRareWordsFilter().filter(this);
        }
        logger.info("Average usage = " + decimalFormat.format(exerciceAvg) + " (" + decimalFormat.format(exerciceAvg / 2) + ") / Words in List = " + getWordsListSize() + " / Unknown words in List = "
                + getUnknownWordsListSize());
    }

    public void addKnownWord(Word word) {
        knownWordList.put(Integer.valueOf(knownWordList.size() + 1), word);
    }

    public void addUnknownWord(Word word) {
        unknownWordList.put(Integer.valueOf(getUnknownWordsListSize() + 1), word);
    }

    public int getWordsListSize() {
        return list.size();
    }

    public int getUnknownWordsListSize() {
        return unknownWordList.size();
    }

    public void success() {
        currentStraight++;
        if (bestStraight < currentStraight) {
            bestStraight = currentStraight;
        }
        currentWord.used();
        currentWord.success();
    }

    public void failed() {
        currentStraight = 0;
        currentWord.used();
        currentWord.failed();
    }
}
