package com.app.quizapp.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import com.app.quizapp.QuizTopAppBar
import com.app.quizapp.R
import com.app.quizapp.navigation.NavigationDestination

object EditProfileDestination : NavigationDestination {
    override val route: String = "edit_profile"
    override val titleRes: Int = R.string.edit_profile
}

/**
 * Edit Profile screen for updating user information
 * Integrates with EditProfileViewModel
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBackClick: () -> Unit = {},
    onSaveSuccess: () -> Unit = {},
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    // Handle successful save navigation
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            viewModel.resetSavedState()
            onSaveSuccess()
        }
    }

    Scaffold(
        topBar = {
            QuizTopAppBar(
                modifier = Modifier,
                title = stringResource(EditProfileDestination.titleRes),
                canNavigateBack = true,
                navigateUp = onBackClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFB0E5E0))
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Show loading indicator while profile is being loaded
            if (uiState.isLoading && uiState.firstname.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
                return@Scaffold
            }

            Spacer(modifier = Modifier.height(16.dp))

            // First Name field
            Text(
                text = stringResource(R.string.edit_profile_firstname_label),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1A1A1A),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
            )

            OutlinedTextField(
                value = uiState.firstname,
                onValueChange = viewModel::onFirstnameChange,
                placeholder = {
                    Text(
                        text = stringResource(R.string.edit_profile_firstname_placeholder),
                        color = Color(0xFFB0A090)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = stringResource(R.string.edit_profile_firstname_icon_description),
                        tint = Color(0xFF654321)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFFFF8E1),
                    focusedContainerColor = Color(0xFFFFF8E1),
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color(0xFF654321)
                ),
                singleLine = true,
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Surname field
            Text(
                text = stringResource(R.string.edit_profile_surname_label),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1A1A1A),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
            )

            OutlinedTextField(
                value = uiState.surname,
                onValueChange = viewModel::onSurnameChange,
                placeholder = {
                    Text(
                        text = stringResource(R.string.edit_profile_surname_placeholder),
                        color = Color(0xFFB0A090)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = stringResource(R.string.edit_profile_surname_icon_description),
                        tint = Color(0xFF654321)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFFFF8E1),
                    focusedContainerColor = Color(0xFFFFF8E1),
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color(0xFF654321)
                ),
                singleLine = true,
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Nickname field (optional)
            Text(
                text = stringResource(R.string.edit_profile_nickname_label),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1A1A1A),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
            )

            OutlinedTextField(
                value = uiState.nickname,
                onValueChange = viewModel::onNicknameChange,
                placeholder = {
                    Text(
                        text = stringResource(R.string.edit_profile_nickname_placeholder),
                        color = Color(0xFFB0A090)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = stringResource(R.string.edit_profile_nickname_icon_description),
                        tint = Color(0xFF654321)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFFFF8E1),
                    focusedContainerColor = Color(0xFFFFF8E1),
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color(0xFF654321)
                ),
                singleLine = true,
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Birthdate field
            Text(
                text = stringResource(R.string.edit_profile_birthdate_label),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1A1A1A),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
            )

            OutlinedTextField(
                value = uiState.birthdate,
                onValueChange = viewModel::onBirthdateChange,
                placeholder = {
                    Text(
                        text = stringResource(R.string.edit_profile_birthdate_placeholder),
                        color = Color(0xFFB0A090)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = stringResource(R.string.edit_profile_birthdate_icon_description),
                        tint = Color(0xFF654321)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFFFF8E1),
                    focusedContainerColor = Color(0xFFFFF8E1),
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color(0xFF654321)
                ),
                singleLine = true,
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Error message
            uiState.error?.let { error ->
                Text(
                    text = error,
                    fontSize = 14.sp,
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save button
            Button(
                onClick = { viewModel.updateProfile() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E7D32)
                ),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading && uiState.firstname.isNotEmpty()) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text(
                        text = stringResource(R.string.edit_profile_save_button),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
