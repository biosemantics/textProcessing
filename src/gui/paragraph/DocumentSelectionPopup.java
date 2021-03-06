/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.paragraph;

import common.db.DBUtil;
import common.utils.StringUtil;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import paragraph.ParagraphLoader;
import paragraph.bean.Document;
import paragraph.db.DBFacade;
import paragraph.db.DocumentTable;

/**
 *
 * @author iychoi
 */
public class DocumentSelectionPopup extends javax.swing.JDialog {

    private File workingParentDir;
    private File djvuFile;
    private File textFile;
    private int documentID;
    
    /**
     * Creates new form DocumentSelectionPopup
     */
    public DocumentSelectionPopup(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    
    private void loadDjvuFile(File file) {
        this.djvuFile = file;
        this.workingParentDir = file.getParentFile();

        this.lblDjvuNew.setText(file.getName());
    }
    
    private void loadTextFile(File file) {
        this.workingParentDir = file.getParentFile();
        this.textFile = file;

        this.lblLoadTextNew.setText(file.getName());
    }
    
    private void findRelatedFiles() {
        if(this.workingParentDir == null)
            return;
        
        String filenameprefix = null;
        if(this.djvuFile != null) {
            filenameprefix = this.djvuFile.getName();
        } else if(this.textFile != null) {
            filenameprefix = this.textFile.getName();
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
                } else if(file.getName().toLowerCase().endsWith(".txt")) {
                    if(this.textFile == null) {
                        loadTextFile(file);
                    }
                }
            }
        }
    }
    
    private void addEntryOnTable(Document document) {
        DefaultTableModel model = (DefaultTableModel)this.tblDocuments.getModel();
        Object[] rowData = new Object[2];
        rowData[0] = document.getDocumentID();
        rowData[1] = document.getFilename();
        
        model.addRow(rowData);
    }
    
    public int getSelectedDocumentID() {
        return this.documentID;
    }
    
    public File getSelectedDjvuFile() {
        return this.djvuFile;
    }
    
    public File getSelectedTextFile() {
        return this.textFile;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnLoadDjvuNew = new javax.swing.JButton();
        lblDjvuNew = new javax.swing.JLabel();
        lblLoadTextNew = new javax.swing.JLabel();
        btnLoadTextNew = new javax.swing.JButton();
        btnLoad = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblDocuments = new javax.swing.JTable();
        btnDelete = new javax.swing.JButton();
        btnResume = new javax.swing.JButton();
        btnNew = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        btnLoadDjvuNew.setText("Load Djvu");
        btnLoadDjvuNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoadDjvuNewActionPerformed(evt);
            }
        });

        lblDjvuNew.setText("Not Loaded");

        lblLoadTextNew.setText("Not Loaded");
        lblLoadTextNew.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblLoadTextNewMouseClicked(evt);
            }
        });

        btnLoadTextNew.setText("Load Text");
        btnLoadTextNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoadTextNewActionPerformed(evt);
            }
        });

        btnLoad.setText("Load");
        btnLoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoadActionPerformed(evt);
            }
        });

        tblDocuments.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "id", "filename"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblDocuments.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblDocuments.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tblDocuments);

        btnDelete.setText("Delete");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnResume.setText("Continue");
        btnResume.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResumeActionPerformed(evt);
            }
        });

        btnNew.setText("New");
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnLoadDjvuNew)
                            .addComponent(btnLoadTextNew))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblLoadTextNew)
                            .addComponent(lblDjvuNew))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnLoad, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 497, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnResume, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnNew, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnLoadDjvuNew)
                            .addComponent(lblDjvuNew))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnLoadTextNew)
                            .addComponent(lblLoadTextNew)))
                    .addComponent(btnLoad, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnResume, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnNew, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnLoadDjvuNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoadDjvuNewActionPerformed
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
    }//GEN-LAST:event_btnLoadDjvuNewActionPerformed

    private void lblLoadTextNewMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblLoadTextNewMouseClicked
        if(this.textFile != null && this.textFile.exists() && this.textFile.isFile()) {
            try {
                Runtime.getRuntime().exec(new String[]{"gedit", this.textFile.getAbsolutePath()});
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }
    }//GEN-LAST:event_lblLoadTextNewMouseClicked

    private void btnLoadTextNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoadTextNewActionPerformed
        final JFileChooser fc = new JFileChooser();

        if(this.workingParentDir != null) {
            fc.setCurrentDirectory(this.workingParentDir);
        }

        //In response to a button click:
        int returnVal = fc.showOpenDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            loadTextFile(file);

            findRelatedFiles();
        }
    }//GEN-LAST:event_btnLoadTextNewActionPerformed

    private void btnLoadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoadActionPerformed
        try {
            Connection conn = DBUtil.getConnection();

            // create empty tables if necessary
            DBFacade.createTables(conn);

            // DocumentTable
            List<Document> documents = DocumentTable.getDocuments(conn, this.textFile);
            DefaultTableModel model = (DefaultTableModel)this.tblDocuments.getModel();
            model.setRowCount(0);
            for(Document document : documents) {
                addEntryOnTable(document);
            }
            conn.close();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }//GEN-LAST:event_btnLoadActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        int selectedRow = this.tblDocuments.getSelectedRow();
        if(selectedRow >= 0 && selectedRow < this.tblDocuments.getRowCount()) {
            int id = Integer.parseInt(this.tblDocuments.getModel().getValueAt(selectedRow, 0).toString());
            this.documentID = id;
            JOptionPane.showMessageDialog(this, "Delete : documentID = " + id);

            try {
                Connection conn = DBUtil.getConnection();

                // create empty tables if necessary
                DBFacade.createTables(conn);

                DocumentTable.deleteDocument(conn, this.documentID);

                // DocumentTable
                List<Document> documents = DocumentTable.getDocuments(conn, this.textFile);
                DefaultTableModel model = (DefaultTableModel) this.tblDocuments.getModel();
                model.setRowCount(0);
                for (Document document : documents) {
                    addEntryOnTable(document);
                }
                conn.close();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Select a document that you want to delete");
        }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnResumeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResumeActionPerformed
        int selectedRow = this.tblDocuments.getSelectedRow();
        if(selectedRow >= 0 && selectedRow < this.tblDocuments.getRowCount()) {
            int id = Integer.parseInt(this.tblDocuments.getModel().getValueAt(selectedRow, 0).toString());
            this.documentID = id;
            JOptionPane.showMessageDialog(this, "Continue work : documentID = " + id);

            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Select a document that you want to continue");
        }
    }//GEN-LAST:event_btnResumeActionPerformed

    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        try {
            ParagraphLoader loader = new ParagraphLoader(this.textFile);

            Connection conn = DBUtil.getConnection();
            Document document = loader.loadToDatabase(conn);
            this.documentID = document.getDocumentID();
            conn.close();

            this.dispose();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }//GEN-LAST:event_btnNewActionPerformed

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
            java.util.logging.Logger.getLogger(DocumentSelectionPopup.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DocumentSelectionPopup.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DocumentSelectionPopup.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DocumentSelectionPopup.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                DocumentSelectionPopup dialog = new DocumentSelectionPopup(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnLoad;
    private javax.swing.JButton btnLoadDjvuNew;
    private javax.swing.JButton btnLoadTextNew;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnResume;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblDjvuNew;
    private javax.swing.JLabel lblLoadTextNew;
    private javax.swing.JTable tblDocuments;
    // End of variables declaration//GEN-END:variables
}
