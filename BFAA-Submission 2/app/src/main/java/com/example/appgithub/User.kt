package com.example.appgithub
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
        var avatar: String?="",
        var username: String = "",
        var name: String = "",
        var followers: String = "",
        var following: String = "",
        var repository: String = "",
        var company: String = "",
        var location: String = ""
): Parcelable




