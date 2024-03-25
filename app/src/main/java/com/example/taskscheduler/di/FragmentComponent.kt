package com.example.taskscheduler.di

import com.example.taskscheduler.presentation.ForgotPasswordFragment
import com.example.taskscheduler.presentation.TabsFragment
import com.example.taskscheduler.presentation.boardlist.BoardListFragment
import com.example.taskscheduler.presentation.boardupdated.InnerBoardFragment
import com.example.taskscheduler.presentation.boardupdated.OuterBoardFragment
import com.example.taskscheduler.presentation.inviteuser.InviteUserFragment
import com.example.taskscheduler.presentation.login.LoginFragment
import com.example.taskscheduler.presentation.myinvites.MyInvitesFragment
import com.example.taskscheduler.presentation.newboard.NewBoardFragment
import com.example.taskscheduler.presentation.newnote.NewNoteFragment
import com.example.taskscheduler.presentation.registration.RegistrationFragment
import com.example.taskscheduler.presentation.userprofile.UserProfileFragment
import com.example.taskscheduler.presentation.welcome.WelcomeFragment
import dagger.Subcomponent

@Subcomponent
interface FragmentComponent {

    fun inject(fragment: OuterBoardFragment)

    fun inject(fragment: InnerBoardFragment)

    fun inject(fragment: RegistrationFragment)

    fun inject(fragment: LoginFragment)

    fun inject(fragment: BoardListFragment)

    fun inject(fragment: InviteUserFragment)

    fun inject(fragment: MyInvitesFragment)

    fun inject(fragment: NewBoardFragment)

    fun inject(fragment: TabsFragment)

    fun inject(fragment: NewNoteFragment)

    fun inject(fragment: UserProfileFragment)

    fun inject(fragment: WelcomeFragment)

    fun inject(fragment: ForgotPasswordFragment)
}