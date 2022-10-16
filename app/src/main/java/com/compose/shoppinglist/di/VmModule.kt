package com.compose.shoppinglist.di

import android.content.Context
import com.compose.shoppinglist.data.database.ShoppingListDatabase
import com.compose.shoppinglist.data.database.ShoppingListDatabaseDao
import com.compose.shoppinglist.data.database.ShoppingListSource
import com.compose.shoppinglist.data.database.ShoppingListSourceImpl
import com.compose.shoppinglist.data.repository.ShoppingListRepository
import com.compose.shoppinglist.data.repository.ShoppingListRepositoryImpl
import com.compose.shoppinglist.ui.model.ScreenBackStack
import com.compose.shoppinglist.ui.model.ScreenBackStackImpl
import com.compose.shoppinglist.ui.model.SharedViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val uiDataModule = module {
    viewModel { SharedViewModel(get(), get()) }
    single { createBackStack() }
}

val dataModule = module {
    single { createShoppingListSource(get(), get()) }
    single { createRepository(get(), get()) }
    single { createDatabase(get()) }
    single { createIoDispatcher() }
}

fun createBackStack(): ScreenBackStack = ScreenBackStackImpl()

fun createIoDispatcher() = Dispatchers.Default

fun createDatabase(context: Context): ShoppingListDatabaseDao {
    return ShoppingListDatabase.getInstance(context).shoppingListDatabaseDao
}

fun createRepository(
    shoppingListDatabase: ShoppingListSource,
    ioDispatcher: CoroutineDispatcher
): ShoppingListRepository {
    return ShoppingListRepositoryImpl(shoppingListDatabase, ioDispatcher)
}

fun createShoppingListSource(
    shoppingListDatabase: ShoppingListDatabaseDao,
    ioDispatcher: CoroutineDispatcher
): ShoppingListSource {
    return ShoppingListSourceImpl(shoppingListDatabase, ioDispatcher)
}