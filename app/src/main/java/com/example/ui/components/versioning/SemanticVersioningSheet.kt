package com.example.ui.components.versioning

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VegaSpec
import com.example.data.model.WorkspaceVersion
import com.example.ui.theme.*

enum class SemVerBump {
    PATCH, MINOR, MAJOR
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SemanticVersioningSheet(
    currentVersionTag: String,
    versions: List<WorkspaceVersion>,
    currentSpec: VegaSpec,
    onPublishNewVersion: (newTag: String, releaseNotes: String) -> Unit,
    onRollbackToVersion: (WorkspaceVersion) -> Unit,
    onDismiss: () -> Unit
) {
    var showPublishDialog by remember { mutableStateOf(false) }
    var selectedDiffVersion by remember { mutableStateOf<WorkspaceVersion?>(null) }

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
                .padding(horizontal = 20.dp)
        ) {
            // Header
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
                            .background(VegaVioletContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.HistoryEdu,
                            contentDescription = null,
                            tint = VegaViolet,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Semantic Versioning",
                            style = MaterialTheme.typography.titleMedium,
                            color = VegaTextPrimary
                        )
                        Text(
                            text = "Active Release: $currentVersionTag",
                            style = MaterialTheme.typography.labelSmall,
                            color = VegaCyan
                        )
                    }
                }

                Button(
                    onClick = { showPublishDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VegaCyan,
                        contentColor = VegaOnCyan
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("publish_version_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Tag Release")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Version Timeline
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(versions, key = { it.id }) { ver ->
                    val isCurrent = ver.versionTag == currentVersionTag
                    VersionCard(
                        version = ver,
                        isCurrent = isCurrent,
                        onRollback = { onRollbackToVersion(ver) },
                        onInspectDiff = {
                            selectedDiffVersion = if (selectedDiffVersion?.id == ver.id) null else ver
                        },
                        isDiffOpen = selectedDiffVersion?.id == ver.id,
                        currentSpec = currentSpec
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showPublishDialog) {
        PublishVersionDialog(
            currentTag = currentVersionTag,
            onDismiss = { showPublishDialog = false },
            onConfirmPublish = { newTag, notes ->
                onPublishNewVersion(newTag, notes)
                showPublishDialog = false
            }
        )
    }
}

@Composable
private fun VersionCard(
    version: WorkspaceVersion,
    isCurrent: Boolean,
    onRollback: () -> Unit,
    onInspectDiff: () -> Unit,
    isDiffOpen: Boolean,
    currentSpec: VegaSpec
) {
    Surface(
        color = VegaNavySurface,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isCurrent) VegaCyan.copy(alpha = 0.8f) else VegaNavyBorder
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = if (isCurrent) VegaCyanContainer else VegaNavyBackground,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isCurrent) VegaCyan else VegaNavyBorder
                        )
                    ) {
                        Text(
                            text = version.versionTag,
                            fontFamily = FontFamily.Monospace,
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isCurrent) VegaCyan else VegaTextPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    if (isCurrent) {
                        Text(
                            text = "CURRENT HEAD",
                            style = MaterialTheme.typography.labelSmall,
                            color = VegaEmerald,
                            fontSize = 10.sp
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = version.timestamp,
                        style = MaterialTheme.typography.labelSmall,
                        color = VegaTextMuted,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "#${version.commitHash}",
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.labelSmall,
                        color = VegaViolet,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = version.releaseNotes,
                style = MaterialTheme.typography.bodyMedium,
                color = VegaTextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Authored by ${version.authorName} • Mark: ${version.specSnapshot.markType.displayName}",
                style = MaterialTheme.typography.labelSmall,
                color = VegaTextSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onInspectDiff) {
                    Icon(
                        imageVector = if (isDiffOpen) Icons.Default.ExpandLess else Icons.Default.CompareArrows,
                        contentDescription = null,
                        tint = VegaCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isDiffOpen) "Hide Spec Diff" else "Inspect Diff", color = VegaCyan)
                }

                if (!isCurrent) {
                    FilledTonalButton(
                        onClick = onRollback,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = VegaNavySurfaceVariant,
                            contentColor = VegaAmber
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Rollback", fontSize = 12.sp)
                    }
                }
            }

            AnimatedVisibility(visible = isDiffOpen) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .background(VegaNavyBackground, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "Spec Snapshot Diff vs Current:",
                        style = MaterialTheme.typography.labelSmall,
                        color = VegaCyan
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    DiffRow("Mark Type", version.specSnapshot.markType.name, currentSpec.markType.name)
                    DiffRow("X Field", version.specSnapshot.encoding.xField, currentSpec.encoding.xField)
                    DiffRow("Y Field", version.specSnapshot.encoding.yField, currentSpec.encoding.yField)
                    DiffRow("Color Field", version.specSnapshot.encoding.colorField, currentSpec.encoding.colorField)
                    DiffRow("Aggregation", version.specSnapshot.encoding.aggregation.name, currentSpec.encoding.aggregation.name)
                }
            }
        }
    }
}

@Composable
private fun DiffRow(label: String, snapshotVal: String, currentVal: String) {
    val changed = snapshotVal != currentVal
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = VegaTextMuted)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                if (snapshotVal.isBlank()) "-" else snapshotVal,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                color = if (changed) VegaRose else VegaTextSecondary
            )
            Text("→", color = VegaTextMuted, fontSize = 11.sp)
            Text(
                if (currentVal.isBlank()) "-" else currentVal,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                color = if (changed) VegaEmerald else VegaTextSecondary
            )
        }
    }
}

@Composable
private fun PublishVersionDialog(
    currentTag: String,
    onDismiss: () -> Unit,
    onConfirmPublish: (newTag: String, releaseNotes: String) -> Unit
) {
    var bumpType by remember { mutableStateOf(SemVerBump.MINOR) }
    var notes by remember { mutableStateOf("") }

    val nextTag = remember(currentTag, bumpType) {
        calculateNextTag(currentTag, bumpType)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = VegaNavySurface,
        title = {
            Text("Publish Semantic Version", color = VegaTextPrimary)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Bump rule for Vega spec revision:",
                    style = MaterialTheme.typography.bodySmall,
                    color = VegaTextSecondary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SemVerBump.values().forEach { bump ->
                        val selected = bump == bumpType
                        FilterChip(
                            selected = selected,
                            onClick = { bumpType = bump },
                            label = { Text(bump.name) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = VegaViolet,
                                selectedLabelColor = VegaTextPrimary,
                                containerColor = VegaNavyBackground,
                                labelColor = VegaTextSecondary
                            )
                        )
                    }
                }

                Surface(
                    color = VegaNavyBackground,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, VegaNavyBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Target SemVer Tag:", style = MaterialTheme.typography.labelMedium, color = VegaTextSecondary)
                        Text(
                            nextTag,
                            fontFamily = FontFamily.Monospace,
                            style = MaterialTheme.typography.titleMedium,
                            color = VegaCyan
                        )
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Release Notes / Changelog") },
                    placeholder = { Text("e.g. Added temporal aggregation & latency color map") },
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
                onClick = {
                    val finalNotes = if (notes.isNotBlank()) notes else "Release update $nextTag"
                    onConfirmPublish(nextTag, finalNotes)
                },
                colors = ButtonDefaults.buttonColors(containerColor = VegaCyan, contentColor = VegaOnCyan)
            ) {
                Text("Publish Release")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = VegaTextMuted)
            }
        }
    )
}

fun calculateNextTag(current: String, bump: SemVerBump): String {
    val clean = current.removePrefix("v").trim()
    val parts = clean.split(".").mapNotNull { it.toIntOrNull() }
    val major = parts.getOrElse(0) { 1 }
    val minor = parts.getOrElse(1) { 0 }
    val patch = parts.getOrElse(2) { 0 }

    return when (bump) {
        SemVerBump.MAJOR -> "v${major + 1}.0.0"
        SemVerBump.MINOR -> "v$major.${minor + 1}.0"
        SemVerBump.PATCH -> "v$major.$minor.${patch + 1}"
    }
}
