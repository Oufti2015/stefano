package sst.stefano.main;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import sst.stefano.data.Word;
import sst.stefano.data.WordList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DicoFileManagerTest {
    private final StefanoConstants constants = StefanoConstants.getINSTANCE();

    @TempDir
    Path temporaryDirectory;
    private String originalDicoFileName;

    @BeforeEach
    void useTemporaryDictionary() {
        originalDicoFileName = constants.getDicoFileName();
        constants.setDicoFileName(temporaryDirectory.resolve("dictionary.txt").toString());
    }

    @AfterEach
    void restoreDictionaryPath() {
        constants.setDicoFileName(originalDicoFileName);
    }

    @Test
    void loadFileReadsWordsAndStreaksAndSavePreservesRelevantComments() throws IOException {
        Path dictionary = Path.of(constants.getDicoFileName());
        Files.write(dictionary, List.of(
                "# user comment",
                StefanoConstants.TMSTMP_PREFIX + "old timestamp",
                StefanoConstants.RESULT_PREFIX + "old statistics",
                StefanoConstants.BEST_STRAIGHT + " 8",
                StefanoConstants.CURRENT_STRAIGHT + " 3",
                "plain note",
                "ami=amico/3/1/2/1",
                "merci=grazie/4/0/4/4"
        ));

        DicoFileManager manager = new DicoFileManager();
        WordList wordList = manager.loadFile();

        assertEquals(2, wordList.getWordsListSize());
        assertEquals(8, wordList.getBestStraight());
        assertEquals(3, wordList.getCurrentStraight());
        try (var files = Files.list(temporaryDirectory)) {
            assertTrue(files.anyMatch(path -> path.getFileName().toString().startsWith("dictionary.txt.")));
        }

        wordList.updateStat();
        manager.saveFile(wordList);

        List<String> savedLines = Files.readAllLines(dictionary);
        assertTrue(savedLines.get(0).startsWith(StefanoConstants.TMSTMP_PREFIX));
        assertTrue(savedLines.contains("# user comment"));
        assertTrue(savedLines.contains("#plain note"));
        assertFalse(savedLines.stream().anyMatch(line -> line.startsWith(StefanoConstants.TMSTMP_PREFIX)
                && !line.equals(savedLines.get(0))));
        assertTrue(savedLines.contains(StefanoConstants.BEST_STRAIGHT + " 8"));
        assertTrue(savedLines.contains(StefanoConstants.CURRENT_STRAIGHT + " 3"));
        assertTrue(savedLines.contains("ami=amico/3/1/2/1"));
        assertTrue(savedLines.contains("merci=grazie/4/0/4/4"));
    }

    @Test
    void saveFileWritesSummaryAndSortsWordsBySuccessRate() throws IOException {
        WordList wordList = new WordList();
        wordList.addWord(new Word("high=alto/10/1/9/2"));
        wordList.addWord(new Word("low=basso/10/5/5/1"));
        wordList.setBestStraight(6);
        wordList.setCurrentStraight(2);
        wordList.updateStat();

        new DicoFileManager().saveFile(wordList);

        List<String> savedLines = Files.readAllLines(Path.of(constants.getDicoFileName()));
        assertTrue(savedLines.stream().anyMatch(line ->
                line.equals(StefanoConstants.RESULT_PREFIX + StefanoConstants.EXERCISES_COUNT + "20")));
        assertTrue(savedLines.stream().anyMatch(line ->
                line.equals(StefanoConstants.RESULT_PREFIX + StefanoConstants.SUCCESS + "14")));
        assertTrue(savedLines.contains(StefanoConstants.BEST_STRAIGHT + " 6"));
        assertTrue(savedLines.contains(StefanoConstants.CURRENT_STRAIGHT + " 2"));
        assertTrue(savedLines.indexOf("low=basso/10/5/5/1")
                < savedLines.indexOf("high=alto/10/1/9/2"));
    }
}
