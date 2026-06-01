package lotto.domain.vo.enums

enum class RoundStatus(
    val status: String,
) {
    OPEN("OPEN"),
    IN_PROGRESS("IN PROGRESS"),
    FINISHED("FINISHED"),
    ;

    companion object {
        fun fromStatus(status: String): RoundStatus = entries.find { it.status == status } ?: OPEN
    }
}
