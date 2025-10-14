package com.acon.core.data.stream

import app.cash.turbine.test
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class DataStreamImplTest {

    private lateinit var dataStream: DataStream

    @BeforeEach
    fun setUp() {
        dataStream = DataStreamImpl()
    }

    @Test
    fun `초기 방출 테스트`() = runTest {
        var value = 0
        val flow = dataStream.subscribe {
            emit(++value)
        }

        flow.test {
            assertEquals(1, awaitItem())
        }
    }

    @Test
    fun `notifyDataChanged가 flow를 트리거하는지 테스트`() = runTest {
        var value = 0
        val flow = dataStream.subscribe {
            emit(++value)
        }

        flow.test {
            assertEquals(1, awaitItem())
            dataStream.notifyDataChanged()
            assertEquals(2, awaitItem())
            dataStream.notifyDataChanged()
            assertEquals(3, awaitItem())
        }
    }

    @Test
    fun `여러 구독이 생겼을 때, 한 번의 notify는 모든 구독자에게 전파되어야 한다`() = runTest {
        var value1 = 0
        val flow1 = dataStream.subscribe<Int> {
            emit(++value1)
        }

        var value2 = 10
        val flow2 = dataStream.subscribe<Int> {
            emit(++value2)
        }

        flow1.test {
            val f1 = this
            flow2.test {
                val f2 = this
                assertEquals(1, f1.awaitItem())  // flow1의 첫 번째 값은 1
                assertEquals(11, f2.awaitItem()) // flow2의 첫 번째 값은 11

                dataStream.notifyDataChanged()

                assertEquals(2, f1.awaitItem())  // flow1의 두 번째 값은 2
                assertEquals(12, f2.awaitItem()) // flow2의 두 번째 값은 12
            }
        }
    }
}