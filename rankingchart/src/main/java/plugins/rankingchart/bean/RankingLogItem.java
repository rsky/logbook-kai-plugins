package plugins.rankingchart.bean;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import plugins.rankingchart.util.DateTimeUtil;

import java.time.ZonedDateTime;
import java.util.Arrays;

public class RankingLogItem {
    /** 日付 */
    private final Property<ZonedDateTime> dateTime = new SimpleObjectProperty<>();

    /** ランキング1位戦果 */
    private final Property<Integer> rank1 = new SimpleObjectProperty<>();

    /** ランキング5位戦果 */
    private final Property<Integer> rank5 = new SimpleObjectProperty<>();

    /** ランキング20位戦果 */
    private final Property<Integer> rank20 = new SimpleObjectProperty<>();

    /** ランキング100位戦果 */
    private final Property<Integer> rank100 = new SimpleObjectProperty<>();

    /** ランキング500位戦果 */
    private final Property<Integer> rank500 = new SimpleObjectProperty<>();

    /** 自分の戦果 */
    private final Property<Integer> rate = new SimpleObjectProperty<>();

    /** 自分の順位 */
    private final Property<Integer> rankNo = new SimpleObjectProperty<>();

    public static RankingLogItem withDateTime(ZonedDateTime dateTime) {
        RankingLogItem row = new RankingLogItem();
        row.setDateTime(dateTime);
        return row;
    }

    public static String csvHeader() {
        return "日付,1位,5位,20位,100位,500位,戦果,順位";
    }

    public String toCSV() {
        return toStringWithDelimiter(",");
    }

    public String toTSV() {
        return toStringWithDelimiter("\t");
    }

    private String toStringWithDelimiter(String delimiter) {
        return String.join(delimiter, Arrays.asList(
                DateTimeUtil.formatDateTime(getDateTime()),
                integerPropertyToString(rank1),
                integerPropertyToString(rank5),
                integerPropertyToString(rank20),
                integerPropertyToString(rank100),
                integerPropertyToString(rank500),
                integerPropertyToString(rate),
                integerPropertyToString(rankNo)
        ));
    }

    private String integerPropertyToString(Property<Integer> property) {
        Integer value = property.getValue();
        return (value == null) ? "" : value.toString();
    }

    public boolean put(int rankNo, int rate) {
        switch (rankNo) {
            case 1:
                setRank1(rate);
                return true;
            case 5:
                setRank5(rate);
                return true;
            case 20:
                setRank20(rate);
                return true;
            case 100:
                setRank100(rate);
                return true;
            case 500:
                setRank500(rate);
                return true;
            default:
                return false;
        }
    }

    public Property<ZonedDateTime> dateTimeProperty() {
        return dateTime;
    }

    public ZonedDateTime getDateTime() {
        return dateTime.getValue();
    }

    public void setDateTime(ZonedDateTime value) {
        dateTime.setValue(value);
    }

    public Property<Integer> rank1Property() {
        return rank1;
    }

    public Integer getRank1() {
        return rank1.getValue();
    }

    public void setRank1(Integer value) {
        rank1.setValue(value);
    }

    public Property<Integer> rank5Property() {
        return rank5;
    }

    public Integer getRank5() {
        return rank5.getValue();
    }

    public void setRank5(Integer value) {
        rank5.setValue(value);
    }

    public Property<Integer> rank20Property() {
        return rank20;
    }

    public Integer getRank20() {
        return rank20.getValue();
    }

    public void setRank20(Integer value) {
        rank20.setValue(value);
    }

    public Property<Integer> rank100Property() {
        return rank100;
    }

    public Integer getRank100() {
        return rank100.getValue();
    }

    public void setRank100(Integer value) {
        rank100.setValue(value);
    }

    public Property<Integer> rank500Property() {
        return rank500;
    }

    public Integer getRank500() {
        return rank500.getValue();
    }

    public void setRank500(Integer value) {
        rank500.setValue(value);
    }

    public Property<Integer> rateProperty() {
        return rate;
    }

    public Integer getRate() {
        return rate.getValue();
    }

    public void setRate(Integer value) {
        rate.setValue(value);
    }

    public Property<Integer> rankNoProperty() {
        return rankNo;
    }

    public Integer getRankNo() {
        return rankNo.getValue();
    }

    public void setRankNo(Integer value) {
        rankNo.setValue(value);
    }
}
