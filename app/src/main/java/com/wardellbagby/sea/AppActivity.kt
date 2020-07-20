package com.wardellbagby.sea

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.ui.core.setContent
import androidx.ui.material.MaterialTheme
import com.squareup.workflow.diagnostic.SimpleLoggingDiagnosticListener
import com.squareup.workflow.ui.ViewEnvironment
import com.squareup.workflow.ui.ViewRegistry
import com.squareup.workflow.ui.compose.WorkflowContainer
import com.wardellbagby.sea.DefaultApplication.Companion.APP_COMPONENT
import java.util.Optional
import javax.inject.Scope
import kotlin.reflect.KClass

class AppActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      App()
    }
  }
}

private val viewRegistry = ViewRegistry(MainViewRegistry)
private val viewEnvironment = ViewEnvironment(viewRegistry)

@Composable fun App() {
  MaterialTheme {
    WorkflowContainer(
        workflow = APP_COMPONENT.appWorkflow(),
        viewEnvironment = viewEnvironment,
        diagnosticListener = SimpleLoggingDiagnosticListener()
    )
  }
}

@Scope
annotation class SingleIn(val value: KClass<*>)

fun <T> Optional<T>.getOrNull() = if (isPresent) get() else null