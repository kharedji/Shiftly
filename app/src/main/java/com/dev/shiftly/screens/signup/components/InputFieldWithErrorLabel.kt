package com.dev.shiftly.screens.signup.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dev.shiftly.R

@Composable
fun InputFieldWithErrorLabel(
    modifier: Modifier,
    input: String,
    onInputChange: (String) -> Unit,
    focusManager: FocusManager,
    onUnfocusedState: (FocusState) -> Unit,
    inputState: InputState,
    keyboardType: KeyboardType,
    imeAction: ImeAction,
    labelId: Int,
    leadingIcon: ImageVector,
    contentDescriptionId: Int,
    errorMessageId: Int,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    TextField(
        modifier = modifier.onFocusChanged {
            if (!it.hasFocus && input.isNotEmpty()) onUnfocusedState(it)
        },
        value = input,
        onValueChange = {
            onInputChange(it)
        },
        isError = when (inputState) {
            InputState.INITIAL, InputState.VALID -> false
            InputState.INVALID -> true
        },
        label = { Text(stringResource(id = labelId)) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(
            onDone = { focusManager.clearFocus() }
        ),
        visualTransformation = visualTransformation,
        colors = TextFieldDefaults.colors(),
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = stringResource(id = contentDescriptionId)
            )
        },
        trailingIcon = {
            if (inputState == InputState.INVALID) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = Icons.Default.Info,
                    contentDescription = stringResource(id = errorMessageId),
                    tint = MaterialTheme.colorScheme.error
                )
            }
            if (inputState == InputState.INITIAL && input.isNotEmpty()) Icon(
                modifier = Modifier
                    .clickable {
                        onInputChange("")
                    }
                    .size(20.dp),
                imageVector = Icons.Default.Clear,
                contentDescription = stringResource(id = R.string.clear)
            )
        }
    )
    // Display the error message below the input field if the input is invalid
    if (inputState == InputState.INVALID) Text(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(start = 12.dp, top = 4.dp, bottom = 6.dp, end = 6.dp),
        text = stringResource(id = errorMessageId),
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.error
    )
}

enum class InputState {
    INITIAL, VALID, INVALID
}