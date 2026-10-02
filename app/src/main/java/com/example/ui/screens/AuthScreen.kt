package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.PrimaryIndigoContainer
import com.example.ui.theme.SecondaryTeal

@Composable
fun AuthScreen(
    onLogin: (String, String, (Boolean, String) -> Unit) -> Unit,
    onRegister: (String, String, String, String, String, String, (Boolean, String) -> Unit) -> Unit,
    onQuickLogin: (String) -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Login, 1: Register

    // Login state
    var loginEmail by remember { mutableStateOf("aarav@campus.edu") }
    var loginPassword by remember { mutableStateOf("password123") }
    var loginError by remember { mutableStateOf("") }

    // Register state
    var regName by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regPhone by remember { mutableStateOf("") }
    var regRole by remember { mutableStateOf("student") }
    var regDept by remember { mutableStateOf("Computer Science") }
    var regError by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Hero Emblem
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(PrimaryIndigoContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = PrimaryIndigo,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Campus Lost & Found",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Centralized College Item Recovery Portal",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Quick Demo Accounts Switcher
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "⚡ Instant 1-Tap Demo Logins:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = loginEmail == "aarav@campus.edu",
                        onClick = {
                            loginEmail = "aarav@campus.edu"
                            loginPassword = "password123"
                            onQuickLogin("student")
                        },
                        label = { Text("🎓 Student", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f).testTag("quick_student")
                    )
                    FilterChip(
                        selected = loginEmail == "priya@campus.edu",
                        onClick = {
                            loginEmail = "priya@campus.edu"
                            loginPassword = "password123"
                            onQuickLogin("staff")
                        },
                        label = { Text("👩‍🏫 Staff", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f).testTag("quick_staff")
                    )
                    FilterChip(
                        selected = loginEmail == "admin@campus.edu",
                        onClick = {
                            loginEmail = "admin@campus.edu"
                            loginPassword = "admin123"
                            onQuickLogin("admin")
                        },
                        label = { Text("🛡️ Admin", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f).testTag("quick_admin")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Tabs: Login / Register
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            modifier = Modifier.clip(RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Log In", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("tab_login")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Register", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("tab_register")
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (selectedTab == 0) {
            // LOGIN FORM
            if (loginError.isNotEmpty()) {
                Text(
                    text = loginError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            OutlinedTextField(
                value = loginEmail,
                onValueChange = { loginEmail = it },
                label = { Text("College Email") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                modifier = Modifier.fillMaxWidth().testTag("login_email"),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = loginPassword,
                onValueChange = { loginPassword = it },
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                modifier = Modifier.fillMaxWidth().testTag("login_password"),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (loginEmail.isBlank() || loginPassword.isBlank()) {
                        loginError = "Please enter both email and password."
                    } else {
                        loginError = ""
                        onLogin(loginEmail, loginPassword) { success, msg ->
                            if (!success) {
                                loginError = msg
                            } else {
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("login_button")
            ) {
                Text("Sign In to Portal", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        } else {
            // REGISTER FORM
            if (regError.isNotEmpty()) {
                Text(
                    text = regError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            OutlinedTextField(
                value = regName,
                onValueChange = { regName = it },
                label = { Text("Full Name") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth().testTag("reg_name"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = regEmail,
                onValueChange = { regEmail = it },
                label = { Text("College Email (e.g. name@campus.edu)") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                modifier = Modifier.fillMaxWidth().testTag("reg_email"),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = regPassword,
                onValueChange = { regPassword = it },
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                modifier = Modifier.fillMaxWidth().testTag("reg_password"),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = regPhone,
                onValueChange = { regPhone = it },
                label = { Text("Phone Number") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                modifier = Modifier.fillMaxWidth().testTag("reg_phone"),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = regDept,
                onValueChange = { regDept = it },
                label = { Text("Department / Class (e.g. TY BCS)") },
                leadingIcon = { Icon(Icons.Default.School, contentDescription = null) },
                modifier = Modifier.fillMaxWidth().testTag("reg_dept"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Role selection
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = regRole == "student",
                    onClick = { regRole = "student" },
                    label = { Text("Student") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = regRole == "staff",
                    onClick = { regRole = "staff" },
                    label = { Text("Staff / Faculty") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (regName.isBlank() || regEmail.isBlank() || regPassword.isBlank() || regPhone.isBlank()) {
                        regError = "Please fill all required fields."
                    } else if (!regEmail.contains("@")) {
                        regError = "Please enter a valid email address."
                    } else {
                        regError = ""
                        onRegister(regName, regEmail, regPassword, regPhone, regRole, regDept) { success, msg ->
                            if (!success) {
                                regError = msg
                            } else {
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("register_button")
            ) {
                Text("Create Account", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
