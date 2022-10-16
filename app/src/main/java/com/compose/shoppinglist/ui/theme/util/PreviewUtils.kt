package com.compose.shoppinglist.ui.theme.util

import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import com.compose.shoppinglist.ui.theme.ShoppingListTheme

@Composable
internal fun ThemedPreview(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    ShoppingListTheme(darkTheme = darkTheme) {
        Surface {
            content()
        }
    }
}
