package lotto.application.dto

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import lotto.domain.vo.LottoNumber
import lotto.domain.vo.LottoNumbers

class PurchaseLottoCommandTest : BehaviorSpec({
    fun lottoNumbers(vararg nums: Int) = LottoNumbers(nums.map { LottoNumber(it) })

    Given("유효한 파라미터가 주어졌을 때") {
        When("수동 번호 없이 자동으로만 구매하면") {
            Then("정상적으로 생성된다") {
                val command = PurchaseLottoCommand.of(
                    roundNumber = 1,
                    ticketCount = 3,
                    manualNumbersList = emptyList(),
                )
                command.roundNumber shouldBe 1
                command.ticketCount shouldBe 3
                command.manualNumbersList shouldBe emptyList()
            }
        }

        When("수동 번호와 함께 생성하면") {
            Then("정상적으로 생성된다") {
                val manual = listOf(lottoNumbers(1, 2, 3, 4, 5, 6))
                val command = PurchaseLottoCommand.of(
                    roundNumber = 1,
                    ticketCount = 3,
                    manualNumbersList = manual,
                )
                command.manualNumbersList shouldBe manual
            }
        }

        When("수동 구매 수와 총 구매 수가 같으면") {
            Then("정상적으로 생성된다") {
                val manual = listOf(
                    lottoNumbers(1, 2, 3, 4, 5, 6),
                    lottoNumbers(7, 8, 9, 10, 11, 12),
                )
                val command = PurchaseLottoCommand.of(
                    roundNumber = 1,
                    ticketCount = 2,
                    manualNumbersList = manual,
                )
                command.ticketCount shouldBe 2
                command.manualNumbersList shouldBe manual
            }
        }
    }

    Given("구매 수량이 0 이하일 때") {
        When("ticketCount가 0이면") {
            Then("IllegalArgumentException이 발생한다") {
                shouldThrow<IllegalArgumentException> {
                    PurchaseLottoCommand.of(
                        roundNumber = 1,
                        ticketCount = 0,
                        manualNumbersList = emptyList(),
                    )
                }
            }
        }

        When("ticketCount가 음수이면") {
            Then("IllegalArgumentException이 발생한다") {
                shouldThrow<IllegalArgumentException> {
                    PurchaseLottoCommand.of(
                        roundNumber = 1,
                        ticketCount = -1,
                        manualNumbersList = emptyList(),
                    )
                }
            }
        }
    }

    Given("수동 구매 개수가 총 구매 개수를 초과할 때") {
        When("수동 3개, 총 2개로 생성하면") {
            Then("IllegalArgumentException이 발생한다") {
                val manual = listOf(
                    lottoNumbers(1, 2, 3, 4, 5, 6),
                    lottoNumbers(7, 8, 9, 10, 11, 12),
                    lottoNumbers(13, 14, 15, 16, 17, 18),
                )
                shouldThrow<IllegalArgumentException> {
                    PurchaseLottoCommand.of(
                        roundNumber = 1,
                        ticketCount = 2,
                        manualNumbersList = manual,
                    )
                }
            }
        }
    }

    Given("manualNumbersList의 방어적 복사를 검증할 때") {
        When("manualNumbersList를 조회하면") {
            Then("원본과 다른 인스턴스를 반환한다") {
                val original = listOf(lottoNumbers(1, 2, 3, 4, 5, 6))
                val command = PurchaseLottoCommand.of(
                    roundNumber = 1,
                    ticketCount = 1,
                    manualNumbersList = original,
                )
                command.manualNumbersList shouldNotBeSameInstanceAs original
            }
        }
    }

    Given("destructuring을 사용할 때") {
        When("component 함수로 분해하면") {
            Then("roundNumber와 ticketCount가 올바르게 반환된다") {
                val command = PurchaseLottoCommand.of(
                    roundNumber = 5,
                    ticketCount = 3,
                    manualNumbersList = emptyList(),
                )
                val (roundNumber, ticketCount) = command
                roundNumber shouldBe 5
                ticketCount shouldBe 3
            }
        }
    }
})
