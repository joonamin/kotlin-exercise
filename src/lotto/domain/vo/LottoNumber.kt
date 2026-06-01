package lotto.domain.vo

@JvmInline
value class LottoNumber(
    val number: Int,
) : Comparable<LottoNumber> {
    override fun compareTo(other: LottoNumber): Int = this.number.compareTo(other.number)

    init {
        require(number in MIN_NUMBER..MAX_NUMBER) {
            "$MIN_NUMBER ~ $MAX_NUMBER 사이의 로또 번호만 입력 가능합니다"
        }
    }

    companion object {
        const val MIN_NUMBER = 1
        const val MAX_NUMBER = 45
    }
}
