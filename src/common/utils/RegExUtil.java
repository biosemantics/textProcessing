/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import output.taxonomy.Rank;

/**
 *
 * @author iychoi
 */
public class RegExUtil {
    
    private static final String FIGURE_TABLE_CAPTION  = "^(Figure|Fig|Table|Tbl|Pic|Image|Picture)\\s+\\d+(-\\d+)?\\.?\\s.*?";
    
    private static final String KEY_STATEMENT_INDEX = "(—\\s|\\d+\\(\\d+\\)|\\d+)(\\s\\[triplet\\])?\\.\\s";//
    private static final String KEY_STATEMENT = "^" + KEY_STATEMENT_INDEX + ".+";
            
    public static boolean isOneCharOrNum(String text) {
        return text.matches("^\\d+|\\w$");
    }
    
    public static int getWordCount(String text) {
        String EXPRESSION = "\\s+";
        Pattern pattern = Pattern.compile(EXPRESSION);
        Matcher matcher = pattern.matcher(text.trim());

        int wordCount = 0;
        while(matcher.find()) {
            wordCount++;
        }
        return wordCount;
    }
    
    public static boolean isFigTblCaption(String text) {
	if (text.matches(FIGURE_TABLE_CAPTION)) {
            return true;
        }
        return false;
    }
    
    public static boolean isKeyStatement(String text) {
	try {
            Pattern p = Pattern.compile(KEY_STATEMENT);
            Matcher mt = p.matcher(text);
            if (mt.matches()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static boolean isTaxonomyName(String text, int maxWordLen) {
        if(getWordCount(text) <= maxWordLen) {
            String taxonRankExp = "";
            String[] ranks = Rank.getPredefinedRanks();
            for(int i=0;i<ranks.length;i++) {
                taxonRankExp += ranks[i];
                if(i < ranks.length - 1) {
                    taxonRankExp += "|";
                }
            }
            
            /**
             * taxon name: include rank and name style 1: rank + name (not in
             * this doc)style 2: rank + genus name + species name (only in this
             * doc)style 3: genus name / Subgenus + name
             *
             * In general: rank? name (\\s/\\sSubgenus\\s)?
             */
            // type 1
            Pattern p1 = Pattern.compile("(" + taxonRankExp + ")\\s+([A-Z][a-z]+)");
            Matcher mt1 = p1.matcher(text);
            if(mt1.matches()) {
                return true;
            }
            // type 2
            Pattern p2 = Pattern.compile("(" + taxonRankExp + ")\\s+([A-Z][a-z]+)\\s+([A-Z][a-z]+)");
            Matcher mt2 = p2.matcher(text);
            if(mt2.matches()) {
                return true;
            }
            // type 3
            Pattern p3 = Pattern.compile("([A-Z][a-z]+)\\s+/\\s+Subgenus\\s+([A-Z][a-z]+)");
            Matcher mt3 = p3.matcher(text);
            if(mt3.matches()) {
                return true;
            }
            // type 4
            Pattern p4 = Pattern.compile("([A-Z][a-z]+)\\s+[a-z]+\\s([A-Z][a-z]+)");
            Matcher mt4 = p4.matcher(text);
            if(mt4.matches()) {
                return true;
            }
            // type 5
            Pattern p5 = Pattern.compile("([A-Z][a-z]+)\\s+[a-z]+\\s[a-z]+\\s([A-Z][a-z]+)");
            Matcher mt5 = p5.matcher(text);
            if(mt5.matches()) {
                return true;
            }
        }
        
        return false;
    }
    
    public static boolean isSynonym(String line, int maxWordLen) {
        String braceRemoved = StringUtil.removeBraceComments(line);
        // type 1
        Pattern p1 = Pattern.compile("([\\w\\s]+)\\s\\d+\\s?:\\s?\\d+\\s?(-\\s?\\d+)?");
        Matcher mt1 = p1.matcher(braceRemoved);
        if(mt1.find()) {
            String text = mt1.group(1);
            if(isTaxonomyName(text, maxWordLen)) {
                return true;
            }
            return false;
        }
        
        return false;
    }
    
    public static String fixMissedSpace(String text) {
        String rv = text;
        Pattern p = Pattern.compile("^.+\\w{2,}([a-z][A-Z]).+$");
        Matcher mt = p.matcher(text);
        if (mt.matches()) {
            String toFix = mt.group(1);
            String theFix = toFix.substring(0, 1) + " " + toFix.substring(1);
            rv = text.replaceAll(toFix, theFix);
        }
        return rv;
    }
    
    public static String getFirstWord(String text) {
        Pattern p = Pattern.compile("(\\w+).*");
        Matcher mt = p.matcher(text);

        String firstWord = null;
        if(mt.matches()) {
            firstWord = mt.group(1);
        }
        
        return firstWord;
    }
    
    public static String removeIndexNumber(String text) {
        Pattern p = Pattern.compile("^(\\d+\\.\\s?).+$");
        Matcher mt = p.matcher(text);
        if (mt.matches()) {
            return text.substring(text.indexOf(".") + 1, text.length());
        }
        return text;
    }

    public static String removeTrailNumber(String text) {
        String EXPRESSION = "^([\\w\\s]+)(,\\s?\\d+)$";
        Pattern p = Pattern.compile(EXPRESSION);
        Matcher mt = p.matcher(text);
        if(mt.matches()) {
            return mt.group(1);
        }
        return text;
    }
}
