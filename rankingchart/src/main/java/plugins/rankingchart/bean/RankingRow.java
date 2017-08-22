package plugins.rankingchart.bean;

import com.orangesignal.csv.annotation.CsvColumn;
import com.orangesignal.csv.annotation.CsvEntity;
import lombok.Data;

@CsvEntity
@Data
public class RankingRow {
    @CsvColumn(name = "日時")
    String date;

    @CsvColumn(name = "1位")
    int rank1;

    @CsvColumn(name = "5位")
    int rank5;

    @CsvColumn(name = "20位")
    int rank20;

    @CsvColumn(name = "100位")
    int rank100;

    @CsvColumn(name = "500位")
    int rank500;

    @CsvColumn(name = "戦果")
    int rate;

    @CsvColumn(name = "順位")
    int rankNo;

    public static RankingRow withDate(String date) {
        RankingRow row = new RankingRow();
        row.date = date;
        return row;
    }
}
