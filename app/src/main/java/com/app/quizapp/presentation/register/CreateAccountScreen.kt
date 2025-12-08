package com.app.quizapp.presentation.register

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.app.quizapp.R
import com.app.quizapp.navigation.NavigationDestination

object CreateAccountDestination : NavigationDestination {
    override val route: String = "create_account"
    override val titleRes: Int = R.string.create_account
}

/**
 * Create account screen for new users
 */
@Composable
fun CreateAccountScreen(
    onCreateAccountClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onEditAvatarClick: () -> Unit = {}
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
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
            Spacer(modifier = Modifier.height(60.dp))

            // Create account title
            Text(
                text = "Create an account",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF654321)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Avatar with edit button
            Box(
                modifier = Modifier.size(120.dp)
            ) {
                // Avatar image placeholder
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFE0F2F1))
                        .border(3.dp, Color.White, RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    // TODO: Replace with actual avatar image
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Avatar",
                        modifier = Modifier.size(60.dp),
                        tint = Color(0xFF00ACC1)
                    )
                }

                // Edit button
                Box(
                    modifier = Modifier
                        .size(36.dp)
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
                        modifier = Modifier.size(20.dp),
                        tint = Color(0xFF654321)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

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
                        text = "Enter preferred username",
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

            Spacer(modifier = Modifier.height(20.dp))

            // Email field
            Text(
                text = "Email",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1A1A1A),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
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
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

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

            Spacer(modifier = Modifier.height(40.dp))

            // Create Account button
            Button(
                onClick = onCreateAccountClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E7D32)
                )
            ) {
                Text(
                    text = "Create Account",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

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
