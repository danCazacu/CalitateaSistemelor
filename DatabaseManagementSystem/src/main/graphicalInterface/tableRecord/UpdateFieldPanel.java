package main.graphicalInterface.tableRecord;

import main.model.Column;
import main.model.FieldComparator;
import main.model.Table;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;


public class UpdateFieldPanel {

    JLabel lblNewValue;
    private JTextField txtNewValue;

    JLabel lblColumns;
    private JComboBox<Column> lstColumns;

    JLabel lblWhere;

    JLabel lblOperator;
    private JComboBox<FieldComparator.Sign> cbOperators;

    JLabel lblMatchValue;
    private JTextField textMatchValue;

    JPanel contentPanel;

    private Table table;


    public void setTable(Table inputTable) {

        assert inputTable != null: "Precondition failed: input parameter can not be null";


        this.table = inputTable;
        init();
    }

    public void init() {

        contentPanel = new JPanel();

        lblNewValue = new JLabel("Set Value");
        txtNewValue = new JTextField(20);

        lblColumns = new JLabel("Columns: ");
        lstColumns = new JComboBox(table.getData().keySet().toArray());

        lblWhere = new JLabel("WHERE");

        lblOperator = new JLabel("Operator: ");
        cbOperators = new JComboBox<>(FieldComparator.Sign.values());

        lblMatchValue = new JLabel("Value: ");
        textMatchValue = new JTextField(20);

        contentPanel.add(lblNewValue);
        contentPanel.add(txtNewValue);
        contentPanel.add(lblColumns);
        contentPanel.add(lstColumns);
        contentPanel.add(lblWhere);
        contentPanel.add(cbOperators);
        contentPanel.add(lblMatchValue);
        contentPanel.add(textMatchValue);
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

    public JTextField getTxtNewValue() {
        return txtNewValue;
    }

    public JComboBox<Column> getLstColumns() {
        return lstColumns;
    }

    public JComboBox<FieldComparator.Sign> getCbOperators() {
        return cbOperators;
    }

    public JTextField getTextMatchValue() {
        return textMatchValue;
    }
}
