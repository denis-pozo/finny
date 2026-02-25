package org.dnais.finny

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.dnais.finny.ui.screen.TransactionListScreen

@Composable
@Preview
fun App() {
    MaterialTheme {
        TransactionListScreen()
    }
}