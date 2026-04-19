import lotto.infrastructure.repository.CsvLottoRepository
import payment.infrastructure.repository.FileWalletRepository
import lotto.infrastructure.strategy.RandomLottoGenerationStrategy
import lotto.application.LottoService
import lotto.presentation.LottoController

fun main() {
    // entry point에서 필요한 구성요소들 생성 및 주입
    // 여기서는 진짜 Driver Program의 역할만 수행하게끔 수정 (필요 모듈들 로드)
    val lottoRepository = CsvLottoRepository("data")
    val walletRepository = FileWalletRepository("data")
    val strategy = RandomLottoGenerationStrategy()
    val service = LottoService(lottoRepository, strategy)

    val wallet = walletRepository.load() ?: payment.domain.entity.Wallet()

    val controller = LottoController(service, walletRepository, strategy, wallet)
    controller.run()
}
