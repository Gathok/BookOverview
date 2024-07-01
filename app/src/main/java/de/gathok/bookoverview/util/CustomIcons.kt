package de.gathok.bookoverview.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp


@Composable
fun customIconFlashlightOff(): ImageVector {
    return remember {
        ImageVector.Builder(
            name = "flashlight_off",
            defaultWidth = 40.0.dp,
            defaultHeight = 40.0.dp,
            viewportWidth = 40.0f,
            viewportHeight = 40.0f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(33.875f, 34.958f)
                quadToRelative(-0.375f, 0.417f, -0.917f, 0.417f)
                quadToRelative(-0.541f, 0f, -0.958f, -0.417f)
                lineToRelative(-5.542f, -5.541f)
                verticalLineToRelative(4.333f)
                quadToRelative(0f, 1.083f, -0.77f, 1.854f)
                quadToRelative(-0.771f, 0.771f, -1.855f, 0.771f)
                horizontalLineToRelative(-7.166f)
                quadToRelative(-1.125f, 0f, -1.875f, -0.771f)
                reflectiveQuadToRelative(-0.75f, -1.854f)
                verticalLineTo(17f)
                lineTo(3.833f, 6.792f)
                quadToRelative(-0.416f, -0.417f, -0.416f, -0.938f)
                quadToRelative(0f, -0.521f, 0.416f, -0.896f)
                quadToRelative(0.375f, -0.416f, 0.917f, -0.416f)
                reflectiveQuadToRelative(0.917f, 0.416f)
                lineToRelative(28.208f, 28.167f)
                quadToRelative(0.375f, 0.375f, 0.375f, 0.917f)
                quadToRelative(0f, 0.541f, -0.375f, 0.916f)
                close()
                moveTo(16.667f, 19.625f)
                verticalLineTo(33.75f)
                horizontalLineToRelative(7.166f)
                verticalLineToRelative(-6.958f)
                close()
                moveToRelative(9.791f, 2.333f)
                lineToRelative(-2.625f, -2.666f)
                verticalLineTo(17.5f)
                lineToRelative(3.584f, -5.25f)
                verticalLineTo(11f)
                horizontalLineTo(15.5f)
                lineToRelative(-2.625f, -2.625f)
                horizontalLineToRelative(14.542f)
                verticalLineTo(6.208f)
                horizontalLineTo(13.125f)
                verticalLineToRelative(2.375f)
                lineTo(10.5f, 5.917f)
                quadToRelative(0.083f, -0.959f, 0.833f, -1.646f)
                quadToRelative(0.75f, -0.688f, 1.75f, -0.688f)
                horizontalLineToRelative(14.334f)
                quadToRelative(1.083f, 0f, 1.854f, 0.771f)
                quadToRelative(0.771f, 0.771f, 0.771f, 1.854f)
                verticalLineToRelative(6.75f)
                lineToRelative(-3.584f, 5.25f)
                close()
                moveToRelative(-6.208f, 1.25f)
                close()
                moveToRelative(0f, -7.5f)
                close()
            }
        }.build()
    }
}

@Composable
fun customIconFlashlightOn(): ImageVector {
    return remember {
        ImageVector.Builder(
            name = "flashlight_on",
            defaultWidth = 40.0.dp,
            defaultHeight = 40.0.dp,
            viewportWidth = 40.0f,
            viewportHeight = 40.0f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(16.417f, 36.375f)
                quadToRelative(-1.084f, 0f, -1.855f, -0.771f)
                quadToRelative(-0.77f, -0.771f, -0.77f, -1.854f)
                verticalLineTo(18.208f)
                lineToRelative(-3.584f, -5.25f)
                verticalLineTo(6.25f)
                quadToRelative(0f, -1.083f, 0.771f, -1.854f)
                quadToRelative(0.771f, -0.771f, 1.854f, -0.771f)
                horizontalLineToRelative(14.334f)
                quadToRelative(1.083f, 0f, 1.854f, 0.771f)
                quadToRelative(0.771f, 0.771f, 0.771f, 1.854f)
                verticalLineToRelative(6.708f)
                lineToRelative(-3.584f, 5.25f)
                verticalLineTo(33.75f)
                quadToRelative(0f, 1.083f, -0.77f, 1.854f)
                quadToRelative(-0.771f, 0.771f, -1.855f, 0.771f)
                close()
                moveTo(20f, 26.125f)
                quadToRelative(-0.875f, 0f, -1.521f, -0.646f)
                quadToRelative(-0.646f, -0.646f, -0.646f, -1.521f)
                quadToRelative(0f, -0.916f, 0.625f, -1.541f)
                quadToRelative(0.625f, -0.625f, 1.542f, -0.625f)
                quadToRelative(0.875f, 0f, 1.521f, 0.625f)
                quadToRelative(0.646f, 0.625f, 0.646f, 1.5f)
                quadToRelative(0f, 0.916f, -0.625f, 1.562f)
                reflectiveQuadTo(20f, 26.125f)
                close()
                moveToRelative(-7.167f, -17.75f)
                horizontalLineToRelative(14.334f)
                verticalLineTo(6.25f)
                horizontalLineTo(12.833f)
                close()
                moveToRelative(14.334f, 2.667f)
                horizontalLineTo(12.833f)
                verticalLineToRelative(1.25f)
                lineToRelative(3.584f, 5.25f)
                verticalLineTo(33.75f)
                horizontalLineToRelative(7.166f)
                verticalLineTo(17.542f)
                lineToRelative(3.584f, -5.25f)
                close()
                moveTo(20f, 20.375f)
                close()
            }
        }.build()
    }
}

@Composable
fun customIconFilterList(): ImageVector {
    return remember {
        ImageVector.Builder(
            name = "filter_list",
            defaultWidth = 40.0.dp,
            defaultHeight = 40.0.dp,
            viewportWidth = 40.0f,
            viewportHeight = 40.0f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(18.208f, 28.875f)
                quadToRelative(-0.291f, 0f, -0.479f, -0.187f)
                quadToRelative(-0.187f, -0.188f, -0.187f, -0.48f)
                quadToRelative(0f, -0.291f, 0.187f, -0.5f)
                quadToRelative(0.188f, -0.208f, 0.479f, -0.208f)
                horizontalLineToRelative(3.584f)
                quadToRelative(0.291f, 0f, 0.479f, 0.208f)
                quadToRelative(0.187f, 0.209f, 0.187f, 0.5f)
                quadToRelative(0f, 0.292f, -0.187f, 0.48f)
                quadToRelative(-0.188f, 0.187f, -0.479f, 0.187f)
                close()
                moveTo(6.625f, 10.958f)
                quadToRelative(-0.333f, 0f, -0.521f, -0.187f)
                quadToRelative(-0.187f, -0.188f, -0.187f, -0.479f)
                quadToRelative(0f, -0.292f, 0.187f, -0.5f)
                quadToRelative(0.188f, -0.209f, 0.521f, -0.209f)
                horizontalLineToRelative(26.792f)
                quadToRelative(0.291f, 0f, 0.479f, 0.209f)
                quadToRelative(0.187f, 0.208f, 0.187f, 0.5f)
                quadToRelative(0f, 0.291f, -0.187f, 0.479f)
                quadToRelative(-0.188f, 0.187f, -0.479f, 0.187f)
                close()
                moveToRelative(4.958f, 8.959f)
                quadToRelative(-0.291f, 0f, -0.5f, -0.188f)
                quadToRelative(-0.208f, -0.187f, -0.208f, -0.521f)
                quadToRelative(0f, -0.25f, 0.208f, -0.437f)
                quadToRelative(0.209f, -0.188f, 0.5f, -0.188f)
                horizontalLineToRelative(16.834f)
                quadToRelative(0.291f, 0f, 0.479f, 0.188f)
                quadToRelative(0.187f, 0.187f, 0.187f, 0.479f)
                reflectiveQuadToRelative(-0.187f, 0.479f)
                quadToRelative(-0.188f, 0.188f, -0.479f, 0.188f)
                close()
            }
        }.build()
    }
}

@Composable
fun customIconBarcodeScanner(): ImageVector {
    return remember {
        ImageVector.Builder(
            name = "barcode_scanner",
            defaultWidth = 40.0.dp,
            defaultHeight = 40.0.dp,
            viewportWidth = 40.0f,
            viewportHeight = 40.0f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(2.25f, 5.542f)
                horizontalLineToRelative(7.042f)
                verticalLineToRelative(2f)
                horizontalLineTo(4.25f)
                verticalLineToRelative(5f)
                horizontalLineToRelative(-2f)
                close()
                moveToRelative(28.458f, 0f)
                horizontalLineToRelative(7.042f)
                verticalLineToRelative(7f)
                horizontalLineToRelative(-2f)
                verticalLineToRelative(-5f)
                horizontalLineToRelative(-5.042f)
                close()
                moveToRelative(5.042f, 26.875f)
                verticalLineToRelative(-5f)
                horizontalLineToRelative(2f)
                verticalLineToRelative(7f)
                horizontalLineToRelative(-7.042f)
                verticalLineToRelative(-2f)
                close()
                moveToRelative(-31.5f, -5f)
                verticalLineToRelative(5f)
                horizontalLineToRelative(5.042f)
                verticalLineToRelative(2f)
                horizontalLineTo(2.25f)
                verticalLineToRelative(-7f)
                close()
                moveToRelative(7.375f, -17.542f)
                horizontalLineToRelative(1.708f)
                verticalLineToRelative(20.208f)
                horizontalLineToRelative(-1.708f)
                close()
                moveToRelative(-5f, 0f)
                horizontalLineToRelative(3.333f)
                verticalLineToRelative(20.208f)
                horizontalLineTo(6.625f)
                close()
                moveToRelative(10f, 0f)
                horizontalLineTo(20f)
                verticalLineToRelative(20.208f)
                horizontalLineToRelative(-3.375f)
                close()
                moveToRelative(11.75f, 0f)
                horizontalLineToRelative(1.708f)
                verticalLineToRelative(20.208f)
                horizontalLineToRelative(-1.708f)
                close()
                moveToRelative(3.375f, 0f)
                horizontalLineToRelative(1.625f)
                verticalLineToRelative(20.208f)
                horizontalLineTo(31.75f)
                close()
                moveToRelative(-10.083f, 0f)
                horizontalLineToRelative(5.041f)
                verticalLineToRelative(20.208f)
                horizontalLineToRelative(-5.041f)
                close()
            }
        }.build()
    }
}

@Composable
fun customIconBook(): ImageVector {
    return remember {
        ImageVector.Builder(
            name = "book",
            defaultWidth = 40.0.dp,
            defaultHeight = 40.0.dp,
            viewportWidth = 40.0f,
            viewportHeight = 40.0f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(10.167f, 35.625f)
                quadToRelative(-1f, 0f, -1.75f, -0.729f)
                reflectiveQuadToRelative(-0.75f, -1.771f)
                verticalLineTo(6.875f)
                quadToRelative(0f, -1.042f, 0.75f, -1.771f)
                quadToRelative(0.75f, -0.729f, 1.75f, -0.729f)
                horizontalLineToRelative(19.666f)
                quadToRelative(1f, 0f, 1.75f, 0.729f)
                reflectiveQuadToRelative(0.75f, 1.771f)
                verticalLineToRelative(26.25f)
                quadToRelative(0f, 1.042f, -0.75f, 1.771f)
                quadToRelative(-0.75f, 0.729f, -1.75f, 0.729f)
                close()
                moveToRelative(0f, -2f)
                horizontalLineToRelative(19.666f)
                quadToRelative(0.167f, 0f, 0.334f, -0.146f)
                quadToRelative(0.166f, -0.146f, 0.166f, -0.354f)
                verticalLineTo(6.875f)
                quadToRelative(0f, -0.208f, -0.166f, -0.354f)
                quadToRelative(-0.167f, -0.146f, -0.334f, -0.146f)
                horizontalLineTo(27.5f)
                verticalLineToRelative(9.292f)
                quadToRelative(0f, 0.416f, -0.312f, 0.604f)
                quadToRelative(-0.313f, 0.187f, -0.646f, -0.021f)
                lineToRelative(-2.667f, -1.542f)
                lineToRelative(-2.667f, 1.542f)
                quadToRelative(-0.333f, 0.208f, -0.646f, 0.021f)
                quadToRelative(-0.312f, -0.188f, -0.312f, -0.604f)
                verticalLineTo(6.375f)
                horizontalLineTo(10.167f)
                quadToRelative(-0.167f, 0f, -0.334f, 0.146f)
                quadToRelative(-0.166f, 0.146f, -0.166f, 0.354f)
                verticalLineToRelative(26.25f)
                quadToRelative(0f, 0.208f, 0.166f, 0.354f)
                quadToRelative(0.167f, 0.146f, 0.334f, 0.146f)
                close()
                moveTo(20.25f, 6.375f)
                horizontalLineToRelative(7.25f)
                close()
                moveToRelative(-10.083f, 0f)
                horizontalLineToRelative(-0.5f)
                horizontalLineTo(30.333f)
                horizontalLineToRelative(-0.5f)
                close()
            }
        }.build()
    }
}
