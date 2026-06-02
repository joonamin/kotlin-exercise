package lotto.domain.vo

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.shouldBe

class LottoNumberTest : BehaviorSpec({
    Given("유효한 범위(${LottoNumber.MIN_NUMBER}~${LottoNumber.MAX_NUMBER}) 내의 숫자가 주어졌을 때") {
        data class ValidNumberTestCase(val description: String, val value: Int)

        listOf(
            ValidNumberTestCase("최솟값 (${LottoNumber.MIN_NUMBER})", LottoNumber.MIN_NUMBER),
            ValidNumberTestCase("최댓값 (${LottoNumber.MAX_NUMBER})", LottoNumber.MAX_NUMBER),
            ValidNumberTestCase("중간값 (23)", 23),
        ).forEach { testCase ->
            When("${testCase.description}로 LottoNumber를 생성하면") {
                Then("정상 생성된다") {
                    val lottoNumber = LottoNumber(testCase.value)
                    lottoNumber.number shouldBe testCase.value
                }
            }
        }
    }

    Given("유효 범위를 벗어난 숫자가 주어졌을 때") {
        data class InvalidNumberTestCase(val description: String, val value: Int)

        listOf(
            InvalidNumberTestCase("최솟값 미만 (${LottoNumber.MIN_NUMBER - 1})", LottoNumber.MIN_NUMBER - 1),
            InvalidNumberTestCase("음수 (-1)", -1),
            InvalidNumberTestCase("최댓값 초과 (${LottoNumber.MAX_NUMBER + 1})", LottoNumber.MAX_NUMBER + 1),
        ).forEach { testCase ->
            When("${testCase.description}으로 LottoNumber를 생성하면") {
                Then("IllegalArgumentException이 발생한다") {
                    shouldThrow<IllegalArgumentException> {
                        LottoNumber(testCase.value)
                    }
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
