package plugins.rankingchart.bean;

import lombok.Value;

import javax.json.JsonObject;

@Value
public class RankingListItem {
    private int no;
    private String nickname;
    private int flag;
    private int rank;
    private String comment;
    private long obfuscatedMedals;
    private long obfuscatedRate;

    public RankingListItem(final JsonObject jo) {
        no = jo.getInt("api_mxltvkpyuklh");
        nickname = jo.getString("api_mtjmdcwtvhdr");
        flag = jo.getInt("api_pbgkfylkbjuy");
        rank = jo.getInt("api_pcumlrymlujh");
        comment = jo.getString("api_itbrdpdbkynm");
        obfuscatedMedals = jo.getJsonNumber("api_itslcqtmrxtf").longValue();
        obfuscatedRate = jo.getJsonNumber("api_wuhnhojjxmke").longValue();
    }
}
