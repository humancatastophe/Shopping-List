package com.compose.shoppinglist.ui.model.state

import com.compose.shoppinglist.ui.model.model.ShoppingListUi

sealed class ScreenState {
    object CurrentShoppingList : ScreenState()
    object ArchivedShoppingList : ScreenState()
    class CurrentProductList(val shoppingList: ShoppingListUi) : ScreenState()
    class ArchivedProductList(val shoppingList: ShoppingListUi) : ScreenState()
    object Empty : ScreenState()
}