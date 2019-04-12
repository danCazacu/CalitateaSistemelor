package main.model;

import main.exception.InexistentColumn;
import main.exception.WrongTypeInColumnException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
     *
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
     * @param columnName
     * @return Columns instance with name, NULL if no such column
     */
    public Column getColumn(String columnName){
        for (Column column:data.keySet()) {
            if(column.getName().equalsIgnoreCase(columnName))
                return column;
        }
        return null;
    }
    /**
     *
     * @param columnNames
     * @return a list of all columns as Column class instances
     */
    public List<Column> getColumnsByColumnNames(List<String> columnNames) {
        List<Column> columns = new ArrayList<>();
        for (String columnName:columnNames) {
            Column column = getColumn(columnName);
            if(column!=null)
                columns.add(column);
        }
        return columns;
    }

    /**
     *
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

    @Override
    public int hashCode() {
        int superHash = super.hashCode();
        for (Column column: data.keySet()) {
            superHash+= column.hashCode();
            for (Field field:this.data.get(column)) {
                superHash+=field.hashCode();
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
                    if(!otherTableColumnData.contains(field))
                        return false;
                }
            }
        }
        return true;


    }
}
