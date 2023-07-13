package com.example.arendainstrumenta.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Passport(
    val passportFIO: String,
    val vidan: String,
    val kod: String,
    val data: String,
    val seria: String,
    val nomer: String
): Parcelable {

    constructor(): this("","","","","","")
}
