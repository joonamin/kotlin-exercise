package lotto.domain.vo

import payment.domain.vo.Money

data class TicketPrice(
    val unitPrice: Money,
) {
    fun totalCost(ticketCount: Int): Money {
        require(ticketCount > 0) {
            "구매 수량은 1개 이상이어야 합니다"
        }
        return unitPrice * ticketCount
    }

    fun maxAffordableCount(budget: Money): Int = budget.value / unitPrice.value

    companion object {
        const val PRICE_PER_TICKET = 1000
        val DEFAULT = TicketPrice(Money.won(PRICE_PER_TICKET))
    }
}
