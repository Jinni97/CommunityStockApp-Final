package com.example.communitystockapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.communitystockapp.data.Product

@Composable
fun ProductScreen(viewModel: ProductViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    ProductContent(
        uiState = uiState,
        onNameChange = viewModel::onNameChange,
        onQtyChange = viewModel::onQtyChange,
        onPriceChange = viewModel::onPriceChange,
        onAddClick = viewModel::addProduct,
        onStockUpdate = viewModel::updateStock,
        onDeleteClick = viewModel::deleteProduct
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductContent(
    uiState: ProductUiState,
    onNameChange: (String) -> Unit,
    onQtyChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onAddClick: () -> Unit,
    onStockUpdate: (Long?, Int, Int) -> Unit,
    onDeleteClick: (Long?) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("ระบบจัดการสต็อกสินค้าชุมชน") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("เพิ่มสินค้าใหม่", fontWeight = FontWeight.Bold, fontSize = 18.sp)

                    OutlinedTextField(
                        value = uiState.nameInput,
                        onValueChange = onNameChange,
                        label = { Text("ชื่อสินค้า") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = uiState.qtyInput,
                            onValueChange = onQtyChange,
                            label = { Text("จำนวน (ชิ้น)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = uiState.priceInput,
                            onValueChange = onPriceChange,
                            label = { Text("ราคา (บาท)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Button(
                        onClick = onAddClick,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isLoading
                    ) {
                        Text("บันทึกสินค้าขึ้น Cloud")
                    }
                }
            }

            if (uiState.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            uiState.errorMessage?.let { error ->
                Text(text = error, color = Color.Red, fontSize = 14.sp)
            }

            Text("รายการสินค้าในระบบ", fontWeight = FontWeight.Bold, fontSize = 18.sp)

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(uiState.products, key = { it.id ?: 0 }) { product ->
                    ProductItemCard(
                        product = product,
                        onStockUpdate = onStockUpdate,
                        onDeleteClick = onDeleteClick
                    )
                }
            }
        }
    }
}

@Composable
fun ProductItemCard(
    product: Product,
    onStockUpdate: (Long?, Int, Int) -> Unit,
    onDeleteClick: (Long?) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(product.productName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("ราคา: B ${String.format("%.2f", product.price)}", color = Color.Gray)
                Text("คงเหลือ: ${product.stockQty} ชิ้น", fontWeight = FontWeight.SemiBold)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(
                    onClick = { onStockUpdate(product.id, product.stockQty, -1) },
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) { Text("-1") }

                Spacer(modifier = Modifier.width(4.dp))

                OutlinedButton(
                    onClick = { onStockUpdate(product.id, product.stockQty, 1) },
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) { Text("+1") }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(onClick = { onDeleteClick(product.id) }) {
                    Icon(Icons.Default.Delete, contentDescription = "ลบสินค้า", tint = Color.Red)
                }
            }
        }
    }
}