package com.example.mydiary.viewModels

import androidx.lifecycle.ViewModel



class  SettingsPageViewModel: ViewModel() {
    companion object {

        @Volatile
        private var instance: SettingsPageViewModel? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: SettingsPageViewModel().also { instance = it }
            }
    }

}