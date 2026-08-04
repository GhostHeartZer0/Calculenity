package com.ghz.calculenity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculenityTheme {
                Surface(color = Color(0xFF121212)) {
                    CalculenityApp()
                }
            }
        }
    }
}

@Composable
fun CalculenityApp() {
    var selectedTab by remember { mutableStateOf("Core Calc") }
    var outputTextSize by remember { mutableStateOf(24f) } // Configurable in settings

    Row(modifier = Modifier.fillMaxSize()) {
        // Left Side: ROY.G.BIV Frosted Glass Sidebar
        Sidebar(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
            modifier = Modifier
                .width(80.dp)
                .fillMaxHeight()
        )

        // Right Side: Main Content Area
        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            when (selectedTab) {
                "Core Calc" -> CoreCalculator(outputTextSize)
                "Terminal" -> TerminalView()
                "Weights" -> WeightsView()
                "Prime Tools" -> PrimeToolsView()
                "Graphing" -> GraphingView()
                "Draw Notes" -> DrawNotesView()
                "Adv Settings" -> AdvSettingsView(outputTextSize) { outputTextSize = it }
            }
        }
    }
}

@Composable
fun Sidebar(selectedTab: String, onTabSelected: (String) -> Unit, modifier: Modifier = Modifier) {
    val tabs = listOf(
        TabItem("Adv Settings", Color(0xFFFF4B4B), Icons.Default.Settings),
        TabItem("Prime Tools", Color(0xFFFFA500), Icons.Default.Extension),
        TabItem("Graphing", Color(0xFFFFE033), Icons.AutoMirrored.Filled.ShowChart),
        TabItem("Core Calc", Color(0xFF4CAF50), Icons.Default.Calculate),
        TabItem("Weights", Color(0xFF2196F3), Icons.Default.Straighten),
        TabItem("Draw Notes", Color(0xFF3F51B5), Icons.Default.Brush),
        TabItem("Terminal", Color(0xFF9C27B0), Icons.Default.Terminal)
    )

    Box(modifier = modifier) {
        // Blurred background layer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(radius = 20.dp)
                .background(Color.White.copy(alpha = 0.05f))
        )
        
        // Sharp content layer
        Column(modifier = Modifier.fillMaxSize()) {
            tabs.forEach { tab ->
                val isSelected = selectedTab == tab.name
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(
                            if (isSelected) tab.color.copy(alpha = 0.3f)
                            else Color.Transparent
                        )
                        .clickable { onTabSelected(tab.name) }
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.name,
                        tint = if (isSelected) Color.White else tab.color,
                        modifier = Modifier
                            .size(32.dp)
                            .align(Alignment.Center)
                    )
                    
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .fillMaxHeight(0.6f)
                                .background(tab.color)
                                .align(Alignment.CenterStart)
                        )
                    }
                }
            }
        }
    }
}

data class TabItem(val name: String, val color: Color, val icon: ImageVector)

@Composable
fun CoreCalculator(fontSize: Float) {
    var base by remember { mutableStateOf("2") }
    var exponent by remember { mutableStateOf("100") }
    var result by remember { mutableStateOf("") }
    var activeField by remember { mutableStateOf(0) } // 0 for base, 1 for exponent
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "High-Precision Engine",
            style = MaterialTheme.typography.titleSmall,
            color = Color(0xFF4CAF50)
        )
        
        // Display Area
        Card(
            modifier = Modifier.fillMaxWidth().height(150.dp).padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.3f)),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
        ) {
            Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                Text(
                    text = if (result.isEmpty()) "0" else result,
                    fontSize = fontSize.sp,
                    color = Color.White,
                    modifier = Modifier.verticalScroll(rememberScrollState()).align(Alignment.BottomEnd)
                )
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = base,
                onValueChange = { base = it },
                label = { Text("Base") },
                modifier = Modifier.weight(1f).onFocusChanged { if(it.isFocused) activeField = 0 },
                textStyle = LocalTextStyle.current.copy(color = Color.White, fontSize = 18.sp)
            )
            OutlinedTextField(
                value = exponent,
                onValueChange = { exponent = it },
                label = { Text("Exp") },
                modifier = Modifier.weight(0.6f).onFocusChanged { if(it.isFocused) activeField = 1 },
                textStyle = LocalTextStyle.current.copy(color = Color.White, fontSize = 18.sp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Keypad
        val keys = listOf(
            listOf("7", "8", "9", "DEL"),
            listOf("4", "5", "6", "AC"),
            listOf("1", "2", "3", "^"),
            listOf(".", "0", "π", "=")
        )

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            keys.forEach { row ->
                Row(modifier = Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    row.forEach { key ->
                        Button(
                            onClick = {
                                when (key) {
                                    "DEL" -> if (activeField == 0) { if(base.isNotEmpty()) base = base.dropLast(1) } else { if(exponent.isNotEmpty()) exponent = exponent.dropLast(1) }
                                    "AC" -> { base = ""; exponent = ""; result = "" }
                                    "=" -> scope.launch { result = MathEngine.power(base, exponent) }
                                    "π" -> if (activeField == 0) base = "3.14159" else exponent = "3.14159"
                                    "^" -> activeField = 1
                                    else -> if (activeField == 0) base += key else exponent += key
                                }
                            },
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = when(key) {
                                    "DEL", "AC" -> Color(0xFFFF4B4B).copy(alpha = 0.8f)
                                    "=" -> Color(0xFF4CAF50)
                                    "^" -> Color(0xFFFFA500)
                                    else -> Color.White.copy(alpha = 0.1f)
                                }
                            )
                        ) {
                            Text(key, fontSize = 20.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WeightsView() {
    var inputValue by remember { mutableStateOf("1") }
    val units = listOf("Kilograms to Pounds", "Miles to Kilometers", "Celsius to Fahrenheit")
    var selectedUnit by remember { mutableStateOf(units[0]) }

    val result = remember(inputValue, selectedUnit) {
        val value = inputValue.toDoubleOrNull() ?: 0.0
        when (selectedUnit) {
            "Kilograms to Pounds" -> "%.2f lbs".format(value * 2.20462)
            "Miles to Kilometers" -> "%.2f km".format(value * 1.60934)
            "Celsius to Fahrenheit" -> "%.2f °F".format((value * 9/5) + 32)
            else -> "0.00"
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Quick Conversions", style = MaterialTheme.typography.headlineMedium, color = Color.White)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = inputValue,
            onValueChange = { inputValue = it },
            label = { Text("Value") },
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(color = Color.White)
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        units.forEach { unit ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedUnit = unit }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = (selectedUnit == unit), onClick = { selectedUnit = unit })
                Text(unit, color = Color.White, modifier = Modifier.padding(start = 8.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Result:", style = MaterialTheme.typography.labelLarge, color = Color.Gray)
        Text(result, style = MaterialTheme.typography.displayMedium, color = Color(0xFF2196F3))
    }
}

@Composable
fun PrimeToolsView() {
    var numberText by remember { mutableStateOf("17") }
    var resultText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Prime Tools", style = MaterialTheme.typography.headlineMedium, color = Color.White)
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = numberText,
            onValueChange = { numberText = it },
            label = { Text("Enter a number") },
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(color = Color.White)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    val n = numberText.toLongOrNull()
                    if (n == null) resultText = "Invalid number"
                    else {
                        val isPrime = (2..Math.sqrt(n.toDouble()).toInt()).none { n % it == 0L } && n > 1
                        resultText = if (isPrime) "$n is prime!" else "$n is not prime."
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500))
            ) {
                Text("Is Prime?", color = Color.White)
            }
            Button(
                onClick = {
                    var n = numberText.toLongOrNull()
                    if (n == null) resultText = "Invalid number"
                    else {
                        val factors = mutableListOf<Long>()
                        var d = 2L
                        while (d * d <= n!!) {
                            while (n % d == 0L) {
                                factors.add(d)
                                n /= d
                            }
                            d++
                        }
                        if (n > 1) factors.add(n)
                        resultText = "Factors: ${factors.joinToString(" × ")}"
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFE033))
            ) {
                Text("Factorize", color = Color.Black)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(resultText, color = Color.White, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun GraphingView() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Function Grapher", style = MaterialTheme.typography.headlineMedium, color = Color.White)
        Text("y = sin(x)", color = Color.Gray)
        Spacer(modifier = Modifier.height(24.dp))
        Canvas(modifier = Modifier.fillMaxSize().background(Color.DarkGray)) {
            val width = size.width
            val height = size.height
            val centerY = height / 2
            val scaleX = width / (4 * Math.PI.toFloat())
            val scaleY = height / 4

            // Axis
            drawLine(Color.Gray, Offset(0f, centerY), Offset(width, centerY))
            drawLine(Color.Gray, Offset(width/2, 0f), Offset(width/2, height))

            val path = Path()
            for (x in 0..width.toInt()) {
                val xInMath = (x - width/2) / scaleX
                val yInMath = sin(xInMath)
                val y = centerY - (yInMath * scaleY)
                if (x == 0) path.moveTo(x.toFloat(), y) else path.lineTo(x.toFloat(), y)
            }
            drawPath(path, Color(0xFFFFE033), style = Stroke(width = 4f))
        }
    }
}

@Composable
fun DrawNotesView() {
    val paths = remember { mutableStateListOf<Path>() }
    var currentPath by remember { mutableStateOf<Path?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Draw Notes", style = MaterialTheme.typography.headlineMedium, color = Color.White)
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.05f))
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            currentPath = Path().apply { moveTo(offset.x, offset.y) }
                            paths.add(currentPath!!)
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            currentPath?.lineTo(change.position.x, change.position.y)
                            // Force redraw
                            val temp = currentPath
                            currentPath = null
                            currentPath = temp
                        }
                    )
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                paths.forEach { path ->
                    drawPath(path, Color(0xFF3F51B5), style = Stroke(width = 5f))
                }
            }
            Button(
                onClick = { paths.clear() },
                modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
            ) {
                Text("Clear")
            }
        }
    }
}

@Composable
fun AdvSettingsView(currentSize: Float, onSizeChange: (Float) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Advanced Settings", style = MaterialTheme.typography.headlineMedium, color = Color.White)
        Spacer(modifier = Modifier.height(32.dp))
        Text("Output Font Size: ${currentSize.toInt()} sp", color = Color.White)
        Slider(
            value = currentSize,
            onValueChange = onSizeChange,
            valueRange = 12f..72f,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun TerminalView() {
    val terminalManager = remember { TerminalManager() }
    val output = terminalManager.outputFlow.collectAsState(initial = "Terminal started...")
    var command by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        terminalManager.startSession()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = output.value,
            color = Color.Green,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.Black)
                .padding(8.dp)
                .verticalScroll(rememberScrollState())
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = command,
                onValueChange = { command = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Enter command") }
            )
            Button(onClick = {
                scope.launch {
                    terminalManager.sendCommand(command)
                    command = ""
                }
            }) {
                Text("Run")
            }
        }
    }
}

