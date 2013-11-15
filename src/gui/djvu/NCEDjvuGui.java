/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.djvu;

import gui.common.DjvuView;
import common.utils.RegExUtil;
import common.utils.StreamUtil;
import common.utils.StringUtil;
import gui.common.ScreenUtil;
import djvu.DjvuConfiguration;
import djvu.DjvuLine;
import djvu.DjvuLineFilter;
import djvu.DjvuLineOrderAlg;
import djvu.DjvuLineTextCorrector;
import djvu.DjvuPage;
import djvu.DjvuParagraphAlg;
import djvu.DjvuParagraphExtractor;
import djvu.DjvuXMLReader;
import djvu.algorithms.DjvuLineOrderAlg2Col;
import djvu.algorithms.DjvuLineOrderAlgAuto;
import djvu.algorithms.DjvuLineParagraphAlgCaption;
import djvu.algorithms.DjvuLineParagraphAlgGap;
import djvu.algorithms.DjvuLineParagraphAlgIndent;
import java.awt.Insets;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author iychoi
 */
public class NCEDjvuGui extends javax.swing.JFrame {

    private DjvuView djvuView;
    private int currentPage;
    private File workingParentDir;
    private File djvuXMLFile;
    private File djvuFile;
    private File copiedTextFile;
    
    private DjvuConfiguration djvuConf;
    private List<DjvuPage> parsedPages;
            
    /**
     * Creates new form NCEGui
     */
    public NCEDjvuGui() {
        initComponents();
        
        init();
    }
    
    private void init() {
        this.djvuConf = new DjvuConfiguration();
        this.djvuConf.setPageRegion(NCEDjvuConfDefaults.DEFAULT_START_PAGE, NCEDjvuConfDefaults.DEFAULT_END_PAGE);
        this.djvuConf.setMarginLeft(NCEDjvuConfDefaults.DEFAULT_SIDE_MARGIN);
        this.djvuConf.setMarginRight(NCEDjvuConfDefaults.DEFAULT_SIDE_MARGIN);
        this.djvuConf.setMarginTop(NCEDjvuConfDefaults.DEFAULT_TOP_MARGIN);
        this.djvuConf.setMarginBottom(NCEDjvuConfDefaults.DEFAULT_BOTTOM_MARGIN);
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
        this.djvuView.movePage(page);
        
        int pageEnd = 0;
        if(this.parsedPages != null && this.parsedPages.size() > 0) {
            pageEnd = this.parsedPages.get(this.parsedPages.size() - 1).getPagenum();
        }
        this.lblPage.setText("Page " + this.currentPage + " / " + pageEnd);
    }
    
    private void syncPage() {
        this.djvuView.movePage(this.currentPage);
    }
    
    private int getPage() {
        return this.currentPage;
    }
    
    private void openConfiguration() {
        DjvuConfigurationPopup popup = new DjvuConfigurationPopup(this.djvuConf, this, true);
        popup.setVisible(true);
        
        this.djvuConf = popup.getConfiguration();
    }
    
    private void startParsing() throws Exception {
        if(this.djvuXMLFile == null) {
            throw new Exception("djvuXMLFile is null");
        }
        
        if(!this.djvuXMLFile.exists() || !this.djvuXMLFile.isFile()) {
            throw new Exception("djvuXMLFile is not exist");
        }
        
        if(this.copiedTextFile == null) {
            throw new Exception("copiedTextFile is null");
        }
        
        if(!this.copiedTextFile.exists() || !this.copiedTextFile.isFile()) {
            throw new Exception("copiedTextFile is not exist");
        }
        
        DjvuXMLReader reader = new DjvuXMLReader(this.djvuXMLFile);
        
        this.parsedPages = reader.parse(this.djvuConf);
        
        DjvuLineTextCorrector corrector = new DjvuLineTextCorrector(this.copiedTextFile);
        corrector.correct(this.parsedPages);
        
        processFirstPage();
    }
    
    private void processFirstPage() {
        try {
            if(this.parsedPages != null && this.parsedPages.size() > 0) {
                processPage(this.parsedPages.get(0).getPagenum(), true);
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
            DjvuPage targetPage = null;
            for(DjvuPage page : this.parsedPages) {
                if(page.getPagenum() == this.currentPage) {
                    findPage = true;
                } else if(findPage) {
                    targetPage = page;
                    break;
                }
            }
            
            if(targetPage != null) {
                processPage(targetPage.getPagenum(), true);
            } else {
                boolean bFindPage = false;
                int wantPage = this.currentPage + 1;
                for(DjvuPage page : this.parsedPages) {
                    if(page.getPagenum() == wantPage) {
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
            DjvuPage targetPage = null;
            for(DjvuPage page : this.parsedPages) {
                if(page.getPagenum() == this.currentPage) {
                    findPage = true;
                    break;
                } else {
                    targetPage = page;
                }
            }
            
            if(targetPage != null) {
                processPage(targetPage.getPagenum(), true);
            } else {
                boolean bFindPage = false;
                int wantPage = this.currentPage - 1;
                for(DjvuPage page : this.parsedPages) {
                    if(page.getPagenum() == wantPage) {
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
        for(DjvuPage page : this.parsedPages) {
            if(page.getPagenum() == this.currentPage) {
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
                for(DjvuPage page : this.parsedPages) {
                    int pageNum = page.getPagenum();
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
        DjvuPage targetPage = null;
        for(DjvuPage page : this.parsedPages) {
            if(page.getPagenum() == pageNum) {
                findPage = true;
                targetPage = page;
            }
        }
        
        if(findPage) {
            // check stored file
            File prevFile = getPageFile(targetPage.getPagenum());
            if(usePageFile && prevFile.exists() && prevFile.isFile()) {
                String pageText = StreamUtil.readFileString(prevFile);
                this.txtPara.setText(pageText);
            } else {
                DjvuParagraphExtractor paraExtractor = new DjvuParagraphExtractor();
                
                DjvuLineOrderAlg orderAlg = new DjvuLineOrderAlgAuto();
                if(this.cboParagraphAlg.getSelectedItem().equals("2COL")) {
                    orderAlg = new DjvuLineOrderAlg2Col();
                }
                
                DjvuLineFilter filter = new DjvuLineFilter() {
                    @Override
                    public boolean accept(DjvuLine line) {
                        if (line.getText().trim().equals("")) {
                            return false;
                        }

                        return !(RegExUtil.isOneCharOrNum(line.getText()));
                    }
                };

                List<DjvuParagraphAlg> paraAlgs = new ArrayList<DjvuParagraphAlg>();
                paraAlgs.add(new DjvuLineParagraphAlgIndent());
                paraAlgs.add(new DjvuLineParagraphAlgGap());
                paraAlgs.add(new DjvuLineParagraphAlgCaption());

                List<String> paragraphs = paraExtractor.extractParagraphs(targetPage, filter, paraAlgs, orderAlg, this.djvuConf);

                String paraText = "";

                for(String paragraph : paragraphs) {
                    paraText += paragraph;
                    paraText += "\n\n";
                }

                this.txtPara.setText(paraText);
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
    
    private void loadCopyTextFile(File file) {
        this.workingParentDir = file.getParentFile();
        this.copiedTextFile = file;

        this.lblCopiedText.setText(file.getName());
    }
    
    private void loadDjvuXMLFile(File file) {
        this.workingParentDir = file.getParentFile();
        this.djvuXMLFile = file;

        this.lblDjvuXML.setText(file.getName());
    }
    
    private void findRelatedFiles() {
        if(this.workingParentDir == null)
            return;
        
        String filenameprefix = null;
        if(this.djvuFile != null) {
            filenameprefix = this.djvuFile.getName();
        } else if(this.djvuXMLFile != null) {
            filenameprefix = this.djvuXMLFile.getName();
        } else if(this.copiedTextFile != null) {
            filenameprefix = this.copiedTextFile.getName();
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
                } else if(file.getName().toLowerCase().endsWith(".xml")) {
                    if(this.djvuXMLFile == null) {
                        loadDjvuXMLFile(file);
                    }
                } else if(file.getName().toLowerCase().endsWith(".txt")) {
                    if(this.copiedTextFile == null) {
                        loadCopyTextFile(file);
                    }
                }
            }
        }
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
        btnLoadCopyText = new javax.swing.JButton();
        btnLoadDjvuXML = new javax.swing.JButton();
        lblDjvuXML = new javax.swing.JLabel();
        lblCopiedText = new javax.swing.JLabel();
        btnStart = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtPara = new javax.swing.JTextArea();
        btnNextPage = new javax.swing.JButton();
        btnConf = new javax.swing.JButton();
        lblPage = new javax.swing.JLabel();
        btnFinish = new javax.swing.JButton();
        btnSpecialMale = new javax.swing.JButton();
        btnSpecialFemale = new javax.swing.JButton();
        btnPrevPage = new javax.swing.JButton();
        btnSpecialMercury = new javax.swing.JButton();
        btnReprocessPage = new javax.swing.JButton();
        cboParagraphAlg = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btnLoadDjvu.setText("Load Djvu");
        btnLoadDjvu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoadDjvuActionPerformed(evt);
            }
        });

        lblDjvu.setText("Not Loaded");

        btnLoadCopyText.setText("Load CopyText");
        btnLoadCopyText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoadCopyTextActionPerformed(evt);
            }
        });

        btnLoadDjvuXML.setText("Load DjvuXML");
        btnLoadDjvuXML.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoadDjvuXMLActionPerformed(evt);
            }
        });

        lblDjvuXML.setText("Not Loaded");
        lblDjvuXML.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblDjvuXMLMouseClicked(evt);
            }
        });

        lblCopiedText.setText("Not Loaded");
        lblCopiedText.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblCopiedTextMouseClicked(evt);
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

        btnConf.setText("Configuration");
        btnConf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfActionPerformed(evt);
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

        cboParagraphAlg.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "AUTO", "2COL" }));
        cboParagraphAlg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboParagraphAlgActionPerformed(evt);
            }
        });

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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(btnLoadDjvuXML)
                                            .addComponent(btnLoadCopyText))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(lblCopiedText)
                                            .addComponent(lblDjvuXML))
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(btnLoadDjvu)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(lblDjvu)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 152, Short.MAX_VALUE)
                                        .addComponent(btnConf)))
                                .addGap(9, 9, 9))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(cboParagraphAlg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnStart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnLoadDjvu)
                            .addComponent(lblDjvu)
                            .addComponent(btnConf))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnLoadDjvuXML)
                            .addComponent(lblDjvuXML))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnLoadCopyText)
                            .addComponent(lblCopiedText))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSpecialMale)
                    .addComponent(btnSpecialFemale)
                    .addComponent(btnSpecialMercury)
                    .addComponent(cboParagraphAlg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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

    private void btnLoadCopyTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoadCopyTextActionPerformed
        final JFileChooser fc = new JFileChooser();
        
        if(this.workingParentDir != null) {
            fc.setCurrentDirectory(this.workingParentDir);
        }
        
        //In response to a button click:
        int returnVal = fc.showOpenDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            loadCopyTextFile(file);
            
            findRelatedFiles();
        }
    }//GEN-LAST:event_btnLoadCopyTextActionPerformed

    private void btnLoadDjvuXMLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoadDjvuXMLActionPerformed
        final JFileChooser fc = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("XML file", "xml");
        fc.addChoosableFileFilter(filter);
        
        if(this.workingParentDir != null) {
            fc.setCurrentDirectory(this.workingParentDir);
        }
        
        //In response to a button click:
        int returnVal = fc.showOpenDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            
            if(file.getName().toLowerCase().endsWith(".xml")) {
                loadDjvuXMLFile(file);
                
                findRelatedFiles();
            } else {
                JOptionPane.showMessageDialog(this, "You can select only XML files");
            }
        }
    }//GEN-LAST:event_btnLoadDjvuXMLActionPerformed

    private void btnStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStartActionPerformed
        try {
            startParsing();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }//GEN-LAST:event_btnStartActionPerformed

    private void btnConfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfActionPerformed
        openConfiguration();
    }//GEN-LAST:event_btnConfActionPerformed

    private void lblDjvuXMLMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblDjvuXMLMouseClicked
        if(this.djvuXMLFile != null && this.djvuXMLFile.exists() && this.djvuXMLFile.isFile()) {
            try {
                Runtime.getRuntime().exec(new String[]{"gedit", this.djvuXMLFile.getAbsolutePath()});
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }
    }//GEN-LAST:event_lblDjvuXMLMouseClicked

    private void lblCopiedTextMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblCopiedTextMouseClicked
        if(this.copiedTextFile != null && this.copiedTextFile.exists() && this.copiedTextFile.isFile()) {
            try {
                Runtime.getRuntime().exec(new String[]{"gedit", this.copiedTextFile.getAbsolutePath()});
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }
    }//GEN-LAST:event_lblCopiedTextMouseClicked

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

    private void cboParagraphAlgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboParagraphAlgActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboParagraphAlgActionPerformed

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
                new NCEDjvuGui().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnConf;
    private javax.swing.JButton btnFinish;
    private javax.swing.JButton btnLoadCopyText;
    private javax.swing.JButton btnLoadDjvu;
    private javax.swing.JButton btnLoadDjvuXML;
    private javax.swing.JButton btnNextPage;
    private javax.swing.JButton btnPrevPage;
    private javax.swing.JButton btnReprocessPage;
    private javax.swing.JButton btnSpecialFemale;
    private javax.swing.JButton btnSpecialMale;
    private javax.swing.JButton btnSpecialMercury;
    private javax.swing.JButton btnStart;
    private javax.swing.JComboBox cboParagraphAlg;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblCopiedText;
    private javax.swing.JLabel lblDjvu;
    private javax.swing.JLabel lblDjvuXML;
    private javax.swing.JLabel lblPage;
    private javax.swing.JTextArea txtPara;
    // End of variables declaration//GEN-END:variables
}
