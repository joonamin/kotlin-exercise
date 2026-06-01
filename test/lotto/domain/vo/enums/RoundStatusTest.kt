package lotto.domain.vo.enums

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class RoundStatusTest : BehaviorSpec({
    Given("유효한 상태 문자열이 주어졌을 때") {
        When("OPEN 문자열로 변환하면") {
            Then("OPEN을 반환한다") {
                RoundStatus.fromStatus("OPEN") shouldBe RoundStatus.OPEN
            }
        }

        When("FINISHED 문자열로 변환하면") {
            Then("FINISHED를 반환한다") {
                RoundStatus.fromStatus("FINISHED") shouldBe RoundStatus.FINISHED
            }
        }

        When("IN PROGRESS 문자열로 변환하면") {
            Then("IN_PROGRESS를 반환한다") {
                RoundStatus.fromStatus("IN PROGRESS") shouldBe RoundStatus.IN_PROGRESS
            }
        }
    }

    Given("알 수 없는 상태 문자열이 주어졌을 때") {
        When("UNKNOWN 문자열로 변환하면") {
            Then("기본값 OPEN을 반환한다") {
                RoundStatus.fromStatus("UNKNOWN") shouldBe RoundStatus.OPEN
            }
        }

        When("빈 문자열로 변환하면") {
            Then("기본값 OPEN을 반환한다") {
                RoundStatus.fromStatus("") shouldBe RoundStatus.OPEN
            }
        }
    }

    Given("각 RoundStatus의 status 속성을 확인할 때") {
        When("status 값을 조회하면") {
            Then("OPEN의 status는 'OPEN'이다") {
                RoundStatus.OPEN.status shouldBe "OPEN"
            }

            Then("IN_PROGRESS의 status는 'IN PROGRESS'이다") {
                RoundStatus.IN_PROGRESS.status shouldBe "IN PROGRESS"
            }

            Then("FINISHED의 status는 'FINISHED'이다") {
                RoundStatus.FINISHED.status shouldBe "FINISHED"
            }
        }
    }
})
