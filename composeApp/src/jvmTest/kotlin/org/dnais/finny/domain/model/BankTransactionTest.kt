package org.dnais.finny.domain.model

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull

class BankTransactionTest {

    @Test
    fun `BankTransaction creates instance with all fields`() {
        val transaction = BankTransaction(
            bookingDate = "2026-02-23",
            valueDate = "2026-02-24",
            partnerName = "John Doe",
            partnerIban = "DE89370400440532013000",
            type = "Transfer",
            paymentReference = "Invoice 123",
            accountName = "Main Account",
            amountEur = "-50.00",
            originalAmount = "-60.00",
            originalCurrency = "USD",
            exchangeRate = "0.833"
        )

        assertEquals("2026-02-23", transaction.bookingDate)
        assertEquals("2026-02-24", transaction.valueDate)
        assertEquals("John Doe", transaction.partnerName)
        assertEquals("DE89370400440532013000", transaction.partnerIban)
        assertEquals("Transfer", transaction.type)
        assertEquals("Invoice 123", transaction.paymentReference)
        assertEquals("Main Account", transaction.accountName)
        assertEquals("-50.00", transaction.amountEur)
        assertEquals("-60.00", transaction.originalAmount)
        assertEquals("USD", transaction.originalCurrency)
        assertEquals("0.833", transaction.exchangeRate)
    }

    @Test
    fun `BankTransaction creates instance with nullable fields as null`() {
        val transaction = BankTransaction(
            bookingDate = "2026-02-23",
            valueDate = "2026-02-23",
            partnerName = null,
            partnerIban = null,
            type = "Fee",
            paymentReference = "Monthly fee",
            accountName = "Main Account",
            amountEur = "-5.00",
            originalAmount = null,
            originalCurrency = null,
            exchangeRate = null
        )

        assertNull(transaction.partnerName)
        assertNull(transaction.partnerIban)
        assertNull(transaction.originalAmount)
        assertNull(transaction.originalCurrency)
        assertNull(transaction.exchangeRate)
    }

    @Test
    fun `BankTransaction equality works correctly`() {
        val transaction1 = BankTransaction(
            bookingDate = "2026-02-23",
            valueDate = "2026-02-23",
            partnerName = "John Doe",
            partnerIban = null,
            type = "Transfer",
            paymentReference = "Payment",
            accountName = "Account",
            amountEur = "-50.00",
            originalAmount = null,
            originalCurrency = null,
            exchangeRate = null
        )

        val transaction2 = BankTransaction(
            bookingDate = "2026-02-23",
            valueDate = "2026-02-23",
            partnerName = "John Doe",
            partnerIban = null,
            type = "Transfer",
            paymentReference = "Payment",
            accountName = "Account",
            amountEur = "-50.00",
            originalAmount = null,
            originalCurrency = null,
            exchangeRate = null
        )

        assertEquals(transaction1, transaction2)
        assertEquals(transaction1.hashCode(), transaction2.hashCode())
    }

    @Test
    fun `BankTransaction inequality works correctly`() {
        val transaction1 = BankTransaction(
            bookingDate = "2026-02-23",
            valueDate = "2026-02-23",
            partnerName = "John Doe",
            partnerIban = null,
            type = "Transfer",
            paymentReference = "Payment",
            accountName = "Account",
            amountEur = "-50.00",
            originalAmount = null,
            originalCurrency = null,
            exchangeRate = null
        )

        val transaction2 = BankTransaction(
            bookingDate = "2026-02-24",
            valueDate = "2026-02-24",
            partnerName = "Jane Smith",
            partnerIban = null,
            type = "Credit",
            paymentReference = "Different",
            accountName = "Account",
            amountEur = "100.00",
            originalAmount = null,
            originalCurrency = null,
            exchangeRate = null
        )

        assertNotEquals(transaction1, transaction2)
    }

    @Test
    fun `BankTransaction copy works correctly`() {
        val original = BankTransaction(
            bookingDate = "2026-02-23",
            valueDate = "2026-02-23",
            partnerName = "John Doe",
            partnerIban = null,
            type = "Transfer",
            paymentReference = "Payment",
            accountName = "Account",
            amountEur = "-50.00",
            originalAmount = null,
            originalCurrency = null,
            exchangeRate = null
        )

        val copied = original.copy(amountEur = "-75.00")

        assertEquals("2026-02-23", copied.bookingDate)
        assertEquals("John Doe", copied.partnerName)
        assertEquals("-75.00", copied.amountEur)
        assertNotEquals(original, copied)
    }

    @Test
    fun `BankTransaction toString contains field values`() {
        val transaction = BankTransaction(
            bookingDate = "2026-02-23",
            valueDate = "2026-02-23",
            partnerName = "John Doe",
            partnerIban = null,
            type = "Transfer",
            paymentReference = "Payment",
            accountName = "Account",
            amountEur = "-50.00",
            originalAmount = null,
            originalCurrency = null,
            exchangeRate = null
        )

        val string = transaction.toString()
        assert(string.contains("2026-02-23"))
        assert(string.contains("John Doe"))
        assert(string.contains("Transfer"))
        assert(string.contains("-50.00"))
    }

    @Test
    fun `BankTransaction handles positive amounts`() {
        val transaction = BankTransaction(
            bookingDate = "2026-02-23",
            valueDate = "2026-02-23",
            partnerName = "Salary",
            partnerIban = null,
            type = "Credit",
            paymentReference = "Monthly salary",
            accountName = "Account",
            amountEur = "3000.00",
            originalAmount = null,
            originalCurrency = null,
            exchangeRate = null
        )

        assertEquals("3000.00", transaction.amountEur)
    }

    @Test
    fun `BankTransaction handles negative amounts`() {
        val transaction = BankTransaction(
            bookingDate = "2026-02-23",
            valueDate = "2026-02-23",
            partnerName = "Shop",
            partnerIban = null,
            type = "Debit",
            paymentReference = "Purchase",
            accountName = "Account",
            amountEur = "-125.50",
            originalAmount = null,
            originalCurrency = null,
            exchangeRate = null
        )

        assertEquals("-125.50", transaction.amountEur)
    }
}
