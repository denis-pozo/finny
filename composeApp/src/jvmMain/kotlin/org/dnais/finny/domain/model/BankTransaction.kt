package org.dnais.finny.domain.model

data class BankTransaction(
    val bookingDate: String,
    val valueDate: String,
    val partnerName: String?,
    val partnerIban: String?,
    val type: String,
    val paymentReference: String,
    val accountName: String,
    val amountEur: String,
    val originalAmount: String?,
    val originalCurrency: String?,
    val exchangeRate: String?
)
