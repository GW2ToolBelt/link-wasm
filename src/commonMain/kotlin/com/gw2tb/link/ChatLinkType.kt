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

import com.gw2tb.gw2chatlinks.ChatLink
import kotlin.reflect.KClass

fun <T : ChatLink> linkTypeOf(cls: KClass<T>): ChatLinkType<T> {
    return when (cls) {
        ChatLink.Item::class -> ChatLinkType.Item
        ChatLink.Skin::class -> ChatLinkType.Skin
        else -> error("Unsupported chat link type: $cls")
    } as ChatLinkType<T>
}

data class ChatLinkPropertyToken<T>(val name: String)

sealed class ChatLinkType<T : ChatLink> {

    abstract val name: String

    fun extractUnsafe(link: ChatLink): Map<ChatLinkPropertyToken<*>, Any?> = extract(link as T)

    abstract fun extract(link: T): Map<ChatLinkPropertyToken<*>, Any?>

    data object Item : ChatLinkType<ChatLink.Item>() {

        val amount = ChatLinkPropertyToken<UByte>("amount")
        val itemId = ChatLinkPropertyToken<UInt>("itemId")
        val skinId = ChatLinkPropertyToken<UInt>("skinId")
        val firstUpgradeSlot = ChatLinkPropertyToken<UInt>("firstUpgradeSlot")
        val secondUpgradeSlot = ChatLinkPropertyToken<UInt>("secondUpgradeSlot")

        override val name: String get() = "Item"

        override fun extract(link: ChatLink.Item): Map<ChatLinkPropertyToken<*>, Any?> = buildMap {
            put(amount, link.amount)
            put(itemId, link.itemId)
            put(skinId, link.skinId)
            put(firstUpgradeSlot, link.firstUpgradeSlot)
            put(secondUpgradeSlot, link.secondUpgradeSlot)
        }

    }

    data object Skin : ChatLinkType<ChatLink.Skin>() {

        val skinId = ChatLinkPropertyToken<UInt>("skinId")

        override val name: String get() = "Skin"

        override fun extract(link: ChatLink.Skin): Map<ChatLinkPropertyToken<*>, Any?> = buildMap {
            put(skinId, link.skinId)
        }

    }

}