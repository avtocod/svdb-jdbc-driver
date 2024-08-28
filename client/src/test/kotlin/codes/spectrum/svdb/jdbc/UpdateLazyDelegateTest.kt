package codes.spectrum.svdb.jdbc

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay

class UpdateLazyDelegateTest : FunSpec() {
    val iterator: Iterator<Int> = sequenceOf(0, 1, 2, 3, 4, 5).iterator()

    var counter = 0

    val property: Int by UpdatableLazyDelegate(refreshTime) {
        counter++
        iterator.next()
    }

    init {
        test("Проверяем проперти делегат") {
            // первоначальное значение лениво задается спустя время больше [refreshTime]
            delay(refreshTime + 200)
            counter shouldBe 0
            property shouldBe 0
            // подождали необходимое время, значение обновилось
            delay(refreshTime + 50)
            counter shouldBe 1
            property shouldBe 1
            // подождали меньше необходимого, значение не обновилось
            delay(refreshTime - 200)
            counter shouldBe 2
            property shouldBe 1
        }
    }

    companion object {
        const val refreshTime = 500L
    }
}
