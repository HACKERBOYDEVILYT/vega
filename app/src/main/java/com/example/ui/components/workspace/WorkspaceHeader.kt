package com.example.ui.components.workspace

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Workspace
import com.example.ui.theme.*

@Composable
fun WorkspaceHeader(
    currentWorkspace: Workspace,
    workspaces: List<Workspace>,
    onSelectWorkspace: (Workspace) -> Unit,
    onCreateWorkspace: (name: String, desc: String) -> Unit,
    onOpenVersioning: () -> Unit,
    onSimulateTeamActivity: () -> Unit,
    unreadCommentsCount: Int,
    onOpenComments: () -> Unit
) {
    var showWorkspaceMenu by remember { mutableStateOf(false) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var showShareDialog by remember { mutableStateOf(false) }

    Surface(
        color = VegaNavySurface,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, VegaNavyBorder),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("workspace_header")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Row: Workspace Selector & Action Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Workspace Selector Pill
                Box {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { showWorkspaceMenu = true }
                            .background(VegaNavyBackground)
                            .border(1.dp, VegaNavyBorder, RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(VegaCyan, CircleShape)
                        )
                        Text(
                            text = currentWorkspace.name,
                            style = MaterialTheme.typography.titleSmall,
                            color = VegaTextPrimary,
                            maxLines = 1
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Switch Workspace",
                            tint = VegaCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showWorkspaceMenu,
                        onDismissRequest = { showWorkspaceMenu = false },
                        modifier = Modifier.background(VegaNavySurface)
                    ) {
                        Text(
                            text = "Collaborative Workspaces",
                            style = MaterialTheme.typography.labelSmall,
                            color = VegaTextMuted,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                        workspaces.forEach { ws ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(ws.name, color = if (ws.id == currentWorkspace.id) VegaCyan else VegaTextPrimary)
                                        Text("${ws.currentVersionTag} • ${ws.members.size} members", style = MaterialTheme.typography.labelSmall, color = VegaTextMuted)
                                    }
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (ws.id == currentWorkspace.id) Icons.Default.CheckCircle else Icons.Default.Workspaces,
                                        contentDescription = null,
                                        tint = if (ws.id == currentWorkspace.id) VegaCyan else VegaTextSecondary
                                    )
                                },
                                onClick = {
                                    onSelectWorkspace(ws)
                                    showWorkspaceMenu = false
                                }
                            )
                        }
                        HorizontalDivider(color = VegaNavyBorder)
                        DropdownMenuItem(
                            text = { Text("New Workspace...", color = VegaCyan) },
                            leadingIcon = { Icon(Icons.Default.Add, contentDescription = null, tint = VegaCyan) },
                            onClick = {
                                showWorkspaceMenu = false
                                showCreateDialog = true
                            }
                        )
                    }
                }

                // SemVer Badge (Clickable to open version history)
                Surface(
                    color = VegaVioletContainer.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, VegaViolet.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .clickable { onOpenVersioning() }
                        .testTag("semver_badge")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Tag, contentDescription = null, tint = VegaViolet, modifier = Modifier.size(14.dp))
                        Text(
                            text = currentWorkspace.currentVersionTag,
                            fontFamily = FontFamily.Monospace,
                            style = MaterialTheme.typography.labelMedium,
                            color = VegaViolet
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom Row: Member Presence Avatars & Team Activity Triggers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Member Avatars with Live Status
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy((-6).dp)
                ) {
                    currentWorkspace.members.forEach { member ->
                        Box(contentAlignment = Alignment.BottomEnd) {
                            Surface(
                                shape = CircleShape,
                                color = VegaNavySurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(2.dp, VegaNavySurface),
                                modifier = Modifier.size(28.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = member.initials,
                                        fontSize = 10.sp,
                                        color = VegaTextPrimary
                                    )
                                }
                            }
                            if (member.isOnline) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(VegaEmerald, CircleShape)
                                        .border(1.dp, VegaNavySurface, CircleShape)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Text(
                        text = "${currentWorkspace.members.count { it.isOnline }} active now",
                        style = MaterialTheme.typography.labelSmall,
                        color = VegaEmerald,
                        fontSize = 11.sp
                    )
                }

                // Action Buttons: Share & Simulate Team Event
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    IconButton(
                        onClick = { showShareDialog = true },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share Workspace",
                            tint = VegaTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    FilledTonalIconButton(
                        onClick = onSimulateTeamActivity,
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = VegaNavyBackground,
                            contentColor = VegaCyan
                        ),
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("simulate_team_activity_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = "Simulate Real-time Sync",
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Comments badge button
                    BadgedBox(
                        badge = {
                            if (unreadCommentsCount > 0) {
                                Badge(
                                    containerColor = VegaCyan,
                                    contentColor = VegaOnCyan
                                ) {
                                    Text(unreadCommentsCount.toString())
                                }
                            }
                        }
                    ) {
                        IconButton(
                            onClick = onOpenComments,
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("open_comments_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChatBubbleOutline,
                                contentDescription = "Collaborative Comments",
                                tint = VegaTextPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateWorkspaceDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { name, desc ->
                onCreateWorkspace(name, desc)
                showCreateDialog = false
            }
        )
    }

    if (showShareDialog) {
        ShareWorkspaceDialog(
            workspace = currentWorkspace,
            onDismiss = { showShareDialog = false }
        )
    }
}

@Composable
private fun CreateWorkspaceDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, desc: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = VegaNavySurface,
        title = { Text("Create Collaborative Workspace", color = VegaTextPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Workspace Name") },
                    placeholder = { Text("e.g. Q4 Executive Board") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VegaCyan,
                        unfocusedBorderColor = VegaNavyBorder,
                        focusedTextColor = VegaTextPrimary,
                        unfocusedTextColor = VegaTextPrimary,
                        focusedContainerColor = VegaNavyBackground,
                        unfocusedContainerColor = VegaNavyBackground
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Scope & Purpose") },
                    placeholder = { Text("Cross-regional data analysis") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VegaCyan,
                        unfocusedBorderColor = VegaNavyBorder,
                        focusedTextColor = VegaTextPrimary,
                        unfocusedTextColor = VegaTextPrimary,
                        focusedContainerColor = VegaNavyBackground,
                        unfocusedContainerColor = VegaNavyBackground
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onConfirm(name.trim(), desc.trim()) },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = VegaCyan, contentColor = VegaOnCyan)
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = VegaTextMuted) }
        }
    )
}

@Composable
private fun ShareWorkspaceDialog(
    workspace: Workspace,
    onDismiss: () -> Unit
) {
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }
    val shareLink = "https://vega-analytics.cloud/ws/${workspace.id}?v=${workspace.currentVersionTag}"

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = VegaNavySurface,
        title = { Text("Share Collaborative Workspace", color = VegaTextPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Invite team members to explore charts and collaborate with real-time sync.",
                    style = MaterialTheme.typography.bodySmall,
                    color = VegaTextSecondary
                )

                Surface(
                    color = VegaNavyBackground,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, VegaNavyBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = shareLink,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = VegaCyan,
                            maxLines = 1,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(shareLink))
                                copied = true
                            }
                        ) {
                            Icon(
                                imageVector = if (copied) Icons.Default.Check else Icons.Default.ContentCopy,
                                contentDescription = "Copy Link",
                                tint = if (copied) VegaEmerald else VegaTextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Text(
                    text = "Active Permissions: Editor (Version Publish & Comments Allowed)",
                    style = MaterialTheme.typography.labelSmall,
                    color = VegaTextMuted
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = VegaCyan, contentColor = VegaOnCyan)
            ) {
                Text("Done")
            }
        }
    )
}
