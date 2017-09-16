package plugins.rankingchart.bean;

import lombok.Value;

import javax.json.JsonObject;

@Value
public class RankingEntry {
    int no;
    String nickname;
    int flag;
    int rank;
    String comment;
    long obfuscatedMedals;
    long obfuscatedRate;

    public RankingEntry(JsonObject jo) {
        no = jo.getInt("api_mxltvkpyuklh");
        nickname = jo.getString("api_mtjmdcwtvhdr");
        flag = jo.getInt("api_pbgkfylkbjuy");
        rank = jo.getInt("api_pcumlrymlujh");
        comment = jo.getString("api_itbrdpdbkynm");
        obfuscatedMedals = jo.getJsonNumber("api_itslcqtmrxtf").longValue();
        obfuscatedRate = jo.getJsonNumber("api_wuhnhojjxmke").longValue();
    }
}
