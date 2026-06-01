package payment.domain.entity

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import payment.domain.vo.Money

class WalletTest : BehaviorSpec({
    Given("초기 잔액이 0원인 지갑이 주어졌을 때") {
        When("잔액을 확인하면") {
            Then("0원이다") {
                val wallet = Wallet()
                wallet.balance shouldBe Money.ZERO
            }
        }
    }

    Given("지갑에 충전할 때") {
        When("양수 금액을 충전하면") {
            Then("잔액이 증가한다") {
                val wallet = Wallet()
                wallet.charge(Money.won(5000))
                wallet.balance shouldBe Money.won(5000)
            }
        }

        When("여러 번 충전하면") {
            Then("잔액이 누적된다") {
                val wallet = Wallet()
                wallet.charge(Money.won(3000))
                wallet.charge(Money.won(2000))
                wallet.balance shouldBe Money.won(5000)
            }
        }

        When("0원을 충전하려고 하면") {
            Then("IllegalArgumentException이 발생한다") {
                val wallet = Wallet()
                shouldThrow<IllegalArgumentException> {
                    wallet.charge(Money.ZERO)
                }
            }
        }
    }

    Given("잔액이 충분한 지갑에서 출금할 때") {
        When("잔액 이하의 금액을 출금하면") {
            Then("잔액이 감소한다") {
                val wallet = Wallet(Money.won(10000))
                wallet.withdraw(Money.won(3000))
                wallet.balance shouldBe Money.won(7000)
            }
        }

        When("잔액 전부를 출금하면") {
            Then("잔액이 0원이 된다") {
                val wallet = Wallet(Money.won(5000))
                wallet.withdraw(Money.won(5000))
                wallet.balance shouldBe Money.ZERO
            }
        }
    }

    Given("잔액이 부족한 지갑에서 출금할 때") {
        When("잔액보다 큰 금액을 출금하려고 하면") {
            Then("IllegalArgumentException이 발생한다") {
                val wallet = Wallet(Money.won(1000))
                val exception = shouldThrow<IllegalArgumentException> {
                    wallet.withdraw(Money.won(5000))
                }
                exception.message shouldContain "잔액이 부족합니다"
            }
        }
    }

    Given("출금 금액이 0 이하일 때") {
        When("0원을 출금하려고 하면") {
            Then("IllegalArgumentException이 발생한다") {
                val wallet = Wallet(Money.won(5000))
                shouldThrow<IllegalArgumentException> {
                    wallet.withdraw(Money.ZERO)
                }
            }
        }
    }

    Given("당첨금을 입금할 때") {
        When("양수 금액을 입금하면") {
            Then("잔액이 증가한다") {
                val wallet = Wallet(Money.won(1000))
                wallet.receivePrize(Money.won(50000))
                wallet.balance shouldBe Money.won(51000)
            }
        }

        When("0원을 입금하려고 하면") {
            Then("IllegalArgumentException이 발생한다") {
                val wallet = Wallet()
                shouldThrow<IllegalArgumentException> {
                    wallet.receivePrize(Money.ZERO)
                }
            }
        }
    }

    Given("초기 잔액이 설정된 지갑이 주어졌을 때") {
        When("생성 시 잔액을 지정하면") {
            Then("해당 잔액으로 생성된다") {
                val wallet = Wallet(Money.won(10000))
                wallet.balance shouldBe Money.won(10000)
            }
        }
    }
})
