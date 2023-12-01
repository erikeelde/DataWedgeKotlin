package com.darryncampbell.datawedgekotlin

import android.content.Context
import com.example.datawedgerepository.DataWedgeRepository
import com.example.datawedgerepository.DataWedgeRepositoryImpl

object DataWedgeHolder {
    private var dataWedgeRepository: DataWedgeRepository? = null

    fun init(context: Context) {
        dataWedgeRepository = DataWedgeRepositoryImpl(context)
    }

    fun get() : DataWedgeRepository = dataWedgeRepository!!
}