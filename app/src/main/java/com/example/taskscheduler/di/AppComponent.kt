package com.example.taskscheduler.di

import android.app.Application
import com.example.taskscheduler.presentation.MainActivity
import com.example.taskscheduler.presentation.TakePhotoActivity
import dagger.BindsInstance
import dagger.Component

@AppScope
@Component(modules = [DatabaseModule::class, DataModule::class, DomainModule::class, ViewModelModule::class, MapperModule::class])
interface AppComponent {

    @Component.Factory
    interface ComponentFactory {

        fun create(@BindsInstance context: Application): AppComponent
    }

    fun inject(activity: MainActivity)

    fun inject(activity: TakePhotoActivity)

    fun fragmentComponent(): FragmentComponent
}