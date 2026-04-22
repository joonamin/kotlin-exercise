package lotto.infrastructure.repository

import lotto.domain.entity.LottoRound

// data access layer
interface LottoRepository {
    fun save(round: LottoRound)

    fun findByRoundNumber(roundNumber: Int): LottoRound?
}
