package com.compose.shoppinglist.ui.view.layout

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import com.compose.shoppinglist.ui.model.AmbientScreenState
import com.compose.shoppinglist.ui.model.AmbientSharedViewModel
import com.compose.shoppinglist.ui.model.model.ScreenUi
import com.compose.shoppinglist.ui.model.state.ScreenState
import com.compose.shoppinglist.ui.view.layout.archivedList.ArchivedProductListScreen
import com.compose.shoppinglist.ui.view.layout.archivedList.ShoppingListArchivedScreen
import com.compose.shoppinglist.ui.view.layout.currentList.ProductListCurrentScreen
import com.compose.shoppinglist.ui.view.layout.currentList.ShoppingListCurrentScreen
import com.compose.shoppinglist.ui.theme.ShoppingListTheme

@Composable
fun ShoppingListApp() {
    ShoppingListTheme {
        Surface(color = MaterialTheme.colors.background) {
            AppContent()
        }
    }
}

@Composable
private fun AppContent() {
    val sharedViewModelAmbient = AmbientSharedViewModel.current
    val screenState: State<ScreenUi?> = sharedViewModelAmbient.screenState.observeAsState()
    val screenStateValue = screenState.value
    screenStateValue ?: return

    CompositionLocalProvider(AmbientScreenState provides screenStateValue) {
        when (screenStateValue.currentScreenState) {
            is ScreenState.CurrentShoppingList -> ShoppingListCurrentScreen()
            is ScreenState.ArchivedShoppingList -> ShoppingListArchivedScreen()
            is ScreenState.CurrentProductList -> ProductListCurrentScreen()
            is ScreenState.ArchivedProductList -> ArchivedProductListScreen()
        }
    }
}
