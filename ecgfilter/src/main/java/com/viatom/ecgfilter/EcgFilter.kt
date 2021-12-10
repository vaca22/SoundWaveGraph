package com.viatom.ecgfilter

object EcgFilter {
    init {
        System.loadLibrary("offline-lib-20211025")
        System.loadLibrary("online-lib");
    }
    external fun shortfilter(shorts: ShortArray): ShortArray
    external fun filter(f: Double, reset: Boolean): DoubleArray?
}