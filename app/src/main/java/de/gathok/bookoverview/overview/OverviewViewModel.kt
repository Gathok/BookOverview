package de.gathok.bookoverview.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.sqlite.db.SimpleSQLiteQuery
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

    private val _filterPossessionStatus = MutableStateFlow<Boolean?>(null)
    private val _filterReadStatus = MutableStateFlow<Boolean?>(null)

    private val _sortType = MutableStateFlow(SortType.TITLE)

    private val _searchQuery = MutableStateFlow("")
    private val _searchType = MutableStateFlow(SearchType.TITLE)

    private val _filterList = combine(_filterPossessionStatus, _filterReadStatus, _sortType,
        _searchQuery, _searchType) { possessionStatus, readStatus, sortType, searchQuery, searchType ->
        listOf(
            possessionStatus,
            readStatus,
            sortType,
            searchQuery,
            searchType
        )
    }

    private val _books = _filterList
        .flatMapLatest { filters ->
            val possessionStatus = filters[0] as Boolean?
            val readStatus = filters[1] as Boolean?
            val sortType = (filters[2] as SortType).queryValue
            val searchQuery = filters[3] as String
            val searchType = (filters[4] as SearchType).queryValue

            val query = SimpleSQLiteQuery(
                "SELECT * FROM book WHERE (:isOwned IS NULL OR possessionStatus = :isOwned)" +
                        " AND (:isRead IS NULL OR readStatus = :isRead)" +
                        " AND $searchType LIKE '%' || :searchQuery || '%'" +
                        " AND deletedSince = 0" +
                        " ORDER BY $sortType COLLATE NOCASE ASC",
                arrayOf(possessionStatus, readStatus, searchQuery)
            )

            dao.bookRawQuery(query)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    private val _state = MutableStateFlow(OverviewState())
    val state = combine(_state, _books) { state, books ->
            state.copy(
                books = books,
                sortType = _sortType.value,
                searchType = _searchType.value,
                possessionStatus = _filterPossessionStatus.value,
                readStatus = _filterReadStatus.value
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), OverviewState())

    fun onEvent(event: OverviewEvent) {
        when (event) {
            is OverviewEvent.ChangeFilterList -> {
                _filterPossessionStatus.value = event.possessionStatus
                _filterReadStatus.value = event.readStatus
                _sortType.value = event.sortType
                _searchType.value = event.searchType
            }
            is OverviewEvent.DeleteBook -> {
                viewModelScope.launch {
                    dao.trashBookById(event.book.id)
                }
            }
            is OverviewEvent.AddBook -> {
                viewModelScope.launch {
                    dao.upsertBook(event.book)
                }
            }
            is OverviewEvent.SearchQueryChanged -> {
                _searchQuery.value = event.searchQuery
                _state.value = _state.value.copy(searchQuery = event.searchQuery)
            }
            is OverviewEvent.RestoreBook -> {
                viewModelScope.launch {
                    dao.restoreBookById(event.book.id)
                }
            }
        }
    }
}