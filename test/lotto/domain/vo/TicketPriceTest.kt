package lotto.domain.vo

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import payment.domain.vo.Money

class TicketPriceTest : BehaviorSpec({
    Given("기본 티켓 가격(DEFAULT)이 주어졌을 때") {
        val ticketPrice = TicketPrice.DEFAULT

        When("단가를 확인하면") {
            Then("1000원이다") {
                ticketPrice.unitPrice shouldBe Money.won(TicketPrice.PRICE_PER_TICKET)
            }
        }

        When("3장의 총 비용을 계산하면") {
            Then("3000원이다") {
                val totalCost = ticketPrice.totalCost(3)
                totalCost shouldBe Money.won(3000)
            }
        }

        When("1장의 총 비용을 계산하면") {
            Then("1000원이다") {
                val totalCost = ticketPrice.totalCost(1)
                totalCost shouldBe Money.won(1000)
            }
        }
    }

    Given("구매 수량이 0 이하일 때") {
        val ticketPrice = TicketPrice.DEFAULT

        When("0장으로 총 비용을 계산하면") {
            Then("IllegalArgumentException이 발생한다") {
                shouldThrow<IllegalArgumentException> {
                    ticketPrice.totalCost(0)
                }
            }
        }

        When("음수로 총 비용을 계산하면") {
            Then("IllegalArgumentException이 발생한다") {
                shouldThrow<IllegalArgumentException> {
                    ticketPrice.totalCost(-1)
                }
            }
        }
    }

    Given("예산에 따른 최대 구매 가능 수량을 계산할 때") {
        val ticketPrice = TicketPrice.DEFAULT

        data class AffordableTestCase(val budget: Money, val expectedCount: Int)

        listOf(
            AffordableTestCase(Money.won(5000), 5),
            AffordableTestCase(Money.won(500), 0),
            AffordableTestCase(Money.won(1000), 1),
            AffordableTestCase(Money.ZERO, 0),
        ).forEach { testCase ->
            When("${testCase.budget}의 예산이 있으면") {
                Then("${testCase.expectedCount}장 구매 가능하다") {
                    ticketPrice.maxAffordableCount(testCase.budget) shouldBe testCase.expectedCount
                }
            }
        }
    }

    Given("PRICE_PER_TICKET 상수를 확인할 때") {
        When("값을 조회하면") {
            Then("1000이다") {
                TicketPrice.PRICE_PER_TICKET shouldBe 1000
            }
        }
    }
})
