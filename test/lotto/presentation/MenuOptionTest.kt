package lotto.presentation

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class MenuOptionTest : BehaviorSpec({
    Given("유효한 메뉴 ID가 주어졌을 때") {
        When("from(1)을 호출하면") {
            Then("PURCHASE를 반환한다") {
                MenuOption.from(1) shouldBe MenuOption.PURCHASE
            }
        }

        When("from(2)을 호출하면") {
            Then("DRAW를 반환한다") {
                MenuOption.from(2) shouldBe MenuOption.DRAW
            }
        }

        When("from(3)을 호출하면") {
            Then("CHARGE를 반환한다") {
                MenuOption.from(3) shouldBe MenuOption.CHARGE
            }
        }

        When("from(4)을 호출하면") {
            Then("QUERY_HISTORY를 반환한다") {
                MenuOption.from(4) shouldBe MenuOption.QUERY_HISTORY
            }
        }

        When("from(5)을 호출하면") {
            Then("EXIT를 반환한다") {
                MenuOption.from(5) shouldBe MenuOption.EXIT
            }
        }
    }

    Given("유효하지 않은 메뉴 ID가 주어졌을 때") {
        When("from(0)을 호출하면") {
            Then("null을 반환한다") {
                MenuOption.from(0) shouldBe null
            }
        }

        When("from(999)를 호출하면") {
            Then("null을 반환한다") {
                MenuOption.from(999) shouldBe null
            }
        }

        When("from(-1)을 호출하면") {
            Then("null을 반환한다") {
                MenuOption.from(-1) shouldBe null
            }
        }
    }

    Given("각 MenuOption의 속성을 확인할 때") {
        When("id와 description을 검증하면") {
            Then("PURCHASE는 id=1, description='로또 구매'이다") {
                MenuOption.PURCHASE.id shouldBe 1
                MenuOption.PURCHASE.description shouldBe "로또 구매"
            }

            Then("EXIT는 id=5, description='종료'이다") {
                MenuOption.EXIT.id shouldBe 5
                MenuOption.EXIT.description shouldBe "종료"
            }
        }
    }
})
