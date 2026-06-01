package lotto.presentation

import lotto.domain.vo.LottoNumber
import lotto.domain.vo.LottoNumbers
import payment.domain.vo.Money

/**
 * 사용자 입력을 처리하는 뷰
 * - 파싱과 기본 형식 검증만 수행
 * - 비즈니스 검증은 도메인에 위임
 * - 잘못된 입력 시 재입력 요청
 */
object InputView {
    private fun <T> retryOnException(action: () -> T): T {
        while (true) {
            try {
                return action()
            } catch (e: Exception) {
                OutputView.printError(e.message ?: "잘못된 입력입니다.")
            }
        }
    }

    fun readMenuOption(
        currentRound: Int,
        walletBalance: Money,
    ): MenuOption =
        retryOnException {
            println("\n=== 로또 시스템 (잔액: ${"%,d".format(walletBalance.value)}원, 현재 진행중: ${currentRound}회차) ===")
            MenuOption.entries.forEach {
                println("${it.id}. ${it.description}")
            }
            print("메뉴를 선택하세요: ")
            val input = readln().trim().toInt()
            MenuOption.from(input) ?: throw IllegalArgumentException("잘못된 메뉴 번호입니다.")
        }

    fun readRoundNumber(): Int =
        retryOnException {
            println("조회할 회차 번호를 입력해 주세요.")
            readln().trim().toInt()
        }

    fun readChargeAmount(): Money =
        retryOnException {
            println("충전할 금액을 입력해 주세요.")
            val amount = readln().trim().toInt()
            Money.won(amount)
        }

    fun readTicketCount(): Int =
        retryOnException {
            println("구매할 로또 개수를 입력해 주세요.")
            readln().trim().toInt()
        }

    fun readManualTicketCount(totalCount: Int): Int =
        retryOnException {
            println("수동으로 구매할 로또 수를 입력해 주세요.")
            val count = readln().trim().toInt()
            require(count in 0..totalCount) { "수동 구매 개수는 0 이상 총 구매 개수($totalCount) 이하여야 합니다." }
            count
        }

    fun readManualNumbers(count: Int): List<LottoNumbers> =
        retryOnException {
            if (count == 0) return@retryOnException emptyList()

            println("수동으로 구매할 번호를 입력해 주세요. (쉼표로 구분 / 한 줄에 하나씩)")
            val manualNumbers = mutableListOf<LottoNumbers>()
            repeat(count) {
                val input = readLine()!!.trim()
                val numbers =
                    input
                        .split(",")
                        .map { LottoNumber(it.trim().toInt()) }
                manualNumbers.add(LottoNumbers(numbers))
            }
            manualNumbers
        }
}
