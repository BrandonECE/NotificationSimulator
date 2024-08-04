package com.example.myapplicationbasepruebascompose

import android.annotation.SuppressLint

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke

import androidx.compose.foundation.background

import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer

import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf

import androidx.compose.runtime.getValue

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

import androidx.compose.ui.unit.sp


import com.example.myapplicationbasepruebascompose.ui.theme.MyApplicationBasePruebasComposeTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            App()
        }
    }
}


@SuppressLint("CoroutineCreationDuringComposition", "UnrememberedMutableState")
@Composable
fun App() {
    val drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutine: CoroutineScope = rememberCoroutineScope()

    val lazyState = rememberLazyListState()
    var marginSize: Dp by remember { mutableStateOf(0.dp) }
    val isDragged: Boolean by lazyState.interactionSource.collectIsDraggedAsState()
    val animatedMargin by animateDpAsState(
        targetValue = marginSize,
        animationSpec = if (marginSize > 0.dp) {
            tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        } else {
            tween(durationMillis = 0) // Sin animación para la reducción
        },
        label = "PruebaAnim",
    )
    val list: MutableList<String> = remember {
        mutableStateListOf()
    }
    var switchStopScroll by remember { mutableStateOf(false) }
    var isVisible by remember { mutableStateOf(false) }
    val flow = flow {
        while (true) {
            delay(1000)
            emit((0..100).random())
        }
    }

    val isAtBottom = remember {
        derivedStateOf {
            val lastVisibleItemIndex = lazyState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
            val totalItemsCount = lazyState.layoutInfo.totalItemsCount
            lastVisibleItemIndex != null && lastVisibleItemIndex  == totalItemsCount - 1
        }
    }

    LaunchedEffect(isAtBottom.value) {
        if(isAtBottom.value){
            lazyState.scrollToItem(list.size + 1)
            switchStopScroll = false
        }
    }


    LaunchedEffect(list.size) {
        isVisible = true
    }

    LaunchedEffect(switchStopScroll, isDragged) {
//        Log.d("PRUEBAAAAA43", "LAUNCHEDEFFECT -> ScrollStop: $isDragged | Switch $switchStopScroll")
        if (switchStopScroll && !isDragged) {
//            Log.d("PRUEBAAAAA44", "ESPERA - INICIO | Switch $switchStopScroll")

            if (!isAtBottom.value) {
                delay(3000)
                lazyState.animateScrollToItem(list.size + 1)
            }

            switchStopScroll = false
//            Log.d("PRUEBAAAAA45", "ESPERA - FINAL | Switch $switchStopScroll")
        }

    }

    LaunchedEffect(lazyState.layoutInfo, isDragged) {
        if (!isDragged && !switchStopScroll && list.isNotEmpty()) {
//            Log.d("PRUEBAAAAA2", "ScrollStop: $isStopScroll | Switch $switchStopScroll")
            if (!lazyState.isScrollInProgress) {
                lazyState.scrollToItem(list.size + 1)
            }
        }

    }

    LaunchedEffect(isDragged) {
        if (isDragged) {
            switchStopScroll = true
        }
//        Log.d("PRUEBAAAAA3", "INTERACCION: $isDragged")
    }

    LaunchedEffect(Unit) {
        flow.collect { value ->
            marginSize = 97.5.dp
            delay(500)
            marginSize = 0.dp
            isVisible = false
            list.add(value.toString())
            Log.d("PRUEBA2", list.size.toString())
        }

    }




    MyApplicationBasePruebasComposeTheme {

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .background(Color.DarkGray)
                    ) {
                    }
                }
            }
        ) {
            Scaffold(
                topBar = {
                    AppTopBar(coroutine, drawerState)
                }
            ) { padding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {

                    if (list.isEmpty()) {
                        Text(
                            text = "STARTING!",
                            fontSize = 24.sp, fontWeight = FontWeight.Bold
                        )

                    } else {
                        LazyColumn(
                            state = lazyState
                        ) {
                            itemsIndexed(list) { index, item ->
                                if (index < list.size - 1) {
                                    CardPerson(
                                        it = item.toInt()
                                    )
                                }
                            }
                            item {
                                AnimatedVisibility(
                                    visible = isVisible,
                                    enter = fadeIn(tween(durationMillis = 200)),
                                    exit = fadeOut(tween(durationMillis = 0)),
                                ) {
                                    CardPerson(it = list[list.size - 1].toInt())
                                }
                            }
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(animatedMargin)
                                )
                            }

                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                )
                            }
                        }
                    }

                }
            }
        }
    }
}

@Composable
private fun CardPerson(it: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp)
            .padding(horizontal = 20.dp)
            .padding(bottom = 10.dp),
        border = BorderStroke(width = 1.dp, color = Color.Gray),
        colors = CardDefaults.cardColors(

            containerColor = colorResource(id = R.color.gris)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = "Noti",
                    modifier = Modifier.padding(end = 6.dp)
                )
                Text(
                    text = "Notification",
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "Num(${it}).",
                fontSize = 20.sp,
                fontWeight = FontWeight.W500

            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun AppTopBar(coroutine: CoroutineScope, drawerState: DrawerState) {
    TopAppBar(
        navigationIcon = {
            IconButton(
                onClick = {
                    coroutine.launch {
                        drawerState.open()
                    }
                }) {
                Icon(imageVector = Icons.Filled.Menu, contentDescription = null)
            }
        },
        title = {
            Text(
                text = "Test",

                fontWeight = FontWeight.SemiBold
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            navigationIconContentColor = Color.White,
            containerColor = Color.DarkGray,
            titleContentColor = Color.White
        )
    )
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    App()
}