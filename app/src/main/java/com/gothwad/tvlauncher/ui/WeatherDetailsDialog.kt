package com.gothwad.tvlauncher.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.gothwad.tvlauncher.R
import com.gothwad.tvlauncher.data.WeatherData

@Composable
fun WeatherDetailsDialog(
    weather: WeatherData,
    onRefresh: () -> Unit,
    onDismiss: () -> Unit,
) {
    var hasLocationPermission by remember { mutableStateOf(weather.hasLocationPermission) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasLocationPermission = granted
        if (granted) onRefresh()
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .width(420.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF14161C))
                .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        stringResource(R.string.cd_weather),
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Surface(
                        onClick = onDismiss,
                        shape = ClickableSurfaceDefaults.shape(CircleShape),
                        colors = ClickableSurfaceDefaults.colors(
                            containerColor = Color.White.copy(alpha = 0.08f),
                            focusedContainerColor = Color.White.copy(alpha = 0.25f),
                        ),
                    ) {
                        Box(Modifier.size(36.dp), contentAlignment = Alignment.Center) {
                            Icon(AppIcons.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Weather Icon & Temp
                val weatherIcon = when (weather.condition.lowercase()) {
                    "rain", "showers", "thunderstorm" -> AppIcons.Rain
                    "partly cloudy", "foggy" -> AppIcons.Cloud
                    else -> AppIcons.Sun
                }
                Icon(
                    weatherIcon,
                    contentDescription = null,
                    tint = if (weather.condition.lowercase() == "clear") Color(0xFFFFD54F) else Color(0xFF90CAF9),
                    modifier = Modifier.size(64.dp)
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = weather.temp,
                    style = MaterialTheme.typography.displaySmall.copy(fontSize = 44.sp, fontWeight = FontWeight.Bold),
                    color = Color.White,
                )

                Text(
                    text = weather.condition,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.75f),
                )

                Text(
                    text = weather.city,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(Modifier.height(24.dp))

                // Permission or Refresh action
                if (!hasLocationPermission) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF20232B))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                stringResource(R.string.permission_location_title),
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(
                                stringResource(R.string.permission_location_desc),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.6f),
                                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                            )
                            Button(
                                onClick = {
                                    locationPermissionLauncher.launch(android.Manifest.permission.ACCESS_COARSE_LOCATION)
                                },
                                shape = ButtonDefaults.shape(RoundedCornerShape(8.dp)),
                                colors = ButtonDefaults.colors(
                                    containerColor = Color(0xFF4C8DF6),
                                    contentColor = Color.White,
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Enable Auto Weather Location")
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }

                Button(
                    onClick = {
                        onRefresh()
                        onDismiss()
                    },
                    shape = ButtonDefaults.shape(RoundedCornerShape(10.dp)),
                    colors = ButtonDefaults.colors(
                        containerColor = Color.White.copy(alpha = 0.12f),
                        contentColor = Color.White,
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Refresh Forecast")
                }
            }
        }
    }
}
