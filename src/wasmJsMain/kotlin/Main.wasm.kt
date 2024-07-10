/*
 * Copyright (c) 2022-2024 Leon Linhart
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
import androidx.compose.material3.*
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.*
import androidx.compose.ui.window.CanvasBasedWindow
import coil3.ImageLoader
import coil3.compose.LocalPlatformContext
import coil3.memory.MemoryCache
import com.gw2tb.gw2api.client.ktor.Gw2ApiClient
import com.gw2tb.gw2chatlinks.decodeChatLink
import com.gw2tb.link.MainScreen
import com.gw2tb.link.composables.LocalImageLoader
import com.gw2tb.link.impl.MainComponentImpl
import kotlinx.browser.window

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val initialChatLink = decodeChatLink(window.location.pathname.removePrefix("/")).getOrNull()

    val gw2ApiClient = Gw2ApiClient()

    val component = MainComponentImpl(
        apiClient = gw2ApiClient,
        initialChatLink = initialChatLink
    )

    CanvasBasedWindow(
        title = "GW2TB Link",
        canvasElementId = "ComposeTarget"
    ) {
        val platformContext = LocalPlatformContext.current

        val imageLoader = ImageLoader.Builder(platformContext)
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(platformContext, 0.5)
                    .build()
            }
            .build()

        CompositionLocalProvider(LocalImageLoader provides imageLoader) {
            MaterialTheme {
                MainScreen(component)
            }
        }
    }
}