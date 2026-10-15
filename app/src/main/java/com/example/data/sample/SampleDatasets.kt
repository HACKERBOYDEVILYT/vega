package com.example.data.sample

import com.example.data.model.*

object SampleDatasets {

    val cloudInfraDataset = VegaDataset(
        id = "cloud_infra",
        title = "Global Cloud & Edge Telemetry",
        description = "Network latency, throughput, CPU utilization, and packet error telemetry across global edge nodes.",
        category = "Infrastructure",
        fields = listOf(
            FieldDefinition("node", "Edge Node", FieldType.CATEGORICAL),
            FieldDefinition("region", "Region", FieldType.CATEGORICAL),
            FieldDefinition("latency_ms", "Latency", FieldType.NUMERIC, "ms"),
            FieldDefinition("throughput_k", "Throughput", FieldType.NUMERIC, "k req/s"),
            FieldDefinition("cpu_load_pct", "CPU Load", FieldType.NUMERIC, "%"),
            FieldDefinition("error_rate_pct", "Error Rate", FieldType.NUMERIC, "%")
        ),
        records = listOf(
            DataRecord(values = mapOf("node" to "us-east-1a", "region" to "US East", "latency_ms" to 34.2, "throughput_k" to 142.0, "cpu_load_pct" to 68.4, "error_rate_pct" to 0.04)),
            DataRecord(values = mapOf("node" to "us-east-1b", "region" to "US East", "latency_ms" to 38.6, "throughput_k" to 125.0, "cpu_load_pct" to 74.1, "error_rate_pct" to 0.08)),
            DataRecord(values = mapOf("node" to "us-west-2a", "region" to "US West", "latency_ms" to 58.1, "throughput_k" to 98.4, "cpu_load_pct" to 52.0, "error_rate_pct" to 0.02)),
            DataRecord(values = mapOf("node" to "us-west-2b", "region" to "US West", "latency_ms" to 62.4, "throughput_k" to 88.0, "cpu_load_pct" to 61.2, "error_rate_pct" to 0.05)),
            DataRecord(values = mapOf("node" to "eu-central-1", "region" to "Europe", "latency_ms" to 82.5, "throughput_k" to 184.2, "cpu_load_pct" to 82.7, "error_rate_pct" to 0.12)),
            DataRecord(values = mapOf("node" to "eu-west-1", "region" to "Europe", "latency_ms" to 76.8, "throughput_k" to 164.0, "cpu_load_pct" to 71.9, "error_rate_pct" to 0.07)),
            DataRecord(values = mapOf("node" to "ap-south-1", "region" to "Asia Pac", "latency_ms" to 134.0, "throughput_k" to 112.5, "cpu_load_pct" to 89.4, "error_rate_pct" to 0.28)),
            DataRecord(values = mapOf("node" to "ap-southeast-1", "region" to "Asia Pac", "latency_ms" to 118.2, "throughput_k" to 130.0, "cpu_load_pct" to 79.8, "error_rate_pct" to 0.18)),
            DataRecord(values = mapOf("node" to "ap-northeast-1", "region" to "Asia Pac", "latency_ms" to 104.5, "throughput_k" to 145.8, "cpu_load_pct" to 64.2, "error_rate_pct" to 0.09)),
            DataRecord(values = mapOf("node" to "sa-east-1", "region" to "South America", "latency_ms" to 158.4, "throughput_k" to 64.0, "cpu_load_pct" to 58.3, "error_rate_pct" to 0.15)),
            DataRecord(values = mapOf("node" to "af-south-1", "region" to "Africa", "latency_ms" to 172.9, "throughput_k" to 48.0, "cpu_load_pct" to 49.1, "error_rate_pct" to 0.22)),
            DataRecord(values = mapOf("node" to "me-central-1", "region" to "Middle East", "latency_ms" to 124.6, "throughput_k" to 92.4, "cpu_load_pct" to 66.7, "error_rate_pct" to 0.11))
        )
    )

    val renewableGridDataset = VegaDataset(
        id = "renewable_grid",
        title = "Clean Energy & Grid Supply",
        description = "Hourly temporal power flow, wind & solar yield, and battery buffer states.",
        category = "Energy",
        fields = listOf(
            FieldDefinition("hour", "Hour of Day", FieldType.TEMPORAL),
            FieldDefinition("solar_mw", "Solar Generation", FieldType.NUMERIC, "MW"),
            FieldDefinition("wind_mw", "Wind Generation", FieldType.NUMERIC, "MW"),
            FieldDefinition("grid_demand_mw", "Grid Demand", FieldType.NUMERIC, "MW"),
            FieldDefinition("battery_pct", "Battery Reserve", FieldType.NUMERIC, "%")
        ),
        records = listOf(
            DataRecord(values = mapOf("hour" to "00:00", "solar_mw" to 0.0, "wind_mw" to 180.0, "grid_demand_mw" to 220.0, "battery_pct" to 78.0)),
            DataRecord(values = mapOf("hour" to "03:00", "solar_mw" to 0.0, "wind_mw" to 210.0, "grid_demand_mw" to 190.0, "battery_pct" to 84.0)),
            DataRecord(values = mapOf("hour" to "06:00", "solar_mw" to 45.0, "wind_mw" to 195.0, "grid_demand_mw" to 260.0, "battery_pct" to 75.0)),
            DataRecord(values = mapOf("hour" to "09:00", "solar_mw" to 240.0, "wind_mw" to 160.0, "grid_demand_mw" to 340.0, "battery_pct" to 82.0)),
            DataRecord(values = mapOf("hour" to "12:00", "solar_mw" to 420.0, "wind_mw" to 140.0, "grid_demand_mw" to 410.0, "battery_pct" to 95.0)),
            DataRecord(values = mapOf("hour" to "15:00", "solar_mw" to 380.0, "wind_mw" to 155.0, "grid_demand_mw" to 390.0, "battery_pct" to 98.0)),
            DataRecord(values = mapOf("hour" to "18:00", "solar_mw" to 90.0, "wind_mw" to 230.0, "grid_demand_mw" to 460.0, "battery_pct" to 85.0)),
            DataRecord(values = mapOf("hour" to "21:00", "solar_mw" to 0.0, "wind_mw" to 260.0, "grid_demand_mw" to 320.0, "battery_pct" to 72.0))
        )
    )

    val ecommerceDataset = VegaDataset(
        id = "ecommerce_funnel",
        title = "Omnichannel Funnel & Revenue",
        description = "Channel acquisition breakdown, conversion ratios, and executive revenue contributions.",
        category = "Commercial",
        fields = listOf(
            FieldDefinition("channel", "Acquisition Channel", FieldType.CATEGORICAL),
            FieldDefinition("visitors_k", "Visitors", FieldType.NUMERIC, "k"),
            FieldDefinition("conversions", "Purchases", FieldType.NUMERIC),
            FieldDefinition("revenue_k", "Gross Revenue", FieldType.NUMERIC, "\$k"),
            FieldDefinition("aov_usd", "Average Order Value", FieldType.NUMERIC, "\$")
        ),
        records = listOf(
            DataRecord(values = mapOf("channel" to "Direct / Brand", "visitors_k" to 450.0, "conversions" to 18400.0, "revenue_k" to 2208.0, "aov_usd" to 120.0)),
            DataRecord(values = mapOf("channel" to "Organic Search", "visitors_k" to 820.0, "conversions" to 29500.0, "revenue_k" to 3245.0, "aov_usd" to 110.0)),
            DataRecord(values = mapOf("channel" to "Paid Search", "visitors_k" to 340.0, "conversions" to 15800.0, "revenue_k" to 2133.0, "aov_usd" to 135.0)),
            DataRecord(values = mapOf("channel" to "Partner Referrals", "visitors_k" to 180.0, "conversions" to 9400.0, "revenue_k" to 1410.0, "aov_usd" to 150.0)),
            DataRecord(values = mapOf("channel" to "Social Media", "visitors_k" to 610.0, "conversions" to 12800.0, "revenue_k" to 1152.0, "aov_usd" to 90.0)),
            DataRecord(values = mapOf("channel" to "Email Campaigns", "visitors_k" to 220.0, "conversions" to 16200.0, "revenue_k" to 2268.0, "aov_usd" to 140.0))
        )
    )

    val allDatasets = listOf(cloudInfraDataset, renewableGridDataset, ecommerceDataset)

    val defaultWorkspaces = listOf(
        Workspace(
            id = "ws_tech_ops",
            name = "Executive Cloud Infrastructure",
            description = "Real-time edge telemetry, global routing latency, and SLA performance review.",
            currentVersionTag = "v1.2.0",
            members = listOf(
                WorkspaceMember("m1", "Ayan Islam", "ayan@corp.io", "AI", "Lead Architect", isOnline = true),
                WorkspaceMember("m2", "Elena Rostova", "elena@corp.io", "ER", "Data Analyst", isOnline = true),
                WorkspaceMember("m3", "David Chen", "david@corp.io", "DC", "Ops Director", isOnline = false)
            ),
            activeDatasetId = "cloud_infra",
            activeSpec = VegaSpec(
                title = "Edge Latency vs. Throughput Distribution",
                description = "Visualizing latency (ms) plotted against throughput (k req/s) colored by region.",
                markType = MarkType.SCATTER,
                encoding = VegaEncoding(
                    xField = "latency_ms",
                    yField = "throughput_k",
                    colorField = "region",
                    sizeField = "cpu_load_pct"
                )
            ),
            updatedAtFormatted = "2 mins ago"
        ),
        Workspace(
            id = "ws_clean_energy",
            name = "Renewable Grid Command",
            description = "Hourly temporal power flow, wind/solar mix, and battery buffer state monitoring.",
            currentVersionTag = "v2.0.4",
            members = listOf(
                WorkspaceMember("m1", "Ayan Islam", "ayan@corp.io", "AI", "Lead Architect", isOnline = true),
                WorkspaceMember("m4", "Siddharth Rao", "sid@grid.org", "SR", "Energy Engineer", isOnline = true)
            ),
            activeDatasetId = "renewable_grid",
            activeSpec = VegaSpec(
                title = "Solar & Wind Generation vs Grid Demand",
                description = "Temporal trajectory of clean energy generation relative to peak demand.",
                markType = MarkType.AREA,
                encoding = VegaEncoding(
                    xField = "hour",
                    yField = "solar_mw",
                    colorField = "battery_pct"
                )
            ),
            updatedAtFormatted = "18 mins ago"
        ),
        Workspace(
            id = "ws_commerce_growth",
            name = "Commercial Growth & Revenue",
            description = "Acquisition channels, conversion efficiency, and gross merchant volume breakdown.",
            currentVersionTag = "v1.0.1",
            members = listOf(
                WorkspaceMember("m1", "Ayan Islam", "ayan@corp.io", "AI", "Lead Architect", isOnline = true),
                WorkspaceMember("m5", "Camila Silva", "camila@sales.io", "CS", "Growth VP", isOnline = true)
            ),
            activeDatasetId = "ecommerce_funnel",
            activeSpec = VegaSpec(
                title = "Channel Revenue vs. Order Volume",
                description = "Gross revenue performance across primary marketing and organic acquisition channels.",
                markType = MarkType.BAR,
                encoding = VegaEncoding(
                    xField = "channel",
                    yField = "revenue_k",
                    colorField = "channel",
                    sizeField = "aov_usd"
                )
            ),
            updatedAtFormatted = "1 hour ago"
        )
    )

    val initialVersions = mapOf(
        "ws_tech_ops" to listOf(
            WorkspaceVersion(
                workspaceId = "ws_tech_ops",
                versionTag = "v1.2.0",
                releaseNotes = "Added CPU load size channel encoding and filtered anomaly spikes in APAC region.",
                specSnapshot = defaultWorkspaces[0].activeSpec,
                authorName = "Ayan Islam",
                timestamp = "Today, 18:40"
            ),
            WorkspaceVersion(
                workspaceId = "ws_tech_ops",
                versionTag = "v1.1.0",
                releaseNotes = "Configured regional color categorical grouping for scatter mark view.",
                specSnapshot = defaultWorkspaces[0].activeSpec.copy(
                    markType = MarkType.SCATTER,
                    encoding = defaultWorkspaces[0].activeSpec.encoding.copy(sizeField = "")
                ),
                authorName = "Elena Rostova",
                timestamp = "Yesterday, 14:15"
            ),
            WorkspaceVersion(
                workspaceId = "ws_tech_ops",
                versionTag = "v1.0.0",
                releaseNotes = "Initial specification baseline with linear latency vs throughput mapping.",
                specSnapshot = defaultWorkspaces[0].activeSpec.copy(
                    markType = MarkType.LINE,
                    encoding = VegaEncoding(xField = "latency_ms", yField = "throughput_k")
                ),
                authorName = "David Chen",
                timestamp = "Oct 10, 09:30"
            )
        )
    )

    val initialComments = listOf(
        DataComment(
            workspaceId = "ws_tech_ops",
            chartTitle = "Edge Latency vs. Throughput Distribution",
            authorName = "Elena Rostova",
            authorInitials = "ER",
            content = "Latency spike in APAC nodes (ap-south-1 @ 134ms) exceeds our 100ms executive threshold. Can we test edge BGP rerouting?",
            dataPointContext = "ap-south-1 (134ms, 112k req/s)",
            isResolved = false,
            timestamp = "12m ago",
            replies = listOf(
                CommentReply(
                    authorName = "Ayan Islam",
                    authorInitials = "AI",
                    content = "Verified. Running automated reroute through Singapore point of presence now.",
                    timestamp = "8m ago"
                )
            )
        ),
        DataComment(
            workspaceId = "ws_tech_ops",
            chartTitle = "Edge Latency vs. Throughput Distribution",
            authorName = "David Chen",
            authorInitials = "DC",
            content = "EU Central throughput is at record 184k req/s with 82% CPU load. Capacity headroom looks solid.",
            dataPointContext = "eu-central-1 (82.5ms, 184.2k req/s)",
            isResolved = true,
            timestamp = "45m ago"
        )
    )
}
