package plugins.rankingchart.util;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.IntStream;

public class Calculator {
    public static final int NO_RATE = -1;

    /** 順位によって変化するランキング係数 */
    private static final int[] RANKING_RATE_MAGIC_NUMBERS =
            {8931, 1201, 1156, 5061, 4569, 4732, 3779, 4568, 5695, 4619, 4912, 5669, 6586};

    /**
     * 戦果を計算する
     *
     * @param rankNo 順位
     * @param obfuscatedRate 難読化済み戦果
     * @param userRateFactor 戦果係数
     * @param strict 難読化済み戦果が係数で割り切れない場合は失敗にする
     * @return 戦果
     */
    private static int calcRate(int rankNo, long obfuscatedRate, int userRateFactor, boolean strict) {
        int rankRateFactor = RANKING_RATE_MAGIC_NUMBERS[rankNo % 13];
        if (strict && obfuscatedRate % rankRateFactor != 0) {
            return NO_RATE;
        } else if (userRateFactor > 0) {
            if (strict && (obfuscatedRate / rankRateFactor) % userRateFactor != 0) {
                return NO_RATE;
            }
            return (int) (obfuscatedRate / rankRateFactor / userRateFactor - 91);
        } else {
            return NO_RATE;
        }
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
        return calcRate(rankNo, obfuscatedRate, userRateFactor, false);
    }

    /**
     * ユーザーごとに異なる戦果係数を総当たりで導出する
     *
     * @param rankNo 順位
     * @param obfuscatedRate 難読化済み戦果
     * @param actualRate 実際の戦果
     * @return 戦果係数
     */
    public static int detectUserRateFactor(int rankNo, long obfuscatedRate, int actualRate) {
        return IntStream.range(1, 100)
                .filter(i -> calcRate(rankNo, obfuscatedRate, i, true) == actualRate)
                .findFirst()
                .orElse(0);
    }

    /**
     * ランキング情報から戦果係数を導出する
     * 正しい値を求めるにはある程度のサンプル数が必要
     *
     * @param source 順位をキー、難読化済み戦果を値とするMap
     * @return 戦果係数
     */
    public static int detectUserRateFactor(Map<Integer, Long> source) {
        // 難読化された戦果を順位別係数で割り、BigIntegerに変換する
        List<BigInteger> list = new ArrayList<>();
        source.entrySet()
                .stream()
                .filter(e -> e.getValue() % RANKING_RATE_MAGIC_NUMBERS[e.getKey() % 13] == 0)
                .map(e -> e.getValue() / RANKING_RATE_MAGIC_NUMBERS[e.getKey() % 13])
                .map(BigInteger::valueOf)
                .forEach(list::add);

        // 割り切れない値が含まれていた場合は異常値とみなす
        // (順位別係数やアルゴリズムの見直しが必要になる)
        if (list.size() != source.size()) {
            return 0;
        }

        list = new ArrayList<>(new HashSet<>(list));

        // 1つの値に収束するまで繰り返し最大公約数を求め、残ったのが戦果係数である
        // (通常は2-passで終わる)
        while (list.size() > 1) {
            Set<BigInteger> set = new HashSet<>();

            // 全ての組み合わせの最大公約数セットを作る
            while (list.size() > 1) {
                BigInteger a = list.remove(0);
                list.stream().map(b -> b.gcd(a)).forEach(set::add);
            }

            list = new ArrayList<>(set);
        }

        return (list.size() == 1) ? list.get(0).intValue() : 0;
    }
}
