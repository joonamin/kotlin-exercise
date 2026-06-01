package lotto.domain.entity

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import lotto.domain.vo.LottoNumber
import lotto.domain.vo.LottoNumbers
import lotto.domain.vo.LottoResult
import lotto.domain.vo.WinningNumbers
import lotto.domain.vo.enums.Rank
import lotto.domain.vo.enums.RoundStatus

class LottoRoundTest : BehaviorSpec({
    fun lottoNumbers(vararg nums: Int) = LottoNumbers(nums.map { LottoNumber(it) })

    Given("OPEN 상태의 로또 회차가 주어졌을 때") {
        When("티켓을 추가하면") {
            Then("pickedNumbers에 정상적으로 추가된다") {
                val round = LottoRound(1, mutableListOf())
                val ticket = lottoNumbers(1, 2, 3, 4, 5, 6)
                round.addTicket(ticket)
                round.pickedNumbers shouldHaveSize 1
                round.pickedNumbers[0] shouldBe ticket
            }
        }

        When("여러 티켓을 추가하면") {
            Then("모든 티켓이 추가된다") {
                val round = LottoRound(1, mutableListOf())
                round.addTicket(lottoNumbers(1, 2, 3, 4, 5, 6))
                round.addTicket(lottoNumbers(7, 8, 9, 10, 11, 12))
                round.pickedNumbers shouldHaveSize 2
            }
        }
    }

    Given("FINISHED 상태의 로또 회차가 주어졌을 때") {
        When("티켓을 추가하려고 하면") {
            Then("IllegalStateException이 발생한다") {
                val round = LottoRound(1, mutableListOf(), RoundStatus.FINISHED)
                shouldThrow<IllegalStateException> {
                    round.addTicket(lottoNumbers(1, 2, 3, 4, 5, 6))
                }
            }
        }
    }

    Given("구매한 티켓이 있는 OPEN 상태의 회차에서") {
        When("당첨 번호로 추첨하면") {
            Then("상태가 FINISHED로 변경된다") {
                val round = LottoRound(1, mutableListOf(lottoNumbers(1, 2, 3, 4, 5, 6)))
                val winningNumbers = WinningNumbers(lottoNumbers(1, 2, 3, 4, 5, 6), LottoNumber(7))
                round.draw(winningNumbers)
                round.status shouldBe RoundStatus.FINISHED
            }

            Then("winningNumbers가 설정된다") {
                val round = LottoRound(1, mutableListOf(lottoNumbers(1, 2, 3, 4, 5, 6)))
                val winningNumbers = WinningNumbers(lottoNumbers(1, 2, 3, 4, 5, 6), LottoNumber(7))
                round.draw(winningNumbers)
                round.winningNumbers shouldBe winningNumbers
            }

            Then("LottoResult가 반환된다") {
                val round = LottoRound(1, mutableListOf(lottoNumbers(1, 2, 3, 4, 5, 6)))
                val winningNumbers = WinningNumbers(lottoNumbers(1, 2, 3, 4, 5, 6), LottoNumber(7))
                val result = round.draw(winningNumbers)
                result shouldNotBe null
                result.results shouldHaveSize 1
            }

            Then("result가 설정된다") {
                val round = LottoRound(1, mutableListOf(lottoNumbers(1, 2, 3, 4, 5, 6)))
                val winningNumbers = WinningNumbers(lottoNumbers(1, 2, 3, 4, 5, 6), LottoNumber(7))
                round.draw(winningNumbers)
                round.result shouldNotBe null
            }
        }

        When("6개 모두 일치하는 티켓으로 추첨하면") {
            Then("해당 티켓은 FIRST 등수를 받는다") {
                val round = LottoRound(1, mutableListOf(lottoNumbers(1, 2, 3, 4, 5, 6)))
                val winningNumbers = WinningNumbers(lottoNumbers(1, 2, 3, 4, 5, 6), LottoNumber(7))
                val result = round.draw(winningNumbers)
                result.results[0].rank shouldBe Rank.FIRST
            }
        }

        When("여러 티켓이 있는 상태에서 추첨하면") {
            Then("각 티켓에 대해 등수가 계산된다") {
                val round = LottoRound(
                    1,
                    mutableListOf(
                        lottoNumbers(1, 2, 3, 4, 5, 6),   // 6개 일치 → FIRST
                        lottoNumbers(1, 2, 3, 4, 5, 7),   // 5개 + 보너스 → SECOND
                        lottoNumbers(10, 20, 30, 40, 41, 42), // 0개 일치 → MISS
                    ),
                )
                val winningNumbers = WinningNumbers(lottoNumbers(1, 2, 3, 4, 5, 6), LottoNumber(7))
                val result = round.draw(winningNumbers)
                result.results shouldHaveSize 3
                result.results[0].rank shouldBe Rank.FIRST
                result.results[1].rank shouldBe Rank.SECOND
                result.results[2].rank shouldBe Rank.MISS
            }
        }
    }

    Given("이미 추첨이 완료된(FINISHED) 회차에서") {
        When("다시 추첨하려고 하면") {
            Then("IllegalStateException이 발생한다") {
                val round = LottoRound(1, mutableListOf(lottoNumbers(1, 2, 3, 4, 5, 6)))
                val winningNumbers = WinningNumbers(lottoNumbers(1, 2, 3, 4, 5, 6), LottoNumber(7))
                round.draw(winningNumbers)

                shouldThrow<IllegalStateException> {
                    round.draw(winningNumbers)
                }
            }
        }
    }

    Given("구매한 티켓이 없는 OPEN 상태의 회차에서") {
        When("추첨하려고 하면") {
            Then("IllegalStateException이 발생한다") {
                val round = LottoRound(1, mutableListOf())
                val winningNumbers = WinningNumbers(lottoNumbers(1, 2, 3, 4, 5, 6), LottoNumber(7))
                shouldThrow<IllegalStateException> {
                    round.draw(winningNumbers)
                }
            }
        }
    }

    Given("restoreState를 호출할 때") {
        When("winningNumbers와 result를 복원하면") {
            Then("해당 값들이 정상적으로 설정된다") {
                val round = LottoRound(1, mutableListOf(lottoNumbers(1, 2, 3, 4, 5, 6)), RoundStatus.FINISHED)
                val winningNumbers = WinningNumbers(lottoNumbers(1, 2, 3, 4, 5, 6), LottoNumber(7))
                val result = LottoResult(
                    listOf(TicketResult(lottoNumbers(1, 2, 3, 4, 5, 6), Rank.FIRST)),
                )
                round.restoreState(winningNumbers, result)
                round.winningNumbers shouldBe winningNumbers
                round.result shouldNotBe null
                round.result!!.results shouldHaveSize 1
            }
        }

        When("null 값으로 복원하면") {
            Then("null 상태로 설정된다") {
                val round = LottoRound(1, mutableListOf())
                round.restoreState(null, null)
                round.winningNumbers shouldBe null
                round.result shouldBe null
            }
        }
    }

    Given("summary를 호출할 때") {
        When("OPEN 상태의 회차에서 호출하면") {
            Then("회차 번호와 상태가 포함된 문자열을 반환한다") {
                val round = LottoRound(1, mutableListOf())
                val summary = round.summary()
                summary shouldContain "1"
                summary shouldContain "OPEN"
            }
        }
    }

    Given("pickedNumbers의 방어적 복사를 검증할 때") {
        When("pickedNumbers를 조회하면") {
            Then("내부 리스트와 다른 인스턴스를 반환한다") {
                val internalList = mutableListOf(lottoNumbers(1, 2, 3, 4, 5, 6))
                val round = LottoRound(1, internalList)
                round.pickedNumbers shouldNotBeSameInstanceAs internalList
            }
        }
    }

    Given("roundNumber를 확인할 때") {
        When("생성시 전달된 번호를 조회하면") {
            Then("동일한 값을 반환한다") {
                val round = LottoRound(42, mutableListOf())
                round.roundNumber shouldBe 42
            }
        }
    }
})
