package com.example.einvoice.presentation.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.branch.screens.all.navigateToBranchesScreen
import com.example.branch.screens.form.BranchFormScreenRoute
import com.example.common.models.SnackBarEvent
import com.example.company.screen.all.CompaniesScreenRoute
import com.example.company.screen.all.navigateToCompaniesScreen
import com.example.functions.handleSnackBarEvent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EInvoiceLayout(startScreen: String) {
    val snackbarHostState by remember {
        mutableStateOf(SnackbarHostState())
    }
    val coroutineScope = rememberCoroutineScope()
    val onShowSnackbarEvent: (SnackBarEvent) -> Unit = {
        coroutineScope.launch {
            snackbarHostState.handleSnackBarEvent(it)
        }
    }
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        content = {
            EInvoiceDrawer(
                navController = navController,
                paddingValues = it,
                startScreen = startScreen,
                drawerState = drawerState,
                onShowSnackbarEvent = onShowSnackbarEvent
            )
        },
        topBar = {
            EInvoiceTopBar(
                navController = navController,
                drawerState = drawerState,
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EInvoiceDrawer(
    navController: NavHostController,
    paddingValues: PaddingValues,
    startScreen: String,
    modifier: Modifier = Modifier,
    drawerState: DrawerState,
    onShowSnackbarEvent: (SnackBarEvent) -> Unit
) {
    ModalNavigationDrawer(
        drawerContent = { DrawerContent(navController = navController) },
        modifier = modifier.padding(paddingValues),
        drawerState = drawerState,
        gesturesEnabled = false
    ) {
        EInvoiceNavGraph(
            navController = navController,
            startScreen = startScreen,
            onShowSnackbarEvent = onShowSnackbarEvent
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DrawerContent(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.8f)
            .background(MaterialTheme.colorScheme.surface),

        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        NavigationDrawerItem(
            icon = { Icon(Icons.Outlined.Home, contentDescription = null) },
            label = { Text("Home") },
            selected = currentRoute == CompaniesScreenRoute,
            onClick = navController::navigateToCompaniesScreen
        )

        NavigationDrawerItem(
            icon = { Icon(Icons.Outlined.Home, contentDescription = null) },
            label = { Text("Branches") },
            selected = currentRoute == BranchFormScreenRoute,
            onClick = navController::navigateToBranchesScreen
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EInvoiceTopBar(navController: NavHostController, drawerState: DrawerState) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: return
    val coroutine = rememberCoroutineScope()
    CenterAlignedTopAppBar(
        title = {
            Text(text = currentRoute.takeWhile { it != '/' })
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    coroutine.launch {
                        if (drawerState.isOpen)
                            drawerState.close()
                        else
                            drawerState.open()
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Menu,
                    contentDescription = "Toggle Drawer"
                )
            }

        },
        actions = {
            IconButton(
                onClick = {
                    if (navController.backQueue.isNotEmpty()) {
                        navController.popBackStack()
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }
    )
}
