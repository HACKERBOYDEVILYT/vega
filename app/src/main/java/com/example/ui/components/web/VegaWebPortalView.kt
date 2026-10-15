package com.example.ui.components.web

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.VegaDataset
import com.example.data.model.VegaSpec
import com.example.data.model.Workspace
import com.example.data.web.VegaWebGenerator
import com.example.ui.theme.*

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun VegaWebPortalView(
    workspace: Workspace,
    dataset: VegaDataset,
    spec: VegaSpec,
    modifier: Modifier = Modifier
) {
    var isDesktopViewport by remember { mutableStateOf(false) }
    var showEmbedDialog by remember { mutableStateOf(false) }
    var copiedFeedback by remember { mutableStateOf<String?>(null) }
    var webLoading by remember { mutableStateOf(true) }

    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val webUrl = "https://vega-analytics.cloud/ws/${workspace.id}/${workspace.currentVersionTag}"

    val htmlSource = remember(workspace, dataset, spec) {
        VegaWebGenerator.generateStandaloneHtml(workspace, dataset, spec)
    }

    var webViewInstance by remember { mutableStateOf<WebView?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("vega_web_portal_view")
    ) {
        // Browser Window Header / URL Bar
        Surface(
            color = VegaNavySurface,
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, VegaNavyBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Browser window traffic light buttons
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(modifier = Modifier.size(10.dp).background(VegaRose, CircleShape))
                        Box(modifier = Modifier.size(10.dp).background(VegaAmber, CircleShape))
                        Box(modifier = Modifier.size(10.dp).background(VegaEmerald, CircleShape))
                    }

                    // Viewport Toggle & Embed button
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = isDesktopViewport,
                            onClick = { isDesktopViewport = !isDesktopViewport },
                            label = { Text(if (isDesktopViewport) "Desktop (1024px)" else "Mobile Web") },
                            leadingIcon = {
                                Icon(
                                    if (isDesktopViewport) Icons.Default.Laptop else Icons.Default.PhoneIphone,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = VegaViolet,
                                selectedLabelColor = VegaTextPrimary,
                                containerColor = VegaNavyBackground,
                                labelColor = VegaTextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isDesktopViewport,
                                borderColor = VegaNavyBorder,
                                selectedBorderColor = VegaViolet
                            ),
                            modifier = Modifier.height(30.dp)
                        )

                        FilledTonalButton(
                            onClick = { showEmbedDialog = true },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = VegaCyanContainer,
                                contentColor = VegaCyan
                            ),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Embed Code", fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Realistic SSL Address Bar
                Surface(
                    color = VegaNavyBackground,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, VegaNavyBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "SSL Secure",
                                tint = VegaEmerald,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = webUrl,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                color = VegaCyan,
                                maxLines = 1
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(webUrl))
                                    copiedFeedback = "Copied Website URL"
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy URL", tint = VegaTextSecondary, modifier = Modifier.size(14.dp))
                            }

                            IconButton(
                                onClick = {
                                    webViewInstance?.loadDataWithBaseURL(
                                        "https://vega-analytics.cloud",
                                        htmlSource,
                                        "text/html",
                                        "UTF-8",
                                        null
                                    )
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = "Reload Web View", tint = VegaTextSecondary, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Live WebView Container
        Surface(
            color = VegaNavySurface,
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, VegaNavyBorder),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
            ) {
                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            settings.useWideViewPort = true
                            settings.loadWithOverviewMode = true
                            setBackgroundColor(0xFF0B0F19.toInt())

                            webViewClient = object : WebViewClient() {
                                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                    super.onPageStarted(view, url, favicon)
                                    webLoading = true
                                }
                                override fun onPageFinished(view: WebView?, url: String?) {
                                    super.onPageFinished(view, url)
                                    webLoading = false
                                }
                                override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                                    super.onReceivedError(view, request, error)
                                    webLoading = false
                                }
                            }

                            loadDataWithBaseURL(
                                "https://vega-analytics.cloud",
                                htmlSource,
                                "text/html",
                                "UTF-8",
                                null
                            )
                            webViewInstance = this
                        }
                    },
                    update = { view ->
                        view.loadDataWithBaseURL(
                            "https://vega-analytics.cloud",
                            htmlSource,
                            "text/html",
                            "UTF-8",
                            null
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                )

                if (webLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(VegaNavyBackground.copy(alpha = 0.7f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = VegaCyan)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Web Export Action Bar
        Surface(
            color = VegaNavySurface,
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, VegaNavyBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("HTML5 Web Application Bundle", style = MaterialTheme.typography.bodyMedium, color = VegaTextPrimary)
                    Text("Self-contained interactive Vega web file", style = MaterialTheme.typography.labelSmall, color = VegaTextMuted)
                }

                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(htmlSource))
                        copiedFeedback = "Copied Standalone HTML5 Code!"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VegaCyan, contentColor = VegaOnCyan),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Export HTML5")
                }
            }
        }
    }

    if (showEmbedDialog) {
        WebEmbedDialog(
            webUrl = webUrl,
            onDismiss = { showEmbedDialog = false },
            onCopy = { code ->
                clipboardManager.setText(AnnotatedString(code))
                copiedFeedback = "Copied Embed Code!"
            }
        )
    }

    // Feedback Toast
    if (copiedFeedback != null) {
        LaunchedEffect(copiedFeedback) {
            kotlinx.coroutines.delay(2500)
            copiedFeedback = null
        }
        Snackbar(
            containerColor = VegaCyanContainer,
            contentColor = VegaCyan,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(copiedFeedback ?: "")
        }
    }
}

@Composable
private fun WebEmbedDialog(
    webUrl: String,
    onDismiss: () -> Unit,
    onCopy: (String) -> Unit
) {
    val iframeSnippet = """<iframe src="$webUrl" width="100%" height="450" frameborder="0" style="border-radius:12px;box-shadow:0 8px 30px rgba(0,0,0,0.3);"></iframe>"""
    val scriptSnippet = """<div id="vega-container" data-workspace="$webUrl"></div>\n<script async src="https://vega-analytics.cloud/sdk/embed.js"></script>"""

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = VegaNavySurface,
        title = { Text("Website Embed Code", color = VegaTextPrimary) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    "Integrate this collaborative data analysis view into any website, blog, or enterprise portal.",
                    style = MaterialTheme.typography.bodySmall,
                    color = VegaTextSecondary
                )

                Text("Option 1: Responsive Iframe", style = MaterialTheme.typography.labelMedium, color = VegaCyan)
                Text(
                    text = iframeSnippet,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = VegaTextPrimary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(VegaNavyBackground, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                )

                Button(
                    onClick = { onCopy(iframeSnippet) },
                    colors = ButtonDefaults.buttonColors(containerColor = VegaCyan, contentColor = VegaOnCyan),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Copy Iframe Embed")
                }

                HorizontalDivider(color = VegaNavyBorder)

                Text("Option 2: Dynamic Web SDK", style = MaterialTheme.typography.labelMedium, color = VegaViolet)
                Text(
                    text = scriptSnippet,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = VegaTextPrimary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(VegaNavyBackground, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                )

                Button(
                    onClick = { onCopy(scriptSnippet) },
                    colors = ButtonDefaults.buttonColors(containerColor = VegaViolet, contentColor = VegaTextPrimary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Copy SDK Snippet")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close", color = VegaTextMuted) }
        }
    )
}
