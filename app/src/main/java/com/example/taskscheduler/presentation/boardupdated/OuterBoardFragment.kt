package com.example.taskscheduler.presentation.boardupdated

import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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

    //    private lateinit var recyclerViewParent: RecyclerView
    private lateinit var parentAdapter: OuterBoardAdapter
    lateinit var viewModel: OuterBoardViewModel
    private var parentList = ArrayList<ListOfNotesItem>()
    private var viewPager: ViewPager2? = null
    private lateinit var tabLayout: TabLayout
    var currentPosition = 0
    var sharedPreferences: SharedPreferences? = null
    var isFirst = true

    private val args by navArgs<OuterBoardFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user = parseArgs()

//        Log.i("USER_TAB_SELECTED", sharedPreferences?.getInt("tabPosition", 0).toString())
//        if (savedInstanceState != null) {
//            val position = savedInstanceState.getInt("tabPosition", 0)
//            viewPager.currentItem = position
//        }
    }

    override fun onPause() {
        super.onPause()
        requireArguments().putInt("position", viewPager?.currentItem ?: 0)
        requireArguments().putBoolean("isFirst", isFirst)
        Log.i("USER_POS", isFirst.toString())
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOuterBoardBinding.inflate(inflater, container, false)
//        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav_view).menu
//            .add(R.menu.bottom_menu_nav, R.id.inviteUserFragment, NONE, "Invite user")

//        TabLayoutMediator(tabLayout, viewPager) {tab, position ->
//            tab.text =
//        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        sharedPreferences = context?.getSharedPreferences("tabPosition", Context.MODE_PRIVATE)
        viewModel = ViewModelProvider(this)[OuterBoardViewModel::class.java]

        currentPosition = requireArguments().getInt("position")
        isFirst = requireArguments().getBoolean("isFirst")
        Log.i("USER_POSITION", currentPosition.toString() + isFirst.toString())
        viewModel.readData(board.id)
//        initViews()
        observeViewModel()
        binding.imageViewInvite.setOnClickListener {
            launchInviteUserFragment()
        }
        binding.buttonInvite.setOnClickListener {

        }
//        Glide.with(this).load(board.backgroundUrl).into(object : CustomTarget<Drawable>() {
//            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
//                binding.constraint.background = resource
//            }
//
//            override fun onLoadCleared(placeholder: Drawable?) {
//                binding.constraint.background = placeholder
//            }
//
//        })
        val textView = binding.textViewAddList
        val editText = binding.editTextAddList

        val buttonAddNewList = binding.buttonAddNewList
        textView.setOnClickListener {
            binding.cardViewBoard.visibility = View.VISIBLE
            textView.visibility = View.GONE
            editText.visibility = View.VISIBLE
            buttonAddNewList.visibility = View.VISIBLE
//            parentList.add(ListOfNotesItem(editText.text.toString(), ArrayList()))
//            parentAdapter.parentListFrom = parentList
        }
        buttonAddNewList.setOnClickListener {

            val textTitle = editText.text.toString().trim()
            if (textTitle.isNotEmpty()) {
//                val list = ArrayList(parentList)
                //parentList.add(ListOfNotesItem("5", textTitle, ArrayList()))
                val item = viewModel.createNewList(textTitle, board)
//                parentAdapter.fragmentList.add(InnerBoardFragment.newInstance(item))
//                parentAdapter.notifyDataSetChanged()
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
        savedInstanceState?.putBoolean("isFirst", isFirst)
    }


    private fun parseArgs(): User {
        board = args.board
        user = args.user
        return user
    }

    private fun launchInviteUserFragment() {
        findNavController().navigate(
            OuterBoardFragmentDirections.actionOuterBoardFragmentToInviteUserFragment(
                board,
                user
            )
        )
    }

//    fun launchNewNoteFragment(listOfNotesItem: ListOfNotesItem, note: Note) {
//        findNavController().navigate(
//            OuterBoardFragmentDirections.actionOuterBoardFragmentToNewNoteFragment(listOfNotesItem, board, user, note))
//    }

    fun retryToListBoard() {
        findTopNavController().popBackStack(R.id.boardListFragment, false)
    }

    private fun observeViewModel() {
        viewModel.listLiveData.observe(viewLifecycleOwner, Observer {
            parentList = it as ArrayList<ListOfNotesItem>
//            Log.i("USER_OBSERVE_LIST", parentList[1].title)
//            val listFragment = ArrayList<Fragment>()
            for (item in it) {
                binding.tabLayout.visibility = View.VISIBLE
            }

//            parentAdapter = OuterBoardAdapter(lifecycle, childFragmentManager, listFragment)
            parentAdapter = OuterBoardAdapter(
                lifecycle,
                childFragmentManager,
                board,
                user,
                currentPosition,
                binding.tabLayout,
                binding.viewPager,
                it,
                isFirst
            )
            isFirst = false
            viewPager = binding.viewPager
            Glide.with(this).load(board.backgroundUrl).into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
//                    viewPager?.setPageTransformer { page, position ->
////                        Handler(Looper.getMainLooper()).post {
////                            val wMeasureSpec =
////                                View.MeasureSpec.makeMeasureSpec(page.width, View.MeasureSpec.EXACTLY)
////                            val hMeasureSpec =
////                                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
////                            page.measure(wMeasureSpec, hMeasureSpec)
////                            page.post {
////                                viewPager?.adapter?.notifyDataSetChanged()
////                            }
//////                    }
////                        }
//                        if (position <= -1.0F || position >= 1.0F) {
//                            page.translationX = page.width * position;
//                            page.alpha = 0.0F;
//                        } else if (position == 0.0F) {
//                            page.translationX = page.width * position;
//                            page.alpha = 1.0F;
//                        } else {
//                            // position is between -1.0F & 0.0F OR 0.0F & 1.0F
//                            page.translationX = page.width * -position;
//                            page.alpha = 1.0F - abs(position);
//                        }
//                    }
                    fillingLayout(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    fillingLayout(placeholder)
                }

            })


//            viewPager.setPageTransformer(object : ViewPager2.PageTransformer {
//                override fun transformPage(view: View, position: Float) {
//
//                }
//
//            })
            tabLayout = binding.tabLayout
            viewPager?.offscreenPageLimit = it.size + 1
            viewPager?.adapter = parentAdapter
            if (viewPager != null) {
                TabLayoutMediator(tabLayout, viewPager!!) { tab, position ->
                    tab.text = it[position].title
                }.attach()
            }


//            listener =


//            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//                override fun onTabSelected(tab: TabLayout.Tab?) {
//
//                }
//
//                override fun onTabUnselected(tab: TabLayout.Tab?) {
//
//
//                }
//
//                override fun onTabReselected(tab: TabLayout.Tab?) {
//
//                }
//            })
//            viewPager.currentItem = currentPosition
//            val tab = tabLayout.getTabAt(currentPosition)
//            tab?.select()
//            val tabL = tabLayout.getTabAt(sharedPreferences?.getInt("position", 0) ?: 0)
//            Log.i("USER_TAB_before", tabL?.position.toString())
//            tabL?.select()
//            viewPager.currentItem = tabL?.position ?: 0
//            Log.i("USER_onVIEW", user.name)
//
//            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//                override fun onTabReselected(tab: TabLayout.Tab?) {
//
//                }
//
//                override fun onTabSelected(tab: TabLayout.Tab?) {
//                    if (tab?.position != null) {
//                        Log.i("USER_TAB", tab.position.toString())
//                        viewPager.currentItem = tab.position
//                        currentPosition = tab.position
//                        sharedPreferences?.edit()?.putInt("position", currentPosition)?.apply()
//                    }
//                }
//
//                override fun onTabUnselected(tab: TabLayout.Tab?) {
//
//                }
//            })


//            recyclerViewParent.adapter = parentAdapter
//            parentAdapter.notifyDataSetChanged()
//            GlobalScope.launch {
//                delay(1000L)
//
//            }


//            recyclerViewParent.visibility = View.VISIBLE
        })
//        viewModel.boardLiveData.observe(viewLifecycleOwner, Observer {
//            board = it
//            val listFragment = ArrayList<Fragment>()
//            for (item in parentList) {
//                val fragment = InnerBoardFragment.newInstance(item, board, user)
//
//                listFragment.add(fragment)
//            }
//        })
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
    }//

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        super.onCreateOptionsMenu(menu, inflater)
//        if (id == OuterBoardFragment().id) {
//            menu.add(R.menu.bottom_menu_nav, R.id.inviteUserFragment, NONE, "Invite user")
//                .setIcon(R.drawable.my_baseline_person_add_24)
//        }
//    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        outState.putInt("tabPosition", tabLayout.selectedTabPosition)
//    }


//    override fun onViewStateRestored(savedInstanceState: Bundle?) {
//        super.onViewStateRestored(savedInstanceState)
//        if (savedInstanceState != null) {
//            val position = savedInstanceState.getInt("tabPosition", 0)
//            viewPager.currentItem = position
//        }
////        currentPosition = savedInstanceState?.getInt("tabPosition") ?: 0
////        binding.viewPager.currentItem = currentPosition
//
//    }


}


class MyPageTransformer : ViewPager2.PageTransformer {
    override fun transformPage(view: View, position: Float) {
        if (position <= -1.0F || position >= 1.0F) {
            view.translationX = view.width * position;
            view.alpha = 0.0F;
        } else if (position == 0.0F) {
            view.translationX = view.width * position;
            view.alpha = 1.0F;
        } else {
            // position is between -1.0F & 0.0F OR 0.0F & 1.0F
            view.translationX = view.width * -position;
            view.alpha = 1.0F - abs(position);
        }

    }

}