/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.djvu;

import djvu.DjvuConfiguration;

/**
 *
 * @author iychoi
 */
public class DjvuConfigurationPopup extends javax.swing.JDialog {

    private DjvuConfiguration conf;
    
    /**
     * Creates new form DjvuConfigurationPopup
     */
    public DjvuConfigurationPopup(DjvuConfiguration conf, java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        this.conf = conf;
        
        loadValues();
    }
    
    public DjvuConfiguration getConfiguration() {
        return this.conf;
    }
    
    private void loadValues() {
        this.txtPageStart.setText(Integer.toString(this.conf.getPageStart()));
        this.txtPageEnd.setText(Integer.toString(this.conf.getPageEnd()));
        
        this.txtMarginLeft.setText(Integer.toString(this.conf.getMarginLeft()));
        this.txtMarginRight.setText(Integer.toString(this.conf.getMarginRight()));
        this.txtMarginTop.setText(Integer.toString(this.conf.getMarginTop()));
        this.txtMarginBottom.setText(Integer.toString(this.conf.getMarginBottom()));
        
        this.txtVGap.setText(Integer.toString(this.conf.getParagraphVGapMinThreshold()));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtPageEnd = new javax.swing.JTextField();
        btnApply = new javax.swing.JButton();
        lblMargin = new javax.swing.JLabel();
        lblPageSep = new javax.swing.JLabel();
        lblMarginTop = new javax.swing.JLabel();
        txtMarginTop = new javax.swing.JTextField();
        lblMarginLeft = new javax.swing.JLabel();
        lblMarginBottom = new javax.swing.JLabel();
        txtMarginLeft = new javax.swing.JTextField();
        txtPageStart = new javax.swing.JTextField();
        lblMarginRight = new javax.swing.JLabel();
        txtMarginRight = new javax.swing.JTextField();
        lblPage = new javax.swing.JLabel();
        txtMarginBottom = new javax.swing.JTextField();
        lblVGap = new javax.swing.JLabel();
        txtVGap = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        btnApply.setText("Apply");
        btnApply.setToolTipText("");
        btnApply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnApplyActionPerformed(evt);
            }
        });

        lblMargin.setText("Margin");

        lblPageSep.setText("~");

        lblMarginTop.setText("Top");

        lblMarginLeft.setText("Left");

        lblMarginBottom.setText("Bottom");

        lblMarginRight.setText("Right");

        lblPage.setText("Pages");

        lblVGap.setText("Paragraph Vertical Gap");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnApply))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblPage)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtPageStart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblPageSep)
                                .addGap(19, 19, 19)
                                .addComponent(txtPageEnd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblMargin)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblMarginTop)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtMarginTop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblMarginLeft)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtMarginLeft, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblMarginRight)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtMarginRight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblMarginBottom)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtMarginBottom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblVGap)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtVGap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPage)
                    .addComponent(txtPageStart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPageEnd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPageSep))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblMargin)
                    .addComponent(lblMarginTop)
                    .addComponent(txtMarginTop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblMarginLeft)
                    .addComponent(txtMarginLeft, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblMarginRight)
                    .addComponent(txtMarginRight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblMarginBottom)
                    .addComponent(txtMarginBottom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblVGap)
                    .addComponent(txtVGap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnApply)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnApplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApplyActionPerformed
        try {
            this.conf.setPageStart(Integer.parseInt(this.txtPageStart.getText()));
        } catch(Exception ex) {
        }

        try {
            this.conf.setPageEnd(Integer.parseInt(this.txtPageEnd.getText()));
        } catch(Exception ex) {
        }

        try {
            int marginLeft = Integer.parseInt(this.txtMarginLeft.getText());
            int marginTop = Integer.parseInt(this.txtMarginTop.getText());
            int marginRight = Integer.parseInt(this.txtMarginRight.getText());
            int marginBottom = Integer.parseInt(this.txtMarginBottom.getText());
            this.conf.setMargin(marginLeft, marginRight, marginTop, marginBottom);
            
            int vgap = Integer.parseInt(this.txtVGap.getText());
            this.conf.setParagraphVGapMinThreshold(vgap);
        } catch(Exception ex) {
        }

        this.dispose();
    }//GEN-LAST:event_btnApplyActionPerformed

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
            java.util.logging.Logger.getLogger(DjvuConfigurationPopup.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DjvuConfigurationPopup.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DjvuConfigurationPopup.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DjvuConfigurationPopup.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        /*
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                DjvuConfigurationPopup dialog = new DjvuConfigurationPopup(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnApply;
    private javax.swing.JLabel lblMargin;
    private javax.swing.JLabel lblMarginBottom;
    private javax.swing.JLabel lblMarginLeft;
    private javax.swing.JLabel lblMarginRight;
    private javax.swing.JLabel lblMarginTop;
    private javax.swing.JLabel lblPage;
    private javax.swing.JLabel lblPageSep;
    private javax.swing.JLabel lblVGap;
    private javax.swing.JTextField txtMarginBottom;
    private javax.swing.JTextField txtMarginLeft;
    private javax.swing.JTextField txtMarginRight;
    private javax.swing.JTextField txtMarginTop;
    private javax.swing.JTextField txtPageEnd;
    private javax.swing.JTextField txtPageStart;
    private javax.swing.JTextField txtVGap;
    // End of variables declaration//GEN-END:variables
}
