package sst.stefano.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Properties;

import lombok.Getter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sst.stefano.data.WordList;

public class StefanoConstants {
    private static final String CONFIG_PROPERTIES = "Stefano.properties";

    private static Logger logger = LoggerFactory.getLogger(StefanoConstants.class);

    public static final String STEFANO_V_1_0 = "Stefano v 1.6";

    public static final String TMSTMP_PREFIXE = "#TMSTMP ";
    public static final String RESULT_PREFIXE = "#RESULT ";
    public static final String AVERAGE = "Moyenne              ";
    public static final String FAILED = "Erreurs              ";
    public static final String SUCCES = "Succès               ";
    public static final String NOMBRE_D_EXERCICES = "Nombre d'exercices   ";
    public static final DecimalFormat decimalNumberFormat = new DecimalFormat("#,##0");
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
    public static final DecimalFormat decimalPercentFormat = new DecimalFormat("#0.00 %");
    public static final String BEST_STRAIGHT = "#BEST_STRAIGHT";
    public static final String CURRENT_STRAIGHT = "#CURRENT_STRAIGHT";
    public static final String IMG_ITALY_FLAG_ICON_24_PNG = "Img/Italy-Flag-icon-24.png";

    private static final String DICO_FILE_NAME = "data/dico.txt2";
    private static final int STRAIGHT_PIVOT = 10;

    public static String calculateAverage(WordList wordList) {
        return StefanoConstants.decimalPercentFormat.format(((double) wordList.getSuccess() / (double) wordList.getUsed()));
    }

    private Properties prop = new Properties();
    private InputStream input = null;

    @Getter
    private String dicoFileName = DICO_FILE_NAME;
    @Getter
    private int straightPivot = STRAIGHT_PIVOT;

    @Getter
    private static StefanoConstants instance = null;

    static {
        instance = new StefanoConstants();
    }

    private StefanoConstants() {
        try {
            input = new FileInputStream(CONFIG_PROPERTIES);
            prop.load(input);

            init();
        } catch (IOException e) {
            logger.error("Cannot load property file <" + CONFIG_PROPERTIES + ">", e);
        }
    }

    private void init() {
        String property = prop.getProperty("dico.filename");
        if (null != property) {
            dicoFileName = property;
        }
        property = prop.getProperty("straight.pivot");
        if (null != property) {
            straightPivot = Integer.parseInt(property);
        }
    }
}
