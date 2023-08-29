package it.polito.mad.reservationapp.view.components

import AboutUsPage
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import it.polito.mad.reservationapp.MainActivity
import it.polito.mad.reservationapp.view.AnnouncementsPage
import it.polito.mad.reservationapp.view.BookPage
import it.polito.mad.reservationapp.view.CancelPage
import it.polito.mad.reservationapp.view.CourtAvailabilityPage
import it.polito.mad.reservationapp.view.EditReservationPage
import it.polito.mad.reservationapp.view.HomePage
import it.polito.mad.reservationapp.view.MyReservationsPage
import it.polito.mad.reservationapp.view.NewReservationPage
import it.polito.mad.reservationapp.view.ProfilePage
import it.polito.mad.reservationapp.view.RatingPage
import it.polito.mad.reservationapp.view.ReservationDetailsPage
import it.polito.mad.reservationapp.view.SignInPage
import it.polito.mad.reservationapp.viewModel.AnnouncementBoardViewModel
import it.polito.mad.reservationapp.viewModel.HomePageViewModel
import it.polito.mad.reservationapp.viewModel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationController(
    context: MainActivity,
    homePageViewModel: HomePageViewModel,
    userViewModel: UserViewModel,
    announcementBoardViewModel: AnnouncementBoardViewModel
) {

    val navController = rememberNavController()

    // To hide bottom bar when the page is loading
    val (bottomBarState, setBottomBarState) = rememberSaveable { (mutableStateOf(false)) }

    // State which is true if the app has been launched for the first time
    val (isFirstStart, setIsFirstStart) = rememberSaveable { mutableStateOf(true) }

//    val res by remember{announcementBoardViewModel.getActiveAnnouncements()}.observeAsState()
//    val res2 by remember { userViewModel.userAnnouncement }.observeAsState()
//
//    LaunchedEffect(Unit){
//        announcementBoardViewModel.saveAnnouncement(
//            Announcement(
//                "",
//                Timestamp.now().toDate(),
//                "I wanna find someone to play with me",
//                User("user1","Leonardo Bonucci","","",0,"","", emptyList()),
//                "Football"
//            )
//        )
//        announcementBoardViewModel.deleteAnnouncementById("")
//    }

    Scaffold(
        bottomBar = { BottomBar(navController, bottomBarState) },
        topBar = { TopBar(navController) }
    ) {
        Box(Modifier.padding(it)) {

            NavHost(navController, startDestination = "SignInPage") {
                composable("SignInPage"){ SignInPage(navController, userViewModel) }
                composable("HomePage") { HomePage(navController, context, setBottomBarState, isFirstStart, setIsFirstStart, homePageViewModel) }
                composable("BookPage") { BookPage(navController) }
                composable("AboutUsPage") { AboutUsPage() }
                composable("ProfilePage") { ProfilePage(navController, context, userViewModel, setBottomBarState) }
                composable("MyReservations") { MyReservationsPage(navController, userViewModel) }
                composable("AnnouncementsPage"){ AnnouncementsPage(announcementBoardViewModel, userViewModel) }
                composable(
                    route = "CancelPage/{reservationID}",
                    arguments = listOf(
                        navArgument("reservationID") {
                            type = NavType.StringType
                            defaultValue = ""
                        }
                    )
                ) { navBackStackEntry ->
                    CancelPage(
                        navBackStackEntry.arguments?.getString("reservationID","")!!,
                        navController
                    ) }

                composable(
                    route = "DetailsPage/{reservationID}",
                    arguments = listOf(
                        navArgument("reservationID") {
                            type = NavType.StringType
                            defaultValue = ""
                        }
                    )
                ) { navBackStackEntry ->
                    ReservationDetailsPage(
                        navBackStackEntry.arguments?.getString("reservationID","")!!,
                        navController
                    ) }

                composable(
                    route = "CourtAvailabilityPage/{sportName}/{date}",
                    arguments = listOf(
                        navArgument("sportName") {
                            type = NavType.StringType
                            defaultValue = ""
                        },
                        navArgument("date") {
                            type = NavType.StringType
                            defaultValue = ""
                        }
                    )
                ) { navBackStackEntry ->
                    CourtAvailabilityPage(
                        navBackStackEntry.arguments?.getString("sportName","")!!,
                        navBackStackEntry.arguments?.getString("date","")!!,
                        navController
                    ) }

                composable(
                    route= "RatingPage/{courtID}/{reservationID}",
                    arguments = listOf(
                        navArgument("courtID") {
                            type = NavType.StringType
                            defaultValue = ""
                        },
                        navArgument("reservationID") {
                            type = NavType.StringType
                            defaultValue = ""
                        }
                    )
                ) { navBackStackEntry ->
                    RatingPage(
                        navBackStackEntry.arguments?.getString("courtID", "")!!,
                        navBackStackEntry.arguments?.getString("reservationID", "")!!,
                        navController
                    ) }

                composable(
                    route = "EditReservation/{reservationID}/{courtID}/{sportName}",
                    arguments = listOf(
                        navArgument("courtID") {
                            type = NavType.StringType
                            defaultValue = ""
                        },
                        navArgument("reservationID") {
                            type = NavType.StringType
                            defaultValue = ""
                        },
                        navArgument("sportName") {
                            type = NavType.StringType
                            defaultValue = ""
                        }
                    )
                ) { navBackStackEntry ->
                    EditReservationPage(
                        navBackStackEntry.arguments?.getString("reservationID", "")!!,
                        navBackStackEntry.arguments?.getString("courtID", "")!!,
                        navBackStackEntry.arguments?.getString("sportName", "")!!,
                        navController
                ) }

                composable(
                    route = "NewReservation/{courtID}/{date}",
                    arguments = listOf(
                        navArgument("courtID") {
                            type = NavType.StringType
                            defaultValue = ""
                        },
                        navArgument("date") {
                            type = NavType.StringType
                            defaultValue = ""
                        }
                    )
                ) { navBackStackEntry ->
                    NewReservationPage(
                        navBackStackEntry.arguments?.getString("courtID", "")!!,
                        navBackStackEntry.arguments?.getString("date", "")!!,
                        navController,
                        userViewModel
                    ) }
            }
        }
    }
}