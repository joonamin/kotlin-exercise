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
) {
    FIRST(6, Money.won(PRIZE_FIRST)),
    SECOND(5, Money.won(PRIZE_SECOND)),
    THIRD(5, Money.won(PRIZE_THIRD)),
    FOURTH(4, Money.won(PRIZE_FOURTH)),
    FIFTH(3, Money.won(PRIZE_FIFTH)),
    MISS(0, Money.won(PRIZE_MISS)),
    ;

    companion object {
        fun valueOf(
            count: Int,
            bonusMatch: Boolean,
        ): Rank =
            when (count) {
                6 -> FIRST
                5 -> if (bonusMatch) SECOND else THIRD
                4 -> FOURTH
                3 -> FIFTH
                else -> MISS
            }
    }
}
