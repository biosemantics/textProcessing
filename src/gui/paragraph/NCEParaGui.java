/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.paragraph;

import common.db.DBUtil;
import gui.common.DjvuView;
import gui.common.ScreenUtil;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableModel;
import paragraph.RegExPriorityComparator;
import paragraph.bean.Document;
import paragraph.bean.Paragraph;
import paragraph.bean.ParagraphType;
import paragraph.bean.RegEx;
import paragraph.db.DocumentTable;
import paragraph.db.ParagraphTable;
import paragraph.db.RegExTable;

/**
 *
 * @author iychoi
 */
public class NCEParaGui extends javax.swing.JFrame {

    private Document document;
    private File textFile;
    private File djvuFile;
    private DjvuView djvuView;
    private List<Paragraph> paragraphs;
    private List<RegEx> regexs;
    private int editingParagraphID;
    private boolean tableModeShowAll = true;

    /**
     * Creates new form NCEParaGui
     */
    public NCEParaGui() throws IOException {
        //File djvuFile, File textFile, int documentID
        DocumentSelectionPopup popup = new DocumentSelectionPopup(this, true);
        popup.setVisible(true);
        
        int documentID = popup.getSelectedDocumentID();
        this.document = loadDocument(documentID);
        
        this.djvuFile = popup.getSelectedDjvuFile();
        this.textFile = popup.getSelectedTextFile();
        
        if(this.document == null || this.djvuFile == null || this.textFile == null) {
            throw new IOException("You have to select document to start program");
        }
        
        initComponents();
        
        addParagraphCellEditHandler();

        loadDjvuView(this.djvuFile);
        this.paragraphs = loadParagraphs(this.document);
        this.regexs = loadRegExs(this.document);
        showParagraphTableAll();
        showRegExTable();
        initCombobox();
    }
    
    private void addParagraphCellEditHandler() {
        this.tblParagraphs.getColumn("confirmed").getCellEditor().addCellEditorListener(new CellEditorListener() {

            @Override
            public void editingStopped(ChangeEvent ce) {
                int selectedRow = NCEParaGui.this.tblParagraphs.getSelectedRow();
                if (selectedRow >= 0 && selectedRow < NCEParaGui.this.tblParagraphs.getRowCount()) {
                    int paragraphID = Integer.parseInt(NCEParaGui.this.tblParagraphs.getModel().getValueAt(selectedRow, 0).toString());
                    boolean confirmed = Boolean.parseBoolean(NCEParaGui.this.tblParagraphs.getModel().getValueAt(selectedRow, 4).toString());
        
                    try {
                        confirmParagraphType(paragraphID, confirmed);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(NCEParaGui.this, ex.getMessage());
                    }
                }
            }

            @Override
            public void editingCanceled(ChangeEvent ce) {
            }
            
        });
    }

    private Document loadDocument(int documentID) throws IOException {
        Connection conn = DBUtil.getConnection();
        Document document = DocumentTable.getDocument(conn, documentID);
        try {
            conn.close();
        } catch (SQLException ex) {
            throw new IOException(ex.getMessage());
        }
        return document;
    }

    private List<Paragraph> loadParagraphs(Document document) throws IOException {
        Connection conn = DBUtil.getConnection();
        List<Paragraph> paragraphs = ParagraphTable.getParagraphs(conn, document.getDocumentID());

        try {
            conn.close();
        } catch (SQLException ex) {
            throw new IOException(ex.getMessage());
        }
        return paragraphs;
    }
    
    private List<RegEx> loadRegExs(Document document) throws IOException {
        Connection conn = DBUtil.getConnection();
        List<RegEx> regexs = RegExTable.getRegExs(conn, document.getDocumentID());

        try {
            conn.close();
        } catch (SQLException ex) {
            throw new IOException(ex.getMessage());
        }
        return regexs;
    }
    
    private void resizeDjvuView(int ratioDjvu, int ratioControl) {
        int scrWidth = ScreenUtil.getScreenWorkingWidth();
        int scrHeight = ScreenUtil.getScreenWorkingHeight();

        double ratio = (double)ratioDjvu / (double)(ratioDjvu + ratioControl);
        Insets scrInsets = ScreenUtil.getScreenInsets();

        int wnd1Width = (int)(scrWidth * ratio);
        int wnd2Width = (int)(scrWidth * (1 - ratio));
        int wndHeight = scrHeight;
        
        int wnd1PosX = scrInsets.left;
        int wnd1PosY = scrInsets.top;
        int wnd2PosX = scrInsets.left + wnd1Width;
        int wnd2PosY = scrInsets.top;
        
        if (this.djvuView != null) {
            this.djvuView.setSize(wnd1Width, wndHeight);
            this.djvuView.setLocation(wnd1PosX, wnd1PosY);
        }

        this.setSize(wnd2Width, wndHeight);
        this.setLocation(wnd2PosX, wnd2PosY);
    }

    private void loadDjvuView(File djvuFile) {
        if (djvuFile == null) {
            throw new IllegalArgumentException("file is null");
        }

        this.djvuView = new DjvuView(djvuFile.getAbsolutePath());
        this.djvuView.run();
        
        resizeDjvuView(1, 2);
    }

    private void showParagraphTableAll() {
        DefaultTableModel model = (DefaultTableModel) this.tblParagraphs.getModel();

        model.setRowCount(0);
        for (Paragraph paragraph : this.paragraphs) {
            Object[] rowData = new Object[5];
            rowData[0] = paragraph.getParagraphID();
            rowData[1] = paragraph.getPageNumber();
            rowData[2] = paragraph.getContent();
            rowData[3] = paragraph.getTypeString();
            rowData[4] = paragraph.getConfirmed();

            model.addRow(rowData);
        }
    }

    private void showParagraphTableUnknown() {
        DefaultTableModel model = (DefaultTableModel) this.tblParagraphs.getModel();

        model.setRowCount(0);
        for (Paragraph paragraph : this.paragraphs) {
            if(paragraph.getType().equals(ParagraphType.PARAGRAPH_UNKNOWN)) {
                Object[] rowData = new Object[5];
                rowData[0] = paragraph.getParagraphID();
                rowData[1] = paragraph.getPageNumber();
                rowData[2] = paragraph.getContent();
                rowData[3] = paragraph.getTypeString();
                rowData[4] = paragraph.getConfirmed();

                model.addRow(rowData);
            }
        }
    }
    
    private void showRegExTable() {
        Collections.sort(this.regexs, new RegExPriorityComparator());
        
        DefaultTableModel model = (DefaultTableModel) this.tblRegExs.getModel();

        model.setRowCount(0);
        for (RegEx regex : this.regexs) {
            Object[] rowData = new Object[3];
            rowData[0] = regex.getRegexID();
            rowData[1] = regex.getRegex();
            rowData[2] = regex.getDescription();

            model.addRow(rowData);
        }
    }
    
    private void moveUpRegEx(int regexID) throws IOException {
        Connection conn = DBUtil.getConnection();

        int regex_update_target_idx = -1;
        int regex_update_neighbor_idx = -1;
        // find paragraph
        for(int i=0;i<this.regexs.size();i++) {
            RegEx regex = this.regexs.get(i);
            if(regex.getRegexID() == regexID) {
                regex_update_target_idx = i;
                if(i > 0) {
                    regex_update_neighbor_idx = i-1;
                }
            }
        }
        
        if(regex_update_target_idx != -1 && regex_update_neighbor_idx != -1) {
            RegEx me = this.regexs.get(regex_update_target_idx);
            RegEx neighbor = this.regexs.get(regex_update_neighbor_idx);
            
            int priorityTemp = me.getPriority();
            me.setPriority(neighbor.getPriority());
            neighbor.setPriority(priorityTemp);
            
            RegExTable.updateRegEx(conn, me);
            RegExTable.updateRegEx(conn, neighbor);
            
            showRegExTable();
        }

        try {
            conn.close();
        } catch (SQLException ex) {
            throw new IOException(ex.getMessage());
        }
    }
    
    private void moveDownRegEx(int regexID) throws IOException {
        Connection conn = DBUtil.getConnection();

        int regex_update_target_idx = -1;
        int regex_update_neighbor_idx = -1;
        // find paragraph
        for(int i=0;i<this.regexs.size();i++) {
            RegEx regex = this.regexs.get(i);
            if(regex.getRegexID() == regexID) {
                regex_update_target_idx = i;
                if(i < this.regexs.size() - 1) {
                    regex_update_neighbor_idx = i+1;
                }
            }
        }
        
        if(regex_update_target_idx != -1 && regex_update_neighbor_idx != -1) {
            RegEx me = this.regexs.get(regex_update_target_idx);
            RegEx neighbor = this.regexs.get(regex_update_neighbor_idx);
            
            int priorityTemp = me.getPriority();
            me.setPriority(neighbor.getPriority());
            neighbor.setPriority(priorityTemp);
            
            RegExTable.updateRegEx(conn, me);
            RegExTable.updateRegEx(conn, neighbor);
            
            showRegExTable();
        }

        try {
            conn.close();
        } catch (SQLException ex) {
            throw new IOException(ex.getMessage());
        }
    }
    
    private void deleteRegEx(int regexID) throws IOException {
        Connection conn = DBUtil.getConnection();
        
        RegExTable.deleteRegEx(conn, regexID);
        this.regexs = loadRegExs(this.document);
        showRegExTable();
        
        try {
            conn.close();
        } catch (SQLException ex) {
            throw new IOException(ex.getMessage());
        }
    }
    
    private void confirmParagraphType(int paragraphID, boolean confirm) throws IOException {
        Connection conn = DBUtil.getConnection();

        Paragraph paragraph_update = null;
        // find paragraph
        for (Paragraph paragraph : this.paragraphs) {
            if (paragraph.getParagraphID() == paragraphID) {
                paragraph_update = paragraph;
                break;
            }
        }

        if (paragraph_update == null) {
            throw new IOException("paragraph not found");
        } else {
            paragraph_update.setConfirmed(confirm);
            ParagraphTable.updateParagraph(conn, paragraph_update);

            if (this.tableModeShowAll) {
                showParagraphTableAll();
            } else {
                showParagraphTableUnknown();
            }
        }

        try {
            conn.close();
        } catch (SQLException ex) {
            throw new IOException(ex.getMessage());
        }
    }

    private void updateParagraphData(int paragraphID, String content, ParagraphType type) throws IOException {
        Connection conn = DBUtil.getConnection();

        Paragraph paragraph_update = null;
        // find paragraph
        for (Paragraph paragraph : this.paragraphs) {
            if (paragraph.getParagraphID() == paragraphID) {
                paragraph_update = paragraph;
                break;
            }
        }

        if (paragraph_update == null) {
            throw new IOException("paragraph not found");
        } else {
            paragraph_update.setContent(content);
            paragraph_update.setType(type);
            paragraph_update.setConfirmed(true);
            ParagraphTable.updateParagraph(conn, paragraph_update);

            if (this.tableModeShowAll) {
                showParagraphTableAll();
            } else {
                showParagraphTableUnknown();
            }
        }

        try {
            conn.close();
        } catch (SQLException ex) {
            throw new IOException(ex.getMessage());
        }
    }
    
    private void applyRegExToUnknownParagraphs() throws IOException {
        Connection conn = DBUtil.getConnection();
        
        for (Paragraph paragraph : this.paragraphs) {
            if(paragraph.getType().equals(ParagraphType.PARAGRAPH_UNKNOWN)) {
                for (RegEx regex : this.regexs) {
                    String paragraphString = paragraph.getContent();
                    String regexString = regex.getRegex();

                    Pattern pattern = Pattern.compile(regexString);
                    Matcher matcher = pattern.matcher(paragraphString);

                    if(matcher.find()) {
                        paragraph.setType(regex.getParagraphType());
                        ParagraphTable.updateParagraph(conn, paragraph);
                        break;
                    }
                }
            }
        }
        
        if (this.tableModeShowAll) {
            showParagraphTableAll();
        } else {
            showParagraphTableUnknown();
        }
        
        try {
            conn.close();
        } catch (SQLException ex) {
            throw new IOException(ex.getMessage());
        }
    }
    
    private void applyRegExToParagraphs() throws IOException {
        Connection conn = DBUtil.getConnection();
        
        for (Paragraph paragraph : this.paragraphs) {
            if(!paragraph.getConfirmed()) {
                boolean matched = false;
                for (RegEx regex : this.regexs) {
                    String paragraphString = paragraph.getContent();
                    String regexString = regex.getRegex();

                    Pattern pattern = Pattern.compile(regexString);
                    Matcher matcher = pattern.matcher(paragraphString);

                    if (matcher.find()) {
                        paragraph.setType(regex.getParagraphType());
                        ParagraphTable.updateParagraph(conn, paragraph);
                        matched = true;
                        break;
                    }
                }

                if (!matched) {
                    paragraph.setType(ParagraphType.PARAGRAPH_UNKNOWN);
                    ParagraphTable.updateParagraph(conn, paragraph);
                }
            }
        }
        
        if (this.tableModeShowAll) {
            showParagraphTableAll();
        } else {
            showParagraphTableUnknown();
        }
        
        try {
            conn.close();
        } catch (SQLException ex) {
            throw new IOException(ex.getMessage());
        }
    }
    
    private void inheritPreviousParagraphType() throws IOException {
        Connection conn = DBUtil.getConnection();
        
        ParagraphType previousType = ParagraphType.PARAGRAPH_UNKNOWN;
        for (Paragraph paragraph : this.paragraphs) {
            if(paragraph.getType().equals(ParagraphType.PARAGRAPH_UNKNOWN)) {
                boolean bUpdated = true;
                switch(previousType) {
                    case PARAGRAPH_SYNONYM:
                        paragraph.setType(ParagraphType.PARAGRAPH_SYNONYM);
                        break;
                    case PARAGRAPH_OTHERINFO:
                        paragraph.setType(ParagraphType.PARAGRAPH_OTHERINFO);
                        break;
                    case PARAGRAPH_IGNORE:
                        paragraph.setType(ParagraphType.PARAGRAPH_OTHERINFO);
                        break;
                    case PARAGRAPH_ABSTRACT:
                        paragraph.setType(ParagraphType.PARAGRAPH_ABSTRACT_BODY);
                        break;
                    case PARAGRAPH_ABSTRACT_BODY:
                        paragraph.setType(ParagraphType.PARAGRAPH_ABSTRACT_BODY);
                        break;
                    case PARAGRAPH_AUTHOR:
                        paragraph.setType(ParagraphType.PARAGRAPH_AUTHOR_DETAILS);
                        break;
                    case PARAGRAPH_AUTHOR_DETAILS:
                        paragraph.setType(ParagraphType.PARAGRAPH_AUTHOR_DETAILS);
                        break;
                    case PARAGRAPH_INTRODUCTION:
                        paragraph.setType(ParagraphType.PARAGRAPH_INTRODUCTION_BODY);
                        break;
                    case PARAGRAPH_INTRODUCTION_BODY:
                        paragraph.setType(ParagraphType.PARAGRAPH_INTRODUCTION_BODY);
                        break;
                    case PARAGRAPH_MATERIALS_METHOD_BODY:
                        paragraph.setType(ParagraphType.PARAGRAPH_INTRODUCTION_BODY);
                        break;
                    case PARAGRAPH_DISTRIBUTION:
                        paragraph.setType(ParagraphType.PARAGRAPH_DISTRIBUTION_BODY);
                        break;
                    case PARAGRAPH_DISTRIBUTION_BODY:
                        paragraph.setType(ParagraphType.PARAGRAPH_DISTRIBUTION_BODY);
                        break;
                    case PARAGRAPH_DISTRIBUTION_WITH_BODY:
                        paragraph.setType(ParagraphType.PARAGRAPH_DISTRIBUTION_WITH_BODY);
                        break;
                    case PARAGRAPH_KEY:
                        paragraph.setType(ParagraphType.PARAGRAPH_KEY_BODY);
                        break;
                    case PARAGRAPH_KEY_BODY:
                        paragraph.setType(ParagraphType.PARAGRAPH_KEY_BODY);
                        break;
                    case PARAGRAPH_REMARKS:
                        paragraph.setType(ParagraphType.PARAGRAPH_REMARKS_BODY);
                        break;
                    case PARAGRAPH_REMARKS_BODY:
                        paragraph.setType(ParagraphType.PARAGRAPH_REMARKS_BODY);
                        break;
                    case PARAGRAPH_REMARKS_WITH_BODY:
                        paragraph.setType(ParagraphType.PARAGRAPH_REMARKS_WITH_BODY);
                        break;
                    case PARAGRAPH_ACKNOWLEDGEMENT:
                        paragraph.setType(ParagraphType.PARAGRAPH_ACKNOWLEDGEMENT_BODY);
                        break;
                    case PARAGRAPH_ACKNOWLEDGEMENT_BODY:
                        paragraph.setType(ParagraphType.PARAGRAPH_ACKNOWLEDGEMENT_BODY);
                        break;
                    case PARAGRAPH_DIAGNOSIS:
                        paragraph.setType(ParagraphType.PARAGRAPH_DIAGNOSIS_BODY);
                        break;
                    case PARAGRAPH_DIAGNOSIS_BODY:
                        paragraph.setType(ParagraphType.PARAGRAPH_DIAGNOSIS_BODY);
                        break;
                    case PARAGRAPH_DIAGNOSIS_WITH_BODY:
                        paragraph.setType(ParagraphType.PARAGRAPH_DIAGNOSIS_WITH_BODY);
                        break;
                    case PARAGRAPH_DEFINITION:
                        paragraph.setType(ParagraphType.PARAGRAPH_DEFINITION_BODY);
                        break;
                    case PARAGRAPH_DEFINITION_BODY:
                        paragraph.setType(ParagraphType.PARAGRAPH_DEFINITION_BODY);
                        break;
                    case PARAGRAPH_DEFINITION_WITH_BODY:
                        paragraph.setType(ParagraphType.PARAGRAPH_DEFINITION_WITH_BODY);
                        break;
                    case PARAGRAPH_DESCRIPTION:
                        paragraph.setType(ParagraphType.PARAGRAPH_DESCRIPTION_BODY);
                        break;
                    case PARAGRAPH_DESCRIPTION_BODY:
                        paragraph.setType(ParagraphType.PARAGRAPH_DESCRIPTION_BODY);
                        break;
                    case PARAGRAPH_DESCRIPTION_WITH_BODY:
                        paragraph.setType(ParagraphType.PARAGRAPH_DESCRIPTION_WITH_BODY);
                        break;
                    case PARAGRAPH_DISCUSSION:
                        paragraph.setType(ParagraphType.PARAGRAPH_DISCUSSION);
                        break;
                    case PARAGRAPH_DISCUSSION_BODY:
                        paragraph.setType(ParagraphType.PARAGRAPH_DISCUSSION_BODY);
                        break;
                    case PARAGRAPH_DISCUSSION_WITH_BODY:
                        paragraph.setType(ParagraphType.PARAGRAPH_DISCUSSION_WITH_BODY);
                        break;
                    case PARAGRAPH_DISCUSSION_NON_TITLED_BODY:
                        paragraph.setType(ParagraphType.PARAGRAPH_DISCUSSION_NON_TITLED_BODY);
                        break;
                    case PARAGRAPH_TYPESPECIES:
                        paragraph.setType(ParagraphType.PARAGRAPH_TYPESPECIES_BODY);
                        break;
                    case PARAGRAPH_TYPESPECIES_BODY:
                        paragraph.setType(ParagraphType.PARAGRAPH_TYPESPECIES_BODY);
                        break;
                    case PARAGRAPH_SUBTITLE:
                        paragraph.setType(ParagraphType.PARAGRAPH_SUBBODY);
                        break;
                    case PARAGRAPH_SUBBODY:
                        paragraph.setType(ParagraphType.PARAGRAPH_SUBBODY);
                        break;
                    case PARAGRAPH_SUBTITLE_WITH_BODY:
                        paragraph.setType(ParagraphType.PARAGRAPH_SUBTITLE_WITH_BODY);
                        break;
                    default:
                        bUpdated = false;
                        break;
                }
                
                if(bUpdated) {
                    ParagraphTable.updateParagraph(conn, paragraph);
                }
            }
            
            previousType = paragraph.getType();
        }
        
        if (this.tableModeShowAll) {
            showParagraphTableAll();
        } else {
            showParagraphTableUnknown();
        }
        
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

        btnUpdate = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtParagraph = new javax.swing.JTextArea();
        cboParagraphType = new javax.swing.JComboBox();
        btnEditParagraph = new javax.swing.JButton();
        btnShowAll = new javax.swing.JButton();
        btnShowUnknown = new javax.swing.JButton();
        btnEditRegEx = new javax.swing.JButton();
        btnAddRegEx = new javax.swing.JButton();
        btnRegExMoveDown = new javax.swing.JButton();
        btnRegExMoveUp = new javax.swing.JButton();
        btnApplyAllRegEx = new javax.swing.JButton();
        btnDisplay66 = new javax.swing.JButton();
        btnDisplay50 = new javax.swing.JButton();
        btnRemoveRegEx = new javax.swing.JButton();
        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        // Use this line to add a CheckBox
        JCheckBox checkBox = new javax.swing.JCheckBox();
        tblParagraphs = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblRegExs = new javax.swing.JTable();
        btnApplyRegExUnknown = new javax.swing.JButton();
        btnInherit = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btnUpdate.setText("Update");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        txtParagraph.setColumns(20);
        txtParagraph.setLineWrap(true);
        txtParagraph.setRows(5);
        txtParagraph.setWrapStyleWord(true);
        jScrollPane2.setViewportView(txtParagraph);

        btnEditParagraph.setText("Edit");
        btnEditParagraph.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditParagraphActionPerformed(evt);
            }
        });

        btnShowAll.setText("All");
        btnShowAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnShowAllActionPerformed(evt);
            }
        });

        btnShowUnknown.setText("Unknown");
        btnShowUnknown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnShowUnknownActionPerformed(evt);
            }
        });

        btnEditRegEx.setText("Edit");
        btnEditRegEx.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditRegExActionPerformed(evt);
            }
        });

        btnAddRegEx.setText("Add");
        btnAddRegEx.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddRegExActionPerformed(evt);
            }
        });

        btnRegExMoveDown.setText("Down");
        btnRegExMoveDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegExMoveDownActionPerformed(evt);
            }
        });

        btnRegExMoveUp.setText("Up");
        btnRegExMoveUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegExMoveUpActionPerformed(evt);
            }
        });

        btnApplyAllRegEx.setText("Apply All");
        btnApplyAllRegEx.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnApplyAllRegExActionPerformed(evt);
            }
        });

        btnDisplay66.setText("1:2");
        btnDisplay66.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDisplay66ActionPerformed(evt);
            }
        });

        btnDisplay50.setText("1:1");
        btnDisplay50.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDisplay50ActionPerformed(evt);
            }
        });

        btnRemoveRegEx.setText("Del");
        btnRemoveRegEx.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveRegExActionPerformed(evt);
            }
        });

        jSplitPane1.setDividerLocation(400);

        tblParagraphs.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "id", "page", "content", "type", "confirmed"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblParagraphs.setColumnSelectionAllowed(true);
        tblParagraphs.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tblParagraphs);
        tblParagraphs.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblParagraphs.getColumn("confirmed").setCellEditor(new DefaultCellEditor(checkBox));

        jSplitPane1.setLeftComponent(jScrollPane1);

        tblRegExs.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "id", "expression", "description"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
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
        tblRegExs.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        jSplitPane1.setRightComponent(jScrollPane3);

        btnApplyRegExUnknown.setText("Apply Unknown");
        btnApplyRegExUnknown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnApplyRegExUnknownActionPerformed(evt);
            }
        });

        btnInherit.setText("Inherit");
        btnInherit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInheritActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSplitPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnShowAll)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnShowUnknown)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnDisplay50)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDisplay66))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(cboParagraphType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnUpdate))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnEditParagraph)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnApplyAllRegEx)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnApplyRegExUnknown)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnInherit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 133, Short.MAX_VALUE)
                        .addComponent(btnRegExMoveUp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRegExMoveDown)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRemoveRegEx)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAddRegEx)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEditRegEx))
                    .addComponent(jScrollPane2))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnShowAll)
                    .addComponent(btnShowUnknown)
                    .addComponent(btnDisplay66)
                    .addComponent(btnDisplay50))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnEditRegEx)
                    .addComponent(btnAddRegEx)
                    .addComponent(btnRegExMoveDown)
                    .addComponent(btnRegExMoveUp)
                    .addComponent(btnEditParagraph)
                    .addComponent(btnApplyAllRegEx)
                    .addComponent(btnRemoveRegEx)
                    .addComponent(btnApplyRegExUnknown)
                    .addComponent(btnInherit))
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboParagraphType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUpdate))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnEditParagraphActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditParagraphActionPerformed
        int selectedRow = this.tblParagraphs.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < this.tblParagraphs.getRowCount()) {
            int paragraphID = Integer.parseInt(this.tblParagraphs.getModel().getValueAt(selectedRow, 0).toString());

            Paragraph paragraph_edit = null;
            // find paragraph
            for (Paragraph paragraph : this.paragraphs) {
                if (paragraph.getParagraphID() == paragraphID) {
                    paragraph_edit = paragraph;
                    break;
                }
            }

            if (paragraph_edit == null) {
                this.txtParagraph.setText("");
                this.editingParagraphID = -1;
                JOptionPane.showMessageDialog(this, "paragraph not found");
            } else {
                // update text;
                this.txtParagraph.setText(paragraph_edit.getContent());
                this.cboParagraphType.setSelectedItem(paragraph_edit.getTypeString());
                this.editingParagraphID = paragraphID;
                this.djvuView.movePage(paragraph_edit.getPageNumber());
            }
        } else {
            this.txtParagraph.setText("");
            this.editingParagraphID = -1;
        }
    }//GEN-LAST:event_btnEditParagraphActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        if (this.editingParagraphID >= 0) {
            try {
                String typeString = this.cboParagraphType.getSelectedItem().toString();
                ParagraphType type = ParagraphType.valueOf(typeString);
                updateParagraphData(this.editingParagraphID, this.txtParagraph.getText(), type);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Canceled! : ParagraphID = " + this.editingParagraphID);
        }
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnShowAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnShowAllActionPerformed
        this.tableModeShowAll = true;
        showParagraphTableAll();
    }//GEN-LAST:event_btnShowAllActionPerformed

    private void btnShowUnknownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnShowUnknownActionPerformed
        this.tableModeShowAll = false;
        showParagraphTableUnknown();
    }//GEN-LAST:event_btnShowUnknownActionPerformed

    private void btnEditRegExActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditRegExActionPerformed
        try {
            int selectedRow = this.tblRegExs.getSelectedRow();
            if (selectedRow >= 0 && selectedRow < this.tblRegExs.getRowCount()) {
                int regexID = Integer.parseInt(this.tblRegExs.getModel().getValueAt(selectedRow, 0).toString());

                RegExEditionPopup popup = new RegExEditionPopup(regexID, this, true);
                
                popup.setVisible(true);

                this.regexs = loadRegExs(this.document);
                showRegExTable();
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }//GEN-LAST:event_btnEditRegExActionPerformed

    private void btnAddRegExActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddRegExActionPerformed
        try {
            RegExAdditionPopup popup = new RegExAdditionPopup(this.document, this, true);
            popup.setVisible(true);
            
            this.regexs = loadRegExs(this.document);
            showRegExTable();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }//GEN-LAST:event_btnAddRegExActionPerformed

    private void btnRegExMoveUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegExMoveUpActionPerformed
        int selectedRow = this.tblRegExs.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < this.tblRegExs.getRowCount()) {
            int regexID = Integer.parseInt(this.tblRegExs.getModel().getValueAt(selectedRow, 0).toString());
            
            try {
                moveUpRegEx(regexID);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }
    }//GEN-LAST:event_btnRegExMoveUpActionPerformed

    private void btnRegExMoveDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegExMoveDownActionPerformed
        int selectedRow = this.tblRegExs.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < this.tblRegExs.getRowCount()) {
            int regexID = Integer.parseInt(this.tblRegExs.getModel().getValueAt(selectedRow, 0).toString());
            
            try {
                moveDownRegEx(regexID);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }
    }//GEN-LAST:event_btnRegExMoveDownActionPerformed

    private void btnApplyAllRegExActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApplyAllRegExActionPerformed
        try {
            applyRegExToParagraphs();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }//GEN-LAST:event_btnApplyAllRegExActionPerformed

    private void btnDisplay50ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDisplay50ActionPerformed
        resizeDjvuView(1, 1);
    }//GEN-LAST:event_btnDisplay50ActionPerformed

    private void btnDisplay66ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDisplay66ActionPerformed
        resizeDjvuView(1, 2);
    }//GEN-LAST:event_btnDisplay66ActionPerformed

    private void btnRemoveRegExActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveRegExActionPerformed
        try {
            int selectedRow = this.tblRegExs.getSelectedRow();
            if (selectedRow >= 0 && selectedRow < this.tblRegExs.getRowCount()) {
                int regexID = Integer.parseInt(this.tblRegExs.getModel().getValueAt(selectedRow, 0).toString());

                deleteRegEx(regexID);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }//GEN-LAST:event_btnRemoveRegExActionPerformed

    private void btnApplyRegExUnknownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApplyRegExUnknownActionPerformed
        try {
            applyRegExToUnknownParagraphs();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }//GEN-LAST:event_btnApplyRegExUnknownActionPerformed

    private void btnInheritActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInheritActionPerformed
        try {
            inheritPreviousParagraphType();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }        
    }//GEN-LAST:event_btnInheritActionPerformed

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
            java.util.logging.Logger.getLogger(NCEParaGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(NCEParaGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(NCEParaGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(NCEParaGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new NCEParaGui().setVisible(true);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                    System.exit(1);
                }
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddRegEx;
    private javax.swing.JButton btnApplyAllRegEx;
    private javax.swing.JButton btnApplyRegExUnknown;
    private javax.swing.JButton btnDisplay50;
    private javax.swing.JButton btnDisplay66;
    private javax.swing.JButton btnEditParagraph;
    private javax.swing.JButton btnEditRegEx;
    private javax.swing.JButton btnInherit;
    private javax.swing.JButton btnRegExMoveDown;
    private javax.swing.JButton btnRegExMoveUp;
    private javax.swing.JButton btnRemoveRegEx;
    private javax.swing.JButton btnShowAll;
    private javax.swing.JButton btnShowUnknown;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JComboBox cboParagraphType;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTable tblParagraphs;
    private javax.swing.JTable tblRegExs;
    private javax.swing.JTextArea txtParagraph;
    // End of variables declaration//GEN-END:variables

    private void initCombobox() {
        ParagraphType[] types = ParagraphType.values();
        for (ParagraphType type : types) {
            this.cboParagraphType.addItem(type.name());
        }
    }
}
