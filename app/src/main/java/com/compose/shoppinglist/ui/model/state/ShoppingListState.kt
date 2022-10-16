package com.compose.shoppinglist.ui.model.state

sealed class ShoppingListState {
    object CURRENT : ShoppingListState()
    object ARCHIVED : ShoppingListState()
}