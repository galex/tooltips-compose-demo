package il.co.galex.compose.tooltips

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import il.co.galex.compose.tooltips.tooltip
import il.co.galex.compose.tooltips.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val tooltipHostState = remember { TooltipHostState() }

                    Box() {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                color = MaterialTheme.colors.primary,
                                text = "Hello, You!",
                                modifier = Modifier
                                    .tooltip(
                                        tooltipHostState = tooltipHostState,
                                        text = "First tooltip on Text 1",
                                    )
                                    .tooltip(
                                        tooltipHostState = tooltipHostState,
                                        text = "Second tooltip on Text 1",
                                    )
                            )

                            Spacer(modifier = Modifier.height(80.dp))

                            Text(
                                color = MaterialTheme.colors.primary,
                                text = "Nice to meet ya!",
                                modifier = Modifier
                                    .tooltip(
                                        tooltipHostState = tooltipHostState,
                                        text = "A third tooltip on Text 2",
                                    )
                            )
                        }
                    }

                    TooltipHost(
                        hostState = tooltipHostState,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}