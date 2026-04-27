package lotto.domain.vo.enums

import payment.domain.vo.Money

private const val PRIZE_FIRST = 2_100_000_000
private const val PRIZE_SECOND = 60_000_000
private const val PRIZE_THIRD = 1_500_000
private const val PRIZE_FOURTH = 50_000
private const val PRIZE_FIFTH = 5_000
private const val PRIZE_MISS = 0

enum class Rank(
    val matchCount: Int,
    val prize: Money,
    val requiresBonus: Boolean = false,
) {
    FIRST(6, Money.won(PRIZE_FIRST)),
    SECOND(5, Money.won(PRIZE_SECOND), true),
    THIRD(5, Money.won(PRIZE_THIRD)),
    FOURTH(4, Money.won(PRIZE_FOURTH)),
    FIFTH(3, Money.won(PRIZE_FIFTH)),
    MISS(0, Money.won(PRIZE_MISS)),
    ;

    companion object {
        // values()의 대응..
        // kotlin에서는 entries 라는 values 래퍼가 존재
        // values()는 항상 새로운 배열을 생성하여 복사하여 제공
        // entries는 내부적으로 캐싱된 리스트를 반환
        fun valueOf(
            count: Int,
            bonusMatch: Boolean,
        ): Rank =
            entries.find {
                it.matchCount == count && it.requiresBonus == (count == 5 && bonusMatch)
            }
                ?: MISS
    }
}
