package plugins.rankingchart.bean;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class RankingLogItem {
    ZonedDateTime dateTime;
    Integer rank1;
    Integer rank5;
    Integer rank20;
    Integer rank100;
    Integer rank500;
    Integer rate;
    Integer rankNo;

    public static RankingLogItem withDateTime(ZonedDateTime date) {
        RankingLogItem row = new RankingLogItem();
        row.dateTime = date;
        return row;
    }
}
