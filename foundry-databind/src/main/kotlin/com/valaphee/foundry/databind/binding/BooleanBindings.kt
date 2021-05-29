/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

@file:JvmName("BooleanBindings")

package com.valaphee.foundry.databind.binding

import com.valaphee.foundry.databind.value.ObservableValue

fun ObservableValue<Boolean>.bindNot(): Binding<Boolean> = ComputedBinding(this) { it.not() }

infix fun ObservableValue<Boolean>.bindAndWith(other: ObservableValue<Boolean>): Binding<Boolean> = ComputedDualBinding(this, other) { thisValue, otherValue -> thisValue && otherValue }

infix fun ObservableValue<Boolean>.bindOrWith(other: ObservableValue<Boolean>): Binding<Boolean> = ComputedDualBinding(this, other) { thisValue, otherValue -> thisValue || otherValue }

infix fun ObservableValue<Boolean>.bindExclusiveOrWith(other: ObservableValue<Boolean>): Binding<Boolean> = ComputedDualBinding(this, other) { thisValue, otherValue -> thisValue xor otherValue }

infix fun ObservableValue<Boolean>.bindEqualsWith(other: ObservableValue<Boolean>): Binding<Boolean> = ComputedDualBinding(this, other) { thisValue, otherValue -> thisValue == otherValue }

fun ObservableValue<Boolean>.bindToString(): Binding<String> = ComputedBinding(this) { it.toString() }