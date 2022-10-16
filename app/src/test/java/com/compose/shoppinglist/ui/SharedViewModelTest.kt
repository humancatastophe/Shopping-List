package com.compose.shoppinglist.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.compose.shoppinglist.MainCoroutineRule
import com.compose.shoppinglist.data.database.model.Product
import com.compose.shoppinglist.data.database.model.ShoppingList
import com.compose.shoppinglist.data.repository.ShoppingListRepository
import com.compose.shoppinglist.data.status.ResultStatus
import com.compose.shoppinglist.util.LiveDataTestUtil.getValue
import com.compose.shoppinglist.util.LiveDataTestUtil.isNotInvoke
import com.compose.shoppinglist.util.any
import com.compose.shoppinglist.ui.model.ScreenBackStackImpl
import com.compose.shoppinglist.ui.model.SharedViewModel
import com.compose.shoppinglist.ui.model.mapper.asUiModel
import com.compose.shoppinglist.ui.model.state.ScreenState
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyLong
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify

@ExperimentalCoroutinesApi
class SharedViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @ObsoleteCoroutinesApi
    private var mainThreadSurrogate = newSingleThreadContext("UI thread")

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var screenBackStackImpl: ScreenBackStackImpl
    private lateinit var sharedViewModel: SharedViewModel
    private val shoppingListRepository = mock(ShoppingListRepository::class.java)

    @ObsoleteCoroutinesApi
    @Before
    fun init() {
        Dispatchers.setMain(mainThreadSurrogate)
        screenBackStackImpl = ScreenBackStackImpl()
        sharedViewModel = SharedViewModel(screenBackStackImpl, shoppingListRepository)
    }

    @ObsoleteCoroutinesApi
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    @Test
    fun testNull() {
        assertThat(sharedViewModel.screenState).isNotNull()
    }

    @Test
    fun testNoInteraction() {
        verify(shoppingListRepository, never()).getCurrentShoppingList()
        verify(shoppingListRepository, never()).getArchivedShoppingList()
        verify(shoppingListRepository, never()).getProductList(anyLong())
    }

    @Test
    fun currentScreenStateNoInvoke() {
        isNotInvoke(sharedViewModel.screenState) {
            sharedViewModel.pushBackStack(ScreenState.CurrentShoppingList)
        }
    }

    @Test
    fun selectedShoppingListNoInvoke() {
        val shoppingList = ShoppingList().asUiModel()
        isNotInvoke(sharedViewModel.screenState) {
            sharedViewModel.pushBackStack(ScreenState.CurrentProductList(shoppingList))
        }
    }

    @Test
    fun screenStateCurrentShoppingList() {
        val shoppingLists = MutableLiveData<ResultStatus<List<ShoppingList>>>()
            .apply { value = ResultStatus.Success(emptyList()) }
        `when`(shoppingListRepository.getCurrentShoppingList()).thenReturn(shoppingLists)
        sharedViewModel.pushBackStack(ScreenState.CurrentShoppingList)
        assertThat(getValue(sharedViewModel.screenState).shoppingListsUi)
            .isInstanceOf(ResultStatus.Success::class.java)
    }

    @Test
    fun screenStateCurrentShoppingListNoInvoke() {
        val shoppingList = ShoppingList().asUiModel()
        val shoppingLists = MutableLiveData<ResultStatus<List<ShoppingList>>>()
            .apply { value = ResultStatus.Success(emptyList()) }
        `when`(shoppingListRepository.getCurrentShoppingList()).thenReturn(shoppingLists)
        sharedViewModel.pushBackStack(ScreenState.CurrentProductList(shoppingList))
        assertThat(getValue(sharedViewModel.screenState).shoppingListsUi).isNull()
    }

    @Test
    fun screenStateArchivedShoppingList() {
        val shoppingLists = MutableLiveData<ResultStatus<List<ShoppingList>>>()
            .apply { value = ResultStatus.Success(emptyList()) }
        `when`(shoppingListRepository.getArchivedShoppingList()).thenReturn(shoppingLists)
        sharedViewModel.pushBackStack(ScreenState.ArchivedShoppingList)
        assertThat(getValue(sharedViewModel.screenState).shoppingListsUi)
            .isInstanceOf(ResultStatus.Success::class.java)
    }

    @Test
    fun screenStateArchivedShoppingListNoInvoke() {
        val shoppingList = ShoppingList().asUiModel()
        val shoppingLists = MutableLiveData<ResultStatus<List<ShoppingList>>>()
            .apply { value = ResultStatus.Success(emptyList()) }
        `when`(shoppingListRepository.getArchivedShoppingList()).thenReturn(shoppingLists)
        sharedViewModel.pushBackStack(ScreenState.ArchivedProductList(shoppingList))
        assertThat(getValue(sharedViewModel.screenState).shoppingListsUi).isNull()
    }

    @Test
    fun screenStateProductList() {
        val shoppingList = ShoppingList().asUiModel()
        val shoppingLists = MutableLiveData<ResultStatus<List<Product>>>()
            .apply { value = ResultStatus.Success(emptyList()) }
        `when`(shoppingListRepository.getProductList(anyLong())).thenReturn(shoppingLists)
        sharedViewModel.pushBackStack(ScreenState.CurrentProductList(shoppingList))
        assertThat(getValue(sharedViewModel.screenState).productListUi)
            .isInstanceOf(ResultStatus.Success::class.java)
    }

    @Test
    fun screenStateProductListNoInvoke() {
        val shoppingLists = MutableLiveData<ResultStatus<List<Product>>>()
            .apply { value = ResultStatus.Success(emptyList()) }
        `when`(shoppingListRepository.getProductList(anyLong())).thenReturn(shoppingLists)
        sharedViewModel.pushBackStack(ScreenState.CurrentShoppingList)
        assertThat(getValue(sharedViewModel.screenState).productListUi).isNull()
    }

    @Test
    fun createShoppingList() = runBlocking {
        val listName = "Test"
        sharedViewModel.createShoppingList(listName)
        assertThat(getValue(sharedViewModel.screenState).createShoppingListLoading).isTrue()
        unlockThread()
        verify(shoppingListRepository).insertShoppingList(any())
        assertThat(getValue(sharedViewModel.screenState).createShoppingListLoading).isFalse()
    }

    @Test
    fun updateShoppingList() = runBlocking {
        val shoppingList = ShoppingList().asUiModel()
        sharedViewModel.updateShoppingList(shoppingList, true)
        assertThat(getValue(sharedViewModel.screenState).updateShoppingListLoading).isTrue()
        unlockThread()
        verify(shoppingListRepository).updateShoppingList(any())
        assertThat(getValue(sharedViewModel.screenState).updateShoppingListLoading).isFalse()
    }

    @Test
    fun createProduct() = runBlocking {
        val product = Product()
        sharedViewModel.createProduct(
            product.productName,
            product.productQuantity,
            product.shoppingListId
        )
        assertThat(getValue(sharedViewModel.screenState).createProductLoading).isTrue()
        unlockThread()
        verify(shoppingListRepository).insertProduct(any())
        assertThat(getValue(sharedViewModel.screenState).createProductLoading).isFalse()
    }

    @Test
    fun deleteProduct() = runBlocking {
        val product = Product().asUiModel()
        sharedViewModel.deleteProduct(product)
        assertThat(getValue(sharedViewModel.screenState).deleteProductLoading).isTrue()
        unlockThread()
        verify(shoppingListRepository).deleteProduct(any())
        assertThat(getValue(sharedViewModel.screenState).deleteProductLoading).isFalse()
    }

    @Test
    fun backStack() {
        assertThat(sharedViewModel.popBackStack()).isNull()
        sharedViewModel.pushBackStack(ScreenState.CurrentShoppingList)
        assertThat(sharedViewModel.popBackStack()).isNull()
        sharedViewModel.pushBackStack(ScreenState.ArchivedShoppingList)
        assertThat(sharedViewModel.popBackStack()).isEqualTo(ScreenState.CurrentShoppingList)
    }
}

suspend fun unlockThread() {
    delay(10)
}