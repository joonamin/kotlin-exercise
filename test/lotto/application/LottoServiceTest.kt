package lotto.application

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import lotto.application.dto.PurchaseLottoCommand
import lotto.domain.entity.LottoRound
import lotto.domain.vo.LottoGenerationStrategy
import lotto.domain.vo.LottoNumber
import lotto.domain.vo.LottoNumbers
import lotto.domain.vo.WinningNumbers
import lotto.domain.vo.enums.Rank
import lotto.domain.vo.enums.RoundStatus
import lotto.infrastructure.repository.LottoRepository

class LottoServiceTest : BehaviorSpec({
    fun lottoNumbers(vararg nums: Int) = LottoNumbers(nums.map { LottoNumber(it) })

    data class TestDependencies(
        val repository: FakeLottoRepository,
        val strategy: LottoGenerationStrategy,
        val service: LottoService
    )

    // 테스트마다 독립적인 Fake Repository와 Service를 생성하는 팩토리
    fun createTestDependencies(): TestDependencies {
        val repository = FakeLottoRepository()
        // 결정적 전략: exclude 목록을 존중하며 순차적 번호 생성
        val strategy = LottoGenerationStrategy { count, exclude ->
            val excludeSet = exclude.map { it.number }.toSet()
            (1..45).filter { it !in excludeSet }.take(count).map { LottoNumber(it) }
        }
        val service = LottoService(repository, strategy)
        return TestDependencies(repository, strategy, service)
    }

    Given("신규 회차에 로또를 구매할 때") {
        When("자동으로만 3장을 구매하면") {
            Then("3장의 티켓이 생성되고 저장된다") {
                val (repository, _, service) = createTestDependencies()
                val command = PurchaseLottoCommand.of(
                    roundNumber = 1,
                    ticketCount = 3,
                    manualNumbersList = emptyList(),
                )
                val round = service.purchaseLotto(command)
                round.pickedNumbers shouldHaveSize 3
                round.roundNumber shouldBe 1
                round.status shouldBe RoundStatus.OPEN
                repository.findByRoundNumber(1) shouldNotBe null
            }
        }

        When("수동 1장 + 자동 2장으로 구매하면") {
            Then("수동 번호가 포함된 3장의 티켓이 생성된다") {
                val (_, _, service) = createTestDependencies()
                val manualNumbers = lottoNumbers(10, 20, 30, 31, 32, 33)
                val command = PurchaseLottoCommand.of(
                    roundNumber = 1,
                    ticketCount = 3,
                    manualNumbersList = listOf(manualNumbers),
                )
                val round = service.purchaseLotto(command)
                round.pickedNumbers shouldHaveSize 3
                round.pickedNumbers[0] shouldBe manualNumbers
            }
        }
    }

    Given("기존 회차에 추가 구매할 때") {
        When("이미 구매한 회차에 추가로 구매하면") {
            Then("기존 티켓에 추가된다") {
                val (_, _, service) = createTestDependencies()
                val command1 = PurchaseLottoCommand.of(1, 2, emptyList())
                service.purchaseLotto(command1)

                val command2 = PurchaseLottoCommand.of(1, 1, emptyList())
                val round = service.purchaseLotto(command2)
                round.pickedNumbers shouldHaveSize 3
            }
        }
    }

    Given("로또를 추첨할 때") {
        When("구매 이력이 있는 회차를 추첨하면") {
            Then("LottoResult가 반환된다") {
                val (_, _, service) = createTestDependencies()
                val command = PurchaseLottoCommand.of(1, 1, emptyList())
                service.purchaseLotto(command)

                val winningNumbers = WinningNumbers(lottoNumbers(1, 2, 3, 4, 5, 6), LottoNumber(7))
                val result = service.drawLotto(1, winningNumbers)
                result shouldNotBe null
                result.results shouldHaveSize 1
            }
        }
    }

    Given("존재하지 않는 회차를 추첨할 때") {
        When("drawLotto를 호출하면") {
            Then("IllegalStateException이 발생한다") {
                val (_, _, service) = createTestDependencies()
                val winningNumbers = WinningNumbers(lottoNumbers(1, 2, 3, 4, 5, 6), LottoNumber(7))
                shouldThrow<IllegalStateException> {
                    service.drawLotto(999, winningNumbers)
                }
            }
        }
    }

    Given("회차를 조회할 때") {
        When("존재하는 회차를 조회하면") {
            Then("LottoRound를 반환한다") {
                val (_, _, service) = createTestDependencies()
                val command = PurchaseLottoCommand.of(1, 1, emptyList())
                service.purchaseLotto(command)
                service.findRound(1) shouldNotBe null
            }
        }

        When("존재하지 않는 회차를 조회하면") {
            Then("null을 반환한다") {
                val (_, _, service) = createTestDependencies()
                service.findRound(999) shouldBe null
            }
        }
    }
})

/**
 * 테스트용 인메모리 Fake Repository
 */
private class FakeLottoRepository : LottoRepository {
    private val store = mutableMapOf<Int, LottoRound>()

    override fun save(round: LottoRound) {
        store[round.roundNumber] = round
    }

    override fun findByRoundNumber(roundNumber: Int): LottoRound? = store[roundNumber]
}
