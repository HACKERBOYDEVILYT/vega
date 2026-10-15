package com.example.ui.components.filters

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*

@Composable
fun VegaFilterBar(
    datasets: List<VegaDataset>,
    activeDataset: VegaDataset,
    onSelectDataset: (VegaDataset) -> Unit,
    activeMarkType: MarkType,
    onSelectMarkType: (MarkType) -> Unit,
    activeFilterDimension: String,
    activeFilterValue: String,
    onFilterChanged: (dimension: String, value: String) -> Unit,
    onOpenSpecSheet: () -> Unit
) {
    Surface(
        color = VegaNavySurface,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, VegaNavyBorder),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("vega_filter_bar")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Row 1: Dataset Category Chips & Open Spec Editor Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    datasets.forEach { ds ->
                        val selected = ds.id == activeDataset.id
                        FilterChip(
                            selected = selected,
                            onClick = { onSelectDataset(ds) },
                            label = { Text(ds.title, fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = when (ds.category) {
                                        "Infrastructure" -> Icons.Default.CloudQueue
                                        "Energy" -> Icons.Default.Bolt
                                        else -> Icons.Default.TrendingUp
                                    },
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = VegaCyan,
                                selectedLabelColor = VegaOnCyan,
                                selectedLeadingIconColor = VegaOnCyan,
                                containerColor = VegaNavyBackground,
                                labelColor = VegaTextSecondary,
                                iconColor = VegaTextMuted
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selected,
                                borderColor = VegaNavyBorder,
                                selectedBorderColor = VegaCyan
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                FilledTonalButton(
                    onClick = onOpenSpecSheet,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = VegaCyanContainer,
                        contentColor = VegaCyan
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("open_spec_editor_button")
                ) {
                    Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Vega Spec", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Row 2: Mark Type Quick Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                MarkType.values().forEach { mark ->
                    val isSelected = mark == activeMarkType
                    Surface(
                        color = if (isSelected) VegaViolet else VegaNavyBackground,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) VegaViolet else VegaNavyBorder
                        ),
                        modifier = Modifier.testTag("mark_selector_${mark.name.lowercase()}")
                    ) {
                        Row(
                            modifier = Modifier
                                .clickable { onSelectMarkType(mark) }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = when (mark) {
                                    MarkType.SCATTER -> Icons.Default.ScatterPlot
                                    MarkType.LINE -> Icons.Default.ShowChart
                                    MarkType.AREA -> Icons.Default.AreaChart
                                    MarkType.BAR -> Icons.Default.BarChart
                                    MarkType.HEATMAP -> Icons.Default.GridOn
                                },
                                contentDescription = null,
                                tint = if (isSelected) VegaTextPrimary else VegaTextSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = mark.displayName,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) VegaTextPrimary else VegaTextSecondary
                            )
                        }
                    }
                }
            }

            // Row 3: Dimension Filter Chips (e.g. Region or Channel)
            val filterField = activeDataset.fields.find { it.type == FieldType.CATEGORICAL }
            if (filterField != null) {
                val distinctValues = remember(activeDataset, filterField) {
                    listOf("All") + activeDataset.records.map { it.getString(filterField.name) }.distinct()
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Filter ${filterField.displayName}:",
                        style = MaterialTheme.typography.labelSmall,
                        color = VegaTextMuted
                    )

                    distinctValues.forEach { valStr ->
                        val isSelected = if (valStr == "All") activeFilterValue.isBlank() else activeFilterValue == valStr
                        Surface(
                            color = if (isSelected) VegaCyan.copy(alpha = 0.2f) else VegaNavyBackground,
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) VegaCyan else VegaNavyBorder.copy(alpha = 0.6f)
                            )
                        ) {
                            Text(
                                text = valStr,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) VegaCyan else VegaTextSecondary,
                                modifier = Modifier
                                    .clickable {
                                        if (valStr == "All") {
                                            onFilterChanged("", "")
                                        } else {
                                            onFilterChanged(filterField.name, valStr)
                                        }
                                    }
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
