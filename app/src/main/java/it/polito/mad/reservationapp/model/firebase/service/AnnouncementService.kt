package it.polito.mad.reservationapp.model.firebase.service

import it.polito.mad.reservationapp.model.firebase.Announcement

interface AnnouncementService {
    /* --- Announcements --- */
    suspend fun getActiveAnnouncements(): List<Announcement>
    fun getActiveAnnouncementsRealTime(userId: String, cb: (List<Announcement>)->Unit)
    fun detachActiveAnnouncementsListener()
    fun getAnnouncementsByUserIdRealTime(userId: String, cb: (List<Announcement>)->Unit)
    fun detachUserAnnouncementListener()
    fun saveAnnouncement(announcement: Announcement)
    fun deleteAnnouncementById(id: String)
}