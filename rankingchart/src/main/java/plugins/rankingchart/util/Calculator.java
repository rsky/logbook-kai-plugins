package plugins.rankingchart.util;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

public class Calculator {
    public static final int NO_RATE = -1;

    /** 順位によって変化するランキング係数 */
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

    /**
     * ランキング情報から戦果係数を導出する
     *
     * @param source 順位 -> 難読化済み戦果のマップ
     * @return 戦果係数
     */
    public static int detectRateCoefficient(Map<Integer, Long> source) {
        // 難読化された戦果を順位別係数で割り、BigIntegerに変換する
        Set<BigInteger> set = new HashSet<>();
        source.entrySet()
                .stream()
                .map(e -> e.getValue() / RANKING_RATE_MAGIC_NUMBERS[e.getKey() % 13])
                .map(BigInteger::valueOf)
                .forEach(set::add);

        List<BigInteger> list = new ArrayList<>(set);
        List<Integer> gcdList = new ArrayList<>();

        // 全ての組み合わせに対して最大公約数を求める
        while (list.size() > 1) {
            BigInteger a = list.remove(0);
            list.stream()
                    .map(b -> b.gcd(a))
                    .map(BigInteger::intValue)
                    //.filter(i -> i < 100) // 今のところ[1, 99]の範囲であるようだが
                    .forEach(gcdList::add);
        }

        // 最頻出する最大公約数が戦果係数である（たぶん）
        return gcdList.stream()
                .collect(Collectors.groupingBy(e -> e, Collectors.counting()))
                .entrySet()
                .stream()
                .max(Comparator.comparing(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .orElse(0);
    }
}
