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

    public List<String> getColumnNames() {
        List<String> colNames = new ArrayList<>();
        for (Column col : data.keySet()) {
            colNames.add(col.getName());
        }
        return colNames;
    }

    public List<Column> getColumnsByColumnNames(List<String> columnNames) {
        List<Column> columns = new ArrayList<>();
        for (String column : columnNames) {
            for (Column col : data.keySet()) {
                if (col.getName().equals(column)) {
                    columns.add(col);
                }
            }
        }
        return columns;
    }

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
                if (col.getName().equals(columnName)) {
                    if (!col.getType().equals(row.get(columnName).getType()))
                        throw new WrongTypeInColumnException(col.getType(), row.get(columnName).getType(), columnName);
                }
            }
        }
        //if all ok add data
        for (String columnName : row.keySet()) {
            for (Column col : data.keySet()) {
                if (col.getName().equals(columnName)) {
                    data.get(col).add(row.get(columnName));
                }
            }

        }
    }


}
