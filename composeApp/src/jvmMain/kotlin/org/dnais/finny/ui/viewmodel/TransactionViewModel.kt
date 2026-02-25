package org.dnais.finny.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.dnais.finny.data.repository.TransactionRepository
import org.dnais.finny.domain.model.BankTransaction
import org.dnais.finny.util.FilePickerUtil

class TransactionViewModel(
    private val repository: TransactionRepository = TransactionRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun importCsv() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val selectedFile = FilePickerUtil.pickCsvFile()

            if (selectedFile == null) {
                // User cancelled
                _uiState.value = _uiState.value.copy(isLoading = false)
                return@launch
            }

            val result = repository.loadTransactionsFromCsv(selectedFile)

            result.fold(
                onSuccess = { transactions ->
                    _uiState.value = UiState(
                        isLoading = false,
                        transactions = transactions,
                        error = null
                    )
                },
                onFailure = { exception ->
                    _uiState.value = UiState(
                        isLoading = false,
                        transactions = emptyList(),
                        error = exception.message ?: "Unknown error occurred"
                    )
                }
            )
        }
    }

    data class UiState(
        val isLoading: Boolean = false,
        val transactions: List<BankTransaction> = emptyList(),
        val error: String? = null
    )
}
