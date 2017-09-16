package plugins.rankingchart.bean;

import com.orangesignal.csv.annotation.CsvColumn;
import com.orangesignal.csv.annotation.CsvEntity;
import lombok.Data;

import java.time.ZonedDateTime;

@CsvEntity
@Data
public class RankingLogItem {
    @CsvColumn(name = "日時")
    ZonedDateTime dateTime;

    @CsvColumn(name = "1位")
    Integer rank1;

    @CsvColumn(name = "5位")
    Integer rank5;

    @CsvColumn(name = "20位")
    Integer rank20;

    @CsvColumn(name = "100位")
    Integer rank100;

    @CsvColumn(name = "500位")
    Integer rank500;

    @CsvColumn(name = "戦果")
    Integer rate;

    @CsvColumn(name = "順位")
    Integer rankNo;

    public static RankingLogItem withDateTime(ZonedDateTime date) {
        RankingLogItem row = new RankingLogItem();
        row.dateTime = date;
        return row;
    }
}
