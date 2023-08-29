package it.polito.mad.reservationapp.view.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import it.polito.mad.reservationapp.R
import it.polito.mad.reservationapp.ui.theme.Button

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavHostController) {
    val currentBackStackEntry = navController.currentBackStackEntryAsState().value
    val isHomepage = currentBackStackEntry?.destination?.route == "SignInPage" || currentBackStackEntry?.destination?.route == "HomePage"
    val (isDropdownMenuOpen, setIsDropdownMenuOpen) = remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Box( modifier = Modifier
                .fillMaxWidth()){
                Text(
                    text = "Duchi Sport",
                    color = it.polito.mad.reservationapp.ui.theme.Text,
                    fontWeight = FontWeight.Bold,
                    fontSize = 35.sp,
                    modifier = Modifier.clickable { navController.navigate("HomePage") }
                )
            }
         },
        navigationIcon = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (!isHomepage) {
                    Box(contentAlignment = Alignment.Center) {
                        /*IconButton(onClick = {
                            navController.navigateUp()
                        }) {
                            Icon(Icons.Rounded.ArrowBack, contentDescription = "", Modifier.size(30.dp))
                        }*/
                        IconButton(onClick = { setIsDropdownMenuOpen(true) }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }

                        DropdownMenu(
                            expanded = isDropdownMenuOpen,
                            onDismissRequest = { setIsDropdownMenuOpen(false) },
                        ) {
                            DropdownMenuItem(text = { Text("Reservations", fontSize = 20.sp) },  onClick = { navController.navigate("MyReservations"); setIsDropdownMenuOpen(false) })
                            Divider()
                            DropdownMenuItem(text = { Text("Annoucements", fontSize = 20.sp) }, onClick = { navController.navigate("AnnouncementsPage"); setIsDropdownMenuOpen(false) })
                            Divider()
                            DropdownMenuItem(text = { Text("About us", fontSize = 20.sp) }, onClick = { navController.navigate("AboutUsPage"); setIsDropdownMenuOpen(false) })
                            Divider()
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(contentAlignment = Alignment.Center, modifier = Modifier.clickable { navController.navigate("HomePage") }) {
                    Image(
                        painter = painterResource(id = R.drawable.basketball_svgrepo_com),
                        contentDescription = "App Icon",
                        modifier = Modifier.size(40.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
        },
    )
}