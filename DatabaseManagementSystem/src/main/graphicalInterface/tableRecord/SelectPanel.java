package main.graphicalInterface.tableRecord;

import javafx.scene.control.CheckBox;
import main.model.FieldComparator;
import main.model.Table;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class SelectPanel extends JPanel {

    JLabel lblColumns;
    JList<CheckBox> lstColumns;

    JCheckBox whereCheckBox;

    JLabel lblOperator;
    JComboBox<FieldComparator.Sign> cbOperators;

    JLabel lblMatchValue;
    JTextField textMatchValue;

    JPanel panelWHERE;
    JPanel contentPanel;

    /* JComboBox<FieldComparator.Sign> comboOperators = new JComboBox(FieldComparator.Sign.values());
            JTextField matchValue = new JTextField(20);

            JCheckBox checkBoxWhere = new JCheckBox("WHERE");

            GridLayout layout = new GridLayout();
            layout.setColumns(1);

            JPanel insertColumnPanel = new JPanel(layout);
            //insertColumnPanel.add(new JLabel("Please enter the column name and select the column type")); //message

            insertColumnPanel.add(new JLabel("Columns: "));
            //TODO add combo with selection
            //insertColumnPanel.add(Box.createHorizontalStrut(5)); // a spacer

            insertColumnPanel.add(checkBoxWhere);

            //insertColumnPanel.add(new JLabel("Operator:"));
            insertColumnPanel.add(comboOperators);

            JPanel matchPanel = new JPanel();
            matchPanel.add(new JLabel("Value: "));
            matchPanel.add(matchValue);

            insertColumnPanel.add(matchPanel);*/

    public SelectPanel(Table table) {

        this.contentPanel = this;
        lblColumns = new JLabel("Columns: ");

        //DefaultListModel<JCheckBox> cbListModel = new DefaultListModel<JCheckBox>();
        lstColumns = new JList<>();

        for(String columnName: table.getColumnNames()){

            lstColumns.add(new JCheckBox(columnName));
        }

        whereCheckBox = new JCheckBox("WHERE");

        lblOperator = new JLabel("Operator: ");
        cbOperators = new JComboBox<>(FieldComparator.Sign.values());

        lblMatchValue = new JLabel("Value: ");
        textMatchValue = new JTextField(20);

        whereCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {

                if (e.getStateChange() == ItemEvent.SELECTED) {

                    panelWHERE.setVisible(true);
                   /* contentPanel.add(panelWHERE);
                    contentPanel.revalidate();
                    contentPanel.repaint();*/
                } else {

                    //contentPanel.remove(panelWHERE);
                    //contentPanel.revalidate();
                    panelWHERE.setVisible(false);
                }
            }
        });

        panelWHERE = new JPanel();
        panelWHERE.setVisible(false);
        panelWHERE.add(lblOperator);
        panelWHERE.add(cbOperators);
        panelWHERE.add(lblMatchValue);
        panelWHERE.add(textMatchValue);

        this.add(lblColumns);
        this.add(lstColumns);
        this.add(whereCheckBox);

        this.add(panelWHERE);
    }
}
