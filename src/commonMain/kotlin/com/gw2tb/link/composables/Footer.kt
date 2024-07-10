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
package com.gw2tb.link.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun Footer(
    buttons: List<FooterSocialButton>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(color = Color.White.copy(alpha = 0.85F))
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "GW2ToolBelt",
            modifier = Modifier
                .align(Alignment.CenterStart),
            color = Color.Black.copy(alpha = 0.5394F)
        )

        Text(
            text = "Copyright 2024".uppercase(),
            modifier = Modifier
                .align(Alignment.Center),
            color = Color.Black.copy(alpha = 0.5394F),
            fontFamily = Typefaces.JetBrainsMono
        )

        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
        ) {
            buttons.forEach { button ->
                CompactIconButton(
                    painter = painterResource(button.drawableResource),
                    contentDescription = button.contentDescription,
                    onClick = button.onClick,
                    modifier = Modifier
                        .padding(12.dp)
                        .size(24.dp)
                )
            }
        }
    }
}

@Immutable
class FooterSocialButton(
    val drawableResource: DrawableResource,
    val contentDescription: String,
    val onClick: () -> Unit
)