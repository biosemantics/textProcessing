/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.paragraph;

import common.db.DBUtil;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import paragraph.bean.Document;
import paragraph.bean.ParagraphType;
import paragraph.bean.RegEx;
import paragraph.db.RegExTable;

/**
 *
 * @author iychoi
 */
public class RegExAdditionPopup extends javax.swing.JDialog {

    private Document document;
    private List<RegEx> regexs;
    
    /**
     * Creates new form RegExAdditionPopup
     */
    public RegExAdditionPopup(Document document, java.awt.Frame parent, boolean modal) throws IOException {
        super(parent, modal);
        
        this.document = document;
        
        initComponents();
        
        this.regexs = loadRegExs();
        showRegExTable();
        initCombobox();
    }
    
    private List<RegEx> loadRegExs() throws IOException {
        Connection conn = DBUtil.getConnection();
        List<RegEx> regexs = RegExTable.getRegExs(conn);

        try {
            conn.close();
        } catch (SQLException ex) {
            throw new IOException(ex.getMessage());
        }
        return regexs;
    }
    
    private void showRegExTable() {
        DefaultTableModel model = (DefaultTableModel) this.tblRegExs.getModel();

        model.setRowCount(0);
        for (RegEx regex : this.regexs) {
            Object[] rowData = new Object[5];
            rowData[0] = regex.getRegexID();
            rowData[1] = regex.getDocumentID();
            rowData[2] = regex.getRegex();
            rowData[3] = regex.getDescription();
            rowData[4] = regex.getParagraphTypeString();

            model.addRow(rowData);
        }
    }
    
    private void initCombobox() {
        ParagraphType[] types = ParagraphType.values();
        for (ParagraphType type : types) {
            this.cboParagraphType.addItem(type.name());
        }
    }
    
    private void addNewRegEx(int documentID, String regex, String description, ParagraphType type) throws IOException {
        Connection conn = DBUtil.getConnection();

        int priority = RegExTable.getMaxRegExPriority(conn, documentID);
        
        RegEx regex_new = new RegEx();
        regex_new.setRegex(regex);
        regex_new.setDescription(description);
        regex_new.setDocumentID(documentID);
        regex_new.setParagraphType(type);
        regex_new.setPriority(priority + 1);
        
        RegExTable.insertRegEx(conn, regex_new);
        
        try {
            conn.close();
        } catch (SQLException ex) {
            throw new IOException(ex.getMessage());
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

        jScrollPane3 = new javax.swing.JScrollPane();
        tblRegExs = new javax.swing.JTable();
        btnCopyRegEx = new javax.swing.JButton();
        txtDescription = new javax.swing.JTextField();
        lblDescription = new javax.swing.JLabel();
        lblRegEx = new javax.swing.JLabel();
        txtRegEx = new javax.swing.JTextField();
        lblParagraphType = new javax.swing.JLabel();
        cboParagraphType = new javax.swing.JComboBox();
        btnSave = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        tblRegExs.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "id", "document_id", "expression", "description", "paragraph_type"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblRegExs.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(tblRegExs);

        btnCopyRegEx.setText("Copy");
        btnCopyRegEx.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCopyRegExActionPerformed(evt);
            }
        });

        lblDescription.setText("Description");

        lblRegEx.setText("RegEx");

        lblParagraphType.setText("ParagraphType");

        btnSave.setText("Save");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 476, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblParagraphType)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboParagraphType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnSave, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnCopyRegEx, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblRegEx)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtRegEx))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblDescription)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDescription)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCopyRegEx)
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDescription, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDescription))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblRegEx)
                    .addComponent(txtRegEx, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblParagraphType)
                    .addComponent(cboParagraphType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSave)
                .addGap(14, 14, 14))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCopyRegExActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCopyRegExActionPerformed
        int selectedRow = this.tblRegExs.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < this.tblRegExs.getRowCount()) {
            int regexID = Integer.parseInt(this.tblRegExs.getModel().getValueAt(selectedRow, 0).toString());

            RegEx regex_edit = null;
            // find paragraph
            for (RegEx regex : this.regexs) {
                if (regex.getRegexID() == regexID) {
                    regex_edit = regex;
                    break;
                }
            }

            if (regex_edit == null) {
                this.txtRegEx.setText("");
                this.txtDescription.setText("");
                JOptionPane.showMessageDialog(this, "regex not found");
            } else {
                // update text;
                this.txtRegEx.setText(regex_edit.getRegex());
                this.txtDescription.setText(regex_edit.getDescription());
                this.cboParagraphType.setSelectedItem(regex_edit.getParagraphTypeString());
            }
        } else {
            this.txtRegEx.setText("");
            this.txtDescription.setText("");
        }
    }//GEN-LAST:event_btnCopyRegExActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        try {
            String typeString = this.cboParagraphType.getSelectedItem().toString();
            ParagraphType type = ParagraphType.valueOf(typeString);

            String regex = this.txtRegEx.getText().trim();
            String description = this.txtDescription.getText().trim();

            addNewRegEx(this.document.getDocumentID(), regex, description, type);

            this.dispose();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }//GEN-LAST:event_btnSaveActionPerformed

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
            java.util.logging.Logger.getLogger(RegExAdditionPopup.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RegExAdditionPopup.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RegExAdditionPopup.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RegExAdditionPopup.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        /*
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                RegExAdditionPopup dialog = new RegExAdditionPopup(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
        */
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCopyRegEx;
    private javax.swing.JButton btnSave;
    private javax.swing.JComboBox cboParagraphType;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblDescription;
    private javax.swing.JLabel lblParagraphType;
    private javax.swing.JLabel lblRegEx;
    private javax.swing.JTable tblRegExs;
    private javax.swing.JTextField txtDescription;
    private javax.swing.JTextField txtRegEx;
    // End of variables declaration//GEN-END:variables
}