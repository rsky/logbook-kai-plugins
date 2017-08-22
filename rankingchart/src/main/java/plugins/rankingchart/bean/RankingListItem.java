package plugins.rankingchart.bean;

import lombok.Value;

import javax.json.JsonObject;

@Value
public class RankingListItem {
    int no;
    String nickname;
    int flag;
    int rank;
    String comment;
    long obfuscatedMedals;
    long obfuscatedRate;

    public RankingListItem(JsonObject jo) {
        no = jo.getInt("api_mxltvkpyuklh");
        nickname = jo.getString("api_mtjmdcwtvhdr");
        flag = jo.getInt("api_pbgkfylkbjuy");
        rank = jo.getInt("api_pcumlrymlujh");
        comment = jo.getString("api_itbrdpdbkynm");
        obfuscatedMedals = jo.getJsonNumber("api_itslcqtmrxtf").longValue();
        obfuscatedRate = jo.getJsonNumber("api_wuhnhojjxmke").longValue();
    }
}
