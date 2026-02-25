package org.dnais.finny.data.repository

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.dnais.finny.data.parser.CsvTransactionParser
import org.dnais.finny.domain.model.BankTransaction
import org.junit.Before
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TransactionRepositoryTest {

    private lateinit var mockParser: CsvTransactionParser
    private lateinit var repository: TransactionRepository

    @Before
    fun setup() {
        mockParser = mockk()
        repository = TransactionRepository(mockParser)
    }

    @Test
    fun `loadTransactionsFromCsv returns success when parser succeeds`() = runTest {
        val file = mockk<File>()
        val expectedTransactions = listOf(
            BankTransaction(
                bookingDate = "2026-02-23",
                valueDate = "2026-02-23",
                partnerName = "John Doe",
                partnerIban = null,
                type = "Transfer",
                paymentReference = "Payment",
                accountName = "Main Account",
                amountEur = "-50.00",
                originalAmount = null,
                originalCurrency = null,
                exchangeRate = null
            )
        )

        every { mockParser.parse(file) } returns Result.success(expectedTransactions)

        val result = repository.loadTransactionsFromCsv(file)

        assertTrue(result.isSuccess)
        assertEquals(expectedTransactions, result.getOrNull())
        verify { mockParser.parse(file) }
    }

    @Test
    fun `loadTransactionsFromCsv returns failure when parser fails`() = runTest {
        val file = mockk<File>()
        val expectedException = Exception("Invalid CSV format")

        every { mockParser.parse(file) } returns Result.failure(expectedException)

        val result = repository.loadTransactionsFromCsv(file)

        assertTrue(result.isFailure)
        assertEquals(expectedException.message, result.exceptionOrNull()?.message)
        verify { mockParser.parse(file) }
    }

    @Test
    fun `loadTransactionsFromCsv returns empty list when parser returns empty`() = runTest {
        val file = mockk<File>()

        every { mockParser.parse(file) } returns Result.success(emptyList())

        val result = repository.loadTransactionsFromCsv(file)

        assertTrue(result.isSuccess)
        assertEquals(0, result.getOrNull()?.size)
        verify { mockParser.parse(file) }
    }

    @Test
    fun `loadTransactionsFromCsv handles multiple transactions`() = runTest {
        val file = mockk<File>()
        val expectedTransactions = listOf(
            BankTransaction(
                bookingDate = "2026-02-23",
                valueDate = "2026-02-23",
                partnerName = "Partner 1",
                partnerIban = null,
                type = "Type 1",
                paymentReference = "Ref 1",
                accountName = "Account 1",
                amountEur = "-10.00",
                originalAmount = null,
                originalCurrency = null,
                exchangeRate = null
            ),
            BankTransaction(
                bookingDate = "2026-02-24",
                valueDate = "2026-02-24",
                partnerName = "Partner 2",
                partnerIban = null,
                type = "Type 2",
                paymentReference = "Ref 2",
                accountName = "Account 2",
                amountEur = "20.00",
                originalAmount = null,
                originalCurrency = null,
                exchangeRate = null
            )
        )

        every { mockParser.parse(file) } returns Result.success(expectedTransactions)

        val result = repository.loadTransactionsFromCsv(file)

        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
        assertEquals(expectedTransactions, result.getOrNull())
        verify { mockParser.parse(file) }
    }
}
