package com.example.ui.components.chart

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

// Color scale mappings for nominal/categorical values
private val CategoricalPalette = listOf(
    VegaCyan,
    VegaViolet,
    VegaEmerald,
    VegaAmber,
    VegaRose,
    VegaBlue,
    Color(0xFF38BDF8),
    Color(0xFFA78BFA)
)

@OptIn(ExperimentalTextApi::class)
@Composable
fun VegaChartEngine(
    dataset: VegaDataset,
    spec: VegaSpec,
    records: List<DataRecord>,
    selectedRecord: DataRecord?,
    onRecordSelected: (DataRecord?, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var touchPosition by remember { mutableStateOf<Offset?>(null) }
    val textMeasurer = rememberTextMeasurer()

    val xFieldDef = dataset.fields.find { it.name == spec.encoding.xField }
    val yFieldDef = dataset.fields.find { it.name == spec.encoding.yField }

    // Aggregate records if specified
    val processedRecords = remember(records, spec.encoding) {
        if (spec.encoding.aggregation == AggregationType.NONE) {
            records
        } else {
            // Group by xField if categorical/temporal, aggregate yField
            records.groupBy { it.getString(spec.encoding.xField) }
                .map { (key, group) ->
                    val yVals = group.map { it.getDouble(spec.encoding.yField) }
                    val aggregatedY = when (spec.encoding.aggregation) {
                        AggregationType.SUM -> yVals.sum()
                        AggregationType.MEAN -> if (yVals.isNotEmpty()) yVals.average() else 0.0
                        AggregationType.COUNT -> group.size.toDouble()
                        AggregationType.MAX -> yVals.maxOrNull() ?: 0.0
                        AggregationType.NONE -> yVals.firstOrNull() ?: 0.0
                    }
                    val first = group.first()
                    first.copy(
                        values = first.values + mapOf(
                            spec.encoding.yField to aggregatedY
                        )
                    )
                }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(340.dp)
            .background(VegaNavySurface, RoundedCornerShape(16.dp))
            .border(1.dp, VegaNavyBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
            .testTag("vega_chart_canvas_container")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Chart Title & Active Spec Channels Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = spec.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = VegaTextPrimary,
                        maxLines = 1
                    )
                    Text(
                        text = "Mark: ${spec.markType.displayName} • X: ${xFieldDef?.displayName ?: spec.encoding.xField} • Y: ${yFieldDef?.displayName ?: spec.encoding.yField}",
                        style = MaterialTheme.typography.bodySmall,
                        color = VegaTextSecondary
                    )
                }
                Surface(
                    color = VegaCyanContainer.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, VegaCyan.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "${processedRecords.size} Records",
                        style = MaterialTheme.typography.labelSmall,
                        color = VegaCyan,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Canvas Rendering Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(processedRecords, spec) {
                            detectTapGestures(
                                onPress = { offset ->
                                    touchPosition = offset
                                    tryAwaitRelease()
                                    // keep touch tooltip until clicked elsewhere
                                },
                                onTap = { offset ->
                                    touchPosition = offset
                                    val matched = findNearestRecord(
                                        offset = offset,
                                        size = size,
                                        records = processedRecords,
                                        spec = spec
                                    )
                                    if (matched != null) {
                                        val summary = formatRecordSummary(matched, spec)
                                        onRecordSelected(matched, summary)
                                    } else {
                                        onRecordSelected(null, "")
                                    }
                                }
                            )
                        }
                ) {
                    if (processedRecords.isEmpty()) return@Canvas

                    val padLeft = 46.dp.toPx()
                    val padBottom = 28.dp.toPx()
                    val padTop = 16.dp.toPx()
                    val padRight = 16.dp.toPx()

                    val chartWidth = size.width - padLeft - padRight
                    val chartHeight = size.height - padTop - padBottom

                    // Draw grid lines
                    drawGridAxes(
                        padLeft = padLeft,
                        padTop = padTop,
                        chartWidth = chartWidth,
                        chartHeight = chartHeight,
                        textMeasurer = textMeasurer,
                        spec = spec,
                        records = processedRecords
                    )

                    // Draw specific mark type
                    when (spec.markType) {
                        MarkType.SCATTER -> drawScatterMarks(
                            records = processedRecords,
                            spec = spec,
                            padLeft = padLeft,
                            padTop = padTop,
                            chartWidth = chartWidth,
                            chartHeight = chartHeight,
                            selectedRecord = selectedRecord
                        )
                        MarkType.LINE -> drawLineMarks(
                            records = processedRecords,
                            spec = spec,
                            padLeft = padLeft,
                            padTop = padTop,
                            chartWidth = chartWidth,
                            chartHeight = chartHeight,
                            isArea = false,
                            selectedRecord = selectedRecord
                        )
                        MarkType.AREA -> drawLineMarks(
                            records = processedRecords,
                            spec = spec,
                            padLeft = padLeft,
                            padTop = padTop,
                            chartWidth = chartWidth,
                            chartHeight = chartHeight,
                            isArea = true,
                            selectedRecord = selectedRecord
                        )
                        MarkType.BAR -> drawBarMarks(
                            records = processedRecords,
                            spec = spec,
                            padLeft = padLeft,
                            padTop = padTop,
                            chartWidth = chartWidth,
                            chartHeight = chartHeight,
                            selectedRecord = selectedRecord,
                            textMeasurer = textMeasurer
                        )
                        MarkType.HEATMAP -> drawHeatmapMarks(
                            records = processedRecords,
                            spec = spec,
                            padLeft = padLeft,
                            padTop = padTop,
                            chartWidth = chartWidth,
                            chartHeight = chartHeight,
                            selectedRecord = selectedRecord,
                            textMeasurer = textMeasurer
                        )
                    }
                }

                // Interactive HUD Tooltip if point selected
                if (selectedRecord != null) {
                    val recordSummary = formatRecordSummary(selectedRecord, spec)
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .testTag("chart_hud_tooltip"),
                        color = VegaNavyBackground.copy(alpha = 0.92f),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, VegaCyan.copy(alpha = 0.6f)),
                        shadowElevation = 8.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(VegaCyan, CircleShape)
                            )
                            Column {
                                Text(
                                    text = recordSummary,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = VegaTextPrimary
                                )
                                Text(
                                    text = "Tap 'Comment' to pin collaborative note",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = VegaCyan,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }

            // Bottom categorical legend
            val colorField = spec.encoding.colorField
            if (colorField.isNotBlank()) {
                val categories = remember(processedRecords, colorField) {
                    processedRecords.map { it.getString(colorField) }.distinct().take(5)
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Legend ($colorField):",
                        style = MaterialTheme.typography.labelSmall,
                        color = VegaTextMuted
                    )
                    categories.forEachIndexed { index, cat ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        CategoricalPalette[index % CategoricalPalette.size],
                                        CircleShape
                                    )
                            )
                            Text(
                                text = cat,
                                style = MaterialTheme.typography.labelSmall,
                                color = VegaTextSecondary,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawGridAxes(
    padLeft: Float,
    padTop: Float,
    chartWidth: Float,
    chartHeight: Float,
    textMeasurer: TextMeasurer,
    spec: VegaSpec,
    records: List<DataRecord>
) {
    val gridColor = Color(0xFF1E293B)
    val axisColor = Color(0xFF475569)

    // Horizontal grid lines (4 lines)
    val numSteps = 4
    for (i in 0..numSteps) {
        val y = padTop + chartHeight * (i.toFloat() / numSteps)
        drawLine(
            color = gridColor,
            start = Offset(padLeft, y),
            end = Offset(padLeft + chartWidth, y),
            strokeWidth = 1f
        )
    }

    // X Axis line
    drawLine(
        color = axisColor,
        start = Offset(padLeft, padTop + chartHeight),
        end = Offset(padLeft + chartWidth, padTop + chartHeight),
        strokeWidth = 2f
    )

    // Y Axis line
    drawLine(
        color = axisColor,
        start = Offset(padLeft, padTop),
        end = Offset(padLeft, padTop + chartHeight),
        strokeWidth = 2f
    )
}

private fun DrawScope.drawScatterMarks(
    records: List<DataRecord>,
    spec: VegaSpec,
    padLeft: Float,
    padTop: Float,
    chartWidth: Float,
    chartHeight: Float,
    selectedRecord: DataRecord?
) {
    val xValues = records.map { it.getDouble(spec.encoding.xField) }
    val yValues = records.map { it.getDouble(spec.encoding.yField) }
    val minX = xValues.minOrNull() ?: 0.0
    val maxX = max(xValues.maxOrNull() ?: 1.0, minX + 0.1)
    val minY = min(yValues.minOrNull() ?: 0.0, 0.0)
    val maxY = max(yValues.maxOrNull() ?: 1.0, minY + 0.1)

    val colorCats = records.map { it.getString(spec.encoding.colorField) }.distinct()

    records.forEach { record ->
        val xVal = record.getDouble(spec.encoding.xField)
        val yVal = record.getDouble(spec.encoding.yField)

        val nx = ((xVal - minX) / (maxX - minX)).toFloat()
        val ny = ((yVal - minY) / (maxY - minY)).toFloat()

        val cx = padLeft + nx * chartWidth
        val cy = padTop + (1f - ny) * chartHeight

        val colorIdx = if (spec.encoding.colorField.isNotBlank()) {
            colorCats.indexOf(record.getString(spec.encoding.colorField)).coerceAtLeast(0)
        } else 0
        val markColor = CategoricalPalette[colorIdx % CategoricalPalette.size]

        val radius = if (spec.encoding.sizeField.isNotBlank()) {
            val sizeVal = record.getDouble(spec.encoding.sizeField)
            (6f + (sizeVal / 15f).toFloat()).coerceIn(6f, 16f)
        } else 8f

        val isSelected = selectedRecord?.id == record.id

        // Glow layer
        drawCircle(
            color = markColor.copy(alpha = if (isSelected) 0.5f else 0.25f),
            radius = radius + if (isSelected) 8f else 4f,
            center = Offset(cx, cy)
        )

        // Core mark
        drawCircle(
            color = if (isSelected) Color.White else markColor,
            radius = radius,
            center = Offset(cx, cy)
        )

        if (isSelected) {
            drawCircle(
                color = VegaCyan,
                radius = radius + 4f,
                center = Offset(cx, cy),
                style = Stroke(width = 2f)
            )
        }
    }
}

private fun DrawScope.drawLineMarks(
    records: List<DataRecord>,
    spec: VegaSpec,
    padLeft: Float,
    padTop: Float,
    chartWidth: Float,
    chartHeight: Float,
    isArea: Boolean,
    selectedRecord: DataRecord?
) {
    if (records.size < 2) return

    val yValues = records.map { it.getDouble(spec.encoding.yField) }
    val minY = min(yValues.minOrNull() ?: 0.0, 0.0)
    val maxY = max(yValues.maxOrNull() ?: 1.0, minY + 0.1)

    val points = records.mapIndexed { idx, record ->
        val xFrac = idx.toFloat() / (records.size - 1)
        val yVal = record.getDouble(spec.encoding.yField)
        val yFrac = ((yVal - minY) / (maxY - minY)).toFloat()
        Offset(padLeft + xFrac * chartWidth, padTop + (1f - yFrac) * chartHeight)
    }

    val linePath = Path().apply {
        moveTo(points.first().x, points.first().y)
        for (i in 1 until points.size) {
            val p0 = points[i - 1]
            val p1 = points[i]
            val midX = (p0.x + p1.x) / 2f
            cubicTo(midX, p0.y, midX, p1.y, p1.x, p1.y)
        }
    }

    if (isArea) {
        val areaPath = Path().apply {
            addPath(linePath)
            lineTo(points.last().x, padTop + chartHeight)
            lineTo(points.first().x, padTop + chartHeight)
            close()
        }
        drawPath(
            path = areaPath,
            brush = Brush.verticalGradient(
                colors = listOf(VegaCyan.copy(alpha = 0.45f), VegaNavySurface.copy(alpha = 0.05f)),
                startY = padTop,
                endY = padTop + chartHeight
            )
        )
    }

    // Draw main stroke
    drawPath(
        path = linePath,
        color = VegaCyan,
        style = Stroke(width = 3f, cap = StrokeCap.Round)
    )

    // Data points nodes
    points.forEachIndexed { index, pt ->
        val record = records[index]
        val isSelected = selectedRecord?.id == record.id
        drawCircle(
            color = if (isSelected) VegaAmber else VegaCyan,
            radius = if (isSelected) 7f else 4f,
            center = pt
        )
        if (isSelected) {
            drawCircle(
                color = Color.White,
                radius = 3f,
                center = pt
            )
        }
    }
}

private fun DrawScope.drawBarMarks(
    records: List<DataRecord>,
    spec: VegaSpec,
    padLeft: Float,
    padTop: Float,
    chartWidth: Float,
    chartHeight: Float,
    selectedRecord: DataRecord?,
    textMeasurer: TextMeasurer
) {
    val yValues = records.map { it.getDouble(spec.encoding.yField) }
    val minY = 0.0
    val maxY = max(yValues.maxOrNull() ?: 1.0, 1.0)

    val barCount = records.size
    val totalSlotWidth = chartWidth / barCount
    val barWidth = (totalSlotWidth * 0.65f).coerceAtLeast(8f)

    records.forEachIndexed { idx, record ->
        val yVal = record.getDouble(spec.encoding.yField)
        val yFrac = ((yVal - minY) / (maxY - minY)).toFloat().coerceIn(0f, 1f)
        val bHeight = yFrac * chartHeight

        val slotCenterX = padLeft + (idx + 0.5f) * totalSlotWidth
        val left = slotCenterX - barWidth / 2f
        val top = padTop + (chartHeight - bHeight)

        val isSelected = selectedRecord?.id == record.id
        val barColor = if (isSelected) VegaCyan else CategoricalPalette[idx % CategoricalPalette.size]

        // Rounded bar
        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(barColor, barColor.copy(alpha = 0.6f)),
                startY = top,
                endY = top + bHeight
            ),
            topLeft = Offset(left, top),
            size = Size(barWidth, bHeight),
            cornerRadius = CornerRadius(6f, 6f)
        )

        if (isSelected) {
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(left - 2f, top - 2f),
                size = Size(barWidth + 4f, bHeight + 4f),
                cornerRadius = CornerRadius(8f, 8f),
                style = Stroke(width = 2f)
            )
        }
    }
}

private fun DrawScope.drawHeatmapMarks(
    records: List<DataRecord>,
    spec: VegaSpec,
    padLeft: Float,
    padTop: Float,
    chartWidth: Float,
    chartHeight: Float,
    selectedRecord: DataRecord?,
    textMeasurer: TextMeasurer
) {
    val columns = 4
    val rows = (records.size + columns - 1) / columns
    val cellW = chartWidth / columns
    val cellH = chartHeight / rows

    val yValues = records.map { it.getDouble(spec.encoding.yField) }
    val minY = yValues.minOrNull() ?: 0.0
    val maxY = max(yValues.maxOrNull() ?: 1.0, minY + 0.1)

    records.forEachIndexed { index, record ->
        val col = index % columns
        val row = index / columns

        val left = padLeft + col * cellW
        val top = padTop + row * cellH

        val yVal = record.getDouble(spec.encoding.yField)
        val frac = ((yVal - minY) / (maxY - minY)).toFloat().coerceIn(0f, 1f)

        val cellColor = lerp(VegaNavySurfaceVariant, VegaCyan, frac)
        val isSelected = selectedRecord?.id == record.id

        drawRoundRect(
            color = if (isSelected) VegaViolet else cellColor,
            topLeft = Offset(left + 2f, top + 2f),
            size = Size(cellW - 4f, cellH - 4f),
            cornerRadius = CornerRadius(4f, 4f)
        )

        if (isSelected) {
            drawRoundRect(
                color = VegaCyan,
                topLeft = Offset(left + 1f, top + 1f),
                size = Size(cellW - 2f, cellH - 2f),
                cornerRadius = CornerRadius(5f, 5f),
                style = Stroke(width = 2f)
            )
        }
    }
}

private fun findNearestRecord(
    offset: Offset,
    size: androidx.compose.ui.unit.IntSize,
    records: List<DataRecord>,
    spec: VegaSpec
): DataRecord? {
    if (records.isEmpty()) return null
    val padLeft = 46f
    val padRight = 16f
    val padTop = 16f
    val padBottom = 28f

    val chartWidth = size.width - padLeft - padRight
    val chartHeight = size.height - padTop - padBottom

    val relX = (offset.x - padLeft) / chartWidth
    if (spec.markType == MarkType.BAR || spec.markType == MarkType.LINE || spec.markType == MarkType.AREA) {
        val idx = (relX * records.size).toInt().coerceIn(0, records.size - 1)
        return records[idx]
    }

    // For scatter, find point with minimum euclidean distance
    val xValues = records.map { it.getDouble(spec.encoding.xField) }
    val yValues = records.map { it.getDouble(spec.encoding.yField) }
    val minX = xValues.minOrNull() ?: 0.0
    val maxX = max(xValues.maxOrNull() ?: 1.0, minX + 0.1)
    val minY = min(yValues.minOrNull() ?: 0.0, 0.0)
    val maxY = max(yValues.maxOrNull() ?: 1.0, minY + 0.1)

    var nearest: DataRecord? = null
    var minDistance = Float.MAX_VALUE

    records.forEach { record ->
        val xVal = record.getDouble(spec.encoding.xField)
        val yVal = record.getDouble(spec.encoding.yField)
        val nx = ((xVal - minX) / (maxX - minX)).toFloat()
        val ny = ((yVal - minY) / (maxY - minY)).toFloat()
        val px = padLeft + nx * chartWidth
        val py = padTop + (1f - ny) * chartHeight

        val dist = sqrt((px - offset.x) * (px - offset.x) + (py - offset.y) * (py - offset.y))
        if (dist < minDistance && dist < 60f) {
            minDistance = dist
            nearest = record
        }
    }

    return nearest ?: records.firstOrNull()
}

fun formatRecordSummary(record: DataRecord, spec: VegaSpec): String {
    val xVal = record.values[spec.encoding.xField] ?: ""
    val yVal = record.values[spec.encoding.yField] ?: ""
    val colorVal = if (spec.encoding.colorField.isNotBlank()) {
        " | ${record.values[spec.encoding.colorField]}"
    } else ""
    return "${spec.encoding.xField}: $xVal, ${spec.encoding.yField}: $yVal$colorVal"
}
