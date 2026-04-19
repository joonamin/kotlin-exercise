import lotto.domain.vo.TicketPrice
import lotto.domain.vo.enums.RoundStatus
import lotto.infrastructure.repository.CsvLottoRepository
import payment.domain.entity.Wallet
import payment.infrastructure.repository.FileWalletRepository
import java.io.File
import kotlin.system.exitProcess

private val ROUND_FILE = File("data/current_round.txt")

fun loadCurrentRound(): Int {
    if (!ROUND_FILE.exists()) {
        ROUND_FILE.parentFile?.mkdirs()
        ROUND_FILE.writeText("1")
        return 1
    }
    return ROUND_FILE.readText().trim().toInt()
}

fun saveCurrentRound(roundNumber: Int) {
    ROUND_FILE.writeText(roundNumber.toString())
}

fun main() {
    // 나중에 프레임워크를 사용한다면...
    // 아래 영역은 설정 파일을 기반으로 DI 받도록~
    val lottoRepository = CsvLottoRepository("data")
    val walletRepository = FileWalletRepository("data")
    val strategy = RandomLottoGenerationStrategy()
    val service = LottoService(lottoRepository, strategy)

    // Wallet 로드 (없으면 잔액 0원으로 시작)
    val wallet: Wallet = walletRepository.load() ?: Wallet()
    var currentRoundNumber = loadCurrentRound()

    while (true) {
        val menu = InputView.readMenuOption(currentRoundNumber, wallet.balance)

        when (menu) {
            1 -> { // 로또 구매
                val ticketCount = InputView.readTicketCount()
                val manualCount = InputView.readManualTicketCount(ticketCount)
                val manualNumbers = InputView.readManualNumbers(manualCount)

                val totalCost = TicketPrice.DEFAULT.totalCost(ticketCount)

                try {
                    wallet.withdraw(totalCost)
                } catch (e: IllegalArgumentException) {
                    OutputView.printError(e.message ?: "잔액이 부족합니다.")
                    continue
                }

                val round = service.purchaseLotto(currentRoundNumber, ticketCount, manualNumbers)
                walletRepository.save(wallet)

                val autoCount = ticketCount - manualCount
                OutputView.printPurchaseResult(round.pickedNumbers, manualCount, autoCount, totalCost, wallet.balance)
            }

            2 -> { // 로또 추첨
                // 당첨 번호 (6개) 자동 생성
                val winningLottoNumbers = LottoNumbers.auto(strategy)
                val usedNumbers = winningLottoNumbers.numbers.map { it.number }.toSet()

                // 보너스 번호 겹치지 않게 무작위 생성
                var generatedBonus: LottoNumber
                while (true) {
                    val rand = LottoNumber.random()
                    if (rand.number !in usedNumbers) {
                        generatedBonus = rand
                        break
                    }
                }

                val winningNumbers = WinningNumbers(winningLottoNumbers, generatedBonus)

                println("\n[${currentRoundNumber}회차 발표]")
                println(
                    "당첨 번호: ${winningLottoNumbers.numbers.map { it.number }.sorted().joinToString(
                        ", ",
                        "[",
                        "]",
                    )} + 보너스: ${generatedBonus.number}",
                )

                try {
                    val result = service.drawLotto(currentRoundNumber, winningNumbers)
                    OutputView.printWinningStatistics(result)

                    val round = service.findRound(currentRoundNumber)!!
                    val totalCost = TicketPrice.DEFAULT.totalCost(round.pickedNumbers.size)
                    OutputView.printYield(result, totalCost)

                    // 당첨금 입금
                    val totalPrize = result.results.sumOf { it.rank.prize.value }
                    if (totalPrize > 0) {
                        wallet.receivePrize(Money.won(totalPrize))
                        walletRepository.save(wallet)
                        println(
                            "\n[안내] 당첨금 ${"%,d".format(
                                totalPrize,
                            )}원이 지갑에 입금되었습니다. (현재 잔액: ${"%,d".format(wallet.balance.value)}원)",
                        )
                    }

                    // 추첨 성공 시 회차 증가 후 저장
                    currentRoundNumber++
                    saveCurrentRound(currentRoundNumber)
                    println("\n[안내] 추첨이 완료되었습니다. 다음 회차(${currentRoundNumber}회차) 구매를 시작합니다.")
                } catch (e: Exception) {
                    OutputView.printError(e.message ?: "추첨 처리 중 오류가 발생했습니다.")
                }
            }

            3 -> { // 잔액 충전
                val amount = InputView.readChargeAmount()
                wallet.charge(amount)
                walletRepository.save(wallet)
                println("충전 완료! 현재 잔액: ${"%,d".format(wallet.balance.value)}원")
            }

            4 -> { // 과거 회차 데이터 조회
                val searchRound = InputView.readRoundNumber()

                // 입력값이 현재 라운드 이후라면 (아직 추첨하지 않은 라운드라면)
                if (searchRound > currentRoundNumber) {
                    OutputView.printError("해당 회차(${searchRound}회차)는 아직 시작되지 않았습니다.")
                    continue
                }

                val roundData = service.findRound(searchRound)

                if (roundData == null) {
                    OutputView.printError("해당 회차(${searchRound}회차)의 데이터가 없습니다.")
                } else if (roundData.status == RoundStatus.OPEN) {
                    // 진행중인 회차
                    OutputView.printError("해당 회차(${searchRound}회차)는 아직 추첨되지 않았습니다. '2. 로또 추첨' 메뉴를 통해 추첨을 먼저 수행해 주세요.")
                } else if (roundData.result != null) {
                    // 추첨이 완료된 경우 당첨 통계 출력
                    println("\n[${searchRound}회차 당첨 조회 결과]")
                    OutputView.printWinningStatistics(roundData.result!!)
                }
            }

            5 -> { // 종료
                println("프로그램을 종료합니다.")
                exitProcess(0)
            }

            else -> {
                OutputView.printError("잘못된 메뉴 번호입니다.")
            }
        }
    }
}
