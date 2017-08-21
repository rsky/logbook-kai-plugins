package plugins.rankingchart.gui;

import javafx.scene.control.MenuItem;
import logbook.plugin.gui.MainExtMenu;

public class RankingChartMenu implements MainExtMenu {
    @Override
    public MenuItem getContent() {
        MenuItem item = new MenuItem("戦果チャート");
        //item.setOnAction(e -> {});
        return item;
    }
}
