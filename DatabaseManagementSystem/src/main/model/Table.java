package main.model;

import main.exception.*;
import main.persistance.PersistenceContants;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

import static main.model.Column.Type.INT;
import static main.model.Column.Type.STRING;
import static main.service.FilteringService.isValid;

public class Table {
    private Map<Column, List<Field>> data;
    private String name;

    public Table(String name, List<Column> columnNames) throws InvalidValue {

        assert name != null && !name.trim().isEmpty() : "Precondition failed: null or empty input name";
        assert isValid(name): "Precondition failed: " + new InvalidValue(name);
        assert columnNames != null : "Precondition failed: null columns list";

        this.name = name;
        data = new HashMap<>();
        for (Column col : columnNames) {

            checkColumn(col, 2);
            data.put(col, new ArrayList<>());
        }

        assert this.name.equals(name): "Post-condition failed: table name set incorrectly... " + this.name + " != " + name;
        for (Column col : columnNames) {

            checkColumn(col, 2);
            assert this.data.keySet().contains(col) : "Post-condition failed: the column \"" + col.getName() + "\" from input does not exist in the table";
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws InvalidValue {

        assert name != null && !name.trim().isEmpty(): "Precondition failed: null or empty input name value";
        assert isValid(name): "Precondition failed: invalid table name input";
        this.name = name;

        assert this.name.equals(name) : "Post-condition failed: the name wasn't set correctly ... " + this.name + " != " + name;
    }

    public Map<Column, List<Field>> getData() {

        assert data != null;
        return data;
    }

    /**
     * @return a list with all column names as string
     * You can use get getColumnsByColumnNames to get them as Column class instance
     */
    public List<String> getColumnNames() {
        List<String> colNames = new ArrayList<>();

        assert data != null : "Precondition failed: no data in table";
        for (Column col : data.keySet()) {

            checkColumn(col, 2);
            colNames.add(col.getName());
        }

        assert colNames.size() == data.keySet().size(): "Post-condition failed: not all columns name were saved";
        return colNames;
    }

    /**
     * Uses equalsIgnoreCase for match
     *
     * @param columnName
     * @return Columns instance with name, NULL if no such column
     */
    public Column getColumn(String columnName) throws DoesNotExist {

        assert columnName != null && !columnName.trim().isEmpty(): "Precondition failed: null or empty input column name value";
        assert isValid(columnName): "Invariant failed: invalid column name input" + new InvalidValue(columnName);

        for (Column column : data.keySet()) {

            checkColumn(column, 2);
            if (column.getName().equalsIgnoreCase(columnName)) {

                assert column.getName().equalsIgnoreCase(columnName) : "Post-condition failed " + column.getName() + " != " + columnName;
                return column;
            }
        }

       assert data.get(columnName) == null : "Post-condition failed: column name exist but wasn't found";
       throw new DoesNotExist(columnName);
    }

    /**
     * @param columnNames
     * @return a list of all columns as Column class instances
     */
    public List<Column> getColumnsByColumnNames(List<String> columnNames) throws DoesNotExist {

        assert columnNames != null : "Precondition failed .. null input for column names list";

        List<Column> columns = new ArrayList<>();
        for (String columnName : columnNames) {

            assert isValid(columnName) : "Invariant failed: invalid column name: " + new InvalidValue(columnName);
            Column column = getColumn(columnName);
            checkColumn(column, 2);
            if (column != null) {

                assert column.getName().equalsIgnoreCase(columnName);
                columns.add(column);
            }
        }

        for(Column column: columns){

            checkColumn(column, 2);
            assert columnNames.contains(column.getName()) : "Post-condition failed ... " + column.getName() + " does not exist in input list ";
        }

        return columns;
    }

    /**
     * @param rowNumber
     * @return a row with mapping from Column to Field
     */
    public Map<Column, Field> getRow(int rowNumber) {

        assert rowNumber >= 0 && rowNumber < getNumberOfRows() : "Precondition failed: invalid number of row";
        Map<Column, Field> row = new HashMap<>();
        for (Column column : data.keySet()) {

            checkColumn(column, 2);
            row.put(column, data.get(column).get(rowNumber));
        }

        assert row.keySet().size() == data.keySet().size(): "Post-condition failed: different numbers of columns";
        return row;
    }

    /**
     * @param row - map where key is column name and field is the value that need to be inserted into that column
     */
    public void insert(Map<String, Field> row) throws TypeMismatchException, InexistentColumn, InvalidValue {

        assert row != null : "Precondition failed: the input is null";
        //validate row
        List<String> columnNames = getColumnNames();
        //check for invalid column names
        for (String columnName : row.keySet()) {
            if (!columnNames.contains(columnName))
                throw new InexistentColumn(columnName);

        }
        //check for incompatible column types
        for (String columnName : row.keySet()) {

            assert isValid(columnName): "Invariant failed: invalid column name ... " + new InvalidValue(columnName);
            for (Column col : data.keySet()) {

                checkColumn(col, 2);
                if (col.getName().equalsIgnoreCase(columnName)) {

                    assert col.getType() != null: "Column type is null";
                    if (!col.getType().equals(row.get(columnName).getType())) {

                        throw new TypeMismatchException(col.getType(), row.get(columnName).getType(), columnName);
                    }
                }
            }
        }

        //if all ok add data
        for (Column col: data.keySet()) {
            checkColumn(col, 2);
            if(row.containsKey(col.getName())){
                data.get(col).add(row.get(col.getName()));
            } else{ // add a default value
                Field field = new Field();
                if(col.getType().equals(INT))
                    field.setValue(0);
                if(col.getType().equals(STRING))
                    field.setValue("");
                data.get(col).add(field);

                assert field.getType() != null : "Post-condition failed: the type of the field wasn't set";
                try{
                    if(field.getType().equals(INT)){

                        assert field.getIntValue() != null && field.getIntValue() == 0 : "Post-condition failed: value of the INT field wasn't set correctly";
                    }else{

                        assert field.getStringValue() != null && field.getStringValue().isEmpty() : "Post-condition failed: value of the STRING field wasn't set correctly";
                    }
                } catch (FieldValueNotSet fieldValueNotSet) {
                    //fieldValueNotSet.printStackTrace();
                    assert true: "Field not set was thrown and shouldn't be here... ";
                }

                assert data.get(col).contains(field) : "Post-condition failed: the field wasn't added correctly";
            }
        }
    }

    public void updateWhere(String columnName, FieldComparator.Sign sign, Field value, Field newValue) throws TypeMismatchException {

        assert columnName != null && !columnName.trim().isEmpty(): "Precondition failed: the input for column name is null or empty";
        assert sign != null && sign.getValue() != null && Arrays.asList(FieldComparator.Sign.values()).contains(sign): "Precondition failed: sign is null or has an incorrect value";
        checkField(value, true);
        checkField(newValue, true);

        assert this.getData() != null : "Precondition failed: the table has no data";
        FieldComparator comparator = new FieldComparator();

        Map<Column, List<Field>> tableData = this.getData();
        for (Column col : tableData.keySet()) {

            checkColumn(col, 2);

            if (col.getName().equalsIgnoreCase(columnName)) {

                if (!col.getType().equals(value.getType())) {
                    throw new TypeMismatchException(col.getType(), value.getType(), col.getName());
                }

                List<Field> columnData = tableData.get(col);
                assert columnData.size() == tableData.get(col).size() : "The saved list of values is different than the corresponding list of field from table data";
                for (Field field : columnData) {

                    checkField(field, false);

                    if (comparator.compareWithSign(field, value, sign)) {

                        assert !field.equals(newValue): "Fields are already equals";
                        field.copyFrom(newValue);

                        assert field.equals(newValue): "Post-condition failed: Fields aren't equals, the update wasn't made";
                    }
                }
            }
        }
    }

    public void deleteWhere(String columnName, FieldComparator.Sign sign, Field value) throws TypeMismatchException {

        assert columnName != null && !columnName.trim().isEmpty(): "Precondition failed: the input for column name is null or empty";
        assert sign != null && sign.getValue() != null && Arrays.asList(FieldComparator.Sign.values()).contains(sign): "Precondition failed: sign is null or has an incorrect value";
        checkField(value, true);

        while(deleteOneWhere(columnName,sign,value)){

        }

        assert !deleteOneWhere(columnName, sign, value): "Post-condition failed: not all values that met the condition were deleted";
    }

    /**
     *  IMPORTANT!!! usage: call multiple times until you receive false return.
     *  This function deletes only one row at a time and it is necessary to call multiple times in order to delete all affected rows
     * @param columnName
     * @param sign
     * @param value
     * @return
     * @throws TypeMismatchException
     */

    //TODO: tomorrow
    public boolean deleteOneWhere(String columnName, FieldComparator.Sign sign, Field value) throws TypeMismatchException {

        assert columnName != null && !columnName.trim().isEmpty(): "Precondition failed: the input for column name is null or empty";
        assert sign != null && sign.getValue() != null && Arrays.asList(FieldComparator.Sign.values()).contains(sign): "Precondition failed: sign is null or has an incorrect value";
        checkField(value, true);

        FieldComparator comparator = new FieldComparator();

        Map<Column, List<Field>> tableData = this.getData();
        for (Column col : tableData.keySet()) {

            checkColumn(col, 2);
            if (col.getName().equalsIgnoreCase(columnName)) {
                if (!col.getType().equals(value.getType())) {

                    throw new TypeMismatchException(col.getType(), value.getType(), col.getName());
                }

                List<Field> columnData = tableData.get(col);
                for(int i=0; i<columnData.size(); i++){

                    assert i >= 0 && i < columnData.size() : "Invariant failed: index out of bound";
                    if (comparator.compareWithSign(columnData.get(i), value, sign)) {
                        this.deleteRow(i);
                        return true;
                    }
                }
            }
        }
        return false;
    }


    /**
     * Use this as where clause. Then use other functions as actions over the result.
     *
     * @param columnName
     * @param sign
     * @param value
     * @return All rows from this table that match with input parameters.
     * @throws TypeMismatchException
     */
    public Map<Column, List<Field>> where(String columnName, FieldComparator.Sign sign, Field value) throws TypeMismatchException {

        assert columnName != null && !columnName.trim().isEmpty(): "Precondition failed: the input for column name is null or empty";
        assert sign != null && sign.getValue() != null && Arrays.asList(FieldComparator.Sign.values()).contains(sign): "Precondition failed: sign is null or has an incorrect value";
        checkField(value, true);

        FieldComparator comparator = new FieldComparator();
        //prepare result
        Map<Column, List<Field>> result = new HashMap<>();
        for (Column col : this.getData().keySet()) {

            checkColumn(col, 2);
            result.put(col, new ArrayList<>());
        }

        Map<Column, List<Field>> tableData = this.getData();
        for (Column col : tableData.keySet()) {

            checkColumn(col, 2);
            if (col.getName().equalsIgnoreCase(columnName)) {
                if (!col.getType().equals(value.getType())) {
                    throw new TypeMismatchException(col.getType(), value.getType(), col.getName());
                }

                List<Field> columnData = tableData.get(col);
                for (Field field : columnData) {
                    checkField(field, false); // invariant

                    if (comparator.compareWithSign(field, value, sign)) {
                        Map<Column, Field> row = this.getRow(columnData.indexOf(field));
                        for (Column rowColumn : row.keySet()) {

                            assert isValid(rowColumn.getName()) : "Invariant failed: invalid column name: " + new InvalidValue(col.getName());
                            result.get(rowColumn).add(row.get(rowColumn));
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * @param data          returned by where function
     * @param selectColumns columns that should remain after filtering
     * @return trims data parameter and leaves only selected columns
     */
    public Map<Column, List<Field>> select(Map<Column, List<Field>> data, List<Column> selectColumns) {

        assert data != null : "Precondition failed: input data is null";
        assert selectColumns != null : "Precondition failed: input columns is null";

        Map<Column, List<Field>> result = new HashMap<>();
        for (Column col : selectColumns) {

            checkColumn(col, 2);
            result.put(col, data.get(col));
        }
        return result;
    }

    public int getNumberOfRows() {

        assert data != null : "Precondition failed: table has no data (null)";
        //check if the table has anything in it
        if (data.keySet().size() > 0) {

            Column column = (Column) data.keySet().toArray()[0];
            checkColumn(column, 2);

            return data.get(column).size();
        }

        return 0;
    }

    public boolean deleteRow(int rowNumber) {

        assert rowNumber > -1 && rowNumber < getNumberOfRows() : "Precondition failed: invalid row number" ;
        if (rowNumber < getNumberOfRows()) {
            for (Column column : data.keySet()) {

                checkColumn(column, 2);
                data.get(column).remove(rowNumber);
            }
            return true;
        }
        return false;
    }

    public void deleteRows(Map<Column, List<Field>> rowsAffected) throws Exception {

        assert rowsAffected != null : "Precondition failed: input is null";

        for (Column dataColumn : data.keySet()) {

            checkColumn(dataColumn, 2);
            List<Field> affectedRow = rowsAffected.get(dataColumn);
            List<Field> dataRow = data.get(dataColumn);
            for (Field affectedRowField : affectedRow) {

                checkField(affectedRowField, false);
                if (!dataRow.contains(affectedRowField))
                    throw new Exception("Field not match");
            }
        }

        for (Column dataColumn : data.keySet()) {
            List<Field> affectedRow = rowsAffected.get(dataColumn);
            List<Field> dataRow = data.get(dataColumn);
            for (Field affectedRowField : affectedRow) {

                checkField(affectedRowField, false);
                dataRow.remove(affectedRowField);
            }
        }
    }

    public void deleteColumn(Column column) throws DoesNotExist {

        checkColumn(column, 1);

        if (!this.data.containsKey(column))
            throw new DoesNotExist(column.getName());

        assert this.data.get(column) != null: "Column does not exist, an exception should be thrown";
        data.remove(column);

        assert this.data.get(column) == null: "Postcondition failed, the column was not delete" ;
    }

    public void insertColumn(Column column) throws ColumnAlreadyExists {

        checkColumn(column, 1);
        for (Column col : this.data.keySet()) {
            checkColumn(col, 2);
            if (col.getName().equalsIgnoreCase(column.getName()))
                throw new ColumnAlreadyExists(column.getName());
        }

        assert this.data.get(column) == null : "Column does not exist, but the exception wasn't thrown";

        this.data.put(column, new ArrayList<>());
        if (column.getType().equals(STRING)) {
            for (int i = 0; i < getNumberOfRows(); i++) {

                assert i >= 0 && i < getNumberOfRows(): "Invariant failed: index out of bound";
                try {
                    this.data.get(column).add(new Field(""));
                } catch (InvalidValue invalidValue) {
                    invalidValue.printStackTrace();
                }
            }

        }
        if (column.getType().equals(INT)) {
            for (int i = 0; i < getNumberOfRows(); i++) {

                assert i >= 0 && i < getNumberOfRows() : "Invariant failed: index out of bound";
                this.data.get(column).add(new Field(0));
            }
        }

        assert this.data.get(column) != null: "Post-condition failed: the column wasn't added";
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

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.getName()).append("\n");
        for (Column column : data.keySet()) {
            stringBuilder.append(column.getName() + " | ");
        }
        stringBuilder.append("\n");
        for (int i = 0; i < getNumberOfRows(); i++) {
            for (Column column : data.keySet()) {
                stringBuilder.append(data.get(column).get(i).toString() + " | ");
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    public static String toString(Map<Column, List<Field>> input) {
        StringBuilder stringBuilder = new StringBuilder();
        if (input.keySet().size() == 0)
            return stringBuilder.toString();

        for (Column column : input.keySet()) {
            stringBuilder.append(column.getName() + " | ");
        }

        Column reference = (Column) input.keySet().toArray()[0];
        int size = input.get(reference).size();

        stringBuilder.append("\n");
        for (int i = 0; i < size; i++) {
            for (Column column : input.keySet()) {
                stringBuilder.append(input.get(column).get(i).toString() + " | ");
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    public void persist(OutputStream outputstream) throws IOException {

        outputstream.write(PersistenceContants.START_TABLE.getBytes());
        outputstream.write("\n".getBytes());
        outputstream.write(this.getName().getBytes());
        outputstream.write("\n".getBytes());

        for (Column column : data.keySet()) {
            outputstream.write(PersistenceContants.START_COLUMN.getBytes());
            outputstream.write("\n".getBytes());
            outputstream.write(column.getName().getBytes());
            outputstream.write("\n".getBytes());
            outputstream.write(column.getType().toString().getBytes());
            outputstream.write("\n".getBytes());

            for (Field field : data.get(column)) {
                try {
                    field.persist(outputstream);
                } catch (FieldValueNotSet fieldValueNotSet) {
                    fieldValueNotSet.printStackTrace();
                }
            }
            outputstream.write(PersistenceContants.END_COLUMN.getBytes());
            outputstream.write("\n".getBytes());
        }


        outputstream.write(PersistenceContants.END_TABLE.getBytes());
        outputstream.write("\n".getBytes());
    }


    private void checkField(Field field, boolean isPrecondition) {

        if(isPrecondition) {

            assert field != null : "Precondition failed ... input field is null";
            assert field.getType() != null : "Precondition failed... input field has no type set";
        }else{

            assert field != null : "Invariant failed ... input field is null";
            assert field.getType() != null : "Invariant failed... input field has no type set";
        }

        try {
            if (field.getType().equals(STRING)) {

                assert field.isStringValueSet() && field.getStringValue() != null && !field.getStringValue().trim().isEmpty() : "Field of type STRING wasn't correctly created";
            } else {

                assert field.isIntValueSet() && field.getIntValue() != null : "Field of type INT wasn't correctly created";
            }
        } catch (FieldValueNotSet fieldValueNotSet) {

            //fieldValueNotSet.printStackTrace();
            assert true: "Field not set was thrown and shouldn't be here... ";
        }
    }

    private void checkColumn(Column column, int Precondition1_Invariant2_Postcondition3){

        String msgNullColumn = "";
        String msgInvalidName = "";
        String msgWrongType = "";

        if(Precondition1_Invariant2_Postcondition3 == 1){

            msgNullColumn = "Precondition failed: ";
            msgInvalidName = "Precondition failed: ";
            msgWrongType = "Precondition failed: ";
        }else if (Precondition1_Invariant2_Postcondition3 == 2){

            msgNullColumn = "Invariant failed: ";
            msgInvalidName = "Invariant failed: ";
            msgWrongType = "Invariant failed: ";
        }else{

            msgNullColumn = "Post-condition failed: ";
            msgInvalidName = "Post-condition failed: ";
            msgWrongType = "Post-condition failed: ";
        }

        msgNullColumn += " the column is null";
        msgInvalidName += "the column name is not valid ... \" + new InvalidValue(column.getName())";
        msgWrongType += "the column type is null or is not one of the existing type values";

        assert column != null : msgNullColumn;
        assert isValid(column.getName()): msgInvalidName;
        assert column.getType() != null && Arrays.asList(Column.Type.values()).contains(column.getType()): msgWrongType;
    }
}
