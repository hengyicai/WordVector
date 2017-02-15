import java.io.*;
import java.util.*;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

/**
 * Created by caihengyi on 2017/2/11.
 */

/**
 * Some util function used by the project
 */
public class Utils {

    /**
     * Reading text contents from the given FileInputStream fis according to the specific encoding(e.g. UTF-8).
     *
     * @param file      FileInputStream of certain txt file
     * @param encoding The name of a supported {@link java.nio.charset.Charset charset}
     * @return contents
     */
    public static String readContents(File file, String encoding) {
    	Date start = new Date();
    	System.out.println("reading start...");
    	StringBuilder data = new StringBuilder();
    	LineIterator it = null;
    	try {
			it = FileUtils.lineIterator(file,encoding);
			while(it.hasNext()){
				data.append(it.nextLine()).append(" ");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			LineIterator.closeQuietly(it);
		}
    	Date end = new Date();
		System.out.println(
				"reading end! The reading process used " + (end.getTime() - start.getTime()) + " total milliseconds");
    	return data.toString();
    }

    /**
     * Word counter
     *
     * @param line The string line
     * @return number of words in this line
     */
    public int wordCount(String line) {
        int numWords = 0;
        int index = 0;
        boolean prevWhiteSpace = true;
        while (index < line.length()) {
            char c = line.charAt(index++);
            boolean currWhiteSpace = Character.isWhitespace(c);
            if (prevWhiteSpace && !currWhiteSpace) {
                numWords++;
            }
            prevWhiteSpace = currWhiteSpace;
        }
        return numWords;
    }

    /**
     * Sort the entry in map
     *
     * @param map  the map needed to be sorted
     * @param <K>  K
     * @param <V>  V
     * @param flag if the flag is true, sort the entry in ascending order, otherwise n descending order
     * @return the sorted map
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map, final boolean flag) {
        List<Map.Entry<K, V>> list =
                new LinkedList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                int res = (o1.getValue()).compareTo(o2.getValue());
                return flag ? res : -res;
            }
        });

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
    
    /**
     * Write the 2D Map to the given file
     * @param filePath path of the file which will write to
     * @param m the 2D Map
     * @return boolean flag to indicate success or fail
     */
    public static boolean write2DMapToFile(String filePath, Map<String,HashMap> m){
    	StringBuilder data = new StringBuilder("");
    	if(m == null || m.size() == 0) return false;
    	for(Map.Entry<String, HashMap> outterEntry:m.entrySet()){
    		String outterKey = outterEntry.getKey();
    		Map<String,Float> nestedMap = Utils.sortByValue(outterEntry.getValue(),false);
    		data.append(outterKey).append("\r\n");
    		for(Map.Entry<String, Float> innerEntry:nestedMap.entrySet()){
    			String innerKey = innerEntry.getKey();
    			Float innerValue = innerEntry.getValue();
    			data.append(innerKey).append("\t-->\t").append(innerValue.toString()).append("\r\n");
    		}
    		data.append("\r\n");
    	}
    	try {
    		
			FileUtils.writeStringToFile(new File(filePath), data.toString());
			return true;
		} catch (IOException e) {
			
			e.printStackTrace();
		}
    	return false;
    }

}
