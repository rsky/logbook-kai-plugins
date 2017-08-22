package plugins.rankingchart;

import logbook.plugin.lifecycle.StartUp;
import plugins.rankingchart.util.RankingDataManager;

public class DatabaseInitializer implements StartUp {
    @Override
    public void run() {
        RankingDataManager.getDefault().createTable();
    }
}
