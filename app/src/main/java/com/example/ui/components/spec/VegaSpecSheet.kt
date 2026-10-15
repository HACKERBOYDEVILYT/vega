package com.example.ui.components.spec

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VegaSpecSheet(
    spec: VegaSpec,
    dataset: VegaDataset,
    onSpecChanged: (VegaSpec) -> Unit,
    onDismiss: () -> Unit
) {
    var showRawJson by remember { mutableStateOf(false) }
    var copiedFeedback by remember { mutableStateOf(false) }
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val vegaLiteJson = remember(spec, dataset) { spec.toVegaLiteJson(dataset.title) }

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
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Vega-Lite Specification",
                        style = MaterialTheme.typography.titleLarge,
                        color = VegaTextPrimary
                    )
                    Text(
                        text = "Declarative visual encoding grammar",
                        style = MaterialTheme.typography.bodyMedium,
                        color = VegaTextSecondary
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilledTonalButton(
                        onClick = { showRawJson = !showRawJson },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = if (showRawJson) VegaCyanContainer else VegaNavySurfaceVariant,
                            contentColor = if (showRawJson) VegaCyan else VegaTextPrimary
                        ),
                        modifier = Modifier.testTag("toggle_raw_spec_button")
                    ) {
                        Icon(
                            imageVector = if (showRawJson) Icons.Default.Tune else Icons.Default.Code,
                            contentDescription = "Toggle Spec Mode",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (showRawJson) "Editor" else "Raw JSON")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (showRawJson) {
                // Raw JSON Vega-Lite Inspector
                Surface(
                    color = VegaNavySurface,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, VegaNavyBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "schema: vega-lite v5",
                                style = MaterialTheme.typography.labelSmall,
                                color = VegaCyan
                            )
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(vegaLiteJson))
                                    copiedFeedback = true
                                },
                                modifier = Modifier.testTag("copy_vega_spec_button")
                            ) {
                                Icon(
                                    imageVector = if (copiedFeedback) Icons.Default.Check else Icons.Default.ContentCopy,
                                    contentDescription = "Copy JSON",
                                    tint = if (copiedFeedback) VegaEmerald else VegaTextSecondary
                                )
                            }
                        }

                        Text(
                            text = vegaLiteJson,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = VegaTextPrimary,
                            lineHeight = 18.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(VegaNavyBackground.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        )
                    }
                }
            } else {
                // Visual Encoding Channels Configurator
                Text(
                    text = "Mark Representation",
                    style = MaterialTheme.typography.labelMedium,
                    color = VegaCyan
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Mark Type Selector Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MarkType.values().forEach { mark ->
                        val selected = spec.markType == mark
                        FilterChip(
                            selected = selected,
                            onClick = { onSpecChanged(spec.copy(markType = mark)) },
                            label = { Text(mark.displayName) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = VegaCyan,
                                selectedLabelColor = VegaOnCyan,
                                containerColor = VegaNavySurface,
                                labelColor = VegaTextSecondary
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

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Encoding Channels",
                    style = MaterialTheme.typography.labelMedium,
                    color = VegaCyan
                )
                Spacer(modifier = Modifier.height(10.dp))

                // X-Axis Channel
                FieldSelectorRow(
                    label = "X Position Channel",
                    currentField = spec.encoding.xField,
                    availableFields = dataset.fields,
                    onFieldSelected = { field ->
                        onSpecChanged(spec.copy(encoding = spec.encoding.copy(xField = field)))
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Y-Axis Channel
                FieldSelectorRow(
                    label = "Y Position Channel",
                    currentField = spec.encoding.yField,
                    availableFields = dataset.fields.filter { it.type == FieldType.NUMERIC },
                    onFieldSelected = { field ->
                        onSpecChanged(spec.copy(encoding = spec.encoding.copy(yField = field)))
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Color Channel
                FieldSelectorRow(
                    label = "Color Encoding (Nominal)",
                    currentField = spec.encoding.colorField,
                    availableFields = dataset.fields,
                    allowEmpty = true,
                    onFieldSelected = { field ->
                        onSpecChanged(spec.copy(encoding = spec.encoding.copy(colorField = field)))
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Size Channel
                FieldSelectorRow(
                    label = "Size Scale (Quantitative)",
                    currentField = spec.encoding.sizeField,
                    availableFields = dataset.fields.filter { it.type == FieldType.NUMERIC },
                    allowEmpty = true,
                    onFieldSelected = { field ->
                        onSpecChanged(spec.copy(encoding = spec.encoding.copy(sizeField = field)))
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Aggregation Transform
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(VegaNavySurface, RoundedCornerShape(10.dp))
                        .border(1.dp, VegaNavyBorder, RoundedCornerShape(10.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Aggregation Transform", style = MaterialTheme.typography.bodyMedium, color = VegaTextPrimary)
                        Text("Applied to Y channel", style = MaterialTheme.typography.labelSmall, color = VegaTextMuted)
                    }

                    var showAggMenu by remember { mutableStateOf(false) }
                    Box {
                        OutlinedButton(
                            onClick = { showAggMenu = true },
                            border = androidx.compose.foundation.BorderStroke(1.dp, VegaNavyBorder)
                        ) {
                            Text(spec.encoding.aggregation.displayName, color = VegaCyan)
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = VegaCyan)
                        }

                        DropdownMenu(
                            expanded = showAggMenu,
                            onDismissRequest = { showAggMenu = false }
                        ) {
                            AggregationType.values().forEach { agg ->
                                DropdownMenuItem(
                                    text = { Text(agg.displayName) },
                                    onClick = {
                                        onSpecChanged(spec.copy(encoding = spec.encoding.copy(aggregation = agg)))
                                        showAggMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun FieldSelectorRow(
    label: String,
    currentField: String,
    availableFields: List<FieldDefinition>,
    allowEmpty: Boolean = false,
    onFieldSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val displayCurrent = availableFields.find { it.name == currentField }?.displayName
        ?: if (currentField.isBlank() && allowEmpty) "None" else currentField

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(VegaNavySurface, RoundedCornerShape(10.dp))
            .border(1.dp, VegaNavyBorder, RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(label, style = MaterialTheme.typography.bodyMedium, color = VegaTextPrimary)
            Text(
                if (currentField.isNotBlank()) "field: $currentField" else "Unset",
                style = MaterialTheme.typography.labelSmall,
                color = VegaTextMuted
            )
        }

        Box {
            OutlinedButton(
                onClick = { expanded = true },
                border = androidx.compose.foundation.BorderStroke(1.dp, VegaNavyBorder)
            ) {
                Text(displayCurrent, color = VegaCyan, maxLines = 1)
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = VegaCyan)
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                if (allowEmpty) {
                    DropdownMenuItem(
                        text = { Text("None (Off)") },
                        onClick = {
                            onFieldSelected("")
                            expanded = false
                        }
                    )
                }
                availableFields.forEach { field ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(field.displayName)
                                Text("${field.name} (${field.type})", style = MaterialTheme.typography.labelSmall, color = VegaTextMuted)
                            }
                        },
                        onClick = {
                            onFieldSelected(field.name)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
