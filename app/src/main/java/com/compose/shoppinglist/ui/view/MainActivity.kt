package com.compose.shoppinglist.ui.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.activity.compose.setContent
import com.compose.shoppinglist.ui.model.AmbientSharedViewModel
import com.compose.shoppinglist.ui.model.ScreenBackStack
import com.compose.shoppinglist.ui.model.SharedViewModel
import com.compose.shoppinglist.ui.view.layout.ShoppingListApp
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val sharedViewModel: SharedViewModel by viewModel()
    private val backStack: ScreenBackStack by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CompositionLocalProvider(
                AmbientSharedViewModel provides sharedViewModel
            ) { ShoppingListApp() }
        }
    }

    override fun onBackPressed() {
        backStack.popBackStack() ?: super.onBackPressed()
    }
}
