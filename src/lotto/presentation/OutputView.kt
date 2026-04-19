import lotto.domain.vo.enums.Rank

/**
 * 결과를 포맷팅하여 출력하는 뷰
 */
object OutputView {
    fun printPurchaseResult(
        tickets: List<LottoNumbers>,
        manualCount: Int,
        autoCount: Int,
        totalCost: Money,
        walletBalance: Money,
    ) {
        println(
            "수동으로 ${manualCount}장, 자동으로 ${autoCount}장을 구매했습니다. (총 ${"%,d".format(
                totalCost.value,
            )}원 / 잔액 ${"%,d".format(walletBalance.value)}원)",
        )
        tickets.forEach { ticket ->
            val formatted =
                ticket.numbers
                    .map { it.number }
                    .sorted()
                    .joinToString(", ", "[", "]")
            println(formatted)
        }
        println()
    }

    fun printWinningStatistics(result: LottoResult) {
        println()
        println("당첨 통계")
        println("---------")

        val rankCounts = result.results.groupingBy { it.rank }.eachCount()

        listOf(Rank.FIFTH, Rank.FOURTH, Rank.THIRD, Rank.SECOND, Rank.FIRST).forEach { rank ->
            val count = rankCounts.getOrDefault(rank, 0)
            val bonusText = if (rank == Rank.SECOND) ", 보너스 볼 일치" else ""
            println("${rank.matchCount}개 일치$bonusText (${formatPrize(rank.prize)})- ${count}개")
        }
    }

    fun printYield(
        result: LottoResult,
        purchaseAmount: Money,
    ) {
        val totalPrize = result.results.sumOf { it.rank.prize.value }
        val yieldRate =
            if (purchaseAmount.value > 0) {
                (totalPrize.toDouble() / purchaseAmount.value.toDouble()) * 100
            } else {
                0.0
            }
        println("총 수익률은 %.2f%%입니다.".format(yieldRate))
    }

    fun printError(message: String) {
        println("[ERROR] $message")
    }

    private fun formatPrize(money: Money): String = "%,d원".format(money.value)
}
