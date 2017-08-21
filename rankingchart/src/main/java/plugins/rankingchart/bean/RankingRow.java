package plugins.rankingchart.bean;

import com.orangesignal.csv.annotation.CsvColumn;
import com.orangesignal.csv.annotation.CsvEntity;

@CsvEntity
public class RankingRow {
    @CsvColumn(name = "日時")
    public String date;

    @CsvColumn(name = "順位")
    public String ranking;

    @CsvColumn(name = "戦果")
    public String result;

    @CsvColumn(name = "1位")
    public String rank1;

    @CsvColumn(name = "5位")
    public String rank5;

    @CsvColumn(name = "20位")
    public String rank20;

    @CsvColumn(name = "100位")
    public String rank100;

    @CsvColumn(name = "500位")
    public String rank500;
}
