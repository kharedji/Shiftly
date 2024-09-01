package com.dev.shiftly.screens.main

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.LeadingIconTab
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dev.shiftly.screens.EmployeesPaySlips
import com.dev.shiftly.screens.admin.HomeScreen
import com.dev.shiftly.screens.admin.PaySlipItem
import com.dev.shiftly.screens.admin.PaySlips
import com.dev.shiftly.screens.admin.Shift
import kotlinx.coroutines.launch

@SuppressLint("NewApi")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    paddingValues: PaddingValues = PaddingValues(),
    navController: NavController? = null
) {
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f
    ) {
        3
    }

    val scope = rememberCoroutineScope()
    val tabData = listOf(
        "Employees" to Icons.Filled.Person,
        "Shifts" to Icons.Filled.DateRange,
        "PaySlips" to Icons.Filled.Menu
    )

    BackHandler {
        if (pagerState.currentPage != 0) {
            scope.launch {
                pagerState.animateScrollToPage(0)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddingValues)
    ) {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            divider = {
                      Spacer(modifier = Modifier.height(5.dp))
            },
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                    height = 3.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            tabData.forEachIndexed { index, (title, icon) ->
                LeadingIconTab(
                    icon = { Icon(imageVector = icon, contentDescription = null) },
                    text = { /*Text(title)*/ },
                    selected = pagerState.currentPage == index,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
        ) { page ->
            when (page) {
                0 -> HomeScreen(navController = navController!!)
                1 -> Shift(navController = navController!!)
                2 -> EmployeesPaySlips(navController = navController!! )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen()
}