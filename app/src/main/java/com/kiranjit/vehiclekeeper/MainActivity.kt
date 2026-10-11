package com.kiranjit.vehiclekeeper

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kiranjit.vehiclekeeper.ui.DetailScreen
import com.kiranjit.vehiclekeeper.ui.EditScreen
import com.kiranjit.vehiclekeeper.ui.VehicleListScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface { AppNav() }
            }
        }
    }
}

@Composable
fun AppNav(vm: VehicleViewModel = viewModel()) {
    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= 33) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val nav = rememberNavController()
    val idArg = listOf(navArgument("id") { type = NavType.LongType })

    NavHost(nav, startDestination = "list") {
        composable("list") {
            VehicleListScreen(vm, onAdd = { nav.navigate("edit/0") }, onOpen = { nav.navigate("detail/$it") })
        }
        composable("detail/{id}", arguments = idArg) { e ->
            DetailScreen(
                vm, e.arguments!!.getLong("id"),
                onBack = { nav.popBackStack() },
                onEdit = { nav.navigate("edit/$it") }
            )
        }
        composable("edit/{id}", arguments = idArg) { e ->
            EditScreen(vm, e.arguments!!.getLong("id"), onDone = { nav.popBackStack() })
        }
    }
}
