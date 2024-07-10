/*
 * Copyright (c) 2018-2024 Leon Linhart
 * MACHINE GENERATED FILE, DO NOT EDIT
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
@file:Suppress("PackageDirectoryMismatch", "UnusedImport")
package com.gw2tb.gw2api.types.v2

import com.gw2tb.gw2api.types.*
import com.gw2tb.gw2api.types.internal.*

import kotlinx.serialization.*
import kotlinx.serialization.builtins.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.*

// Generated for type: Title

/**
 * Information about a title.
 *
 * @param id the ID of the title
 * @param name the display name of the title
 * @param achievement the ID of the achievement that grants this title
 * @param achievements the IDs of the achievements that grant this title
 * @param apRequired the amount of AP required to unlock this title
 */
@Serializable
public data class GW2v2Title(
    /** This field holds the ID of the title. */
    val id: Int,
    /** This field holds the display name of the title. */
    val name: String,
    /** This field holds the ID of the achievement that grants this title. */
    @Deprecated(message = "This property is deprecated.")
    val achievement: GW2AchievementId? = null,
    /** This field holds the IDs of the achievements that grant this title. */
    val achievements: List<GW2AchievementId>? = null,
    /** This field holds the amount of AP required to unlock this title. */
    @SerialName("ap_required")
    val apRequired: Int? = null
)