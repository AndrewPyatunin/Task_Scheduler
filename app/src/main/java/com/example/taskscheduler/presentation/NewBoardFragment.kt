package com.example.taskscheduler.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.taskscheduler.R
import com.example.taskscheduler.databinding.FragmentNewBoardBinding
import com.example.taskscheduler.domain.Board

class NewBoardFragment : Fragment() {
    private var _binding: FragmentNewBoardBinding? = null
    private val binding: FragmentNewBoardBinding
        get() = _binding ?: throw RuntimeException("FragmentNewBoardBinding==null")

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
        binding.saveNewBoard.setOnClickListener {
            launchBoardFragment(board = Board("MyBoard", ArrayList(), ArrayList(), ArrayList()))
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

    companion object {

        const val NAME = "NewBoardFragment"

        private const val KEY_NEW_BOARD = "new_board"

        fun newInstance(): NewBoardFragment {
            return NewBoardFragment()
        }
    }
}