package com.app.quizapp.presentation.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.quizapp.R
import com.app.quizapp.navigation.NavigationDestination

object UsersDestination : NavigationDestination {
    override val route: String = "users"
    override val titleRes: Int = R.string.users
}

enum class UserRole {
    USER,
    ADMIN
}

data class UserItem(
    val name: String,
    val birthDate: String,
    val email: String,
    val role: UserRole,
    val isActive: Boolean
)

/**
 * Admin screen for viewing and managing users
 */
@Composable
fun UsersScreen(
    onBackClick: () -> Unit = {},
    onUserClick: (String) -> Unit = {},
    onHomeClick: () -> Unit = {},
    onDiscoverClick: () -> Unit = {},
    onAdminClick: () -> Unit = {}
) {
    // Sample user data - will be replaced with ViewModel data
    val users = remember {
        listOf(
            UserItem(
                name = "Soliane Biga",
                birthDate = "05.12.1994",
                email = "sallie.big@email.com",
                role = UserRole.USER,
                isActive = false
            ),
            UserItem(
                name = "Soliane Biga",
                birthDate = "05.12.1994",
                email = "sallie.big@email.com",
                role = UserRole.ADMIN,
                isActive = true
            ),
            UserItem(
                name = "Soliane Biga",
                birthDate = "05.12.1994",
                email = "sallie.big@email.com",
                role = UserRole.USER,
                isActive = true
            ),
            UserItem(
                name = "Soliane Biga",
                birthDate = "05.12.1994",
                email = "sallie.big@email.com",
                role = UserRole.USER,
                isActive = false
            ),
            UserItem(
                name = "Soliane Biga",
                birthDate = "05.12.1994",
                email = "sallie.big@email.com",
                role = UserRole.ADMIN,
                isActive = true
            )
        )
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFB0E5E0))
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF654321),
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Users",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF654321)
                )
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF00ACC1),
                modifier = Modifier.height(64.dp)
            ) {
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Home,
                            contentDescription = "Home",
                            tint = Color.White
                        )
                    },
                    label = { Text("Home", color = Color.White, fontSize = 12.sp) },
                    selected = false,
                    onClick = onHomeClick,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        unselectedIconColor = Color.White,
                        indicatorColor = Color(0xFF00838F)
                    )
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Discover",
                            tint = Color.White
                        )
                    },
                    label = { Text("Discover", color = Color.White, fontSize = 12.sp) },
                    selected = false,
                    onClick = onDiscoverClick,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        unselectedIconColor = Color.White,
                        indicatorColor = Color(0xFF00838F)
                    )
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Admin",
                            tint = Color.White
                        )
                    },
                    label = { Text("Admin", color = Color.White, fontSize = 12.sp) },
                    selected = true,
                    onClick = onAdminClick,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        unselectedIconColor = Color.White,
                        indicatorColor = Color(0xFF00838F)
                    )
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFB0E5E0))
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(users) { user ->
                UserItemCard(
                    user = user,
                    onClick = { onUserClick(user.email) }
                )
            }
        }
    }
}

/**
 * Individual user item card with role badge and status icons
 */
@Composable
fun UserItemCard(
    user: UserItem,
    onClick: () -> Unit
) {
    val roleColor = when (user.role) {
        UserRole.USER -> Color(0xFF2E7D32)
        UserRole.ADMIN -> Color(0xFFFF8F00)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF006064)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF00838F)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "User avatar",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // User details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = user.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = user.birthDate,
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = user.email,
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = roleColor
                ) {
                    Text(
                        text = user.role.name.lowercase().replaceFirstChar { it.uppercase() },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // Status icons
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "User status",
                    tint = if (user.isActive) Color(0xFF2E7D32) else Color(0xFFD32F2F),
                    modifier = Modifier.size(24.dp)
                )
                Icon(
                    imageVector = Icons.Filled.Email,
                    contentDescription = "Message",
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
