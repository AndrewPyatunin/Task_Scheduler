package com.example.taskscheduler.presentation.boardupdated

import android.app.ActionBar.LayoutParams
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.taskscheduler.R
import com.example.taskscheduler.databinding.FragmentInnerBoardBinding
import com.example.taskscheduler.domain.*
import com.google.android.material.tabs.TabLayout


class InnerBoardFragment: Fragment() {

    lateinit var binding: FragmentInnerBoardBinding
    lateinit var list : ListOfNotesItem
    lateinit var user: User
    lateinit var board: Board
    lateinit var recyclerView: RecyclerView
    lateinit var innerAdapter: InnerBoardAdapter
    private var recyclerViewReadyCallback: RecyclerViewReadyCallback? = null
    var currentPosition = 0
    var tabLayout: TabLayout? = null
    var viewPager: ViewPager2? = null
//    private val args by navArgs<InnerBoardFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        list = requireArguments().getParcelable<ListOfNotesItem>(LIST) ?: ListOfNotesItem()
        user = requireArguments().getParcelable(USER) ?: User()
        board = requireArguments().getParcelable(BOARD) ?: Board()
    }


//    override fun onHiddenChanged(hidden: Boolean) {
//        super.onHiddenChanged(hidden)
//        if (!hidden) {
//            if (tabLayout != null)
//                tabLayout?.getTabAt(currentPosition)?.select()
//        }
//    }
//
//    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
//        super.setUserVisibleHint(isVisibleToUser)
//        if (isVisibleToUser)
//            if (tabLayout != null) {
//                tabLayout?.getTabAt(currentPosition)?.select()
//            }
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInnerBoardBinding.inflate(inflater, container, false)
//        tabLayout = view?.findViewById(R.id.tab_layout)
//        tabLayout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//            override fun onTabSelected(tab: TabLayout.Tab?) {
//                if (tab?.position != null)
//                    currentPosition = tab.position
//            }
//
//            override fun onTabUnselected(tab: TabLayout.Tab?) {
//            }
//
//            override fun onTabReselected(tab: TabLayout.Tab?) {
//            }
//
//        })
        viewPager = activity?.findViewById(R.id.view_pager)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        list = arguments?.getParcelable<ListOfNotesItem>(LIST) ?: ListOfNotesItem()
        initViews(ListOfNotesItem(list.id, list.title, list.listNotes))
        binding.listOfNotesTitle.text = list.title
        binding.textViewAddCard.setOnClickListener {
            launchNewNoteFragment(Note())
        }
//        if (parentFragment is OuterBoardFragment) {
//            // Проверьте, является ли фрагмент текущим
//            if ((parentFragment as OuterBoardFragment?)?.childFragmentManager?.findFragmentById(R.id.innerBoardFragment) === this) {
//
//                // Обновите высоту элементов интерфейса
//                val params: ViewGroup.LayoutParams = recyclerView.layoutParams
//                params.height =  LayoutParams.WRAP_CONTENT//новая высота
//                recyclerView.layoutParams = params
//            }
//        }

    }


//    override fun onResume() {
//        super.onResume()
//        binding.root.requestLayout()
//    }

    private fun launchNewNoteFragment(note: Note) {
//        requireActivity().supportFragmentManager.beginTransaction()
//            .addToBackStack(null)
//            .replace(R.id.fragment_container, NewNoteFragment.newInstance(list, Board(), User(), note))
//            .commit()
//        val navController = Navigation.findNavController(requireActivity().findViewById(R.id.fragment_container))
//        navController.navigateUp()
        findNavController().navigate(InnerBoardFragmentDirections.actionGlobalNewNoteFragment(list, board, user, note))
//        findNavController().navigate(OuterBoardFragmentDirections.actionOuterBoardFragmentToNewNoteFragment(list, Board(), User(), note))
//        findNavController().navigate(InnerBoardFragmentDirections.actionInnerBoardFragmentToNewNoteFragment(list, Board(), User(), note))
//        findTopNavController().navigate(InnerBoardFragmentDirections.actionInnerBoardFragmentToNewNoteFragment(list, Board(), User(), note))
//        NewNoteFragment.newInstance()
//        findTopNavController().navigate(InnerBoardFragmentDirections.actionInnerBoardFragmentToNewNoteFragment(list, Board(), User(), note))
    }

//    override fun onResume() {
//        super.onResume()
//        binding.root.requestLayout()
//    }

    private fun initViews(list: ListOfNotesItem) {
//        Log.i("USER_LIST_INNER_BOARD", list.title)
        recyclerView = binding.childRecyclerViewBoard
//        recyclerView.visibility = View.GONE
        val newList = list
        innerAdapter = InnerBoardAdapter(newList)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 1,
            GridLayoutManager.VERTICAL, false)
        recyclerView.adapter = innerAdapter
        recyclerView.setHasFixedSize(true)

        binding.loadingIndicatorList.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE

//        val bundle = parentFragmentManager.findFragmentById(R.id.fragment_container)
//            ?.arguments
//        val nameList = list.title
//            if (Constant.fragmentTabs.containsKey(list.title)) {
//                tabLayout?.getTabAt(Constant.fragmentTabs[list.title])
//                    ?.select()
//            }
//        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
//            override fun onPageSelected(position: Int) {
//                Constant.fragmentTabs[bundle.getString("TAB_NAME")] = position
//            }
//        })
//        recyclerViewReadyCallback = object: RecyclerViewReadyCallback {
//            override fun onLayoutReady() {
//                binding.loadingIndicatorList.visibility = View.GONE
//                recyclerView.visibility = View.VISIBLE
//            }
//
//        }
//        recyclerView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
//            override fun onGlobalLayout() {
//                if (recyclerViewReadyCallback != null) {
//                    recyclerViewReadyCallback?.onLayoutReady()
//                }
//                recyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
//            }
//
//        })
        innerAdapter.onItemClick = {
            launchNewNoteFragment(it)
        }

    }
    companion object {
        const val USER = "user"
        const val LIST = "list"
        const val BOARD = "board"

        fun newInstance(listOfNotesItem: ListOfNotesItem, board: Board, user: User): InnerBoardFragment {
            return InnerBoardFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(LIST, listOfNotesItem)
                    putParcelable(BOARD, board)
                    putParcelable(USER, user)
                }
            }
        }
    }
}