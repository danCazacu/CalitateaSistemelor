package main.graphicalInterface.tableRecord;

import main.model.FieldComparator;
import main.model.Table;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;


public class SelectPanel {

    JLabel lblColumns;
    JList<CheckListItem> lstColumns;

    JCheckBox whereCheckBox;

    JLabel lblOperator;
    JComboBox<FieldComparator.Sign> cbOperators;

    JLabel lblMatchValue;
    JTextField textMatchValue;

    JPanel panelWHERE;
    JPanel contentPanel;

    public SelectPanel( Table table) {

        contentPanel = new JPanel();
        lblColumns = new JLabel("Columns: ");

        lstColumns = new JList(table.getColumnNames().toArray());

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
                    contentPanel.repaint();
                    contentPanel.revalidate();

                    if(lstColumns.getSelectedValuesList().size() > 1){

                        //deselect values; let user select only ONE value if wants to use WHERE clause
                        lstColumns.clearSelection();
                        lstColumns.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                        lstColumns.setToolTipText("You can select only one value if you use WHERE clause");
                    }
                } else {

                    lstColumns.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                    panelWHERE.setVisible(false);
                    lstColumns.setToolTipText("You can select more than one value if you don't use WHERE clause");
                }
            }
        });

        panelWHERE = new JPanel();
       // panelWHERE.setVisible(false);
        panelWHERE.add(lblOperator);
        panelWHERE.add(cbOperators);
        panelWHERE.add(lblMatchValue);
        panelWHERE.add(textMatchValue);

        contentPanel.add(lblColumns);
        contentPanel.add(lstColumns);
        contentPanel.add(whereCheckBox);

        contentPanel.add(panelWHERE);

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

    class CheckListItem {

        private String label;
        private boolean isSelected = false;

        public CheckListItem(String label) {
            this.label = label;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean isSelected) {
            this.isSelected = isSelected;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    class CheckListRenderer extends JCheckBox implements ListCellRenderer {
        public Component getListCellRendererComponent(JList list, Object value,
                                                      int index, boolean isSelected, boolean hasFocus) {
            setEnabled(list.isEnabled());
            setSelected(((CheckListItem) value).isSelected());
            setFont(list.getFont());
            setBackground(list.getBackground());
            setForeground(list.getForeground());
            setText(value.toString());
            return this;
        }
    }

    public JList<CheckListItem> getLstColumns() {
        return lstColumns;
    }

    public JCheckBox getWhereCheckBox() {
        return whereCheckBox;
    }

    public JComboBox<FieldComparator.Sign> getCbOperators() {
        return cbOperators;
    }

    public JTextField getTextMatchValue() {
        return textMatchValue;
    }
}
