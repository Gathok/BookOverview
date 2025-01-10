package de.gathok.bookoverview.series

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

class SeriesOverviewViewModel (
    private val dao: BookDao
): ViewModel() {

    private val _searchQuery = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _seriesList = _searchQuery
        .flatMapLatest { searchQuery ->

            val query = SimpleSQLiteQuery(
                "SELECT * FROM bookseries WHERE title LIKE '%' || :searchQuery || '%'",
                arrayOf(searchQuery)
            )

            dao.seriesRawQuery(query)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val _state = MutableStateFlow(SeriesOverviewState())

    val state = combine(_state, _seriesList) { state, books ->
        state.copy(
            seriesList = books,
            searchQuery = _searchQuery.value
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SeriesOverviewState())

    fun onEvent(event: SeriesOverviewEvent) {
        when(event) {
            is SeriesOverviewEvent.SearchQueryChanged -> {
                _searchQuery.value = event.searchQuery
            }
            is SeriesOverviewEvent.AddSeries -> {
                viewModelScope.launch {
                    dao.upsertBookSeries(event.series)
                }
            }
        }
    }
}