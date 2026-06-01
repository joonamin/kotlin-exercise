package lotto.domain.vo

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import lotto.domain.entity.TicketResult
import lotto.domain.vo.enums.Rank

class LottoResultTest : BehaviorSpec({
    Given("TicketResult 리스트로 LottoResult가 생성되었을 때") {
        val ticketResults = listOf(
            TicketResult(
                LottoNumbers(listOf(1, 2, 3, 4, 5, 6).map { LottoNumber(it) }),
                Rank.FIRST,
            ),
            TicketResult(
                LottoNumbers(listOf(7, 8, 9, 10, 11, 12).map { LottoNumber(it) }),
                Rank.MISS,
            ),
        )
        val lottoResult = LottoResult(ticketResults)

        When("results를 조회하면") {
            Then("원본 리스트와 동일한 내용을 반환한다") {
                lottoResult.results shouldBe ticketResults
            }

            Then("방어적 복사된 새로운 리스트를 반환한다") {
                lottoResult.results shouldNotBeSameInstanceAs ticketResults
            }
        }
    }

    Given("빈 TicketResult 리스트로 LottoResult가 생성되었을 때") {
        val lottoResult = LottoResult(emptyList())

        When("results를 조회하면") {
            Then("빈 리스트를 반환한다") {
                lottoResult.results shouldBe emptyList()
            }
        }
    }
})
