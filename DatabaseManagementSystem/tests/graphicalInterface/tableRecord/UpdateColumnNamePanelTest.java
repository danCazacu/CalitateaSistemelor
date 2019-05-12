package graphicalInterface.tableRecord;

import main.exception.InvalidValue;
import main.graphicalInterface.tableRecord.UpdateColumnNamePanel;
import main.model.Column;
import main.model.Table;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests suit for {@link UpdateColumnNamePanel}
 */
public class UpdateColumnNamePanelTest {

    UpdateColumnNamePanel updateColumnNamePanel = new UpdateColumnNamePanel();

    @Test
    public void testUpdateColumnNamePanel() throws InvalidValue {

        List<Column> lstColumns = new ArrayList<>();
        lstColumns.add(new Column("Col1_int", Column.Type.INT));
        lstColumns.add(new Column("Col2_int", Column.Type.INT));
        lstColumns.add(new Column("Col3_string", Column.Type.STRING));
        lstColumns.add(new Column("Col4_string", Column.Type.STRING));

        Table table = new Table("test", lstColumns);
        updateColumnNamePanel.setInputTable(table);

        JComboBox<Column> cbColumns = updateColumnNamePanel.getLstColumns();
        for(int i = 0 ; i < cbColumns.getModel().getSize(); i++){

            assertTrue(table.getColumnNames().contains(cbColumns.getModel().getElementAt(i).getName()));
        }

        assertTrue(updateColumnNamePanel.getTxtNewColumnName().getText().isEmpty());
    }
}
