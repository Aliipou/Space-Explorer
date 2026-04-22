package com.example.spaceexplorer.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RegisterScreen(
    vm: AuthViewModel,
    onNavigateLogin: () -> Unit
) {
    val state by vm.uiState.collectAsState()
    val focus = LocalFocusManager.current
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var showPass by remember { mutableStateOf(false) }

    val passwordsMatch = confirm.isEmpty() || password == confirm
    val isValid = email.isNotBlank() && password.length >= 8 && password == confirm

    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFF0A0A1A)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Create Account", color = Color.White, style = MaterialTheme.typography.headlineMedium)
            Text("Join Space Explorer", color = Color.White.copy(alpha = 0.5f), fontSize = 14.sp)

            Spacer(Modifier.height(4.dp))

            OutlinedTextField(
                value = name, onValueChange = { name = it },
                label = { Text("Display Name (optional)") },
                leadingIcon = { Icon(Icons.Default.Person, null) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focus.moveFocus(FocusDirection.Down) }),
                colors = authFieldColors(), modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = email, onValueChange = { email = it },
                label = { Text("Email") },
                leadingIcon = { Icon(Icons.Default.Email, null) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focus.moveFocus(FocusDirection.Down) }),
                colors = authFieldColors(), modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = password, onValueChange = { password = it },
                label = { Text("Password (min 8 chars)") },
                leadingIcon = { Icon(Icons.Default.Lock, null) },
                trailingIcon = {
                    IconButton(onClick = { showPass = !showPass }) {
                        Icon(if (showPass) Icons.Default.VisibilityOff else Icons.Default.Visibility, null)
                    }
                },
                visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focus.moveFocus(FocusDirection.Down) }),
                colors = authFieldColors(), modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = confirm, onValueChange = { confirm = it },
                label = { Text("Confirm Password") },
                leadingIcon = { Icon(Icons.Default.LockOpen, null) },
                visualTransformation = PasswordVisualTransformation(),
                isError = !passwordsMatch,
                supportingText = { if (!passwordsMatch) Text("Passwords don't match") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Go),
                keyboardActions = KeyboardActions(onGo = { if (isValid) vm.register(email, password, name.ifBlank { null }) }),
                colors = authFieldColors(), modifier = Modifier.fillMaxWidth()
            )

            AnimatedVisibility(visible = state.error != null, enter = fadeIn()) {
                state.error?.let { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp) }
            }

            Button(
                onClick = { vm.register(email, password, name.ifBlank { null }) },
                enabled = isValid && !state.isLoading,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                if (state.isLoading) CircularProgressIndicator(Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                else Text("Create Account")
            }

            TextButton(onClick = onNavigateLogin) {
                Text("Already have an account? Sign in", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
