package it.polito.mad.reservationapp.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PageTitle(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        modifier = Modifier
            .padding(vertical = 16.dp)
            .then(modifier)
    )
}

@Composable
fun InfoText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        fontWeight = FontWeight.Light,
        fontSize = 20.sp,
        modifier = Modifier
            .padding(vertical = 16.dp)
            .then(modifier)
    )
}

@Composable
fun DateDisplayer(text: String) {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.align(Alignment.Center),
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SportTimeslotDisplayer(sport: String, timeslots: List<String>, onClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = sport,
                modifier = Modifier.weight(0.6f),
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = buildTimeslotVisualization(timeslots),
                modifier = Modifier.weight(0.4f),
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 24.sp,
                //fontWeight = FontWeight.Bold
            )
        }
    }
}
@Composable
fun SportDisplayer(sport: String, onClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = sport,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun TimeslotDisplayer(text: String, selected: Boolean = false) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.secondary)
            .padding(16.dp)
    ) {
        Text(
            color = if(selected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onPrimary,
            text = text,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun TimeslotFilterDisplayer(text: String, selected: Boolean = false) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.secondary)
            .padding(8.dp)
    ) {
        Text(
            color = if(selected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onPrimary,
            text = text,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun CourtWithRating(court: String, rating: Float) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.secondary)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically){

            Text(
                text = court,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.End,modifier = Modifier.fillMaxWidth()){

            Text(
                text = rating.toString(),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
            )

            Icon(
                Icons.Default.Star,
                contentDescription = "Star icon",
                tint = Color(0xFFFFD700),
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

fun buildTimeslotVisualization(timeslots: List<String>): String {

    //VERSIONE SEMPLIFICATA
    return if (timeslots.isNotEmpty()) {
        timeslots[0].split("-").first() + '\n' + timeslots.last().split("-").last()
    } else {
        ""
    }
}