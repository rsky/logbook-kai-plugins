package plugins.rankingchart.util;

import com.orangesignal.csv.Csv;
import com.orangesignal.csv.CsvConfig;
import com.orangesignal.csv.CsvListHandler;
import com.orangesignal.csv.handlers.ColumnPositionMappingBeanListHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import plugins.rankingchart.bean.RankingRow;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class RankingCsvManager {
    private List<RankingRow> rankingList = new ArrayList<>();

    public void load() {
        try {
            final File csvFile = getCsvPath().toFile();
            if (csvFile.exists()) {
                // FIXME
                List<RankingRow> list = Csv.load(csvFile, getCsvConfig(), getCsvHandler());
                if (list != null && !list.isEmpty()) {
                    rankingList = new ArrayList<>(rankingList);
                }
            }
        } catch (IOException e) {
            LoggerHolder.LOG.error(e.getMessage(), e);
        }
    }

    private Path getCsvPath() {
        // TODO: ディレクトリを選択可能に
        return FileSystems.getDefault().getPath(".", "戦果ログ");
    }

    private CsvConfig getCsvConfig() {
        return new CsvConfig();
    }

    private CsvListHandler<RankingRow> getCsvHandler() {
        return new ColumnPositionMappingBeanListHandler<>(RankingRow.class);
    }

    public RankingRow getLatest() {
        return getLatest(true);
    }

    private RankingRow getLatest(boolean copy) {
        final int size = rankingList.size();
        if (size > 0) {
            final RankingRow latest = rankingList.get(size - 1);
            return (copy) ? latest.copy() : latest;
        } else {
            return null;
        }
    }

    public void updateLatest(RankingRow row) {
        final RankingRow latest = getLatest(false);
        if (latest != null) {
            if (latest.equals(row)) {
                return;
            }
            if (latest.date.equals(row.date)) {
                rankingList.remove(latest);
            }
        }
        rankingList.add(row.copy());

        try {
            Csv.save(rankingList, getCsvPath().toFile(), getCsvConfig(), getCsvHandler());
        } catch (IOException e) {
            LoggerHolder.LOG.error(e.getMessage(), e);
        }
    }

    private static class LoggerHolder {
        /**
         * ロガー
         */
        private static final Logger LOG = LogManager.getLogger(RankingCsvManager.class);
    }
}
