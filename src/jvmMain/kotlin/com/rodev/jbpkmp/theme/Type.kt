package com.rodev.jbpkmp.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.unit.sp


val OpenSans = FontFamily(
    Font("fonts/opensans_lightitalic.ttf", FontWeight.Light, FontStyle.Italic),
    Font("fonts/opensans_semibold.ttf", FontWeight.SemiBold, FontStyle.Normal),
    Font("fonts/opensans_semibolditalic.ttf", FontWeight.SemiBold, FontStyle.Italic),
    Font("fonts/opensans_extrabolditalic.ttf", FontWeight.ExtraBold, FontStyle.Italic),
    Font("fonts/opensans_extrabold.ttf", FontWeight.ExtraBold, FontStyle.Normal),
    Font("fonts/opensans_medium.ttf", FontWeight.Medium, FontStyle.Normal),
    Font("fonts/opensans_regular.ttf", FontWeight.Normal, FontStyle.Normal),
    Font("fonts/opensans_light.ttf", FontWeight.Light, FontStyle.Normal),
    Font("fonts/opensans_italic.ttf", FontWeight.Normal, FontStyle.Italic),
    Font("fonts/opensans_bolditalic.ttf", FontWeight.Bold, FontStyle.Italic),
    Font("fonts/opensans_mediumitalic.ttf", FontWeight.Medium, FontStyle.Italic),
    Font("fonts/opensans_bold.ttf", FontWeight.Bold, FontStyle.Normal)
)

val DefaultTextStyle = TextStyle(
    fontFamily = OpenSans,
    fontSize = 14.sp
)

val Typography = Typography(
    h1 = TextStyle(
        fontFamily = OpenSans,
        fontWeight = FontWeight.Bold,
        fontSize = 60.sp,
        letterSpacing = (-1.5).sp
    ),
    h2 = TextStyle(
        fontFamily = OpenSans,
        fontWeight = FontWeight.Bold,
        fontSize = 40.sp,
        letterSpacing = (-0.5).sp
    ),
    h3 = TextStyle(
        fontFamily = OpenSans,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        letterSpacing = 0.sp
    ),
    h4 = TextStyle(
        fontFamily = OpenSans,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        letterSpacing = 0.25.sp
    ),
    h5 = TextStyle(
        fontFamily = OpenSans,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        letterSpacing = 0.sp
    ),
    subtitle1 = TextStyle(
        fontFamily = OpenSans,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        letterSpacing = 0.15.sp
    ),
    subtitle2 = TextStyle(
        fontFamily = OpenSans,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        letterSpacing = 0.1.sp
    ),
    body1 = TextStyle(
        fontFamily = OpenSans,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        letterSpacing = 0.5.sp
    ),
    body2 = TextStyle(
        fontFamily = OpenSans,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        letterSpacing = 0.25.sp
    ),
    button = TextStyle(
        fontFamily = OpenSans,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        letterSpacing = 1.25.sp
    ),
    caption = TextStyle(
        fontFamily = OpenSans,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        letterSpacing = 0.4.sp
    ),
    overline = TextStyle(
        fontFamily = OpenSans,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        letterSpacing = 1.5.sp
    )
)