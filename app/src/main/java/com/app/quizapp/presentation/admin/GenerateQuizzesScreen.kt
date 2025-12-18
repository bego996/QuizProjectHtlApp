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
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.quizapp.BottomNavigationBar
import com.app.quizapp.QuizTopAppBar
import com.app.quizapp.R
import com.app.quizapp.navigation.NavigationDestination

object GenerateQuizzesDestination : NavigationDestination {
    override val route: String = "generate_quizzes"
    override val titleRes: Int = R.string.generate_quizzes
}

/**
 * Admin screen for generating quizzes and topics
 * Integrates with GenerateQuizzesViewModel for AI quiz generation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateQuizzesScreen(
    onBackClick: () -> Unit = {},
    onApplyQuizClick: () -> Unit = {},
    onSeeResultsClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onDiscoverClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    viewModel: GenerateQuizzesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var isQuizMode by remember { mutableStateOf(true) } // true = Quiz erstellen, false = Thema erstellen

    // Use topics and difficulties from ViewModel
    val difficulties = uiState.difficulty

    var selectedTopics by remember { mutableStateOf(listOf("Math","Geography","Informatics")) }
    var selectedSubTopics by remember { mutableStateOf(listOf("Algebra", "Geometry")) }
    var selectedSubSubTopics by remember { mutableStateOf(listOf("Addition", "Subtraction")) }



    Scaffold(
        topBar = {
            QuizTopAppBar(
                modifier = Modifier,
                title = stringResource(GenerateQuizzesDestination.titleRes),
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFB0E5E0))
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Toggle buttons for Quiz/Thema creation

            Row() {
                Button(
                    onClick = { isQuizMode = true },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isQuizMode) Color(0xFFFF8F00) else Color(0xFFE0E0E0)
                    )
                ) {
                    Text(
                        text = "Quiz generieren",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isQuizMode) Color.White else Color(0xFF9E9E9E)
                    )
                }
            }


                    Spacer(modifier = Modifier.weight(1f))


            Row() {
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
            }

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
                // Show selected topic from ViewModel
                uiState.selectedTopic?.let { topic ->
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
                                text = topic.topic,
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
                                        viewModel.selectTopic(topic) // Deselect by selecting same topic
                                    }
                            )
                        }
                    }
                }
            }

            // Topic selection section
            Text(
                text = "Wähle ein Subtopic:",
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
                selectedSubTopics.forEach { topic ->
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
                                        selectedSubTopics = selectedSubTopics.filter { it != topic }
                                    }
                            )
                        }
                    }
                }
            }

            // Topic selection section
            Text(
                text = "Wähle ein SubSUBtopic:",
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
                selectedSubSubTopics.forEach { topic ->
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
                                        selectedSubSubTopics = selectedSubSubTopics.filter { it != topic }
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
                            viewModel.setDifficulty(difficulty)
                        },
                        shape = RoundedCornerShape(20.dp),
                        color = if (uiState.selectedDifficulty == difficulty) Color(0xFF00695C) else Color.White,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (uiState.selectedDifficulty == difficulty) Color(0xFF00695C) else Color(0xFFE0E0E0)
                        )
                    ) {
                        Text(
                            text = difficulty.mode,
                            fontSize = 14.sp,
                            color = if (uiState.selectedDifficulty == difficulty) Color.White else Color(0xFF654321),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
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
