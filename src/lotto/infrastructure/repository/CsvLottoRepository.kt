package lotto.infrastructure.repository

import LottoNumber
import LottoNumbers
import LottoResult
import LottoRound
import TicketResult
import WinningNumbers
import lotto.domain.vo.enums.Rank
import lotto.domain.vo.enums.RoundStatus
import java.io.File

class CsvLottoRepository(
    private val dir: String,
) : LottoRepository {
    init {
        File(dir).mkdirs()
    }

    override fun save(round: LottoRound) {
        val file = File(dir, "lottos_${round.roundNumber}.csv")
        val sb = StringBuilder()

        val winningPart =
            round.winningNumbers?.let {
                "${it.lottoNumbers.toCsvString()},${it.bonusNumber.number}"
            } ?: ","
        sb.appendLine("${round.roundNumber},${round.status.name},$winningPart")

        sb.appendLine("numbers,rank")

        if (round.result != null) {
            round.result!!.results.forEach { detail ->
                sb.appendLine("${detail.ticketNumbers.toCsvString()},${detail.rank}")
            }
        } else {
            // 미추첨시 번호만 저장한다
            round.pickedNumbers.forEach { numbers ->
                sb.appendLine("${numbers.toCsvString()},")
            }
        }

        file.writeText(sb.toString().trimEnd())
    }

    override fun findByRoundNumber(roundNumber: Int): LottoRound? {
        val file = File(dir, "lottos_$roundNumber.csv")
        if (!file.exists()) return null
        return deserialize(file)
    }

    private fun deserialize(file: File): LottoRound {
        val lines = file.readLines().filter { it.isNotBlank() }
        if (lines.isEmpty()) throw IllegalStateException("파일이 비어있음")

        // 1행 파싱: roundNumber,status,winningNumbers,bonusNumber
        val headerParts = lines[0].split(",")
        val roundNumber = headerParts[0].toInt()
        val status = RoundStatus.valueOf(headerParts[1])

        // 3행~ 티켓 데이터 파싱
        val dataLines = lines.drop(2)
        val pickedNumbers = mutableListOf<LottoNumbers>()
        val ticketResults = mutableListOf<TicketResult>()

        dataLines.forEach { line ->
            val parts = line.split(",")
            val numbers = LottoNumbers.fromCsvString(parts[0].trim())
            pickedNumbers.add(numbers)

            val rankStr = parts.getOrNull(1)?.trim()
            if (!rankStr.isNullOrBlank()) {
                ticketResults.add(TicketResult(numbers, Rank.valueOf(rankStr)))
            }
        }

        // LottoRound 복원
        return LottoRound(
            roundNumber = roundNumber,
            _pickedNumbers = pickedNumbers,
            status = status,
        ).apply {
            // 생성자로 객체 생성 후 바로 초기화하는 lambda with reciever 문법!
            // 당첨번호 복원
            val winStr = headerParts.getOrNull(2)?.trim() ?: ""
            val bonusStr = headerParts.getOrNull(3)?.trim() ?: ""

            val winningNumbers =
                if (winStr.isNotBlank() && bonusStr.isNotBlank()) {
                    val winLottoNums = LottoNumbers.fromCsvString(winStr)
                    val bonusNum = LottoNumber(bonusStr.toInt())
                    WinningNumbers(winLottoNums, bonusNum)
                } else {
                    null
                }

            restoreState(
                winningNumbers = winningNumbers,
                result = if (ticketResults.isNotEmpty()) LottoResult(ticketResults) else null,
            )
        }
    }
}
