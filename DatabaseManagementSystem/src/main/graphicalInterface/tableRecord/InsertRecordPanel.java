package main.graphicalInterface.tableRecord;

import main.model.Column;
import main.model.Table;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static java.awt.Color.RED;

public class InsertRecordPanel {

    JPanel contentPanel;

    Map<JLabel, JTextField> mapColumnNameValue;

    Table inputTable;

    public InsertRecordPanel(){

    }

    public void setInputTable(Table inputTable) {

        this.inputTable = inputTable;
        init();
    }

    private void init(){

        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        mapColumnNameValue = new HashMap<>();

        for(Column column: inputTable.getData().keySet()){

            JLabel columnLabel = new JLabel(column.getName());
            JTextField txtField = new JTextField(15);

            contentPanel.add(columnLabel);
            contentPanel.add(txtField);

            mapColumnNameValue.put(columnLabel, txtField);
        }
    }

    public Object openPopUp(String title, boolean isReopened) {

        Object result = null;
        if (isReopened) {

            JLabel errorLabel = new JLabel(title);
            errorLabel.setForeground(RED);
            contentPanel.add(errorLabel);

            result = JOptionPane.showConfirmDialog(null, contentPanel,
                    title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
            contentPanel.remove(errorLabel);
        } else {

            result = JOptionPane.showConfirmDialog(null, contentPanel,
                    title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        }
        return result;
    }


}
