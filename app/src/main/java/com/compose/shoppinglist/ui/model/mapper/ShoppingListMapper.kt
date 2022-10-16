package com.compose.shoppinglist.ui.model.mapper

import com.compose.shoppinglist.data.database.model.ShoppingList
import com.compose.shoppinglist.ui.model.model.ShoppingListUi

fun List<ShoppingList>.asUiModel(): List<ShoppingListUi> {
    return map { it.asUiModel() }
}

fun ShoppingList.asUiModel(): ShoppingListUi {
    return ShoppingListUi(
        id = id,
        shoppingListName = shoppingListName,
        shoppingListTimestamp = shoppingListTimestamp,
        isArchived = isArchived
    )
}

fun ShoppingListUi.asDomainModel(): ShoppingList {
    return ShoppingList(
        id = id,
        shoppingListName = shoppingListName,
        shoppingListTimestamp = shoppingListTimestamp,
        isArchived = isArchived
    )
}