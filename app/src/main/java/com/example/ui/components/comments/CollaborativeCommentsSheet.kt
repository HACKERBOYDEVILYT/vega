package com.example.ui.components.comments

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CommentReply
import com.example.data.model.DataComment
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollaborativeCommentsSheet(
    comments: List<DataComment>,
    pinnedDataContext: String,
    onAddComment: (content: String, pinnedContext: String) -> Unit,
    onAddReply: (commentId: String, content: String) -> Unit,
    onToggleResolved: (commentId: String) -> Unit,
    onDismiss: () -> Unit
) {
    var newCommentText by remember { mutableStateOf("") }
    var usePinnedContext by remember { mutableStateOf(pinnedDataContext.isNotBlank()) }
    var activeReplyCommentId by remember { mutableStateOf<String?>(null) }
    var replyText by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = VegaNavyBackground,
        dragHandle = {
            Surface(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(40.dp)
                    .height(4.dp),
                shape = RoundedCornerShape(2.dp),
                color = VegaNavyBorder
            ) {}
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(horizontal = 18.dp)
        ) {
            // Sheet Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(VegaCyanContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Forum,
                            contentDescription = null,
                            tint = VegaCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Collaborative Insights",
                            style = MaterialTheme.typography.titleMedium,
                            color = VegaTextPrimary
                        )
                        Text(
                            text = "${comments.size} team threads • Live sync",
                            style = MaterialTheme.typography.labelSmall,
                            color = VegaTextSecondary
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = VegaTextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Thread List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (comments.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 36.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.ChatBubbleOutline,
                                    contentDescription = null,
                                    tint = VegaTextMuted,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "No collaborative comments yet",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = VegaTextSecondary
                                )
                                Text(
                                    text = "Tap on a chart point or post below to start a thread",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = VegaTextMuted
                                )
                            }
                        }
                    }
                } else {
                    items(comments, key = { it.id }) { comment ->
                        CommentCard(
                            comment = comment,
                            isReplying = activeReplyCommentId == comment.id,
                            replyText = replyText,
                            onReplyTextChange = { replyText = it },
                            onStartReply = {
                                activeReplyCommentId = if (activeReplyCommentId == comment.id) null else comment.id
                                replyText = ""
                            },
                            onSubmitReply = {
                                if (replyText.isNotBlank()) {
                                    onAddReply(comment.id, replyText.trim())
                                    replyText = ""
                                    activeReplyCommentId = null
                                }
                            },
                            onToggleResolve = { onToggleResolved(comment.id) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Composer Card
            Surface(
                color = VegaNavySurface,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, VegaNavyBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Pinned Data Context Indicator
                    if (pinnedDataContext.isNotBlank()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(VegaNavyBackground, RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PinDrop,
                                    contentDescription = null,
                                    tint = if (usePinnedContext) VegaCyan else VegaTextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = pinnedDataContext,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (usePinnedContext) VegaCyan else VegaTextMuted,
                                    maxLines = 1
                                )
                            }
                            Switch(
                                checked = usePinnedContext,
                                onCheckedChange = { usePinnedContext = it },
                                modifier = Modifier.height(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newCommentText,
                            onValueChange = { newCommentText = it },
                            placeholder = {
                                Text(
                                    "Add executive insight or query team...",
                                    color = VegaTextMuted,
                                    fontSize = 14.sp
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VegaCyan,
                                unfocusedBorderColor = VegaNavyBorder,
                                focusedTextColor = VegaTextPrimary,
                                unfocusedTextColor = VegaTextPrimary,
                                focusedContainerColor = VegaNavyBackground,
                                unfocusedContainerColor = VegaNavyBackground
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("comment_input_field")
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        FilledIconButton(
                            onClick = {
                                if (newCommentText.isNotBlank()) {
                                    val contextToAttach = if (usePinnedContext) pinnedDataContext else ""
                                    onAddComment(newCommentText.trim(), contextToAttach)
                                    newCommentText = ""
                                }
                            },
                            enabled = newCommentText.isNotBlank(),
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = VegaCyan,
                                contentColor = VegaOnCyan,
                                disabledContainerColor = VegaNavyBorder
                            ),
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("send_comment_button")
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Send Insight")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CommentCard(
    comment: DataComment,
    isReplying: Boolean,
    replyText: String,
    onReplyTextChange: (String) -> Unit,
    onStartReply: () -> Unit,
    onSubmitReply: () -> Unit,
    onToggleResolve: () -> Unit
) {
    Surface(
        color = VegaNavySurface,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (comment.isResolved) VegaNavyBorder.copy(alpha = 0.5f) else VegaNavyBorder
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header with Author & Resolve Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .background(VegaVioletContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = comment.authorInitials,
                            style = MaterialTheme.typography.labelSmall,
                            color = VegaViolet
                        )
                    }
                    Column {
                        Text(
                            text = comment.authorName,
                            style = MaterialTheme.typography.labelMedium,
                            color = VegaTextPrimary
                        )
                        Text(
                            text = comment.timestamp,
                            style = MaterialTheme.typography.labelSmall,
                            color = VegaTextMuted,
                            fontSize = 10.sp
                        )
                    }
                }

                // Resolve pill button
                FilterChip(
                    selected = comment.isResolved,
                    onClick = onToggleResolve,
                    label = {
                        Text(
                            text = if (comment.isResolved) "Resolved" else "Open",
                            fontSize = 11.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = if (comment.isResolved) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = VegaEmerald.copy(alpha = 0.15f),
                        selectedLabelColor = VegaEmerald,
                        selectedLeadingIconColor = VegaEmerald,
                        containerColor = VegaNavyBackground,
                        labelColor = VegaAmber,
                        iconColor = VegaAmber
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = comment.isResolved,
                        borderColor = VegaAmber.copy(alpha = 0.3f),
                        selectedBorderColor = VegaEmerald.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.height(28.dp)
                )
            }

            // Pinned Data Context Tag if present
            if (comment.dataPointContext.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = VegaNavyBackground,
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, VegaNavyBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.GpsFixed,
                            contentDescription = null,
                            tint = VegaCyan,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = comment.dataPointContext,
                            style = MaterialTheme.typography.labelSmall,
                            color = VegaCyan,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Comment text
            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodyMedium,
                color = if (comment.isResolved) VegaTextSecondary else VegaTextPrimary
            )

            // Thread Replies
            if (comment.replies.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp)
                        .border(
                            width = 1.dp,
                            color = VegaNavyBorder,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .background(VegaNavyBackground.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    comment.replies.forEach { reply ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .background(VegaCyanContainer, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = reply.authorInitials,
                                    fontSize = 9.sp,
                                    color = VegaCyan
                                )
                            }
                            Column {
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = reply.authorName,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = VegaTextPrimary,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = reply.timestamp,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = VegaTextMuted,
                                        fontSize = 10.sp
                                    )
                                }
                                Text(
                                    text = reply.content,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = VegaTextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Reply Action Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onStartReply,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = if (isReplying) Icons.Default.ExpandLess else Icons.Default.Reply,
                        contentDescription = null,
                        tint = VegaCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isReplying) "Cancel" else "Reply",
                        color = VegaCyan,
                        fontSize = 12.sp
                    )
                }
            }

            // Inline Reply Composer
            AnimatedVisibility(visible = isReplying) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = replyText,
                        onValueChange = onReplyTextChange,
                        placeholder = { Text("Write reply...", fontSize = 12.sp, color = VegaTextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VegaCyan,
                            unfocusedBorderColor = VegaNavyBorder,
                            focusedContainerColor = VegaNavyBackground,
                            unfocusedContainerColor = VegaNavyBackground
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = onSubmitReply,
                        enabled = replyText.isNotBlank(),
                        colors = IconButtonDefaults.iconButtonColors(contentColor = VegaCyan)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send Reply")
                    }
                }
            }
        }
    }
}
