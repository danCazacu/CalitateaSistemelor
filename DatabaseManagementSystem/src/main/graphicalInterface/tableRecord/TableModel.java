package main.graphicalInterface.tableRecord;

import main.exception.FieldValueNotSet;
import main.model.Field;
import main.model.Table;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.Vector;

public class TableModel extends AbstractTableModel {

    private Table table ;
    private Class[] colTypes;
    private String[] columnNames ;
    private Vector data;

    public TableModel(Table table) throws FieldValueNotSet {

        this.table = table;
        List<String> columnNamesList = table.getColumnNames();

        columnNames = new String[columnNamesList.size()];
        columnNamesList.toArray(columnNames);

         colTypes = new Class[columnNamesList.size()];

         int count = 0;
         for(String colName: columnNames){

             colTypes[count++] = table.getColumn(colName).getType().getClass();
         }

        for(int colCount = 0;  colCount < columnNames.length ; colCount++) {

            List<Field> lstFields = table.getData().get(table.getColumn(columnNames[colCount]));
            if (lstFields != null) {
                int rowCount = 0;
                for (Field field : lstFields) {

                    if (field.isIntValueSet()) {

                        setValueAt(field.getIntValue(), rowCount++, colCount );
                    } else {

                        setValueAt(field.getStringValue(), rowCount++, colCount);
                    }
                }
            }
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
        return columnNames.length;
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
        return 0 ;
    }

    public String getColumnName(int col) {

        return columnNames[col];
    }
    public Class getColumnClass(int col) {
        if (col == 2) {
            return Double.class;
        }
        else {
            return String.class;
        }
    }
}
