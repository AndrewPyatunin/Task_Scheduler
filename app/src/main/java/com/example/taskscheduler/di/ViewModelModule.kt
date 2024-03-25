package com.example.taskscheduler.di

import androidx.lifecycle.ViewModel
import com.example.taskscheduler.presentation.boardlist.BoardListViewModel
import com.example.taskscheduler.presentation.boardupdated.InnerBoardViewModel
import com.example.taskscheduler.presentation.boardupdated.OuterBoardViewModel
import com.example.taskscheduler.presentation.inviteuser.InviteUserViewModel
import com.example.taskscheduler.presentation.login.LoginViewModel
import com.example.taskscheduler.presentation.myinvites.MyInvitesViewModel
import com.example.taskscheduler.presentation.newboard.NewBoardViewModel
import com.example.taskscheduler.presentation.newnote.NewNoteViewModel
import com.example.taskscheduler.presentation.registration.RegistrationViewModel
import com.example.taskscheduler.presentation.userprofile.UserProfileViewModel
import com.example.taskscheduler.presentation.welcome.WelcomeViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModule {

    @IntoMap
    @ViewModelKey(UserProfileViewModel::class)
    @Binds
    fun bindUserProfileViewModel(impl: UserProfileViewModel): ViewModel

    @IntoMap
    @ViewModelKey(OuterBoardViewModel::class)
    @Binds
    fun bindOuterBoardViewModel(impl: OuterBoardViewModel): ViewModel

    @IntoMap
    @ViewModelKey(InnerBoardViewModel::class)
    @Binds
    fun bindInnerBoardViewModel(impl: InnerBoardViewModel): ViewModel

    @IntoMap
    @ViewModelKey(BoardListViewModel::class)
    @Binds
    fun bindBoardListViewModel(impl: BoardListViewModel): ViewModel

    @IntoMap
    @ViewModelKey(NewBoardViewModel::class)
    @Binds
    fun bindNewBoardViewModel(impl: NewBoardViewModel): ViewModel

    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    @Binds
    fun bindLoginViewModel(impl: LoginViewModel): ViewModel

    @IntoMap
    @ViewModelKey(RegistrationViewModel::class)
    @Binds
    fun bindRegistrationViewModel(impl: RegistrationViewModel): ViewModel

    @IntoMap
    @ViewModelKey(InviteUserViewModel::class)
    @Binds
    fun bindInviteUserViewModel(impl: InviteUserViewModel): ViewModel

    @IntoMap
    @ViewModelKey(MyInvitesViewModel::class)
    @Binds
    fun bindMyInvitesViewModel(impl: MyInvitesViewModel): ViewModel

    @IntoMap
    @ViewModelKey(NewNoteViewModel::class)
    @Binds
    fun bindNewNoteViewModel(impl: NewNoteViewModel): ViewModel

    @IntoMap
    @ViewModelKey(WelcomeViewModel::class)
    @Binds
    fun bindWelcomeViewModel(impl: WelcomeViewModel): ViewModel
}