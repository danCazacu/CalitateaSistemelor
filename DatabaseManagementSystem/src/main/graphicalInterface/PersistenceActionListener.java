package main.graphicalInterface;

import main.persistance.DatabasePersistance;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public abstract class PersistenceActionListener implements ActionListener {

    DatabasePersistance databasePersistance;
    /**
     * Invoked when an action occurs.
     *
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        beforePersist(e);

       databasePersistance.persist();
    }

    public abstract void beforePersist(ActionEvent e);

    public void setDatabasePersistence(DatabasePersistance databasePersistance) {

        this.databasePersistance = databasePersistance;
    }
}
