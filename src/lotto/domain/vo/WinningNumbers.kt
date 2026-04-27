package lotto.domain.vo

import lotto.domain.vo.enums.Rank

data class WinningNumbers(
    val lottoNumbers: LottoNumbers,
    val bonusNumber: LottoNumber,
) {
    init {
        require(!lottoNumbers.contains(bonusNumber)) {
            "보너스 번호는 당첨 번호들과 중복될 수 없습니다"
        }
    }

    fun match(userNumbers: LottoNumbers): Rank {
        val matchCount = lottoNumbers.countMatchedNumbers(userNumbers)
        val isBonusMatched = userNumbers.contains(bonusNumber)

        return Rank.valueOf(matchCount, isBonusMatched)
    }

    fun toCsv(): String = "${lottoNumbers.toCsvString()},$bonusNumber"

    companion object {
        fun draw(strategy: LottoGenerationStrategy): WinningNumbers {
            val lottoNumbers = LottoNumbers.auto(strategy)
            val bonus = strategy.generate(count = 1, exclude = lottoNumbers.numbers).first()
            return WinningNumbers(lottoNumbers, bonus)
        }
    }
}
