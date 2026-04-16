package lotto.domain.vo.enums

import Money

enum class Rank(
    val matchCount: Int,
    val prize: Money,
) {
    FIRST(6, Money(2_000_000_000)),
    SECOND(5, Money(30_000_000)),
    THIRD(5, Money(1_500_000)),
    FOURTH(4, Money(50_000)),
    FIFTH(3, Money(5_000)),
    MISS(0, Money(0)), // 이건 3개 미만인 수들에 대해서 어차피 matchCount는 당첨금에 영향 X 므로 0으로 처리
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
