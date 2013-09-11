/*
 * Correct illegal space by using hint
 */
package djvu.correction;

import common.utils.RegExUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import common.utils.StreamUtil;
import common.utils.StringUtil;

/**
 *
 * @author iychoi
 */
public class SpaceCorrector {
    
    private Map<String, String> lookupTable;
    
    public SpaceCorrector(File textFile) {
        this.lookupTable = new Hashtable<String, String>();
        
        generateLookupTable(textFile);
    }
    
    public SpaceCorrector(Hashtable<String, String> lookupTable) {
        this.lookupTable = lookupTable;
    }
    
    public SpaceCorrector() {
        this.lookupTable = new Hashtable<String, String>();
    }
    
    private void generateLookupTable(File file) {
        try {
            BufferedReader rd = StreamUtil.getReader(file);
            
            String line = "";
            while ((line = rd.readLine()) != null) {
                    String l = line.trim();
                    if (!l.equals("")) {
                            addLookupTableEntry(l);
                    }
            }
            rd.close();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
    
    public void addLookupTableEntry(String str) {
        // original line
        this.lookupTable.put(str.replaceAll("\\s", ""), str);

        // drop last word and compare
        String[] lastdrop = StringUtil.splitLastWord(str);
        if(lastdrop != null) {
            this.lookupTable.put(lastdrop[0].replaceAll("\\s", ""), lastdrop[0]);
        }
        
        // drop the first word and compare
        String[] firstdrop = StringUtil.splitFirstWord(str);
        if(firstdrop != null) {
            this.lookupTable.put(firstdrop[1].replaceAll("\\s", ""), firstdrop[1]);
        }
        
        // drop the first and the last word and compare
        String[] bothdrop = StringUtil.splitFirstAndLastWord(str);
        if(bothdrop != null) {
            this.lookupTable.put(bothdrop[1].replaceAll("\\s", ""), bothdrop[1]);
        }
    }
    
    private String correctSpace_(String str) {
        // original compare
        // remove white space
        String key = str.replaceAll("\\s", "");
        String fixed_txt = this.lookupTable.get(key);
        if (fixed_txt != null) {
            if (fixed_txt.length() < str.length()) {
                return fixed_txt;
            } else {
                return str;
            }
        }
        
        return null;
    } 
    
    public String correctWrongSpace(String str) {
        String originalCorrect = correctSpace_(str);
        if(originalCorrect != null)
            return originalCorrect;

        // drop last word and compare
        String[] lastdrop = StringUtil.splitLastWord(str);
        if(lastdrop != null) {
            String lastdropCorrect = correctSpace_(lastdrop[0]);
            if(lastdropCorrect != null) 
                return lastdropCorrect + " " + lastdrop[1];
        }
        
        // drop the first word and compare
        String[] firstdrop = StringUtil.splitFirstWord(str);
        if(firstdrop != null) {
            String firstdropCorrect = correctSpace_(firstdrop[1]);
            if(firstdropCorrect != null)
                return firstdrop[0] + " " + firstdropCorrect;
        }
        
        // drop the first and the last word and compare
        String[] bothdrop = StringUtil.splitFirstAndLastWord(str);
        if(bothdrop != null) {
            String bothdropCorrect = correctSpace_(bothdrop[1]);
            if(bothdropCorrect != null)
                return bothdrop[0] + " " + bothdropCorrect + " " + bothdrop[2];
        }
        
        return str;
    }
    
    public String correctMissedSpace(String str) {
        return RegExUtil.fixMissedSpace(str);
    }
    
    public String correct(String str) {
        //OCR somtimes add wrong spaces
        String correct1Str = correctWrongSpace(str);
        if(!correct1Str.equals(str)) {
            System.err.println("Space fixed : ");
            System.err.println("From : " + str);
            System.err.println("To : " + correct1Str);
        }
        
        //sometimes space is missing between two words through copied text
        String correct2Str = correctMissedSpace(correct1Str);
        if(!correct2Str.equals(correct1Str)) {
            System.err.println("Space fixed : ");
            System.err.println("From : " + correct1Str);
            System.err.println("To : " + correct2Str);
        }
        
        return correct2Str;
    }
}
