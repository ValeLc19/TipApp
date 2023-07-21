package com.organization.tipapp.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.organization.tipapp.R

@OptIn(ExperimentalComposeUiApi::class)
@Preview
@Composable
fun BillForm(
    onValChange: (String) -> Unit = {}
) {
    val totalPerPersonState = remember { mutableStateOf(0.0) }
    val totalBillState = remember { mutableStateOf("") }
    val validState = remember(totalBillState.value) { totalBillState.value.trim().isNotEmpty() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val tipAmountState = remember { mutableStateOf(0.0) }
    val sliderPositionState = remember { mutableStateOf(0f) }
    val tipPercentage = (sliderPositionState.value * 100).toInt()
    val splitByState = remember { mutableStateOf(1) }
    val range = IntRange(start = 1, endInclusive = 100)

    Surface(
        modifier = Modifier
            .padding(15.dp)
            .fillMaxWidth()
            .fillMaxHeight(),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        border = BorderStroke(width = 1.dp, Color.LightGray)
    ) {
        Column(
            modifier = Modifier.padding(top = 30.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {

            InputField(
                valueState = totalBillState,

                labelId = "Enter Bill",
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions {
                    if (!validState) return@KeyboardActions
                    onValChange(totalBillState.value.trim())
                    keyboardController?.hide()
                }
            )
            Column() {
                Row(
                    modifier = Modifier.padding(top = 20.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        "Split",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(start = 20.dp)
                            .align(alignment = Alignment.CenterVertically)
                    )

                    Spacer(modifier = Modifier.width(100.dp))

                    ElevatedButton(
                        onClick = {
                            splitByState.value =
                                if (splitByState.value > 1) {
                                    splitByState.value - 1
                                } else {
                                    1
                                }
                            totalPerPersonState.value =
                                calculateTotalPerPerson(
                                    totalBill = totalBillState.value.toDouble(),
                                    splitBy = splitByState.value,
                                    tipPercentage = tipPercentage
                                )
                        },
                        content = {
                            Icon(
                                painter = painterResource(id = R.drawable.minus),
                                modifier = Modifier.size(24.dp),
                                contentDescription = "minus icon"
                            )

                        }
                    )

                    Text(
                        text = "${splitByState.value}",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(
                                start = 9.dp, end = 9.dp, top = 1.dp,
                            )
                    )

                    ElevatedButton(
                        onClick = {
                            if (splitByState.value < range.last) {
                                splitByState.value = splitByState.value + 1
                            }
                            totalPerPersonState.value =
                                calculateTotalPerPerson(
                                    totalBill = totalBillState.value.toDouble(),
                                    splitBy = splitByState.value,
                                    tipPercentage = tipPercentage
                                )
                        },
                        content = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_baseline_add_circle_24),
                                contentDescription = "Plus icon"
                            )
                        }
                    )

                }
                Row(
                    modifier = Modifier.padding(bottom = 10.dp)
                ) {
                    Text(
                        text = "Tip",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 22.dp)
                    )
                    Spacer(modifier = Modifier.width(180.dp))

                    Text(
                        text = "$ ${tipAmountState.value}",
                        modifier = Modifier
                            .padding(top = 5.dp)
                            .align(Alignment.CenterVertically)
                    )
                }

            }

            Surface(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(corner = CornerSize(8.dp)),
                border = BorderStroke(width = 1.dp, Color.LightGray)
            ) {
                Row() {

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            modifier = Modifier.padding(top = 90.dp, start = 40.dp),
                            text = "Tip Percentage",
                            fontWeight = FontWeight.Bold

                        )
                        Text(
                            modifier = Modifier.padding(start = 50.dp),
                            fontWeight = FontWeight.Bold,
                            fontSize = 30.sp,
                            text = "$tipPercentage %"
                        )
                    }

                    Column() {
                        Slider(
                            modifier = Modifier
                                .padding(start = 3.dp, bottom = 130.dp, top = 120.dp)
                                .rotate(90.0F),
                            value = sliderPositionState.value,
                            colors = SliderDefaults.colors(
                                thumbColor = colorResource(id = R.color.marron),
                                activeTickColor = colorResource(id = R.color.orange),
                                inactiveTrackColor = colorResource(id = R.color.light_pink),
                                activeTrackColor = colorResource(id = R.color.pink),
                                inactiveTickColor = colorResource(id = R.color.orange)
                            ),
                            steps = 10,
                            onValueChange = { newVal ->
                                sliderPositionState.value = newVal
                                tipAmountState.value =
                                    calculateTotalTip(
                                        totalBill = totalBillState.value.toDouble(),
                                        tipPercentage = tipPercentage
                                    )
                            })
                    }
                }
            }
            TopHeader(totalPerPerson = totalPerPersonState.value)
        }

    }

}

