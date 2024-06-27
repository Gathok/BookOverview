package de.gathok.bookoverview.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.gathok.bookoverview.data.BookDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class OverviewViewModel (
    private val dao: BookDao
): ViewModel() {

    private val _sortType = MutableStateFlow(SortType.TITLE)
    private val _filterPossessionStatus = MutableStateFlow<Boolean?>(null)
    private val _filterReadStatus = MutableStateFlow<Boolean?>(null)
    private val _filterList = combine(_filterPossessionStatus, _filterReadStatus, _sortType) { possessionStatus, readStatus, sortType ->
        listOf(
            possessionStatus,
            readStatus,
            sortType
        )
    }

    private val _books = _filterList
        .flatMapLatest { filters ->
            when (filters[2]) {
                SortType.AUTHOR -> dao.getBooks(filters[0] as Boolean?, filters[1] as Boolean?, "author")
                else -> dao.getBooks(filters[0] as Boolean?, filters[1] as Boolean?, "title")
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    private val _state = MutableStateFlow(OverviewState())
    val state = combine(_state, _sortType, _books) { state, sortType, books ->
            state.copy(
                books = books,
                sortType = sortType,
                possessionStatus = _filterPossessionStatus.value,
                readStatus = _filterReadStatus.value
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), OverviewState())

    fun onEvent(event: OverviewEvent) {
        when (event) {
            is OverviewEvent.ChangePossessionStatus -> {
                _filterPossessionStatus.value = event.possessionStatus
            }
            is OverviewEvent.ChangeReadStatus -> {
                _filterReadStatus.value = event.readStatus
            }
            is OverviewEvent.ChangeSortType -> {
                _sortType.value = event.sortType
            }
            is OverviewEvent.ChangeFilterList -> {
                _filterPossessionStatus.value = event.possessionStatus
                _filterReadStatus.value = event.readStatus
            }
            is OverviewEvent.DeleteBook -> {
                viewModelScope.launch {
                    dao.deleteBook(event.book)
                }
            }
            is OverviewEvent.AddBook -> {
                viewModelScope.launch {
                    dao.upsertBook(event.book)
                }
            }
        }
    }
}