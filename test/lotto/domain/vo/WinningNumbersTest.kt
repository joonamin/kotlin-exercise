package lotto.domain.vo

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import lotto.domain.vo.enums.Rank

class WinningNumbersTest : BehaviorSpec({
    // 테스트 전반에서 사용되는 헬퍼 함수
    fun lottoNumbers(vararg nums: Int) = LottoNumbers(nums.map { LottoNumber(it) })

    Given("유효한 당첨 번호와 보너스 번호가 주어졌을 때") {
        When("WinningNumbers를 생성하면") {
            Then("정상적으로 생성된다") {
                val winning = WinningNumbers(lottoNumbers(1, 2, 3, 4, 5, 6), LottoNumber(7))
                winning.lottoNumbers shouldBe lottoNumbers(1, 2, 3, 4, 5, 6)
                winning.bonusNumber shouldBe LottoNumber(7)
            }
        }
    }

    Given("보너스 번호가 당첨 번호와 중복될 때") {
        When("WinningNumbers를 생성하면") {
            Then("IllegalArgumentException이 발생한다") {
                shouldThrow<IllegalArgumentException> {
                    WinningNumbers(lottoNumbers(1, 2, 3, 4, 5, 6), LottoNumber(3))
                }
            }
        }
    }

    Given("6개 모두 일치하는 사용자 번호가 주어졌을 때") {
        When("match를 호출하면") {
            Then("1등(FIRST)을 반환한다") {
                val winning = WinningNumbers(lottoNumbers(1, 2, 3, 4, 5, 6), LottoNumber(7))
                val userNumbers = lottoNumbers(1, 2, 3, 4, 5, 6)
                winning.match(userNumbers) shouldBe Rank.FIRST
            }
        }
    }

    Given("5개 일치하고 보너스 번호도 일치하는 사용자 번호가 주어졌을 때") {
        When("match를 호출하면") {
            Then("2등(SECOND)을 반환한다") {
                val winning = WinningNumbers(lottoNumbers(1, 2, 3, 4, 5, 6), LottoNumber(7))
                val userNumbers = lottoNumbers(1, 2, 3, 4, 5, 7)
                winning.match(userNumbers) shouldBe Rank.SECOND
            }
        }
    }

    Given("5개 일치하고 보너스 번호는 불일치하는 사용자 번호가 주어졌을 때") {
        When("match를 호출하면") {
            Then("3등(THIRD)을 반환한다") {
                val winning = WinningNumbers(lottoNumbers(1, 2, 3, 4, 5, 6), LottoNumber(7))
                val userNumbers = lottoNumbers(1, 2, 3, 4, 5, 8)
                winning.match(userNumbers) shouldBe Rank.THIRD
            }
        }
    }

    Given("4개 일치하는 사용자 번호가 주어졌을 때") {
        When("match를 호출하면") {
            Then("4등(FOURTH)을 반환한다") {
                val winning = WinningNumbers(lottoNumbers(1, 2, 3, 4, 5, 6), LottoNumber(7))
                val userNumbers = lottoNumbers(1, 2, 3, 4, 8, 9)
                winning.match(userNumbers) shouldBe Rank.FOURTH
            }
        }
    }

    Given("3개 일치하는 사용자 번호가 주어졌을 때") {
        When("match를 호출하면") {
            Then("5등(FIFTH)을 반환한다") {
                val winning = WinningNumbers(lottoNumbers(1, 2, 3, 4, 5, 6), LottoNumber(7))
                val userNumbers = lottoNumbers(1, 2, 3, 8, 9, 10)
                winning.match(userNumbers) shouldBe Rank.FIFTH
            }
        }
    }

    Given("2개 이하 일치하는 사용자 번호가 주어졌을 때") {
        val winning = WinningNumbers(lottoNumbers(1, 2, 3, 4, 5, 6), LottoNumber(7))

        When("2개 일치하면") {
            Then("꽝(MISS)을 반환한다") {
                val userNumbers = lottoNumbers(1, 2, 8, 9, 10, 11)
                winning.match(userNumbers) shouldBe Rank.MISS
            }
        }

        When("1개 일치하면") {
            Then("꽝(MISS)을 반환한다") {
                val userNumbers = lottoNumbers(1, 8, 9, 10, 11, 12)
                winning.match(userNumbers) shouldBe Rank.MISS
            }
        }

        When("0개 일치하면") {
            Then("꽝(MISS)을 반환한다") {
                val userNumbers = lottoNumbers(7, 8, 9, 10, 11, 12)
                winning.match(userNumbers) shouldBe Rank.MISS
            }
        }
    }

    Given("toCsv를 검증할 때") {
        When("WinningNumbers를 CSV로 변환하면") {
            Then("당첨 번호와 보너스 번호가 포함된 문자열을 반환한다") {
                val winning = WinningNumbers(lottoNumbers(1, 2, 3, 4, 5, 6), LottoNumber(7))
                val csv = winning.toCsv()
                csv shouldContain "1:2:3:4:5:6"
                csv shouldContain "7"
            }
        }
    }

    Given("draw 팩토리 메서드를 사용할 때") {
        // 결정적 전략: 항상 1~6번, 보너스 7번
        val deterministicStrategy = LottoGenerationStrategy { count, exclude ->
            val excludeSet = exclude.map { it.number }.toSet()
            (1..45).filter { it !in excludeSet }.take(count).map { LottoNumber(it) }
        }

        When("전략을 통해 당첨 번호를 생성하면") {
            Then("유효한 WinningNumbers가 생성된다") {
                val winning = WinningNumbers.draw(deterministicStrategy)
                winning shouldNotBe null
                winning.lottoNumbers.numbers.size shouldBe 6
                // 보너스 번호가 당첨 번호에 포함되지 않는지 검증
                winning.lottoNumbers.contains(winning.bonusNumber) shouldBe false
            }
        }
    }
})
