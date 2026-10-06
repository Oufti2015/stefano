package sst.stefano.main;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sst.stefano.data.WordList;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Properties;

public class StefanoConstants {
    public static final String STEFANO_V_1_0 = "Stefano v 2026.001";
    public static final String TMSTMP_PREFIX = "#TMSTMP ";
    public static final String RESULT_PREFIX = "#RESULT ";
    public static final String AVERAGE = "Moyenne              ";
    public static final String FAILED = "Erreurs              ";
    public static final String SUCCESS = "Succès               ";
    public static final String EXERCISES_COUNT = "Nombre d'exercices   ";
    public static final DecimalFormat decimalNumberFormat = new DecimalFormat("#,##0");
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
    public static final DecimalFormat decimalPercentFormat = new DecimalFormat("#0.00 %");
    public static final String BEST_STRAIGHT = "#BEST_STRAIGHT";
    public static final String CURRENT_STRAIGHT = "#CURRENT_STRAIGHT";
    public static final String IMG_ITALY_FLAG_ICON_24_PNG = "Img/Italy-Flag-icon-24.png";
    private static final String CONFIG_PROPERTIES = "Stefano.properties";
    private static final Logger logger = LoggerFactory.getLogger(StefanoConstants.class);
    private static final String DICO_FILE_NAME = "data/dico.txt2";
    private static final int STRAIGHT_PIVOT = 10;
    @Getter
    private final static StefanoConstants INSTANCE = new StefanoConstants();
    private final Properties prop = new Properties();

    @Getter
    @Setter
    private String dicoFileName = DICO_FILE_NAME;
    @Getter
    @Setter
    private int straightPivot = STRAIGHT_PIVOT;

    private StefanoConstants() {
        try {
            String propertyFile = System.getenv().getOrDefault("PROPERTIES_FILE", CONFIG_PROPERTIES);
            InputStream input = new FileInputStream(propertyFile);
            prop.load(input);

            init();
        } catch (IOException e) {
            logger.error("Cannot load property file <" + CONFIG_PROPERTIES + ">", e);
        }
    }

    public static String calculateAverage(WordList wordList) {
        return StefanoConstants.decimalPercentFormat.format(((double) wordList.getSuccess() / (double) wordList.getUsed()));
    }

    private void init() {
        String property = prop.getProperty("dico.filename");
        if (null != property) {
            dicoFileName = property;
            logger.info("Loading Dico file <{}>", dicoFileName);
        }
        property = prop.getProperty("straight.pivot");
        if (null != property) {
            straightPivot = Integer.parseInt(property);
            logger.info("Straight Pivot <{}>", straightPivot);
        }
    }
}
