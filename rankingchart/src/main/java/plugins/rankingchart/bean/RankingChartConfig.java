package plugins.rankingchart.bean;

import lombok.Data;
import lombok.NoArgsConstructor;
import plugins.util.ConfigLoader;

/**
 * 戦果チャートの設定
 */
@Data
@NoArgsConstructor
public class RankingChartConfig {
    /**
     * 戦果係数
     */
    int userRateFactor = 0;

    /**
     * 順位
     */
    int lastRankNo = 0;

    /**
     * 難読化された戦果
     */
    long lastObfuscatedRate = 0;

    /**
     * アプリケーションのデフォルト設定ディレクトリから{@link RankingChartConfig}を取得します、
     * これは次の記述と同等です
     * <blockquote>
     * <code>Config.getDefault().get(RankingChartConfig.class, RankingChartConfig::new)</code>
     * </blockquote>
     *
     * @return {@link RankingChartConfig}
     */
    public static RankingChartConfig get() {
        return ConfigLoader.load(RankingChartConfig.class, RankingChartConfig::new);
    }
}
