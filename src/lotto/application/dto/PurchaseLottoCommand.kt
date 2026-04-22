package lotto.application.dto

import lotto.domain.vo.LottoNumbers

data class PurchaseLottoCommand private constructor(
    val roundNumber: Int,
    val ticketCount: Int,
    private val _manualNumbersList: List<LottoNumbers>,
) {
    init {
        require(ticketCount > 0) {
            "구매 수량은 1개 이상이어야 합니다."
        }
        require(_manualNumbersList.size <= ticketCount) {
            "수동 구매 개수는 총 구매 개수를 초과할 수 없습니다."
        }
    }

    val manualNumbersList: List<LottoNumbers> get() = _manualNumbersList.toList()

    // static factory method로만 생성할 수 있게끔
    companion object {
        fun of(
            roundNumber: Int,
            ticketCount: Int,
            manualNumbersList: List<LottoNumbers>,
        ): PurchaseLottoCommand = PurchaseLottoCommand(roundNumber, ticketCount, manualNumbersList)
    }
}
