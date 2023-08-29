package it.polito.mad.reservationapp.utils

class Result<out T>(
    val value: T?,
    val throwable: Throwable?
)