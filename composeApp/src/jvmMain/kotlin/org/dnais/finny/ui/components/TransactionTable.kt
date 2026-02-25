package org.dnais.finny.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.dnais.finny.domain.model.BankTransaction

@Composable
fun TransactionTable(
    transactions: List<BankTransaction>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(8.dp)
        ) {
            HeaderCell("Date", Modifier.weight(1f))
            HeaderCell("Type", Modifier.weight(1.5f))
            HeaderCell("Partner", Modifier.weight(2f))
            HeaderCell("Reference", Modifier.weight(2f))
            HeaderCell("Amount (EUR)", Modifier.weight(1f))
        }

        HorizontalDivider()

        // Data Rows
        LazyColumn {
            items(transactions) { transaction ->
                TransactionRow(transaction)
                HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
            }
        }
    }
}

@Composable
private fun HeaderCell(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier.padding(horizontal = 4.dp),
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun TransactionRow(transaction: BankTransaction) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        DataCell(transaction.bookingDate, Modifier.weight(1f))
        DataCell(transaction.type, Modifier.weight(1.5f))
        DataCell(transaction.partnerName ?: "-", Modifier.weight(2f))
        DataCell(transaction.paymentReference, Modifier.weight(2f))
        AmountCell(transaction.amountEur, Modifier.weight(1f))
    }
}

@Composable
private fun DataCell(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier.padding(horizontal = 4.dp),
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
private fun AmountCell(amount: String, modifier: Modifier = Modifier) {
    val isNegative = amount.startsWith("-")
    Text(
        text = amount,
        modifier = modifier.padding(horizontal = 4.dp),
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.SemiBold,
        color = if (isNegative) {
            MaterialTheme.colorScheme.error
        } else {
            MaterialTheme.colorScheme.primary
        },
        textAlign = TextAlign.End
    )
}
