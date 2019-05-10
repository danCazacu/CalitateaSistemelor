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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static main.graphicalInterface.GIConstants.DATABASE_MANAGEMENT_SYSTEM_TITLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests suit for {@link MainWindow}
 */
public class MainWindowTest {

    private MainWindow mainFrame;

    @BeforeEach
    public void setup() {

        mainFrame = new MainWindow();
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

        // setup
        DatabasePersistance databasePersistenceMock = mock(DatabasePersistance.class);
        WindowAdapter windowAdapterMock = mock(WindowAdapter.class);

        doAnswer((i) -> {

            databasePersistenceMock.persist();
            mainFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            return null;
        }).when(windowAdapterMock).windowClosing(any());

        // execute
        mainFrame.open();
        assertEquals(1, mainFrame.getWindowListeners().length);
        mainFrame.removeWindowListener(mainFrame.getWindowListeners()[0]);

        mainFrame.addWindowListener(windowAdapterMock);
        // execute
        mainFrame.dispatchEvent(new WindowEvent(mainFrame, WindowEvent.WINDOW_CLOSING));

        // verify
        Mockito.verify(windowAdapterMock, times(1)).windowClosing(any());
        Mockito.verify(databasePersistenceMock, times(1)).persist();
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
