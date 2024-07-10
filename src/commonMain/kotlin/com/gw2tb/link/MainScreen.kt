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

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.gw2tb.gw2api.client.v2.gw2v2ItemsByIds
import com.gw2tb.gw2api.client.v2.gw2v2SkinsById
import com.gw2tb.gw2api.types.GW2ItemId
import com.gw2tb.gw2api.types.v2.GW2v2Item
import com.gw2tb.gw2api.types.v2.GW2v2Skin
import com.gw2tb.gw2chatlinks.ChatLink
import com.gw2tb.link.composables.*
import kotlinx.browser.window
import com.gw2tb.link.link.generated.resources.*
import com.gw2tb.link.link.generated.resources.Res
import com.gw2tb.link.link.generated.resources.background
import com.gw2tb.link.link.generated.resources.github
import com.gw2tb.link.link.generated.resources.githubsponsors
import org.jetbrains.compose.resources.painterResource

@Composable
fun MainScreen(component: MainComponent) {
    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues),
            contentAlignment = Alignment.TopCenter
        ) {
            Image(
                painter = painterResource(Res.drawable.background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                alignment = Alignment.BottomStart,
                contentScale = ContentScale.Crop,
                colorFilter = ColorFilter.tint(Color.Black.copy(alpha = 0.15F), blendMode = BlendMode.Overlay)
            )

            Footer(
                buttons = listOf(
                    FooterSocialButton(
                        drawableResource = Res.drawable.github,
                        contentDescription = "Follow on GitHub",
                        onClick = { component.navigateToUrl("https://github.com/GW2ToolBelt/link") }
                    ),
                    FooterSocialButton(
                        drawableResource = Res.drawable.githubsponsors,
                        contentDescription = "Sponsor on GitHub",
                        onClick = { component.navigateToUrl("https://github.com/GW2ToolBelt/link") }
                    ),
                    FooterSocialButton(
                        drawableResource = Res.drawable.mastodon,
                        contentDescription = "Follow on Mastodon",
                        onClick = { component.navigateToUrl("https://mastodon.social/@themrmilchmann") }
                    )
                ),
                modifier = Modifier
                    .align(Alignment.BottomStart)
            )

            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
            ) {
                @OptIn(ExperimentalComposeUiApi::class)
                Spacer(
                    modifier = Modifier
                        .heightIn(max = with(LocalDensity.current) { (LocalWindowInfo.current.containerSize.height * 0.2F).toDp() })
                        .fillMaxHeight()
                )

                ChatLinkPanel(component = component)
            }
        }
    }
}

@Composable
fun ChatLinkPanel(
    component: MainComponent,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color.White.copy(alpha = 0.95F),
        shape = RoundedCornerShape(4.dp),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = modifier
                .wrapContentHeight()
                .width(with(LocalDensity.current) { window.innerWidth.toDp() } * 0.25F)
                .sizeIn(minWidth = 350.dp)
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .animateContentSize()
        ) {
            val chatLink by component.chatLink.collectAsState()

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                UnderlinedTextField(
                    value = chatLink.encodedString ?: "",
                    onValueChange = component::updateChatLink,
                    modifier = Modifier
                        .weight(1F, fill = true),
                    textStyle = LocalTextStyle.current.copy(
                        fontFamily = Typefaces.JetBrainsMono
                    ),
                    label = { Text("Chat code") },
                    isError = chatLink.type === null,
                    singleLine = true
                )

                CompactIconButton(
                    painter = painterResource(Res.drawable.copy),
                    contentDescription = "Copy",
                    onClick = {
                        component.copyToClipboard(chatLink.encodedString!!)
                        // TODO display toast
                    },
                    modifier = Modifier
                        .padding(12.dp)
                        .size(24.dp),
                    enabled = chatLink.type !== null && chatLink.encodedString !== null
                )

                CompactIconButton(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    onClick = {
                        // TODO impl
                    },
                    modifier = Modifier
                        .padding(12.dp)
                        .size(24.dp)
                )
            }

            var isDropdownExpanded by remember { mutableStateOf(false) }

            @OptIn(ExperimentalMaterial3Api::class)
            (ExposedDropdownMenuBox(
                expanded = isDropdownExpanded,
                onExpandedChange = { isDropdownExpanded = it },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = chatLink.type?.name ?: "Invalid chat code",
                    onValueChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    readOnly = true,
                    singleLine = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.run { TrailingIcon(expanded = isDropdownExpanded) }
                    },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )

                ExposedDropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Item") },
                        onClick = {
                            component.updateChatLink(type = ChatLinkType.Item)
                            isDropdownExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Skin") },
                        onClick = {
                            component.updateChatLink(type = ChatLinkType.Skin)
                            isDropdownExpanded = false
                        }
                    )
                }
            })

            println(chatLink)

            when (chatLink.type) {
                null -> {}
                ChatLinkType.Item -> {
                    val amount by derivedStateOf { chatLink[ChatLinkType.Item.amount] }
                    val itemId by derivedStateOf { chatLink[ChatLinkType.Item.itemId] }
                    val skinId by derivedStateOf { chatLink[ChatLinkType.Item.skinId] }
                    val firstUpgradeSlot by derivedStateOf { chatLink[ChatLinkType.Item.firstUpgradeSlot] }
                    val secondUpgradeSlot by derivedStateOf { chatLink[ChatLinkType.Item.secondUpgradeSlot] }

                    var item: GW2v2Item? by remember { mutableStateOf(null) }
                    var skin: GW2v2Skin? by remember { mutableStateOf(null) }
                    var firstUpgradeItem: GW2v2Item? by remember { mutableStateOf(null) }
                    var secondUpgradeItem: GW2v2Item? by remember { mutableStateOf(null) }

                    LaunchedEffect(itemId) {
                        val ids: List<UInt> = buildList {
                            itemId?.let(::add)
                            firstUpgradeSlot?.let(::add)
                            secondUpgradeSlot?.let(::add)
                        }.distinct()

                        val items = if (ids.isNotEmpty()) {
                            component.apiClient.executeAsync(gw2v2ItemsByIds(ids.map { GW2ItemId(it.toInt()) })).dataOrNull
                        } else {
                            null
                        } ?: emptyList()

                        item = items.find { it.id.raw == itemId?.toInt() }
                        firstUpgradeItem = items.find { it.id.raw == firstUpgradeSlot?.toInt() }
                        secondUpgradeItem = items.find { it.id.raw == secondUpgradeSlot?.toInt() }
                    }

                    LaunchedEffect(skinId) {
                        skin = skinId?.let { component.apiClient.executeAsync(gw2v2SkinsById(it.toInt())).dataOrNull }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconGameObject(
                            iconModel = item?.let { item ->
                                ImageRequest.Builder(LocalPlatformContext.current)
                                    .data(item.icon)
                                    .crossfade(true)
                                    .build()
                            },
                            iconContentDescription = "", // TODO
                            value = item?.name ?: "",
                            label = "Item",
                            modifier = Modifier
                                .fillMaxWidth()
                        )

                        IconGameObject(
                            iconModel = skin?.let { skin ->
                                ImageRequest.Builder(LocalPlatformContext.current)
                                    .data(skin.icon)
                                    .crossfade(true)
                                    .build()
                            },
                            iconContentDescription = "", // TODO
                            value = skin?.name ?: "",
                            label = "Skin",
                            modifier = Modifier
                                .fillMaxWidth()
                        )

                        IconGameObject(
                            iconModel = firstUpgradeItem?.let { firstUpgradeItem ->
                                ImageRequest.Builder(LocalPlatformContext.current)
                                    .data(firstUpgradeItem.icon)
                                    .crossfade(true)
                                    .build()
                            },
                            iconContentDescription = "", // TODO
                            value = firstUpgradeItem?.name ?: "",
                            label = "Upgrade #1",
                            modifier = Modifier
                                .fillMaxWidth()
                        )

                        IconGameObject(
                            iconModel = secondUpgradeItem?.let { secondUpgradeItem ->
                                ImageRequest.Builder(LocalPlatformContext.current)
                                    .data(secondUpgradeItem.icon)
                                    .crossfade(true)
                                    .build()
                            },
                            iconContentDescription = "", // TODO
                            value = secondUpgradeItem?.name ?: "",
                            label = "Upgrade #2",
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                }
                ChatLinkType.Skin -> {
                    val itemId by derivedStateOf { (chatLink as? ChatLink.Skin)?.skinId }
                    var item: GW2v2Skin? by remember { mutableStateOf(null) }

                    LaunchedEffect(itemId) {
                        if (itemId == null) return@LaunchedEffect
                        item = component.apiClient.executeAsync(gw2v2SkinsById(itemId!!.toInt())).dataOrNull
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalPlatformContext.current)
                                .data(item?.icon)
                                .build(),
                            contentDescription = "",
                            imageLoader = ImageLoader(LocalPlatformContext.current),
                            modifier = Modifier
                                .padding(vertical = 4.dp)
                                .size(48.dp)
                        )

                        OutlinedTextField(
                            value = item?.name ?: "",
                            onValueChange = {},
                            modifier = Modifier
                                .weight(1F, fill = true),
                            readOnly = true,
                            label = { Text("Name") },
                            singleLine = true
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun IconGameObject(
    iconModel: Any?,
    iconContentDescription: String,
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier
                .padding(all = 4.dp),
            shadowElevation = 4.dp
        ) {
            AsyncImage(
                model = iconModel,
                contentDescription = iconContentDescription,
                imageLoader = LocalImageLoader.current,
                modifier = Modifier
                    .size(48.dp)
            )
        }

        UnderlinedTextField(
            value = value,
            onValueChange = {},
            modifier = Modifier
                .weight(1F, fill = true),
            readOnly = true,
            label = { Text(label) },
            singleLine = true
        )
    }
}