package com.example.ui.core.components

import android.annotation.SuppressLint
import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.R
import com.example.ui.core.toPersianNumberSafe
import kotlinx.coroutines.delay

enum class LatexSkeletonType {
    QUESTION,
    OPTION,
    EXPLANATION,
    FLASHCARD
}

@Composable
fun SkeletonPlaceholder(
    textColor: Color,
    skeletonType: LatexSkeletonType,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 850, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    when (skeletonType) {
        LatexSkeletonType.QUESTION -> {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.End
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .height(14.dp)
                        .background(
                            color = textColor.copy(alpha = alpha),
                            shape = RoundedCornerShape(4.dp)
                        )
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.70f)
                        .height(14.dp)
                        .background(
                            color = textColor.copy(alpha = alpha),
                            shape = RoundedCornerShape(4.dp)
                        )
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.45f)
                        .height(14.dp)
                        .background(
                            color = textColor.copy(alpha = alpha),
                            shape = RoundedCornerShape(4.dp)
                        )
                )
            }
        }
        LatexSkeletonType.OPTION -> {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.80f)
                        .height(14.dp)
                        .background(
                            color = textColor.copy(alpha = alpha),
                            shape = RoundedCornerShape(4.dp)
                        )
                )
            }
        }
        LatexSkeletonType.EXPLANATION -> {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.End
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.90f)
                        .height(12.dp)
                        .background(
                            color = textColor.copy(alpha = alpha),
                            shape = RoundedCornerShape(4.dp)
                        )
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(12.dp)
                        .background(
                            color = textColor.copy(alpha = alpha),
                            shape = RoundedCornerShape(4.dp)
                        )
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.60f)
                        .height(12.dp)
                        .background(
                            color = textColor.copy(alpha = alpha),
                            shape = RoundedCornerShape(4.dp)
                        )
                )
            }
        }
        LatexSkeletonType.FLASHCARD -> {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(16.dp)
                        .background(
                            color = textColor.copy(alpha = alpha),
                            shape = RoundedCornerShape(4.dp)
                        )
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(16.dp)
                        .background(
                            color = textColor.copy(alpha = alpha),
                            shape = RoundedCornerShape(4.dp)
                        )
                )
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun LatexText(
    latexString: String,
    textColor: Color,
    modifier: Modifier = Modifier,
    skeletonType: LatexSkeletonType = LatexSkeletonType.QUESTION,
    isBold: Boolean = false
) {
    val processedString = remember(latexString) {
        latexString.toPersianNumberSafe()
    }

    val hasMath = remember(processedString) {
        processedString.contains("\\(") || processedString.contains("\\[") || 
        processedString.contains("$") || processedString.contains("\\begin{")
    }

    if (!hasMath) {
        val withNewLines = processedString
                .replace(Regex("<br\\s*/?>", RegexOption.IGNORE_CASE), "\n")
                .replace("&nbsp;", " ")
        val plainText = withNewLines.replace(Regex("<[^>]*>"), "")
        
        Text(
            text = plainText,
            color = textColor,
            modifier = modifier,
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = com.example.ui.theme.VazirmatnFontFamily,
                fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
                textDirection = TextDirection.Rtl,
                textAlign = TextAlign.Right,
                lineHeight = 22.sp
            )
        )
        return
    }

    var isLoaded by remember(processedString) { mutableStateOf(false) }

    LaunchedEffect(processedString) {
        delay(1800)
        isLoaded = true
    }

    val colorHex = String.format("#%06X", 0xFFFFFF and textColor.toArgb())

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopEnd
    ) {
        if (!isLoaded) {
            SkeletonPlaceholder(
                textColor = textColor,
                skeletonType = skeletonType,
                modifier = Modifier.fillMaxWidth()
            )
        }

        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 24.dp)
                .alpha(if (isLoaded) 1f else 0.01f),
            factory = { ctx ->
                WebView(ctx).apply {
                    layoutParams = android.view.ViewGroup.LayoutParams(
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                        android.view.ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    setBackgroundColor(android.graphics.Color.TRANSPARENT)
                    setLayerType(android.view.View.LAYER_TYPE_SOFTWARE, null)
                    
                    isVerticalScrollBarEnabled = false
                    isHorizontalScrollBarEnabled = false

                    addJavascriptInterface(object {
                        @JavascriptInterface
                        fun onRenderComplete() {
                            post {
                                isLoaded = true
                            }
                        }
                    }, "AndroidInterface")
                }
            },
            update = { webView ->
                val htmlData = """
                    <!DOCTYPE html>
                    <html dir="rtl">
                    <head>
                    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
                    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/katex@0.16.9/dist/katex.min.css">
                    <script src="https://cdn.jsdelivr.net/npm/katex@0.16.9/dist/katex.min.js"></script>
                    <script src="https://cdn.jsdelivr.net/npm/katex@0.16.9/dist/contrib/auto-render.min.js"></script>
                    <style>
                        @font-face {
                            font-family: 'Peyda';
                            src: url('font/peyda_regular.ttf') format('truetype');
                            font-weight: normal;
                        }
                        @font-face {
                            font-family: 'Peyda';
                            src: url('font/peyda_bold.ttf') format('truetype');
                            font-weight: bold;
                        }
                        body {
                            color: $colorHex;
                            font-family: 'Peyda', sans-serif;
                            font-weight: ${if (isBold) "bold" else "normal"};
                            font-size: 16px;
                            padding: 0;
                            margin: 0;
                            background-color: transparent;
                            line-height: 1.5;
                            direction: rtl;
                            text-align: right;
                            word-wrap: break-word;
                            overflow-wrap: break-word;
                        }
                        p {
                            margin: 0;
                            padding: 0;
                        }
                        .katex {
                            direction: ltr !important;
                            unicode-bidi: isolate;
                            font-size: 0.9em;
                        }
                        .katex-display {
                            direction: ltr !important;
                            unicode-bidi: isolate;
                            text-align: left !important;
                        }
                    </style>
                    </head>
                    <body>
                      ${processedString.replace("\n", "<br>")}
                      <script>
                            renderMathInElement(document.body, {
                                delimiters: [
                                    {left: '${'$'}${'$'}', right: '${'$'}${'$'}', display: true},
                                    {left: '\\[', right: '\\]', display: true},
                                    {left: '${'$'}', right: '${'$'}', display: false},
                                    {left: '\\(', right: '\\)', display: false}
                                ],
                                throwOnError : false
                            });
                            setTimeout(() => {
                               if (window.AndroidInterface) {
                                   window.AndroidInterface.onRenderComplete();
                               }
                            }, 150);
                      </script>
                    </body>
                    </html>
                """.trimIndent()
                
                val currentHtml = webView.tag as? String
                if (currentHtml != htmlData) {
                    webView.tag = htmlData
                    webView.settings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    webView.settings.allowFileAccess = true
                    webView.loadDataWithBaseURL("file:///android_res/", htmlData, "text/html", "UTF-8", null)
                }
            }
        )
    }
}
