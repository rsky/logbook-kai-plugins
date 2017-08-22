package plugins.rankingchart.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import plugins.rankingchart.bean.RankingRow;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RankingDataManager {

    private static final String DEFAULT_DRIVER = "org.sqlite.JDBC";
    private static final String DEFAULT_URL = "jdbc:sqlite:ranking.db";

    private static RankingDataManager DEFAULT = null;

    private String url;

    public static RankingDataManager getDefault() {
        if (DEFAULT == null) {
            try {
                Class.forName(DEFAULT_DRIVER);
                DEFAULT = new RankingDataManager(DEFAULT_URL);
            } catch (ClassNotFoundException e) {
                LoggerHolder.LOG.error(e.getMessage(), e);
            }
        }
        return DEFAULT;
    }

    private RankingDataManager(String url) {
        this.url = url;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url);
    }

    public void createTable() {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            //noinspection SqlNoDataSourceInspection
            statement.execute("CREATE TABLE IF NOT EXISTS ranking (\n" +
                    "  published_at TIMESTAMP PRYMARY KEY,\n" +
                    "  rank_no INTEGER,\n" +
                    "  rate INTEGER,\n" +
                    "  rank1 INTEGER,\n" +
                    "  rank5 INTEGER,\n" +
                    "  rank20 INTEGER,\n" +
                    "  rank100 INTEGER,\n" +
                    "  rank500 INTEGER\n" +
                    ")");
        } catch (SQLException e) {
            LoggerHolder.LOG.error(e.getMessage(), e);
        }
    }

    public void update(RankingRow row) {
        //noinspection SqlNoDataSourceInspection,SqlResolve
        String sql = "REPLACE INTO ranking (published_at, rank_no, rate, rank1, rank5, rank20, rank100, rank500)" +
                " values (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setTimestamp(1, Timestamp.from(row.getDateTime().toInstant()));
            statement.setInt(2, row.getRankNo());
            statement.setInt(3, row.getRate());
            statement.setInt(4, row.getRank1());
            statement.setInt(5, row.getRank5());
            statement.setInt(6, row.getRank20());
            statement.setInt(7, row.getRank100());
            statement.setInt(8, row.getRank500());
            statement.execute();
        } catch (SQLException e) {
            LoggerHolder.LOG.error(e.getMessage(), e);
        }
    }

    public List<RankingRow> loadAll() {
        List<RankingRow> list = new ArrayList<>();
        //noinspection SqlNoDataSourceInspection,SqlResolve
        String sql = "SELECT * FROM ranking ORDER BY published_at";
        Calendar calendar = DateTimeUtil.getCalender();
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                list.add(convertToRow(resultSet, calendar));
            }
        } catch (SQLException e) {
            LoggerHolder.LOG.error(e.getMessage(), e);
        }
        return list;
    }

    public RankingRow getLatest() {
        //noinspection SqlNoDataSourceInspection,SqlResolve
        String sql = "SELECT * FROM ranking ORDER BY published_at DESC LIMIT 1";
        Calendar calendar = DateTimeUtil.getCalender();
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            if (resultSet.next()) {
                return convertToRow(resultSet, calendar);
            }
        } catch (SQLException e) {
            LoggerHolder.LOG.error(e.getMessage(), e);
        }
        return null;
    }

    private RankingRow convertToRow(ResultSet resultSet, Calendar calendar) throws SQLException {
        Timestamp timestamp = resultSet.getTimestamp("published_at", calendar);
        RankingRow row = RankingRow.withDateTime(DateTimeUtil.dateTimeFromTimestamp(timestamp));
        row.setRankNo(resultSet.getInt("rank_no"));
        row.setRate(resultSet.getInt("rate"));
        row.setRank1(resultSet.getInt("rank1"));
        row.setRank5(resultSet.getInt("rank5"));
        row.setRank20(resultSet.getInt("rank20"));
        row.setRank100(resultSet.getInt("rank100"));
        row.setRank500(resultSet.getInt("rank500"));
        return row;
    }

    private static class LoggerHolder {
        /**
         * ロガー
         */
        private static final Logger LOG = LogManager.getLogger(RankingDataManager.class);
    }
}
