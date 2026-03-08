package com.app.quizapp.presentation.register

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.quizapp.R
import com.app.quizapp.navigation.NavigationDestination

object CreateAccountDestination : NavigationDestination {
    override val route: String = "create_account"
    override val titleRes: Int = R.string.create_account
}

/**
 * Create account screen for new users
 * Integrates with RegisterViewModel for user registration
 */
@Composable
fun CreateAccountScreen(
    onRegisterSuccess: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onEditAvatarClick: () -> Unit = {},
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    // Handle successful registration navigation
    LaunchedEffect(uiState.isRegistered) {
        if (uiState.isRegistered) {
            viewModel.resetRegisterState()
            onRegisterSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFB0E5E0))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Create account title
            Text(
                text = "Create an account",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF654321)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Avatar with edit button
            Box(
                modifier = Modifier.size(100.dp)
            ) {
                // Avatar image placeholder
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFE0F2F1))
                        .border(3.dp, Color.White, RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Avatar",
                        modifier = Modifier.size(50.dp),
                        tint = Color(0xFF00ACC1)
                    )
                }

                // Edit button
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.BottomEnd)
                        .offset(x = 4.dp, y = 4.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFF8E1))
                        .clickable { onEditAvatarClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit avatar",
                        modifier = Modifier.size(18.dp),
                        tint = Color(0xFF654321)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // First Name field
            Text(
                text = "First Name",
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
                        text = "Enter first name",
                        color = Color(0xFFB0A090)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "First Name",
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
                text = "Surname",
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
                        text = "Enter surname",
                        color = Color(0xFFB0A090)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Surname",
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
                text = "Nickname (optional)",
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
                        text = "Enter preferred nickname",
                        color = Color(0xFFB0A090)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Nickname",
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

            // Email field
            Text(
                text = "Email",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1A1A1A),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
            )

            OutlinedTextField(
                value = uiState.email,
                onValueChange = viewModel::onEmailChange,
                placeholder = {
                    Text(
                        text = "example@email.com",
                        color = Color(0xFFB0A090)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Email,
                        contentDescription = "Email",
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
                text = "Birthdate (dd.MM.yyyy)",
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
                        text = "01.01.2000",
                        color = Color(0xFFB0A090)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.DateRange,
                        contentDescription = "Birthdate",
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

            // Password field
            Text(
                text = "Password",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1A1A1A),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
            )

            OutlinedTextField(
                value = uiState.password,
                onValueChange = viewModel::onPasswordChange,
                placeholder = {
                    Text(
                        text = "at least 8 characters",
                        color = Color(0xFFB0A090)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "Password",
                        tint = Color(0xFF654321)
                    )
                },
                visualTransformation = PasswordVisualTransformation(),
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

            Spacer(modifier = Modifier.height(16.dp))

            // Create Account button
            Button(
                onClick = { viewModel.register() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E7D32)
                ),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text(
                        text = "Create Account",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Login text
            val annotatedString = buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color(0xFF1A1A1A))) {
                    append("Already have an account? ")
                }
                withStyle(
                    style = SpanStyle(
                        color = Color(0xFF654321),
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline
                    )
                ) {
                    append("Log in")
                }
            }

            Text(
                text = annotatedString,
                fontSize = 14.sp,
                modifier = Modifier.clickable { onLoginClick() }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }

        // Bottom indicator
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
                .width(40.dp)
                .height(4.dp)
                .background(Color.White, RoundedCornerShape(2.dp))
        )
    }
}
