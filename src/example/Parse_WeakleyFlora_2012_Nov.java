/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package example;

import static common.utils.RegExUtil.removeTrailDot;
import common.utils.StringUtil;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import taxonomy.Taxonomy;
import taxonomy.bean.TaxonomyDiscussion;
import taxonomy.bean.TaxonomyGenericElement;
import taxonomy.bean.TaxonomyKeyFile;
import taxonomy.bean.TaxonomyMeta;
import taxonomy.bean.TaxonomyNomenclature;
import taxonomy.key.KeyTo;
import taxonomy.key.bean.KeyHeading;
import taxonomy.key.bean.KeyStatement;

/**
 *
 * @author iychoi
 */
public class Parse_WeakleyFlora_2012_Nov {
    
    private static boolean isFootnote(XWPFParagraph para) {
        boolean isFootnote = false;
        if (para.getCTP() != null) {
            if (para.getCTP().getPPr() != null) {
                if (para.getCTP().getPPr().getPStyle() != null) {
                    if (para.getCTP().getPPr().getPStyle().getVal()
                            .contains("Footnote")) {
                        return true;
                    }
                }
            }
        }
        return isFootnote;
    }
    
    private static XWPFRun getFirstValidRun(XWPFParagraph para) {
        List<XWPFRun> runs = para.getRuns();
        XWPFRun first_valid_run = runs.get(0);
        for (int i = 0; i < runs.size(); i++) {
            XWPFRun run = runs.get(i);
            if (run == null) {
                continue;
            }

            if (run.getText(0) == null || run.getText(0).trim().equals("")
                    || run.getText(0).equals("•") || run.getText(0).equals("●")
                    || run.getText(0).equals("●")) {
                continue;
            }

            first_valid_run = run;
            break;
        }
        return first_valid_run;
    }
    
    private static boolean isBold(XWPFParagraph para) {
        XWPFRun first_valid_run = getFirstValidRun(para);
        if (first_valid_run.isBold()) {
            return true;
        }
        
        if (first_valid_run.getCTR() != null) {
            if (first_valid_run.getCTR().getRPr() != null) {
                boolean is_rStyle_bold = false;

                if (first_valid_run.getCTR().getRPr().getRStyle() != null) {
                    String rStyle = first_valid_run.getCTR().getRPr()
                            .getRStyle().getVal();
                    if (rStyle.equals("bold")) {
                        is_rStyle_bold = true;
                    }
                }

                if (is_rStyle_bold) {
                    if (first_valid_run.getCTR().getRPr().getB() != null) {
                        if (first_valid_run.getCTR().getRPr().getB().getVal()
                                .intValue() == 0) {
                            return false;
                        }
                    } else {
                        return true;
                    }
                } else {
                    return false;
                }
            }
        }
        return false;
    }
    
    private static boolean isCenterAligned(XWPFParagraph para) {
        if (para.getAlignment().equals(ParagraphAlignment.CENTER)) {
            return true;
        }
        return false;
    }
    
    private static boolean isLeftAligned(XWPFParagraph para) {
        if (para.getAlignment().equals(ParagraphAlignment.LEFT)) {
            return true;
        }
        return false;
    }
    
    private static boolean isTaxonTitle(String paragraph) {
        if (paragraph.matches("^\\d+\\. .+$")) {
            return true;
        }
        return false;
    }
    
    private static boolean isSubTaxonTitle(String paragraph) {
        if (paragraph.matches("^(\\*\\??)\t[A-Z][a-z]+[^:]+\\.  .+$")) {
            return true;
        } else if(paragraph.matches("^\t[A-Z][a-z]+.+\\.  .+$")) {
            return true;
        } else if(paragraph.matches("^\t?[A-Z][a-z]+[^:]+\\.  .+$")) {
            return true;
        }
        return false;
    }
    
    private static boolean isKeyTitle(String paragraph) {
        if (paragraph.matches("^(Key|KEY) [A-Z]+\\d* – .+$")) {
            return true;
        }

        return false;
    }
    
    private static boolean isKeyData(String paragraph) {
        if (paragraph.matches("^\\d+\t.+$")) {
            return true;
        }
        
        return false;
    }
    
    private static boolean isRefKeyStatement(String determination) {
        if(determination.matches("^(Key|KEY) [A-Z]+\\d*$")) {
            return true;
        }
        
        return false;
    }
    
    private static KeyTo genKeyTo(String title) {
        KeyTo key = new KeyTo();

        KeyHeading heading = new KeyHeading();
        heading.setHeading(title);
        key.setHeading(heading);
        return key;
    }
    
    private static Taxonomy genTaxon(File source, String title) {
        Taxonomy taxon = new Taxonomy();
        
        TaxonomyMeta meta = new TaxonomyMeta();
        meta.setSource(source);
        taxon.setMeta(meta);
        
        TaxonomyNomenclature nomenclature = new TaxonomyNomenclature();
        nomenclature.setName(getTaxonName(title));
        nomenclature.setCommonName(getTaxonCommonName(title));
        nomenclature.setNameInfo(title);
        taxon.setNomenclture(nomenclature);
        return taxon;
    }
    
    private static Taxonomy genSubTaxon(File source, String title) {
        Taxonomy taxon = new Taxonomy();
        
        TaxonomyMeta meta = new TaxonomyMeta();
        meta.setSource(source);
        taxon.setMeta(meta);
        
        TaxonomyNomenclature nomenclature = new TaxonomyNomenclature();
        nomenclature.setName(getSubTaxonName(title));
        nomenclature.setCommonName(getSubTaxonCommonName(title));
        nomenclature.setNameInfo(title);
        taxon.setNomenclture(nomenclature);
        return taxon;
    }
    
    private static String getTaxonName(String title) {
        Pattern pattern1 = Pattern.compile("^\\d+\\. (.+)$");
        Matcher mt1 = pattern1.matcher(title);
        if (mt1.find()) {
            String next = mt1.group(1).trim();
            Pattern pattern2 = Pattern.compile("^(.+) \\d+.+$");
            Matcher mt2 = pattern2.matcher(next);
            if (mt2.find()) {
                return mt2.group(1).trim();
            }
            return next;
        }
        return title;
    }
    
    private static String getTaxonCommonName(String title) {
        Pattern pattern1 = Pattern.compile("^\\d+\\. (.+)$");
        Matcher mt1 = pattern1.matcher(title);
        if (mt1.find()) {
            String next = mt1.group(1).trim();
            Pattern pattern2 = Pattern.compile("^.+ \\d+ \\((.+)\\)$");
            Matcher mt2 = pattern2.matcher(next);
            if (mt2.find()) {
                return mt2.group(1).trim();
            }
            return "";
        }
        return "";
    }
    
    private static String getSubTaxonName(String title) {
        int idx = title.indexOf(",");
        if(idx > 0) {
            return title.substring(0, idx).trim();
        }
        return title;
    }
    
    private static String getSubTaxonCommonName(String title) {
        int idx = title.indexOf(",");
        if(idx > 0) {
            return title.substring(idx + 1).trim();
        }
        return "";
    }
    
    private static String getSubTaxonTitle(String paragraph) {
        String newPara = paragraph.trim();
        if(newPara.startsWith("*")) {
            newPara = newPara.substring(1).trim();
        }
        
        if(newPara.startsWith("?")) {
            newPara = newPara.substring(1).trim();
        }
        
        int idx = newPara.indexOf(".  ");
        if(idx > 0) {
            newPara = newPara.substring(0, idx).trim();
        }
        return newPara;
    }
    
    private static String getSubTaxonDiscussion(String paragraph) {
        int idx = paragraph.indexOf(".  ");
        if(idx > 0) {
            return paragraph.substring(idx + 3).trim();
        }
        return paragraph;
    }
    
    private static String[] extractArticulation(String body) {
        String[] bodyExt = new String[2];
        int idxStart = body.indexOf("[");
        int idxEnd = body.indexOf("]");
        if(idxStart > 0 && idxEnd > 0) {
            bodyExt[0] = body.substring(0, idxStart).trim();
            bodyExt[1] = body.substring(idxStart, idxEnd+1).trim();
        } else {
            bodyExt[0] = body.trim();
            bodyExt[1] = "";
        }
        return bodyExt;
    }
    
    private static String[] splitKeyTitle(String keytitle) {
        Pattern pattern1 = Pattern.compile("^((Key|KEY) [A-Z]+\\d*) – (.+)$");
        Matcher mt1 = pattern1.matcher(keytitle);
        if (mt1.find()) {
            String[] split = new String[2];
            split[0] = mt1.group(1).trim();
            split[1] = mt1.group(3).trim();
            return split;
        }
        return null;
    }
    
    private static String[] splitKeyData(String keydata) {
        Pattern pattern1 = Pattern.compile("^(\\d+)\t(.+)\t(.+)$");
        Matcher mt1 = pattern1.matcher(keydata);
        if (mt1.find()) {
            String[] split = new String[3];
            split[0] = mt1.group(1).trim();
            split[1] = mt1.group(2).trim();
            split[2] = mt1.group(3).trim();
            return split;
        }
        
        Pattern pattern2 = Pattern.compile("^(\\d+)\t(.+)$");
        Matcher mt2 = pattern2.matcher(keydata);
        if (mt2.find()) {
            String[] split = new String[2];
            split[0] = mt2.group(1).trim();
            split[1] = mt2.group(2).trim();
            return split;
        }
        return null;
    }
    
    public static void main(String[] args) throws Exception {
        if(args.length != 1) {
            System.err.println("specify text file path");
            return;
        }
        
        File file = new File(args[0]);
        File parentDir = file.getParentFile();
        File taxonDir = new File(parentDir, "taxon");
        taxonDir.mkdir();
        File keyDir = new File(parentDir, "key");
        keyDir.mkdir();
        
        FileInputStream fileIS = new FileInputStream(file);
        XWPFDocument wordDoc = new XWPFDocument(fileIS);
        
        String taxonTitle = "";
        String keyTitle = "";
        
        List<Taxonomy> taxonomies = new ArrayList<Taxonomy>();
        List<KeyTo> keytos = new ArrayList<KeyTo>();
        Taxonomy prevTaxon = null;
        KeyTo prevKeyTo = null;
        KeyStatement prevUndeterminedStatement = null;
        
        boolean bPrevTaxonTitle = false;
        boolean bPrevKeyTitle = false;
        
        int keyIndex = 1;
        List<XWPFParagraph> paragraphs = wordDoc.getParagraphs();
        for (XWPFParagraph para : paragraphs) {
            if (isFootnote(para)) {
                //System.out.println(para.getText().trim());
                continue;
            }

            if (para.getRuns().size() < 1) {
                continue;
            }

            if (para.getText() != null
                    && (para.getText().trim().matches("^_+$") || para
                    .getText().trim().equals(""))) {
                continue;
            }
            
            if(isCenterAligned(para)) {
                // at here taxon title or title-otherinfo, or key title
                String paragraphString = para.getText().trim();
                if(isTaxonTitle(paragraphString)) {
                    // taxonTitle
                    taxonTitle = paragraphString;
                    keyTitle = null;
                    
                    prevTaxon = genTaxon(file, taxonTitle);
                    // exception
                    if(taxonTitle.startsWith("147. ROSACEAE")) {
                        prevTaxon.getNomenclature().setRank("Family");
                        prevTaxon.getNomenclature().setHierarchy("Family Rose");
                    } else {
                        String taxonName = prevTaxon.getNomenclature().getName();
                        prevTaxon.getNomenclature().setRank("Genus");
                        prevTaxon.getNomenclature().setHierarchy("Family Rose; Genus " + taxonName);
                    }
                    taxonomies.add(prevTaxon);
                    bPrevTaxonTitle = true;
                    bPrevKeyTitle = false;
                } else if(isKeyTitle(paragraphString)) {
                    // keyTitle
                    keyTitle = paragraphString;
                    //String[] keyTitleSplit = splitKeyTitle(keyTitle);
                    
                    prevKeyTo = genKeyTo(keyTitle);
                    keytos.add(prevKeyTo);
                    //if(keyTitleSplit != null) {
                    //    prevKeyTo.setFilename(StringUtil.getSafeFileName(keyIndex + ". " + keyTitleSplit[0]) + ".xml");
                    //} else {
                        prevKeyTo.setFilename(StringUtil.getSafeFileName(keyIndex + ". " + keyTitle) + ".xml");
                    //}
                    if(prevTaxon != null) {
                        TaxonomyKeyFile keyfile = new TaxonomyKeyFile();
                        String keyFileName;
                        //if(keyTitleSplit != null) {
                        //    keyFileName = StringUtil.getSafeFileName(keyIndex + ". " + keyTitleSplit[0]) + ".xml";
                        //} else {
                            keyFileName = StringUtil.getSafeFileName(keyIndex + ". " + keyTitle) + ".xml";
                        //}
                        keyfile.setKeyFile(keyFileName);
                        prevTaxon.addKeyFile(keyfile);
                        prevKeyTo.setFilename(keyFileName);
                    }
                    keyIndex++;
                    bPrevKeyTitle = true;
                    bPrevTaxonTitle = false;
                } else {
                    // taxon-otherinfo
                    if(prevTaxon != null) {
                        if(bPrevTaxonTitle) {
                            String original = prevTaxon.getNomenclature().getNameInfo();
                            prevTaxon.getNomenclature().setNameInfo(original + " " + paragraphString);
                        }
                    }
                    // keyto heading
                    if(prevKeyTo != null) {
                        if(bPrevKeyTitle) {
                            String original = prevKeyTo.getHeading().getHeading();
                            System.out.println(original);
                            prevKeyTo.getHeading().setHeading(original + " " + paragraphString);
                        }
                    }
                }
            } else if(isLeftAligned(para)) {
                //System.out.println(para.getText());
                String paragraphString = para.getText().trim();
                if(isKeyData(paragraphString)) {
                    if(keyTitle == null) {
                        keyTitle = "Key To " + prevTaxon.getNomenclature().getName();
                        
                        prevKeyTo = genKeyTo(keyTitle);
                        keytos.add(prevKeyTo);
                        
                        TaxonomyKeyFile keyfile = new TaxonomyKeyFile();
                        String keyFileName = StringUtil.getSafeFileName(keyIndex + ". " + keyTitle) + ".xml";
                        keyfile.setKeyFile(keyFileName);
                        prevTaxon.addKeyFile(keyfile);
                        prevKeyTo.setFilename(keyFileName);
                        keyIndex++;
                    }
                    
                    // key data
                    String[] keydataSplits = splitKeyData(paragraphString);
                    if(keydataSplits != null) {
                        if(prevUndeterminedStatement != null) {
                            prevUndeterminedStatement.setNextStatementId(keydataSplits[0]);
                            prevUndeterminedStatement = null;
                        }
                        
                        if(keydataSplits.length == 2) {
                            // refer next
                            KeyStatement statement = new KeyStatement();
                            statement.setId(keydataSplits[0]);
                            statement.setStatement(keydataSplits[1]);
                            prevKeyTo.addStatement(statement);
                            
                            prevUndeterminedStatement = statement;
                        } else if(keydataSplits.length == 3) {
                            KeyStatement statement = new KeyStatement();
                            statement.setId(keydataSplits[0]);
                            statement.setStatement(keydataSplits[1]);
                            statement.setDetermination(keydataSplits[2]);
                            prevKeyTo.addStatement(statement);
                        }
                    }
                } else {
                    // may contain keyData part 2
                    if(prevUndeterminedStatement != null) {
                        prevUndeterminedStatement.setDetermination(paragraphString);
                        prevUndeterminedStatement = null;
                    } else {
                        // not key - other info or another sub taxon
                        String paragraphStringOthers = para.getText();
                        boolean isSubTaxon = false;
                        if (isSubTaxonTitle(paragraphStringOthers)) {
                            isSubTaxon = true;
                        }
                        
                        if(isSubTaxon) {
                            String subtaxonTitle = getSubTaxonTitle(paragraphStringOthers);
                            Taxonomy subTaxon = genSubTaxon(file, subtaxonTitle);

                            String taxonName = subTaxon.getNomenclature().getName();
                            subTaxon.getNomenclature().setRank("Species");
                            subTaxon.getNomenclature().setHierarchy(prevTaxon.getNomenclature().getHierarchy() + "; Species " + taxonName);
                            
                            String body = getSubTaxonDiscussion(paragraphStringOthers);
                            
                            String[] bodyExt = extractArticulation(body);
                            
                            TaxonomyDiscussion discussion = new TaxonomyDiscussion();
                            discussion.setText(bodyExt[0]);
                            subTaxon.addDiscussionNonTitled(discussion);
                            
                            TaxonomyGenericElement articulation = new TaxonomyGenericElement();
                            articulation.setName("articulation");
                            articulation.setText(bodyExt[1]);
                            subTaxon.addElement(articulation);
                            
                            taxonomies.add(subTaxon);
                        } else {
                            TaxonomyDiscussion discussion = new TaxonomyDiscussion();
                            discussion.setText(paragraphStringOthers.trim());
                            prevTaxon.addDiscussionNonTitled(discussion);
                        }
                    }
                }
            }
        }
        
        
        for(KeyTo keyto : keytos) {
            // scan ref key to files
            boolean isMyTaxon = false;
            for(KeyStatement statement : keyto.getStatement()) {
                if(statement.getDetermination() != null && isRefKeyStatement(statement.getDetermination())) {
                    // is ref
                    //System.out.println("Key file - " + statement.getDetermination());
                    String refFilename = null;
                    
                    for(Taxonomy taxonomy : taxonomies) {
                        for(TaxonomyKeyFile keyFile : taxonomy.getKeyFiles()) {
                            if(keyFile.getKeyFile().indexOf(statement.getDetermination()) > 0) {
                                // found!!
                                refFilename = keyFile.getKeyFile();
                            }
                            
                            if(keyFile.getKeyFile().equals(keyto.getFilename())) {
                                // mine
                                isMyTaxon = true;
                            }
                        }
                        
                        if(isMyTaxon) {
                            break;
                        }
                    }
                    
                    if(isMyTaxon) {
                        statement.setDeterminationRefFilename(refFilename);
                    }
                }
            }
            
            File outKeyFile = new File(keyDir, keyto.getFilename());
            keyto.toXML(outKeyFile);
        }
        
        int taxonIndex = 1;
        for(Taxonomy taxonomy : taxonomies) {
            File outTaxonFile = new File(taxonDir, StringUtil.getSafeFileName(taxonIndex + ". " + taxonomy.getNomenclature().getName()) + ".xml");
            taxonomy.toXML(outTaxonFile);
            taxonIndex++;
        }
    }
}