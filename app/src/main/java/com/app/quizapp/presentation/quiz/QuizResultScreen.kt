package com.app.quizapp.presentation.quiz

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.quizapp.R
import com.app.quizapp.navigation.NavigationDestination

object QuizResultDestination : NavigationDestination {
    override val route: String = "quiz_result"
    override val titleRes: Int = R.string.quiz_result
}

/**
 * Quiz result screen showing score and options to replay, review, or continue
 */
@Composable
fun QuizResultScreen(
    score: Int = 0,
    totalQuestions: Int = 5,
    onReplayClick: () -> Unit = {},
    onReviewClick: () -> Unit = {},
    onContinueClick: () -> Unit = {}
) {
    // Determine result message and color based on score
    val (resultTitle, resultColor) = when {
        score == 0 -> "Had a bad day?" to Color(0xFFD32F2F)
        score == totalQuestions -> "Congratulations" to Color(0xFF2E7D32)
        score >= totalQuestions * 0.6 -> "Well Done!" to Color(0xFFFF8F00)
        else -> "Nice Try!" to Color(0xFFFF8F00)
    }

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
            Spacer(modifier = Modifier.height(40.dp))

            // Illustration placeholder with "COMPLETED" badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp),
                contentAlignment = Alignment.Center
            ) {
                // TODO: Replace with actual illustration
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Badge
                    Box(
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                    ) {
                        Text(
                            text = "⭐",
                            fontSize = 80.sp
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .offset(y = 20.dp)
                                .background(Color(0xFF654321), RoundedCornerShape(4.dp))
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "COMPLETED",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Person sitting illustration placeholder
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .background(Color(0xFF80C5C0).copy(alpha = 0.3f), RoundedCornerShape(60.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "👤",
                            fontSize = 60.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Result title
            Text(
                text = resultTitle,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = resultColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Score subtitle
            Text(
                text = "QUESTIONS YOU GOT RIGHT",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF654321),
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Score
            Text(
                text = "$score of $totalQuestions",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF654321)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Replay button
            OutlinedButton(
                onClick = onReplayClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color(0xFF654321)
                ),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF654321))
            ) {
                Text(
                    text = "Replay",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Review button
            OutlinedButton(
                onClick = onReviewClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color(0xFF654321)
                ),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF654321))
            ) {
                Text(
                    text = "Review",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Continue button
            Button(
                onClick = onContinueClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E7D32)
                )
            ) {
                Text(
                    text = "Continue",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
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
