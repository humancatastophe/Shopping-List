package com.compose.shoppinglist.data.repository

import androidx.lifecycle.LiveData
import com.compose.shoppinglist.data.database.model.Product
import com.compose.shoppinglist.data.database.model.ShoppingList
import com.compose.shoppinglist.data.status.ResultStatus

interface ShoppingListRepository {
    suspend fun insertShoppingList(shoppingList: ShoppingList)

    suspend fun updateShoppingList(shoppingList: ShoppingList)

    suspend fun insertProduct(product: Product)

    suspend fun updateProduct(product: Product)

    suspend fun deleteProduct(product: Product)

    fun getCurrentShoppingList(): LiveData<ResultStatus<List<ShoppingList>>>

    fun getArchivedShoppingList(): LiveData<ResultStatus<List<ShoppingList>>>

    fun getProductList(listId: Long): LiveData<ResultStatus<List<Product>>>
}