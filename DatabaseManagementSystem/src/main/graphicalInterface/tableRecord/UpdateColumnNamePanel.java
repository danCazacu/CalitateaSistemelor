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
    JComboBox<Column> lstColumns;

    JLabel lblNewValue;
    JTextField txtNewColumnName;

    JPanel contentPanel;

    public UpdateColumnNamePanel(Table table) {

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

        Object result = null;
        if (isReopened) {

            result = JOptionPane.showConfirmDialog(null, contentPanel,
                    title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
        } else {

            result = JOptionPane.showConfirmDialog(null, contentPanel,
                    title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        }
        return result;
    }

}