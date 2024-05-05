package com.manudev.presentation.screens.home

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.manudev.domain.model.CharacterDomain
import com.manudev.presentation.R
import com.manudev.presentation.screens.Screen

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onItemClick: (Int) -> Unit
) {
    val listState = rememberLazyListState()
    var isSearchMode by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (isSearchMode) {
                        Text(text = stringResource(R.string.Search))
                    } else {
                        Text(text = stringResource(R.string.AppName))
                    }
                },
                actions = {
                    IconButton(onClick = {
                        isSearchMode = !isSearchMode
                    }) {
                        if (isSearchMode) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = stringResource(R.string.Close)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = stringResource(R.string.Search)
                            )
                        }

                    }
                }
            )
        },
        content = {
            HomeContent(
                listState,
                viewModel.state,
                onItemClick = onItemClick,
                onRefreshList = viewModel::getCharacters
            )
        }
    )

    LaunchedEffect(Unit) {
        viewModel.getCharacters(0, 20)
    }
}

@Composable
fun HomeContent(
    listState: LazyListState,
    state: HomeViewModel.UiState,
    onItemClick: (Int) -> Unit,
    onRefreshList: (Int, Int) -> Unit
) {
    Screen {
        when {
            state.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            state.error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.error)
                }
            }

            else -> {
                LazyColumn(state = listState) {
                    itemsIndexed(state.characters) { index, character ->
                        if (index > 0) Divider()
                        CharacterItem(
                            character = character,
                            onItemClick = { onItemClick(character.id) }
                        )
                        if (index == state.characters.size - 1) {
                            onRefreshList(index + 1, 20)
                            LaunchedEffect(Unit) {
                                listState.scrollToItem(index)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CharacterItem(
    character: CharacterDomain,
    onItemClick: () -> Unit
) {
    ListItem(
        headlineContent = { character.name?.let { Text(text = it) } },
        leadingContent = {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(character.image)
                    .crossfade(true)
                    .build(),
                contentDescription = character.name,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape),
            )
        },
        supportingContent = {
            Text(
                text = character.comics.size.toString(),
            )
        },
        modifier = Modifier
            .clickable { onItemClick() }
    )
}