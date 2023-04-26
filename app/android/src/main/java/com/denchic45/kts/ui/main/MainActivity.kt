package com.denchic45.kts.ui.main

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.updateLayoutParams
import androidx.navigation.NavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.denchic45.kts.R
import com.denchic45.kts.app
import com.denchic45.kts.databinding.ActivityMainBinding
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.ui.appbar.NavigationIcon
import com.denchic45.kts.ui.base.BaseActivity
import com.denchic45.kts.ui.confirm.ConfirmDialog
import com.denchic45.kts.ui.get
import com.denchic45.kts.ui.getPainter
import com.denchic45.kts.ui.initImageLoader
import com.denchic45.kts.ui.login.LoginActivity
import com.denchic45.kts.ui.theme.AppTheme
import com.denchic45.kts.ui.updateView.SnackbarUpdateView
import com.denchic45.kts.util.collectWhenResumed
import com.denchic45.kts.util.collectWhenStarted
import com.denchic45.kts.util.dpToPx
import com.denchic45.kts.util.findFragmentContainerNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.seiko.imageloader.rememberAsyncImagePainter
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update


class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>(R.layout.activity_main) {

    override val binding: ActivityMainBinding by viewBinding(ActivityMainBinding::bind)
    override val viewModel: MainViewModel by viewModels { viewModelFactory }

    //    private lateinit var appBarLayout: AppBarLayout
//    private lateinit var toolbar: CustomToolbar
    private lateinit var toggle: ActionBarDrawerToggle

    private lateinit var snackbar: Snackbar

    private lateinit var bnv: BottomNavigationView

//    private val navAdapter = navAdapter {
//        extensions {
//            clickBuilder<NavItemHolder> {
//                onClick = {
//                    binding.drawerLayout.close()
//                    viewModel.onNavItemClick(it)
//                }
//            }
//            clickBuilder<NavDropdownItemHolder> {
//                onClick = {
//                    viewModel.onExpandCoursesClick()
//                }
//            }
//        }
//    }

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

        val appBarInteractor = app.appComponent.appBarInteractor
        val fabInteractor = app.appComponent.fabInteractor
        val confirmInteractor = app.appComponent.confirmInteractor

//        toolbarInteractor.appBarState.collectWhenStarted(this) {
//            title = it.title.get(this)
//        }

        appBarInteractor.stateFlow.collectWhenStarted(this) {
            if (it.visible) {
                binding.appBar.updateLayoutParams<CoordinatorLayout.LayoutParams> {
                    height = 56.dpToPx
                }
            } else {
                binding.appBar.updateLayoutParams<CoordinatorLayout.LayoutParams> { height = 0 }
            }
        }

        binding.topAppBarComposable.setContent {
            AppTheme {
                val state by appBarInteractor.stateFlow.collectAsState()

                if (state.visible)
                    TopAppBar(
                        title = { Text(state.title.get(LocalContext.current)) },
                        navigationIcon = {
                            val icon by appBarInteractor.navigationIcon.collectAsState()

                            when (icon) {
                                NavigationIcon.TOGGLE -> IconButton(
                                    onClick = { binding.drawerLayout.open() }) {
                                    Icon(
                                        imageVector = Icons.Outlined.Menu,
                                        contentDescription = "menu"
                                    )
                                }

                                NavigationIcon.BACK -> IconButton(
                                    onClick = { onBackPressedDispatcher.onBackPressed() }) {
                                    Icon(
                                        imageVector = Icons.Outlined.ArrowBack,
                                        contentDescription = "back"
                                    )
                                }
                            }
                        },
                        actions = {
                            state.actions.forEach { actionMenuItem ->
                                val contentDescription = actionMenuItem.title
                                    ?.get(LocalContext.current)
                                IconButton(
                                    onClick = { actionMenuItem.onClick() },
                                    enabled = actionMenuItem.enabled
                                ) {
                                    Icon(
                                        painter = actionMenuItem.icon.getPainter(),
                                        contentDescription = contentDescription
                                    )
                                }
                            }

                            if (state.dropdown.isNotEmpty()) {
                                var menuExpanded by remember { mutableStateOf(false) }
                                IconButton(onClick = { menuExpanded = !menuExpanded }) {
                                    Icon(Icons.Filled.MoreVert, "Меню")
                                }
                                DropdownMenu(
                                    expanded = menuExpanded,
                                    offset = DpOffset(x = (-84).dp, y = 0.dp),
                                    onDismissRequest = { menuExpanded = false },
                                ) {
                                    state.dropdown.forEach { item ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(item.title.get(LocalContext.current))
                                            },
                                            onClick = {
                                                menuExpanded = false
                                                item.onClick()
                                                state.onDropdownMenuItemClick(item)
                                            },
                                        )
                                    }
                                }
                            }
                        }
                    )


                ConfirmDialog(confirmInteractor)
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
//            rvNav.adapter = navAdapter
            viewModel.bottomMenuVisibility.observe(this@MainActivity) {
                bnv.visibility = if (it) View.VISIBLE else View.GONE
            }

            fabCompose.apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {
                    AppTheme {
                        val state by fabInteractor.stateFlow.collectAsState()
                        if (state.visible) {
                            FloatingActionButton(onClick = { state.onClick() }) {
                                state.icon?.let {
                                    Icon(
                                        painter = it.getPainter(),
                                        contentDescription = "fab"
                                    )
                                }
                            }
                        }
                    }
                }
            }

            navCompose.apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {
                    AppTheme {
                        Surface {
                            Column {
                                val userInfo by viewModel.userInfo.collectAsState()
                                Row(
                                    Modifier
                                        .clickable {
                                            binding.drawerLayout.close()
                                            viewModel.onProfileClick()
                                        }
                                        .fillMaxWidth()
                                        .height(84.dp)
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    userInfo.onSuccess { info ->
                                        Image(
                                            painter = rememberAsyncImagePainter(info.avatarUrl),
                                            contentDescription = "user avatar",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(CircleShape),
                                        )
                                        Spacer(Modifier.width(16.dp))
                                        Text(
                                            text = info.fullName,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                    }
                                }
                                Divider(Modifier.padding(vertical = 4.dp))
                                val navMenu by viewModel.navMenuState.collectAsState()
                                Column(Modifier.padding(8.dp)) {
                                    navMenu.topItems.forEach {
                                        NavigationDrawerItem(
                                            label = {
                                                Text(
                                                    it.name.get(LocalContext.current),
                                                    style = MaterialTheme.typography.labelLarge
                                                )
                                            },
                                            icon = {
                                                Icon(
                                                    painter = it.icon.getPainter(),
                                                    contentDescription = null
                                                )
                                            },
                                            selected = false,
                                            onClick = {
                                                binding.drawerLayout.close()
                                                viewModel.onTopNavItemClick(it.name)
                                            })
                                    }
                                }
                                Divider(Modifier.padding(vertical = 4.dp))
                                if (navMenu.courses.isNotEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp)
                                            .padding(horizontal = 28.dp),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        Text(
                                            text = "Курсы",
                                            style = MaterialTheme.typography.titleSmall
                                        )
                                    }

                                    Column(Modifier.padding(8.dp)) {
                                        navMenu.courses.forEach {
                                            NavigationDrawerItem(
                                                label = {
                                                    Text(
                                                        it.name,
                                                        style = MaterialTheme.typography.labelLarge
                                                    )
                                                },
                                                icon = {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(36.dp)
                                                            .clip(CircleShape)
                                                            .background(colorResource(R.color.blue))
                                                            .padding(8.dp),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text(
                                                            text = it.name.first().uppercase(),
                                                            style = MaterialTheme.typography.labelLarge,
                                                            color = androidx.compose.ui.graphics.Color.White
                                                        )
                                                    }
                                                },
                                                selected = false,
                                                onClick = {
                                                    binding.drawerLayout.close()
                                                    viewModel.onCourseClick(it.id)
                                                })
                                        }
                                    }
                                    Divider(Modifier.padding(vertical = 4.dp))
                                }

                                Column(Modifier.padding(8.dp)) {
                                    navMenu.footerItems.forEach {
                                        NavigationDrawerItem(
                                            label = {
                                                Text(
                                                    it.name.get(LocalContext.current),
                                                    style = MaterialTheme.typography.labelLarge
                                                )
                                            },
                                            icon = {
                                                Icon(
                                                    painter = it.icon.getPainter(),
                                                    contentDescription = null
                                                )
                                            },
                                            selected = false,
                                            onClick = {
                                                binding.drawerLayout.close()
                                                viewModel.onFooterNavItemClick(it.name)
                                            })
                                    }
                                }
                            }
                        }
                    }
                }
            }


//            viewModel.fabVisibility.collectWhenStarted(this@MainActivity) {
//                if (it) fabMain.show() else fabMain.hide()
//            }

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

//            viewModel.userInfo.collectWhenStarted(this@MainActivity) { user ->
//                user.onSuccess {
//                    val headerView = binding.navHeader.root
//                    headerView.setOnClickListener {
//                        binding.drawerLayout.close()
//                        viewModel.onProfileClick()
//                    }
//                    Glide.with(this@MainActivity)
//                        .load(it.avatarUrl)
//                        .into(binding.navHeader.ivAvatar)
//                    binding.navHeader.tvFullName.text = it.fullName
//                }
//            }

            navController.addOnDestinationChangedListener { _, destination, _ ->
                viewModel.onDestinationChanged(destination.id)


                appBarInteractor.navigationIcon.update {
                    if (destination.id in viewModel.mainScreenIds)
                        NavigationIcon.TOGGLE
                    else NavigationIcon.BACK
                }
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