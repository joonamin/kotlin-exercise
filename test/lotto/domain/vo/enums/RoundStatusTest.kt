package lotto.domain.vo.enums

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class RoundStatusTest : BehaviorSpec({
    Given("상태 문자열을 Enum으로 변환할 때") {
        data class FromStatusTestCase(
            val statusString: String,
            val expectedStatus: RoundStatus
        )

        listOf(
            FromStatusTestCase("OPEN", RoundStatus.OPEN),
            FromStatusTestCase("FINISHED", RoundStatus.FINISHED),
            FromStatusTestCase("IN PROGRESS", RoundStatus.IN_PROGRESS),
            FromStatusTestCase("UNKNOWN", RoundStatus.OPEN),
            FromStatusTestCase("", RoundStatus.OPEN)
        ).forEach { testCase ->
            When("'${testCase.statusString}' 문자열로 변환하면") {
                Then("${testCase.expectedStatus}를 반환한다") {
                    RoundStatus.fromStatus(testCase.statusString) shouldBe testCase.expectedStatus
                }
            }
        }
    }

    Given("각 RoundStatus의 status 속성을 확인할 때") {
        data class StatusPropertyTestCase(
            val roundStatus: RoundStatus,
            val expectedStatusString: String
        )

        listOf(
            StatusPropertyTestCase(RoundStatus.OPEN, "OPEN"),
            StatusPropertyTestCase(RoundStatus.IN_PROGRESS, "IN PROGRESS"),
            StatusPropertyTestCase(RoundStatus.FINISHED, "FINISHED")
        ).forEach { testCase ->
            When("${testCase.roundStatus}의 status 값을 조회하면") {
                Then("'${testCase.expectedStatusString}'이다") {
                    testCase.roundStatus.status shouldBe testCase.expectedStatusString
                }
            }
        }
    }
})
