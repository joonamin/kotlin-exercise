package lotto.infrastructure.repository

import LottoRound

// data access layer
interface LottoRepository {
    fun save(round: LottoRound)

    fun findByRoundNumber(roundNumber: Int): LottoRound?
}
