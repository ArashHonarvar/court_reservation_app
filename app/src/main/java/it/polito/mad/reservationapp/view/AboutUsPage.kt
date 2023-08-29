import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.polito.mad.reservationapp.R
import it.polito.mad.reservationapp.ui.theme.Background_Secondary
import it.polito.mad.reservationapp.view.components.PageTitle
import it.polito.mad.reservationapp.view.components.TimeslotFilterDisplayer

data class Member(val name: String, val description: String, val imageResId: Int)

val members = listOf(
    Member(
        "Giuseppe Fanuli",
        "Passionate about backend development. Loves building scalable and efficient systems.",
        R.drawable.giuseppe
    ),
    Member(
        "Marta Corcione",
        "Frontend enthusiast with a keen eye for user experience. Loves creating beautiful interfaces.",
        R.drawable.marta
    ),
    Member(
        "Arash Honarvar",
        "Software development expert. Loves crafting intuitive and optimized code.",
        R.drawable.arash
    ),
    Member(
        "Mattia Scamuzzi",
        "Full-stack developer who enjoys working on end-to-end solutions. Loves learning new technologies.",
        R.drawable.mattia
    )
)

@Composable
fun AboutUsPage() {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        PageTitle(
            title = "Meet Our Team"
        )
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            items(
                members
            )
            { it ->
                MemberCard(member = it)
            }
        }
    }
}

@Composable
fun MemberCard(member: Member) {
    Card(
        modifier = Modifier
            .fillMaxWidth().padding(16.dp).clip(RoundedCornerShape(16.dp)),
        elevation = 6.dp,
        backgroundColor = Background_Secondary,
    ) {
        Row(
            horizontalArrangement =  Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Column() {

                Image(
                    painter = painterResource(id = member.imageResId),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(150.dp)
                        .padding(8.dp)
                )
            }
            Column() {
                Text(
                    text = member.name,
                    modifier = Modifier.padding(bottom = 8.dp),
                    fontSize = 22.sp,
                    color = it.polito.mad.reservationapp.ui.theme.Text
                )
                Text(
                    text = member.description,
                    fontSize = 16.sp,
                    color = it.polito.mad.reservationapp.ui.theme.Text
                )
            }
        }
    }
}
