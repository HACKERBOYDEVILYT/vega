package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.chart.VegaChartEngine
import com.example.ui.components.comments.CollaborativeCommentsSheet
import com.example.ui.components.filters.VegaFilterBar
import com.example.ui.components.kpi.ExecutiveKpiRow
import com.example.ui.components.spec.VegaSpecSheet
import com.example.ui.components.versioning.SemanticVersioningSheet
import com.example.ui.components.web.VegaWebPortalView
import com.example.ui.components.workspace.WorkspaceHeader
import com.example.ui.theme.*

enum class DashboardViewMode(val title: String) {
    MOBILE_APP("Mobile App View"),
    WEB_APPLICATION("Cloud Web App & Site")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VegaDashboardScreen(
    viewModel: VegaViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var currentViewMode by remember { mutableStateOf(DashboardViewMode.MOBILE_APP) }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = VegaNavyBackground,
        topBar = {
            Column(modifier = Modifier.background(VegaNavyBackground)) {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = VegaCyanContainer,
                                border = androidx.compose.foundation.BorderStroke(1.dp, VegaCyan),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Timeline,
                                        contentDescription = null,
                                        tint = VegaCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "Vega Analytics",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = VegaTextPrimary
                                    )
                                    Surface(
                                        color = VegaVioletContainer.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "LIVE",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = VegaEmerald,
                                            fontSize = 9.sp,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "Visual Data Exploration & Collaborative Workspaces",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = VegaTextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    },
                    actions = {
                        // SemVer quick badge
                        Surface(
                            color = VegaNavySurfaceVariant,
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, VegaNavyBorder),
                            modifier = Modifier
                                .clickable { viewModel.setVersioningSheetVisible(true) }
                                .padding(end = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Code,
                                    contentDescription = null,
                                    tint = VegaCyan,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = uiState.activeWorkspace.currentVersionTag,
                                    fontFamily = FontFamily.Monospace,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = VegaCyan
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = VegaNavyBackground,
                        titleContentColor = VegaTextPrimary
                    )
                )

                // View Mode Switcher (Mobile App vs Cloud Web App)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .background(VegaNavySurface, RoundedCornerShape(12.dp))
                        .border(1.dp, VegaNavyBorder, RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    DashboardViewMode.values().forEach { mode ->
                        val isSelected = currentViewMode == mode
                        Surface(
                            color = if (isSelected) VegaCyan else Color.Transparent,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { currentViewMode = mode }
                                .testTag("view_mode_${mode.name.lowercase()}")
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (mode == DashboardViewMode.MOBILE_APP) Icons.Default.PhoneAndroid else Icons.Default.Language,
                                    contentDescription = null,
                                    tint = if (isSelected) VegaOnCyan else VegaTextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = mode.title,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (isSelected) VegaOnCyan else VegaTextSecondary
                                )
                            }
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            if (currentViewMode == DashboardViewMode.MOBILE_APP) {
                ExtendedFloatingActionButton(
                    onClick = { viewModel.setCommentsSheetVisible(true) },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (uiState.unreadCommentsCount > 0) {
                                    Badge(containerColor = VegaRose, contentColor = Color.White) {
                                        Text(uiState.unreadCommentsCount.toString())
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Default.Forum, contentDescription = null)
                        }
                    },
                    text = { Text("Insights & Discussion") },
                    containerColor = VegaCyan,
                    contentColor = VegaOnCyan,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.testTag("floating_comments_fab")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentViewMode) {
                DashboardViewMode.MOBILE_APP -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .widthIn(max = 900.dp)
                            .align(Alignment.TopCenter)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = PaddingValues(top = 8.dp, bottom = 88.dp)
                    ) {
                        // Workspace Header with Member Presence & SemVer trigger
                        item {
                            WorkspaceHeader(
                                currentWorkspace = uiState.activeWorkspace,
                                workspaces = uiState.workspaces,
                                onSelectWorkspace = { viewModel.selectWorkspace(it) },
                                onCreateWorkspace = { name, desc -> viewModel.createWorkspace(name, desc) },
                                onOpenVersioning = { viewModel.setVersioningSheetVisible(true) },
                                onSimulateTeamActivity = { viewModel.simulateTeamEvent() },
                                unreadCommentsCount = uiState.unreadCommentsCount,
                                onOpenComments = { viewModel.setCommentsSheetVisible(true) }
                            )
                        }

                        // Executive KPI Metrics Row
                        item {
                            ExecutiveKpiRow(
                                dataset = uiState.activeDataset,
                                spec = uiState.activeSpec,
                                records = uiState.filteredRecords
                            )
                        }

                        // Main Interactive Vega Canvas Chart Engine
                        item {
                            VegaChartEngine(
                                dataset = uiState.activeDataset,
                                spec = uiState.activeSpec,
                                records = uiState.filteredRecords,
                                selectedRecord = uiState.selectedRecord,
                                onRecordSelected = { record, summary ->
                                    viewModel.selectRecord(record, summary)
                                }
                            )
                        }

                        // Quick Filter & Dimension Bar
                        item {
                            VegaFilterBar(
                                datasets = uiState.datasets,
                                activeDataset = uiState.activeDataset,
                                onSelectDataset = { viewModel.selectDataset(it) },
                                activeMarkType = uiState.activeSpec.markType,
                                onSelectMarkType = { viewModel.selectMarkType(it) },
                                activeFilterDimension = uiState.activeFilterDimension,
                                activeFilterValue = uiState.activeFilterValue,
                                onFilterChanged = { dim, valStr -> viewModel.setFilter(dim, valStr) },
                                onOpenSpecSheet = { viewModel.setSpecSheetVisible(true) }
                            )
                        }

                        // Quick Action Cards
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    color = VegaNavySurface,
                                    shape = RoundedCornerShape(14.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, VegaNavyBorder),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { viewModel.setSpecSheetVisible(true) }
                                        .testTag("action_spec_inspector")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(34.dp)
                                                .background(VegaCyanContainer, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Tune,
                                                contentDescription = null,
                                                tint = VegaCyan,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        Column {
                                            Text("Vega Spec", style = MaterialTheme.typography.titleSmall, color = VegaTextPrimary)
                                            Text("Inspect & customize", style = MaterialTheme.typography.labelSmall, color = VegaTextSecondary)
                                        }
                                    }
                                }

                                Surface(
                                    color = VegaNavySurface,
                                    shape = RoundedCornerShape(14.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, VegaNavyBorder),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { viewModel.setVersioningSheetVisible(true) }
                                        .testTag("action_semver_history")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(34.dp)
                                                .background(VegaVioletContainer, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.History,
                                                contentDescription = null,
                                                tint = VegaViolet,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        Column {
                                            Text("SemVer History", style = MaterialTheme.typography.titleSmall, color = VegaTextPrimary)
                                            Text("Releases & rollbacks", style = MaterialTheme.typography.labelSmall, color = VegaTextSecondary)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                DashboardViewMode.WEB_APPLICATION -> {
                    VegaWebPortalView(
                        workspace = uiState.activeWorkspace,
                        dataset = uiState.activeDataset,
                        spec = uiState.activeSpec,
                        modifier = Modifier
                            .fillMaxSize()
                            .widthIn(max = 900.dp)
                            .align(Alignment.TopCenter)
                            .padding(16.dp)
                    )
                }
            }

            // Real-time status sync toast pill
            AnimatedVisibility(
                visible = uiState.syncStatusMessage != null,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp)
            ) {
                Surface(
                    color = VegaCyanContainer,
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, VegaCyan),
                    shadowElevation = 6.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(VegaCyan, CircleShape)
                        )
                        Text(
                            text = uiState.syncStatusMessage ?: "",
                            style = MaterialTheme.typography.labelMedium,
                            color = VegaCyan
                        )
                    }
                }
            }
        }
    }

    // Modal Bottom Sheets
    if (uiState.showSpecSheet) {
        VegaSpecSheet(
            spec = uiState.activeSpec,
            dataset = uiState.activeDataset,
            onSpecChanged = { viewModel.updateSpec(it) },
            onDismiss = { viewModel.setSpecSheetVisible(false) }
        )
    }

    if (uiState.showCommentsSheet) {
        CollaborativeCommentsSheet(
            comments = uiState.comments,
            pinnedDataContext = uiState.selectedRecordSummary,
            onAddComment = { content, pinnedContext ->
                viewModel.addComment(content, pinnedContext)
            },
            onAddReply = { commentId, content ->
                viewModel.addReply(commentId, content)
            },
            onToggleResolved = { commentId ->
                viewModel.toggleCommentResolved(commentId)
            },
            onDismiss = { viewModel.setCommentsSheetVisible(false) }
        )
    }

    if (uiState.showVersioningSheet) {
        SemanticVersioningSheet(
            currentVersionTag = uiState.activeWorkspace.currentVersionTag,
            versions = uiState.versions,
            currentSpec = uiState.activeSpec,
            onPublishNewVersion = { tag, notes ->
                viewModel.publishNewVersion(tag, notes)
            },
            onRollbackToVersion = { version ->
                viewModel.rollbackToVersion(version)
            },
            onDismiss = { viewModel.setVersioningSheetVisible(false) }
        )
    }
}
