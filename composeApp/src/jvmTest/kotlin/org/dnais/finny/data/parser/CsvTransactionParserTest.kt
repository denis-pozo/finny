package org.dnais.finny.data.parser

import org.dnais.finny.domain.model.BankTransaction
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CsvTransactionParserTest {

    private lateinit var parser: CsvTransactionParser
    private val testFiles = mutableListOf<File>()

    @Before
    fun setup() {
        parser = CsvTransactionParser()
    }

    @After
    fun cleanup() {
        testFiles.forEach { it.delete() }
        testFiles.clear()
    }

    private fun createTempCsvFile(content: String): File {
        val file = File.createTempFile("test_transactions_", ".csv")
        file.writeText(content)
        testFiles.add(file)
        return file
    }

    @Test
    fun `parse valid CSV with single transaction`() {
        val csvContent = """
            "Booking Date","Value Date","Partner Name","Partner Iban","Type","Payment Reference","Account Name","Amount (EUR)","Original Amount","Original Currency","Exchange Rate"
            2026-02-23,2026-02-23,,,"Debit Transfer","Sesión Denís 23.02","Main Account",-70.000000000,,,
        """.trimIndent()

        val file = createTempCsvFile(csvContent)
        val result = parser.parse(file)

        assertTrue(result.isSuccess)
        val transactions = result.getOrNull()!!
        assertEquals(1, transactions.size)

        val transaction = transactions[0]
        assertEquals("2026-02-23", transaction.bookingDate)
        assertEquals("2026-02-23", transaction.valueDate)
        assertEquals(null, transaction.partnerName)
        assertEquals(null, transaction.partnerIban)
        assertEquals("Debit Transfer", transaction.type)
        assertEquals("Sesión Denís 23.02", transaction.paymentReference)
        assertEquals("Main Account", transaction.accountName)
        assertEquals("-70.000000000", transaction.amountEur)
        assertEquals(null, transaction.originalAmount)
        assertEquals(null, transaction.originalCurrency)
        assertEquals(null, transaction.exchangeRate)
    }

    @Test
    fun `parse valid CSV with multiple transactions`() {
        val csvContent = """
            "Booking Date","Value Date","Partner Name","Partner Iban","Type","Payment Reference","Account Name","Amount (EUR)","Original Amount","Original Currency","Exchange Rate"
            2026-02-23,2026-02-23,"John Doe","DE89370400440532013000","Debit Transfer","Invoice 123","Main Account",-50.00,,,
            2026-02-24,2026-02-24,"Jane Smith","GB33BUKB20201555555555","Credit","Payment received","Main Account",100.00,,,
            2026-02-25,2026-02-25,,,"Fee","Monthly fee","Main Account",-5.00,,,
        """.trimIndent()

        val file = createTempCsvFile(csvContent)
        val result = parser.parse(file)

        assertTrue(result.isSuccess)
        val transactions = result.getOrNull()!!
        assertEquals(3, transactions.size)

        assertEquals("John Doe", transactions[0].partnerName)
        assertEquals("Jane Smith", transactions[1].partnerName)
        assertEquals(null, transactions[2].partnerName)
    }

    @Test
    fun `parse CSV with currency exchange`() {
        val csvContent = """
            "Booking Date","Value Date","Partner Name","Partner Iban","Type","Payment Reference","Account Name","Amount (EUR)","Original Amount","Original Currency","Exchange Rate"
            2026-02-23,2026-02-23,"Shop USA","US64SVBKUS6S3300958879","Purchase","Online purchase","Main Account",-85.50,-100.00,"USD",0.855
        """.trimIndent()

        val file = createTempCsvFile(csvContent)
        val result = parser.parse(file)

        assertTrue(result.isSuccess)
        val transactions = result.getOrNull()!!
        assertEquals(1, transactions.size)

        val transaction = transactions[0]
        assertEquals("-85.50", transaction.amountEur)
        assertEquals("-100.00", transaction.originalAmount)
        assertEquals("USD", transaction.originalCurrency)
        assertEquals("0.855", transaction.exchangeRate)
    }

    @Test
    fun `parse fails when file does not exist`() {
        val nonExistentFile = File("/tmp/nonexistent_${System.currentTimeMillis()}.csv")

        val result = parser.parse(nonExistentFile)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("does not exist") == true)
    }

    @Test
    fun `parse fails when CSV is missing required columns`() {
        val csvContent = """
            "Booking Date","Value Date","Type","Amount (EUR)"
            2026-02-23,2026-02-23,"Transfer",-50.00
        """.trimIndent()

        val file = createTempCsvFile(csvContent)
        val result = parser.parse(file)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("missing required columns") == true)
    }

    @Test
    fun `parse fails when CSV has only headers`() {
        val csvContent = """
            "Booking Date","Value Date","Partner Name","Partner Iban","Type","Payment Reference","Account Name","Amount (EUR)","Original Amount","Original Currency","Exchange Rate"
        """.trimIndent()

        val file = createTempCsvFile(csvContent)
        val result = parser.parse(file)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("No valid transactions") == true)
    }

    @Test
    fun `parse handles empty CSV file`() {
        val csvContent = ""

        val file = createTempCsvFile(csvContent)
        val result = parser.parse(file)

        assertTrue(result.isFailure)
    }

    @Test
    fun `parse handles malformed rows gracefully`() {
        val csvContent = """
            "Booking Date","Value Date","Partner Name","Partner Iban","Type","Payment Reference","Account Name","Amount (EUR)","Original Amount","Original Currency","Exchange Rate"
            2026-02-23,2026-02-23,"Valid Row",,"Transfer","Payment","Account",-50.00,,,
            this,is,clearly,malformed,data
            2026-02-24,2026-02-24,"Another Valid",,,"Payment 2","Account",100.00,,,
        """.trimIndent()

        val file = createTempCsvFile(csvContent)
        val result = parser.parse(file)

        assertTrue(result.isSuccess)
        val transactions = result.getOrNull()!!
        assertEquals(2, transactions.size)
        assertEquals("Valid Row", transactions[0].partnerName)
        assertEquals("Another Valid", transactions[1].partnerName)
    }

    @Test
    fun `parse handles empty string fields correctly`() {
        val csvContent = """
            "Booking Date","Value Date","Partner Name","Partner Iban","Type","Payment Reference","Account Name","Amount (EUR)","Original Amount","Original Currency","Exchange Rate"
            2026-02-23,2026-02-23,"","","Transfer","","Main Account",-50.00,"","",""
        """.trimIndent()

        val file = createTempCsvFile(csvContent)
        val result = parser.parse(file)

        assertTrue(result.isSuccess)
        val transactions = result.getOrNull()!!
        assertEquals(1, transactions.size)

        val transaction = transactions[0]
        assertEquals(null, transaction.partnerName)
        assertEquals(null, transaction.partnerIban)
        assertEquals(null, transaction.originalAmount)
        assertEquals(null, transaction.originalCurrency)
        assertEquals(null, transaction.exchangeRate)
    }

    @Test
    fun `parse handles whitespace in fields`() {
        val csvContent = """
            "Booking Date","Value Date","Partner Name","Partner Iban","Type","Payment Reference","Account Name","Amount (EUR)","Original Amount","Original Currency","Exchange Rate"
            2026-02-23,2026-02-23,"  John Doe  ",,"  Transfer  ","  Payment  ","Main Account",-50.00,,,
        """.trimIndent()

        val file = createTempCsvFile(csvContent)
        val result = parser.parse(file)

        assertTrue(result.isSuccess)
        val transactions = result.getOrNull()!!
        assertEquals(1, transactions.size)

        val transaction = transactions[0]
        assertEquals("John Doe", transaction.partnerName)
        assertEquals("Transfer", transaction.type)
        assertEquals("Payment", transaction.paymentReference)
    }
}
