package com.example.taskscheduler.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.taskscheduler.databinding.FragmentBoardBinding
import com.example.taskscheduler.domain.Board

class BoardFragment : Fragment() {

    private var _binding: FragmentBoardBinding? = null
    private val binding: FragmentBoardBinding
        get() = _binding ?: throw RuntimeException("FragmentBoardBinding==null")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBoardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                retryToListBoard()
            }

        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseArgs()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun parseArgs() {
        requireArguments().getParcelable<Board>(KEY_BOARD)?.let {

        }
    }

    fun retryToListBoard() {
        requireActivity().supportFragmentManager.popBackStack(NewBoardFragment.NAME, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    companion object {

        private const val KEY_BOARD = "board"

        fun newInstance(board: Board): BoardFragment {
            return BoardFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_BOARD, board)
                }
            }
        }
    }
}