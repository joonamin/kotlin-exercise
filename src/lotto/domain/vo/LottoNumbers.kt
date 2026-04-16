data class LottoNumbers(
    val numbers: List<LottoNumber>,
) {
    init {
        require(numbers.distinct().size == TOTAL_COUNT) {
            "로또 번호들은 ${TOTAL_COUNT} 개여야 합니다"
        }
    }

    fun toCsvString(): String = this.numbers.joinToString(":")

    companion object {
        const val TOTAL_COUNT = 6

        // 1. 수동
        fun manual(numbers: List<LottoNumber>): LottoNumbers = LottoNumbers(numbers)

        // 2. 자동
        fun auto(strategy: LottoGenerationStrategy): LottoNumbers {
            val generated = strategy.generate(TOTAL_COUNT, exclude = emptyList())
            return LottoNumbers(generated)
        }

        // 3. 반자동
        fun halfAuto(
            manualNumbers: List<LottoNumber>,
            strategy: LottoGenerationStrategy,
        ): LottoNumbers {
            require(manualNumbers.size < TOTAL_COUNT) {
                "반자동은 ${TOTAL_COUNT}개 미만의 수동 번호가 필요합니다"
            }
            val requiredMore = TOTAL_COUNT - manualNumbers.size
            val autoNumbers = strategy.generate(requiredMore, exclude = manualNumbers)
            return LottoNumbers(manualNumbers + autoNumbers)
        }

        fun fromCsvString(csvString: String): LottoNumbers =
            LottoNumbers(csvString.split(":").map { LottoNumber(it.toInt()) })
    }

    fun contains(lottoNumber: LottoNumber): Boolean = this.numbers.contains(lottoNumber)

    fun countMatchedNumbers(other: LottoNumbers): Int = numbers.count { other.contains(it) }
}

fun interface LottoGenerationStrategy {
    fun generate(
        count: Int,
        exclude: List<LottoNumber>,
    ): List<LottoNumber>
}
