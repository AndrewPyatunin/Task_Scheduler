package com.example.taskscheduler.presentation.boardupdated

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.taskscheduler.MyDatabaseConnection
import com.example.taskscheduler.R
import com.example.taskscheduler.databinding.FragmentOuterBoardBinding
import com.example.taskscheduler.domain.Board
import com.example.taskscheduler.domain.ListOfNotesItem
import com.example.taskscheduler.domain.User
import com.example.taskscheduler.findTopNavController
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.math.abs


class OuterBoardFragment : Fragment() {
    lateinit var binding: FragmentOuterBoardBinding

    //    private val binding: FragmentBoardBinding
//        get() = _binding ?: throw RuntimeException("FragmentBoardBinding==null")
    lateinit var board: Board
    lateinit var user: User
    val position = MyDatabaseConnection.currentPosition
    private var parentAdapter: OuterBoardAdapter? = null
    lateinit var viewModel: OuterBoardViewModel
    private var parentList = ArrayList<ListOfNotesItem>()
    private var viewPager: ViewPager2? = null
    private lateinit var tabLayout: TabLayout
    var currentPosition = 0
    var isFirst = true

    private val args by navArgs<OuterBoardFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        if (savedInstanceState != null) {
//            currentPosition = requireArguments().getInt("position")
//            Log.i("USER_SAVED", currentPosition.toString())
//        }
        parseArgs()
        MyDatabaseConnection.currentPosition = 0
        MyDatabaseConnection.updated = true
    }

    override fun onPause() {
        super.onPause()
        val item = viewPager?.currentItem ?: 0
        requireArguments().putInt("position", item)
        Log.i("USER_SAVED", item.toString())
        MyDatabaseConnection.currentPosition = item
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOuterBoardBinding.inflate(inflater, container, false)
        registerForContextMenu(binding.viewPager)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        sharedPreferences = context?.getSharedPreferences("tabPosition", Context.MODE_PRIVATE)
        viewModel = ViewModelProvider(this)[OuterBoardViewModel::class.java]

//        currentPosition = requireArguments().getInt("position")

        currentPosition = MyDatabaseConnection.currentPosition
//        if (parentAdapter != null) {
//            parentAdapter?.notifyDataSetChanged()
//        }
        if (MyDatabaseConnection.updated) {
            viewModel.readData(board.id)
        }

//        initViews()
        observeViewModel()
        binding.imageViewInvite.setOnClickListener {
            launchInviteUserFragment()
        }
        binding.buttonInvite.setOnClickListener {

        }
        val textView = binding.textViewAddList
        val editText = binding.editTextAddList

        val buttonAddNewList = binding.buttonAddNewList
        textView.setOnClickListener {
            binding.cardViewBoard.visibility = View.VISIBLE
            textView.visibility = View.GONE
            editText.visibility = View.VISIBLE
            buttonAddNewList.visibility = View.VISIBLE
        }




        buttonAddNewList.setOnClickListener {

            val textTitle = editText.text.toString().trim()
            if (textTitle.isNotEmpty()) {
                val item = viewModel.createNewList(textTitle, board, user)
                textView.visibility = View.VISIBLE
                editText.visibility = View.INVISIBLE
                buttonAddNewList.visibility = View.INVISIBLE
            } else
                Toast.makeText(requireContext(), "Заполните поле ввода", Toast.LENGTH_SHORT).show()
        }
        requireActivity().onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    retryToListBoard()
                }

            })
        savedInstanceState?.putInt("position", viewPager?.currentItem ?: 0)
    }


    private fun parseArgs() {
        board = args.board
        user = args.user
    }

    private fun launchInviteUserFragment() {
        findNavController().navigate(
            OuterBoardFragmentDirections.actionOuterBoardFragmentToInviteUserFragment(
                board,
                user
            )
        )
    }

    fun retryToListBoard() {
        findTopNavController().popBackStack(R.id.boardListFragment, false)
    }

    private fun observeViewModel() {

//        viewModel.listNotesLiveData.observe(viewLifecycleOwner, Observer {
//
//
//        })
        viewModel.listLiveData.observe(viewLifecycleOwner, Observer {
            parentList = it as ArrayList<ListOfNotesItem>
//            Log.i("USER_OBSERVE_LIST", parentList[1].title)
//            val listFragment = ArrayList<Fragment>()
            for (item in it) {
                binding.tabLayout.visibility = View.VISIBLE
            }
            parentAdapter = OuterBoardAdapter(
                lifecycle,
                childFragmentManager,
                board,
                user,
                currentPosition,
                it
            )
            viewPager = binding.viewPager
            Glide.with(this).load(board.backgroundUrl).into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    fillingLayout(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    fillingLayout(placeholder)
                }

            })
            viewPager?.offscreenPageLimit = it.size + 1
            tabLayout = binding.tabLayout
            viewPager?.adapter = parentAdapter
            if (viewPager != null) {
                TabLayoutMediator(tabLayout, viewPager!!) { tab, position ->
                    tab.text = it[position].title
                }.attach()
            }
            tabLayout.getTabAt(currentPosition)?.select()
            viewPager?.currentItem = currentPosition
        })
        viewModel.boardLiveData.observe(viewLifecycleOwner, Observer {
            board = it
        })
    }

    private fun fillingLayout(draw: Drawable?) {
        with(binding) {
            constraint.background = draw
            loadingIndicatorBoard.visibility = View.GONE
            cardViewBoard.visibility = View.VISIBLE
            imageViewInvite.visibility = View.VISIBLE
            viewPager.visibility = View.VISIBLE
//                tabLayout.visibility = View.VISIBLE
        }
    }
    

}


class MyPageTransformer : ViewPager2.PageTransformer {
    override fun transformPage(view: View, position: Float) {
        if (position <= -1.0F || position >= 1.0F) {
            view.translationX = view.width * position
            view.alpha = 0.0F
        } else if (position == 0.0F) {
            view.translationX = view.width * position
            view.alpha = 1.0F
        } else {
            // position is between -1.0F & 0.0F OR 0.0F & 1.0F
            view.translationX = view.width * -position
            view.alpha = 1.0F - abs(position)
        }

    }


}