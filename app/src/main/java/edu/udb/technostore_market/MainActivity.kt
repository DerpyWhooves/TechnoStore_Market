package edu.udb.technostore_market

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import edu.udb.technostore_market.ui.TechnoStoreRoot
import edu.udb.technostore_market.ui.theme.TechnoStore_MarketTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TechnoStore_MarketTheme {
                TechnoStoreRoot()
            }
        }
    }
}
