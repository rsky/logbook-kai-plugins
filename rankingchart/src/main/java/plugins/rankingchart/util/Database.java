package plugins.rankingchart.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import plugins.rankingchart.model.LogItem;

import java.sql.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Database {

    private static final String DEFAULT_DRIVER = "org.sqlite.JDBC";
    private static final String DEFAULT_URL = "jdbc:sqlite:ranking.db";

    private static Database DEFAULT = null;

    private String url;

    /**
     * @return デフォルトのRankingDataManager
     */
    public static synchronized Database getDefault() {
        if (DEFAULT == null) {
            try {
                Class.forName(DEFAULT_DRIVER);
                DEFAULT = new Database(DEFAULT_URL);
            } catch (ClassNotFoundException e) {
                LoggerHolder.LOG.error(e.getMessage(), e);
            }
        }
        return DEFAULT;
    }

    public static void setDefault(Database manager) {
        DEFAULT = manager;
    }

    private Database(String url) {
        this.url = url;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url);
    }

    /**
     * テーブルがなければ作成する
     */
    public void createTable() {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            //noinspection SqlNoDataSourceInspection
            statement.execute("CREATE TABLE IF NOT EXISTS ranking (\n" +
                    "  published_at INTEGER PRIMARY KEY,\n" +
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

    /**
     * ランキング情報を登録する
     * @param ranking ランキング情報
     */
    public void update(LogItem ranking) {
        //noinspection SqlDialectInspection,SqlNoDataSourceInspection,SqlResolve
        String sql = "REPLACE INTO ranking" +
                " (published_at, rank_no, rate, rank1, rank5, rank20, rank100, rank500)" +
                " values (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setTimestamp(1, Timestamp.from(ranking.getDateTime().toInstant()));
            setIntOrNull(statement, 2, ranking.getRankNo());
            setIntOrNull(statement, 3, ranking.getRate());
            setIntOrNull(statement, 4, ranking.getRank1());
            setIntOrNull(statement, 5, ranking.getRank5());
            setIntOrNull(statement, 6, ranking.getRank20());
            setIntOrNull(statement, 7, ranking.getRank100());
            setIntOrNull(statement, 8, ranking.getRank500());
            statement.execute();
        } catch (SQLException e) {
            LoggerHolder.LOG.error(e.getMessage(), e);
        }
    }

    private void setIntOrNull(PreparedStatement statement, int parameterIndex, Integer value) throws SQLException {
        if (value == null) {
            statement.setNull(parameterIndex, Types.NULL);
        } else {
            statement.setInt(parameterIndex, value);
        }
    }

    private Integer getIntOrNull(ResultSet resultSet, String columnLabel) throws SQLException {
        int value = resultSet.getInt(columnLabel);
        if (resultSet.wasNull()) {
            return null;
        } else {
            return value;
        }
    }

    /**
     * @return 全期間の日付を降順にソートしたリスト
     */
    public List<ZonedDateTime> allDateTime() {
        List<ZonedDateTime> list = new ArrayList<>();
        //noinspection SqlDialectInspection,SqlNoDataSourceInspection,SqlResolve
        String sql = "SELECT published_at FROM ranking ORDER BY published_at DESC";
        Calendar calendar = DateTimeUtil.getCalender();
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                Timestamp timestamp = resultSet.getTimestamp(1, calendar);
                list.add(DateTimeUtil.dateTimeFromTimestamp(timestamp));
            }
        } catch (SQLException e) {
            LoggerHolder.LOG.error(e.getMessage(), e);
        }
        return list;
    }

    /**
     * @param from 開始日時
     * @param to 終了日時
     * @return 指定期間[from, to]のランキング情報を日付で降順にソートしたリスト
     */
    public List<LogItem> load(ZonedDateTime from, ZonedDateTime to) {
        List<LogItem> list = new ArrayList<>();
        //noinspection SqlDialectInspection,SqlNoDataSourceInspection,SqlResolve
        String sql = "SELECT * FROM ranking" +
                " WHERE published_at >= ? AND published_at <= ?" +
                " ORDER BY published_at DESC";
        Calendar calendar = DateTimeUtil.getCalender();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setTimestamp(1, Timestamp.from(from.toInstant()));
            statement.setTimestamp(2, Timestamp.from(to.toInstant()));
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(convertResult(resultSet, calendar));
                }
            }
        } catch (SQLException e) {
            LoggerHolder.LOG.error(e.getMessage(), e);
        }
        return list;
    }

    /**
     * @return 直近のランキング情報
     */
    public LogItem getLatest() {
        //noinspection SqlDialectInspection,SqlNoDataSourceInspection,SqlResolve
        String sql = "SELECT * FROM ranking ORDER BY published_at DESC LIMIT 1";
        Calendar calendar = DateTimeUtil.getCalender();
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            if (resultSet.next()) {
                return convertResult(resultSet, calendar);
            }
        } catch (SQLException e) {
            LoggerHolder.LOG.error(e.getMessage(), e);
        }
        return null;
    }

    private LogItem convertResult(ResultSet resultSet, Calendar calendar) throws SQLException {
        Timestamp timestamp = resultSet.getTimestamp("published_at", calendar);
        LogItem row = LogItem.withDateTime(DateTimeUtil.dateTimeFromTimestamp(timestamp));
        row.setRankNo(getIntOrNull(resultSet, "rank_no"));
        row.setRate(getIntOrNull(resultSet, "rate"));
        row.setRank1(getIntOrNull(resultSet, "rank1"));
        row.setRank5(getIntOrNull(resultSet, "rank5"));
        row.setRank20(getIntOrNull(resultSet, "rank20"));
        row.setRank100(getIntOrNull(resultSet, "rank100"));
        row.setRank500(getIntOrNull(resultSet, "rank500"));
        return row;
    }

    private static class LoggerHolder {
        /**
         * ロガー
         */
        private static final Logger LOG = LogManager.getLogger(Database.class);
    }
}
