package com.example.communitystockapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.communitystockapp.ui.ProductScreen
import com.example.communitystockapp.ui.theme.CommunityStockAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CommunityStockAppTheme {
                ProductScreen()
            }
        }
    }
}