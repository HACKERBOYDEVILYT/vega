package com.example.ui.components.kpi

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DataRecord
import com.example.data.model.VegaDataset
import com.example.data.model.VegaSpec
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun ExecutiveKpiRow(
    dataset: VegaDataset,
    spec: VegaSpec,
    records: List<DataRecord>,
    modifier: Modifier = Modifier
) {
    val yValues = remember(records, spec.encoding.yField) {
        records.map { it.getDouble(spec.encoding.yField) }
    }
    val avgY = if (yValues.isNotEmpty()) yValues.average() else 0.0
    val maxY = yValues.maxOrNull() ?: 0.0
    val totalRecords = records.size

    val yFieldDef = dataset.fields.find { it.name == spec.encoding.yField }
    val unit = yFieldDef?.unit ?: ""

    Row(
        modifier = modifier
            .fillMaxWidth()
            .testTag("executive_kpi_row"),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        KpiCard(
            title = "Average ${yFieldDef?.displayName ?: "Metric"}",
            value = String.format(Locale.US, "%.1f %s", avgY, unit).trim(),
            subtext = "Mean across sample",
            icon = Icons.Default.Analytics,
            tint = VegaCyan,
            modifier = Modifier.weight(1f)
        )

        KpiCard(
            title = "Peak Observed",
            value = String.format(Locale.US, "%.1f %s", maxY, unit).trim(),
            subtext = "Max recorded value",
            icon = Icons.Default.VerticalAlignTop,
            tint = VegaViolet,
            modifier = Modifier.weight(1f)
        )

        KpiCard(
            title = "Active Records",
            value = "$totalRecords",
            subtext = "Filtered telemetry",
            icon = Icons.Default.Layers,
            tint = VegaEmerald,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun KpiCard(
    title: String,
    value: String,
    subtext: String,
    icon: ImageVector,
    tint: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = VegaNavySurface,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, VegaNavyBorder),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = VegaTextSecondary,
                    maxLines = 1
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = VegaTextPrimary,
                fontSize = 17.sp,
                maxLines = 1
            )

            Text(
                text = subtext,
                style = MaterialTheme.typography.labelSmall,
                color = VegaTextMuted,
                fontSize = 10.sp,
                maxLines = 1
            )
        }
    }
}
