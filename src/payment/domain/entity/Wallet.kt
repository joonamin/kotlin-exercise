package payment.domain.entity

import payment.domain.vo.Money

class Wallet(
    private var _balance: Money = Money.ZERO,
) {
    val balance: Money get() = _balance

    // command: 돈을 충전한다
    fun charge(amount: Money) {
        require(amount.value > 0) {
            "충전 금액은 0보다 커야 합니다"
        }
        _balance += amount
    }

    // command: 출금한다 (로또 구매 등)
    fun withdraw(amount: Money) {
        require(amount.value > 0) {
            "출금 금액은 0보다 커야 합니다"
        }
        require(_balance.isAffordable(amount)) {
            "잔액이 부족합니다. (현재 잔액: ${_balance.value}원, 요청 금액: ${amount.value}원)"
        }
        _balance -= amount
    }

    // command: 당첨금을 입금한다 (lotto context -> 당첨금이 계산됨 이벤트에 의해 트리거)
    fun receivePrize(amount: Money) {
        require(amount.value > 0) {
            "당첨금은 0보다 커야 합니다"
        }
        _balance += amount
    }
}
