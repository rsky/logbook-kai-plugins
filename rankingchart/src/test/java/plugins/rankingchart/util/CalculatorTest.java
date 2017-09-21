package plugins.rankingchart.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("WeakerAccess")
@Tag("util")
public class CalculatorTest {

    private static final int RANK_NO = 187;
    private static final int USER_RATE_FACTOR = 46;
    private static final int ACTUAL_RATE = 1387;
    private static final long OBF_RATE = 321719216;

    @Test
    @DisplayName("戦果を計算する")
    public void calcRateSuccess() {
        int rate = Calculator.calcRate(RANK_NO, OBF_RATE, USER_RATE_FACTOR);
        assertEquals(ACTUAL_RATE, rate);
    }

    @Test
    @DisplayName("戦果係数が0のため戦果を求められない")
    public void calcRateFail() {
        int rate = Calculator.calcRate(RANK_NO, OBF_RATE, 0);
        assertEquals(Calculator.NO_RATE, rate);
    }

    @Test
    @DisplayName("ユーザー別戦果係数を総当たりで求める")
    public void detectUserRateFactorSuccess() {
        int factor = Calculator.detectUserRateFactor(RANK_NO, OBF_RATE, ACTUAL_RATE);
        assertEquals(USER_RATE_FACTOR, factor);
    }

    @Test
    @DisplayName("順位のズレによりユーザー別戦果係数の導出に失敗する")
    public void detectUserRateFactorFailBadRankNo() {
        int factor = Calculator.detectUserRateFactor(RANK_NO + 1, OBF_RATE, ACTUAL_RATE);
        assertEquals(0, factor);
    }

    @Test
    @DisplayName("仮戦果のズレによりユーザー別戦果係数の導出に失敗する")
    public void detectUserRateFactorFailBadObfuscatedRate() {
        int factor = Calculator.detectUserRateFactor(RANK_NO, OBF_RATE + 1, ACTUAL_RATE);
        assertEquals(0, factor);
    }

    @Test
    @DisplayName("実戦果のズレによりユーザー別戦果係数の導出に失敗する")
    public void detectUserRateFactorFailBadActualRate() {
        int factor = Calculator.detectUserRateFactor(RANK_NO, OBF_RATE, ACTUAL_RATE + 1);
        assertEquals(0, factor);
    }

    @Test
    @DisplayName("ユーザー別戦果係数をランキング1-20位の戦果の最大公約数より求める")
    public void detectUserRateFactorByMapSuccess() {
        Map<Integer, Long> source = new HashMap<Integer, Long>() {{
            put(1, 337718798L);
            put(2, 290021904L);
            put(3, 927964716L);
            put(4, 826824516L);
            put(5, 817140688L);
            put(6, 618849040L);
            put(7, 731035312L);
            put(8, 881267080L);
            put(9, 705626154L);
            put(10, 719657120L);
            put(11, 797446892L);
            put(12, 925227624L);
            put(13, 1241105346L);
            put(14, 164909310L);
            put(15, 150753960L);
            put(16, 648597516L);
            put(17, 574405542L);
            put(18, 594244560L);
            put(19, 474392986L);
            put(20, 566715216L);
        }};
        int factor = Calculator.detectUserRateFactor(source);
        assertEquals(USER_RATE_FACTOR, factor);
    }

    @Test
    @DisplayName("辞書に不正なデータがあるので戦果係数の導出に失敗する")
    public void detectUserRateFactorFailBadMap() {
        Map<Integer, Long> source = new HashMap<Integer, Long>() {{
            put(1, 337718799L);
            put(2, 290021904L);
            put(3, 927964716L);
        }};
        int factor = Calculator.detectUserRateFactor(source);
        assertEquals(0, factor);
    }

    @Test
    @DisplayName("辞書が空なので戦果係数を求められない")
    public void detectUserRateFactorFailEmptyMap() {
        int factor = Calculator.detectUserRateFactor(Collections.emptyMap());
        assertEquals(0, factor);
    }

    @Test
    @DisplayName("順位別戦果係数は互いに素である")
    public void rankRateFactorsAreMutuallyPrime() {
        int gcd = IntStream.of(Calculator.RANK_RATE_FACTORS)
                .asLongStream()
                .boxed()
                .map(BigInteger::valueOf)
                .reduce(BigInteger::gcd)
                .orElse(BigInteger.ZERO)
                .intValue();
        assertEquals(1, gcd);
    }
}
