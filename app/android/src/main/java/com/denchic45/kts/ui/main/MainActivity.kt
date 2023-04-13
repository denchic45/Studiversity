package com.denchic45.kts.ui.main

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.arkivanov.decompose.defaultComponentContext
import com.bumptech.glide.Glide
import com.denchic45.kts.R
import com.denchic45.kts.app
import com.denchic45.kts.databinding.ActivityMainBinding
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.ui.adapter.NavDropdownItemHolder
import com.denchic45.kts.ui.adapter.NavItemHolder
import com.denchic45.kts.ui.adapter.navAdapter
import com.denchic45.kts.ui.base.BaseActivity
import com.denchic45.kts.ui.get
import com.denchic45.kts.ui.initImageLoader
import com.denchic45.kts.ui.login.LoginActivity
import com.denchic45.kts.ui.updateView.SnackbarUpdateView
import com.denchic45.kts.util.collectWhenResumed
import com.denchic45.kts.util.collectWhenStarted
import com.denchic45.kts.util.findFragmentContainerNavController
import com.denchic45.widget.extendedAdapter.extension.clickBuilder
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce


class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>(R.layout.activity_main) {

    override val binding: ActivityMainBinding by viewBinding(ActivityMainBinding::bind)
    override val viewModel: MainViewModel by viewModels { viewModelFactory }

    //    private lateinit var appBarLayout: AppBarLayout
//    private lateinit var toolbar: CustomToolbar
    private lateinit var toggle: ActionBarDrawerToggle

    private lateinit var snackbar: Snackbar

    private lateinit var bnv: BottomNavigationView
    private val navAdapter = navAdapter {
        extensions {
            clickBuilder<NavItemHolder> {
                onClick = {
                    binding.drawerLayout.close()
                    viewModel.onNavItemClick(it)
                }
            }
            clickBuilder<NavDropdownItemHolder> {
                onClick = {
                    viewModel.onExpandCoursesClick()
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        app.appComponent.componentContext = defaultComponentContext()
        initImageLoader(this)
        viewModel.setActivityForService(this)
        snackbar = Snackbar.make(this, binding.root, "", Snackbar.LENGTH_INDEFINITE)
        val snackbarUpdateView = SnackbarUpdateView(this)
        snackbar.view.setBackgroundColor(Color.TRANSPARENT)
        val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout
        snackbarLayout.setPadding(0, 0, 0, 0)
        snackbarLayout.addView(snackbarUpdateView, 0)

        snackbarUpdateView.onDownloadClickListener = { viewModel.onDownloadUpdateClick() }
        snackbarUpdateView.onLaterClickListener = { viewModel.onLaterUpdateClick() }
        snackbarUpdateView.onInstallClickListener = { viewModel.onInstallClick() }

        bnv = findViewById(R.id.bottom_nav_view)
//        toolbar = findViewById(R.id.toolbar_main)

//        appBarLayout = findViewById(R.id.app_bar)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
//        AppBarController.create(this, appBarLayout)
//        setSupportActionBar(toolbar)
        setupWithNavController(navigationView, navController)
        setupWithNavController(bnv, navController)

        bnv.setOnItemReselectedListener { refreshCurrentFragment() }

        val toolbarInteractor = app.appComponent.toolbarInteractor

        toolbarInteractor.titleFlow.collectWhenStarted(this) {
            title = it.get(this)
        }

        binding.topAppBarComposable.setContent {
            MaterialTheme {
                val title by toolbarInteractor.titleFlow.collectAsState()
                val dropdown by toolbarInteractor.dropdown.collectAsState()
                TopAppBar(
                    title = { Text(title.get(LocalContext.current)) },
                    actions = {
                        if (dropdown.isNotEmpty())
                            IconButton(onClick = { }) {
                                Icon(Icons.Filled.MoreVert, "Меню")
                            }

                        var menuExpanded by remember { mutableStateOf(false) }

                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false },
                        ) {
                            dropdown.forEach { item ->
                                DropdownMenuItem(
                                    text = {
                                        Text(item.title.get(LocalContext.current))
                                    },
                                    onClick = { /* TODO */ },
                                )
                            }
                        }
                    }
                )
            }
        }

        viewModel.updateBannerState.debounce(1000)
            .collectWhenResumed(this) { bannerState ->
                if (bannerState !is MainViewModel.UpdateBannerState.Hidden)
                    snackbar.show()
                else {
                    snackbar.dismiss()
                    return@collectWhenResumed
                }

                when (bannerState) {
                    MainViewModel.UpdateBannerState.Hidden -> snackbar.dismiss()
                    MainViewModel.UpdateBannerState.Remind -> {
                        snackbarUpdateView.showState(
                            SnackbarUpdateView.UpdateState.REMIND
                        )
                    }
                    is MainViewModel.UpdateBannerState.WaitLoading -> {
                        snackbarUpdateView.showState(SnackbarUpdateView.UpdateState.LOADING)
                        snackbarUpdateView.indeterminate(true)
                    }
                    is MainViewModel.UpdateBannerState.Loading -> {
                        snackbarUpdateView.indeterminate(false)
                        snackbarUpdateView.showState(SnackbarUpdateView.UpdateState.LOADING)
                        snackbarUpdateView.updateLoadingProgress(
                            bannerState.progress,
                            bannerState.info
                        )
                    }
                    MainViewModel.UpdateBannerState.Install -> {
                        snackbarUpdateView.showState(
                            SnackbarUpdateView.UpdateState.INSTALL
                        )
                    }
                }
            }

        viewModel.menuBtnVisibility.collectWhenStarted(this) { (itemId, visibility) ->
            bnv.menu.findItem(itemId).isVisible = visibility
        }

        with(binding) {
            rvNav.adapter = navAdapter
            viewModel.bottomMenuVisibility.observe(this@MainActivity) {
                bnv.visibility = if (it) View.VISIBLE else View.GONE
            }
            viewModel.fabVisibility.collectWhenStarted(this@MainActivity) {
                if (it) fabMain.show() else fabMain.hide()
            }



            viewModel.toolbarNavigationState.collectWhenResumed(this@MainActivity) {
                toggle = ActionBarDrawerToggle(
                    this@MainActivity,
                    drawerLayout,
                    R.string.navigation_drawer_open,
                    R.string.navigation_drawer_close
                )
                when (it) {
                    MainViewModel.ToolbarNavigationState.MENU -> {
                        drawerLayout.addDrawerListener(toggle)
                        toggle.isDrawerIndicatorEnabled = true
                        toggle.syncState()
                    }
                    MainViewModel.ToolbarNavigationState.BACK -> {
                        toggle.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
                        toggle.isDrawerIndicatorEnabled = false
                        toggle.syncState()
                    }
                    else -> {}
                }

                toggle.toolbarNavigationClickListener = View.OnClickListener {
                    onBackPressed()
                }
            }

            viewModel.userInfo.collectWhenStarted(this@MainActivity) { user ->
                user.onSuccess {
                    val headerView = binding.navHeader.root
                    headerView.setOnClickListener {
                        binding.drawerLayout.close()
                        viewModel.onProfileClick()
                    }
                    Glide.with(this@MainActivity)
                        .load(it.avatarUrl)
                        .into(binding.navHeader.ivAvatar)
                    binding.navHeader.tvFullName.text = it.fullName
                }
            }

            viewModel.navMenuItems.collectWhenStarted(this@MainActivity) {
                if (it is MainViewModel.NavMenuState.NavMenu) {
                    navAdapter.submit(it.items)
                }
            }

            navController.addOnDestinationChangedListener { _, destination, _ ->
                viewModel.onDestinationChanged(destination.id)
            }
        }

        viewModel.openLogin.collectWhenStarted(this) {
            finish()
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        }

        viewModel.goBack.collectWhenStarted(this) {
            navController.navigateUp()
        }
    }

    override val navController: NavController by lazy {
        findFragmentContainerNavController(R.id.nav_host_fragment)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        viewModel.onOptionItemSelect(item.itemId)
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
//        toast(BuildConfig.VERSION_CODE.toString())
    }

    private fun refreshCurrentFragment() {
        val ids = setOf(R.id.menu_timetable, R.id.menu_group)
        var id = navController.currentDestination!!.id
        navController.popBackStack(id, true)
        while (!ids.contains(id)) {
            id = navController.currentDestination!!.id
            navController.popBackStack(id, true)
        }
        navController.navigate(id)
    }
}