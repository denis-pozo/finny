package org.dnais.finny.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.dnais.finny.data.parser.CsvTransactionParser
import org.dnais.finny.domain.model.BankTransaction
import java.io.File

class TransactionRepository(
    private val csvParser: CsvTransactionParser = CsvTransactionParser()
) {

    suspend fun loadTransactionsFromCsv(file: File): Result<List<BankTransaction>> {
        return withContext(Dispatchers.IO) {
            csvParser.parse(file)
        }
    }
}
