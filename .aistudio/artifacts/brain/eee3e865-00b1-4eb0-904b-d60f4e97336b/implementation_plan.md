# Vega Analytics: Visual Exploration & Collaborative Executive Workspace

An executive visual exploration environment inspired by Vega for interactive data analysis, real-time collaborative workspaces, semantic versioning of chart specifications, and team insights.

---

### User Review & Critical Decisions

> [!IMPORTANT]
> The following user preferences were confirmed during clarification and anchor the architecture:

- **Confirmed Workspace Structure**: Executive dashboard viewer with live interactive chart visualizer and collaborative data comments.
- **Confirmed Collaboration & Sync Model**: Real-time multi-user workspaces with live snapshot listeners and cloud state persistence via Firestore.
- **Confirmed Starter Datasets**: Diverse analytics pack featuring multi-dimensional scatter plots, temporal time-series trends, and categorical heatmaps/distribution matrices.
- **Semantic Versioning Strategy**: Visual specs and workspace states will maintain explicit semantic version tags (`v1.0.0`, `v1.1.0`) with immutable version changelog snapshots and rollback capability.

---

### 1. Overview & Core Concept

- **What It Does**: Provides an executive-grade mobile and tablet visual data analysis environment inspired by Vega / Vega-Lite. Users can explore multi-dimensional datasets through dynamic charts (scatter plots, line/area time-series, categorical bar charts, correlation heatmaps), filter dimensions in real time, view and fork declarative visualization specs, author collaborative analytical comments on specific data points, and track changes using semantic versioning.
- **Target Audience / Persona**: Data analysts, product leads, and executives reviewing operational and analytical metrics who need quick visual exploration, team collaboration, and version-tracked chart states on mobile devices.
- **Key Value**: Bridges the gap between complex declarative visualization grammar (Vega) and immediate mobile executive decision-making with seamless real-time team collaboration.

---

### 2. User Experience & Visual Design

#### Key User Flows
1. **Workspace Overview & Switcher**: View active team workspaces, current semantic versions (`v1.2.0`), member presence, and recent activity feeds.
2. **Executive Dashboard & Chart Exploration**:
   - High-fidelity interactive chart canvas (Canvas rendering with pinch-zoom, pan, data scrubbing, and tooltips).
   - Dimension filters (date ranges, categories, metric selectors, aggregation modes: Sum, Mean, Count, Max).
   - Vega Specification Drawer: View the underlying declarative JSON spec, toggle encoding channels (X, Y, Color, Size, Mark type), and preview changes.
3. **Collaborative Data Comment Threads**:
   - Pin comments directly to chart metrics or time slices.
   - Real-time discussion thread with unread badges, author tags, and resolution status.
4. **Semantic Versioning & History**:
   - Release new versions with SemVer labels (`Major.Minor.Patch`) and release notes.
   - Audit trail of previous chart revisions with 1-tap diff inspection and rollback.

#### Visual Identity & Theme
- **Aesthetic Direction**: *Executive Dark Glassmorphism & Precision FinTech*. Deep navy-slate surface (`#0B0F19`), crisp subtle borders (`#1E293B`), accented with vibrant electric cyan (`#06B6D4`), violet (`#8B5CF6`), and amber alert highlights.
- **Typography**: Clean, high-density monospace numerals for data values paired with modern geometric sans-serif for UI titles and labels.
- **Touch Targets & Polish**: Minimum 48dp tappable dimensions, smooth animated state transitions, and responsive chart scrubbers with haptic feedback.

---

### 3. Key Product Decisions & Trade-Offs

- **Rendering Engine: Jetpack Compose Canvas vs. WebView Vega Embed**:
  - *Chosen Approach*: Native Jetpack Compose Canvas rendering with a declarative Vega-Lite specification parser.
  - *Why*: Ultra-fast 60+ FPS touch scrubbing, zero WebView memory leaks, offline resilience, and seamless integration with Compose gestures and Material 3 design tokens.
  - *Alternatives Considered*: Embedded WebView with Vega JavaScript runtime (heavier bundle size, sluggish touch interactions on low-spec devices, awkward dark theme synchronization).
- **Data Persistence & Sync: Firestore Real-Time Subcollections**:
  - *Chosen Approach*: Workspaces, charts, comments, and semantic versions stored in structured Firestore collections with real-time `snapshots()` flows and zero-trust security rules.
  - *Why*: Enables instant cross-user synchronization, real-time collaborator presence, and robust offline caching.
- **Semantic Versioning Architecture**:
  - *Chosen Approach*: Immutable version documents capturing snapshot state of the visual spec, dataset references, and author metadata upon each published release.

---

### 4. Technical Architecture & Data Strategy

```
┌─────────────────────────────────────────────────────────────┐
│                       UI Layer (Compose)                    │
│ ┌──────────────────────┐ ┌────────────────────────────────┐ │
│ │  Workspace Header    │ │  Chart Filter Bar              │ │
│ │  & SemVer Badge      │ │  (Metrics, Dimensions, Ranges) │ │
│ └──────────┬───────────┘ └───────────────┬────────────────┘ │
│            │                             │                  │
│ ┌──────────▼─────────────────────────────▼────────────────┐ │
│ │         Executive Chart Canvas (Custom DrawScope)       │ │
│ │   [Scatter Plot | Time-Series | Heatmap | Bar Matrix]   │ │
│ └──────────────────────────┬──────────────────────────────┘ │
│                            │                                │
│ ┌──────────────────────────▼──────────────────────────────┐ │
│ │     Collaborative Comments Sheet & SemVer Audit Drawer  │ │
│ └──────────────────────────┬──────────────────────────────┘ │
└────────────────────────────┼────────────────────────────────┘
                             │
┌────────────────────────────▼────────────────────────────────┐
│               VegaViewModel (MVI / StateFlow)               │
│ - Current Workspace & Version State                         │
│ - Active Chart Spec & Encoded Dimensions                    │
│ - Collaborative Comment Threads                             │
│ - Dataset Filter Pipeline & Aggregations                    │
└────────────────────────────┬────────────────────────────────┘
                             │
┌────────────────────────────▼────────────────────────────────┐
│                   Data & Repository Layer                   │
│ ┌─────────────────────────┐  ┌────────────────────────────┐ │
│ │ WorkspaceRepository     │  │ CommentRepository          │ │
│ └───────────┬─────────────┘  └────────────┬───────────────┘ │
│             │                             │                 │
│ ┌───────────▼─────────────────────────────▼───────────────┐ │
│ │           Firebase Firestore Real-Time Database         │ │
│ │  /workspaces/{workspaceId}                              │ │
│ │    ├── /versions/{versionId}   (SemVer snapshots)       │ │
│ │    ├── /comments/{commentId}   (Live collaboration)     │ │
│ │    └── /charts/{chartId}       (Declarative specs)      │ │
└─────────────────────────────────────────────────────────────┘
```

#### Core Data Entities
1. **Workspace**: `id`, `name`, `description`, `currentVersion` (e.g., "1.2.0"), `members`, `updatedAt`.
2. **ChartSpec**: `id`, `title`, `markType` (`SCATTER`, `LINE`, `BAR`, `HEATMAP`), `xField`, `yField`, `colorField`, `sizeField`, `filterParams`.
3. **DataPoint**: Generic tabular data representation supporting numbers, strings, and timestamps.
4. **WorkspaceVersion**: `id`, `versionTag`, `releaseNotes`, `chartSnapshot`, `authorEmail`, `createdAt`.
5. **DataComment**: `id`, `chartId`, `authorName`, `authorEmail`, `content`, `dataPointContext`, `resolved`, `createdAt`.
