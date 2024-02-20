package com.example.taskscheduler.presentation.boardupdated

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.taskscheduler.MyApp
import com.example.taskscheduler.R
import com.example.taskscheduler.databinding.FragmentInnerBoardBinding
import com.example.taskscheduler.domain.NoteComparator
import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.Note
import com.example.taskscheduler.domain.models.NotesListItem
import com.example.taskscheduler.domain.models.User
import com.example.taskscheduler.presentation.ViewModelFactory
import com.google.android.material.tabs.TabLayout
import javax.inject.Inject


class InnerBoardFragment : Fragment(), MenuProvider {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var binding: FragmentInnerBoardBinding
    private lateinit var list: NotesListItem
    private lateinit var user: User
    private lateinit var board: Board
    private lateinit var recyclerView: RecyclerView
    private lateinit var innerAdapter: InnerBoardAdapter
    private lateinit var listOfLists: ArrayList<NotesListItem>
    private var position: Int = 0
    private var tabLayout: TabLayout? = null
    private var viewPager: ViewPager2? = null
    private var listNotes: List<Note> = emptyList()
    private var isFirst = true
    private val component by lazy { (requireActivity().application as MyApp).component.fragmentComponent() }
    private val viewModel by viewModels<InnerBoardViewModel>(factoryProducer = { viewModelFactory })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this)
        position = requireArguments().getInt(POSITION)
        isFirst = false
        listOfLists =
            requireArguments().getParcelableArrayList(LIST) ?: arrayListOf()
        list = listOfLists[position]
        user = requireArguments().getParcelable(USER)!!
        board = requireArguments().getParcelable(BOARD)!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInnerBoardBinding.inflate(inflater, container, false)
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
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

    private fun initList(list: List<Note>) {
        val newList = list.sortedWith(NoteComparator)
        innerAdapter = InnerBoardAdapter(newList)
        recyclerView.adapter = innerAdapter
        innerAdapter.onItemClick = {
            launchNewNoteFragment(it)
        }
    }

    private fun initViews(list: List<Note>) {
        if (!isFirst) {
            recyclerView = binding.childRecyclerViewBoard
            initList(list)
            recyclerView.layoutManager = GridLayoutManager(
                requireContext(), 1,
                GridLayoutManager.VERTICAL, false
            )
            recyclerView.setHasFixedSize(true)

            binding.loadingIndicatorList.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    private fun observeViewModel() {
        viewModel.listNotesLiveData.observe(viewLifecycleOwner) {
            listNotes = it
            initViews(it)
        }
        viewModel.fetchNotes(list, listNotes)

        viewModel.readyLiveData.observe(viewLifecycleOwner) {
            viewModel.getNotes(list.listNotes)
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
                        if (listTitleEdit.text.toString().trim().isNotEmpty()) {
                            viewModel.renameList(list, board, listTitleEdit.text.toString().trim())
                            buttonSaveListTitle.visibility = View.GONE
                            listTitleEdit.visibility = View.GONE
                            listOfNotesTitle.visibility = View.VISIBLE
                        } else {
                            Toast.makeText(
                                requireContext(), getString(R.string.cannot_enter_an_empty_name),
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
                viewModel.deleteList(list, board, true)

            }
            R.id.item_delete_board -> {
                viewModel.deleteBoard(board, user)
                findNavController().popBackStack()
            }
            android.R.id.home -> findNavController().popBackStack()
        }
        return true
    }

    companion object {

        const val USER = "user"
        const val LIST = "list"
        const val BOARD = "board"
        const val POSITION = "position"

        fun newInstance(
            notesListItems: ArrayList<NotesListItem>,
            position: Int,
            board: Board,
            user: User
        ): InnerBoardFragment {
            return InnerBoardFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(LIST, notesListItems)
                    putInt(POSITION, position)
                    putParcelable(BOARD, board)
                    putParcelable(USER, user)
                }
            }
        }
    }
}
