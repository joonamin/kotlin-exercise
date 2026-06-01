package lotto.infrastructure.repository

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import lotto.domain.vo.LottoNumber
import lotto.domain.vo.LottoNumbers
import lotto.domain.vo.WinningNumbers
import lotto.domain.vo.enums.Rank
import lotto.domain.vo.enums.RoundStatus
import java.io.File

class CsvLottoRepositoryTest : BehaviorSpec({
    fun lottoNumbers(vararg nums: Int) = LottoNumbers(nums.map { LottoNumber(it) })

    // 각 테스트에서 임시 디렉토리를 생성하여 격리된 환경을 보장
    fun withTempDir(block: (String) -> Unit) {
        val tempDir = kotlin.io.path.createTempDirectory("csv-lotto-test").toFile()
        try {
            block(tempDir.absolutePath)
        } finally {
            tempDir.deleteRecursively()
        }
    }

    Given("미추첨 상태의 로또 회차를 저장할 때") {
        When("save 후 findByRoundNumber로 조회하면") {
            Then("동일한 회차 정보가 복원된다") {
                withTempDir { dir ->
                    val repository = CsvLottoRepository(dir)
                    val round = lotto.domain.entity.LottoRound(
                        1,
                        mutableListOf(
                            lottoNumbers(1, 2, 3, 4, 5, 6),
                            lottoNumbers(7, 8, 9, 10, 11, 12),
                        ),
                    )
                    repository.save(round)

                    val loaded = repository.findByRoundNumber(1)
                    loaded shouldNotBe null
                    loaded!!.roundNumber shouldBe 1
                    loaded.status shouldBe RoundStatus.OPEN
                    loaded.pickedNumbers shouldHaveSize 2
                    loaded.winningNumbers shouldBe null
                    loaded.result shouldBe null
                }
            }
        }
    }

    Given("추첨 완료된 로또 회차를 저장할 때") {
        When("save 후 findByRoundNumber로 조회하면") {
            Then("당첨 번호와 결과가 함께 복원된다") {
                withTempDir { dir ->
                    val repository = CsvLottoRepository(dir)
                    val round = lotto.domain.entity.LottoRound(
                        1,
                        mutableListOf(
                            lottoNumbers(1, 2, 3, 4, 5, 6),
                            lottoNumbers(7, 8, 9, 10, 11, 12),
                        ),
                    )
                    val winningNumbers = WinningNumbers(lottoNumbers(1, 2, 3, 4, 5, 6), LottoNumber(7))
                    round.draw(winningNumbers)

                    repository.save(round)

                    val loaded = repository.findByRoundNumber(1)
                    loaded shouldNotBe null
                    loaded!!.status shouldBe RoundStatus.FINISHED
                    loaded.winningNumbers shouldNotBe null
                    loaded.winningNumbers!!.lottoNumbers shouldBe lottoNumbers(1, 2, 3, 4, 5, 6)
                    loaded.winningNumbers!!.bonusNumber shouldBe LottoNumber(7)
                    loaded.result shouldNotBe null
                    loaded.result!!.results shouldHaveSize 2
                    loaded.result!!.results[0].rank shouldBe Rank.FIRST
                    loaded.result!!.results[1].rank shouldBe Rank.MISS
                }
            }
        }
    }

    Given("존재하지 않는 회차를 조회할 때") {
        When("findByRoundNumber를 호출하면") {
            Then("null을 반환한다") {
                withTempDir { dir ->
                    val repository = CsvLottoRepository(dir)
                    repository.findByRoundNumber(999) shouldBe null
                }
            }
        }
    }

    Given("여러 회차를 저장할 때") {
        When("각각 다른 회차 번호로 저장하면") {
            Then("각 회차를 독립적으로 조회할 수 있다") {
                withTempDir { dir ->
                    val repository = CsvLottoRepository(dir)

                    val round1 = lotto.domain.entity.LottoRound(
                        1,
                        mutableListOf(lottoNumbers(1, 2, 3, 4, 5, 6)),
                    )
                    val round2 = lotto.domain.entity.LottoRound(
                        2,
                        mutableListOf(lottoNumbers(7, 8, 9, 10, 11, 12)),
                    )

                    repository.save(round1)
                    repository.save(round2)

                    repository.findByRoundNumber(1) shouldNotBe null
                    repository.findByRoundNumber(2) shouldNotBe null
                    repository.findByRoundNumber(1)!!.roundNumber shouldBe 1
                    repository.findByRoundNumber(2)!!.roundNumber shouldBe 2
                }
            }
        }
    }

    Given("같은 회차를 덮어쓸 때") {
        When("동일 회차를 두 번 save하면") {
            Then("마지막 저장 상태가 복원된다") {
                withTempDir { dir ->
                    val repository = CsvLottoRepository(dir)

                    val round = lotto.domain.entity.LottoRound(
                        1,
                        mutableListOf(lottoNumbers(1, 2, 3, 4, 5, 6)),
                    )
                    repository.save(round)

                    round.addTicket(lottoNumbers(7, 8, 9, 10, 11, 12))
                    repository.save(round)

                    val loaded = repository.findByRoundNumber(1)
                    loaded!!.pickedNumbers shouldHaveSize 2
                }
            }
        }
    }

    Given("잘못된 형식의 파일이 존재할 때") {
        When("파일이 완전히 비어있으면") {
            Then("IllegalStateException이 발생한다") {
                withTempDir { dir ->
                    val repository = CsvLottoRepository(dir)
                    File(dir, "lottos_1.csv").writeText("")
                    io.kotest.assertions.throwables.shouldThrow<IllegalStateException> {
                        repository.findByRoundNumber(1)
                    }
                }
            }
        }

        When("당첨 번호만 있고 보너스 번호가 없으면") {
            Then("winningNumbers는 null로 복원된다") {
                withTempDir { dir ->
                    val repository = CsvLottoRepository(dir)
                    File(dir, "lottos_1.csv").writeText(
                        "1,OPEN,1:2:3:4:5:6,\n" +
                        "numbers,rank\n" +
                        "1:2:3:4:5:6,\n"
                    )
                    val loaded = repository.findByRoundNumber(1)
                    loaded!!.winningNumbers shouldBe null
                }
            }
        }

        When("당첨 번호가 비어있고 보너스 번호만 있으면") {
            Then("winningNumbers는 null로 복원된다") {
                withTempDir { dir ->
                    val repository = CsvLottoRepository(dir)
                    File(dir, "lottos_1.csv").writeText(
                        "1,OPEN,,7\n" +
                        "numbers,rank\n" +
                        "1:2:3:4:5:6,\n"
                    )
                    val loaded = repository.findByRoundNumber(1)
                    loaded!!.winningNumbers shouldBe null
                }
            }
        }

        When("랭크 정보가 공백으로만 이루어져 있으면") {
            Then("결과가 없는 것으로 처리된다") {
                withTempDir { dir ->
                    val repository = CsvLottoRepository(dir)
                    File(dir, "lottos_1.csv").writeText(
                        "1,OPEN,, \n" +
                        "numbers,rank\n" +
                        "1:2:3:4:5:6,   \n"
                    )
                    val loaded = repository.findByRoundNumber(1)
                    loaded!!.result shouldBe null
                }
            }
        }
    }
})
