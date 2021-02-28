package com.rodrigoja.randomuser.di

import com.rodrigoja.randomuser.repository.UserRepository
import com.rodrigoja.randomuser.view.ui.MainActivity
import com.rodrigoja.randomuser.viewmodel.UserViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(userRepository: UserRepository)
    fun inject(viewModel: UserViewModel)
    fun inject(mainActivity: MainActivity)
}