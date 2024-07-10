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
package com.gw2tb.link

import androidx.compose.runtime.Immutable
import com.gw2tb.gw2chatlinks.ChatLink
import com.gw2tb.gw2chatlinks.encodeChatLink

fun ChatLinkState(chatLink: ChatLink): ChatLinkState {
    val encodedString = encodeChatLink(chatLink).getOrThrow()
    val type = linkTypeOf(chatLink::class)

    return ChatLinkState(
        encodedString = encodedString,
        type = type,
        data = type.extractUnsafe(chatLink)
    )
}

@Immutable
data class ChatLinkState(
    val encodedString: String?,
    val type: ChatLinkType<*>?,
    private val data: Map<ChatLinkPropertyToken<*>, Any?> = emptyMap()
) {

    companion object {
        val EMPTY = ChatLinkState(null, null)
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <T> get(property: ChatLinkPropertyToken<T>): T? = data[property] as T?

}