package com.olio.trainsembleapp.a_generic

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ListItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun <T>GenericList(
    items: List<T>, innerPadding: PaddingValues,
    onSelect: (T) -> Unit,
    getId: (T) -> Any,
    headlineContent: @Composable (T) -> Unit,
    overlineContent: @Composable (T) -> Unit,
    supportingContent: @Composable (T) -> Unit,

    ) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 16.dp),
    ) {

        items(
            items = items,
            key = getId
        ) { item ->
            Card() {
                ListItem(
                    headlineContent = {
                        headlineContent(item)
                    },
                    overlineContent = {
                        overlineContent(item)
                    },
                    supportingContent = {
                        supportingContent(item)
                    },
                    modifier = Modifier.clickable {
                        onSelect(item)
                    }


                )
            }

        }
    }
}