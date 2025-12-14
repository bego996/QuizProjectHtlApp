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
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.quizapp.BottomNavigationBar
import com.app.quizapp.QuizTopAppBar
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
    val userId: Int,
    val name: String,
    val birthDate: String,
    val email: String,
    val role: UserRole,
    val isActive: Boolean
)

/**
 * Admin screen for viewing and managing users
 * Integrates with UsersScreenViewModel to load real user data
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersScreen(
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onDiscoverClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    viewModel: UsersScreenViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Map domain User to UI UserItem
    val users = uiState.users.map { user ->
        UserItem(
            userId = user.userId,
            name = "${user.firstname} ${user.surname}",
            birthDate = user.birthdate,
            email = user.email,
            role = if (user.userRole.userRole == "admin") UserRole.ADMIN else UserRole.USER,
            isActive = true // TODO: Add active status to User model if needed
        )
    }

    Scaffold(
        topBar = {
            QuizTopAppBar(
                modifier = Modifier,
                title = stringResource(UsersDestination.titleRes),
                canNavigateBack = true,
                navigateUp = onBackClick
            )
        },
        bottomBar = {
            BottomNavigationBar(
                onHomeClick = onHomeClick,
                onDiscoverClick = onDiscoverClick,
                onProfileClick = onProfileClick
            )
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
                    onUserDeleteClick = { viewModel.deleteUser(user.userId) }
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
    onUserDeleteClick: () -> Unit
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
                Button(
                    onClick = onUserDeleteClick
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "User status",
                        tint = if (user.isActive) Color(0xFF2E7D32) else Color(0xFFD32F2F),
                        modifier = Modifier.size(24.dp),
                    )
                }

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
