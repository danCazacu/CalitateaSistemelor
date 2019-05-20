package main.graphicalInterface.tableRecord;

import main.model.Column;
import main.model.FieldComparator;
import main.model.Table;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;


public class UpdateColumnNamePanel {

    JLabel lblColumn;
    private JComboBox<Column> lstColumns;

    JLabel lblNewValue;
    private JTextField txtNewColumnName;

    JPanel contentPanel;

    Table table;

    public UpdateColumnNamePanel(){

    }


    public void setInputTable(Table inputTable) {

        assert inputTable != null: "Precondition failed: input parameter can not be null";

        this.table = inputTable;
        init();
    }

    public void init() {

        contentPanel = new JPanel();
        lblColumn = new JLabel("Columns: ");

        lstColumns = new JComboBox(table.getData().keySet().toArray());

        lblNewValue = new JLabel("Value: ");
        txtNewColumnName = new JTextField(20);

        contentPanel.add(lblColumn);
        contentPanel.add(lstColumns);
        contentPanel.add(lblNewValue);
        contentPanel.add(txtNewColumnName);
    }

    public Object openPopUp(String title, boolean isReopened) {

        assert title != null: "Precondition failed: input parameter can not be null";


        Object result;
        if (isReopened) {

            result = JOptionPane.showConfirmDialog(null, contentPanel,
                    title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
        } else {

            result = JOptionPane.showConfirmDialog(null, contentPanel,
                    title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        }
        return result;
    }

    public JComboBox<Column> getLstColumns() {
        return lstColumns;
    }

    public JTextField getTxtNewColumnName() {
        return txtNewColumnName;
    }
}
