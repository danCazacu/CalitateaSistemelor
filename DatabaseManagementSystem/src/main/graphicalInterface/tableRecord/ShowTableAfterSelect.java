package main.graphicalInterface.tableRecord;

import main.exception.FieldValueNotSet;
import main.model.Table;

import javax.swing.*;
import java.awt.*;

public class ShowTableAfterSelect {


    public ShowTableAfterSelect(Table inputTable) throws FieldValueNotSet {

        JFrame frame = new JFrame("Table after select filter ( " + inputTable.getName() + " )");

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JTable table = new JTable(new TableModel(inputTable, true));

        JScrollPane tableContainer = new JScrollPane(table);

        panel.add(tableContainer, BorderLayout.CENTER);
        frame.getContentPane().add(panel);

        frame.pack();
        frame.setVisible(true);
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);
        frame.setAlwaysOnTop(true);

    }
}
