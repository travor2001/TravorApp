package com.example.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.UserRole
import com.example.ui.theme.*

@Composable
fun AuthScreen(
    onLoginSuccess: (UserRole) -> Unit,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableIntStateOf(1) } // 1: Phone input, 2: OTP verification, 3: Profile/Role pick
    var phoneInput by remember { mutableStateOf("923 456 789") }
    var otpInput by remember { mutableStateOf("4821") }
    var nameInput by remember { mutableStateOf("Carlos Baptista") }
    var selectedRole by remember { mutableStateOf(UserRole.PASSENGER) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Hero Banner image or Logo
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = DarkSurfaceElevated,
                border = BorderStroke(1.dp, DarkOutline),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Box {
                    Image(
                        painter = painterResource(id = R.drawable.moto_go_hero),
                        contentDescription = "MOTO GO Angola",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Dark Gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                androidx.compose.ui.graphics.Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, DarkBackground.copy(alpha = 0.9f))
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "MOTO GO",
                                color = MotoGoldPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 24.sp,
                                letterSpacing = 2.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MotoGoldPrimary
                            ) {
                                Text(
                                    text = "ANGOLA",
                                    color = OnMotoGold,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Chegue rápido. Vá seguro.",
                            color = DarkTextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                shape = RoundedCornerShape(24.dp),
                color = DarkSurfaceElevated,
                border = BorderStroke(1.dp, DarkOutline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    when (step) {
                        1 -> {
                            Text(
                                text = "Entrar com Telemóvel",
                                color = DarkTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Introduza o seu número de Angola para receber o código SMS",
                                color = DarkTextSecondary,
                                fontSize = 12.sp
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = phoneInput,
                                onValueChange = { phoneInput = it },
                                leadingIcon = {
                                    Text(
                                        text = "+244 ",
                                        color = MotoGoldPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(start = 12.dp)
                                    )
                                },
                                placeholder = { Text("9XX XXX XXX") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("auth_phone_field"),
                                shape = RoundedCornerShape(14.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = { step = 2 },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("auth_send_code_button"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MotoGoldPrimary, contentColor = OnMotoGold)
                            ) {
                                Text("ENVIAR CÓDIGO SMS", fontWeight = FontWeight.Bold)
                            }
                        }

                        2 -> {
                            Text(
                                text = "Código de Verificação OTP",
                                color = DarkTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Código enviado para +244 $phoneInput",
                                color = DarkTextSecondary,
                                fontSize = 12.sp
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = otpInput,
                                onValueChange = { otpInput = it },
                                label = { Text("Código de 4 dígitos") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("auth_otp_field"),
                                shape = RoundedCornerShape(14.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = { step = 3 },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("auth_verify_otp_button"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MotoGoldPrimary, contentColor = OnMotoGold)
                            ) {
                                Text("VERIFICAR CÓDIGO", fontWeight = FontWeight.Bold)
                            }
                        }

                        3 -> {
                            Text(
                                text = "Escolha o seu Perfil",
                                color = DarkTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Selecione o modo de utilização para continuar",
                                color = DarkTextSecondary,
                                fontSize = 12.sp
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Roles selector: Passenger, Driver, Admin
                            val roleOptions = listOf(
                                Triple(UserRole.PASSENGER, "Passageiro", "Solicitar moto-táxi em Luanda"),
                                Triple(UserRole.DRIVER, "Moto-Taxista", "Ficar online e receber viagens"),
                                Triple(UserRole.ADMIN, "Administrador", "Painel de gestão, frota e tarifas")
                            )

                            roleOptions.forEach { (role, label, desc) ->
                                val isSelected = selectedRole == role
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (isSelected) MotoGoldContainer else DarkSurfaceVariant,
                                    border = BorderStroke(1.dp, if (isSelected) MotoGoldPrimary else DarkOutline),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable { selectedRole = role }
                                        .testTag("role_select_${role.name}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = when (role) {
                                                UserRole.PASSENGER -> Icons.Default.Person
                                                UserRole.DRIVER -> Icons.Default.TwoWheeler
                                                UserRole.ADMIN -> Icons.Default.AdminPanelSettings
                                            },
                                            contentDescription = null,
                                            tint = if (isSelected) MotoGoldPrimary else DarkTextSecondary
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = label,
                                                color = if (isSelected) MotoGoldPrimary else DarkTextPrimary,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                            Text(text = desc, color = DarkTextSecondary, fontSize = 11.sp)
                                        }
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = { selectedRole = role },
                                            colors = RadioButtonDefaults.colors(selectedColor = MotoGoldPrimary)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = { onLoginSuccess(selectedRole) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("auth_enter_app_button"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MotoGoldPrimary, contentColor = OnMotoGold)
                            ) {
                                Text("ENTRAR NO MOTO GO", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
