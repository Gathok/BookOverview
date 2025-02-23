package de.gathok.bookoverview.ui

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
fun customIconTrash(): ImageVector {
    return remember {
        ImageVector.Builder(
            name = "delete",
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
                moveTo(11.208f, 34.708f)
                quadToRelative(-1.041f, 0f, -1.833f, -0.77f)
                quadToRelative(-0.792f, -0.771f, -0.792f, -1.855f)
                verticalLineTo(9.25f)
                horizontalLineTo(8.25f)
                quadToRelative(-0.583f, 0f, -0.958f, -0.396f)
                reflectiveQuadToRelative(-0.375f, -0.937f)
                quadToRelative(0f, -0.542f, 0.375f, -0.917f)
                reflectiveQuadToRelative(0.958f, -0.375f)
                horizontalLineToRelative(6.458f)
                quadToRelative(0f, -0.583f, 0.375f, -0.958f)
                reflectiveQuadToRelative(0.959f, -0.375f)
                horizontalLineTo(24f)
                quadToRelative(0.583f, 0f, 0.938f, 0.375f)
                quadToRelative(0.354f, 0.375f, 0.354f, 0.958f)
                horizontalLineToRelative(6.5f)
                quadToRelative(0.541f, 0f, 0.937f, 0.375f)
                reflectiveQuadToRelative(0.396f, 0.917f)
                quadToRelative(0f, 0.583f, -0.396f, 0.958f)
                reflectiveQuadToRelative(-0.937f, 0.375f)
                horizontalLineToRelative(-0.334f)
                verticalLineToRelative(22.833f)
                quadToRelative(0f, 1.084f, -0.791f, 1.855f)
                quadToRelative(-0.792f, 0.77f, -1.875f, 0.77f)
                close()
                moveToRelative(0f, -25.458f)
                verticalLineToRelative(22.833f)
                horizontalLineToRelative(17.584f)
                verticalLineTo(9.25f)
                close()
                moveToRelative(4.125f, 18.042f)
                quadToRelative(0f, 0.583f, 0.396f, 0.958f)
                reflectiveQuadToRelative(0.938f, 0.375f)
                quadToRelative(0.541f, 0f, 0.916f, -0.375f)
                reflectiveQuadToRelative(0.375f, -0.958f)
                verticalLineTo(14f)
                quadToRelative(0f, -0.542f, -0.375f, -0.937f)
                quadToRelative(-0.375f, -0.396f, -0.916f, -0.396f)
                quadToRelative(-0.584f, 0f, -0.959f, 0.396f)
                quadToRelative(-0.375f, 0.395f, -0.375f, 0.937f)
                close()
                moveToRelative(6.709f, 0f)
                quadToRelative(0f, 0.583f, 0.396f, 0.958f)
                quadToRelative(0.395f, 0.375f, 0.937f, 0.375f)
                reflectiveQuadToRelative(0.937f, -0.375f)
                quadToRelative(0.396f, -0.375f, 0.396f, -0.958f)
                verticalLineTo(14f)
                quadToRelative(0f, -0.542f, -0.396f, -0.937f)
                quadToRelative(-0.395f, -0.396f, -0.937f, -0.396f)
                reflectiveQuadToRelative(-0.937f, 0.396f)
                quadToRelative(-0.396f, 0.395f, -0.396f, 0.937f)
                close()
                moveTo(11.208f, 9.25f)
                verticalLineToRelative(22.833f)
                verticalLineTo(9.25f)
                close()
            }
        }.build()
    }
}

@Composable
fun customIconRead(): ImageVector {
    return remember {
        ImageVector.Builder(
            name = "Glasses",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                fillAlpha = 1.0f,
                stroke = SolidColor(Color(0xFF000000)),
                strokeAlpha = 1.0f,
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(10f, 15f)
                arcTo(4f, 4f, 0f, isMoreThanHalf = false, isPositiveArc = true, 6f, 19f)
                arcTo(4f, 4f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2f, 15f)
                arcTo(4f, 4f, 0f, isMoreThanHalf = false, isPositiveArc = true, 10f, 15f)
                close()
            }
            path(
                fill = null,
                fillAlpha = 1.0f,
                stroke = SolidColor(Color(0xFF000000)),
                strokeAlpha = 1.0f,
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(22f, 15f)
                arcTo(4f, 4f, 0f, isMoreThanHalf = false, isPositiveArc = true, 18f, 19f)
                arcTo(4f, 4f, 0f, isMoreThanHalf = false, isPositiveArc = true, 14f, 15f)
                arcTo(4f, 4f, 0f, isMoreThanHalf = false, isPositiveArc = true, 22f, 15f)
                close()
            }
            path(
                fill = null,
                fillAlpha = 1.0f,
                stroke = SolidColor(Color(0xFF000000)),
                strokeAlpha = 1.0f,
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(14f, 15f)
                arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, -2f, -2f)
                arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, -2f, 2f)
            }
            path(
                fill = null,
                fillAlpha = 1.0f,
                stroke = SolidColor(Color(0xFF000000)),
                strokeAlpha = 1.0f,
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(2.5f, 13f)
                lineTo(5f, 7f)
                curveToRelative(0.7f, -1.3f, 1.4f, -2f, 3f, -2f)
            }
            path(
                fill = null,
                fillAlpha = 1.0f,
                stroke = SolidColor(Color(0xFF000000)),
                strokeAlpha = 1.0f,
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(21.5f, 13f)
                lineTo(19f, 7f)
                curveToRelative(-0.7f, -1.3f, -1.5f, -2f, -3f, -2f)
            }
        }.build()
    }
}