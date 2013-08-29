/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import common.utils.RegExUtil;
import common.utils.StreamUtil;
import common.utils.StringUtil;
import input.djvu.DjvuConfiguration;
import input.djvu.DjvuLine;
import input.djvu.DjvuLineFilter;
import input.djvu.DjvuLineTextCorrector;
import input.djvu.DjvuPage;
import input.djvu.DjvuParagraphAlg;
import input.djvu.DjvuParagraphExtractor;
import input.djvu.DjvuXMLReader;
import input.djvu.algorithms.DjvuLineParagraphAlgCaption;
import input.djvu.algorithms.DjvuLineParagraphAlgGap;
import input.djvu.algorithms.DjvuLineParagraphAlgIndent;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author iychoi
 */
public class NCEGui extends javax.swing.JFrame {

    private DjvuView djvuView;
    private int currentPage;
    private File workingParentDir;
    private File djvuXMLFile;
    private File djvuFile;
    private File copiedTextFile;
    private List<File> pageOutputFiles;
    
    private int startPage = 0;
    private int endPage = Integer.MAX_VALUE;
    private int topMargin = 200;
    private int sideMargin = 0;
    private int bottomMargin = 200;
    
    
    private DjvuConfiguration djvuConf;
    private List<DjvuPage> parsedPages;
            
    /**
     * Creates new form NCEGui
     */
    public NCEGui() {
        initComponents();
        
        init();
    }
    
    private void init() {
        this.djvuConf = new DjvuConfiguration();
        this.djvuConf.setPageRegion(this.startPage, this.endPage);
        this.djvuConf.setMarginLeft(this.sideMargin);
        this.djvuConf.setMarginRight(this.sideMargin);
        this.djvuConf.setMarginTop(this.topMargin);
        this.djvuConf.setMarginBottom(this.bottomMargin);
    }
    
    public int GetScreenWorkingWidth() {
        return java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width;
    }

    public int GetScreenWorkingHeight() {
        return java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;
    }
    
    public Insets getScreenInsets() {
        Insets si = Toolkit.getDefaultToolkit().getScreenInsets(new Frame().getGraphicsConfiguration());
        return si;
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
        NCEConfGui confGui = new NCEConfGui(this.djvuConf);
        final JFrame currentWindow = this;
        confGui.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                currentWindow.setEnabled(true);
            }
        });

        currentWindow.setEnabled(false);
        confGui.setAlwaysOnTop(true);
        confGui.setResizable(false);
        confGui.setLocationRelativeTo(getRootPane());
        confGui.setVisible(true);
        confGui.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
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
        
        if(this.pageOutputFiles != null) {
            for(File file : this.pageOutputFiles) {
                file.delete();
            }
            this.pageOutputFiles.clear();
        }
        this.pageOutputFiles = new ArrayList<File>();
    }
    
    private void processFirstPage() {
        if(this.parsedPages != null && this.parsedPages.size() > 0) {
            processPage(this.parsedPages.get(0).getPagenum());
        } else {
            processPage(1);
        }
    }
    
    private void processNextPage() {
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
                processPage(targetPage.getPagenum());
            } else {
                processPage(this.currentPage + 1);
            }
        }
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
                File pageOutput = File.createTempFile("NCE_TEMP_Page" + this.currentPage + "_", ".txt", this.workingParentDir);

                FileWriter writer = new FileWriter(pageOutput, false);
                writer.write(this.txtPara.getText());
                writer.close();

                this.pageOutputFiles.add(pageOutput);
            } catch (IOException ex) {
                throw new Exception(ex.getMessage());
            }
        }
    }
    
    private void combineAllPages() throws Exception {
        if(this.pageOutputFiles != null) {
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
                    
                    for(File pageFile : this.pageOutputFiles) {
                        String pageString = StreamUtil.readFileString(pageFile);
                        writer.write(pageString);
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
    }
    
    private void processPage(int pageNum) {
        boolean findPage = false;
        DjvuPage targetPage = null;
        for(DjvuPage page : this.parsedPages) {
            if(page.getPagenum() == pageNum) {
                findPage = true;
                targetPage = page;
            }
        }
        
        if(findPage) {
            DjvuParagraphExtractor paraExtractor = new DjvuParagraphExtractor();
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
            
            List<String> paragraphs = paraExtractor.extractParagraphs(targetPage, filter, paraAlgs, this.djvuConf);

            String paraText = "";

            for(String paragraph : paragraphs) {
                paraText += paragraph;
                paraText += "\n\n";
            }
            
            this.txtPara.setText(paraText);
            
            this.movePage(pageNum);
        } else {
            this.txtPara.setText("[CONTENT END!]");
        }
        
        this.txtPara.setCaretPosition(0);
        this.currentPage = pageNum;
    }

    private void loadDjvuFile(File file) {
        this.djvuFile = file;
        this.workingParentDir = file.getParentFile();

        loadView(file);

        this.lblDjvu.setText(file.getName());

        int scrWidth = GetScreenWorkingWidth();
        int scrHeight = GetScreenWorkingHeight();

        Insets scrInsets = getScreenInsets();

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnLoadDjvu)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblDjvu)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 155, Short.MAX_VALUE)
                                .addComponent(btnConf))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnLoadDjvuXML)
                                    .addComponent(btnLoadCopyText))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblCopiedText)
                                    .addComponent(lblDjvuXML))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnStart, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(lblPage)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnNextPage)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnFinish)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNextPage)
                    .addComponent(lblPage)
                    .addComponent(btnFinish))
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
            java.util.logging.Logger.getLogger(NCEGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(NCEGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(NCEGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(NCEGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new NCEGui().setVisible(true);
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
    private javax.swing.JButton btnStart;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblCopiedText;
    private javax.swing.JLabel lblDjvu;
    private javax.swing.JLabel lblDjvuXML;
    private javax.swing.JLabel lblPage;
    private javax.swing.JTextArea txtPara;
    // End of variables declaration//GEN-END:variables
}
