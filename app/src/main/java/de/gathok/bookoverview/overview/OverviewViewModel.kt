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

@OptIn(ExperimentalCoroutinesApi::class)
class OverviewViewModel (
    private val dao: BookDao
): ViewModel() {

    private val _sortType = MutableStateFlow(SortType.TITLE)
    private val _contacts = _sortType
        .flatMapLatest { sortType ->
            when(sortType) {
                SortType.TITLE -> dao.getBooksOrderedByTitle()
                SortType.AUTHOR -> dao.getBooksOrderedByAuthor()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    private val _state = MutableStateFlow(OverviewState())
    val state = combine(_state, _sortType, _contacts) { state, sortType, books ->
            state.copy(
                books = books,
                sortType = sortType
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), OverviewState())

    fun onEvent(event: OverviewEvent) {
        when (event) {
            is OverviewEvent.SortBookOverview -> {
                _sortType.value = event.sortType
            }
        }
    }
}