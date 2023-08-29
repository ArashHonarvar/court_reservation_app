package it.polito.mad.reservationapp.view.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun RatingBarRow(text: String, rating: Float) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.secondary)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            modifier = Modifier.weight(1f).padding(8.dp)
        )
        RatingBar(
            rating = rating,
            modifier = Modifier
                .weight(1f)
                .size(200.dp, 27.dp),
        )
    }
}

@Composable
fun EditableRatingBarRow(
    text: String,
    rating: Float,
    setRating: (Float) -> Unit,
    onCancel: (() -> Unit)? = null,
    onRatingChanged: (Float) -> Unit
) {

    /*val (ratingState, setRatingState) = remember {
        mutableStateOf(rating)
    }*/

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.secondary)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            modifier = Modifier.weight(1f).padding(8.dp)
        )
        EditableRatingBar(
            rating = rating,
            setRatingState = setRating,
            onRatingChanged = onRatingChanged,
            modifier = Modifier.weight(1f).size(200.dp, 27.dp).padding(end= 8.dp)
        )
        if (onCancel != null) {
            Icon(
                Icons.Default.Delete,
                null,
                tint = Color.Red,
                modifier = Modifier.clickable { onCancel() }.padding(8.dp,0.dp,8.dp,0.dp)
            )
        }
    }
}


@Composable
fun RatingBar(
    rating: Float,
    modifier: Modifier = Modifier,
    color: Color = Color.Yellow
) {
    Row(modifier = modifier.wrapContentSize()) {
        (1..5).forEach { step ->
            val stepRating = when {
                rating > step -> 1f
                step.rem(rating) < 1 -> rating - (step - 1f)
                else -> 0f
            }
            RatingStar(stepRating, color)
        }
    }
}

@Composable
fun EditableRatingBar(
    rating: Float,
    setRatingState: (Float) -> Unit,
    onRatingChanged: (Float) -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Color.Yellow
) {
    Row(
        modifier = modifier
            .wrapContentSize()
            .pointerInput(Unit, {}) {
                detectTapGestures { tap ->
                    val widthPerStar = this.size.width / 5
                    var ratingValue = (tap.x / widthPerStar).coerceIn(0f, 5.toFloat())
                    ratingValue = (ratingValue * 2f).roundToInt() / 2f
                    setRatingState(ratingValue)
                    onRatingChanged(ratingValue)
                }
            }
    ) {
        (1..5).forEach { step ->
            val stepRating = when {
                rating > step -> 1f
                step.rem(rating) < 1 -> rating - (step - 1f)
                else -> 0f
            }
            RatingStar(stepRating, color)
        }
    }
}

@Composable
private fun RatingStar(
    rating: Float,
    ratingColor: Color = Color.Yellow,
    backgroundColor: Color = Color.Gray
) {
    BoxWithConstraints(
        modifier = Modifier
            .padding(2.dp)
            .fillMaxHeight()
            .clip(starShape)
    ) {
        Canvas(modifier = Modifier.size(maxHeight)) {
            drawRect(
                brush = SolidColor(backgroundColor),
                size = Size(
                    height = size.height * 1.4f,
                    width = size.width * 1.4f
                ),
                topLeft = Offset(
                    x = -(size.width * 0.1f),
                    y = -(size.height * 0.1f)
                )
            )
            if (rating > 0) {
                drawRect(
                    brush = SolidColor(ratingColor),
                    size = Size(
                        height = size.height * 1.1f,
                        width = size.width * rating
                    )
                )
            }
        }
    }
}

private val starShape = GenericShape { size, _ ->
    addPath(starPath(size.height))
}

private val starPath = { size: Float ->
    Path().apply {
        val outerRadius: Float = size / 1.8f
        val innerRadius: Double = outerRadius / 2.5
        var rot: Double = Math.PI / 2 * 3
        val cx: Float = size / 2
        val cy: Float = size / 20 * 11
        var x: Float = cx
        var y: Float = cy
        val step = Math.PI / 5

        moveTo(cx, cy - outerRadius)
        repeat(5) {
            x = (cx + cos(rot) * outerRadius).toFloat()
            y = (cy + sin(rot) * outerRadius).toFloat()
            lineTo(x, y)
            rot += step

            x = (cx + cos(rot) * innerRadius).toFloat()
            y = (cy + sin(rot) * innerRadius).toFloat()
            lineTo(x, y)
            rot += step
        }
        close()
    }
}