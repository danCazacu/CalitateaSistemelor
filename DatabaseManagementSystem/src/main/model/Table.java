package main.model;

import main.exception.ColumnAlreadyExists;
import main.exception.InexistentColumn;
import main.exception.NotSameFieldType;
import main.exception.WrongTypeInColumnException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Table {
    private Map<Column, List<Field>> data;
    private String name;

    public Table(String name, List<Column> columnNames) {
        this.name = name;
        data = new HashMap<>();
        for (Column col : columnNames) {
            data.put(col, new ArrayList<>());
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<Column, List<Field>> getData() {
        return data;
    }

    /**
     * @return a list with all column names as string
     * You can use get getColumnsByColumnNames to get them as Column class instance
     */
    public List<String> getColumnNames() {
        List<String> colNames = new ArrayList<>();
        for (Column col : data.keySet()) {
            colNames.add(col.getName());
        }
        return colNames;
    }

    /**
     * Uses equalsIgnoreCase for match
     *
     * @param columnName
     * @return Columns instance with name, NULL if no such column
     */
    public Column getColumn(String columnName) {
        for (Column column : data.keySet()) {
            if (column.getName().equalsIgnoreCase(columnName))
                return column;
        }
        return null;
    }

    /**
     * @param columnNames
     * @return a list of all columns as Column class instances
     */
    public List<Column> getColumnsByColumnNames(List<String> columnNames) {
        List<Column> columns = new ArrayList<>();
        for (String columnName : columnNames) {
            Column column = getColumn(columnName);
            if (column != null)
                columns.add(column);
        }
        return columns;
    }

    /**
     * @param rowNumber
     * @return a row with mapping from Column to Field
     */
    public Map<Column, Field> getRow(int rowNumber) {
        Map<Column, Field> row = new HashMap<>();
        if (rowNumber < 0)
            return row;
        for (Column column : data.keySet()) {
            row.put(column, data.get(column).get(rowNumber));
        }
        return row;
    }

    /**
     * @param row - map where key is column name and field is the value that need to be inserted into that column
     */
    public void insert(Map<String, Field> row) throws WrongTypeInColumnException, InexistentColumn {
        List<String> columnNames = getColumnNames();
        //check for invalid column names
        for (String columnName : row.keySet()) {
            if (!columnNames.contains(columnName))
                throw new InexistentColumn(columnName);

        }
        //check for incompatible column types
        for (String columnName : row.keySet()) {
            for (Column col : data.keySet()) {
                if (col.getName().equalsIgnoreCase(columnName)) {
                    if (!col.getType().equals(row.get(columnName).getType()))
                        throw new WrongTypeInColumnException(col.getType(), row.get(columnName).getType(), columnName);
                }
            }
        }
        //if all ok add data
        for (String columnName : row.keySet()) {
            for (Column col : data.keySet()) {
                if (col.getName().equalsIgnoreCase(columnName)) {
                    data.get(col).add(row.get(columnName));
                }
            }

        }
    }

    /**
     * Use this as where clause. Then use other functions as actions over the result.
     * @param columnName
     * @param sign
     * @param value
     * @return All rows from this table that match with input parameters.
     * @throws WrongTypeInColumnException
     * @throws NotSameFieldType
     */
    public Map<Column, List<Field>> where(String columnName, FieldComparator.Sign sign, Field value) throws WrongTypeInColumnException, NotSameFieldType {
        FieldComparator comparator = new FieldComparator();
        //prepare result
        Map<Column, List<Field>> result = new HashMap<>();
        for (Column col : this.getData().keySet()) {
            result.put(col, new ArrayList<>());
        }

        Map<Column, List<Field>> tableData = this.getData();
        for (Column col : tableData.keySet()) {
            if (col.getName().equalsIgnoreCase(columnName)) {
                if (!col.getType().equals(value.getType())) {
                    throw new WrongTypeInColumnException(col.getType(), value.getType(), col.getName());
                }

                List<Field> columnData = tableData.get(col);
                for (Field field : columnData) {
                    if (comparator.compareWithSign(field,value,sign)) {
                        Map<Column, Field> row = this.getRow(columnData.indexOf(field));
                        for (Column rowColumn : row.keySet()) {
                            result.get(rowColumn).add(row.get(rowColumn));
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     *
     * @param data returned by where function
     * @param selectColumns columns that should remain after filtering
     * @return trims data parameter and leaves only selected columns
     */
    public Map<Column,List<Field>> select(Map<Column, List<Field>> data, List<Column> selectColumns){
        for (Column column: data.keySet()) {
            if(!selectColumns.contains(column)){
                data.remove(column);
            }
        }
        return data;
    }

    public void deleteColumn(Column column){
        data.remove(column);
    }

    public void insertColumn(Column column, List<Field> data) throws ColumnAlreadyExists {
        if(this.data.keySet().contains(column))
            throw new ColumnAlreadyExists(column.getName());
        this.data.put(column,data);
    }

    @Override
    public int hashCode() {
        int superHash = super.hashCode();
        for (Column column : data.keySet()) {
            superHash += column.hashCode();
            for (Field field : this.data.get(column)) {
                superHash += field.hashCode();
            }
        }
        return superHash;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Table))
            return false;
        Table table = (Table) obj;
        if (!this.name.equalsIgnoreCase(table.name))
            return false;
        for (Column column : this.data.keySet()) {
            if (!table.data.containsKey(column))
                return false;
            else {
                List<Field> otherTableColumnData = table.data.get(column);
                for (Field field : this.data.get(column)) {
                    if (!otherTableColumnData.contains(field))
                        return false;
                }
            }
        }
        return true;
    }
}
