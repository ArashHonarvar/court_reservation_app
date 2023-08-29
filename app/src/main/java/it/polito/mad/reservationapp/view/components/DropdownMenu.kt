package it.polito.mad.reservationapp.view.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.polito.mad.reservationapp.ui.theme.Background

@Composable
fun DropdownMenuDemo(items: Map<String, String>, selectedString: Pair<String, String>, setSelectedString: (Pair<String,String>)->Unit) {
    val (expanded, setExpanded) = remember {
        mutableStateOf(false)
    }

    Log.d("ITEMS DROPDOWN", "$items, $selectedString")

    Box(
        modifier = Modifier.fillMaxWidth()
            .clickable(onClick = { setExpanded(true) })
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
    ) {
        TextButton(
            onClick = { setExpanded(true) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(start=16.dp, top=8.dp, end=8.dp, bottom=8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                //setSelectedString( Pair<Int,String>(selectedIndex, items[selectedIndex]?:"" ))
                setSelectedString(selectedString)
                Text(
                    text = selectedString.second,
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 24.sp
                )
                Icon(
                    Icons.Default.ArrowDropDown,
                    null,
                )
            }
        }
    }
    Box(Modifier.fillMaxWidth().background(Background)) {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { setExpanded(false) },
            modifier = Modifier
                .background(Background)
                .padding(start=16.dp, end = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .border(1.dp, Color.Black)
                .fillMaxWidth()
        ) {
            items.forEach { (index, item) ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = item,
                            modifier = Modifier.padding(8.dp),
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = 16.sp
                        )
                    },
                    onClick = {
                        setSelectedString(Pair(index, items[index].toString()))
                        setExpanded(false)
                    }
                )
                if (items[index] != items.values.last()) {
                    Divider(color = Color.Gray, thickness = 1.dp)
                }
            }
        }
    }
}