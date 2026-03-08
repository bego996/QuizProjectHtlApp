package com.app.quizapp.domain.model

/**
 * Domain model for Topic/Category with hierarchical structure
 * @param parentTopic Parent topic for hierarchical categorization (null for root categories)
 */
data class Topic(
    val topicId: Int,
    val topic: String,
    val parentTopic: Topic? = null
)
