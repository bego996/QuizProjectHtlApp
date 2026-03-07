package com.app.quizapp

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.app.quizapp.navigation.MettingNavHost


private const val TAG = "NotificationApp"

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun QuizToGoApp(
    navController: NavHostController = rememberNavController()
) {
    MettingNavHost(navController = navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizTopAppBar(
    title: String,
    canNavigateBack: Boolean,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    navigateUp: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = { Text(text = title, fontWeight = FontWeight.Bold) },
        modifier = modifier.wrapContentSize(Alignment.Center),
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color(0xFF00ACC1),
            titleContentColor = Color(0xFFD5CCCC),
            navigationIconContentColor = Color.White,
            ),
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "back",
                        modifier = modifier.size(35.dp)
                    )
                }
            }
        },
        actions = actions
    )
}

private val homeRoutes    = setOf("home")
private val discoverRoutes = setOf("categories", "topics", "subtopics")
private val profileRoutes  = setOf("profile_overview", "profile_statistics", "edit_profile", "settings")

/**
 * Bottom navigation bar with Home, Discover, and Profile tabs.
 * @param currentRoute the active route string to highlight the correct tab
 */
@Composable
fun BottomNavigationBar(
    currentRoute: String? = null,
    onHomeClick: () -> Unit,
    onDiscoverClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Surface(
        color = Color(0xFF00ACC1),
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Home",
                    modifier = Modifier.size(25.dp)
                )
            },
            label = { Text("Home", fontSize = 12.sp) },
            selected = currentRoute in homeRoutes,
            onClick = onHomeClick,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = Color(0xFF0D434B),
                selectedTextColor = Color.White,
                unselectedTextColor = Color(0xFF004D56),
                indicatorColor = Color(0xFF00796B)
            )
        )

        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Discover",
                    modifier = Modifier.size(25.dp)
                )
            },
            label = { Text("Discover", fontSize = 12.sp) },
            selected = currentRoute in discoverRoutes,
            onClick = onDiscoverClick,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = Color(0xFF004D56),
                selectedTextColor = Color.White,
                unselectedTextColor = Color(0xFF004D56),
                indicatorColor = Color(0xFF00796B)
            )
        )

        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Profile",
                    modifier = Modifier.size(25.dp)
                )
            },
            label = { Text("Profile", fontSize = 12.sp) },
            selected = currentRoute in profileRoutes,
            onClick = onProfileClick,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = Color(0xFF004D56),
                selectedTextColor = Color.White,
                unselectedTextColor = Color(0xFF004D56),
                indicatorColor = Color(0xFF00796B)
            )
        )
        }
    }
}