package payment.infrastructure.repository

import Money
import payment.domain.entity.Wallet
import java.io.File

class FileWalletRepository(
    private val dir: String,
) : WalletRepository {
    private val fileName = "user_wallet.txt"

    init {
        File(dir).mkdirs()
    }

    override fun save(wallet: Wallet) {
        val file = File(dir, fileName)
        file.writeText(wallet.balance.value.toString())
    }

    override fun load(): Wallet? {
        val file = File(dir, fileName)
        if (!file.exists()) return null

        val text = file.readText().trim()
        if (text.isBlank()) return null

        val balance = text.toInt()
        return Wallet(Money.won(balance))
    }
}
