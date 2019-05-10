package graphicalInterface.database;

import main.graphicalInterface.database.DatabaseFrame;
import main.graphicalInterface.table.TableFrame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;

import static main.graphicalInterface.GIConstants.DATABASES_TITLE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests suit for {@link main.graphicalInterface.database.DatabaseFrame}
 */
public class DatabaseFrameTest {

    private TableFrame tableFrame;
    private DatabaseFrame databaseFrame;
    private DefaultListModel listModel;

    private JList databasesList;

    @BeforeEach
    public void setup() {

        tableFrame = TableFrame.getInstance();
        databaseFrame = new DatabaseFrame();
        listModel = new DefaultListModel();
        listModel.addElement("DBTest1");
        listModel.addElement("DBTest2");
        listModel.addElement("DBTest3");
        databaseFrame.setListModel(listModel);

        databasesList = new JList(listModel);
        databaseFrame.setDatabasesList(new JList(listModel));
    }

    @Test
    public void testDatabaseFrame() {

        assertEquals(DATABASES_TITLE, databaseFrame.getTitleLabel().getText());
    }

    @Test
    public void givenDatabaseFrameWhenInvalidValueSelectedThenDisableButtonsAndSetDatabaseInTableFrameNull() {

        // setup
        databaseFrame.getDatabasesList().setSelectedIndex(listModel.getSize() + 1);

        // execute
        databaseFrame.valueChanged(new ListSelectionEvent(listModel, listModel.getSize(), listModel.getSize(), false));

        // verify
        assertNull(tableFrame.getSelectedDatabase());
    }

    @Test
    public void givenDatabaseFrameWhenValidValueSelectedThenEnableButtonsAndSetDatabaseInTableFrame() {

        // setup
        databaseFrame.getDatabasesList().setSelectedIndex(1);

        assertTrue(listModel.getSize() > 1);

        // execute
        databaseFrame.valueChanged(new ListSelectionEvent(listModel, 0, 0, false)); //first selection

        // verify
        assertNotNull(tableFrame.getSelectedDatabase());
        assertEquals(listModel.get(0), tableFrame.getSelectedDatabase());
    }

    @Test
    public void testCreateListener() {

        // setup
        //JButton btnCreate = mock(JButton.class);
       // databaseFrame.setBtnCreate(btnCreate);


        // execute
        //btnCreate.doClick();
        databaseFrame.getBtnCreate().doClick();

        // verify
    }    @Test
    public void testUpdateListener() {

        // setup
        //JButton btnCreate = mock(JButton.class);
       // databaseFrame.setBtnCreate(btnCreate);


        // execute
        //btnCreate.doClick();
        //databaseFrame.getBtnUpdate().doClick();

        // verify
    }    @Test
    public void testDeleteListener() {

        // setup
        databaseFrame.getDatabasesList().setSelectedIndex(0);

        JButton btnDelete = mock(databaseFrame.getBtnDelete().getClass());
        databaseFrame.setBtnDelete(btnDelete);

        doAnswer((i) -> {

            return doCallRealMethod();
        }). when(btnDelete).doClick();
        // execute & verify
        assertEquals(listModel.getSize() , databaseFrame.getDatabasesList().getModel().getSize() , "Number of databases before delete");
        databaseFrame.getBtnDelete().doClick();
        assertEquals(listModel.getSize() - 1 , databaseFrame.getDatabasesList().getModel().getSize() , "Number of databases before delete");
    }
}
