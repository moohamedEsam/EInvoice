package com.example.einvoicecomponents.charts

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun Pie(
    label: String,
    valueAsPercentage: Float,
    modifier: Modifier = Modifier,
) {
    val animatedValue by animateFloatAsState(
        targetValue = valueAsPercentage,
        animationSpec = tween(durationMillis = 1000)
    )
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            textAlign = TextAlign.Center
        )
        PieChart(value = animatedValue, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun PieChart(
    value: Float,
    modifier: Modifier = Modifier,
    color: Color,
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        drawArc(
            color = Color.Black,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = 5f)
        )

        drawArc(
            color = color,
            startAngle = 180f,
            sweepAngle = 360f * value,
            useCenter = false,
            style = Stroke(width = 15f, cap = StrokeCap.Round)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PiePreview() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Pie(
            label = "created documents",
            valueAsPercentage = 0.75f,
            modifier = Modifier.size(100.dp)
        )
    }
}