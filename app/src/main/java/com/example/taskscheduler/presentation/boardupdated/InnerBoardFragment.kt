package com.example.taskscheduler.presentation.boardupdated

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.taskscheduler.R
import com.example.taskscheduler.databinding.FragmentInnerBoardBinding
import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.ListOfNotesItem
import com.example.taskscheduler.domain.models.Note
import com.example.taskscheduler.domain.models.User
import com.google.android.material.tabs.TabLayout


class InnerBoardFragment : Fragment(), MenuProvider {

    lateinit var binding: FragmentInnerBoardBinding
    lateinit var list: ListOfNotesItem
    lateinit var user: User
    lateinit var board: Board
    lateinit var listNotes: List<Note>
    lateinit var recyclerView: RecyclerView
    lateinit var innerAdapter: InnerBoardAdapter
    lateinit var viewModel: InnerBoardViewModel
    lateinit var listOfLists: ArrayList<ListOfNotesItem>
    var position: Int = 0
    var tabLayout: TabLayout? = null
    var viewPager: ViewPager2? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        position = requireArguments().getInt(POSITION)
        listOfLists =
            requireArguments().getParcelableArrayList(LIST) ?: ArrayList<ListOfNotesItem>()
        list = listOfLists[position]
        user = requireArguments().getParcelable(USER)!!
        board = requireArguments().getParcelable(BOARD)!!
        Log.i("USER_BOARD_FROM_OUTER", board.title)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInnerBoardBinding.inflate(inflater, container, false)
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        viewModel = ViewModelProvider(this)[InnerBoardViewModel::class.java]
        viewModel.getNotes(list.listNotes.keys.toList())
        observeViewModel()
        viewPager = requireActivity().findViewById(R.id.view_pager)
        tabLayout = requireActivity().findViewById(R.id.tab_layout)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.listOfNotesTitle.text = list.title
        binding.textViewAddCard.setOnClickListener {
            launchNewNoteFragment(Note())
        }

    }


    private fun launchNewNoteFragment(note: Note) {
        findNavController().navigate(
            InnerBoardFragmentDirections.actionGlobalNewNoteFragment(
                list,
                board,
                user,
                note,
                listOfLists.toTypedArray()
            )
        )
    }

    private fun dateToInt(date: String): Int {
        Log.i("USER_note_date", date)
        var dateForm = ""
        date.forEach { if (it != '.') dateForm += it }
        Log.i("USER_NOTE_DATE", dateForm)
        val num = dateForm.toIntOrNull()
        var newDate: Int = Int.MAX_VALUE
        if (num != null) {
            val year = num % 10000
            val month = num / 10000 % 100
            val day = num / 1000000
            newDate = year * 10000 + month * 100 + day
        }
        Log.i("USER_NEWDATE", newDate.toString())
        return newDate
    }


    private fun initViews(list: List<Note>) {
        recyclerView = binding.childRecyclerViewBoard
        var newList = list
        newList = newList.sortedWith(Comparator { ln, rn ->
            if (ln.priority < rn.priority || (ln.priority == rn.priority &&
                        dateToInt(ln.date) < dateToInt(rn.date))
            ) -1 else if (ln.priority > rn.priority) 1 else 0
        })
        newList.forEach { Log.i("USER_NEW_LIST", it.title) }

        innerAdapter = InnerBoardAdapter(newList)
        recyclerView.layoutManager = GridLayoutManager(
            requireContext(), 1,
            GridLayoutManager.VERTICAL, false
        )
        recyclerView.adapter = innerAdapter
        recyclerView.setHasFixedSize(true)

        binding.loadingIndicatorList.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE

        innerAdapter.onItemClick = {
            launchNewNoteFragment(it)
        }

    }

    private fun observeViewModel() {
        viewModel.listNotesLiveData.observe(viewLifecycleOwner, Observer {
            initViews(it)
        })
    }


    companion object {

        const val USER = "user"
        const val LIST = "list"
        const val BOARD = "board"
        const val POSITION = "position"

        fun newInstance(
            listOfNotesItems: ArrayList<ListOfNotesItem>,
            position: Int,
            board: Board,
            user: User
        ): InnerBoardFragment {
            return InnerBoardFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(LIST, listOfNotesItems)
                    putInt(POSITION, position)
                    putParcelable(BOARD, board)
                    putParcelable(USER, user)
                }
            }
        }
    }


    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.list_menu, menu)
    }

    override fun onPrepareMenu(menu: Menu) {
        super.onPrepareMenu(menu)
        if (list.creatorId != user.id) {
            menu.findItem(R.id.item_delete_list).isVisible = false
            menu.findItem(R.id.item_change_list_name).isVisible = false
        }
        if (board.creatorId != user.id) {
            menu.findItem(R.id.item_delete_board).isVisible = false
            menu.findItem(R.id.item_change_board).isVisible = false
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

        when (menuItem.itemId) {
            R.id.item_change_list_name -> {
                with(binding) {
                    listOfNotesTitle.visibility = View.INVISIBLE
                    listTitleEdit.visibility = View.VISIBLE
                    buttonSaveListTitle.visibility = View.VISIBLE
                    buttonSaveListTitle.setOnClickListener {
                        if (listTitleEdit.text.toString().trim() != "") {
                            Log.i("USER_BOARD_NAME", board.title)
                            viewModel.renameList(list, board, listTitleEdit.text.toString().trim())
                            buttonSaveListTitle.visibility = View.GONE
                            listTitleEdit.visibility = View.GONE
                            listOfNotesTitle.visibility = View.VISIBLE
                        } else {
                            Toast.makeText(
                                requireContext(), "Нельзя задать пустое название!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

            }
            R.id.item_change_board -> {
                findNavController().navigate(
                    InnerBoardFragmentDirections.actionGlobalNewBoardFragment(
                        user,
                        board
                    )
                )
            }
            R.id.item_delete_list -> {
                if (board.listsOfNotesIds.size < 2) {
                    tabLayout?.visibility = View.GONE
                }
                viewModel.deleteList(list.id, board, true)

            }
            R.id.item_delete_board -> {
                viewModel.deleteBoard(board, user)
                findNavController().popBackStack()
            }
            android.R.id.home -> findNavController().popBackStack()
        }
        return true
    }
}