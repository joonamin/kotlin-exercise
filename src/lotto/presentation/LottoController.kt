package lotto.presentation

import lotto.application.LottoService
import lotto.application.dto.PurchaseLottoCommand
import lotto.domain.vo.LottoGenerationStrategy
import lotto.domain.vo.TicketPrice
import lotto.domain.vo.WinningNumbers
import lotto.domain.vo.enums.RoundStatus
import payment.domain.entity.Wallet
import payment.domain.vo.Money
import payment.infrastructure.repository.WalletRepository
import java.io.File
import kotlin.system.exitProcess

// 사용자 서비스 레이어는 일단 정의하지 않고 진행함
// 컨트롤러에서 직접 도메인 서비스를 호출하고 저장하는 구조
// 분리의 필요성이 있는지는 PR 리뷰 이후에 판단해보자..
class LottoController(
    private val service: LottoService,
    private val walletRepository: WalletRepository,
    private val strategy: LottoGenerationStrategy,
    private val wallet: Wallet,
) {
    private val roundFile = File("data/current_round.txt")
    private var currentRoundNumber: Int = loadCurrentRound()

    fun run() {
        while (true) {
            val menu = InputView.readMenuOption(currentRoundNumber, wallet.balance)
            executeMenu(menu)
        }
    }

    private fun executeMenu(menu: Int) {
        when (menu) {
            1 -> purchase()
            2 -> draw()
            3 -> charge()
            4 -> queryHistory()
            5 -> exit()
            else -> OutputView.printError("잘못된 메뉴 번호입니다.")
        }
    }

    private fun purchase() {
        val ticketCount = InputView.readTicketCount()
        val manualCount = InputView.readManualTicketCount(ticketCount)
        val manualNumbers = InputView.readManualNumbers(manualCount)
        val totalCost = TicketPrice.DEFAULT.totalCost(ticketCount)

        try {
            wallet.withdraw(totalCost)
        } catch (e: IllegalArgumentException) {
            OutputView.printError(e.message ?: "잔액이 부족합니다.")
            return
        }

        val purchaseLottoCommand =
            PurchaseLottoCommand.of(
                roundNumber = currentRoundNumber,
                ticketCount = ticketCount,
                manualNumbersList = manualNumbers,
            )

        val round = service.purchaseLotto(purchaseLottoCommand)
        walletRepository.save(wallet)

        val autoCount = ticketCount - manualCount
        OutputView.printPurchaseResult(
            round.pickedNumbers,
            manualCount,
            autoCount,
            totalCost,
            wallet.balance,
        )
    }

    private fun draw() {
        val winningNumbers = generateWinningNumbers()

        println("\n[${currentRoundNumber}회차 발표]")
        val numbersString =
            winningNumbers
                .lottoNumbers
                .numbers
                .joinToString(", ", "[", "]") { it.number.toString() }
        println("당첨 번호: $numbersString + 보너스: ${winningNumbers.bonusNumber.number}")

        try {
            processDrawResult(winningNumbers)
        } catch (e: Exception) {
            OutputView.printError(e.message ?: "추첨 처리 중 오류가 발생했습니다.")
        }
    }

    private fun generateWinningNumbers(): WinningNumbers = WinningNumbers.draw(strategy)

    private fun processDrawResult(winningNumbers: WinningNumbers) {
        val result = service.drawLotto(currentRoundNumber, winningNumbers)
        OutputView.printWinningStatistics(result)

        val round = service.findRound(currentRoundNumber)!!
        val totalCost = TicketPrice.DEFAULT.totalCost(round.pickedNumbers.size)
        OutputView.printYield(result, totalCost)

        depositPrize(result.results.sumOf { it.rank.prize.value })

        currentRoundNumber++
        saveCurrentRound(currentRoundNumber)
        println("\n[안내] 추첨이 완료되었습니다. 다음 회차(${currentRoundNumber}회차) 구매를 시작합니다.")
    }

    private fun depositPrize(totalPrize: Int) {
        if (totalPrize > 0) {
            wallet.receivePrize(Money.won(totalPrize))
            walletRepository.save(wallet)
            println(
                "\n[안내] 당첨금 ${"%,d".format(
                    totalPrize,
                )}원이 지갑에 입금되었습니다. (현재 잔액: ${"%,d".format(wallet.balance.value)}원)",
            )
        }
    }

    private fun charge() {
        val amount = InputView.readChargeAmount()
        wallet.charge(amount)
        walletRepository.save(wallet)
        println("충전 완료! 현재 잔액: ${"%,d".format(wallet.balance.value)}원")
    }

    private fun queryHistory() {
        val searchRound = InputView.readRoundNumber()
        if (searchRound > currentRoundNumber) {
            OutputView.printError("해당 회차(${searchRound}회차)는 아직 시작되지 않았습니다.")
            return
        }

        val roundData = service.findRound(searchRound)
        if (roundData == null) {
            OutputView.printError("해당 회차(${searchRound}회차)의 데이터가 없습니다.")
        } else if (roundData.status == RoundStatus.OPEN) {
            OutputView.printError(
                "해당 회차(${searchRound}회차)는 아직 추첨되지 않았습니다. '2. 로또 추첨' 메뉴를 통해 추첨을 먼저 수행해 주세요.",
            )
        } else if (roundData.result != null) {
            println("\n[${searchRound}회차 당첨 조회 결과]")
            OutputView.printWinningStatistics(roundData.result!!)
        }
    }

    private fun exit() {
        println("프로그램을 종료합니다.")
        exitProcess(0)
    }

    private fun loadCurrentRound(): Int {
        if (!roundFile.exists()) {
            roundFile.parentFile?.mkdirs()
            roundFile.writeText("1")
            return 1
        }
        return roundFile.readText().trim().toInt()
    }

    private fun saveCurrentRound(roundNumber: Int) {
        roundFile.writeText(roundNumber.toString())
    }
}
