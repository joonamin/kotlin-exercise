package lotto.infrastructure.strategy

import lotto.domain.vo.LottoGenerationStrategy
import lotto.domain.vo.LottoNumber

class RandomLottoGenerationStrategy : LottoGenerationStrategy {
    override fun generate(
        count: Int,
        exclude: List<LottoNumber>,
    ): List<LottoNumber> {
        val excludeNumbers = exclude.map { it.number }.toSet()
        val candidates =
            (LottoNumber.MIN_NUMBER..LottoNumber.MAX_NUMBER)
                .filter { it !in excludeNumbers }
                .shuffled()
                .take(count)
                .sorted()
                .map { LottoNumber(it) }

        return candidates
    }
}
