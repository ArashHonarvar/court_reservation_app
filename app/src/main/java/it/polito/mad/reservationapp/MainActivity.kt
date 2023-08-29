package it.polito.mad.reservationapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import it.polito.mad.reservationapp.ui.theme.ReservationAppThemeCustom
import it.polito.mad.reservationapp.view.components.NavigationController
import it.polito.mad.reservationapp.viewModel.AnnouncementBoardViewModel
import it.polito.mad.reservationapp.viewModel.HomePageViewModel
import it.polito.mad.reservationapp.viewModel.UserViewModel

class MainActivity : ComponentActivity() {
    private val homePageViewModel by viewModels<HomePageViewModel>()
    private val profileViewModel by viewModels<UserViewModel>()
    private val announcementBoardViewModel by viewModels<AnnouncementBoardViewModel>()

    lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var signInLauncher: ActivityResultLauncher<Intent>

    override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            account.email?.let { Log.d("Signed In: ", it) };
        } else {
            Log.d("Signed In: ", "Not signed in");
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.firebase_client_id))
                .requestEmail().build()

        mGoogleSignInClient = GoogleSignIn.getClient(
            this,
            gso
        ) // since googleApiClient is deprecated, have to use workaround

        signInLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback<ActivityResult> { result ->
                val task: Task<GoogleSignInAccount> =
                    GoogleSignIn.getSignedInAccountFromIntent(result.data)
                profileViewModel.handleSignInResult(task, this)
            })
        profileViewModel.mGoogleSignInClient = mGoogleSignInClient
        profileViewModel.signInLauncher = signInLauncher

        setContent {
            ReservationAppThemeCustom {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    NavigationController(this, homePageViewModel, profileViewModel, announcementBoardViewModel)
                }
            }
        }
    }
}