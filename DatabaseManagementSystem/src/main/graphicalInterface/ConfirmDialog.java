package main.graphicalInterface;

import javax.swing.*;

public class ConfirmDialog {

    String title;
    String message;

    public ConfirmDialog(String title, String message){

        this.title = title;
        this.message = message;
    }

    public boolean confirm(){

        int dialogResult = JOptionPane.showConfirmDialog (null, message, title,
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        return (dialogResult == JOptionPane.YES_OPTION);

        //TODO: what happens if the user just close the window
    }
}
