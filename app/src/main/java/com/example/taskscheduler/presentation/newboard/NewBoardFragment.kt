package com.example.taskscheduler.presentation.newboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskscheduler.R
import com.example.taskscheduler.databinding.FragmentNewBoardBinding
import com.example.taskscheduler.domain.BackgroundImage
import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.User
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class NewBoardFragment : Fragment() {

    private var _binding: FragmentNewBoardBinding? = null
    private val binding: FragmentNewBoardBinding
        get() = _binding ?: throw RuntimeException("FragmentNewBoardBinding==null")
    private lateinit var viewModel: NewBoardViewModel
    lateinit var user: User
    lateinit var board: Board
    lateinit var newBoardAdapter: NewBoardAdapter
    var urlBackground = ""

    private val args by navArgs<NewBoardFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user = args.user
        board = args.board
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
        initViews()
        observeViewModel()
        if (board.id != "") {
            binding.editNameBoard.setText(board.title)
            urlBackground = board.backgroundUrl
            newBoardAdapter.urlBackground = urlBackground
        }
        binding.saveNewBoard.setOnClickListener {

            val name = binding.editNameBoard.text.toString().trim()

            if (name != "" && urlBackground != "")
                lifecycleScope.launch {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                        viewModel.createNewBoard(name, user, urlBackground, board).catch {
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        }.collect {
                            launchBoardFragment(it, user)
                        }
                    }
                }
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
        newBoardAdapter = NewBoardAdapter(requireContext())
        recyclerView.adapter = newBoardAdapter
        with(binding) {
            loadingIndicatorNewBoard.visibility = View.GONE
            pleaseWaitTextViewNewBoard.visibility = View.GONE
            recyclerViewNewBoard.visibility = View.VISIBLE
            saveNewBoard.visibility = View.VISIBLE

        }
    }

    private fun RecyclerView.runWhenReady(action: () -> Unit) {
        val globalLayoutListener = object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                action()
                viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        }
        viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
    }


    private fun observeViewModel() {
        viewModel.user.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                user = it
            }
        })
        viewModel.boardLiveData.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                Log.i("USER_NEW_BOARD", user.name)
                launchBoardFragment(it, user)
            }
        })
        viewModel.urlImage.observe(viewLifecycleOwner, Observer {
            Log.i("USER_OBSERVE", Thread.currentThread().name)
            newBoardAdapter.backgroundImageUrls = it as ArrayList<BackgroundImage>
            with(binding) {
                loadingIndicatorNewBoard.visibility = View.GONE
                pleaseWaitTextViewNewBoard.visibility = View.GONE
                recyclerViewNewBoard.visibility = View.VISIBLE
                saveNewBoard.visibility = View.VISIBLE

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