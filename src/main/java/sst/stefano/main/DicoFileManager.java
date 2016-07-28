package sst.stefano.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sst.stefano.data.Word;
import sst.stefano.data.WordList;

import com.google.common.collect.Ordering;

public class DicoFileManager {
    private static Logger logger = LoggerFactory.getLogger(DicoFileManager.class);

    private ArrayList<String> ignoredLine = new ArrayList<>();

    public WordList loadFile() {
        WordList wordList = new WordList();
        try {
            backupFile();

            FileInputStream fileInputStream = new FileInputStream(new File(StefanoConstants.getInstance().getDicoFileName()));
            DataInputStream dataInputStream = new DataInputStream(fileInputStream);
            InputStreamReader inputStreamReader = new InputStreamReader(dataInputStream, StandardCharsets.UTF_8);

            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String strLine = null;
            // Read File Line By Line
            while ((strLine = bufferedReader.readLine()) != null) {
                if (!strLine.startsWith("#") && strLine.contains("=")) {
                    wordList.addWord(new Word(strLine));
                } else {
                    // System.out.println("Line ignored = " + strLine);
                    if (!strLine.startsWith(StefanoConstants.TMSTMP_PREFIXE) && !strLine.startsWith(StefanoConstants.RESULT_PREFIXE) && !strLine.startsWith(StefanoConstants.BEST_STRAIGHT)
                            && !strLine.startsWith(StefanoConstants.CURRENT_STRAIGHT)) {
                        if (!strLine.startsWith("#")) {
                            strLine = "#" + strLine;
                        }
                        ignoredLine.add(strLine);
                    }
                    if (strLine.startsWith(StefanoConstants.BEST_STRAIGHT)) {
                        wordList.setBestStraight(Integer.parseInt(strLine.substring(StefanoConstants.BEST_STRAIGHT.length() + 1)));
                    }
                    if (strLine.startsWith(StefanoConstants.CURRENT_STRAIGHT)) {
                        wordList.setCurrentStraight(Integer.parseInt(strLine.substring(StefanoConstants.CURRENT_STRAIGHT.length() + 1)));
                    }
                }
            }

            // Close the input stream
            inputStreamReader.close();
        } catch (Exception e) {// Catch exception if any
            logger.error("Cannot load file " + StefanoConstants.getInstance().getDicoFileName(), e);
            System.exit(-1);
        }
        return wordList;
    }

    public void saveFile(WordList wordList) {
        Ordering<Word> nameOrdering = new Ordering<Word>() {
            public int compare(Word left, Word right) {
                return new Double(left.getStat()).compareTo(new Double(right.getStat()));
                // return left.getFrancais().compareTo(right.getFrancais());
            }
        };

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(StefanoConstants.getInstance().getDicoFileName()));
            DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(dataOutputStream, StandardCharsets.UTF_8);

            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

            printTmsTmp(bufferedWriter);
            printStat(wordList, bufferedWriter);

            for (String line : ignoredLine) {
                bufferedWriter.write(line);
                bufferedWriter.newLine();
            }

            Collection<Word> list = nameOrdering.sortedCopy(wordList.getWords());
            for (Word word : list) {
                bufferedWriter.write(word.toString());
                bufferedWriter.newLine();
            }

            // outputStreamWriter.close();
            bufferedWriter.close();
        } catch (Exception e) {// Catch exception if any
            logger.error("Cannot save file " + StefanoConstants.getInstance().getDicoFileName(), e);
            System.exit(-1);
        }
    }

    private void printStat(WordList wordList, BufferedWriter bufferedWriter) throws IOException {

        bufferedWriter.write(StefanoConstants.RESULT_PREFIXE);
        bufferedWriter.newLine();

        bufferedWriter.write(StefanoConstants.RESULT_PREFIXE + StefanoConstants.NOMBRE_D_EXERCICES + StefanoConstants.decimalNumberFormat.format(wordList.getUsed()));
        bufferedWriter.newLine();
        bufferedWriter.write(StefanoConstants.RESULT_PREFIXE + StefanoConstants.SUCCES + StefanoConstants.decimalNumberFormat.format(wordList.getSuccess()));
        bufferedWriter.newLine();
        bufferedWriter.write(StefanoConstants.RESULT_PREFIXE + StefanoConstants.FAILED + StefanoConstants.decimalNumberFormat.format(wordList.getFailed()));
        bufferedWriter.newLine();
        bufferedWriter.write(StefanoConstants.RESULT_PREFIXE + StefanoConstants.AVERAGE + StefanoConstants.calculateAverage(wordList));
        bufferedWriter.newLine();

        bufferedWriter.write(StefanoConstants.RESULT_PREFIXE);
        bufferedWriter.newLine();
        bufferedWriter.write(StefanoConstants.BEST_STRAIGHT + " " + wordList.getBestStraight());
        bufferedWriter.newLine();
        bufferedWriter.write(StefanoConstants.CURRENT_STRAIGHT + " " + wordList.getCurrentStraight());
        bufferedWriter.newLine();
    }

    private void printTmsTmp(BufferedWriter bufferedWriter) throws IOException {
        bufferedWriter.write(StefanoConstants.TMSTMP_PREFIXE + StefanoConstants.dateFormat.format(new Date()));
        bufferedWriter.newLine();
    }

    private void backupFile() throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");

        String dicoFileName = StefanoConstants.getInstance().getDicoFileName();
        File source = new File(dicoFileName);
        File dest = new File(dicoFileName + "." + sdf.format(new Date()));

        Files.copy(source.toPath(), dest.toPath());
    }

}
