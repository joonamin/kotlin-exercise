package payment.domain.vo

data class Money(
    val value: Int,
) : Comparable<Money> {
    init {
        require(value >= 0) {
            "금액은 0 이상이여야 합니다"
        }
    }

    operator fun plus(other: Money): Money = Money(this.value + other.value)

    operator fun minus(other: Money): Money = Money(this.value - other.value)

    operator fun times(multiplier: Int): Money = Money(this.value * multiplier)

    override fun compareTo(other: Money): Int = this.value.compareTo(other.value)

    fun isAffordable(cost: Money): Boolean = this >= cost

    companion object {
        val ZERO = Money(0)

        fun won(value: Int) = Money(value)
    }
}
