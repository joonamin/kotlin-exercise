package lotto.infrastructure.strategy

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.ints.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldNot
import io.kotest.matchers.collections.containAnyOf
import lotto.domain.vo.LottoNumber

class RandomLottoGenerationStrategyTest : BehaviorSpec({
    val strategy = RandomLottoGenerationStrategy()

    Given("번호를 생성할 때") {
        When("6개의 번호를 요청하면") {
            Then("정확히 6개가 생성된다") {
                val result = strategy.generate(6, emptyList())
                result shouldHaveSize 6
            }
        }

        When("1개의 번호를 요청하면") {
            Then("정확히 1개가 생성된다") {
                val result = strategy.generate(1, emptyList())
                result shouldHaveSize 1
            }
        }
    }

    Given("생성된 번호의 유효 범위를 검증할 때") {
        When("번호를 생성하면") {
            Then("모든 번호가 1~45 범위 내에 있다") {
                val result = strategy.generate(6, emptyList())
                result.forEach { lottoNumber ->
                    lottoNumber.number shouldBeGreaterThanOrEqual LottoNumber.MIN_NUMBER
                    lottoNumber.number shouldBeLessThanOrEqual LottoNumber.MAX_NUMBER
                }
            }
        }
    }

    Given("제외할 번호가 주어졌을 때") {
        When("특정 번호를 제외하고 생성하면") {
            Then("제외된 번호가 결과에 포함되지 않는다") {
                val excludeNumbers = listOf(1, 2, 3, 4, 5, 6).map { LottoNumber(it) }
                val result = strategy.generate(6, excludeNumbers)
                result shouldNot containAnyOf(excludeNumbers)
            }
        }
    }
})
