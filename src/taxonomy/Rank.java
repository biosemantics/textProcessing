/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package taxonomy;

import common.utils.RegExUtil;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author iychoi
 */
public class Rank {

    private static List<String> rankPredefined = new ArrayList<String>();
    
    static {
        initializeRankPredefined();
    }
    
    public static String getRankFromNameInfo(String name, TaxonomyConfiguration conf) {
        return findRank(name, conf);
    }

    private static void initializeRankPredefined() {
        if (rankPredefined.isEmpty()) {
            rankPredefined.add("Phylum");
            rankPredefined.add("Subphylum");
            rankPredefined.add("Class");
            rankPredefined.add("Subclass");
            rankPredefined.add("Order");
            rankPredefined.add("Suborder");
            rankPredefined.add("Superfamily");
            rankPredefined.add("Family");
            rankPredefined.add("Subfamily");
            rankPredefined.add("Tribe");
            rankPredefined.add("Genus");
            rankPredefined.add("Subgenera");
            rankPredefined.add("Subgenus");
            rankPredefined.add("Species");
        }
    }

    public static String[] getPredefinedRanks() {
        String[] arr = new String[rankPredefined.size()];
        
        arr = rankPredefined.toArray(arr);
        return arr;
    }
    
    private static String findRank(String name, TaxonomyConfiguration conf) {
        String rankString = RegExUtil.getFirstWord(name);
        
        if(rankString == null) {
            return conf.getRankDefault();
        }

        for (String rankDefined : rankPredefined) {
            if (rankDefined.trim().equalsIgnoreCase(rankString.trim())) {
                return rankDefined;
            }
        }

        // default
        return conf.getRankDefault();
    }
}
