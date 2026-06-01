package lotto.presentation

enum class MenuOption(
    val id: Int,
    val description: String,
) {
    PURCHASE(1, "로또 구매"),
    DRAW(2, "로또 추첨 (당첨 번호 발표)"),
    CHARGE(3, "잔액 충전"),
    QUERY_HISTORY(4, "과거 회차 데이터 조회"),
    EXIT(5, "종료"),
    ;

    companion object {
        fun from(id: Int): MenuOption? = entries.find { it.id == id }
    }
}
