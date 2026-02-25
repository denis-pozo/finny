package org.dnais.finny.ui.viewmodel

import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.*
import org.dnais.finny.data.repository.TransactionRepository
import org.dnais.finny.domain.model.BankTransaction
import org.dnais.finny.util.FilePickerUtil
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionViewModelTest {

    private lateinit var mockRepository: TransactionRepository
    private lateinit var viewModel: TransactionViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = mockk()

        mockkObject(FilePickerUtil)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `initial state is correct`() {
        viewModel = TransactionViewModel(mockRepository)

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.transactions.isEmpty())
        assertNull(state.error)
    }

    @Test
    fun `importCsv sets loading state when started`() = runTest {
        viewModel = TransactionViewModel(mockRepository)
        val mockFile = mockk<File>()

        coEvery { FilePickerUtil.pickCsvFile() } coAnswers {
            delay(100)
            mockFile
        }
        coEvery { mockRepository.loadTransactionsFromCsv(mockFile) } returns Result.success(emptyList())

        viewModel.importCsv()

        advanceTimeBy(50)

        assertTrue(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `importCsv updates state with transactions on success`() = runTest {
        viewModel = TransactionViewModel(mockRepository)
        val mockFile = mockk<File>()
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

        coEvery { FilePickerUtil.pickCsvFile() } returns mockFile
        coEvery { mockRepository.loadTransactionsFromCsv(mockFile) } returns Result.success(expectedTransactions)

        viewModel.importCsv()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(1, state.transactions.size)
        assertEquals(expectedTransactions, state.transactions)
        assertNull(state.error)
    }

    @Test
    fun `importCsv updates state with error on failure`() = runTest {
        viewModel = TransactionViewModel(mockRepository)
        val mockFile = mockk<File>()
        val errorMessage = "Invalid CSV format"

        coEvery { FilePickerUtil.pickCsvFile() } returns mockFile
        coEvery { mockRepository.loadTransactionsFromCsv(mockFile) } returns Result.failure(Exception(errorMessage))

        viewModel.importCsv()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.transactions.isEmpty())
        assertEquals(errorMessage, state.error)
    }

    @Test
    fun `importCsv does nothing when user cancels file picker`() = runTest {
        viewModel = TransactionViewModel(mockRepository)

        coEvery { FilePickerUtil.pickCsvFile() } returns null

        viewModel.importCsv()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.transactions.isEmpty())
        assertNull(state.error)

        coVerify(exactly = 0) { mockRepository.loadTransactionsFromCsv(any()) }
    }

    @Test
    fun `importCsv handles repository returning empty list`() = runTest {
        viewModel = TransactionViewModel(mockRepository)
        val mockFile = mockk<File>()

        coEvery { FilePickerUtil.pickCsvFile() } returns mockFile
        coEvery { mockRepository.loadTransactionsFromCsv(mockFile) } returns Result.success(emptyList())

        viewModel.importCsv()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.transactions.isEmpty())
        assertNull(state.error)
    }

    @Test
    fun `importCsv replaces previous transactions on new import`() = runTest {
        viewModel = TransactionViewModel(mockRepository)
        val mockFile1 = mockk<File>()
        val mockFile2 = mockk<File>()

        val firstTransactions = listOf(
            BankTransaction(
                bookingDate = "2026-02-23",
                valueDate = "2026-02-23",
                partnerName = "First",
                partnerIban = null,
                type = "Type1",
                paymentReference = "Ref1",
                accountName = "Account1",
                amountEur = "-10.00",
                originalAmount = null,
                originalCurrency = null,
                exchangeRate = null
            )
        )

        val secondTransactions = listOf(
            BankTransaction(
                bookingDate = "2026-02-24",
                valueDate = "2026-02-24",
                partnerName = "Second",
                partnerIban = null,
                type = "Type2",
                paymentReference = "Ref2",
                accountName = "Account2",
                amountEur = "20.00",
                originalAmount = null,
                originalCurrency = null,
                exchangeRate = null
            ),
            BankTransaction(
                bookingDate = "2026-02-25",
                valueDate = "2026-02-25",
                partnerName = "Third",
                partnerIban = null,
                type = "Type3",
                paymentReference = "Ref3",
                accountName = "Account3",
                amountEur = "30.00",
                originalAmount = null,
                originalCurrency = null,
                exchangeRate = null
            )
        )

        coEvery { FilePickerUtil.pickCsvFile() } returnsMany listOf(mockFile1, mockFile2)
        coEvery { mockRepository.loadTransactionsFromCsv(mockFile1) } returns Result.success(firstTransactions)
        coEvery { mockRepository.loadTransactionsFromCsv(mockFile2) } returns Result.success(secondTransactions)

        viewModel.importCsv()
        advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.transactions.size)

        viewModel.importCsv()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(2, state.transactions.size)
        assertEquals(secondTransactions, state.transactions)
    }

    @Test
    fun `importCsv clears error on new successful import`() = runTest {
        viewModel = TransactionViewModel(mockRepository)
        val mockFile1 = mockk<File>()
        val mockFile2 = mockk<File>()
        val successTransactions = listOf(
            BankTransaction(
                bookingDate = "2026-02-23",
                valueDate = "2026-02-23",
                partnerName = "Success",
                partnerIban = null,
                type = "Type",
                paymentReference = "Ref",
                accountName = "Account",
                amountEur = "-10.00",
                originalAmount = null,
                originalCurrency = null,
                exchangeRate = null
            )
        )

        coEvery { FilePickerUtil.pickCsvFile() } returnsMany listOf(mockFile1, mockFile2)
        coEvery { mockRepository.loadTransactionsFromCsv(mockFile1) } returns Result.failure(Exception("Error"))
        coEvery { mockRepository.loadTransactionsFromCsv(mockFile2) } returns Result.success(successTransactions)

        viewModel.importCsv()
        advanceUntilIdle()

        assertEquals("Error", viewModel.uiState.value.error)

        viewModel.importCsv()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNull(state.error)
        assertEquals(1, state.transactions.size)
    }
}
