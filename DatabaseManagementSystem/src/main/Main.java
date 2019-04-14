package main;

import main.graphicalInterface.MainWindow;
import main.model.Column;
import main.model.DatabaseManagementSystem;
import main.model.Field;
import main.model.Table;
import main.util.DataBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {


        DataBuilder.buildeDataOnce();
        DataBuilder.buildeDataSecondTime();

        MainWindow mainFrame = new MainWindow();
        mainFrame.open();

    }
}
