package lotto.domain.vo.enums

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import payment.domain.vo.Money

class RankTest : BehaviorSpec({
    Given("각 등수의 조건을 확인할 때") {
        data class ConditionTestCase(
            val matchCount: Int,
            val matchBonus: Boolean,
            val expectedRank: Rank
        )

        listOf(
            ConditionTestCase(6, false, Rank.FIRST),
            ConditionTestCase(6, true, Rank.FIRST),
            ConditionTestCase(5, true, Rank.SECOND),
            ConditionTestCase(5, false, Rank.THIRD),
            ConditionTestCase(4, false, Rank.FOURTH),
            ConditionTestCase(4, true, Rank.FOURTH),
            ConditionTestCase(3, false, Rank.FIFTH),
            ConditionTestCase(3, true, Rank.FIFTH),
            ConditionTestCase(2, false, Rank.MISS),
            ConditionTestCase(1, false, Rank.MISS),
            ConditionTestCase(0, false, Rank.MISS)
        ).forEach { testCase ->
            When("${testCase.matchCount}개 일치, 보너스 일치 여부가 ${testCase.matchBonus}일 때") {
                Then("${testCase.expectedRank}을 반환한다") {
                    Rank.valueOf(testCase.matchCount, testCase.matchBonus) shouldBe testCase.expectedRank
                }
            }
        }
    }

    Given("각 등수의 속성을 확인할 때") {
        data class PropertyTestCase(
            val rank: Rank,
            val expectedPrize: Money,
            val expectedMatchCount: Int,
            val expectedRequiresBonus: Boolean
        )

        listOf(
            PropertyTestCase(Rank.FIRST, Money.won(2_100_000_000), 6, false),
            PropertyTestCase(Rank.SECOND, Money.won(60_000_000), 5, true),
            PropertyTestCase(Rank.THIRD, Money.won(1_500_000), 5, false),
            PropertyTestCase(Rank.FOURTH, Money.won(50_000), 4, false),
            PropertyTestCase(Rank.FIFTH, Money.won(5_000), 3, false),
            PropertyTestCase(Rank.MISS, Money.won(0), 0, false)
        ).forEach { testCase ->
            When("${testCase.rank}의 속성을 검증하면") {
                Then("상금은 ${testCase.expectedPrize}, matchCount는 ${testCase.expectedMatchCount}, requiresBonus는 ${testCase.expectedRequiresBonus}이다") {
                    testCase.rank.prize shouldBe testCase.expectedPrize
                    testCase.rank.matchCount shouldBe testCase.expectedMatchCount
                    testCase.rank.requiresBonus shouldBe testCase.expectedRequiresBonus
                }
            }
        }
    }
})
