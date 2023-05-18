package com.example.taskscheduler.presentation.newboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.example.taskscheduler.NewBoardAdapter
import com.example.taskscheduler.databinding.FragmentNewBoardBinding
import com.example.taskscheduler.domain.Board
import com.example.taskscheduler.domain.User

class NewBoardFragment : Fragment() {
    private var _binding: FragmentNewBoardBinding? = null
    private val binding: FragmentNewBoardBinding
        get() = _binding ?: throw RuntimeException("FragmentNewBoardBinding==null")
    private lateinit var viewModel: NewBoardViewModel
    lateinit var user: User
    lateinit var newBoardAdapter: NewBoardAdapter
    var listOfImageUrls = ArrayList<String>()
    var urlBackground = ""

    private val args by navArgs<NewBoardFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user = args.user
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
        initViews()

        binding.saveNewBoard.setOnClickListener {
            val name = binding.nameBoard.text.toString().trim()
            if (name != "" && urlBackground != "")
                viewModel.createNewBoard(name, user, urlBackground)
            else
                Toast.makeText(
                    requireContext(),
                    "Заполните поле названия, а также выберите фон!",
                    Toast.LENGTH_SHORT
                ).show()
        }
        newBoardAdapter.onItemClick = {
            urlBackground = it
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun launchBoardFragment(board: Board, user: User) {
        findNavController().navigate(NewBoardFragmentDirections.actionNewBoardFragmentToBoardFragment(board, user))
//        Log.i("USER_NEW_BOARD", user.name)
//        requireActivity().supportFragmentManager.beginTransaction()
//            .replace(R.id.fragment_container, BoardFragment.newInstance(board, user))
////            .addToBackStack(null)
//            .commit()
    }
    private fun launchLoginFragment() {
//        findNavController().navigate(NewBoardFragmentDirections.actionNewBoardFragmentToLoginFragment())
//        requireActivity().supportFragmentManager.beginTransaction()
//            .replace(R.id.fragment_container, LoginFragment.newInstance())
//            .addToBackStack(null)
//            .commit()
    }

    private fun initViews() {
        binding.recyclerViewNewBoard.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerViewNewBoard.setHasFixedSize(true)
        newBoardAdapter = NewBoardAdapter()
        if (listOfImageUrls.isNotEmpty()) newBoardAdapter.backgroundImageUrls = listOfImageUrls
        binding.recyclerViewNewBoard.adapter = newBoardAdapter
    }

    private fun parseArgs(): User {
        var user = User()
        requireArguments().getParcelable<User>(USER)?.let {
            user = it
        }
        return user
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
        viewModel.error.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                launchLoginFragment()
            }
        })
        viewModel.urlImage.observe(viewLifecycleOwner, Observer {
            newBoardAdapter.backgroundImageUrls = it as ArrayList<String>
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