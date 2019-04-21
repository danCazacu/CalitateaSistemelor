package main.graphicalInterface.tableRecord;

import main.exception.FieldValueNotSet;
import main.model.Column;
import main.model.Field;
import main.model.Table;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class TableModel extends AbstractTableModel {

    private Table table;
    private String[] columnNames;
    private Class[] columnTypes;
    private Object[][] data;

    public TableModel(){}

    public TableModel(Table inputTable) throws FieldValueNotSet {

        this.table = inputTable;
        columnNames = new String[this.table.getColumnNames().size() + 1];
        columnTypes = new Class[this.table.getColumnNames().size() + 1];

        columnNames[0] = "Select";
        columnTypes[0] = Boolean.class;

        for (int i = 0; i < this.table.getColumnNames().size(); i++) {

            columnNames[i + 1] = this.table.getColumnNames().get(i);

            if (this.table.getColumn(this.table.getColumnNames().get(i)).getType().equals(Column.Type.INT)) {

                columnTypes[i + 1] = Integer.class;
            } else {

                columnTypes[i + 1] = String.class;
            }
        }

        data = new Object[this.table.getNumberOfRows()][this.table.getColumnNames().size() + 1];

        int columnIndex = 1;
        for (Column column : this.table.getData().keySet()) {

            List<Field> fieldValues = this.table.getData().get(column);
            int rowIndex = 0;
            for (Field field : fieldValues) {

                if (field.isIntValueSet()) {

                    data[rowIndex++][columnIndex] = field.getIntValue();
                } else {

                    data[rowIndex++][columnIndex] = field.getStringValue();
                }
            }

            columnIndex++;
        }

        //set the first column to false: Boolean (not selected)
        for (int i = 0; i < this.table.getNumberOfRows(); i++) {

            data[i][0] = false;
        }
    }

    /**
     * Returns the number of rows in the model. A
     * <code>JTable</code> uses this method to determine how many rows it
     * should display.  This method should be quick, as it
     * is called frequently during rendering.
     *
     * @return the number of rows in the model
     * @see #getColumnCount
     */
    @Override
    public int getRowCount() {
        return this.table.getNumberOfRows();
    }

    /**
     * Returns the number of columns in the model. A
     * <code>JTable</code> uses this method to determine how many columns it
     * should create and display by default.
     *
     * @return the number of columns in the model
     * @see #getRowCount
     */
    @Override
    public int getColumnCount() {
        return this.table.getColumnNames().size() + 1;
    }

    /**
     * Returns a default name for the column using spreadsheet conventions:
     * A, B, C, ... Z, AA, AB, etc.  If <code>column</code> cannot be found,
     * returns an empty string.
     *
     * @param column the column being queried
     * @return a string containing the default name of <code>column</code>
     */
    @Override
    public String getColumnName(int column) {

        return columnNames[column];
    }

    /**
     * Returns the value for the cell at <code>columnIndex</code> and
     * <code>rowIndex</code>.
     *
     * @param rowIndex    the row whose value is to be queried
     * @param columnIndex the column whose value is to be queried
     * @return the value Object at the specified cell
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        return data[rowIndex][columnIndex];
    }

    /**
     * Returns false.  This is the default implementation for all cells.
     *
     * @param rowIndex    the row being queried
     * @param columnIndex the column being queried
     * @return false
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {

        if (columnIndex == 0) {

            return true;
        }
        return false;
    }

    /**
     * Returns <code>Object.class</code> regardless of <code>columnIndex</code>.
     *
     * @param columnIndex the column being queried
     * @return the Object.class
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {

        return columnTypes[columnIndex];
    }

    /**
     * This empty implementation is provided so users don't have to implement
     * this method if their data model is not editable.
     *
     * @param aValue      value to assign to cell
     * @param rowIndex    row of cell
     * @param columnIndex column of cell
     */
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

        if (columnIndex == 0) {

            if (data[rowIndex][columnIndex].equals(Boolean.FALSE)) {

                data[rowIndex][columnIndex] = true;
                TableContentFrame.getInstance().setAreRowsSelected(true);
            } else {

                data[rowIndex][columnIndex] = false;

                //disable button delete button if nothing is selected
                boolean isRowSelected = false;
                for (int i = 0; i < getRowCount() && !isRowSelected; i++) {

                    if (data[i][0].equals(Boolean.TRUE)) {

                        isRowSelected = true;
                    }
                }

                if (isRowSelected) {

                    TableContentFrame.getInstance().setAreRowsSelected(true);

                } else {

                    TableContentFrame.getInstance().setAreRowsSelected(false);
                }
            }
        }
    }
}
