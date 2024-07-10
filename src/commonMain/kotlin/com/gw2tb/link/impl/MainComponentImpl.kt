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
package com.gw2tb.link.impl

import com.gw2tb.gw2api.client.Gw2ApiClient
import com.gw2tb.gw2chatlinks.ChatLink
import com.gw2tb.gw2chatlinks.decodeChatLink
import com.gw2tb.gw2chatlinks.encodeChatLink
import com.gw2tb.link.ChatLinkState
import com.gw2tb.link.ChatLinkType
import com.gw2tb.link.MainComponent
import com.gw2tb.link.linkTypeOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

internal class MainComponentImpl(
    override val apiClient: Gw2ApiClient,
    initialChatLink: ChatLink?
) : MainComponent {

    private val coroutineScope = CoroutineScope(SupervisorJob())

    private val _chatLink: MutableStateFlow<ChatLinkState> =
        MutableStateFlow(
            value = initialChatLink?.let(::ChatLinkState) ?: ChatLinkState.EMPTY
        )

    override val chatLink: StateFlow<ChatLinkState> = _chatLink.asStateFlow()

    init {
        coroutineScope.launch {
            chatLink
                .onEach { chatLink ->
                    val fragment = chatLink.type?.let { chatLink.encodedString } ?: "/"
                    platformStoreNavigationFragment(fragment)
                }
                .collect()
        }
    }

    override fun copyToClipboard(value: String) {
        platformCopyToClipboard(value)
    }

    override fun navigateToUrl(url: String) {
        platformNavigateToUrl(url)
    }

    override fun updateChatLink(value: ChatLink) {
        val state = ChatLinkState(value)
        _chatLink.tryEmit(state)
    }

    override fun updateChatLink(encodedChatLink: String) {
        val chatLink = decodeChatLink(encodedChatLink).getOrNull()
        val chatLinkType = chatLink?.let { linkTypeOf(it::class) }

        val state = ChatLinkState(
            encodedString = encodedChatLink,
            type = chatLinkType,
            data = chatLinkType?.extractUnsafe(chatLink) ?: emptyMap()
        )

        _chatLink.tryEmit(state)
    }

    override fun updateChatLink(type: ChatLinkType<*>) {
        if (chatLink.value.type == type) return

        val state = ChatLinkState(
            encodedString = null,
            type = type
        )

        _chatLink.tryEmit(state)
    }

}