package it.polito.mad.reservationapp.view.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import it.polito.mad.reservationapp.R

@Composable
fun StartupView(isLoaded: Boolean = false){
    val (editable, setEditable) =  remember { mutableStateOf(true) }
    val (sizeState, setSizeState) = remember { mutableStateOf(50.dp) }
    val size by animateDpAsState(targetValue = sizeState, tween(durationMillis = 2000,
        easing = LinearEasing))


    val infiniteTransition = rememberInfiniteTransition()
    val angle by infiniteTransition.animateFloat(
        initialValue = 0F,
        targetValue = 360F,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing)
        )
    )

    LaunchedEffect(isLoaded){
        if(isLoaded){
            setSizeState(300.dp)
            setEditable(!editable)
        }
    }

    AnimatedVisibility(
        visible = editable,
        exit = fadeOut( animationSpec = tween(2000, easing = FastOutLinearInEasing))
    )
    {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.basketball_svgrepo_com),
                contentDescription = "",
                modifier = Modifier.size(size)
                    .graphicsLayer {
                        rotationZ = angle
                    }
            )
        }
    }

}