package plugins.rankingchart.bean;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import plugins.rankingchart.util.DateTimeUtil;

import java.text.NumberFormat;

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

    /** マイ戦果 */
    private StringProperty rate;

    /** マイランク */
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

    public String getDate() {
        return date.get();
    }

    public void setDate(String date) {
        this.date.set(date);
    }

    public String getRank1() {
        return rank1.get();
    }

    public void setRank1(String rank1) {
        this.rank1.set(rank1);
    }

    public String getRank5() {
        return rank5.get();
    }

    public void setRank5(String rank5) {
        this.rank5.set(rank5);
    }

    public String getRank20() {
        return rank20.get();
    }

    public void setRank20(String rank20) {
        this.rank20.set(rank20);
    }

    public String getRank100() {
        return rank100.get();
    }

    public void setRank100(String rank100) {
        this.rank100.set(rank100);
    }

    public String getRank500() {
        return rank500.get();
    }

    public void setRank500(String rank500) {
        this.rank500.set(rank500);
    }

    public String getRate() {
        return rate.get();
    }

    public void setRate(String rate) {
        this.rate.set(rate);
    }

    public String getRankNo() {
        return rankNo.get();
    }

    public void setRankNo(String rankNo) {
        this.rankNo.set(rankNo);
    }
}
