package lotto.domain.vo

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.shouldBe

class LottoNumberTest : BehaviorSpec({
    Given("유효한 범위(${LottoNumber.MIN_NUMBER}~${LottoNumber.MAX_NUMBER}) 내의 숫자가 주어졌을 때") {
        When("LottoNumber를 생성하면") {
            Then("최솟값 ${LottoNumber.MIN_NUMBER}로 정상 생성된다") {
                val lottoNumber = LottoNumber(LottoNumber.MIN_NUMBER)
                lottoNumber.number shouldBe LottoNumber.MIN_NUMBER
            }

            Then("최댓값 ${LottoNumber.MAX_NUMBER}로 정상 생성된다") {
                val lottoNumber = LottoNumber(LottoNumber.MAX_NUMBER)
                lottoNumber.number shouldBe LottoNumber.MAX_NUMBER
            }

            Then("중간값으로 정상 생성된다") {
                val lottoNumber = LottoNumber(23)
                lottoNumber.number shouldBe 23
            }
        }
    }

    Given("유효 범위를 벗어난 숫자가 주어졌을 때") {
        When("${LottoNumber.MIN_NUMBER - 1}으로 LottoNumber를 생성하면") {
            Then("IllegalArgumentException이 발생한다") {
                shouldThrow<IllegalArgumentException> {
                    LottoNumber(LottoNumber.MIN_NUMBER - 1)
                }
            }
        }

        When("음수로 LottoNumber를 생성하면") {
            Then("IllegalArgumentException이 발생한다") {
                shouldThrow<IllegalArgumentException> {
                    LottoNumber(-1)
                }
            }
        }

        When("${LottoNumber.MAX_NUMBER + 1}으로 LottoNumber를 생성하면") {
            Then("IllegalArgumentException이 발생한다") {
                shouldThrow<IllegalArgumentException> {
                    LottoNumber(LottoNumber.MAX_NUMBER + 1)
                }
            }
        }
    }

    Given("두 개의 LottoNumber가 주어졌을 때") {
        When("compareTo로 비교하면") {
            Then("작은 번호가 큰 번호보다 앞선다") {
                val small = LottoNumber(LottoNumber.MIN_NUMBER)
                val large = LottoNumber(LottoNumber.MAX_NUMBER)
                small shouldBeLessThan large
            }

            Then("큰 번호가 작은 번호보다 뒤에 온다") {
                val small = LottoNumber(LottoNumber.MIN_NUMBER)
                val large = LottoNumber(LottoNumber.MAX_NUMBER)
                large shouldBeGreaterThan small
            }

            Then("같은 번호는 동등하다") {
                val a = LottoNumber(10)
                val b = LottoNumber(10)
                a.compareTo(b) shouldBe 0
            }
        }
    }

    Given("LottoNumber의 companion object가 주어졌을 때") {
        When("상수 값을 확인하면") {
            Then("MIN_NUMBER은 1이다") {
                LottoNumber.MIN_NUMBER shouldBe 1
            }

            Then("MAX_NUMBER은 45이다") {
                LottoNumber.MAX_NUMBER shouldBe 45
            }
        }
    }
})
