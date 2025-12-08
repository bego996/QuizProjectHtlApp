package com.app.quizapp.presentation.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.quizapp.R
import com.app.quizapp.navigation.NavigationDestination

object WelcomeDestination : NavigationDestination {
    override val route: String = "welcome"
    override val titleRes: Int = R.string.welcome
}

/**
 * Welcome screen - first screen users see (after splash)
 */
@Composable
fun WelcomeScreen(
    onGetStartedClick: () -> Unit = {},
    onLogInClick: () -> Unit = {}
) {
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

            // Greeting title
            Text(
                text = "Greetings!",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF654321)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Subtitle
            Text(
                text = "Test your knowledge on all things!",
                fontSize = 18.sp,
                color = Color(0xFF654321),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Cityscape illustration
            Image(
                painter = painterResource(id = R.drawable.silhouette_skyline_panorama_of_city_accraghana),
                contentDescription = "Cityscape illustration",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(horizontal = 16.dp),
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(Color(0xFF80C5C0))
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Bottom text
            Text(
                text = "Dive in and quiz your way\nthrough time!",
                fontSize = 16.sp,
                color = Color(0xFF654321),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Get Started button
            Button(
                onClick = onGetStartedClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF654321)
                )
            ) {
                Text(
                    text = "Get Started",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Log in text
            val annotatedString = buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color(0xFF654321))) {
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
                modifier = Modifier.clickable { onLogInClick() }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }

        // Bottom indicator (like page indicator)
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
