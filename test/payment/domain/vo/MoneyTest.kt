package payment.domain.vo

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.shouldBe

class MoneyTest : BehaviorSpec({
    Given("유효한 금액으로 Money를 생성할 때") {
        When("0원으로 생성하면") {
            Then("정상적으로 생성된다") {
                val money = Money(0)
                money.value shouldBe 0
            }
        }

        When("양수로 생성하면") {
            Then("정상적으로 생성된다") {
                val money = Money(1000)
                money.value shouldBe 1000
            }
        }
    }

    Given("음수 금액으로 Money를 생성할 때") {
        When("-1로 생성하면") {
            Then("IllegalArgumentException이 발생한다") {
                shouldThrow<IllegalArgumentException> {
                    Money(-1)
                }
            }
        }
    }

    Given("두 Money의 연산을 수행할 때") {
        When("plus 연산을 수행하면") {
            Then("두 금액의 합이 반환된다") {
                val result = Money(1000) + Money(2000)
                result shouldBe Money(3000)
            }
        }

        When("minus 연산을 수행하면") {
            Then("차액이 반환된다") {
                val result = Money(3000) - Money(1000)
                result shouldBe Money(2000)
            }
        }

        When("minus 결과가 음수가 되면") {
            Then("IllegalArgumentException이 발생한다") {
                shouldThrow<IllegalArgumentException> {
                    Money(1000) - Money(2000)
                }
            }
        }

        When("times 연산을 수행하면") {
            Then("곱셈 결과가 반환된다") {
                val result = Money(1000) * 3
                result shouldBe Money(3000)
            }
        }

        When("times에 0을 곱하면") {
            Then("0원이 반환된다") {
                val result = Money(1000) * 0
                result shouldBe Money.ZERO
            }
        }
    }

    Given("두 Money를 비교할 때") {
        When("compareTo로 비교하면") {
            Then("큰 금액이 작은 금액보다 크다") {
                Money(2000) shouldBeGreaterThan Money(1000)
            }

            Then("작은 금액이 큰 금액보다 작다") {
                Money(1000) shouldBeLessThan Money(2000)
            }

            Then("같은 금액은 동등하다") {
                Money(1000).compareTo(Money(1000)) shouldBe 0
            }
        }
    }

    Given("isAffordable을 검증할 때") {
        When("잔액이 비용보다 크거나 같으면") {
            Then("true를 반환한다") {
                Money(5000).isAffordable(Money(3000)) shouldBe true
                Money(3000).isAffordable(Money(3000)) shouldBe true
            }
        }

        When("잔액이 비용보다 작으면") {
            Then("false를 반환한다") {
                Money(1000).isAffordable(Money(3000)) shouldBe false
            }
        }
    }

    Given("companion object의 팩토리와 상수를 확인할 때") {
        When("ZERO를 조회하면") {
            Then("0원 Money를 반환한다") {
                Money.ZERO shouldBe Money(0)
            }
        }

        When("won 팩토리로 생성하면") {
            Then("해당 금액의 Money를 반환한다") {
                Money.won(5000) shouldBe Money(5000)
            }
        }
    }
})
