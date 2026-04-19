package lotto.domain.vo.enums

import Money

enum class Rank(
    val matchCount: Int,
    val prize: Money,
) {
    FIRST(6, Money(PRIZE_FIRST)),
    SECOND(5, Money(PRIZE_SECOND)),
    THIRD(5, Money(PRIZE_THIRD)),
    FOURTH(4, Money(PRIZE_FOURTH)),
    FIFTH(3, Money(PRIZE_FIFTH)),
    MISS(0, Money(PRIZE_MISS)), // 이건 3개 미만인 수들에 대해서 어차피 matchCount는 당첨금에 영향 X 므로 0으로 처리
    ;

    companion object {
        const val PRIZE_FIRST = 2_100_000_000
        const val PRIZE_SECOND = 60_000_000
        const val PRIZE_THIRD = 1_500_000
        const val PRIZE_FOURTH = 50_000
        const val PRIZE_FIFTH = 5_000
        const val PRIZE_MISS = 0

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
