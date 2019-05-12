package graphicalInterface.tableRecord;

import main.exception.InvalidValue;
import main.graphicalInterface.tableRecord.InsertRecordPanel;
import main.model.Column;
import main.model.Table;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests suit for {@link InsertRecordPanel}
 */
public class InsertRecordPanelTest {

    InsertRecordPanel insertRecordPanel = new InsertRecordPanel();

    @Test
    public void givenInsertRecord_showPopUp() throws InvalidValue {

        List<Column> lstColumns = new ArrayList<>();
        lstColumns.add(new Column("Col1_int", Column.Type.INT));
        lstColumns.add(new Column("Col2_int", Column.Type.INT));
        lstColumns.add(new Column("Col3_string", Column.Type.STRING));
        lstColumns.add(new Column("Col4_string", Column.Type.STRING));

        Table table = new Table("test", lstColumns);
        insertRecordPanel.setInputTable(table);

        /*
        check if exist JLabel for each column
         */
        Set<JLabel> labels = insertRecordPanel.getMapColumnNameValue().keySet();

        for (JLabel label : labels) {

            assertTrue(table.getColumnNames().contains(label.getText()));
        }
    }
}
