

// 여기서는 어떤 로또 번호들과 결과가 어떠한지 바로 확인할 수 있게끔 리턴
// 도메인스럽게 wrapping한 클래스. 관심사에 따른 결합도를 낮추기 위함
data class LottoResult(
    val results: List<TicketResult>,
)
