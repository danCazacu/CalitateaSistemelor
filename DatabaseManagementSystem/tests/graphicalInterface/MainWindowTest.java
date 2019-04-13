package graphicalInterface;

import main.graphicalInterface.GIConstants;
import main.graphicalInterface.MainWindow;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MainWindowTest {

    @Test
    public void showFrame(){

        MainWindow mainFrame = new MainWindow();
        mainFrame.open();

        assertEquals(true, mainFrame.isVisible());
        assertEquals(GIConstants.DATABASE_MANAGEMENT_SYSTEM_TITLE, mainFrame.getTitle());
    }
}
