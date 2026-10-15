package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.sample.SampleDatasets
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class VegaUiState(
    val workspaces: List<Workspace> = SampleDatasets.defaultWorkspaces,
    val activeWorkspace: Workspace = SampleDatasets.defaultWorkspaces[0],
    val datasets: List<VegaDataset> = SampleDatasets.allDatasets,
    val activeDataset: VegaDataset = SampleDatasets.cloudInfraDataset,
    val activeSpec: VegaSpec = SampleDatasets.defaultWorkspaces[0].activeSpec,
    val comments: List<DataComment> = SampleDatasets.initialComments,
    val versions: List<WorkspaceVersion> = SampleDatasets.initialVersions["ws_tech_ops"] ?: emptyList(),
    val selectedRecord: DataRecord? = null,
    val selectedRecordSummary: String = "",
    val activeFilterDimension: String = "",
    val activeFilterValue: String = "",
    val showSpecSheet: Boolean = false,
    val showCommentsSheet: Boolean = false,
    val showVersioningSheet: Boolean = false,
    val syncStatusMessage: String? = null
) {
    val filteredRecords: List<DataRecord>
        get() {
            if (activeFilterDimension.isBlank() || activeFilterValue.isBlank()) {
                return activeDataset.records
            }
            return activeDataset.records.filter {
                it.getString(activeFilterDimension).equals(activeFilterValue, ignoreCase = true)
            }
        }

    val unreadCommentsCount: Int
        get() = comments.count { !it.isResolved }
}

class VegaViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(VegaUiState())
    val uiState: StateFlow<VegaUiState> = _uiState.asStateFlow()

    fun selectWorkspace(workspace: Workspace) {
        val targetDataset = _uiState.value.datasets.find { it.id == workspace.activeDatasetId }
            ?: _uiState.value.datasets.first()

        val targetVersions = SampleDatasets.initialVersions[workspace.id] ?: listOf(
            WorkspaceVersion(
                workspaceId = workspace.id,
                versionTag = workspace.currentVersionTag,
                releaseNotes = "Initial specification commit for ${workspace.name}.",
                specSnapshot = workspace.activeSpec,
                authorName = "Ayan Islam",
                timestamp = "Just now"
            )
        )

        _uiState.update {
            it.copy(
                activeWorkspace = workspace,
                activeDataset = targetDataset,
                activeSpec = workspace.activeSpec,
                versions = targetVersions,
                selectedRecord = null,
                selectedRecordSummary = "",
                activeFilterDimension = "",
                activeFilterValue = "",
                syncStatusMessage = "Switched to workspace: ${workspace.name}"
            )
        }
        clearStatusAfterDelay()
    }

    fun createWorkspace(name: String, desc: String) {
        val newWs = Workspace(
            id = "ws_${UUID.randomUUID().toString().take(8)}",
            name = name,
            description = desc,
            currentVersionTag = "v1.0.0",
            members = listOf(
                WorkspaceMember("m1", "Ayan Islam", "ayan@corp.io", "AI", "Lead Architect", isOnline = true)
            ),
            activeDatasetId = _uiState.value.activeDataset.id,
            activeSpec = _uiState.value.activeSpec,
            updatedAtFormatted = "Just now"
        )

        _uiState.update {
            it.copy(
                workspaces = it.workspaces + newWs,
                activeWorkspace = newWs,
                syncStatusMessage = "Created workspace '$name' at v1.0.0"
            )
        }
        clearStatusAfterDelay()
    }

    fun selectDataset(dataset: VegaDataset) {
        // Build an intuitive default spec for the chosen dataset
        val defaultSpec = when (dataset.id) {
            "renewable_grid" -> VegaSpec(
                title = "Renewable Generation Trajectory",
                description = "Solar generation over hours of the day.",
                markType = MarkType.AREA,
                encoding = VegaEncoding(xField = "hour", yField = "solar_mw", colorField = "battery_pct")
            )
            "ecommerce_funnel" -> VegaSpec(
                title = "Channel Gross Revenue Contribution",
                description = "Revenue performance across acquisition channels.",
                markType = MarkType.BAR,
                encoding = VegaEncoding(xField = "channel", yField = "revenue_k", colorField = "channel")
            )
            else -> VegaSpec(
                title = "Edge Latency vs Throughput Distribution",
                description = "Latency and throughput metrics across edge nodes.",
                markType = MarkType.SCATTER,
                encoding = VegaEncoding(xField = "latency_ms", yField = "throughput_k", colorField = "region", sizeField = "cpu_load_pct")
            )
        }

        _uiState.update {
            it.copy(
                activeDataset = dataset,
                activeSpec = defaultSpec,
                selectedRecord = null,
                selectedRecordSummary = "",
                activeFilterDimension = "",
                activeFilterValue = "",
                syncStatusMessage = "Loaded dataset: ${dataset.title}"
            )
        }
        clearStatusAfterDelay()
    }

    fun updateSpec(newSpec: VegaSpec) {
        _uiState.update {
            it.copy(
                activeSpec = newSpec,
                syncStatusMessage = "Updated spec encodings"
            )
        }
        clearStatusAfterDelay()
    }

    fun selectMarkType(mark: MarkType) {
        _uiState.update {
            it.copy(
                activeSpec = it.activeSpec.copy(markType = mark),
                syncStatusMessage = "Mark representation changed to ${mark.displayName}"
            )
        }
        clearStatusAfterDelay()
    }

    fun setFilter(dimension: String, value: String) {
        _uiState.update {
            it.copy(
                activeFilterDimension = dimension,
                activeFilterValue = value,
                selectedRecord = null,
                selectedRecordSummary = ""
            )
        }
    }

    fun selectRecord(record: DataRecord?, summary: String) {
        _uiState.update {
            it.copy(
                selectedRecord = record,
                selectedRecordSummary = summary
            )
        }
    }

    fun addComment(content: String, pinnedContext: String) {
        val newComment = DataComment(
            workspaceId = _uiState.value.activeWorkspace.id,
            chartTitle = _uiState.value.activeSpec.title,
            authorName = "Ayan Islam",
            authorInitials = "AI",
            content = content,
            dataPointContext = pinnedContext,
            isResolved = false,
            timestamp = "Just now"
        )
        _uiState.update {
            it.copy(
                comments = listOf(newComment) + it.comments,
                syncStatusMessage = "Published insight to workspace team"
            )
        }
        clearStatusAfterDelay()
    }

    fun addReply(commentId: String, content: String) {
        val reply = CommentReply(
            authorName = "Ayan Islam",
            authorInitials = "AI",
            content = content,
            timestamp = "Just now"
        )
        _uiState.update { state ->
            val updatedComments = state.comments.map { comment ->
                if (comment.id == commentId) {
                    comment.copy(replies = comment.replies + reply)
                } else comment
            }
            state.copy(comments = updatedComments)
        }
    }

    fun toggleCommentResolved(commentId: String) {
        _uiState.update { state ->
            val updated = state.comments.map { comment ->
                if (comment.id == commentId) {
                    comment.copy(isResolved = !comment.isResolved)
                } else comment
            }
            state.copy(comments = updated)
        }
    }

    fun publishNewVersion(tag: String, releaseNotes: String) {
        val newVersion = WorkspaceVersion(
            workspaceId = _uiState.value.activeWorkspace.id,
            versionTag = tag,
            releaseNotes = releaseNotes,
            specSnapshot = _uiState.value.activeSpec,
            authorName = "Ayan Islam",
            timestamp = "Just now"
        )

        val updatedWs = _uiState.value.activeWorkspace.copy(
            currentVersionTag = tag,
            updatedAtFormatted = "Just now"
        )

        _uiState.update { state ->
            val updatedWorkspaces = state.workspaces.map { ws ->
                if (ws.id == updatedWs.id) updatedWs else ws
            }
            state.copy(
                activeWorkspace = updatedWs,
                workspaces = updatedWorkspaces,
                versions = listOf(newVersion) + state.versions,
                syncStatusMessage = "Successfully tagged release $tag!"
            )
        }
        clearStatusAfterDelay()
    }

    fun rollbackToVersion(version: WorkspaceVersion) {
        val updatedWs = _uiState.value.activeWorkspace.copy(
            currentVersionTag = version.versionTag,
            activeSpec = version.specSnapshot
        )

        _uiState.update { state ->
            val updatedWorkspaces = state.workspaces.map { ws ->
                if (ws.id == updatedWs.id) updatedWs else ws
            }
            state.copy(
                activeWorkspace = updatedWs,
                workspaces = updatedWorkspaces,
                activeSpec = version.specSnapshot,
                syncStatusMessage = "Rolled back to ${version.versionTag} snapshot"
            )
        }
        clearStatusAfterDelay()
    }

    fun simulateTeamEvent() {
        viewModelScope.launch {
            _uiState.update { it.copy(syncStatusMessage = "Collaborator Elena Rostova joined the workspace...") }
            delay(1200)

            val incomingComment = DataComment(
                workspaceId = _uiState.value.activeWorkspace.id,
                chartTitle = _uiState.value.activeSpec.title,
                authorName = "Elena Rostova",
                authorInitials = "ER",
                content = "Real-time sync alert: Anomaly filter triggered on US West edge cluster. Latency has stabilized.",
                dataPointContext = "US West edge nodes (58ms)",
                isResolved = false,
                timestamp = "Just now"
            )

            _uiState.update {
                it.copy(
                    comments = listOf(incomingComment) + it.comments,
                    syncStatusMessage = "Elena Rostova shared a new team insight"
                )
            }
            clearStatusAfterDelay()
        }
    }

    fun setSpecSheetVisible(visible: Boolean) {
        _uiState.update { it.copy(showSpecSheet = visible) }
    }

    fun setCommentsSheetVisible(visible: Boolean) {
        _uiState.update { it.copy(showCommentsSheet = visible) }
    }

    fun setVersioningSheetVisible(visible: Boolean) {
        _uiState.update { it.copy(showVersioningSheet = visible) }
    }

    private fun clearStatusAfterDelay() {
        viewModelScope.launch {
            delay(3500)
            _uiState.update { it.copy(syncStatusMessage = null) }
        }
    }
}
