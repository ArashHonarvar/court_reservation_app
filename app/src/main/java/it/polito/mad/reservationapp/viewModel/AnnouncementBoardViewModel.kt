package it.polito.mad.reservationapp.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import it.polito.mad.reservationapp.model.firebase.Announcement
import it.polito.mad.reservationapp.model.firebase.service.AnnouncementServiceImpl
import it.polito.mad.reservationapp.model.firebase.service.FirebaseServiceImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Date

class AnnouncementBoardViewModel(application: Application) : AndroidViewModel(application) {
    private val _firebaseAnnouncementService = AnnouncementServiceImpl()
    private val _activeAnnouncements = MutableLiveData<List<Announcement>>()
    val activeAnnouncements =  _activeAnnouncements


    fun loadActiveAnnouncements(userId: String) {
        viewModelScope.launch {
            _firebaseAnnouncementService.detachActiveAnnouncementsListener()
            _firebaseAnnouncementService.getActiveAnnouncementsRealTime(userId) {
                _activeAnnouncements.value = it
            }
        }
    }

    fun saveAnnouncement(announcement: Announcement) {
        viewModelScope.launch {
            _firebaseAnnouncementService.saveAnnouncement(announcement)
//            loadActiveAnnouncements()
        }
    }

    fun deleteAnnouncementById(id: String) {
        viewModelScope.launch {
            _firebaseAnnouncementService.deleteAnnouncementById(id)
//            loadActiveAnnouncements() // Refresh the list after deletion
        }
    }
}