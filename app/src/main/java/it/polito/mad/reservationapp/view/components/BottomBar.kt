package it.polito.mad.reservationapp.view.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Absolute.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.SpaceAround
import androidx.compose.foundation.layout.Arrangement.SpaceEvenly
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Text
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.*
import it.polito.mad.reservationapp.ui.theme.Button
import java.security.spec.EllipticCurve

@Composable
fun BottomBar(navController: NavController, bottomBarState: Boolean) {
    val activeColor = Button
    val inactiveColor = Color.White

    val icons = listOf<Pair<ImageVector, String>>(
        Pair(Icons.Filled.Home, "HomePage"),
        Pair(Icons.Filled.AddCircle, "BookPage"),
        Pair(Icons.Filled.Person, "ProfilePage")
    )

    var selectedTab by remember { mutableStateOf(0) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    AnimatedVisibility(
        visible = bottomBarState,
        enter = slideInVertically(initialOffsetY = { it })
    ) {
        BottomNavigation(
            backgroundColor = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.height(60.dp).clip(shape = RoundedCornerShape(topEnd = 20.dp, topStart = 20.dp))
        ) {
            icons.forEachIndexed { index, icon ->
                val isSelected = /*(selectedTab == index || */currentRoute?.contains(icon.second) == true//)

                BottomNavigationItem(
                    selected = isSelected,
                    onClick = {
                        selectedTab = index
                        navController.navigate(icon.second)
                    },
                    icon = {
                        WaveAnimation(
                            isSelected = isSelected,
                            content = {
                                Column(horizontalAlignment = CenterHorizontally, verticalArrangement = SpaceAround) {
                                    Icon(
                                        modifier = Modifier.size(36.dp),
                                        imageVector = icon.first,
                                        contentDescription = null,
                                        tint = if (isSelected) activeColor else inactiveColor,
                                    )
                                    Text(
                                        icon.second.dropLast(4),
                                        color = if (isSelected) activeColor else inactiveColor
                                    )
                                }
                            }
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun WaveAnimation(isSelected: Boolean, content: @Composable () -> Unit) {
    val waveAnimation = animateWave(!isSelected)

    Box(
        modifier = Modifier
            .graphicsLayer {
                translationY = waveAnimation * 20.dp.toPx()
            }
    ) {
        content()
    }
}

@Composable
fun animateWave(isSelected: Boolean): Float {
    val transition = updateTransition(targetState = isSelected, label = "")
    val waveAnimation by transition.animateFloat(
        transitionSpec = {
            if (targetState) {
                spring(dampingRatio = 0.6f)
            } else {
                spring(dampingRatio = 0.8f)
            }
        }, label = ""
    ) { isSelected ->
        if (isSelected) 1f else 0f
    }
    return waveAnimation
}
