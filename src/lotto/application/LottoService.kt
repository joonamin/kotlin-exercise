package lotto.application

import lotto.domain.entity.LottoRound
import lotto.domain.vo.LottoGenerationStrategy
import lotto.domain.vo.LottoNumbers
import lotto.domain.vo.LottoResult
import lotto.domain.vo.WinningNumbers
import lotto.domain.vo.enums.RoundStatus
import lotto.infrastructure.repository.LottoRepository

// 애플리케이션 서비스는 오케스트레이션 역할만 수행 (사용자 유즈케이스를 담당)
class LottoService(
    private val repository: LottoRepository,
    private val strategy: LottoGenerationStrategy,
) {
    fun purchaseLotto(
        roundNumber: Int,
        ticketCount: Int,
        manualNumbersList: List<LottoNumbers>,
    ): LottoRound {
        require(ticketCount > 0) {
            "구매 수량은 1개 이상이어야 합니다."
        }
        require(manualNumbersList.size <= ticketCount) {
            "수동 구매 개수는 총 구매 개수를 초과할 수 없습니다."
        }

        // 금액의 검증은 payment context에게 위임하자!

        val round =
            repository.findByRoundNumber(roundNumber)
                ?: LottoRound(roundNumber, mutableListOf(), RoundStatus.OPEN)

        // 수동 번호 추가
        manualNumbersList.forEach { manualNumbers ->
            round.addTicket(manualNumbers)
        }

        // 자동 번호 추가
        val autoCount = ticketCount - manualNumbersList.size
        repeat(autoCount) {
            val lottoNumbers = LottoNumbers.auto(strategy)
            round.addTicket(lottoNumbers)
        }

        repository.save(round)
        return round
    }

    fun drawLotto(
        roundNumber: Int,
        winningNumbers: WinningNumbers,
    ): LottoResult {
        val round =
            repository.findByRoundNumber(roundNumber)
                ?: throw IllegalStateException("${roundNumber}회차 정보가 없습니다. 먼저 로또를 구매해 주세요.")

        val result = round.draw(winningNumbers)

        repository.save(round)
        return result
    }

    fun findRound(roundNumber: Int): LottoRound? = repository.findByRoundNumber(roundNumber)
}
