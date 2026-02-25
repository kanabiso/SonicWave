package io.sonicwave.library.ui.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.sonicwave.library.R
import io.sonicwave.library.ui.LibraryUiEvent
import io.sonicwave.library.ui.SortOrder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private data class SortOptionConfig(
    val sortOrder: SortOrder,
    @StringRes val textRes: Int,
    @DrawableRes val iconRes: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortSheet(
    sheetState: SheetState,
    onSheetOpen: (Boolean) -> Unit,
    sortOrder: SortOrder,
    isDesc: Boolean,
    isAlbumGroup: Boolean,
    scope: CoroutineScope,
    onEvent: (LibraryUiEvent) -> Unit,
) {
    var tempSortOrder by remember(sortOrder) { mutableStateOf(sortOrder) }
    var tempIsDesc by remember(isDesc) { mutableStateOf(isDesc) }
    var tempIsAlbumGroup by remember(isAlbumGroup) { mutableStateOf(isAlbumGroup) }
    var tempFilterQuery by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = {
            onSheetOpen(false)
        },
        sheetState = sheetState,
    ) {
        val sortOptions = remember {
            listOf(
                SortOptionConfig(
                    SortOrder.TITLE,
                    R.string.sort_by_title,
                    R.drawable.sort_by_alpha_24dp
                ),
                SortOptionConfig(
                    SortOrder.AUTHOR,
                    R.string.sort_by_author,
                    R.drawable.sort_by_alpha_24dp
                ),
                SortOptionConfig(
                    SortOrder.DURATION,
                    R.string.sort_by_duration,
                    R.drawable.sort_by_duration_24
                ),
                SortOptionConfig(
                    SortOrder.LAST_ADDED,
                    R.string.sort_by_last_added,
                    R.drawable.save_clock_24
                )
            )
        }

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text(
                text = stringResource(R.string.sort_your_library),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 16.dp)
            )

            sortOptions.forEach { option ->
                val isSelected = tempSortOrder == option.sortOrder

                SortElementButton(
                    text = option.textRes,
                    icon = option.iconRes,
                    isSelected = isSelected,
                    isDesc = if (isSelected) tempIsDesc else false,
                    onClick = {
                        if (isSelected) {
                            tempIsDesc = !tempIsDesc
                        } else {
                            tempSortOrder = option.sortOrder
                            tempIsDesc = false
                        }
                    }
                )
            }

            Button(
                onClick = {
                    onEvent(
                        LibraryUiEvent.OnApplyFilters(
                            sortOrder = tempSortOrder,
                            isDesc = tempIsDesc,
                            isAlbumGroup = tempIsAlbumGroup,
                            filterQuery = tempFilterQuery
                        )
                    )

                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            onSheetOpen(false)
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 16.dp)
            ) {
                Text("Apply")
            }
        }
    }
}