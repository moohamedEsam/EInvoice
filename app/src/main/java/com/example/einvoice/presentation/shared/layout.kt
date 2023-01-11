package com.example.einvoice.presentation.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.auth.login.LoginScreenRoute
import com.example.branch.screens.all.BranchesScreenRoute
import com.example.branch.screens.all.navigateToBranchesScreen
import com.example.client.screens.all.ClientsScreenRoute
import com.example.client.screens.all.navigateToClientsScreen
import com.example.common.models.SnackBarEvent
import com.example.company.screen.all.CompaniesScreenRoute
import com.example.company.screen.all.navigateToCompaniesScreen
import com.example.document.screens.all.DocumentsScreenRoute
import com.example.document.screens.all.navigateToDocumentsScreen
import com.example.einvoice.R
import com.example.einvoice.presentation.settings.SettingsScreenRoute
import com.example.einvoice.presentation.settings.navigateToSettingsScreen
import com.example.functions.handleSnackBarEvent
import com.example.item.screens.all.ItemsScreenRoute
import com.example.item.screens.all.navigateToItemsScreen
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.viewModel
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun EInvoiceLayout(startScreen: String) {
    val viewModel: LayoutViewModel by viewModel()
    val snackbarHostState by remember {
        mutableStateOf(SnackbarHostState())
    }
    observeSnackBarEventChannel(viewModel, snackbarHostState)
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .semantics { testTagsAsResourceId = true },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) {
                DraggableSnackBar(it, snackbarHostState)
            }
        },
        content = {
            EInvoiceDrawer(
                navController = navController,
                paddingValues = it,
                startScreen = startScreen,
                drawerState = drawerState
            )
        },
        topBar = {
            EInvoiceTopBar(
                navController = navController,
                drawerState = drawerState,
                viewModel = viewModel
            )
        }
    )
}

@Composable
private fun observeSnackBarEventChannel(
    viewModel: LayoutViewModel,
    snackbarHostState: SnackbarHostState
) {
    LaunchedEffect(key1 = Unit) {
        viewModel.getReceiverChannel().collectLatest {
            snackbarHostState.handleSnackBarEvent(it)
        }
    }
}

@Composable
private fun DraggableSnackBar(
    snackbarData: SnackbarData,
    snackbarHostState: SnackbarHostState
) {
    Snackbar(
        snackbarData,
        modifier = Modifier
            .draggable(
                orientation = Orientation.Horizontal,
                state = rememberDraggableState(
                    onDelta = { delta ->
                        if (abs(delta) > 20) snackbarHostState.currentSnackbarData?.dismiss()
                    }
                )
            )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EInvoiceDrawer(
    navController: NavHostController,
    paddingValues: PaddingValues,
    startScreen: String,
    modifier: Modifier = Modifier,
    drawerState: DrawerState
) {
    ModalNavigationDrawer(
        drawerContent = { DrawerContent(navController = navController) },
        modifier = modifier.padding(paddingValues),
        drawerState = drawerState,
        gesturesEnabled = false
    ) {
        EInvoiceNavGraph(
            navController = navController,
            startScreen = startScreen
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
fun EInvoiceTopBar(
    navController: NavHostController,
    drawerState: DrawerState,
    viewModel: LayoutViewModel
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: return
    val coroutineScope = rememberCoroutineScope()
    if (currentRoute == LoginScreenRoute) return
    TopAppBar(
        title = {
            Text(text = currentRoute.takeWhile { it != '/' })
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        if (drawerState.isOpen)
                            drawerState.close()
                        else drawerState.open()
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
            val lifecycleOwner = LocalLifecycleOwner.current
            IconButton(onClick = { viewModel.sync(lifecycleOwner) }) {
                Icon(
                    imageVector = Icons.Outlined.Sync,
                    contentDescription = "Sync"
                )
            }

            IconButton(
                onClick = viewModel::logout,
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