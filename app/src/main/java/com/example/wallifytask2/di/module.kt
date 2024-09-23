package com.example.wallifytask2.di

import com.example.wallifytask2.data.repository.ApiRepository
import com.example.wallifytask2.viewmodel.ApiViewModel
import com.example.wallifytask2.viewmodel.StorageViewModel
import org.koin.dsl.module

val appModule = module {
    single { ApiRepository() }
    single { ApiViewModel(get()) }
    single { StorageViewModel() }
}