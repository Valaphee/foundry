/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.foundry.math

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

/**
 * @author Kevin Ludwig
 */
open class Double4x4 {
    var matrix = DoubleArray(16)
        protected set
    var offset = 0
        protected set

    init {
        setIdentity()
    }

    operator fun get(index: Int) = matrix[offset + index]

    operator fun get(row: Int, column: Int) = matrix[offset + column * 4 + row]

    operator fun set(index: Int, value: Double) {
        matrix[offset + index] = value
    }

    operator fun set(row: Int, column: Int, value: Double) {
        matrix[offset + column * 4 + row] = value
    }

    fun getRow(row: Int, result: MutableDouble4): MutableDouble4 {
        result.x = this[row, 0]
        result.y = this[row, 1]
        result.z = this[row, 2]
        result.w = this[row, 3]
        return result
    }

    fun setRow(row: Int, value: Double3, w: Double) {
        this[row, 0] = value.x
        this[row, 1] = value.y
        this[row, 2] = value.z
        this[row, 3] = w
    }

    fun setRow(row: Int, value: Double4) {
        this[row, 0] = value.x
        this[row, 1] = value.y
        this[row, 2] = value.z
        this[row, 3] = value.w
    }

    fun getColumn(column: Int, result: MutableDouble4): MutableDouble4 {
        result.x = this[0, column]
        result.y = this[1, column]
        result.z = this[2, column]
        result.w = this[3, column]
        return result
    }

    fun setColumn(column: Int, value: Double3, w: Double) {
        this[0, column] = value.x
        this[1, column] = value.y
        this[2, column] = value.z
        this[3, column] = w
    }

    fun setColumn(column: Int, value: Double4) {
        this[0, column] = value.x
        this[1, column] = value.y
        this[2, column] = value.z
        this[3, column] = value.w
    }

    fun set(value: Double4x4) = apply {
        for (i in 0..15) matrix[offset + i] = value.matrix[value.offset + i]
    }

    fun set(value: Float4x4) = apply {
        for (i in 0..15) matrix[offset + i] = value.matrix[value.offset + i].toDouble()
    }

    fun set(value: List<Double>) = apply {
        for (i in 0..15) matrix[offset + i] = value[i]
    }

    fun setZero() = apply {
        for (i in 0..15) matrix[offset + i] = 0.0
    }

    fun setIdentity() = apply {
        setZero()
        matrix[offset] = 1.0
        matrix[offset + 5] = 1.0
        matrix[offset + 10] = 1.0
        matrix[offset + 15] = 1.0
    }

    fun setRotate(value: Double, axisX: Double, axisY: Double, axisZ: Double) = apply {
        var axisXVar = axisX
        var axisYVar = axisY
        var axisZVar = axisZ

        matrix[offset + 3] = 0.0
        matrix[offset + 7] = 0.0
        matrix[offset + 11] = 0.0
        matrix[offset + 12] = 0.0
        matrix[offset + 13] = 0.0
        matrix[offset + 14] = 0.0
        matrix[offset + 15] = 1.0

        val rad = value.toRad()
        val sin = sin(rad)
        val cos = cos(rad)

        if (axisXVar == 1.0 && axisYVar == 0.0 && axisZVar == 0.0) {
            matrix[offset + 5] = cos
            matrix[offset + 10] = cos
            matrix[offset + 6] = sin
            matrix[offset + 9] = -sin
            matrix[offset + 1] = 0.0
            matrix[offset + 2] = 0.0
            matrix[offset + 4] = 0.0
            matrix[offset + 8] = 0.0
            matrix[offset + 0] = 1.0
        } else if (axisXVar == 0.0 && axisYVar == 1.0 && axisZVar == 0.0) {
            matrix[offset + 0] = cos
            matrix[offset + 10] = cos
            matrix[offset + 8] = sin
            matrix[offset + 2] = -sin
            matrix[offset + 1] = 0.0
            matrix[offset + 4] = 0.0
            matrix[offset + 6] = 0.0
            matrix[offset + 9] = 0.0
            matrix[offset + 5] = 1.0
        } else if (axisXVar == 0.0 && axisYVar == 0.0 && axisZVar == 1.0) {
            matrix[offset + 0] = cos
            matrix[offset + 5] = cos
            matrix[offset + 1] = sin
            matrix[offset + 4] = -sin
            matrix[offset + 2] = 0.0
            matrix[offset + 6] = 0.0
            matrix[offset + 8] = 0.0
            matrix[offset + 9] = 0.0
            matrix[offset + 10] = 1.0
        } else {
            val length = sqrt(axisXVar * axisXVar + axisYVar * axisYVar + axisZVar * axisZVar)
            if (!equals(length, 1.0)) {
                val reciprocalLength = 1.0 / length
                axisXVar *= reciprocalLength
                axisYVar *= reciprocalLength
                axisZVar *= reciprocalLength
            }

            val vers = 1.0 - cos
            val xy = axisXVar * axisYVar
            val yz = axisYVar * axisZVar
            val zx = axisZVar * axisXVar
            val xs = axisXVar * sin
            val ys = axisYVar * sin
            val zs = axisZVar * sin
            matrix[offset + 0] = axisXVar * axisXVar * vers + cos
            matrix[offset + 4] = xy * vers - zs
            matrix[offset + 8] = zx * vers + ys
            matrix[offset + 1] = xy * vers + zs
            matrix[offset + 5] = axisYVar * axisYVar * vers + cos
            matrix[offset + 9] = yz * vers - xs
            matrix[offset + 2] = zx * vers - ys
            matrix[offset + 6] = yz * vers + xs
            matrix[offset + 10] = axisZVar * axisZVar * vers + cos
        }
    }

    fun setRotation(quaternion: Double4) = apply {
        val w = quaternion.w
        val x = quaternion.x
        val y = quaternion.y
        val z = quaternion.z

        var value = sqrt(w * w + x * x + y * y + z * z)
        value = 1.0 / (value * value)

        this[0, 0] = 1.0 - 2 * value * (y * y + z * z)
        this[0, 1] = 2.0 * value * (x * y - z * w)
        this[0, 2] = 2.0 * value * (x * z + y * w)
        this[0, 3] = 0.0
        this[1, 0] = 2.0 * value * (x * y + z * w)
        this[1, 1] = 1.0 - 2 * value * (x * x + z * z)
        this[1, 2] = 2.0 * value * (y * z - x * w)
        this[1, 3] = 0.0
        this[2, 0] = 2.0 * value * (x * z - y * w)
        this[2, 1] = 2.0 * value * (y * z + x * w)
        this[2, 2] = 1.0 - 2 * value * (x * x + y * y)
        this[2, 3] = 0.0
        this[3, 0] = 0.0
        this[3, 1] = 0.0
        this[3, 2] = 0.0
        this[3, 3] = 1.0
    }

    fun setRotation(value: Float3x3) = apply {
        for (row in 0..2) for (column in 0..2) this[row, column] = value[row, column].toDouble()
        scale(1.0f / sqrt(this[0, 0] * this[0, 0] + this[1, 0] * this[1, 0] + this[2, 0] * this[2, 0] + this[3, 0] * this[3, 0]))
    }

    fun setRotation(value: Double4x4) = apply {
        for (row in 0..2) for (col in 0..2) this[row, col] = value[row, col]
        scale(1.0f / sqrt(this[0, 0] * this[0, 0] + this[1, 0] * this[1, 0] + this[2, 0] * this[2, 0] + this[3, 0] * this[3, 0]))
    }

    fun setLookAt(position: Float3, lookAt: Float3, up: Float3) = setLookAt(position.toDouble3(), lookAt.toDouble3(), up.toDouble3())

    fun setLookAt(position: Double3, lookAt: Double3, up: Double3) = apply {
        var fx = lookAt.x - position.x
        var fy = lookAt.y - position.y
        var fz = lookAt.z - position.z

        val rlf = 1.0 / sqrt(fx * fx + fy * fy + fz * fz)
        fx *= rlf
        fy *= rlf
        fz *= rlf

        var sx = fy * up.z - fz * up.y
        var sy = fz * up.x - fx * up.z
        var sz = fx * up.y - fy * up.x

        val rls = 1.0 / sqrt(sx * sx + sy * sy + sz * sz)
        sx *= rls
        sy *= rls
        sz *= rls

        matrix[offset + 0] = sx
        matrix[offset + 1] = sy * fz - sz * fy
        matrix[offset + 2] = -fx
        matrix[offset + 3] = 0.0
        matrix[offset + 4] = sy
        matrix[offset + 5] = sz * fx - sx * fz
        matrix[offset + 6] = -fy
        matrix[offset + 7] = 0.0
        matrix[offset + 8] = sz
        matrix[offset + 9] = sx * fy - sy * fx
        matrix[offset + 10] = -fz
        matrix[offset + 11] = 0.0
        matrix[offset + 12] = 0.0
        matrix[offset + 13] = 0.0
        matrix[offset + 14] = 0.0
        matrix[offset + 15] = 1.0

        translate(-position.x, -position.y, -position.z)
    }

    fun setOrthographic(left: Float, right: Float, bottom: Float, top: Float, near: Float, far: Float) = setOrthographic(left.toDouble(), right.toDouble(), bottom.toDouble(), top.toDouble(), near.toDouble(), far.toDouble())

    fun setOrthographic(left: Double, right: Double, bottom: Double, top: Double, near: Double, far: Double) = apply {
        require(left != right)
        require(bottom != top)
        require(near != far)

        val width = 1.0 / (right - left)
        val height = 1.0 / (top - bottom)
        val depth = 1.0 / (far - near)
        matrix[offset + 0] = 2.0 * width
        matrix[offset + 5] = 2.0 * height
        matrix[offset + 10] = -2.0 * depth
        matrix[offset + 12] = -(right + left) * width
        matrix[offset + 13] = -(top + bottom) * height
        matrix[offset + 14] = -(far + near) * depth
        matrix[offset + 15] = 1.0
        matrix[offset + 1] = 0.0
        matrix[offset + 2] = 0.0
        matrix[offset + 3] = 0.0
        matrix[offset + 4] = 0.0
        matrix[offset + 6] = 0.0
        matrix[offset + 7] = 0.0
        matrix[offset + 8] = 0.0
        matrix[offset + 9] = 0.0
        matrix[offset + 11] = 0.0
    }

    fun setPerspective(fovY: Float, aspect: Float, near: Float, far: Float) = setPerspective(fovY.toDouble(), aspect.toDouble(), near.toDouble(), far.toDouble())

    fun setPerspective(fovY: Double, aspect: Double, near: Double, far: Double) = apply {
        val value = 1.0 / tan(fovY * (PI / 360.0))
        val rangeReciprocal = 1.0 / (near - far)

        matrix[offset + 0] = value / aspect
        matrix[offset + 1] = 0.0
        matrix[offset + 2] = 0.0
        matrix[offset + 3] = 0.0

        matrix[offset + 4] = 0.0
        matrix[offset + 5] = value
        matrix[offset + 6] = 0.0
        matrix[offset + 7] = 0.0

        matrix[offset + 8] = 0.0
        matrix[offset + 9] = 0.0
        matrix[offset + 10] = (far + near) * rangeReciprocal
        matrix[offset + 11] = -1.0

        matrix[offset + 12] = 0.0
        matrix[offset + 13] = 0.0
        matrix[offset + 14] = 2.0 * far * near * rangeReciprocal
        matrix[offset + 15] = 0.0
    }

    fun getOrigin(result: MutableDouble3): MutableDouble3 {
        result.x = this[0, 3]
        result.y = this[1, 3]
        result.z = this[2, 3]
        return result
    }

    fun getRotation(result: Float3x3): Float3x3 {
        result[0, 0] = this[0, 0].toFloat()
        result[0, 1] = this[0, 1].toFloat()
        result[0, 2] = this[0, 2].toFloat()
        result[1, 0] = this[1, 0].toFloat()
        result[1, 1] = this[1, 1].toFloat()
        result[1, 2] = this[1, 2].toFloat()
        result[2, 0] = this[2, 0].toFloat()
        result[2, 1] = this[2, 1].toFloat()
        result[2, 2] = this[2, 2].toFloat()
        return result
    }

    fun getRotationTransposed(result: Float3x3): Float3x3 {
        result[0, 0] = this[0, 0].toFloat()
        result[0, 1] = this[1, 0].toFloat()
        result[0, 2] = this[2, 0].toFloat()
        result[1, 0] = this[0, 1].toFloat()
        result[1, 1] = this[1, 1].toFloat()
        result[1, 2] = this[2, 1].toFloat()
        result[2, 0] = this[0, 2].toFloat()
        result[2, 1] = this[1, 2].toFloat()
        result[2, 2] = this[2, 2].toFloat()
        return result
    }

    fun getRotation(result: MutableDouble4): MutableDouble4 {
        val trace = this[0, 0] + this[1, 1] + this[2, 2]

        if (trace > 0.0) {
            var s = sqrt(trace + 1.0)
            result.w = s * 0.5
            s = 0.5f / s

            result.x = (this[2, 1] - this[1, 2]) * s
            result.y = (this[0, 2] - this[2, 0]) * s
            result.z = (this[1, 0] - this[0, 1]) * s
        } else {
            val i = if (this[0, 0] < this[1, 1]) if (this[1, 1] < this[2, 2]) 2 else 1 else if (this[0, 0] < this[2, 2]) 2 else 0
            val j = (i + 1) % 3
            val k = (i + 2) % 3

            var s = sqrt(this[i, i] - this[j, j] - this[k, k] + 1.0f)
            result[i] = s * 0.5f
            s = 0.5f / s

            result.w = (this[k, j] - this[j, k]) * s
            result[j] = (this[j, i] + this[i, j]) * s
            result[k] = (this[k, i] + this[i, k]) * s
        }
        return result
    }

    fun transpose() = set(transpose(Double4x4()))

    fun transpose(result: Double4x4): Double4x4 {
        for (i in 0..3) {
            val baseIndex = i * 4 + offset
            result.matrix[i + result.offset] = matrix[baseIndex]
            result.matrix[i + 4 + result.offset] = matrix[baseIndex + 1]
            result.matrix[i + 8 + result.offset] = matrix[baseIndex + 2]
            result.matrix[i + 12 + result.offset] = matrix[baseIndex + 3]
        }
        return result
    }

    fun invert(epsilon: Double = 0.0): Boolean {
        val result = Double4x4()
        return invert(result, epsilon).also { if (it) set(result) }
    }

    fun invert(result: Double4x4, epsilon: Double = 0.0): Boolean {
        val src0 = matrix[offset + 0]
        val src4 = matrix[offset + 1]
        val src8 = matrix[offset + 2]
        val src12 = matrix[offset + 3]
        val src1 = matrix[offset + 4]
        val src5 = matrix[offset + 5]
        val src9 = matrix[offset + 6]
        val src13 = matrix[offset + 7]
        val src2 = matrix[offset + 8]
        val src6 = matrix[offset + 9]
        val src10 = matrix[offset + 10]
        val src14 = matrix[offset + 11]
        val src3 = matrix[offset + 12]
        val src7 = matrix[offset + 13]
        val src11 = matrix[offset + 14]
        val src15 = matrix[offset + 15]
        val atmp0 = src10 * src15
        val atmp1 = src11 * src14
        val atmp2 = src9 * src15
        val atmp3 = src11 * src13
        val atmp4 = src9 * src14
        val atmp5 = src10 * src13
        val atmp6 = src8 * src15
        val atmp7 = src11 * src12
        val atmp8 = src8 * src14
        val atmp9 = src10 * src12
        val atmp10 = src8 * src13
        val atmp11 = src9 * src12
        val dst0 = atmp0 * src5 + atmp3 * src6 + atmp4 * src7 - (atmp1 * src5 + atmp2 * src6 + atmp5 * src7)
        val dst1 = atmp1 * src4 + atmp6 * src6 + atmp9 * src7 - (atmp0 * src4 + atmp7 * src6 + atmp8 * src7)
        val dst2 = atmp2 * src4 + atmp7 * src5 + atmp10 * src7 - (atmp3 * src4 + atmp6 * src5 + atmp11 * src7)
        val dst3 = atmp5 * src4 + atmp8 * src5 + atmp11 * src6 - (atmp4 * src4 + atmp9 * src5 + atmp10 * src6)
        val dst4 = atmp1 * src1 + atmp2 * src2 + atmp5 * src3 - (atmp0 * src1 + atmp3 * src2 + atmp4 * src3)
        val dst5 = atmp0 * src0 + atmp7 * src2 + atmp8 * src3 - (atmp1 * src0 + atmp6 * src2 + atmp9 * src3)
        val dst6 = atmp3 * src0 + atmp6 * src1 + atmp11 * src3 - (atmp2 * src0 + atmp7 * src1 + atmp10 * src3)
        val dst7 = atmp4 * src0 + atmp9 * src1 + atmp10 * src2 - (atmp5 * src0 + atmp8 * src1 + atmp11 * src2)
        val btmp0 = src2 * src7
        val btmp1 = src3 * src6
        val btmp2 = src1 * src7
        val btmp3 = src3 * src5
        val btmp4 = src1 * src6
        val btmp5 = src2 * src5
        val btmp6 = src0 * src7
        val btmp7 = src3 * src4
        val btmp8 = src0 * src6
        val btmp9 = src2 * src4
        val btmp10 = src0 * src5
        val btmp11 = src1 * src4
        val dst8 = btmp0 * src13 + btmp3 * src14 + btmp4 * src15 - (btmp1 * src13 + btmp2 * src14 + btmp5 * src15)
        val dst9 = btmp1 * src12 + btmp6 * src14 + btmp9 * src15 - (btmp0 * src12 + btmp7 * src14 + btmp8 * src15)
        val dst10 = btmp2 * src12 + btmp7 * src13 + btmp10 * src15 - (btmp3 * src12 + btmp6 * src13 + btmp11 * src15)
        val dst11 = btmp5 * src12 + btmp8 * src13 + btmp11 * src14 - (btmp4 * src12 + btmp9 * src13 + btmp10 * src14)
        val dst12 = btmp2 * src10 + btmp5 * src11 + btmp1 * src9 - (btmp4 * src11 + btmp0 * src9 + btmp3 * src10)
        val dst13 = btmp8 * src11 + btmp0 * src8 + btmp7 * src10 - (btmp6 * src10 + btmp9 * src11 + btmp1 * src8)
        val dst14 = btmp6 * src9 + btmp11 * src11 + btmp3 * src8 - (btmp10 * src11 + btmp2 * src8 + btmp7 * src9)
        val dst15 = btmp10 * src10 + btmp4 * src8 + btmp9 * src9 - (btmp8 * src9 + btmp11 * src10 + btmp5 * src8)
        var determinant = src0 * dst0 + src1 * dst1 + src2 * dst2 + src3 * dst3
        return if (determinant.isZero(epsilon)) false else {
            determinant = 1.0 / determinant
            result.matrix[result.offset] = dst0 * determinant
            result.matrix[1 + result.offset] = dst1 * determinant
            result.matrix[2 + result.offset] = dst2 * determinant
            result.matrix[3 + result.offset] = dst3 * determinant
            result.matrix[4 + result.offset] = dst4 * determinant
            result.matrix[5 + result.offset] = dst5 * determinant
            result.matrix[6 + result.offset] = dst6 * determinant
            result.matrix[7 + result.offset] = dst7 * determinant
            result.matrix[8 + result.offset] = dst8 * determinant
            result.matrix[9 + result.offset] = dst9 * determinant
            result.matrix[10 + result.offset] = dst10 * determinant
            result.matrix[11 + result.offset] = dst11 * determinant
            result.matrix[12 + result.offset] = dst12 * determinant
            result.matrix[13 + result.offset] = dst13 * determinant
            result.matrix[14 + result.offset] = dst14 * determinant
            result.matrix[15 + result.offset] = dst15 * determinant
            true
        }
    }

    fun add(value: Double4x4) = apply {
        for (i in 0..15) matrix[offset + i] += value.matrix[value.offset + i]
    }

    fun mul(value: Double4x4) = apply {
        val result = Double4x4()
        mul(value, result)
        set(result)
    }

    fun mul(value: Double4x4, result: Double4x4): Double4x4 {
        for (i in 0..3) for (j in 0..3) {
            var x = 0.0
            for (k in 0..3) x += matrix[offset + j + k * 4] * value.matrix[value.offset + i * 4 + k]
            result.matrix[result.offset + i * 4 + j] = x
        }
        return result
    }

    fun translate(x: Double, y: Double, z: Double) = apply {
        for (i in 0..3) {
            val offsetIndex = offset + i
            matrix[12 + offsetIndex] += matrix[offsetIndex] * x + matrix[4 + offsetIndex] * y + matrix[8 + offsetIndex] * z
        }
    }

    fun translate(value: Double3) = translate(value.x, value.y, value.z)

    fun translate(x: Double, y: Double, z: Double, result: Double4x4): Double4x4 {
        for (i in 0..11) result.matrix[result.offset + i] = matrix[offset + i]
        for (i in 0..3) {
            val offsetIndex = offset + i
            result.matrix[result.offset + 12 + i] = matrix[offsetIndex] * x + matrix[4 + offsetIndex] * y + matrix[8 + offsetIndex] * z + matrix[12 + offsetIndex]
        }
        return result
    }

    fun rotate(value: Double, axisX: Double, axisY: Double, axisZ: Double) = set(mul(Double4x4().setRotate(value, axisX, axisY, axisZ), Double4x4()))

    fun rotate(value: Double, axis: Double3) = rotate(value, axis.x, axis.y, axis.z)

    fun rotate(value: Double, axisX: Double, axisY: Double, axisZ: Double, result: Double4x4) = mul(Double4x4().setRotate(value, axisX, axisY, axisZ), result)

    fun rotate(value: Double, axis: Double3, result: Double4x4) = rotate(value, axis.x, axis.y, axis.z, result)

    fun scale(value: Double) = scale(value, value, value)

    fun scale(x: Double, y: Double, z: Double) = apply {
        for (i in 0..3) {
            val offsetIndex = offset + i
            matrix[offsetIndex] *= x
            matrix[4 + offsetIndex] *= y
            matrix[8 + offsetIndex] *= z
        }
    }

    fun scale(scale: Double3): Double4x4 = scale(scale.x, scale.y, scale.z)

    fun scale(x: Double, y: Double, z: Double, result: Double4x4): Double4x4 {
        for (i in 0..3) {
            val resultOffsetIndex = result.offset + i
            val offsetIndex = offset + i
            result.matrix[resultOffsetIndex] = matrix[offsetIndex] * x
            result.matrix[4 + resultOffsetIndex] = matrix[4 + offsetIndex] * y
            result.matrix[8 + resultOffsetIndex] = matrix[8 + offsetIndex] * z
            result.matrix[12 + resultOffsetIndex] = matrix[12 + offsetIndex]
        }
        return result
    }

    fun resetScale() = apply {
        scale(1.0 / sqrt(this[0, 0] * this[0, 0] + this[1, 0] * this[1, 0] + this[2, 0] * this[2, 0]), 1.0 / sqrt(this[0, 1] * this[0, 1] + this[1, 1] * this[1, 1] + this[2, 1] * this[2, 1]), 1.0 / sqrt(this[0, 2] * this[0, 2] + this[1, 2] * this[1, 2] + this[2, 2] * this[2, 2]))
    }

    fun transform(value: MutableFloat3, w: Float = 1.0f): MutableFloat3 {
        val x = value.x * this[0, 0] + value.y * this[0, 1] + value.z * this[0, 2] + w * this[0, 3]
        val y = value.x * this[1, 0] + value.y * this[1, 1] + value.z * this[1, 2] + w * this[1, 3]
        val z = value.x * this[2, 0] + value.y * this[2, 1] + value.z * this[2, 2] + w * this[2, 3]
        return value.set(x.toFloat(), y.toFloat(), z.toFloat())
    }

    fun transform(value: Float3, w: Float = 1.0f, result: MutableFloat3): MutableFloat3 {
        result.x = (value.x * this[0, 0] + value.y * this[0, 1] + value.z * this[0, 2] + w * this[0, 3]).toFloat()
        result.y = (value.x * this[1, 0] + value.y * this[1, 1] + value.z * this[1, 2] + w * this[1, 3]).toFloat()
        result.z = (value.x * this[2, 0] + value.y * this[2, 1] + value.z * this[2, 2] + w * this[2, 3]).toFloat()
        return result
    }

    fun transform(value: MutableFloat4): MutableFloat4 {
        val x = value.x * this[0, 0] + value.y * this[0, 1] + value.z * this[0, 2] + value.w * this[0, 3]
        val y = value.x * this[1, 0] + value.y * this[1, 1] + value.z * this[1, 2] + value.w * this[1, 3]
        val z = value.x * this[2, 0] + value.y * this[2, 1] + value.z * this[2, 2] + value.w * this[2, 3]
        val w = value.x * this[3, 0] + value.y * this[3, 1] + value.z * this[3, 2] + value.w * this[3, 3]
        return value.set(x.toFloat(), y.toFloat(), z.toFloat(), w.toFloat())
    }

    fun transform(value: Float4, result: MutableFloat4): MutableFloat4 {
        result.x = (value.x * this[0, 0] + value.y * this[0, 1] + value.z * this[0, 2] + value.w * this[0, 3]).toFloat()
        result.y = (value.x * this[1, 0] + value.y * this[1, 1] + value.z * this[1, 2] + value.w * this[1, 3]).toFloat()
        result.z = (value.x * this[2, 0] + value.y * this[2, 1] + value.z * this[2, 2] + value.w * this[2, 3]).toFloat()
        result.w = (value.x * this[3, 0] + value.y * this[3, 1] + value.z * this[3, 2] + value.w * this[3, 3]).toFloat()
        return result
    }

    fun transform(value: MutableDouble3, w: Double = 1.0): MutableDouble3 {
        val x = value.x * this[0, 0] + value.y * this[0, 1] + value.z * this[0, 2] + w * this[0, 3]
        val y = value.x * this[1, 0] + value.y * this[1, 1] + value.z * this[1, 2] + w * this[1, 3]
        val z = value.x * this[2, 0] + value.y * this[2, 1] + value.z * this[2, 2] + w * this[2, 3]
        return value.set(x, y, z)
    }

    fun transform(value: Double3, w: Double = 1.0, result: MutableDouble3): MutableDouble3 {
        result.x = value.x * this[0, 0] + value.y * this[0, 1] + value.z * this[0, 2] + w * this[0, 3]
        result.y = value.x * this[1, 0] + value.y * this[1, 1] + value.z * this[1, 2] + w * this[1, 3]
        result.z = value.x * this[2, 0] + value.y * this[2, 1] + value.z * this[2, 2] + w * this[2, 3]
        return result
    }

    fun transform(value: MutableDouble4): MutableDouble4 {
        val x = value.x * this[0, 0] + value.y * this[0, 1] + value.z * this[0, 2] + value.w * this[0, 3]
        val y = value.x * this[1, 0] + value.y * this[1, 1] + value.z * this[1, 2] + value.w * this[1, 3]
        val z = value.x * this[2, 0] + value.y * this[2, 1] + value.z * this[2, 2] + value.w * this[2, 3]
        val w = value.x * this[3, 0] + value.y * this[3, 1] + value.z * this[3, 2] + value.w * this[3, 3]
        return value.set(x, y, z, w)
    }

    fun transform(value: Double4, result: MutableDouble4): MutableDouble4 {
        result.x = value.x * this[0, 0] + value.y * this[0, 1] + value.z * this[0, 2] + value.w * this[0, 3]
        result.y = value.x * this[1, 0] + value.y * this[1, 1] + value.z * this[1, 2] + value.w * this[1, 3]
        result.z = value.x * this[2, 0] + value.y * this[2, 1] + value.z * this[2, 2] + value.w * this[2, 3]
        result.w = value.x * this[3, 0] + value.y * this[3, 1] + value.z * this[3, 2] + value.w * this[3, 3]
        return result
    }

    fun toList() = listOf(
        matrix[offset + 0], matrix[offset + 1], matrix[offset + 2], matrix[offset + 3],
        matrix[offset + 4], matrix[offset + 5], matrix[offset + 6], matrix[offset + 7],
        matrix[offset + 8], matrix[offset + 9], matrix[offset + 10], matrix[offset + 11],
        matrix[offset + 12], matrix[offset + 13], matrix[offset + 14], matrix[offset + 15],
    )
}

/**
 * @author Kevin Ludwig
 */
class Double4x4Stack(
    val stackSize: Int = DefaultStackSize
) : Double4x4() {
    companion object {
        const val DefaultStackSize = 32
    }

    var stackIndex = 0
        private set(value) {
            field = value
            offset = value * 16
        }

    init {
        matrix = DoubleArray(16 * stackSize)
        setIdentity()
    }

    fun push() = apply {
        require(stackIndex < stackSize)
        repeat(16) { matrix[offset + 16 + it] = matrix[offset + it] }
        stackIndex++
    }

    fun pop() = apply {
        require(stackIndex > 0)
        stackIndex--
    }

    fun reset() = apply {
        stackIndex = 0
        setIdentity()
    }
}