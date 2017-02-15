/**
 * Created by caihengyi on 2017/2/15.
 */

import java.util.HashMap;
import java.util.Map;

/**
 * Read all text files under a directory, and generate the TF-IDF scores
 * of each term in each file to a output file( " tf_idf_output.txt" for default)
 * <p>
 * This is a command-line application, run it with no command-line arguments for
 * usage information.
 */
public class GenTFIDF {
    public static void main(String[] args){
        String usage = "java GenTFIDF"
                + " -docs DOCS_PATH [-o OUTPUT_FILE] [-e TEXT_FILE_ENCODING] \n\n"
                + "This read all text files under a directory, and generate the TF-IDF scores"
                + " of each term in each file to a output file(\"tf_idf_output.txt\" for default),"
                + " default encoding is UTF-8";
        String indexPath = "./index";
        String docsPath = null;
        String outFilePath = "./tf_idf_output.txt";
        String encoding = "UTF-8";
        for (int i = 0; i < args.length; i++) {
            if("-docs".equals(args[i])){
                docsPath = args[i+1];
                i++;
            }else if("-o".equals(args[i])){
                outFilePath = args[i+1];
                i++;
            }else if("-e".equals(args[i])){
                encoding = args[i+1];
                i++;
            }
        }
        if(docsPath == null){
            System.err.println("Usage: " + usage);
            System.exit(1);
        }

        WordVector wordVector = new WordVector(indexPath,docsPath,true,encoding);
        Map<String, HashMap> documentsScores = wordVector.TFIDFScore();
        Utils.write2DMapToFile(outFilePath,documentsScores);
    }

}
