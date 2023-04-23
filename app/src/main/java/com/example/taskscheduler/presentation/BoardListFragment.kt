package com.example.taskscheduler.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.taskscheduler.R
import com.example.taskscheduler.databinding.FragmentBoardListBinding
import com.example.taskscheduler.domain.Board

class BoardListFragment: Fragment() {
    private var _binding: FragmentBoardListBinding? = null
    private val binding: FragmentBoardListBinding
        get() = _binding ?: throw RuntimeException("FragmentBoardListBinding==null")
    private lateinit var board: Board

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBoardListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imageView.setOnClickListener {
            launchNewBoardFragment()
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun launchNewBoardFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, NewBoardFragment.newInstance())
            .addToBackStack(NewBoardFragment.NAME)
            .commit()
    }

    fun launchBoardFragment(board: Board) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, BoardFragment.newInstance(board))
            .addToBackStack(null)
            .commit()
    }

    private fun parseArgs() {
        requireArguments().getParcelable<Board>(KEY_BOARD)?.let {
            board = it
        }
    }

    companion object {

        const val NAME = "BoardListFragment"

        private const val KEY_BOARD = "board"

        fun newInstance(board: Board): BoardListFragment {
            return BoardListFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_BOARD, board)
                }
            }
        }
    }

}