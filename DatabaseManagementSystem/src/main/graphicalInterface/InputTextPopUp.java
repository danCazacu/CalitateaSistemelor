package main.graphicalInterface;

import javax.swing.*;

public class InputTextPopUp {

    private String title;
    public InputTextPopUp(String title){

        this.title = (title);
    }

    public Object openPopUp(String message, boolean isReopened){

        Object result = null;
        if(isReopened){

            result = JOptionPane.showInputDialog(null, message, title, JOptionPane.ERROR_MESSAGE );
        }else{

            result = JOptionPane.showInputDialog(null, message, title, JOptionPane.QUESTION_MESSAGE);
        }
        return result;
    }
}
