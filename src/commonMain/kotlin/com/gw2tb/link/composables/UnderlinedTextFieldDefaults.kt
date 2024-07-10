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

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.*
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Typography
import androidx.compose.material3.internal.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gw2tb.link.composables.tokens.ColorSchemeKeyTokens
import com.gw2tb.link.composables.tokens.ShapeKeyTokens

@Immutable
object UnderlinedTextFieldDefaults {
    /** Default shape for an [OutlinedTextField]. */
    val shape: Shape
        @Composable get() = UnderlinedTextFieldTokens.ContainerShape.value

    /**
     * Converts a shape token key to the local shape provided by the theme The color is subscribed to
     * [LocalShapes] changes
     */
    private val ShapeKeyTokens.value: Shape
        @Composable @ReadOnlyComposable get() = MaterialTheme.shapes.fromToken(this)

    /**
     * Helper function for component shape tokens. Here is an example on how to use component color
     * tokens: ``MaterialTheme.shapes.fromToken(FabPrimarySmallTokens.ContainerShape)``
     */
    private fun Shapes.fromToken(value: ShapeKeyTokens): Shape {
        return when (value) {
            ShapeKeyTokens.CornerExtraLarge -> extraLarge
            ShapeKeyTokens.CornerExtraLargeTop -> extraLarge.top()
            ShapeKeyTokens.CornerExtraSmall -> extraSmall
            ShapeKeyTokens.CornerExtraSmallTop -> extraSmall.top()
            ShapeKeyTokens.CornerFull -> CircleShape
            ShapeKeyTokens.CornerLarge -> large
            ShapeKeyTokens.CornerLargeEnd -> large.end()
            ShapeKeyTokens.CornerLargeTop -> large.top()
            ShapeKeyTokens.CornerMedium -> medium
            ShapeKeyTokens.CornerNone -> RectangleShape
            ShapeKeyTokens.CornerSmall -> small
        }
    }

    /** Helper function for component shape tokens. Used to grab the top values of a shape parameter. */
    internal fun CornerBasedShape.top(): CornerBasedShape {
        return copy(bottomStart = CornerSize(0.0.dp), bottomEnd = CornerSize(0.0.dp))
    }

    /**
     * Helper function for component shape tokens. Used to grab the bottom values of a shape parameter.
     */
    internal fun CornerBasedShape.bottom(): CornerBasedShape {
        return copy(topStart = CornerSize(0.0.dp), topEnd = CornerSize(0.0.dp))
    }

    /**
     * Helper function for component shape tokens. Used to grab the start values of a shape parameter.
     */
    internal fun CornerBasedShape.start(): CornerBasedShape {
        return copy(topEnd = CornerSize(0.0.dp), bottomEnd = CornerSize(0.0.dp))
    }

    /** Helper function for component shape tokens. Used to grab the end values of a shape parameter. */
    internal fun CornerBasedShape.end(): CornerBasedShape {
        return copy(topStart = CornerSize(0.0.dp), bottomStart = CornerSize(0.0.dp))
    }

    /**
     * The default min height applied to an [OutlinedTextField]. Note that you can override it by
     * applying Modifier.heightIn directly on a text field.
     */
    val MinHeight = 56.dp

    /**
     * The default min width applied to an [OutlinedTextField]. Note that you can override it by
     * applying Modifier.widthIn directly on a text field.
     */
    val MinWidth = 280.dp

    /** The default thickness of the border in [OutlinedTextField] in unfocused state. */
    val UnfocusedBorderThickness = 1.dp

    /** The default thickness of the border in [OutlinedTextField] in focused state. */
    val FocusedBorderThickness = 2.dp

    /**
     * Composable that draws a default container for an [OutlinedTextField] with a border stroke.
     * You can apply it to a [BasicTextField] using [DecorationBox] to create a custom text field
     * based on the styling of a Material outlined text field. The [OutlinedTextField] component
     * applies it automatically.
     *
     * @param enabled whether the text field is enabled
     * @param isError whether the text field's current value is in error
     * @param interactionSource the [InteractionSource] of the text field. Used to determine if the
     *   text field is in focus or not
     * @param modifier the [Modifier] of this container
     * @param colors [TextFieldColors] used to resolve colors of the text field
     * @param shape the shape of this container
     * @param focusedBorderThickness thickness of the border when the text field is focused
     * @param unfocusedBorderThickness thickness of the border when the text field is not focused
     */
    @ExperimentalMaterial3Api
    @Composable
    fun Container(
        enabled: Boolean,
        isError: Boolean,
        interactionSource: InteractionSource,
        modifier: Modifier = Modifier,
        colors: UnderlinedTextFieldColors = colors(),
        shape: Shape = UnderlinedTextFieldDefaults.shape
    ) {
        val focused = interactionSource.collectIsFocusedAsState().value
        val containerColor =
            animateColorAsState(
                targetValue = colors.containerColor(enabled, isError, focused),
                animationSpec = tween(durationMillis = TextFieldAnimationDuration),
            )
        Box(
            modifier
                .textFieldBackground(containerColor::value, shape)
        )
    }

    /**
     * A decoration box used to create custom text fields based on <a
     * href="https://m3.material.io/components/text-fields/overview" class="external"
     * target="_blank">Material Design outlined text field</a>.
     *
     * If your text field requires customising elements that aren't exposed by [OutlinedTextField],
     * consider using this decoration box to achieve the desired design.
     *
     * For example, if you wish to customize the thickness of the border, you can pass a custom
     * [Container] to this decoration box's [container].
     *
     * An example of building a custom text field using [DecorationBox]:
     *
     * @sample androidx.compose.material3.samples.CustomOutlinedTextFieldBasedOnDecorationBox
     *
     * @param value the input [String] shown by the text field
     * @param innerTextField input text field that this decoration box wraps. You will pass here a
     *   framework-controlled composable parameter "innerTextField" from the decorationBox lambda of
     *   the [BasicTextField]
     * @param enabled the enabled state of the text field. When `false`, this decoration box will
     *   appear visually disabled. This must be the same value that is passed to [BasicTextField].
     * @param singleLine indicates if this is a single line or multi line text field. This must be
     *   the same value that is passed to [BasicTextField].
     * @param visualTransformation transforms the visual representation of the input [value]. This
     *   must be the same value that is passed to [BasicTextField].
     * @param interactionSource the read-only [InteractionSource] representing the stream of
     *   [Interaction]s for this text field. You must first create and pass in your own `remember`ed
     *   [MutableInteractionSource] instance to the [BasicTextField] for it to dispatch events. And
     *   then pass the same instance to this decoration box to observe [Interaction]s and customize
     *   the appearance / behavior of this text field in different states.
     * @param isError indicates if the text field's current value is in an error state. When `true`,
     *   this decoration box will display its contents in an error color.
     * @param label the optional label to be displayed inside the text field container. The default
     *   text style for internal [Text] is [Typography.bodySmall] when the text field is in focus
     *   and [Typography.bodyLarge] when the text field is not in focus.
     * @param placeholder the optional placeholder to be displayed when the text field is in focus
     *   and the input text is empty. The default text style for internal [Text] is
     *   [Typography.bodyLarge].
     * @param leadingIcon the optional leading icon to be displayed at the beginning of the text
     *   field container
     * @param trailingIcon the optional trailing icon to be displayed at the end of the text field
     *   container
     * @param prefix the optional prefix to be displayed before the input text in the text field
     * @param suffix the optional suffix to be displayed after the input text in the text field
     * @param supportingText the optional supporting text to be displayed below the text field
     * @param colors [TextFieldColors] that will be used to resolve the colors used for this text
     *   field in different states. See [OutlinedTextFieldDefaults.colors].
     * @param contentPadding the padding applied between the internal elements of this decoration
     *   box and the edge of its container
     * @param container the container to be drawn behind the text field. By default, this is
     *   transparent and only includes a border. The cutout in the border to fit the [label] will be
     *   automatically added by the framework. Default colors for the container come from the
     *   [colors].
     */
    @Composable
    @ExperimentalMaterial3Api
    fun DecorationBox(
        value: String,
        innerTextField: @Composable () -> Unit,
        enabled: Boolean,
        singleLine: Boolean,
        visualTransformation: VisualTransformation,
        interactionSource: InteractionSource,
        isError: Boolean = false,
        label: @Composable (() -> Unit)? = null,
        placeholder: @Composable (() -> Unit)? = null,
        leadingIcon: @Composable (() -> Unit)? = null,
        trailingIcon: @Composable (() -> Unit)? = null,
        prefix: @Composable (() -> Unit)? = null,
        suffix: @Composable (() -> Unit)? = null,
        supportingText: @Composable (() -> Unit)? = null,
        colors: UnderlinedTextFieldColors = colors(),
        contentPadding: PaddingValues = contentPadding(),
        container: @Composable () -> Unit = {
            Container(
                enabled = enabled,
                isError = isError,
                interactionSource = interactionSource,
                modifier = Modifier,
                colors = colors,
                shape = shape
            )
        }
    ) {
        UnderlinedDecorationBox(
            value = value,
            visualTransformation = visualTransformation,
            innerTextField = innerTextField,
            placeholder = placeholder,
            label = label,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            prefix = prefix,
            suffix = suffix,
            supportingText = supportingText,
            singleLine = singleLine,
            enabled = enabled,
            isError = isError,
            interactionSource = interactionSource,
            colors = colors,
            contentPadding = contentPadding,
            container = container
        )
    }

    /**
     * Default content padding applied to [UnderlinedTextField]. See [PaddingValues] for more details.
     */
    fun contentPadding(
        start: Dp = 0.dp,
        top: Dp = TextFieldPadding,
        end: Dp = 0.dp,
        bottom: Dp = TextFieldPadding
    ): PaddingValues = PaddingValues(start, top, end, bottom)

    /**
     * Default padding applied to supporting text for both [TextField] and [OutlinedTextField]. See
     * [PaddingValues] for more details.
     */
    internal fun supportingTextPadding(
        start: Dp = TextFieldPadding,
        top: Dp = SupportingTopPadding,
        end: Dp = TextFieldPadding,
        bottom: Dp = 0.dp,
    ): PaddingValues = PaddingValues(start, top, end, bottom)

    /**
     * Creates a [TextFieldColors] that represents the default input text, container, and content
     * colors (including label, placeholder, icons, etc.) used in an [OutlinedTextField].
     */
    @Composable
    fun colors() = MaterialTheme.colorScheme.defaultOutlinedTextFieldColors

    /**
     * Creates a [TextFieldColors] that represents the default input text, container, and content
     * colors (including label, placeholder, icons, etc.) used in an [OutlinedTextField].
     *
     * @param focusedTextColor the color used for the input text of this text field when focused
     * @param unfocusedTextColor the color used for the input text of this text field when not
     *   focused
     * @param disabledTextColor the color used for the input text of this text field when disabled
     * @param errorTextColor the color used for the input text of this text field when in error
     *   state
     * @param focusedContainerColor the container color for this text field when focused
     * @param unfocusedContainerColor the container color for this text field when not focused
     * @param disabledContainerColor the container color for this text field when disabled
     * @param errorContainerColor the container color for this text field when in error state
     * @param cursorColor the cursor color for this text field
     * @param errorCursorColor the cursor color for this text field when in error state
     * @param selectionColors the colors used when the input text of this text field is selected
     * @param focusedBorderColor the border color for this text field when focused
     * @param unfocusedBorderColor the border color for this text field when not focused
     * @param disabledBorderColor the border color for this text field when disabled
     * @param errorBorderColor the border color for this text field when in error state
     * @param focusedLeadingIconColor the leading icon color for this text field when focused
     * @param unfocusedLeadingIconColor the leading icon color for this text field when not focused
     * @param disabledLeadingIconColor the leading icon color for this text field when disabled
     * @param errorLeadingIconColor the leading icon color for this text field when in error state
     * @param focusedTrailingIconColor the trailing icon color for this text field when focused
     * @param unfocusedTrailingIconColor the trailing icon color for this text field when not
     *   focused
     * @param disabledTrailingIconColor the trailing icon color for this text field when disabled
     * @param errorTrailingIconColor the trailing icon color for this text field when in error state
     * @param focusedLabelColor the label color for this text field when focused
     * @param unfocusedLabelColor the label color for this text field when not focused
     * @param disabledLabelColor the label color for this text field when disabled
     * @param errorLabelColor the label color for this text field when in error state
     * @param focusedPlaceholderColor the placeholder color for this text field when focused
     * @param unfocusedPlaceholderColor the placeholder color for this text field when not focused
     * @param disabledPlaceholderColor the placeholder color for this text field when disabled
     * @param errorPlaceholderColor the placeholder color for this text field when in error state
     * @param focusedSupportingTextColor the supporting text color for this text field when focused
     * @param unfocusedSupportingTextColor the supporting text color for this text field when not
     *   focused
     * @param disabledSupportingTextColor the supporting text color for this text field when
     *   disabled
     * @param errorSupportingTextColor the supporting text color for this text field when in error
     *   state
     * @param focusedPrefixColor the prefix color for this text field when focused
     * @param unfocusedPrefixColor the prefix color for this text field when not focused
     * @param disabledPrefixColor the prefix color for this text field when disabled
     * @param errorPrefixColor the prefix color for this text field when in error state
     * @param focusedSuffixColor the suffix color for this text field when focused
     * @param unfocusedSuffixColor the suffix color for this text field when not focused
     * @param disabledSuffixColor the suffix color for this text field when disabled
     * @param errorSuffixColor the suffix color for this text field when in error state
     */
    @Composable
    fun colors(
        focusedTextColor: Color = Color.Unspecified,
        unfocusedTextColor: Color = Color.Unspecified,
        disabledTextColor: Color = Color.Unspecified,
        errorTextColor: Color = Color.Unspecified,
        focusedContainerColor: Color = Color.Unspecified,
        unfocusedContainerColor: Color = Color.Unspecified,
        disabledContainerColor: Color = Color.Unspecified,
        errorContainerColor: Color = Color.Unspecified,
        cursorColor: Color = Color.Unspecified,
        errorCursorColor: Color = Color.Unspecified,
        selectionColors: TextSelectionColors? = null,
        focusedBorderColor: Color = Color.Unspecified,
        unfocusedBorderColor: Color = Color.Unspecified,
        disabledBorderColor: Color = Color.Unspecified,
        errorBorderColor: Color = Color.Unspecified,
        focusedLeadingIconColor: Color = Color.Unspecified,
        unfocusedLeadingIconColor: Color = Color.Unspecified,
        disabledLeadingIconColor: Color = Color.Unspecified,
        errorLeadingIconColor: Color = Color.Unspecified,
        focusedTrailingIconColor: Color = Color.Unspecified,
        unfocusedTrailingIconColor: Color = Color.Unspecified,
        disabledTrailingIconColor: Color = Color.Unspecified,
        errorTrailingIconColor: Color = Color.Unspecified,
        focusedLabelColor: Color = Color.Unspecified,
        unfocusedLabelColor: Color = Color.Unspecified,
        disabledLabelColor: Color = Color.Unspecified,
        errorLabelColor: Color = Color.Unspecified,
        focusedPlaceholderColor: Color = Color.Unspecified,
        unfocusedPlaceholderColor: Color = Color.Unspecified,
        disabledPlaceholderColor: Color = Color.Unspecified,
        errorPlaceholderColor: Color = Color.Unspecified,
        focusedSupportingTextColor: Color = Color.Unspecified,
        unfocusedSupportingTextColor: Color = Color.Unspecified,
        disabledSupportingTextColor: Color = Color.Unspecified,
        errorSupportingTextColor: Color = Color.Unspecified,
        focusedPrefixColor: Color = Color.Unspecified,
        unfocusedPrefixColor: Color = Color.Unspecified,
        disabledPrefixColor: Color = Color.Unspecified,
        errorPrefixColor: Color = Color.Unspecified,
        focusedSuffixColor: Color = Color.Unspecified,
        unfocusedSuffixColor: Color = Color.Unspecified,
        disabledSuffixColor: Color = Color.Unspecified,
        errorSuffixColor: Color = Color.Unspecified,
    ): UnderlinedTextFieldColors =
        MaterialTheme.colorScheme.defaultOutlinedTextFieldColors.copy(
            focusedTextColor = focusedTextColor,
            unfocusedTextColor = unfocusedTextColor,
            disabledTextColor = disabledTextColor,
            errorTextColor = errorTextColor,
            focusedContainerColor = focusedContainerColor,
            unfocusedContainerColor = unfocusedContainerColor,
            disabledContainerColor = disabledContainerColor,
            errorContainerColor = errorContainerColor,
            cursorColor = cursorColor,
            errorCursorColor = errorCursorColor,
            textSelectionColors = selectionColors,
            focusedIndicatorColor = focusedBorderColor,
            unfocusedIndicatorColor = unfocusedBorderColor,
            disabledIndicatorColor = disabledBorderColor,
            errorIndicatorColor = errorBorderColor,
            focusedLeadingIconColor = focusedLeadingIconColor,
            unfocusedLeadingIconColor = unfocusedLeadingIconColor,
            disabledLeadingIconColor = disabledLeadingIconColor,
            errorLeadingIconColor = errorLeadingIconColor,
            focusedTrailingIconColor = focusedTrailingIconColor,
            unfocusedTrailingIconColor = unfocusedTrailingIconColor,
            disabledTrailingIconColor = disabledTrailingIconColor,
            errorTrailingIconColor = errorTrailingIconColor,
            focusedLabelColor = focusedLabelColor,
            unfocusedLabelColor = unfocusedLabelColor,
            disabledLabelColor = disabledLabelColor,
            errorLabelColor = errorLabelColor,
            focusedPlaceholderColor = focusedPlaceholderColor,
            unfocusedPlaceholderColor = unfocusedPlaceholderColor,
            disabledPlaceholderColor = disabledPlaceholderColor,
            errorPlaceholderColor = errorPlaceholderColor,
            focusedSupportingTextColor = focusedSupportingTextColor,
            unfocusedSupportingTextColor = unfocusedSupportingTextColor,
            disabledSupportingTextColor = disabledSupportingTextColor,
            errorSupportingTextColor = errorSupportingTextColor,
            focusedPrefixColor = focusedPrefixColor,
            unfocusedPrefixColor = unfocusedPrefixColor,
            disabledPrefixColor = disabledPrefixColor,
            errorPrefixColor = errorPrefixColor,
            focusedSuffixColor = focusedSuffixColor,
            unfocusedSuffixColor = unfocusedSuffixColor,
            disabledSuffixColor = disabledSuffixColor,
            errorSuffixColor = errorSuffixColor,
        )

    internal val ColorScheme.defaultOutlinedTextFieldColors: UnderlinedTextFieldColors
        @Composable
        get() {
            return UnderlinedTextFieldColors(
                    focusedTextColor = fromToken(UnderlinedTextFieldTokens.FocusInputColor),
                    unfocusedTextColor = fromToken(UnderlinedTextFieldTokens.InputColor),
                    disabledTextColor =
                    fromToken(UnderlinedTextFieldTokens.DisabledInputColor)
                        .copy(alpha = UnderlinedTextFieldTokens.DisabledInputOpacity),
                    errorTextColor = fromToken(UnderlinedTextFieldTokens.ErrorInputColor),
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    errorContainerColor = Color.Transparent,
                    cursorColor = fromToken(UnderlinedTextFieldTokens.CaretColor),
                    errorCursorColor = fromToken(UnderlinedTextFieldTokens.ErrorFocusCaretColor),
                    textSelectionColors = LocalTextSelectionColors.current,
                    focusedIndicatorColor =
                    fromToken(UnderlinedTextFieldTokens.FocusOutlineColor),
                    unfocusedIndicatorColor = fromToken(UnderlinedTextFieldTokens.OutlineColor),
                    disabledIndicatorColor =
                    fromToken(UnderlinedTextFieldTokens.DisabledOutlineColor)
                        .copy(alpha = UnderlinedTextFieldTokens.DisabledOutlineOpacity),
                    errorIndicatorColor = fromToken(UnderlinedTextFieldTokens.ErrorOutlineColor),
                    focusedLeadingIconColor =
                    fromToken(UnderlinedTextFieldTokens.FocusLeadingIconColor),
                    unfocusedLeadingIconColor =
                    fromToken(UnderlinedTextFieldTokens.LeadingIconColor),
                    disabledLeadingIconColor =
                    fromToken(UnderlinedTextFieldTokens.DisabledLeadingIconColor)
                        .copy(alpha = UnderlinedTextFieldTokens.DisabledLeadingIconOpacity),
                    errorLeadingIconColor =
                    fromToken(UnderlinedTextFieldTokens.ErrorLeadingIconColor),
                    focusedTrailingIconColor =
                    fromToken(UnderlinedTextFieldTokens.FocusTrailingIconColor),
                    unfocusedTrailingIconColor =
                    fromToken(UnderlinedTextFieldTokens.TrailingIconColor),
                    disabledTrailingIconColor =
                    fromToken(UnderlinedTextFieldTokens.DisabledTrailingIconColor)
                        .copy(alpha = UnderlinedTextFieldTokens.DisabledTrailingIconOpacity),
                    errorTrailingIconColor =
                    fromToken(UnderlinedTextFieldTokens.ErrorTrailingIconColor),
                    focusedLabelColor = fromToken(UnderlinedTextFieldTokens.FocusLabelColor),
                    unfocusedLabelColor = fromToken(UnderlinedTextFieldTokens.LabelColor),
                    disabledLabelColor =
                    fromToken(UnderlinedTextFieldTokens.DisabledLabelColor)
                        .copy(alpha = UnderlinedTextFieldTokens.DisabledLabelOpacity),
                    errorLabelColor = fromToken(UnderlinedTextFieldTokens.ErrorLabelColor),
                    focusedPlaceholderColor =
                    fromToken(UnderlinedTextFieldTokens.InputPlaceholderColor),
                    unfocusedPlaceholderColor =
                    fromToken(UnderlinedTextFieldTokens.InputPlaceholderColor),
                    disabledPlaceholderColor =
                    fromToken(UnderlinedTextFieldTokens.DisabledInputColor)
                        .copy(alpha = UnderlinedTextFieldTokens.DisabledInputOpacity),
                    errorPlaceholderColor =
                    fromToken(UnderlinedTextFieldTokens.InputPlaceholderColor),
                    focusedSupportingTextColor =
                    fromToken(UnderlinedTextFieldTokens.FocusSupportingColor),
                    unfocusedSupportingTextColor =
                    fromToken(UnderlinedTextFieldTokens.SupportingColor),
                    disabledSupportingTextColor =
                    fromToken(UnderlinedTextFieldTokens.DisabledSupportingColor)
                        .copy(alpha = UnderlinedTextFieldTokens.DisabledSupportingOpacity),
                    errorSupportingTextColor =
                    fromToken(UnderlinedTextFieldTokens.ErrorSupportingColor),
                    focusedPrefixColor = fromToken(UnderlinedTextFieldTokens.InputPrefixColor),
                    unfocusedPrefixColor = fromToken(UnderlinedTextFieldTokens.InputPrefixColor),
                    disabledPrefixColor =
                    fromToken(UnderlinedTextFieldTokens.InputPrefixColor)
                        .copy(alpha = UnderlinedTextFieldTokens.DisabledInputOpacity),
                    errorPrefixColor = fromToken(UnderlinedTextFieldTokens.InputPrefixColor),
                    focusedSuffixColor = fromToken(UnderlinedTextFieldTokens.InputSuffixColor),
                    unfocusedSuffixColor = fromToken(UnderlinedTextFieldTokens.InputSuffixColor),
                    disabledSuffixColor =
                    fromToken(UnderlinedTextFieldTokens.InputSuffixColor)
                        .copy(alpha = UnderlinedTextFieldTokens.DisabledInputOpacity),
                    errorSuffixColor = fromToken(UnderlinedTextFieldTokens.InputSuffixColor)
                )
        }

    @Stable
    internal fun ColorScheme.fromToken(value: ColorSchemeKeyTokens): Color {
        return when (value) {
            ColorSchemeKeyTokens.Background -> background
            ColorSchemeKeyTokens.Error -> error
            ColorSchemeKeyTokens.ErrorContainer -> errorContainer
            ColorSchemeKeyTokens.InverseOnSurface -> inverseOnSurface
            ColorSchemeKeyTokens.InversePrimary -> inversePrimary
            ColorSchemeKeyTokens.InverseSurface -> inverseSurface
            ColorSchemeKeyTokens.OnBackground -> onBackground
            ColorSchemeKeyTokens.OnError -> onError
            ColorSchemeKeyTokens.OnErrorContainer -> onErrorContainer
            ColorSchemeKeyTokens.OnPrimary -> onPrimary
            ColorSchemeKeyTokens.OnPrimaryContainer -> onPrimaryContainer
            ColorSchemeKeyTokens.OnSecondary -> onSecondary
            ColorSchemeKeyTokens.OnSecondaryContainer -> onSecondaryContainer
            ColorSchemeKeyTokens.OnSurface -> onSurface
            ColorSchemeKeyTokens.OnSurfaceVariant -> onSurfaceVariant
            ColorSchemeKeyTokens.SurfaceTint -> surfaceTint
            ColorSchemeKeyTokens.OnTertiary -> onTertiary
            ColorSchemeKeyTokens.OnTertiaryContainer -> onTertiaryContainer
            ColorSchemeKeyTokens.Outline -> outline
            ColorSchemeKeyTokens.OutlineVariant -> outlineVariant
            ColorSchemeKeyTokens.Primary -> primary
            ColorSchemeKeyTokens.PrimaryContainer -> primaryContainer
            ColorSchemeKeyTokens.Scrim -> scrim
            ColorSchemeKeyTokens.Secondary -> secondary
            ColorSchemeKeyTokens.SecondaryContainer -> secondaryContainer
            ColorSchemeKeyTokens.Surface -> surface
            ColorSchemeKeyTokens.SurfaceVariant -> surfaceVariant
            ColorSchemeKeyTokens.SurfaceBright -> surfaceBright
            ColorSchemeKeyTokens.SurfaceContainer -> surfaceContainer
            ColorSchemeKeyTokens.SurfaceContainerHigh -> surfaceContainerHigh
            ColorSchemeKeyTokens.SurfaceContainerHighest -> surfaceContainerHighest
            ColorSchemeKeyTokens.SurfaceContainerLow -> surfaceContainerLow
            ColorSchemeKeyTokens.SurfaceContainerLowest -> surfaceContainerLowest
            ColorSchemeKeyTokens.SurfaceDim -> surfaceDim
            ColorSchemeKeyTokens.Tertiary -> tertiary
            ColorSchemeKeyTokens.TertiaryContainer -> tertiaryContainer
            else -> Color.Unspecified
        }
    }
}

/**
 * Represents the colors of the input text, container, and content (including label, placeholder,
 * leading and trailing icons) used in a text field in different states.
 *
 * @param focusedTextColor the color used for the input text of this text field when focused
 * @param unfocusedTextColor the color used for the input text of this text field when not focused
 * @param disabledTextColor the color used for the input text of this text field when disabled
 * @param errorTextColor the color used for the input text of this text field when in error state
 * @param focusedContainerColor the container color for this text field when focused
 * @param unfocusedContainerColor the container color for this text field when not focused
 * @param disabledContainerColor the container color for this text field when disabled
 * @param errorContainerColor the container color for this text field when in error state
 * @param cursorColor the cursor color for this text field
 * @param errorCursorColor the cursor color for this text field when in error state
 * @param textSelectionColors the colors used when the input text of this text field is selected
 * @param focusedIndicatorColor the indicator color for this text field when focused
 * @param unfocusedIndicatorColor the indicator color for this text field when not focused
 * @param disabledIndicatorColor the indicator color for this text field when disabled
 * @param errorIndicatorColor the indicator color for this text field when in error state
 * @param focusedLeadingIconColor the leading icon color for this text field when focused
 * @param unfocusedLeadingIconColor the leading icon color for this text field when not focused
 * @param disabledLeadingIconColor the leading icon color for this text field when disabled
 * @param errorLeadingIconColor the leading icon color for this text field when in error state
 * @param focusedTrailingIconColor the trailing icon color for this text field when focused
 * @param unfocusedTrailingIconColor the trailing icon color for this text field when not focused
 * @param disabledTrailingIconColor the trailing icon color for this text field when disabled
 * @param errorTrailingIconColor the trailing icon color for this text field when in error state
 * @param focusedLabelColor the label color for this text field when focused
 * @param unfocusedLabelColor the label color for this text field when not focused
 * @param disabledLabelColor the label color for this text field when disabled
 * @param errorLabelColor the label color for this text field when in error state
 * @param focusedPlaceholderColor the placeholder color for this text field when focused
 * @param unfocusedPlaceholderColor the placeholder color for this text field when not focused
 * @param disabledPlaceholderColor the placeholder color for this text field when disabled
 * @param errorPlaceholderColor the placeholder color for this text field when in error state
 * @param focusedSupportingTextColor the supporting text color for this text field when focused
 * @param unfocusedSupportingTextColor the supporting text color for this text field when not
 *   focused
 * @param disabledSupportingTextColor the supporting text color for this text field when disabled
 * @param errorSupportingTextColor the supporting text color for this text field when in error state
 * @param focusedPrefixColor the prefix color for this text field when focused
 * @param unfocusedPrefixColor the prefix color for this text field when not focused
 * @param disabledPrefixColor the prefix color for this text field when disabled
 * @param errorPrefixColor the prefix color for this text field when in error state
 * @param focusedSuffixColor the suffix color for this text field when focused
 * @param unfocusedSuffixColor the suffix color for this text field when not focused
 * @param disabledSuffixColor the suffix color for this text field when disabled
 * @param errorSuffixColor the suffix color for this text field when in error state
 * @constructor create an instance with arbitrary colors. See [TextFieldDefaults.colors] for the
 *   default colors used in [TextField]. See [OutlinedTextFieldDefaults.colors] for the default
 *   colors used in [OutlinedTextField].
 */
@Immutable
class UnderlinedTextFieldColors(
    val focusedTextColor: Color,
    val unfocusedTextColor: Color,
    val disabledTextColor: Color,
    val errorTextColor: Color,
    val focusedContainerColor: Color,
    val unfocusedContainerColor: Color,
    val disabledContainerColor: Color,
    val errorContainerColor: Color,
    val cursorColor: Color,
    val errorCursorColor: Color,
    val textSelectionColors: TextSelectionColors,
    val focusedIndicatorColor: Color,
    val unfocusedIndicatorColor: Color,
    val disabledIndicatorColor: Color,
    val errorIndicatorColor: Color,
    val focusedLeadingIconColor: Color,
    val unfocusedLeadingIconColor: Color,
    val disabledLeadingIconColor: Color,
    val errorLeadingIconColor: Color,
    val focusedTrailingIconColor: Color,
    val unfocusedTrailingIconColor: Color,
    val disabledTrailingIconColor: Color,
    val errorTrailingIconColor: Color,
    val focusedLabelColor: Color,
    val unfocusedLabelColor: Color,
    val disabledLabelColor: Color,
    val errorLabelColor: Color,
    val focusedPlaceholderColor: Color,
    val unfocusedPlaceholderColor: Color,
    val disabledPlaceholderColor: Color,
    val errorPlaceholderColor: Color,
    val focusedSupportingTextColor: Color,
    val unfocusedSupportingTextColor: Color,
    val disabledSupportingTextColor: Color,
    val errorSupportingTextColor: Color,
    val focusedPrefixColor: Color,
    val unfocusedPrefixColor: Color,
    val disabledPrefixColor: Color,
    val errorPrefixColor: Color,
    val focusedSuffixColor: Color,
    val unfocusedSuffixColor: Color,
    val disabledSuffixColor: Color,
    val errorSuffixColor: Color,
) {

    /**
     * Returns a copy of this ChipColors, optionally overriding some of the values. This uses the
     * Color.Unspecified to mean “use the value from the source”
     */
    fun copy(
        focusedTextColor: Color = this.focusedTextColor,
        unfocusedTextColor: Color = this.unfocusedTextColor,
        disabledTextColor: Color = this.disabledTextColor,
        errorTextColor: Color = this.errorTextColor,
        focusedContainerColor: Color = this.focusedContainerColor,
        unfocusedContainerColor: Color = this.unfocusedContainerColor,
        disabledContainerColor: Color = this.disabledContainerColor,
        errorContainerColor: Color = this.errorContainerColor,
        cursorColor: Color = this.cursorColor,
        errorCursorColor: Color = this.errorCursorColor,
        textSelectionColors: TextSelectionColors? = this.textSelectionColors,
        focusedIndicatorColor: Color = this.focusedIndicatorColor,
        unfocusedIndicatorColor: Color = this.unfocusedIndicatorColor,
        disabledIndicatorColor: Color = this.disabledIndicatorColor,
        errorIndicatorColor: Color = this.errorIndicatorColor,
        focusedLeadingIconColor: Color = this.focusedLeadingIconColor,
        unfocusedLeadingIconColor: Color = this.unfocusedLeadingIconColor,
        disabledLeadingIconColor: Color = this.disabledLeadingIconColor,
        errorLeadingIconColor: Color = this.errorLeadingIconColor,
        focusedTrailingIconColor: Color = this.focusedTrailingIconColor,
        unfocusedTrailingIconColor: Color = this.unfocusedTrailingIconColor,
        disabledTrailingIconColor: Color = this.disabledTrailingIconColor,
        errorTrailingIconColor: Color = this.errorTrailingIconColor,
        focusedLabelColor: Color = this.focusedLabelColor,
        unfocusedLabelColor: Color = this.unfocusedLabelColor,
        disabledLabelColor: Color = this.disabledLabelColor,
        errorLabelColor: Color = this.errorLabelColor,
        focusedPlaceholderColor: Color = this.focusedPlaceholderColor,
        unfocusedPlaceholderColor: Color = this.unfocusedPlaceholderColor,
        disabledPlaceholderColor: Color = this.disabledPlaceholderColor,
        errorPlaceholderColor: Color = this.errorPlaceholderColor,
        focusedSupportingTextColor: Color = this.focusedSupportingTextColor,
        unfocusedSupportingTextColor: Color = this.unfocusedSupportingTextColor,
        disabledSupportingTextColor: Color = this.disabledSupportingTextColor,
        errorSupportingTextColor: Color = this.errorSupportingTextColor,
        focusedPrefixColor: Color = this.focusedPrefixColor,
        unfocusedPrefixColor: Color = this.unfocusedPrefixColor,
        disabledPrefixColor: Color = this.disabledPrefixColor,
        errorPrefixColor: Color = this.errorPrefixColor,
        focusedSuffixColor: Color = this.focusedSuffixColor,
        unfocusedSuffixColor: Color = this.unfocusedSuffixColor,
        disabledSuffixColor: Color = this.disabledSuffixColor,
        errorSuffixColor: Color = this.errorSuffixColor,
    ) =
        UnderlinedTextFieldColors(
            focusedTextColor.takeOrElse { this.focusedTextColor },
            unfocusedTextColor.takeOrElse { this.unfocusedTextColor },
            disabledTextColor.takeOrElse { this.disabledTextColor },
            errorTextColor.takeOrElse { this.errorTextColor },
            focusedContainerColor.takeOrElse { this.focusedContainerColor },
            unfocusedContainerColor.takeOrElse { this.unfocusedContainerColor },
            disabledContainerColor.takeOrElse { this.disabledContainerColor },
            errorContainerColor.takeOrElse { this.errorContainerColor },
            cursorColor.takeOrElse { this.cursorColor },
            errorCursorColor.takeOrElse { this.errorCursorColor },
            textSelectionColors.takeOrElse { this.textSelectionColors },
            focusedIndicatorColor.takeOrElse { this.focusedIndicatorColor },
            unfocusedIndicatorColor.takeOrElse { this.unfocusedIndicatorColor },
            disabledIndicatorColor.takeOrElse { this.disabledIndicatorColor },
            errorIndicatorColor.takeOrElse { this.errorIndicatorColor },
            focusedLeadingIconColor.takeOrElse { this.focusedLeadingIconColor },
            unfocusedLeadingIconColor.takeOrElse { this.unfocusedLeadingIconColor },
            disabledLeadingIconColor.takeOrElse { this.disabledLeadingIconColor },
            errorLeadingIconColor.takeOrElse { this.errorLeadingIconColor },
            focusedTrailingIconColor.takeOrElse { this.focusedTrailingIconColor },
            unfocusedTrailingIconColor.takeOrElse { this.unfocusedTrailingIconColor },
            disabledTrailingIconColor.takeOrElse { this.disabledTrailingIconColor },
            errorTrailingIconColor.takeOrElse { this.errorTrailingIconColor },
            focusedLabelColor.takeOrElse { this.focusedLabelColor },
            unfocusedLabelColor.takeOrElse { this.unfocusedLabelColor },
            disabledLabelColor.takeOrElse { this.disabledLabelColor },
            errorLabelColor.takeOrElse { this.errorLabelColor },
            focusedPlaceholderColor.takeOrElse { this.focusedPlaceholderColor },
            unfocusedPlaceholderColor.takeOrElse { this.unfocusedPlaceholderColor },
            disabledPlaceholderColor.takeOrElse { this.disabledPlaceholderColor },
            errorPlaceholderColor.takeOrElse { this.errorPlaceholderColor },
            focusedSupportingTextColor.takeOrElse { this.focusedSupportingTextColor },
            unfocusedSupportingTextColor.takeOrElse { this.unfocusedSupportingTextColor },
            disabledSupportingTextColor.takeOrElse { this.disabledSupportingTextColor },
            errorSupportingTextColor.takeOrElse { this.errorSupportingTextColor },
            focusedPrefixColor.takeOrElse { this.focusedPrefixColor },
            unfocusedPrefixColor.takeOrElse { this.unfocusedPrefixColor },
            disabledPrefixColor.takeOrElse { this.disabledPrefixColor },
            errorPrefixColor.takeOrElse { this.errorPrefixColor },
            focusedSuffixColor.takeOrElse { this.focusedSuffixColor },
            unfocusedSuffixColor.takeOrElse { this.unfocusedSuffixColor },
            disabledSuffixColor.takeOrElse { this.disabledSuffixColor },
            errorSuffixColor.takeOrElse { this.errorSuffixColor },
        )

    internal fun TextSelectionColors?.takeOrElse(
        block: () -> TextSelectionColors
    ): TextSelectionColors = this ?: block()

    /**
     * Represents the color used for the leading icon of this text field.
     *
     * @param enabled whether the text field is enabled
     * @param isError whether the text field's current value is in error
     * @param focused whether the text field is in focus
     */
    @Stable
    internal fun leadingIconColor(
        enabled: Boolean,
        isError: Boolean,
        focused: Boolean,
    ): Color =
        when {
            !enabled -> disabledLeadingIconColor
            isError -> errorLeadingIconColor
            focused -> focusedLeadingIconColor
            else -> unfocusedLeadingIconColor
        }

    /**
     * Represents the color used for the trailing icon of this text field.
     *
     * @param enabled whether the text field is enabled
     * @param isError whether the text field's current value is in error
     * @param focused whether the text field is in focus
     */
    @Stable
    internal fun trailingIconColor(
        enabled: Boolean,
        isError: Boolean,
        focused: Boolean,
    ): Color =
        when {
            !enabled -> disabledTrailingIconColor
            isError -> errorTrailingIconColor
            focused -> focusedTrailingIconColor
            else -> unfocusedTrailingIconColor
        }

    /**
     * Represents the color used for the border indicator of this text field.
     *
     * @param enabled whether the text field is enabled
     * @param isError whether the text field's current value is in error
     * @param focused whether the text field is in focus
     */
    @Stable
    internal fun indicatorColor(
        enabled: Boolean,
        isError: Boolean,
        focused: Boolean,
    ): Color =
        when {
            !enabled -> disabledIndicatorColor
            isError -> errorIndicatorColor
            focused -> focusedIndicatorColor
            else -> unfocusedIndicatorColor
        }

    /**
     * Represents the container color for this text field.
     *
     * @param enabled whether the text field is enabled
     * @param isError whether the text field's current value is in error
     * @param focused whether the text field is in focus
     */
    @Stable
    internal fun containerColor(
        enabled: Boolean,
        isError: Boolean,
        focused: Boolean,
    ): Color =
        when {
            !enabled -> disabledContainerColor
            isError -> errorContainerColor
            focused -> focusedContainerColor
            else -> unfocusedContainerColor
        }

    /**
     * Represents the color used for the placeholder of this text field.
     *
     * @param enabled whether the text field is enabled
     * @param isError whether the text field's current value is in error
     * @param focused whether the text field is in focus
     */
    @Stable
    internal fun placeholderColor(
        enabled: Boolean,
        isError: Boolean,
        focused: Boolean,
    ): Color =
        when {
            !enabled -> disabledPlaceholderColor
            isError -> errorPlaceholderColor
            focused -> focusedPlaceholderColor
            else -> unfocusedPlaceholderColor
        }

    /**
     * Represents the color used for the label of this text field.
     *
     * @param enabled whether the text field is enabled
     * @param isError whether the text field's current value is in error
     * @param focused whether the text field is in focus
     */
    @Stable
    internal fun labelColor(
        enabled: Boolean,
        isError: Boolean,
        focused: Boolean,
    ): Color =
        when {
            !enabled -> disabledLabelColor
            isError -> errorLabelColor
            focused -> focusedLabelColor
            else -> unfocusedLabelColor
        }

    /**
     * Represents the color used for the input field of this text field.
     *
     * @param enabled whether the text field is enabled
     * @param isError whether the text field's current value is in error
     * @param focused whether the text field is in focus
     */
    @Stable
    internal fun textColor(
        enabled: Boolean,
        isError: Boolean,
        focused: Boolean,
    ): Color =
        when {
            !enabled -> disabledTextColor
            isError -> errorTextColor
            focused -> focusedTextColor
            else -> unfocusedTextColor
        }

    /**
     * Represents the colors used for the supporting text of this text field.
     *
     * @param enabled whether the text field is enabled
     * @param isError whether the text field's current value is in error
     * @param focused whether the text field is in focus
     */
    @Stable
    internal fun supportingTextColor(
        enabled: Boolean,
        isError: Boolean,
        focused: Boolean,
    ): Color =
        when {
            !enabled -> disabledSupportingTextColor
            isError -> errorSupportingTextColor
            focused -> focusedSupportingTextColor
            else -> unfocusedSupportingTextColor
        }

    /**
     * Represents the color used for the prefix of this text field.
     *
     * @param enabled whether the text field is enabled
     * @param isError whether the text field's current value is in error
     * @param focused whether the text field is in focus
     */
    @Stable
    internal fun prefixColor(
        enabled: Boolean,
        isError: Boolean,
        focused: Boolean,
    ): Color =
        when {
            !enabled -> disabledPrefixColor
            isError -> errorPrefixColor
            focused -> focusedPrefixColor
            else -> unfocusedPrefixColor
        }

    /**
     * Represents the color used for the suffix of this text field.
     *
     * @param enabled whether the text field is enabled
     * @param isError whether the text field's current value is in error
     * @param focused whether the text field is in focus
     */
    @Stable
    internal fun suffixColor(
        enabled: Boolean,
        isError: Boolean,
        focused: Boolean,
    ): Color =
        when {
            !enabled -> disabledSuffixColor
            isError -> errorSuffixColor
            focused -> focusedSuffixColor
            else -> unfocusedSuffixColor
        }

    /**
     * Represents the color used for the cursor of this text field.
     *
     * @param isError whether the text field's current value is in error
     */
    @Stable
    internal fun cursorColor(isError: Boolean): Color =
        if (isError) errorCursorColor else cursorColor

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is UnderlinedTextFieldColors) return false

        if (focusedTextColor != other.focusedTextColor) return false
        if (unfocusedTextColor != other.unfocusedTextColor) return false
        if (disabledTextColor != other.disabledTextColor) return false
        if (errorTextColor != other.errorTextColor) return false
        if (focusedContainerColor != other.focusedContainerColor) return false
        if (unfocusedContainerColor != other.unfocusedContainerColor) return false
        if (disabledContainerColor != other.disabledContainerColor) return false
        if (errorContainerColor != other.errorContainerColor) return false
        if (cursorColor != other.cursorColor) return false
        if (errorCursorColor != other.errorCursorColor) return false
        if (textSelectionColors != other.textSelectionColors) return false
        if (focusedIndicatorColor != other.focusedIndicatorColor) return false
        if (unfocusedIndicatorColor != other.unfocusedIndicatorColor) return false
        if (disabledIndicatorColor != other.disabledIndicatorColor) return false
        if (errorIndicatorColor != other.errorIndicatorColor) return false
        if (focusedLeadingIconColor != other.focusedLeadingIconColor) return false
        if (unfocusedLeadingIconColor != other.unfocusedLeadingIconColor) return false
        if (disabledLeadingIconColor != other.disabledLeadingIconColor) return false
        if (errorLeadingIconColor != other.errorLeadingIconColor) return false
        if (focusedTrailingIconColor != other.focusedTrailingIconColor) return false
        if (unfocusedTrailingIconColor != other.unfocusedTrailingIconColor) return false
        if (disabledTrailingIconColor != other.disabledTrailingIconColor) return false
        if (errorTrailingIconColor != other.errorTrailingIconColor) return false
        if (focusedLabelColor != other.focusedLabelColor) return false
        if (unfocusedLabelColor != other.unfocusedLabelColor) return false
        if (disabledLabelColor != other.disabledLabelColor) return false
        if (errorLabelColor != other.errorLabelColor) return false
        if (focusedPlaceholderColor != other.focusedPlaceholderColor) return false
        if (unfocusedPlaceholderColor != other.unfocusedPlaceholderColor) return false
        if (disabledPlaceholderColor != other.disabledPlaceholderColor) return false
        if (errorPlaceholderColor != other.errorPlaceholderColor) return false
        if (focusedSupportingTextColor != other.focusedSupportingTextColor) return false
        if (unfocusedSupportingTextColor != other.unfocusedSupportingTextColor) return false
        if (disabledSupportingTextColor != other.disabledSupportingTextColor) return false
        if (errorSupportingTextColor != other.errorSupportingTextColor) return false
        if (focusedPrefixColor != other.focusedPrefixColor) return false
        if (unfocusedPrefixColor != other.unfocusedPrefixColor) return false
        if (disabledPrefixColor != other.disabledPrefixColor) return false
        if (errorPrefixColor != other.errorPrefixColor) return false
        if (focusedSuffixColor != other.focusedSuffixColor) return false
        if (unfocusedSuffixColor != other.unfocusedSuffixColor) return false
        if (disabledSuffixColor != other.disabledSuffixColor) return false
        if (errorSuffixColor != other.errorSuffixColor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = focusedTextColor.hashCode()
        result = 31 * result + unfocusedTextColor.hashCode()
        result = 31 * result + disabledTextColor.hashCode()
        result = 31 * result + errorTextColor.hashCode()
        result = 31 * result + focusedContainerColor.hashCode()
        result = 31 * result + unfocusedContainerColor.hashCode()
        result = 31 * result + disabledContainerColor.hashCode()
        result = 31 * result + errorContainerColor.hashCode()
        result = 31 * result + cursorColor.hashCode()
        result = 31 * result + errorCursorColor.hashCode()
        result = 31 * result + textSelectionColors.hashCode()
        result = 31 * result + focusedIndicatorColor.hashCode()
        result = 31 * result + unfocusedIndicatorColor.hashCode()
        result = 31 * result + disabledIndicatorColor.hashCode()
        result = 31 * result + errorIndicatorColor.hashCode()
        result = 31 * result + focusedLeadingIconColor.hashCode()
        result = 31 * result + unfocusedLeadingIconColor.hashCode()
        result = 31 * result + disabledLeadingIconColor.hashCode()
        result = 31 * result + errorLeadingIconColor.hashCode()
        result = 31 * result + focusedTrailingIconColor.hashCode()
        result = 31 * result + unfocusedTrailingIconColor.hashCode()
        result = 31 * result + disabledTrailingIconColor.hashCode()
        result = 31 * result + errorTrailingIconColor.hashCode()
        result = 31 * result + focusedLabelColor.hashCode()
        result = 31 * result + unfocusedLabelColor.hashCode()
        result = 31 * result + disabledLabelColor.hashCode()
        result = 31 * result + errorLabelColor.hashCode()
        result = 31 * result + focusedPlaceholderColor.hashCode()
        result = 31 * result + unfocusedPlaceholderColor.hashCode()
        result = 31 * result + disabledPlaceholderColor.hashCode()
        result = 31 * result + errorPlaceholderColor.hashCode()
        result = 31 * result + focusedSupportingTextColor.hashCode()
        result = 31 * result + unfocusedSupportingTextColor.hashCode()
        result = 31 * result + disabledSupportingTextColor.hashCode()
        result = 31 * result + errorSupportingTextColor.hashCode()
        result = 31 * result + focusedPrefixColor.hashCode()
        result = 31 * result + unfocusedPrefixColor.hashCode()
        result = 31 * result + disabledPrefixColor.hashCode()
        result = 31 * result + errorPrefixColor.hashCode()
        result = 31 * result + focusedSuffixColor.hashCode()
        result = 31 * result + unfocusedSuffixColor.hashCode()
        result = 31 * result + disabledSuffixColor.hashCode()
        result = 31 * result + errorSuffixColor.hashCode()
        return result
    }
}