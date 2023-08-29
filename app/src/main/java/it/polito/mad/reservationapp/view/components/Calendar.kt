package it.polito.mad.reservationapp.view.components

import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.drawable.toDrawable
import com.stacktips.view.CalendarListener
import com.stacktips.view.CustomCalendarView
import com.stacktips.view.DayDecorator
import com.stacktips.view.DayView
import it.polito.mad.reservationapp.R
import it.polito.mad.reservationapp.model.firebase.Reservation
import it.polito.mad.reservationapp.ui.theme.Button
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun CustomCalendar(
    reservationList: List<Reservation>?, setSelectedDate: (String)->Unit, notAvailableDays: List<String>,
    isNeededToDisablePastDate:Boolean = false) {

    AndroidView(
        factory = { context ->
            LayoutInflater.from(context).inflate(R.layout.custom_calendar, null)
        },
        update = { view ->

            val calendarView = view as CustomCalendarView

            val currentCalendar: Calendar = Calendar.getInstance(Locale.ENGLISH)
            calendarView.firstDayOfWeek = Calendar.MONDAY
            calendarView.setShowOverflowDate(false)
            calendarView.refreshCalendar(currentCalendar)
            calendarView.findViewById<ImageView>(com.imanoweb.calendarview.R.id.leftButton).setColorFilter(
                Button.toArgb(), PorterDuff.Mode.MULTIPLY
            )
            calendarView.findViewById<ImageView>(com.imanoweb.calendarview.R.id.rightButton).setColorFilter(
                Button.toArgb(), PorterDuff.Mode.MULTIPLY
            )
            calendarView.setCalendarListener(object : CalendarListener {
                override fun onDateSelected(date: Date?) {
                    val selectedDate = date ?: return
                    val df = SimpleDateFormat("dd/MM/yyyy")
                    val selectedDateString = df.format(selectedDate)
                    if (isNeededToDisablePastDate) {
                        val today = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, 0)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }

                        if (selectedDate.before(today.time)) {
                            Toast.makeText(
                                view.context,
                                "Selected date ($selectedDateString) is before today.",
                                Toast.LENGTH_SHORT
                            ).show()
                            return
                        }
                    }
                    setSelectedDate(selectedDateString)
                }

                override fun onMonthChanged(date: Date?) {
                    val df = SimpleDateFormat("MM-yyyy")
                    Toast.makeText(view.context, df.format(date), Toast.LENGTH_SHORT).show()
                }
            })

            val decorators: MutableList<DayDecorator> = ArrayList()
            decorators.add(ReservationDayColors(reservationList, notAvailableDays))
            decorators.add(DayDecorator { dayView -> // Change background color for dates before today if enabled
                if (isNeededToDisablePastDate) {
                    val today = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    val selectedDate = Calendar.getInstance().apply {
                        time = dayView.date
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    if (selectedDate.before(today)) {
                        dayView.setBackgroundColor(android.graphics.Color.DKGRAY)
                    }
                }
            })
            calendarView.decorators = decorators
            calendarView.refreshCalendar(currentCalendar)

        }
    )
}

private class ReservationDayColors(val reservationList: List<Reservation>?, val notAvailableDays: List<String>) : DayDecorator {
    override fun decorate(dayView: DayView) {
        val inputDateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
        val df = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)

        reservationList?.map { it -> df.format(inputDateFormat.parse(it.reserved_date.toString())) }?.forEach {
            println("DATE FROM LIST: ${it}, DATE TO MATCH: ${df.format(dayView.date)}")
        }

        if (reservationList?.map { it -> df.format(inputDateFormat.parse(it.reserved_date.toString())) }?.any { it == df.format(dayView.date) } == true) {
            dayView.setBackgroundResource(R.drawable.ellipse_green_calendar)
        }
        notAvailableDays.forEach {
            if(df.format(dayView.date) == df.format(inputDateFormat.parse(it)))
                dayView.setBackgroundResource(R.drawable.ellipse_red_calendar)
        }
    }
}