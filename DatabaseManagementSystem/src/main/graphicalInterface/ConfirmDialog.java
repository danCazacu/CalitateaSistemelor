package main.graphicalInterface;

import javax.swing.*;

public class ConfirmDialog {

    String title;
    String message;

    public ConfirmDialog(){
    }

    public void setTitle(String title) {

        assert title != null : "Precondition failed, title is null";
        this.title = title;

        assert this.title.equals(title) : "Post-condition failed: the title has not the input value";
    }

    public void setMessage(String message) {

        assert message != null : "Precondition failed, title is null";

        this.message = message;

        assert this.message.equals(message) : "Post-condition failed: the title has not the input value";

    }

    public boolean confirm(){

        assert message != null && title != null : "Precondition failed: the message and/or title values weren't set ";

        int dialogResult = JOptionPane.showConfirmDialog (null, message, title,
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        assert dialogResult == JOptionPane.YES_OPTION || dialogResult == JOptionPane.NO_OPTION || dialogResult == JOptionPane.CLOSED_OPTION : "Postcondition";

        return (dialogResult == JOptionPane.YES_OPTION);
    }
}
