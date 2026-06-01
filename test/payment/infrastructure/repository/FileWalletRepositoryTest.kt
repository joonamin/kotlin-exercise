package payment.infrastructure.repository

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import payment.domain.entity.Wallet
import payment.domain.vo.Money
import java.io.File

class FileWalletRepositoryTest : BehaviorSpec({
    fun withTempDir(block: (String) -> Unit) {
        val tempDir = kotlin.io.path.createTempDirectory("wallet-test").toFile()
        try {
            block(tempDir.absolutePath)
        } finally {
            tempDir.deleteRecursively()
        }
    }

    Given("지갑을 저장하고 로드할 때") {
        When("잔액이 있는 지갑을 save 후 load하면") {
            Then("동일한 잔액으로 복원된다") {
                withTempDir { dir ->
                    val repository = FileWalletRepository(dir)
                    val wallet = Wallet(Money.won(5000))
                    repository.save(wallet)

                    val loaded = repository.load()
                    loaded shouldNotBe null
                    loaded!!.balance shouldBe Money.won(5000)
                }
            }
        }

        When("잔액이 0원인 지갑을 save 후 load하면") {
            Then("0원 잔액으로 복원된다") {
                withTempDir { dir ->
                    val repository = FileWalletRepository(dir)
                    val wallet = Wallet(Money.ZERO)
                    repository.save(wallet)

                    val loaded = repository.load()
                    loaded shouldNotBe null
                    loaded!!.balance shouldBe Money.ZERO
                }
            }
        }
    }

    Given("파일이 존재하지 않을 때") {
        When("load를 호출하면") {
            Then("null을 반환한다") {
                withTempDir { dir ->
                    val repository = FileWalletRepository(dir)
                    repository.load() shouldBe null
                }
            }
        }
    }

    Given("파일이 빈 내용일 때") {
        When("load를 호출하면") {
            Then("null을 반환한다") {
                withTempDir { dir ->
                    val repository = FileWalletRepository(dir)
                    // 빈 파일 생성
                    File(dir, "user_wallet.txt").writeText("")
                    repository.load() shouldBe null
                }
            }
        }
    }

    Given("지갑을 덮어쓸 때") {
        When("다른 잔액으로 다시 save하면") {
            Then("마지막 잔액이 복원된다") {
                withTempDir { dir ->
                    val repository = FileWalletRepository(dir)
                    repository.save(Wallet(Money.won(1000)))
                    repository.save(Wallet(Money.won(9999)))

                    val loaded = repository.load()
                    loaded!!.balance shouldBe Money.won(9999)
                }
            }
        }
    }
})
