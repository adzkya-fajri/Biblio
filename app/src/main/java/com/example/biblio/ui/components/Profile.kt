package com.example.biblio.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import com.example.biblio.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.biblio.fraunces
import com.example.biblio.ibmplexsans
import com.example.biblio.viewmodel.UserViewModel

@Composable
fun Profile(
    name: String,
    navController: NavController,
    backgroundColor: Color = colorResource(id = R.color.colorPrimaryVariant),
    textColor: Color = Color.White,
    size: Dp = 48.dp,
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel? = null,
    onProfileClick: () -> Unit = {}
) {
        val userProfile by userViewModel?.userProfile?.collectAsState() ?: return

        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Hi, ${userProfile.name}! 👋",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = fraunces
                )
                Text(
                    text = "Mau baca apa hari ini?",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            // FOTO PROFIL (CLICKABLE)
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1976D2))
                    .border(2.dp, Color.White, CircleShape)
                    .clickable { onProfileClick() },
                contentAlignment = Alignment.Center
            ) {
                if (userProfile.photoUrl.isNotEmpty()) {
                    AsyncImage(
                        model = userProfile.photoUrl,
                        contentDescription = "Profile Photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Initial nama jika tidak ada foto
                    Text(
                        text = userProfile.name.first().uppercase(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor, shape = CircleShape)
            .clickable { navController?.navigate("settings") },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name.firstOrNull()?.uppercase() ?: "?",
            color = textColor,
            fontFamily = ibmplexsans,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}