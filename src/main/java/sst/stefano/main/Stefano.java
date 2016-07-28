package sst.stefano.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sst.stefano.data.Word;
import sst.stefano.data.WordList;

public class Stefano extends Application {

    private static final int RIGHT = 1;

    private static final int LEFT = 0;

    private static Logger logger = LoggerFactory.getLogger(Stefano.class);

    private Label fromLabel = new Label("Français");
    private Label toLabel = new Label("Italien");
    private Label resultLabel = new Label("");
    private Label fromTextLabel = new Label();
    private TextField toTextField = new TextField();
    private Label fromSolutionLabel = new Label();
    private Label yourAnswerLabel = new Label();
    private Label toSolutionLabel = new Label();
    private Label exercicesLabel = new Label();
    private Label successLabel = new Label();
    private Label failedLabel = new Label();
    private Label averageLabel = new Label();
    private Label wordStatLabel = new Label();
    private Label prevWordStatLabel = new Label();
    private Label currentStraightLabel = new Label();
    private Label bestStraightLabel = new Label();

    private Button checkButton = new Button("Vérifier");

    private Label wordsInDicoLabel = new Label();
    private ProgressIndicator progressBar = new ProgressIndicator();
    private Label unknownWordsInDico = new Label();

    private WordList wordList = new WordList();
    private Word currentWord = null;

    private DicoFileManager dicoFileManager = new DicoFileManager();

    private boolean proposeSameWord = false;

    private ObservableList<Word> lastFiveWordsList = null;

    @Override
    public void start(Stage primaryStage) throws Exception {
        int windowWidth = 800;
        int windowHeight = 1024;

        configureLogger();
        wordList = dicoFileManager.loadFile();

        // set icon
        primaryStage.getIcons().add(getImage(StefanoConstants.IMG_ITALY_FLAG_ICON_24_PNG));

        // set title
        primaryStage.setTitle(StefanoConstants.STEFANO_V_1_0);

        logger.info(StefanoConstants.STEFANO_V_1_0);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Scene scene = new Scene(grid, windowWidth, windowHeight);
        primaryStage.setScene(scene);

        // scene.getStylesheets().add("stefano.css");
        scene.getStylesheets().add(Stefano.class.getResource("stefano.css").toExternalForm());

        init(grid);

        primaryStage.show();

        updateStat();
        proposeAWord();
    }

    private void configureLogger() throws IOException {
        // Handler handler = new FileHandler("logs/Stefano.log.%g", 1024 * 1024
        // * 1024, 10, true);
        // handler.setFormatter(new SimpleFormatter());
        // logger.addHandler(handler);
    }

    private void init(GridPane grid) {

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(35);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPercentWidth(5);
        ColumnConstraints col4 = new ColumnConstraints();
        col3.setPercentWidth(10);
        grid.getColumnConstraints().addAll(col1, col2, col3, col4);

        fromTextLabel.setId("from-label");
        toTextField.setId("input-textfield");
        toSolutionLabel.setId("result-textfield");

        createGrid(grid);

        checkButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                check();
            }
        });

        toTextField.addEventFilter(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {
            public void handle(KeyEvent t) {
                if ("\r".equals(t.getCharacter())) {
                    check();
                }
            }
        });

        toTextField.requestFocus();
    }

    private void createGrid(GridPane grid) {
        int i = 0;

        addLine(grid, i++, fromLabel, toLabel);
        addLine(grid, i++, fromTextLabel, toTextField);
        addLine(grid, i++, "Stat", wordStatLabel);

        grid.add(new Separator(), LEFT, i++, 2, 1);

        addLine(grid, i++, checkButton, resultLabel);
        addLine(grid, i++, "En français", fromSolutionLabel);
        addLine(grid, i++, "Votre réponse", yourAnswerLabel);
        addLine(grid, i++, "En italien", toSolutionLabel);
        addLine(grid, i++, "Stat", prevWordStatLabel);

        grid.add(new Separator(), LEFT, i++, 2, 1);

        addLine(grid, i++, StefanoConstants.NOMBRE_D_EXERCICES, configureStatLabel(exercicesLabel));
        addLine(grid, i++, StefanoConstants.SUCCES, configureStatLabel(successLabel));
        addLine(grid, i++, StefanoConstants.FAILED, configureStatLabel(failedLabel));
        addLine(grid, i++, StefanoConstants.AVERAGE, configureStatLabel(averageLabel));
        addLine(grid, i++, "Suite", configureStatLabel(currentStraightLabel));
        addLine(grid, i++, "Meilleur Suite", configureStatLabel(bestStraightLabel));

        Separator sep = new Separator();
        sep.setOrientation(Orientation.VERTICAL);
        grid.add(sep, 2, 0, 1, i);

        grid.add(wordsInDicoLabel, 3, 0, 1, 1);
        grid.add(progressBar, 3, RIGHT, 1, i - 2);
        grid.add(unknownWordsInDico, 3, i - 1, 1, 1);

        grid.add(createTable(), 0, i, 4, 1);
    }

    // final ObservableList<Word> data =
    // FXCollections.observableArrayList(wordList.random(), wordList.random(),
    // wordList.random(), wordList.random(), wordList.random());

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Node createTable() {
        int textWidth = 130, intWidth = 15, percentWidth = 130;

        TableColumn francaisCol = new TableColumn();
        francaisCol.setText("Français");
        francaisCol.setMinWidth(textWidth);
        francaisCol.setCellValueFactory(new PropertyValueFactory("francais"));

        TableColumn italienCol = new TableColumn();
        italienCol.setText("Italien");
        italienCol.setMinWidth(textWidth);
        italienCol.setCellValueFactory(new PropertyValueFactory("italien"));

        TableColumn usedCol = new TableColumn();
        usedCol.setText("Joués");
        usedCol.setMinWidth(intWidth);
        usedCol.setCellValueFactory(new PropertyValueFactory("used"));

        TableColumn failedCol = new TableColumn();
        failedCol.setText("Ratés");
        failedCol.setMinWidth(intWidth);
        failedCol.setCellValueFactory(new PropertyValueFactory("failed"));

        TableColumn successCol = new TableColumn();
        successCol.setText("Succès");
        successCol.setMinWidth(intWidth);
        successCol.setCellValueFactory(new PropertyValueFactory("success"));

        TableColumn straightCol = new TableColumn();
        straightCol.setText("Suite");
        straightCol.setMinWidth(intWidth);
        straightCol.setCellValueFactory(new PropertyValueFactory("straight"));

        TableColumn statCol = new TableColumn();
        statCol.setText("Stat");
        statCol.setMinWidth(percentWidth);
        statCol.setCellValueFactory(new PropertyValueFactory("wordStat"));

        lastFiveWordsList = FXCollections.observableArrayList();
        TableView tableView = new TableView();
        tableView.setEditable(false);
        tableView.setItems(lastFiveWordsList);

        tableView.getColumns().addAll(francaisCol, italienCol, usedCol, failedCol, successCol, straightCol, statCol);
        return tableView;
    }

    private void addLine(GridPane grid, int i, String leftNodeText, Node rightNode) {
        addLine(grid, i, new Label(leftNodeText), rightNode);
    }

    private void addLine(GridPane grid, int i, Node leftNode, Node rightNode) {
        grid.add(leftNode, LEFT, i);
        grid.add(rightNode, RIGHT, i);
        RowConstraints rc = new RowConstraints();
        rc.setPrefHeight(30);
        grid.getRowConstraints().add(rc);
    }

    private Node configureStatLabel(Label label) {
        label.setId("stat-label");
        // label.setPrefWidth(200);
        return label;
    }

    private void updateStat() {
        wordList.updateStat();

        exercicesLabel.setText(StefanoConstants.decimalNumberFormat.format(wordList.getUsed()));
        successLabel.setText(StefanoConstants.decimalNumberFormat.format(wordList.getSuccess()));
        failedLabel.setText(StefanoConstants.decimalNumberFormat.format(wordList.getFailed()));
        averageLabel.setText(calculateAverage());
        currentStraightLabel.setText("" + wordList.getCurrentStraight());
        bestStraightLabel.setText("" + wordList.getBestStraight());
    }

    private double getProgress() {
        return ((double) wordList.getUnknownWordsListSize()) / ((double) wordList.getWordsListSize());
    }

    private String calculateAverage() {
        return StefanoConstants.decimalPercentFormat.format(((double) wordList.getSuccess() / (double) wordList.getUsed()));
    }

    private void proposeAWord() {
        Word newWord = null;
        if (null == currentWord || !proposeSameWord) {
            do {
                newWord = wordList.random();
            } while (1 < wordList.getUnknownWordsListSize() && newWord.equals(currentWord));
            currentWord = newWord;
        }

        if (currentWord.isSpecialWord()) {
            fromTextLabel.setId("from-special-label");
        } else {
            fromTextLabel.setId("from-label");
        }
        fromTextLabel.setText(currentWord.getFrancais());
        initToTextField(currentWord.getItalien());
        wordStatLabel.setText("" + currentWord.getWordStat());
        updateProgressBar();
        proposeSameWord = false;

        int index = lastFiveWordsList.indexOf(currentWord);
        if (-1 != index) {
            lastFiveWordsList.remove(index);
        }
    }

    private void initToTextField(String italian) {
        int indexOf = italian.indexOf(" ");
        if (-1 == indexOf) {
            indexOf = italian.indexOf("'");
        }
        toTextField.setText("");

        if (-1 != indexOf) {
            String article = italian.substring(0, indexOf + 1);
            toTextField.setText(article);
            toTextField.positionCaret(article.length());
        }
    }

    private void updateProgressBar() {
        wordsInDicoLabel.setText(StefanoConstants.decimalNumberFormat.format(wordList.getWordsListSize()));
        progressBar.setProgress(getProgress());
        unknownWordsInDico.setText(StefanoConstants.decimalNumberFormat.format(wordList.getUnknownWordsListSize()));
    }

    protected void check() {
        if (currentWord.getItalien().equalsIgnoreCase(toTextField.getText())) {
            resultLabel.setText("OK !");
            resultLabel.setId("result-textfield-ok");
            wordList.success();
            logger.info(" OK     : " + currentWord);
            updateLastWords();
        } else {
            proposeSameWord = true;
            resultLabel.setText("ERREUR !");
            resultLabel.setId("result-textfield-failed");
            wordList.failed();

            logger.info("ERREUR : " + currentWord);
            logger.info("Solution : " + currentWord.getItalien());
            logger.info("Answer   : " + toTextField.getText());
        }

        prevWordStatLabel.setText(currentWord.getWordStat());

        updateStat();

        fromSolutionLabel.setText(currentWord.getFrancais());
        toSolutionLabel.setText(currentWord.getItalien());
        yourAnswerLabel.setText(toTextField.getText());

        dicoFileManager.saveFile(wordList);

        proposeAWord();
    }

    private void updateLastWords() {
        int index = lastFiveWordsList.indexOf(currentWord);
        try {
            lastFiveWordsList.remove((-1 == index) ? 9 : index);
        } catch (IndexOutOfBoundsException e) {
            // nothig to do
        }
        lastFiveWordsList.add(0, currentWord);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }

    private Image getImage(String filename) {
        File file = null;
        Image image = null;
        try {
            file = new File(filename);
            FileInputStream fileInputStream = new FileInputStream(file);
            image = new Image(fileInputStream, 30, 30, true, true);
        } catch (FileNotFoundException e) {
            logger.error("Cannot load image file <" + filename + ">, file=" + file, e);
            System.exit(-RIGHT);
        }
        return image;
    }
}
