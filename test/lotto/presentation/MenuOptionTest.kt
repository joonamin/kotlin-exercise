package lotto.presentation

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class MenuOptionTest : BehaviorSpec({
    Given("메뉴 ID로 MenuOption을 조회할 때") {
        data class FromIdTestCase(val id: Int, val expected: MenuOption?)

        listOf(
            FromIdTestCase(1, MenuOption.PURCHASE),
            FromIdTestCase(2, MenuOption.DRAW),
            FromIdTestCase(3, MenuOption.CHARGE),
            FromIdTestCase(4, MenuOption.QUERY_HISTORY),
            FromIdTestCase(5, MenuOption.EXIT),
            FromIdTestCase(0, null),
            FromIdTestCase(999, null),
            FromIdTestCase(-1, null),
        ).forEach { testCase ->
            When("from(${testCase.id})을 호출하면") {
                Then("${testCase.expected ?: "null"}을 반환한다") {
                    MenuOption.from(testCase.id) shouldBe testCase.expected
                }
            }
        }
    }

    Given("각 MenuOption의 속성을 확인할 때") {
        data class PropertyTestCase(
            val menuOption: MenuOption,
            val expectedId: Int,
            val expectedDescription: String,
        )

        listOf(
            PropertyTestCase(MenuOption.PURCHASE, 1, "로또 구매"),
            PropertyTestCase(MenuOption.DRAW, 2, "로또 추첨 (당첨 번호 발표)"),
            PropertyTestCase(MenuOption.CHARGE, 3, "잔액 충전"),
            PropertyTestCase(MenuOption.QUERY_HISTORY, 4, "과거 회차 데이터 조회"),
            PropertyTestCase(MenuOption.EXIT, 5, "종료"),
        ).forEach { testCase ->
            When("${testCase.menuOption}의 속성을 검증하면") {
                Then("id는 ${testCase.expectedId}, description은 '${testCase.expectedDescription}'이다") {
                    testCase.menuOption.id shouldBe testCase.expectedId
                    testCase.menuOption.description shouldBe testCase.expectedDescription
                }
            }
        }
    }
})
