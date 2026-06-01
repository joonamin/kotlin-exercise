package lotto.domain.vo.enums

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import payment.domain.vo.Money

class RankTest : BehaviorSpec({
    Given("6개 일치할 때") {
        When("valueOf(6, false)를 호출하면") {
            Then("1등(FIRST)을 반환한다") {
                Rank.valueOf(6, false) shouldBe Rank.FIRST
            }
        }

        When("valueOf(6, true)를 호출해도") {
            Then("1등(FIRST)을 반환한다") {
                Rank.valueOf(6, true) shouldBe Rank.FIRST
            }
        }
    }

    Given("5개 일치할 때") {
        When("보너스가 일치하면") {
            Then("2등(SECOND)을 반환한다") {
                Rank.valueOf(5, true) shouldBe Rank.SECOND
            }
        }

        When("보너스가 불일치하면") {
            Then("3등(THIRD)을 반환한다") {
                Rank.valueOf(5, false) shouldBe Rank.THIRD
            }
        }
    }

    Given("4개 일치할 때") {
        When("valueOf(4, false)를 호출하면") {
            Then("4등(FOURTH)을 반환한다") {
                Rank.valueOf(4, false) shouldBe Rank.FOURTH
            }
        }

        When("valueOf(4, true)를 호출해도") {
            Then("4등(FOURTH)을 반환한다") {
                Rank.valueOf(4, true) shouldBe Rank.FOURTH
            }
        }
    }

    Given("3개 일치할 때") {
        When("valueOf(3, false)를 호출하면") {
            Then("5등(FIFTH)을 반환한다") {
                Rank.valueOf(3, false) shouldBe Rank.FIFTH
            }
        }

        When("valueOf(3, true)를 호출해도") {
            Then("5등(FIFTH)을 반환한다") {
                Rank.valueOf(3, true) shouldBe Rank.FIFTH
            }
        }
    }

    Given("2개 이하 일치할 때") {
        When("valueOf(2, false)를 호출하면") {
            Then("꽝(MISS)을 반환한다") {
                Rank.valueOf(2, false) shouldBe Rank.MISS
            }
        }

        When("valueOf(1, false)를 호출하면") {
            Then("꽝(MISS)을 반환한다") {
                Rank.valueOf(1, false) shouldBe Rank.MISS
            }
        }

        When("valueOf(0, false)를 호출하면") {
            Then("꽝(MISS)을 반환한다") {
                Rank.valueOf(0, false) shouldBe Rank.MISS
            }
        }
    }

    Given("각 등수의 상금을 확인할 때") {
        When("모든 Rank의 prize를 검증하면") {
            Then("FIRST 상금은 21억원이다") {
                Rank.FIRST.prize shouldBe Money.won(2_100_000_000)
            }

            Then("SECOND 상금은 6천만원이다") {
                Rank.SECOND.prize shouldBe Money.won(60_000_000)
            }

            Then("THIRD 상금은 150만원이다") {
                Rank.THIRD.prize shouldBe Money.won(1_500_000)
            }

            Then("FOURTH 상금은 5만원이다") {
                Rank.FOURTH.prize shouldBe Money.won(50_000)
            }

            Then("FIFTH 상금은 5천원이다") {
                Rank.FIFTH.prize shouldBe Money.won(5_000)
            }

            Then("MISS 상금은 0원이다") {
                Rank.MISS.prize shouldBe Money.won(0)
            }
        }
    }

    Given("각 등수의 matchCount를 확인할 때") {
        When("matchCount를 검증하면") {
            Then("FIRST는 6이다") {
                Rank.FIRST.matchCount shouldBe 6
            }

            Then("SECOND는 5이다") {
                Rank.SECOND.matchCount shouldBe 5
            }

            Then("THIRD는 5이다") {
                Rank.THIRD.matchCount shouldBe 5
            }

            Then("FOURTH는 4이다") {
                Rank.FOURTH.matchCount shouldBe 4
            }

            Then("FIFTH는 3이다") {
                Rank.FIFTH.matchCount shouldBe 3
            }

            Then("MISS는 0이다") {
                Rank.MISS.matchCount shouldBe 0
            }
        }
    }

    Given("requiresBonus 속성을 확인할 때") {
        When("SECOND의 requiresBonus를 확인하면") {
            Then("true이다") {
                Rank.SECOND.requiresBonus shouldBe true
            }
        }

        When("나머지 등수의 requiresBonus를 확인하면") {
            Then("모두 false이다") {
                Rank.FIRST.requiresBonus shouldBe false
                Rank.THIRD.requiresBonus shouldBe false
                Rank.FOURTH.requiresBonus shouldBe false
                Rank.FIFTH.requiresBonus shouldBe false
                Rank.MISS.requiresBonus shouldBe false
            }
        }
    }
})
