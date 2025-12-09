package com.app.quizapp.presentation.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.quizapp.R
import com.app.quizapp.navigation.NavigationDestination

object GenerateQuizzesDestination : NavigationDestination {
    override val route: String = "generate_quizzes"
    override val titleRes: Int = R.string.generate_quizzes
}

/**
 * Admin screen for generating quizzes and topics
 */
@Composable
fun GenerateQuizzesScreen(
    onBackClick: () -> Unit = {},
    onApplyQuizClick: () -> Unit = {},
    onSeeResultsClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onDiscoverClick: () -> Unit = {},
    onAdminClick: () -> Unit = {}
) {
    var isQuizMode by remember { mutableStateOf(true) } // true = Quiz erstellen, false = Thema erstellen
    var questionText by remember { mutableStateOf("Was ist die Hauptstadt von Deutschland?") }
    var selectedTopics by remember { mutableStateOf(listOf("Hamburg", "München", "Tönig")) }
    var selectedDifficulty by remember { mutableStateOf<String?>(null) }

    val difficulties = listOf("Easy", "Medium", "Hard", "Experte")

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
                    text = "Generate Quizzes",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFB0E5E0))
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Toggle buttons for Quiz/Thema creation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { isQuizMode = true },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isQuizMode) Color(0xFFFF8F00) else Color(0xFFE0E0E0)
                    )
                ) {
                    Text(
                        text = "Quiz erstellen",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isQuizMode) Color.White else Color(0xFF9E9E9E)
                    )
                }
                Button(
                    onClick = { isQuizMode = false },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!isQuizMode) Color(0xFFFF8F00) else Color(0xFFE0E0E0)
                    )
                ) {
                    Text(
                        text = "Thema erstellen",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (!isQuizMode) Color.White else Color(0xFF9E9E9E)
                    )
                }
            }

            // Question text field
            OutlinedTextField(
                value = questionText,
                onValueChange = { questionText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color(0xFF00695C),
                    unfocusedBorderColor = Color.Transparent
                )
            )

            // Topic selection section
            Text(
                text = "Wähle ein Thema:",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF654321),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Topic chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                selectedTopics.forEach { topic ->
                    Surface(
                        modifier = Modifier,
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00695C))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = topic,
                                fontSize = 14.sp,
                                color = Color(0xFF654321)
                            )
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Remove",
                                tint = Color(0xFFD32F2F),
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable {
                                        selectedTopics = selectedTopics.filter { it != topic }
                                    }
                            )
                        }
                    }
                }
            }

            // Difficulty selection section
            Text(
                text = "Wähle die Schwierigkeit:",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF654321),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Difficulty chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                difficulties.forEach { difficulty ->
                    Surface(
                        modifier = Modifier.clickable {
                            selectedDifficulty = if (selectedDifficulty == difficulty) null else difficulty
                        },
                        shape = RoundedCornerShape(20.dp),
                        color = if (selectedDifficulty == difficulty) Color(0xFF00695C) else Color.White,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (selectedDifficulty == difficulty) Color(0xFF00695C) else Color(0xFFE0E0E0)
                        )
                    ) {
                        Text(
                            text = difficulty,
                            fontSize = 14.sp,
                            color = if (selectedDifficulty == difficulty) Color.White else Color(0xFF654321),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Apply Quiz button
            Button(
                onClick = onApplyQuizClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E7D32)
                )
            ) {
                Text(
                    text = "Apply Quiz",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // See Results button
            OutlinedButton(
                onClick = onSeeResultsClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color(0xFF00ACC1)
                ),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF00ACC1))
            ) {
                Text(
                    text = "See Results",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
