package plugins.rankingchart.bean;

import com.orangesignal.csv.annotation.CsvColumn;
import com.orangesignal.csv.annotation.CsvColumnAccessType;
import com.orangesignal.csv.annotation.CsvEntity;

@CsvEntity
public class RankingRow {
    @CsvColumn(name = "日時", access = CsvColumnAccessType.READ, required = true)
    public String date;

    @CsvColumn(name = "順位")
    public int rankNo;

    @CsvColumn(name = "戦果")
    public int rate;

    @CsvColumn(name = "1位")
    public int rank1;

    @CsvColumn(name = "5位")
    public int rank5;

    @CsvColumn(name = "20位")
    public int rank20;

    @CsvColumn(name = "100位")
    public int rank100;

    @CsvColumn(name = "500位")
    public int rank500;

    public static RankingRow withDate(String date) {
        RankingRow row = new RankingRow();
        row.date = date;
        return row;
    }
}
