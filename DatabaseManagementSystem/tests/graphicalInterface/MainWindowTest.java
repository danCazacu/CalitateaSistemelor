package graphicalInterface;

import main.graphicalInterface.MainWindow;
import main.graphicalInterface.database.DatabaseFrame;
import main.graphicalInterface.table.TableFrame;
import main.graphicalInterface.tableRecord.TableContentFrame;
import main.persistance.DatabasePersistance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.swing.*;
import java.awt.event.WindowEvent;

import static main.graphicalInterface.GIConstants.DATABASE_MANAGEMENT_SYSTEM_TITLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * Tests suit for {@link MainWindow}
 */
public class MainWindowTest {

    private MainWindow mainFrame;


    private static DatabasePersistance databasePersistanceMock;

    @BeforeEach
    public void setup() {

        mainFrame = new MainWindow();
        databasePersistanceMock = mock(DatabasePersistance.class);
        doNothing().when(databasePersistanceMock).persist();
        mainFrame.setDatabasePersistance(databasePersistanceMock);
    }

    @Test
    public void givenMainFrameWhenOpenThenIsVisibleAndHaveTheRightComponents() {

        // execute
        mainFrame.open();

        // verify properties that have been set to JFrame
        assertEquals(true, mainFrame.isVisible());
        assertEquals(DATABASE_MANAGEMENT_SYSTEM_TITLE, mainFrame.getTitle());

        assertEquals(3, mainFrame.getContentPane().getComponentCount());
        assertEquals(DatabaseFrame.class, mainFrame.getContentPane().getComponent(0).getClass());
        assertEquals(TableFrame.class, mainFrame.getContentPane().getComponent(1).getClass());
        assertEquals(TableContentFrame.class, mainFrame.getContentPane().getComponent(2).getClass());
    }

    @Test
    public void givenMainFrameWhenCloseFrameThenDBPersist() {

        // execute
        mainFrame.open();
        assertEquals(1, mainFrame.getWindowListeners().length);

        mainFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        // execute
        mainFrame.dispatchEvent(new WindowEvent(mainFrame, WindowEvent.WINDOW_CLOSING));

        Mockito.verify(databasePersistanceMock, times(1)).persist();
    }

    @Test
    public void givenNullMainFrameThenShouldThrowException() {

        // setup
        mainFrame = null;

        // execute & verify
        //we aspect to throw a NullPointerException as we don't have any main frame window
        assertThrows(NullPointerException.class,
                () -> {
                    mainFrame.open();
                });
    }
}
