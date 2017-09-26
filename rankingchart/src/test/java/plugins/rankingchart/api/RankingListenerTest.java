package plugins.rankingchart.api;

import com.example.mockito.MockitoExtension;
import logbook.internal.Config;
import logbook.plugin.PluginContainer;
import logbook.proxy.RequestMetaData;
import logbook.proxy.ResponseMetaData;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import plugins.rankingchart.bean.LogItem;
import plugins.rankingchart.bean.RankingChartConfig;
import plugins.rankingchart.util.Database;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@SuppressWarnings("WeakerAccess")
@Tag("api")
@ExtendWith(MockitoExtension.class)
public class RankingListenerTest {
    private List<InvocationOnMock> invocationOnMocks;

    @BeforeAll
    public static void setUpClass() {
        PluginContainer.getInstance().init(Collections.emptyList());
    }

    @BeforeEach
    public void setUp(@Mock Database database) {
        invocationOnMocks = new CopyOnWriteArrayList<>();

        doAnswer(invocationOnMocks::add).when(database).update(any());

        Database.setDefault(database);

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

        ClassLoader classLoader = getClass().getClassLoader();

        String filename = "api_get_member_record_subset.json";
        InputStream stream = classLoader.getResourceAsStream(filename);
        JsonObject json = Json.createReader(stream).readObject();
        listener.accept(json, requestMetaData, responseMetaData);

        for (int i = 1; i < 10; i++) {
            filename = String.format("api_req_ranking_%02d.json", i);
            stream = classLoader.getResourceAsStream(filename);
            json = Json.createReader(stream).readObject();
            listener.accept(json, requestMetaData, responseMetaData);
        }

        // データが足りないので Database#update() はまだコールされない
        assertEquals(0, invocationOnMocks.size());

        filename = String.format("api_req_ranking_%02d.json", 10);
        stream = classLoader.getResourceAsStream(filename);
        json = Json.createReader(stream).readObject();
        listener.accept(json, requestMetaData, responseMetaData);

        // Database#update() が1回コールされた
        assertEquals(1, invocationOnMocks.size());

        // 自分のIDとニックネーム
        assertEquals(1, listener.getMemberId());
        assertEquals("Test User", listener.getNickname());

        // 戦果係数
        RankingChartConfig config = RankingChartConfig.get();
        assertEquals(46, config.getUserRateFactor());

        // ログ
        LogItem item = invocationOnMocks.get(0).getArgument(0);
        assertEquals(7369, item.getRank1().intValue());
        assertEquals(4247, item.getRank5().intValue());
        assertEquals(3237, item.getRank20().intValue());
        assertEquals(2230, item.getRank100().intValue());
        assertNull(item.getRank500());
        assertEquals(2245, item.getRate().intValue());
        assertEquals(95, item.getRankNo().intValue());
    }
}
