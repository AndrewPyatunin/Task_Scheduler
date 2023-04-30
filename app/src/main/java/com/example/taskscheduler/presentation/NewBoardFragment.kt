package com.example.taskscheduler.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.taskscheduler.NewBoardViewModel
import com.example.taskscheduler.R
import com.example.taskscheduler.databinding.FragmentNewBoardBinding
import com.example.taskscheduler.domain.Board
import com.example.taskscheduler.domain.User

class NewBoardFragment : Fragment() {
    private var _binding: FragmentNewBoardBinding? = null
    private val binding: FragmentNewBoardBinding
        get() = _binding ?: throw RuntimeException("FragmentNewBoardBinding==null")
    private lateinit var viewModel: NewBoardViewModel
    lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user = parseArgs()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewBoardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[NewBoardViewModel::class.java]
        observeViewModel()
        binding.saveNewBoard.setOnClickListener {
            val name = binding.nameBoard.text.toString().trim()
            viewModel.createNewBoard(name, user)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun launchBoardFragment(board: Board) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, BoardFragment.newInstance(board))
            .addToBackStack(null)
            .commit()
    }
    fun launchLoginFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, LoginFragment.newInstance())
            .addToBackStack(null)
            .commit()
    }

    fun parseArgs(): User {
        var user = User()
        requireArguments().getParcelable<User>(USER)?.let {
            user = it
        }
        return user
    }

    fun observeViewModel() {
        viewModel.user.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                user = it
            }
        })
        viewModel.boardLiveData.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                launchBoardFragment(it)
            }
        })
        viewModel.error.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                launchLoginFragment()
            }
        })
    }

    companion object {

        const val NAME = "NewBoardFragment"
        const val USER = "User"

        private const val KEY_NEW_BOARD = "new_board"

        fun newInstance(user: User): NewBoardFragment {
            return NewBoardFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(USER, user)
                }
            }
        }
    }
}