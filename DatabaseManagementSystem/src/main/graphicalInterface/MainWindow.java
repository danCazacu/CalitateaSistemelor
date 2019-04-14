package main.graphicalInterface;

import main.graphicalInterface.database.DatabaseFrame;
import main.graphicalInterface.table.TableFrame;
import main.graphicalInterface.tableRecord.TableContentFrame;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

import static main.graphicalInterface.GIConstants.DATABASE_MANAGEMENT_SYSTEM_TITLE;

public class MainWindow extends JFrame {

    private DatabaseFrame databaseFrame;
    private TableFrame tableFrame;
    private TableContentFrame tableContentFrame;

    public MainWindow(){

        databaseFrame = new DatabaseFrame();
        tableFrame = TableFrame.getInstance();
        tableContentFrame = TableContentFrame.getInstance();

        this.setTitle(DATABASE_MANAGEMENT_SYSTEM_TITLE);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //this.setLayout(null);
        this.setSize(new Dimension(1200, 800));
        this.setResizable(false);

        this.setLayout(new GridLayout(1,3));
        this.getContentPane().add(databaseFrame, BorderLayout.NORTH);
        this.getContentPane().add(tableFrame, BorderLayout.CENTER);
        this.getContentPane().add(tableContentFrame, BorderLayout.SOUTH);
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
