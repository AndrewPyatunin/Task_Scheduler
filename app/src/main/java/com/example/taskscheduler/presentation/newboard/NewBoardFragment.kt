package com.example.taskscheduler.presentation.newboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.example.taskscheduler.R
import com.example.taskscheduler.databinding.FragmentNewBoardBinding
import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.User

class NewBoardFragment : Fragment() {

    private lateinit var user: User
    private lateinit var board: Board
    private lateinit var newBoardAdapter: NewBoardAdapter
    private var urlBackground = ""
    private var _binding: FragmentNewBoardBinding? = null
    private val args by navArgs<NewBoardFragmentArgs>()
    private val binding: FragmentNewBoardBinding
        get() = _binding ?: throw RuntimeException("FragmentNewBoardBinding==null")

    private val viewModel by lazy {
        ViewModelProvider(this)[NewBoardViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user = args.user
        board = args.board
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewBoardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observeViewModel()
        if (board.id.isNotEmpty()) {
            binding.editNameBoard.setText(board.title)
            urlBackground = board.backgroundUrl
            newBoardAdapter.urlBackground = urlBackground
        }
        binding.saveNewBoard.setOnClickListener {

            val name = binding.editNameBoard.text.toString().trim()

            if (name.isNotEmpty() && urlBackground.isNotEmpty())
                viewModel.createNewBoard(name, user, urlBackground, board)
            else
                Toast.makeText(
                    requireContext(),
                    getString(R.string.fill_in_title_field),
                    Toast.LENGTH_SHORT
                ).show()
        }
        newBoardAdapter.onItemClick = {
            urlBackground = it.imageUrl
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun launchBoardFragment(board: Board, user: User) {
        findNavController().navigate(
            NewBoardFragmentDirections.actionNewBoardFragmentToOuterBoardFragment(
                user,
                board
            )
        )
    }

    private fun initViews() {
        val recyclerView = binding.recyclerViewNewBoard
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        newBoardAdapter = NewBoardAdapter()
        recyclerView.adapter = newBoardAdapter
        with(binding) {
            loadingIndicatorNewBoard.visibility = View.GONE
            pleaseWaitTextViewNewBoard.visibility = View.GONE
            recyclerViewNewBoard.visibility = View.VISIBLE
            saveNewBoard.visibility = View.VISIBLE
        }
    }

    private fun observeViewModel() {
        viewModel.boardLiveData.observe(viewLifecycleOwner) {
            launchBoardFragment(it, user)
        }
        viewModel.urlImage.observe(viewLifecycleOwner) {
            newBoardAdapter.backgroundImageUrls = ArrayList(it)
            with(binding) {
                loadingIndicatorNewBoard.visibility = View.GONE
                pleaseWaitTextViewNewBoard.visibility = View.GONE
                recyclerViewNewBoard.visibility = View.VISIBLE
                saveNewBoard.visibility = View.VISIBLE
            }
        }
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