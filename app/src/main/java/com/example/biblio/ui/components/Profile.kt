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
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.biblio.fraunces
import com.example.biblio.ibmplexsans

@Composable
fun Profile(
    name: String,
    backgroundColor: Color = colorResource(id = R.color.colorPrimaryVariant),
    textColor: Color = Color.White,
    size: Dp = 48.dp,
    fontSize: TextUnit = 20.sp,
    modifier: Modifier = Modifier,
    navController: NavController? = null,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor, shape = CircleShape)
            .clickable { navController?.navigate("profile") },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name.firstOrNull()?.uppercase() ?: "?",
            color = textColor,
            fontFamily = ibmplexsans,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold
        )
    }
}