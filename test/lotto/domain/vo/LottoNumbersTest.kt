package lotto.domain.vo

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class LottoNumbersTest : BehaviorSpec({
    Given("중복 없는 6개의 유효한 로또 번호가 주어졌을 때") {
        val numbers = listOf(1, 2, 3, 4, 5, 6).map { LottoNumber(it) }

        When("LottoNumbers를 생성하면") {
            val lottoNumbers = LottoNumbers(numbers)

            Then("정상적으로 생성된다") {
                lottoNumbers shouldNotBe null
            }

            Then("정렬된 SortedSet으로 저장된다") {
                lottoNumbers.numbers shouldHaveSize 6
                lottoNumbers.numbers.first() shouldBe LottoNumber(1)
                lottoNumbers.numbers.last() shouldBe LottoNumber(6)
            }
        }
    }

    Given("중복이 포함된 번호 리스트가 주어졌을 때") {
        When("LottoNumbers를 생성하면") {
            Then("중복 제거 후 6개 미만이므로 IllegalArgumentException이 발생한다") {
                val duplicateNumbers = listOf(1, 1, 2, 3, 4, 5).map { LottoNumber(it) }
                shouldThrow<IllegalArgumentException> {
                    LottoNumbers(duplicateNumbers)
                }
            }
        }
    }

    Given("6개가 아닌 번호 리스트가 주어졌을 때") {
        When("5개로 생성하면") {
            Then("IllegalArgumentException이 발생한다") {
                val fiveNumbers = listOf(1, 2, 3, 4, 5).map { LottoNumber(it) }
                shouldThrow<IllegalArgumentException> {
                    LottoNumbers(fiveNumbers)
                }
            }
        }

        When("7개로 생성하면") {
            Then("IllegalArgumentException이 발생한다") {
                val sevenNumbers = listOf(1, 2, 3, 4, 5, 6, 7).map { LottoNumber(it) }
                shouldThrow<IllegalArgumentException> {
                    LottoNumbers(sevenNumbers)
                }
            }
        }
    }

    Given("LottoNumbers가 생성되어 있을 때") {
        val lottoNumbers = LottoNumbers(listOf(1, 2, 3, 4, 5, 6).map { LottoNumber(it) })

        When("포함된 번호로 contains를 호출하면") {
            Then("true를 반환한다") {
                lottoNumbers.contains(LottoNumber(3)) shouldBe true
            }
        }

        When("포함되지 않은 번호로 contains를 호출하면") {
            Then("false를 반환한다") {
                lottoNumbers.contains(LottoNumber(7)) shouldBe false
            }
        }
    }

    Given("두 LottoNumbers가 주어졌을 때") {
        When("3개의 번호가 일치하면") {
            Then("countMatchedNumbers는 3을 반환한다") {
                val numbers1 = LottoNumbers(listOf(1, 2, 3, 4, 5, 6).map { LottoNumber(it) })
                val numbers2 = LottoNumbers(listOf(1, 2, 3, 10, 11, 12).map { LottoNumber(it) })
                numbers1.countMatchedNumbers(numbers2) shouldBe 3
            }
        }

        When("모든 번호가 일치하면") {
            Then("countMatchedNumbers는 6을 반환한다") {
                val numbers1 = LottoNumbers(listOf(1, 2, 3, 4, 5, 6).map { LottoNumber(it) })
                val numbers2 = LottoNumbers(listOf(1, 2, 3, 4, 5, 6).map { LottoNumber(it) })
                numbers1.countMatchedNumbers(numbers2) shouldBe 6
            }
        }

        When("일치하는 번호가 없으면") {
            Then("countMatchedNumbers는 0을 반환한다") {
                val numbers1 = LottoNumbers(listOf(1, 2, 3, 4, 5, 6).map { LottoNumber(it) })
                val numbers2 = LottoNumbers(listOf(7, 8, 9, 10, 11, 12).map { LottoNumber(it) })
                numbers1.countMatchedNumbers(numbers2) shouldBe 0
            }
        }
    }

    Given("CSV 직렬화/역직렬화를 검증할 때") {
        val lottoNumbers = LottoNumbers(listOf(1, 2, 3, 4, 5, 6).map { LottoNumber(it) })

        When("toCsvString으로 변환하면") {
            val csvString = lottoNumbers.toCsvString()

            Then("콜론으로 구분된 문자열이 반환된다") {
                csvString shouldBe "1:2:3:4:5:6"
            }
        }

        When("fromCsvString으로 복원하면") {
            Then("원래 LottoNumbers와 동일하다") {
                val restored = LottoNumbers.fromCsvString("1:2:3:4:5:6")
                restored shouldBe lottoNumbers
            }
        }
    }

    Given("manual 팩토리 메서드를 사용할 때") {
        When("유효한 번호 리스트로 생성하면") {
            Then("정상적으로 생성된다") {
                val numbers = listOf(10, 20, 30, 31, 32, 33).map { LottoNumber(it) }
                val lottoNumbers = LottoNumbers.manual(numbers)
                lottoNumbers.numbers shouldHaveSize 6
            }
        }
    }

    Given("auto 팩토리 메서드를 사용할 때") {
        val fixedStrategy = LottoGenerationStrategy { count, _ ->
            (1..count).map { LottoNumber(it) }
        }

        When("전략을 통해 생성하면") {
            Then("6개의 번호가 자동 생성된다") {
                val lottoNumbers = LottoNumbers.auto(fixedStrategy)
                lottoNumbers.numbers shouldHaveSize 6
            }
        }
    }

    Given("halfAuto 팩토리 메서드를 사용할 때") {
        val fixedStrategy = LottoGenerationStrategy { count, _ ->
            (40..40 + count - 1).map { LottoNumber(it) }
        }

        When("수동 3개 + 자동 3개로 생성하면") {
            Then("총 6개의 번호가 생성된다") {
                val manualNumbers = listOf(1, 2, 3).map { LottoNumber(it) }
                val lottoNumbers = LottoNumbers.halfAuto(manualNumbers, fixedStrategy)
                lottoNumbers.numbers shouldHaveSize 6
                lottoNumbers.numbers shouldContainAll listOf(LottoNumber(1), LottoNumber(2), LottoNumber(3))
            }
        }

        When("수동 번호가 6개 이상이면") {
            Then("IllegalArgumentException이 발생한다") {
                val tooMany = listOf(1, 2, 3, 4, 5, 6).map { LottoNumber(it) }
                shouldThrow<IllegalArgumentException> {
                    LottoNumbers.halfAuto(tooMany, fixedStrategy)
                }
            }
        }
    }

    Given("TOTAL_COUNT 상수를 확인할 때") {
        When("값을 조회하면") {
            Then("6이다") {
                LottoNumbers.TOTAL_COUNT shouldBe 6
            }
        }
    }
})
