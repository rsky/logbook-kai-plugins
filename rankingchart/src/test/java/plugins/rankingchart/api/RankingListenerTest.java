package plugins.rankingchart.api;

import com.example.mockito.MockitoExtension;
import logbook.internal.Config;
import logbook.proxy.RequestMetaData;
import logbook.proxy.ResponseMetaData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import plugins.rankingchart.bean.RankingChartConfig;
import plugins.rankingchart.bean.RankingLogItem;
import plugins.rankingchart.util.Calculator;
import plugins.rankingchart.util.RankingDataManager;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.StringReader;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@SuppressWarnings("WeakerAccess")
@Tag("api")
@ExtendWith(MockitoExtension.class)
public class RankingListenerTest {
    private static final int USER_RATE_FACTOR = 23;

    private static final String API_MEMBER_JSON = "{\"api_result\":1,\"api_data\":{\n" +
            "    \"api_member_id\":1,\n" +
            "    \"api_nickname\":\"Test User\"\n" +
            "}}";

    private static final String API_REQ_RANKING_JSON_PREFIX = "{\"api_result\":1,\"api_data\":{\n" +
            "\"api_count\":1000,\n" +
            "\"api_page_count\":100,\n" +
            "\"api_disp_page\":1,\n" +
            "\"api_list\":[\n";
    private static final String API_REQ_RANKING_JSON_RANKER_ENTRY_FORMAT = "{\n" +
            "    \"api_mxltvkpyuklh\":%d,\n" +
            "    \"api_mtjmdcwtvhdr\":\"Ranker\",\n" +
            "    \"api_pbgkfylkbjuy\":0,\n" +
            "    \"api_pcumlrymlujh\":1,\n" +
            "    \"api_itbrdpdbkynm\":\"Test Comment\",\n" +
            "    \"api_itslcqtmrxtf\":999999,\n" +
            "    \"api_wuhnhojjxmke\":%d\n" +
            "}";
    private static final String API_REQ_RANKING_JSON_SELF_ENTRY_FORMAT = "{\n" +
            "    \"api_mxltvkpyuklh\":%d,\n" +
            "    \"api_mtjmdcwtvhdr\":\"Test User\",\n" +
            "    \"api_pbgkfylkbjuy\":0,\n" +
            "    \"api_pcumlrymlujh\":1,\n" +
            "    \"api_itbrdpdbkynm\":\"Test Comment\",\n" +
            "    \"api_itslcqtmrxtf\":999999,\n" +
            "    \"api_wuhnhojjxmke\":%d\n" +
            "}";
    private static final String API_REQ_RANKING_JSON_SUFFIX = "]}}";

    private List<InvocationOnMock> invocationOnMocks;

    @BeforeEach
    public void setUp(@Mock RankingDataManager rankingDataManager) {
        invocationOnMocks = new CopyOnWriteArrayList<>();

        doAnswer(invocationOnMocks::add).when(rankingDataManager).update(any());

        RankingDataManager.setDefault(rankingDataManager);

        RankingChartConfig config = RankingChartConfig.get();
        config.setUserRateFactor(0);
        config.setLastRankNo(0);
        config.setLastObfuscatedRate(0);
        Config.getDefault().store();
    }

    @Test
    @DisplayName("1〜100位の戦果から自動で戦果係数を導出し、戦果をデータベースに保存する")
    public void autoDetectUserRateFactor(@Mock RequestMetaData requestMetaData,
                                         @Mock ResponseMetaData responseMetaData) {
        when(requestMetaData.getRequestURI())
                .thenReturn("/kcsapi/api_get_member/record")
                .thenReturn("/kcsapi/api_req_ranking/mxltvkpyuklh");

        RankingListener listener = new RankingListener();

        JsonObject memberJson = jsonObjectForMember();
        listener.accept(memberJson, requestMetaData, responseMetaData);

        JsonObject rankingJson = jsonObjectForAutoDetectUserRatingFactor();
        listener.accept(rankingJson, requestMetaData, responseMetaData);

        assertEquals(1, invocationOnMocks.size());
        RankingLogItem item = invocationOnMocks.get(0).getArgument(0);
        assertEquals(9999, item.getRank1().intValue());
        assertEquals(9995, item.getRank5().intValue());
        assertEquals(9980, item.getRank20().intValue());
        assertEquals(9900, item.getRank100().intValue());
        assertEquals(9500, item.getRank500().intValue());
        assertEquals(9223, item.getRate().intValue());
        assertEquals(777, item.getRankNo().intValue());
    }

    private JsonObject jsonObjectForMember() {
        return Json.createReader(new StringReader(API_MEMBER_JSON)).readObject();
    }

    private JsonObject jsonObjectForAutoDetectUserRatingFactor() {
        StringBuilder sb = new StringBuilder();

        sb.append(API_REQ_RANKING_JSON_PREFIX);

        IntStream.concat(IntStream.rangeClosed(1, 100), IntStream.of(500)).forEach(rankNo -> {
            int rate = 10000 - rankNo;
            long obfuscatedRate = Calculator.obfuscateRate(rankNo, rate, USER_RATE_FACTOR);
            sb.append(String.format(API_REQ_RANKING_JSON_RANKER_ENTRY_FORMAT, rankNo, obfuscatedRate));
            sb.append(",");
        });

        int rankNo = 777;
        int rate = 10000 - rankNo;
        long obfuscatedRate = Calculator.obfuscateRate(rankNo, rate, USER_RATE_FACTOR);
        sb.append(String.format(API_REQ_RANKING_JSON_SELF_ENTRY_FORMAT, rankNo, obfuscatedRate));

        sb.append(API_REQ_RANKING_JSON_SUFFIX);

        return Json.createReader(new StringReader(sb.toString())).readObject();
    }
}
