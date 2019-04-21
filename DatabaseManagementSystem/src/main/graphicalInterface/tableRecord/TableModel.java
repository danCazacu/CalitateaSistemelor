package main.graphicalInterface.tableRecord;

import main.exception.FieldValueNotSet;
import main.model.Column;
import main.model.Field;
import main.model.Table;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class TableModel extends AbstractTableModel {

    private Table table ;
    private String[] columnNames;
    private Class[] columnTypes;
    private Object[][] data;

    public TableModel(Table table) throws FieldValueNotSet {

        this.table = table;
        columnNames = new String[table.getColumnNames().size()];
        columnTypes = new Class[table.getColumnNames().size()];

        for(int i = 0; i < table.getColumnNames().size(); i++){

            columnNames[i] = table.getColumnNames().get(i);

            if(table.getColumn(table.getColumnNames().get(i)).getType().equals(Column.Type.INT)){

                columnTypes[i] = Integer.class;
            }else {

                columnTypes[i] = String.class;
            }
        }

        data = new Object[table.getNumberOfRows()][table.getColumnNames().size()];
        int columnIndex = 0;
        for(Column column : this.table.getData().keySet()){

            List<Field> fieldValues = this.table.getData().get(column);
            int rowIndex = 0;
            for(Field field : fieldValues){

                if(field.isIntValueSet()) {

                    data[rowIndex++][columnIndex] = field.getIntValue();
                }else{

                    data[rowIndex++][columnIndex] = field.getStringValue();
                }
            }

            columnIndex++;
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
        return table.getNumberOfRows();
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
        return table.getColumnNames().size();
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
}
