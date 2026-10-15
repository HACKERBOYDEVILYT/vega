package com.example.data.web

import com.example.data.model.VegaDataset
import com.example.data.model.VegaSpec
import com.example.data.model.Workspace
import org.json.JSONArray
import org.json.JSONObject

object VegaWebGenerator {

    fun generateStandaloneHtml(
        workspace: Workspace,
        dataset: VegaDataset,
        spec: VegaSpec
    ): String {
        // Convert dataset records to JSON array
        val dataArray = JSONArray()
        dataset.records.forEach { record ->
            val obj = JSONObject()
            record.values.forEach { (key, value) ->
                obj.put(key, value)
            }
            dataArray.put(obj)
        }

        val rawSpecJson = spec.toVegaLiteJson(dataset.title)

        return """
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>${workspace.name} | Vega Analytics Web Application</title>
  <!-- Vega, Vega-Lite and Vega-Embed Libraries -->
  <script src="https://cdn.jsdelivr.net/npm/vega@5"></script>
  <script src="https://cdn.jsdelivr.net/npm/vega-lite@5"></script>
  <script src="https://cdn.jsdelivr.net/npm/vega-embed@6"></script>
  <style>
    :root {
      --bg: #0b0f19;
      --surface: #111827;
      --border: #1e293b;
      --cyan: #06b6d4;
      --violet: #8b5cf6;
      --emerald: #10b981;
      --text: #f8fafc;
      --text-muted: #94a3b8;
    }
    * { box-sizing: border-box; margin: 0; padding: 0; }
    body {
      background-color: var(--bg);
      color: var(--text);
      font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
      padding: 16px;
      line-height: 1.5;
    }
    .header {
      background: var(--surface);
      border: 1px solid var(--border);
      border-radius: 12px;
      padding: 16px 20px;
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;
      box-shadow: 0 4px 20px rgba(0,0,0,0.4);
    }
    .header h1 {
      font-size: 18px;
      color: var(--text);
      display: flex;
      align-items: center;
      gap: 8px;
    }
    .badge {
      background: rgba(6, 182, 212, 0.15);
      border: 1px solid var(--cyan);
      color: var(--cyan);
      font-size: 12px;
      font-family: monospace;
      padding: 3px 8px;
      border-radius: 6px;
    }
    .live-dot {
      width: 8px;
      height: 8px;
      background: var(--emerald);
      border-radius: 50%;
      display: inline-block;
      box-shadow: 0 0 8px var(--emerald);
    }
    .chart-card {
      background: var(--surface);
      border: 1px solid var(--border);
      border-radius: 14px;
      padding: 20px;
      margin-bottom: 16px;
      position: relative;
    }
    .chart-card h2 {
      font-size: 16px;
      margin-bottom: 4px;
      color: var(--cyan);
    }
    .chart-card p {
      font-size: 13px;
      color: var(--text-muted);
      margin-bottom: 16px;
    }
    #vis {
      width: 100%;
      min-height: 280px;
      display: flex;
      justify-content: center;
      align-items: center;
    }
    .meta-bar {
      display: flex;
      gap: 12px;
      flex-wrap: wrap;
      margin-top: 14px;
      font-size: 12px;
      color: var(--text-muted);
    }
    .meta-pill {
      background: rgba(255,255,255,0.05);
      border: 1px solid var(--border);
      padding: 4px 10px;
      border-radius: 6px;
    }
    .footer {
      text-align: center;
      font-size: 12px;
      color: var(--text-muted);
      margin-top: 24px;
    }
  </style>
</head>
<body>
  <div class="header">
    <div>
      <h1>
        <span class="live-dot"></span>
        ${workspace.name}
      </h1>
      <div style="font-size: 12px; color: var(--text-muted); margin-top: 4px;">
        Collaborative Web Exploration Workspace • Real-Time Data Sync Active
      </div>
    </div>
    <div>
      <span class="badge">${workspace.currentVersionTag}</span>
    </div>
  </div>

  <div class="chart-card">
    <h2>${spec.title}</h2>
    <p>${spec.description}</p>
    <div id="vis"></div>
    <div class="meta-bar">
      <span class="meta-pill">Mark: <strong>${spec.markType.displayName}</strong></span>
      <span class="meta-pill">X: <strong>${spec.encoding.xField}</strong></span>
      <span class="meta-pill">Y: <strong>${spec.encoding.yField}</strong></span>
      <span class="meta-pill">Dataset: <strong>${dataset.title} (${dataset.records.size} rows)</strong></span>
    </div>
  </div>

  <div class="footer">
    Powered by Vega Analytics Web Runtime • Versioned under SemVer ${workspace.currentVersionTag}
  </div>

  <script>
    const datasetValues = $dataArray;

    const baseSpec = $rawSpecJson;
    // Inject dataset inline
    baseSpec.data = { values: datasetValues };
    baseSpec.background = 'transparent';
    baseSpec.config = {
      axis: {
        domainColor: '#334155',
        gridColor: '#1e293b',
        labelColor: '#94a3b8',
        titleColor: '#06b6d4'
      },
      view: { stroke: 'transparent' }
    };
    baseSpec.width = "container";
    baseSpec.height = 240;

    vegaEmbed('#vis', baseSpec, { actions: { export: true, source: false, editor: false } })
      .then(result => {
        console.log("Vega chart successfully embedded in Web Application.");
      })
      .catch(err => {
        document.getElementById('vis').innerHTML = '<div style="color:#f43f5e;padding:20px;">Rendering Error: ' + err.message + '</div>';
      });
  </script>
</body>
</html>
""".trimIndent()
    }
}
