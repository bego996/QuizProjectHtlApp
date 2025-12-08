package com.app.quizapp.presentation.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.quizapp.R
import com.app.quizapp.navigation.NavigationDestination

object LoginDestination : NavigationDestination {
    override val route: String = "login"
    override val titleRes: Int = R.string.login
}

/**
 * Login screen for existing users
 */
@Composable
fun LoginScreen(
    onLoginClick: () -> Unit = {},
    onCreateAccountClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {}
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFB0E5E0))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            // App logo
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.White, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                // TODO: Replace with actual logo
                Text(
                    text = "Q",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00ACC1)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Login title
            Text(
                text = "Login",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF654321)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Username field
            Text(
                text = "Username",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1A1A1A),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                placeholder = {
                    Text(
                        text = "Enter preferred username or mail",
                        color = Color(0xFFB0A090)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Username",
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
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Password field
            Text(
                text = "Password",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1A1A1A),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
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
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Forgot Password
            Text(
                text = "Forgot Password",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF654321),
                textDecoration = TextDecoration.Underline,
                modifier = Modifier
                    .clickable { onForgotPasswordClick() }
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Login button
            Button(
                onClick = onLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E7D32)
                )
            ) {
                Text(
                    text = "Login",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Create account text
            val annotatedString = buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color(0xFF1A1A1A))) {
                    append("Don't have an account? ")
                }
                withStyle(
                    style = SpanStyle(
                        color = Color(0xFF654321),
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline
                    )
                ) {
                    append("Create Account")
                }
            }

            Text(
                text = annotatedString,
                fontSize = 14.sp,
                modifier = Modifier.clickable { onCreateAccountClick() }
            )

            Spacer(modifier = Modifier.weight(1f))
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
