package com.example.data.model

import java.util.UUID

enum class MarkType(val displayName: String, val vegaMark: String) {
    SCATTER("Scatter Plot", "circle"),
    LINE("Line Chart", "line"),
    AREA("Area Trend", "area"),
    BAR("Bar Chart", "bar"),
    HEATMAP("Heatmap Matrix", "rect")
}

enum class AggregationType(val displayName: String) {
    NONE("Raw Values"),
    SUM("Sum"),
    MEAN("Average"),
    COUNT("Count"),
    MAX("Maximum")
}

enum class FieldType {
    NUMERIC,
    CATEGORICAL,
    TEMPORAL
}

data class FieldDefinition(
    val name: String,
    val displayName: String,
    val type: FieldType,
    val unit: String = ""
)

data class DataRecord(
    val id: String = UUID.randomUUID().toString(),
    val values: Map<String, Any>
) {
    fun getDouble(key: String): Double {
        return when (val v = values[key]) {
            is Number -> v.toDouble()
            is String -> v.toDoubleOrNull() ?: 0.0
            else -> 0.0
        }
    }

    fun getString(key: String): String {
        return values[key]?.toString() ?: ""
    }
}

data class VegaDataset(
    val id: String,
    val title: String,
    val description: String,
    val fields: List<FieldDefinition>,
    val records: List<DataRecord>,
    val category: String
)

data class VegaEncoding(
    val xField: String,
    val yField: String,
    val colorField: String = "",
    val sizeField: String = "",
    val aggregation: AggregationType = AggregationType.NONE
)

data class VegaSpec(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val markType: MarkType,
    val encoding: VegaEncoding,
    val schemaVersion: String = "https://vega.github.io/schema/vega-lite/v5.json",
    val filterDimension: String = "",
    val filterValue: String = ""
) {
    fun toVegaLiteJson(datasetTitle: String): String {
        val colorSection = if (encoding.colorField.isNotBlank()) {
            """,
    "color": {"field": "${encoding.colorField}", "type": "nominal"}"""
        } else ""

        val sizeSection = if (encoding.sizeField.isNotBlank()) {
            """,
    "size": {"field": "${encoding.sizeField}", "type": "quantitative"}"""
        } else ""

        val aggPrefix = if (encoding.aggregation != AggregationType.NONE) {
            """"aggregate": "${encoding.aggregation.name.lowercase()}","""
        } else ""

        return """
{
  "${'$'}schema": "$schemaVersion",
  "description": "$description",
  "data": {"name": "$datasetTitle"},
  "mark": {"type": "${markType.vegaMark}", "tooltip": true},
  "encoding": {
    "x": {"field": "${encoding.xField}", "type": "quantitative"},
    "y": {$aggPrefix "field": "${encoding.yField}", "type": "quantitative"}$colorSection$sizeSection
  }
}
""".trimIndent()
    }
}

data class WorkspaceMember(
    val id: String,
    val name: String,
    val email: String,
    val initials: String,
    val role: String,
    val isOnline: Boolean = true
)

data class Workspace(
    val id: String,
    val name: String,
    val description: String,
    val currentVersionTag: String = "v1.2.0",
    val members: List<WorkspaceMember>,
    val activeDatasetId: String,
    val activeSpec: VegaSpec,
    val updatedAtFormatted: String = "Just now",
    val isPublic: Boolean = true
)

data class WorkspaceVersion(
    val id: String = UUID.randomUUID().toString(),
    val workspaceId: String,
    val versionTag: String, // e.g. "v1.0.0", "v1.1.0"
    val releaseNotes: String,
    val specSnapshot: VegaSpec,
    val authorName: String,
    val timestamp: String,
    val commitHash: String = UUID.randomUUID().toString().take(7)
)

data class CommentReply(
    val id: String = UUID.randomUUID().toString(),
    val authorName: String,
    val authorInitials: String,
    val content: String,
    val timestamp: String
)

data class DataComment(
    val id: String = UUID.randomUUID().toString(),
    val workspaceId: String,
    val chartTitle: String,
    val authorName: String,
    val authorInitials: String,
    val content: String,
    val dataPointContext: String = "", // e.g. "Point: US-East, Latency 142ms"
    val isResolved: Boolean = false,
    val timestamp: String,
    val replies: List<CommentReply> = emptyList()
)
