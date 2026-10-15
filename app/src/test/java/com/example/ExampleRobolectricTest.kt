package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.AggregationType
import com.example.data.model.MarkType
import com.example.data.model.VegaEncoding
import com.example.data.model.VegaSpec
import com.example.ui.components.versioning.SemVerBump
import com.example.ui.components.versioning.calculateNextTag
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Vega Analytics", appName)
  }

  @Test
  fun `semantic version bumping calculates correct tags`() {
    assertEquals("v1.2.1", calculateNextTag("v1.2.0", SemVerBump.PATCH))
    assertEquals("v1.3.0", calculateNextTag("v1.2.0", SemVerBump.MINOR))
    assertEquals("v2.0.0", calculateNextTag("v1.2.0", SemVerBump.MAJOR))
  }

  @Test
  fun `vega lite json generator includes valid schema and encodings`() {
    val spec = VegaSpec(
        title = "Test Spec",
        description = "Test description",
        markType = MarkType.SCATTER,
        encoding = VegaEncoding(xField = "latency_ms", yField = "throughput_k", colorField = "region")
    )
    val json = spec.toVegaLiteJson("Test Dataset")
    assertTrue(json.contains("https://vega.github.io/schema/vega-lite/v5.json"))
    assertTrue(json.contains("\"type\": \"circle\""))
    assertTrue(json.contains("\"field\": \"latency_ms\""))
  }
}
