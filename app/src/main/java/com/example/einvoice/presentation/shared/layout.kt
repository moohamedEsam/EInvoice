package com.example.einvoice.presentation.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.auth.login.LoginScreenRoute
import com.example.auth.login.navigateToLoginScreen
import com.example.branch.screens.all.BranchesScreenRoute
import com.example.branch.screens.all.navigateToBranchesScreen
import com.example.client.screens.all.ClientsScreenRoute
import com.example.client.screens.all.navigateToClientsScreen
import com.example.common.models.SnackBarEvent
import com.example.company.screen.all.CompaniesScreenRoute
import com.example.company.screen.all.navigateToCompaniesScreen
import com.example.document.screens.all.DocumentsScreenRoute
import com.example.document.screens.all.navigateToDocumentsScreen
import com.example.domain.auth.LogoutUseCase
import com.example.domain.sync.OneTimeSyncUseCase
import com.example.einvoice.R
import com.example.einvoice.presentation.settings.SettingsScreenRoute
import com.example.einvoice.presentation.settings.navigateToSettingsScreen
import com.example.functions.handleSnackBarEvent
import com.example.item.screens.all.ItemsScreenRoute
import com.example.item.screens.all.navigateToItemsScreen
import kotlinx.coroutines.launch
import org.koin.androidx.compose.inject

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
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
        modifier = Modifier.fillMaxSize().semantics { testTagsAsResourceId = true },
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
        val iconSize = 24
        NavigationDrawerItem(
            icon = {
                Icon(
                    painterResource(id = R.drawable.office_building),
                    contentDescription = null,
                    modifier = Modifier.size(iconSize.dp)
                )
            },
            label = { Text("Companies") },
            selected = currentRoute == CompaniesScreenRoute,
            onClick = navController::navigateToCompaniesScreen
        )

        NavigationDrawerItem(
            icon = {
                Icon(
                    painterResource(id = R.drawable.branch),
                    contentDescription = null,
                    modifier = Modifier.size(iconSize.dp)
                )
            },
            label = { Text("Branches") },
            selected = currentRoute == BranchesScreenRoute,
            onClick = navController::navigateToBranchesScreen
        )

        NavigationDrawerItem(
            icon = {
                Icon(
                    painterResource(id = R.drawable.rating),
                    contentDescription = null,
                    modifier = Modifier.size(iconSize.dp)
                )
            },
            label = { Text("Clients") },
            selected = currentRoute == ClientsScreenRoute,
            onClick = navController::navigateToClientsScreen
        )

        NavigationDrawerItem(
            icon = {
                Icon(
                    painterResource(id = R.drawable.list_tems),
                    contentDescription = null,
                    modifier = Modifier.size(iconSize.dp)
                )
            },
            label = { Text("Items") },
            selected = currentRoute == ItemsScreenRoute,
            onClick = navController::navigateToItemsScreen
        )

        NavigationDrawerItem(
            icon = {
                Icon(
                    painterResource(id = R.drawable.invoice),
                    contentDescription = null,
                    modifier = Modifier.size(iconSize.dp)
                )
            },
            label = { Text("Documents") },
            selected = currentRoute == DocumentsScreenRoute,
            onClick = navController::navigateToDocumentsScreen
        )
        NavigationDrawerItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null
                )
            },
            label = { Text("Settings") },
            selected = currentRoute == SettingsScreenRoute,
            onClick = navController::navigateToSettingsScreen
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EInvoiceTopBar(navController: NavHostController, drawerState: DrawerState) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: return
    val coroutine = rememberCoroutineScope()
    val logoutUseCase: LogoutUseCase by inject()
    val syncUseCase: OneTimeSyncUseCase by inject()
    if (currentRoute == LoginScreenRoute) return
    TopAppBar(
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

            IconButton(onClick = {
                syncUseCase()
            }) {
                Icon(
                    imageVector = Icons.Outlined.Sync,
                    contentDescription = "Sync"
                )
            }

            IconButton(
                onClick = {
                    coroutine.launch {
                        val result = logoutUseCase()
                        result.ifSuccess {
                            navController.navigateToLoginScreen()
                        }
                    }
                },
                modifier = Modifier.testTag("Logout")
            ) {
                Icon(
                    imageVector = Icons.Outlined.Logout,
                    contentDescription = "Logout"
                )
            }
        }
    )
}