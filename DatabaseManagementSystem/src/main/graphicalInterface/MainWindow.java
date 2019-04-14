package main.graphicalInterface;

import main.graphicalInterface.database.DatabaseFrame;
import main.graphicalInterface.table.TableFrame;

import javax.swing.*;
import java.awt.*;

import static main.graphicalInterface.GIConstants.DATABASE_MANAGEMENT_SYSTEM_TITLE;

public class MainWindow extends JFrame {

    private DatabaseFrame databaseFrame;
    private TableFrame tableFrame;

    public MainWindow(){

        databaseFrame = new DatabaseFrame();
        tableFrame = TableFrame.getInstance();

        this.setTitle(DATABASE_MANAGEMENT_SYSTEM_TITLE);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //this.setLayout(null);
        this.setResizable(false);
        this.setBounds(10,10, 1200,800);
        this.setSize(1200, 800);

        this.add(databaseFrame);
        this.add(tableFrame);
    }

    public void open(){

        centreWindow(this);
        this.setLocationByPlatform(true);
        this.setVisible(true);
    }

    public static void centreWindow(Window frame) {

        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);
    }
}
