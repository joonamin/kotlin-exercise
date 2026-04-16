data class LottoNumber(
    val number: Int,
) {
    init {
        require(number in MIN_NUMBER..MAX_NUMBER) {
            "$MIN_NUMBER ~ $MAX_NUMBER 사이의 로또 번호만 입력 가능합니다"
        }
    }

    override fun toString(): String = number.toString()

    companion object {
        const val MIN_NUMBER = 1
        const val MAX_NUMBER = 45

        fun random(): LottoNumber = LottoNumber((MIN_NUMBER..MAX_NUMBER).random())
    }
}
