package plugins.rankingchart;

import logbook.plugin.lifecycle.StartUp;
import plugins.rankingchart.util.Database;

public class DatabaseInitializer implements StartUp {
    @Override
    public void run() {
        Database.getDefault().createTable();
    }
}
