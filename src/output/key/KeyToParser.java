/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package output.key;

import common.utils.RegExUtil;
import common.utils.StreamUtil;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import output.key.bean.KeyHeading;
import output.key.bean.KeyStatement;
import output.taxonomy.DocumentSpecificSymbolTable;
import output.taxonomy.TaxonomyConfiguration;
import output.taxonomy.TaxonomyLineCategorizeAlg;
import static output.taxonomy.TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_KEY_TO_FAMILY;
import static output.taxonomy.TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_NAME;
import static output.taxonomy.TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_TAXONOMY_KEY_TO_FAMILY;
import static output.taxonomy.TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_TAXONOMY_NAME;

/**
 *
 * @author iychoi
 */
public class KeyToParser {
    private File file;
    private TaxonomyLineCategorizeAlg lineAlg;
    
    private String previousKeyToId;
    
    public KeyToParser(File file) {
        if(file == null || !file.exists() || !file.isFile())
            throw new IllegalArgumentException("Cannot find the file");
        
        this.file = file;
    }
    
    public List<KeyTo> parseKeyTo(TaxonomyConfiguration conf) throws IOException {
        return parseKeyTo(new DocumentSpecificSymbolTable(), conf);
    }
    
    public List<KeyTo> parseKeyTo(DocumentSpecificSymbolTable dsSymbolTable, TaxonomyConfiguration conf) throws IOException {
        this.lineAlg = new TaxonomyLineCategorizeAlg(dsSymbolTable, conf);
        
        List<KeyTo> keytos = new ArrayList<KeyTo>();
        
        String filedata;
        try {
            filedata = StreamUtil.readFileString(this.file);
        } catch (Exception ex) {
            throw new IOException(ex.getMessage());
        }
        
        String[] paragraphs = filedata.split("\\n[\\r\\t ]*\\n[\\r\\t ]*\\n*");
        
        KeyTo keyto = null;
        for(String paragraph : paragraphs) {
            this.lineAlg.feedLine(paragraph.trim());
            
            if(this.lineAlg.isNewKeyTo()) {
                keyto = new KeyTo();
                
                // add to list
                keytos.add(keyto);
            }
            
            if(keyto != null) {
                processKeyTo(keyto, paragraph.trim(), conf);
            }
        }
        
        return keytos;
    }
    
    private void processKeyTo(KeyTo keyto, String line, TaxonomyConfiguration conf) {
        switch(this.lineAlg.getLineType()) {
            case LINE_DIRECTION_TAXONOMY_NAME:
                break;
            case LINE_TAXONOMY_NAME:
                break;
            case LINE_DIRECTION_TAXONOMY_KEY_TO_FAMILY:
                processKeyToHeading(keyto, line, conf);
                break;
            case LINE_TAXONOMY_KEY_TO_FAMILY:
                processKeyToStatement(keyto, line, conf);
                break;
        }
    }

    private void processKeyToStatement(KeyTo keyto, String line, TaxonomyConfiguration conf) {
        KeyStatement statement = new KeyStatement();
        String[] state = RegExUtil.splitKeyToStatement(line);
        if(state != null && state.length >= 3) {
            if(RegExUtil.isSiblingKeyToID(state[0]) && previousKeyToId != null) {
                statement.setId(previousKeyToId + state[0]);
            } else {
                this.previousKeyToId = state[0];
                statement.setId(state[0]);
            }
            statement.setStatement(state[1]);
            statement.setDetermination(state[2]);
            keyto.addStatement(statement);
        }
    }

    private void processKeyToHeading(KeyTo keyto, String line, TaxonomyConfiguration conf) {
        KeyHeading heading = new KeyHeading();
        heading.setHeading(line.trim());
        
        keyto.setHeading(heading);
    }
}
