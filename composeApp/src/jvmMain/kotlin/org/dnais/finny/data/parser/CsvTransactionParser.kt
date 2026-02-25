package org.dnais.finny.data.parser

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.dnais.finny.domain.model.BankTransaction
import java.io.File
import java.io.FileReader

class CsvTransactionParser {

    private val expectedHeaders = listOf(
        "Booking Date",
        "Value Date",
        "Partner Name",
        "Partner Iban",
        "Type",
        "Payment Reference",
        "Account Name",
        "Amount (EUR)",
        "Original Amount",
        "Original Currency",
        "Exchange Rate"
    )

    fun parse(file: File): Result<List<BankTransaction>> {
        return try {
            if (!file.exists()) {
                return Result.failure(Exception("File does not exist"))
            }

            if (!file.canRead()) {
                return Result.failure(Exception("Cannot read file. Permission denied."))
            }

            val transactions = mutableListOf<BankTransaction>()

            FileReader(file).use { reader ->
                val csvParser = CSVParser(reader, CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setIgnoreEmptyLines(true)
                    .setTrim(true)
                    .build())

                // Validate headers
                val headers = csvParser.headerMap.keys.toList()
                val missingHeaders = expectedHeaders.filter { it !in headers }
                if (missingHeaders.isNotEmpty()) {
                    return Result.failure(
                        Exception("CSV is missing required columns: ${missingHeaders.joinToString(", ")}")
                    )
                }

                // Parse records
                for (record in csvParser) {
                    try {
                        val transaction = BankTransaction(
                            bookingDate = record.get("Booking Date"),
                            valueDate = record.get("Value Date"),
                            partnerName = record.get("Partner Name").takeIf { it.isNotBlank() },
                            partnerIban = record.get("Partner Iban").takeIf { it.isNotBlank() },
                            type = record.get("Type"),
                            paymentReference = record.get("Payment Reference"),
                            accountName = record.get("Account Name"),
                            amountEur = record.get("Amount (EUR)"),
                            originalAmount = record.get("Original Amount").takeIf { it.isNotBlank() },
                            originalCurrency = record.get("Original Currency").takeIf { it.isNotBlank() },
                            exchangeRate = record.get("Exchange Rate").takeIf { it.isNotBlank() }
                        )
                        transactions.add(transaction)
                    } catch (e: Exception) {
                        // Skip malformed records
                        continue
                    }
                }
            }

            if (transactions.isEmpty()) {
                Result.failure(Exception("No valid transactions found in file"))
            } else {
                Result.success(transactions)
            }

        } catch (e: Exception) {
            Result.failure(Exception("Failed to parse CSV: ${e.message}"))
        }
    }
}
