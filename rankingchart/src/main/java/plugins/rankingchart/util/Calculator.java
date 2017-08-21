package plugins.rankingchart.util;

public class Calculator {
    public static final int NO_RATE = -1;

    /* 順位によって変化するランキング係数 */
    private static final int[] RANKING_RATE_MAGIC_NUMBERS =
            {8931, 1201, 1156, 5061, 4569, 4732, 3779, 4568, 5695, 4619, 4912, 5669, 6586};

    /**
     * ユーザーごとに異なる戦果係数を総当たりで導出する
     *
     * @param rankNo 順位
     * @param obfuscatedRate 難読化済み戦果
     * @param actualRate 実際の戦果
     * @return 戦果係数
     */
    public static int detectUserRateFactor(int rankNo, long obfuscatedRate, int actualRate) {
        for (int i = 1; i < 100; i++) {
            if (calcRate(rankNo, obfuscatedRate, i) == actualRate) {
                return i;
            }
        }
        return 0;
    }

    /**
     * 戦果を計算する
     *
     * @param rankNo 順位
     * @param obfuscatedRate 難読化済み戦果
     * @param userRateFactor 戦果係数
     * @return 戦果
     */
    public static int calcRate(int rankNo, long obfuscatedRate, int userRateFactor) {
        if (userRateFactor > 0) {
            long rate = obfuscatedRate / RANKING_RATE_MAGIC_NUMBERS[rankNo % 13] / userRateFactor - 91;
            return (int) ((rate > 0) ? rate : obfuscatedRate);
        } else {
            return NO_RATE;
        }
    }
}
