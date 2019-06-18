package com.kuzheevadel.vmplayerv2.model

data class Country(
    val name: String,
    val stationcount: String,
    val value: String
) {
    override fun toString(): String {
        return name
    }
}