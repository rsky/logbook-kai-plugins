package plugins.rankingchart.bean;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import plugins.rankingchart.util.DateTimeUtil;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;

public class RankingTableRow {
    /** 日付 */
    private StringProperty date;

    /** ランキング1位戦果 */
    private StringProperty rank1;

    /** ランキング5位戦果 */
    private StringProperty rank5;

    /** ランキング1位戦果 */
    private StringProperty rank20;

    /** ランキング1位戦果 */
    private StringProperty rank100;

    /** ランキング500位戦果 */
    private StringProperty rank500;

    /** 自分の戦果 */
    private StringProperty rate;

    /** 自分の順位 */
    private StringProperty rankNo;

    public RankingTableRow(RankingLogItem item) {
        NumberFormat format = NumberFormat.getNumberInstance();
        Integer value;

        date = new SimpleStringProperty(DateTimeUtil.formatDateTime(item.getDateTime()));

        value = item.getRank1();
        rank1 = new SimpleStringProperty(value == null ? "" : format.format(value));

        value = item.getRank5();
        rank5 = new SimpleStringProperty(value == null ? "" : format.format(value));

        value = item.getRank20();
        rank20 = new SimpleStringProperty(value == null ? "" : format.format(value));

        value = item.getRank100();
        rank100 = new SimpleStringProperty(value == null ? "" : format.format(value));

        value = item.getRank500();
        rank500 = new SimpleStringProperty(value == null ? "" : format.format(value));

        value = item.getRate();
        rate = new SimpleStringProperty(value == null ? "" : format.format(value));

        value = item.getRankNo();
        rankNo = new SimpleStringProperty(value == null ? "" : format.format(value));
    }

    @Override
    public String toString() {
        return toTSV();
    }

    public String toCSV() {
        return toStringWithDelimiter(",");
    }

    public String toTSV() {
        return toStringWithDelimiter("\t");
    }

    private String toStringWithDelimiter(String delimiter) {
        NumberFormat format = NumberFormat.getNumberInstance();
        return String.join(delimiter, Arrays.asList(
                getDate(),
                unFormatNumber(format, getRank1()),
                unFormatNumber(format, getRank5()),
                unFormatNumber(format, getRank20()),
                unFormatNumber(format, getRank100()),
                unFormatNumber(format, getRank500()),
                unFormatNumber(format, getRate()),
                unFormatNumber(format, getRankNo())
        ));
    }

    private String unFormatNumber(NumberFormat format, String text) {
        if (text.isEmpty()) {
            return "";
        } else {
            try {
                return format.parse(text).toString();
            } catch (ParseException e) {
                return "";
            }
        }
    }

    public String getDate() {
        return date.get();
    }

    public String getRank1() {
        return rank1.get();
    }

    public String getRank5() {
        return rank5.get();
    }

    public String getRank20() {
        return rank20.get();
    }

    public String getRank100() {
        return rank100.get();
    }

    public String getRank500() {
        return rank500.get();
    }

    public String getRate() {
        return rate.get();
    }

    public String getRankNo() {
        return rankNo.get();
    }
}
