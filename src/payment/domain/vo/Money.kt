data class Money(
    val value: Int,
) {
    init {
        require(value >= MONEY_UNIT) {
            "금액은 $MONEY_UNIT 이상이여야 합니다"
        }
        require(value % MONEY_UNIT == 0) {
            "금액은 $MONEY_UNIT 단위로만 가능합니다"
        }
    }

    operator fun plus(m: Money): Money = Money(this.value + m.value)

    operator fun minus(m: Money): Money = Money(this.value - m.value)

    companion object {
        const val MONEY_UNIT = 1000

        fun won(value: Int) = Money(value)
    }
}
