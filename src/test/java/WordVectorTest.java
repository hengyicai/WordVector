import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;

import static org.junit.Assert.*;

/**
 * Created by caihengyi on 2017/2/11.
 */
public class WordVectorTest {
    private WordVector wordVector;

    @org.junit.Before
    public void setUp() throws Exception {
        String indexPath = "C:\\Temp\\index";
        String docsPath = "C:\\Users\\Lisa\\Desktop\\Test_data";
        wordVector = new WordVector(indexPath, docsPath, true, "UTF-8");
    }

    @org.junit.Test
    public void TFIDFScore() throws Exception {
        Map<String, HashMap> documentsScores = wordVector.TFIDFScore();
        Assert.assertNotNull(documentsScores);
        for (Map.Entry<String, HashMap> entry : documentsScores.entrySet()) {
            System.out.println("------------------" + entry.getKey() + "---------------------");
            Map<String, Float> termsScore = entry.getValue();
           
            termsScore = Utils.sortByValue(termsScore, false);
            for (Map.Entry<String, Float> termWithTFIDF : termsScore.entrySet()) {
                System.out.println(termWithTFIDF.getKey() + "-->" + termWithTFIDF.getValue());
            }
        }
    }

}