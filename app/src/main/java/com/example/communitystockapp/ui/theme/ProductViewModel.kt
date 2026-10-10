package com.example.communitystockapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communitystockapp.data.Product
import com.example.communitystockapp.data.ProductRepository
import com.example.communitystockapp.data.ProductRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProductUiState(
    val nameInput: String = "",
    val qtyInput: String = "",
    val priceInput: String = "",
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ProductViewModel(
    private val repository: ProductRepository = ProductRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductUiState())
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    init {
        loadProducts()
    }

    fun onNameChange(value: String) { _uiState.update { it.copy(nameInput = value) } }
    fun onQtyChange(value: String) { _uiState.update { it.copy(qtyInput = value) } }
    fun onPriceChange(value: String) { _uiState.update { it.copy(priceInput = value) } }

    fun loadProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val list = repository.getProducts()
                _uiState.update { it.copy(products = list, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message ?: "เกิดข้อผิดพลาด", isLoading = false) }
            }
        }
    }

    fun addProduct() {
        val state = _uiState.value
        val name = state.nameInput.trim()
        val qty = state.qtyInput.toIntOrNull() ?: 0
        val price = state.priceInput.toDoubleOrNull() ?: 0.0

        if (name.isBlank()) return

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val newProduct = Product(productName = name, stockQty = qty, price = price)
                repository.addProduct(newProduct)
                _uiState.update { it.copy(nameInput = "", qtyInput = "", priceInput = "") }
                loadProducts()
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message, isLoading = false) }
            }
        }
    }

    fun updateStock(id: Long?, currentQty: Int, delta: Int) {
        if (id == null) return
        val newQty = (currentQty + delta).coerceAtLeast(0)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.updateStock(id, newQty)
                loadProducts()
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message) }
            }
        }
    }

    fun deleteProduct(id: Long?) {
        if (id == null) return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.deleteProduct(id)
                loadProducts()
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message) }
            }
        }
    }
}