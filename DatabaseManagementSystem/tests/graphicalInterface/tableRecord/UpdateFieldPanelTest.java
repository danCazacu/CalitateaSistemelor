package graphicalInterface.tableRecord;

import main.exception.InvalidValue;
import main.graphicalInterface.tableRecord.UpdateFieldPanel;
import main.model.Column;
import main.model.FieldComparator;
import main.model.Table;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests suit for {@link UpdateFieldPanel}
 */
public class UpdateFieldPanelTest {

    UpdateFieldPanel updateFieldPanel = new UpdateFieldPanel();

    @Test
    public void testUpdateFieldPanel() throws InvalidValue {

        List<Column> lstColumns = new ArrayList<>();
        lstColumns.add(new Column("Col1_int", Column.Type.INT));
        lstColumns.add(new Column("Col2_int", Column.Type.INT));
        lstColumns.add(new Column("Col3_string", Column.Type.STRING));
        lstColumns.add(new Column("Col4_string", Column.Type.STRING));

        Table table = new Table("test", lstColumns);
        updateFieldPanel.setTable(table);

        assertTrue(updateFieldPanel.getTextMatchValue().getText().isEmpty());
        assertTrue(updateFieldPanel.getTxtNewValue().getText().isEmpty());

        assertEquals(lstColumns.size(), updateFieldPanel.getLstColumns().getModel().getSize());
        JComboBox<Column> cbColumns = updateFieldPanel.getLstColumns();
        for(int i = 0 ; i < cbColumns.getModel().getSize(); i++){

            assertTrue(table.getColumnNames().contains(cbColumns.getModel().getElementAt(i).getName()));
        }

        for(int i = 0 ; i < updateFieldPanel.getCbOperators().getModel().getSize(); i++){

            assertTrue(Arrays.asList(FieldComparator.Sign.values()).contains(updateFieldPanel.getCbOperators().getModel().getElementAt(i)));
        }


    }
}
