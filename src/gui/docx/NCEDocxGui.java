/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.docx;

import gui.djvu.*;
import gui.common.DjvuView;
import common.utils.RegExUtil;
import common.utils.StreamUtil;
import common.utils.StringUtil;
import gui.common.ScreenUtil;
import djvu.DjvuLine;
import djvu.DjvuLineFilter;
import djvu.DjvuLineTextCorrector;
import djvu.DjvuPage;
import djvu.DjvuParagraphAlg;
import djvu.DjvuParagraphExtractor;
import java.awt.Insets;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import msword.WordPage;
import msword.WordParagraph;
import msword.WordReader;

/**
 *
 * @author iychoi
 */
public class NCEDocxGui extends javax.swing.JFrame {

    private DjvuView djvuView;
    private int currentPage;
    private File workingParentDir;
    private File docxFile;
    private File djvuFile;
    
    private List<WordPage> parsedPages;
            
    /**
     * Creates new form NCEGui
     */
    public NCEDocxGui() {
        initComponents();
        
        init();
    }
    
    private void init() {
    }
    
    private void loadView(File djvuFile) {
        if(djvuFile == null) 
            throw new IllegalArgumentException("file is null");
        
        this.djvuView = new DjvuView(djvuFile.getAbsolutePath());
        this.djvuView.run();
    }
    
    private void closeView() {
        if(this.djvuView != null) {
            this.djvuView.setVisible(false);
            this.djvuView.dispose();
        }
        
        this.djvuView = null;
    }
    
    private void movePage(int page) {
        this.currentPage = page;
        if(this.chkSyncPage.isSelected()) {
            this.djvuView.movePage(page);
        }
        
        int pageEnd = 0;
        if(this.parsedPages != null && this.parsedPages.size() > 0) {
            pageEnd = this.parsedPages.get(this.parsedPages.size() - 1).getPageNo();
        }
        this.lblPage.setText("Page " + this.currentPage + " / " + pageEnd);
    }
    
    private void syncPage() {
        this.djvuView.movePage(this.currentPage);
    }
    
    private int getPage() {
        return this.currentPage;
    }
        
    private void startParsing() throws Exception {
        if(this.docxFile == null) {
            throw new Exception("docxFile is null");
        }
        
        if(!this.docxFile.exists() || !this.docxFile.isFile()) {
            throw new Exception("docxFile is not exist");
        }
        
        WordReader reader = new WordReader(this.docxFile);
        
        this.parsedPages = reader.parse();
        
        processFirstPage();
    }
    
    private void processFirstPage() {
        try {
            if(this.parsedPages != null && this.parsedPages.size() > 0) {
                processPage(this.parsedPages.get(0).getPageNo(), true);
            } else {
                processPage(1, true);
            }
        } catch(Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
    
    private void processNextPage() throws Exception {
        if(this.currentPage < 1) {
            processFirstPage();
        } else {
            boolean findPage = false;
            WordPage targetPage = null;
            for(WordPage page : this.parsedPages) {
                if(page.getPageNo() == this.currentPage) {
                    findPage = true;
                } else if(findPage) {
                    targetPage = page;
                    break;
                }
            }
            
            if(targetPage != null) {
                processPage(targetPage.getPageNo(), true);
            } else {
                boolean bFindPage = false;
                int wantPage = this.currentPage + 1;
                for(WordPage page : this.parsedPages) {
                    if(page.getPageNo() == wantPage) {
                        bFindPage = true;
                        break;
                    }
                }
                
                if(bFindPage) {
                    processPage(this.currentPage + 1, true);
                } else {
                    throw new Exception("There's no next page");
                }
            }
        }
    }
    
    private void processPrevPage() throws Exception {
        if(this.currentPage < 1) {
            processFirstPage();
        } else {
            boolean findPage = false;
            WordPage targetPage = null;
            for(WordPage page : this.parsedPages) {
                if(page.getPageNo() == this.currentPage) {
                    findPage = true;
                    break;
                } else {
                    targetPage = page;
                }
            }
            
            if(targetPage != null) {
                processPage(targetPage.getPageNo(), true);
            } else {
                boolean bFindPage = false;
                int wantPage = this.currentPage - 1;
                for(WordPage page : this.parsedPages) {
                    if(page.getPageNo() == wantPage) {
                        bFindPage = true;
                        break;
                    }
                }
                
                if(bFindPage) {
                    processPage(this.currentPage - 1, true);
                } else {
                    throw new Exception("There's no prev page");
                }
            }
        }
    }
    
    private File getPageFile(int page) {
        return new File(this.workingParentDir, "page" + page + ".txt");
    }
    
    private void saveCurrentPage() throws Exception {
        boolean findPage = false;
        for(WordPage page : this.parsedPages) {
            if(page.getPageNo() == this.currentPage) {
                findPage = true;
                break;
            }
        }
        
        if(findPage) {
            try {
                File pageOutput = getPageFile(this.currentPage);

                FileWriter writer = new FileWriter(pageOutput, false);
                writer.write(this.txtPara.getText());
                writer.close();
            } catch (IOException ex) {
                throw new Exception(ex.getMessage());
            }
        }
    }
    
    private void combineAllPages() throws Exception {
        final JFileChooser fc = new JFileChooser();
        if (this.workingParentDir != null) {
            fc.setCurrentDirectory(this.workingParentDir);
        }

        //In response to a button click:
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            FileWriter writer = null;
            try {
                File file = fc.getSelectedFile();
                writer = new FileWriter(file, false);

                boolean bNewParagraph = true;
                for(WordPage page : this.parsedPages) {
                    int pageNum = page.getPageNo();
                    File pageFile = getPageFile(pageNum);
                    if(pageFile.exists() && pageFile.isFile() && pageFile.length() > 0) {
                        String pageString = StreamUtil.readFileString(pageFile);
                        String[] paragraphs = RegExUtil.splitWithNewLines(pageString);
                        boolean endWithNewLine = RegExUtil.isEndWithNewLines(pageString);
                        //System.out.println("page " + pageNum + " ends with newLine" + endWithNewLine);
                        for(int i=0;i<paragraphs.length;i++) {
                            String para = paragraphs[i];
                            if(i == 0) {
                                if(bNewParagraph) {
                                    writer.write("[page : " + pageNum + "]" + para + "\n\n");
                                } else {
                                    writer.write(para + "\n\n");
                                }
                            } else if(i == paragraphs.length - 1) {
                                if(endWithNewLine) {
                                    writer.write("[page : " + pageNum + "]" + para + "\n\n");
                                    bNewParagraph = true;
                                } else {
                                    writer.write("[page : " + pageNum + "]" + para);
                                    bNewParagraph = false;
                                }
                            } else {
                                writer.write("[page : " + pageNum + "]" + para + "\n\n");
                            }
                        }
                    }
                }
                
                writer.close();
            } catch (IOException ex) {
                throw new Exception(ex.getMessage());
            } finally {
                try {
                    writer.close();
                } catch (IOException ex) {
                    throw new Exception(ex.getMessage());
                }
            }
        }
    }
    
    private void processPage(int pageNum, boolean usePageFile) throws Exception {
        boolean findPage = false;
        WordPage targetPage = null;
        for(WordPage page : this.parsedPages) {
            if(page.getPageNo() == pageNum) {
                findPage = true;
                targetPage = page;
            }
        }
        
        if(findPage) {
            // check stored file
            File prevFile = getPageFile(targetPage.getPageNo());
            if(usePageFile && prevFile.exists() && prevFile.isFile()) {
                String pageText = StreamUtil.readFileString(prevFile);
                this.txtPara.setText(pageText);
            } else {
                List<WordParagraph> paragraphs = targetPage.getParagraphs();

                String pageText = "";

                for(WordParagraph paragraph : paragraphs) {
                    String paragraphText = paragraph.getParagraph();
                    if(!isInformalParagraph(paragraphText)) {
                        pageText += paragraphText;
                        pageText += "\n\n";
                    }
                }

                this.txtPara.setText(pageText);
            }
            
            this.movePage(pageNum);
        } else {
            throw new Exception("Cannot find page : " + pageNum);
        }
        
        this.txtPara.setCaretPosition(0);
        this.currentPage = pageNum;
    }

    private void loadDjvuFile(File file) {
        this.djvuFile = file;
        this.workingParentDir = file.getParentFile();

        loadView(file);

        this.lblDjvu.setText(file.getName());

        int scrWidth = ScreenUtil.getScreenWorkingWidth();
        int scrHeight = ScreenUtil.getScreenWorkingHeight();

        Insets scrInsets = ScreenUtil.getScreenInsets();

        int wnd1PosX = scrInsets.left;
        int wnd1PosY = scrInsets.top;
        int wnd2PosX = scrInsets.left + scrWidth / 2;
        int wnd2PosY = scrInsets.top;

        int wndWidth = scrWidth / 2;
        int wndHeight = scrHeight;

        if (this.djvuView != null) {
            this.djvuView.setSize(wndWidth, wndHeight);
            this.djvuView.setLocation(wnd1PosX, wnd1PosY);
        }

        this.setSize(wndWidth, wndHeight);
        this.setLocation(wnd2PosX, wnd2PosY);

        this.currentPage = 0;
    }
    
    private void loadDocxFile(File file) {
        this.workingParentDir = file.getParentFile();
        this.docxFile = file;

        this.lblDocx.setText(file.getName());
    }
    
    private void findRelatedFiles() {
        if(this.workingParentDir == null)
            return;
        
        String filenameprefix = null;
        if(this.djvuFile != null) {
            filenameprefix = this.djvuFile.getName();
        } else if(this.docxFile != null) {
            filenameprefix = this.docxFile.getName();
        }
        
        final String prefix = StringUtil.removeExtensions(filenameprefix);
        
        File[] files = this.workingParentDir.listFiles(new FilenameFilter(){

            @Override
            public boolean accept(File file, String name) {
                if(name.startsWith(prefix)) {
                    return true;
                }
                return false;
            }
        });
        
        if(files != null && files.length > 0) {
            for(File file : files) {
                if(file.getName().toLowerCase().endsWith(".djvu")) {
                    if(this.djvuFile == null) {
                        loadDjvuFile(file);
                    }
                } else if(file.getName().toLowerCase().endsWith(".docx")) {
                    if(this.docxFile == null) {
                        loadDocxFile(file);
                    }
                }
            }
        }
    }
    
    private boolean isInformalParagraph(String paragraphText) {
        Pattern p1 = Pattern.compile("^\\[\\s*(page|Page|PAGE)\\s+(\\d+)\\s+(from|From|FROM)\\s+(pdf|Pdf|PDF)\\s*\\]$");
        Matcher mt1 = p1.matcher(paragraphText);
        if(mt1.find()) {
            return true;
        }
        
        Pattern p2 = Pattern.compile("^\\[\\s*(page|Page|PAGE)\\s+(no|No|NO)\\s+(not|Not|NOT)\\s+(given|Given|GIVEN)\\s+(in|In|IN)\\s+(pdf|Pdf|PDF)\\s*\\]$");
        Matcher mt2 = p2.matcher(paragraphText);
        if(mt2.find()) {
            return true;
        }
        
        Pattern p3 = Pattern.compile("^\\[\\s*(picture|Picture|PICTURE)\\s+\\d+([,]\\d+)*\\s*\\]$");
        Matcher mt3 = p3.matcher(paragraphText);
        if(mt3.find()) {
            return true;
        }
        
        Pattern p4 = Pattern.compile("^\\[\\s*(pdf|Pdf|PDF)\\s+(page|Page|PAGE)");
        Matcher mt4 = p4.matcher(paragraphText);
        if(mt4.find()) {
            return true;
        }
        
        return false;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnLoadDjvu = new javax.swing.JButton();
        lblDjvu = new javax.swing.JLabel();
        btnLoadDocx = new javax.swing.JButton();
        lblDocx = new javax.swing.JLabel();
        btnStart = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtPara = new javax.swing.JTextArea();
        btnNextPage = new javax.swing.JButton();
        lblPage = new javax.swing.JLabel();
        btnFinish = new javax.swing.JButton();
        btnSpecialMale = new javax.swing.JButton();
        btnSpecialFemale = new javax.swing.JButton();
        btnPrevPage = new javax.swing.JButton();
        btnSpecialMercury = new javax.swing.JButton();
        btnReprocessPage = new javax.swing.JButton();
        chkSyncPage = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btnLoadDjvu.setText("Load Djvu");
        btnLoadDjvu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoadDjvuActionPerformed(evt);
            }
        });

        lblDjvu.setText("Not Loaded");

        btnLoadDocx.setText("Load Docx");
        btnLoadDocx.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoadDocxActionPerformed(evt);
            }
        });

        lblDocx.setText("Not Loaded");
        lblDocx.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblDocxMouseClicked(evt);
            }
        });

        btnStart.setText("Start!");
        btnStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStartActionPerformed(evt);
            }
        });

        txtPara.setColumns(20);
        txtPara.setLineWrap(true);
        txtPara.setRows(5);
        txtPara.setTabSize(4);
        txtPara.setWrapStyleWord(true);
        jScrollPane1.setViewportView(txtPara);

        btnNextPage.setText("NextPage");
        btnNextPage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextPageActionPerformed(evt);
            }
        });

        lblPage.setText("Page");

        btnFinish.setText("Finish");
        btnFinish.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFinishActionPerformed(evt);
            }
        });

        btnSpecialMale.setText("♂");
        btnSpecialMale.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSpecialMaleActionPerformed(evt);
            }
        });

        btnSpecialFemale.setText("♀");
        btnSpecialFemale.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSpecialFemaleActionPerformed(evt);
            }
        });

        btnPrevPage.setText("PrevPage");
        btnPrevPage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrevPageActionPerformed(evt);
            }
        });

        btnSpecialMercury.setText("☿");
        btnSpecialMercury.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSpecialMercuryActionPerformed(evt);
            }
        });

        btnReprocessPage.setText("Reparse");
        btnReprocessPage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReprocessPageActionPerformed(evt);
            }
        });

        chkSyncPage.setSelected(true);
        chkSyncPage.setText("Sync. page");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(lblPage)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 219, Short.MAX_VALUE)
                        .addComponent(btnReprocessPage)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnPrevPage)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnNextPage)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnFinish))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnLoadDocx)
                                .addGap(20, 20, 20)
                                .addComponent(lblDocx))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnLoadDjvu)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblDjvu))
                            .addComponent(chkSyncPage))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnStart, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(btnSpecialMale)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnSpecialFemale)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnSpecialMercury)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnStart, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnLoadDjvu)
                            .addComponent(lblDjvu))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnLoadDocx)
                            .addComponent(lblDocx))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnSpecialMale)
                        .addComponent(btnSpecialFemale)
                        .addComponent(btnSpecialMercury))
                    .addComponent(chkSyncPage, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNextPage)
                    .addComponent(lblPage)
                    .addComponent(btnFinish)
                    .addComponent(btnPrevPage)
                    .addComponent(btnReprocessPage))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnLoadDjvuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoadDjvuActionPerformed
        final JFileChooser fc = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("DJVU file", "djvu");
        fc.addChoosableFileFilter(filter);
        
        if(this.workingParentDir != null) {
            fc.setCurrentDirectory(this.workingParentDir);
        }

        //In response to a button click:
        int returnVal = fc.showOpenDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            
            if(file.getName().toLowerCase().endsWith(".djvu")) {
                loadDjvuFile(file);  
                
                findRelatedFiles();
            } else {
                JOptionPane.showMessageDialog(this, "You can select only DJVU files");
            }
        }
    }//GEN-LAST:event_btnLoadDjvuActionPerformed

    private void btnLoadDocxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoadDocxActionPerformed
        final JFileChooser fc = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("MSWord file", "docx");
        fc.addChoosableFileFilter(filter);
        
        if(this.workingParentDir != null) {
            fc.setCurrentDirectory(this.workingParentDir);
        }
        
        //In response to a button click:
        int returnVal = fc.showOpenDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            
            if(file.getName().toLowerCase().endsWith(".docx")) {
                loadDocxFile(file);
                
                findRelatedFiles();
            } else {
                JOptionPane.showMessageDialog(this, "You can select only docx files");
            }
        }
    }//GEN-LAST:event_btnLoadDocxActionPerformed

    private void btnStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStartActionPerformed
        try {
            startParsing();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }//GEN-LAST:event_btnStartActionPerformed

    private void lblDocxMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblDocxMouseClicked
        if(this.docxFile != null && this.docxFile.exists() && this.docxFile.isFile()) {
            try {
                //Runtime.getRuntime().exec(new String[]{"gedit", this.docxFile.getAbsolutePath()});
                throw new IOException("not supported");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }
    }//GEN-LAST:event_lblDocxMouseClicked

    private void btnNextPageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextPageActionPerformed
        try {
            saveCurrentPage();
            processNextPage();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }//GEN-LAST:event_btnNextPageActionPerformed

    private void btnFinishActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFinishActionPerformed
        try {
            saveCurrentPage();
            combineAllPages();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }//GEN-LAST:event_btnFinishActionPerformed

    private void btnSpecialFemaleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSpecialFemaleActionPerformed
        this.txtPara.insert("♀", this.txtPara.getCaretPosition());
    }//GEN-LAST:event_btnSpecialFemaleActionPerformed

    private void btnSpecialMaleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSpecialMaleActionPerformed
        this.txtPara.insert("♂", this.txtPara.getCaretPosition());
    }//GEN-LAST:event_btnSpecialMaleActionPerformed

    private void btnPrevPageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrevPageActionPerformed
        try {
            saveCurrentPage();
            processPrevPage();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }//GEN-LAST:event_btnPrevPageActionPerformed

    private void btnSpecialMercuryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSpecialMercuryActionPerformed
        this.txtPara.insert("☿", this.txtPara.getCaretPosition());
    }//GEN-LAST:event_btnSpecialMercuryActionPerformed

    private void btnReprocessPageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReprocessPageActionPerformed
        try {
            processPage(this.currentPage, false);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }//GEN-LAST:event_btnReprocessPageActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(NCEDjvuGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(NCEDjvuGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(NCEDjvuGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(NCEDjvuGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new NCEDocxGui().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFinish;
    private javax.swing.JButton btnLoadDjvu;
    private javax.swing.JButton btnLoadDocx;
    private javax.swing.JButton btnNextPage;
    private javax.swing.JButton btnPrevPage;
    private javax.swing.JButton btnReprocessPage;
    private javax.swing.JButton btnSpecialFemale;
    private javax.swing.JButton btnSpecialMale;
    private javax.swing.JButton btnSpecialMercury;
    private javax.swing.JButton btnStart;
    private javax.swing.JCheckBox chkSyncPage;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblDjvu;
    private javax.swing.JLabel lblDocx;
    private javax.swing.JLabel lblPage;
    private javax.swing.JTextArea txtPara;
    // End of variables declaration//GEN-END:variables

}
