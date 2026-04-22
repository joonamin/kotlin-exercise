package lotto.domain.entity

import lotto.domain.vo.LottoNumbers
import lotto.domain.vo.LottoResult
import lotto.domain.vo.WinningNumbers
import lotto.domain.vo.enums.Rank
import lotto.domain.vo.enums.RoundStatus

// 채점의 주체 Aggregate
class LottoRound(
    val roundNumber: Int,
    private val _pickedNumbers: MutableList<LottoNumbers>,
    status: RoundStatus = RoundStatus.OPEN,
) {
    var status: RoundStatus = status
        private set

    var winningNumbers: WinningNumbers? = null
        private set

    var result: LottoResult? = null
        private set

    val pickedNumbers: List<LottoNumbers> get() = _pickedNumbers.toList()

    // repository 에서 상태를 복원하기 위해서 열어둔 백도어
    fun restoreState(
        winningNumbers: WinningNumbers?,
        result: LottoResult?,
    ) {
        this.winningNumbers = winningNumbers
        this.result = result
    }

    // command: 이번 회차의 로또를 추첨한다.
    fun draw(winningNumbers: WinningNumbers): LottoResult {
        // REVIEW: require은 기존 생성자로 전달되는 값들의 검증에 사용
        // check는 객체 내부의 상태에 따른 불변성 검증에 사용 (~~실행 전 ~상태를 보장 등등...)
        // exception 타입이 다르더라 -> require은 IllegalArgumentException, check는 IllegalStateException
        check(status == RoundStatus.OPEN) {
            "${roundNumber}회차는 이미 추첨이 완료되었습니다."
        }
        check(_pickedNumbers.isNotEmpty()) {
            "이번 회차에 구매한 로또가 없습니다"
        }

        this.winningNumbers = winningNumbers
        this.status = RoundStatus.FINISHED

        // 구매한 로또 티켓들의 aggregation 도출
        // 번호 리스트와 등수를 매핑하여 전달
        var results =
            _pickedNumbers.map { ticketNumbers ->
                val rank = winningNumbers.match(ticketNumbers)
                TicketResult(ticketNumbers, rank)
            }

        val drawResult = LottoResult(results)
        this.result = drawResult

        // 아래는 만약 이벤트 발행 인프라가 있다면 추가 수행
        // 현재는 바로 결과값을 리턴하여 콘솔에 보여주기 위함
        return drawResult
    }

    // domain service
    fun summary(): String = "$roundNumber,${this.status.name},$winningNumbers,${_pickedNumbers}"

    fun addTicket(numbers: LottoNumbers) {
        check(status == RoundStatus.OPEN) {
            "이미 마감된 회차에는 티켓을 추가할 수 없습니다"
        }
        _pickedNumbers.add(numbers)
    }
}

data class TicketResult(
    val ticketNumbers: LottoNumbers,
    val rank: Rank,
) {
    // 여기서는 따로 검증을 하진 않음
    // Aggregate 에서 도출된 결과물임. 하위 엔티티나 vo에서 정합성이 보장됨
}
