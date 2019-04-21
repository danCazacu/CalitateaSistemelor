package main.graphicalInterface;

import main.persistance.DatabasePersistance;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public abstract class PersistenceActionListener implements ActionListener {


    /**
     * Invoked when an action occurs.
     *
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        beforePersist(e);

        new DatabasePersistance().persist();
    }

    public abstract void beforePersist(ActionEvent e);
}
