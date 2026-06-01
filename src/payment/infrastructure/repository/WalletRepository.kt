package payment.infrastructure.repository

import payment.domain.entity.Wallet

// data access layer
interface WalletRepository {
    fun save(wallet: Wallet)

    fun load(): Wallet?
}
