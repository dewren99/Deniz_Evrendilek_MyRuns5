package com.example.deniz_evrendilek_myruns5.utils

import java.lang.Math.abs
import java.lang.Math.cos
import java.lang.Math.cosh
import java.lang.Math.sin
import java.lang.Math.sinh

/**
 * https://rosettacode.org/wiki/Fast_Fourier_transform#Kotlin
 */
class Complex(val re: Double, val im: Double) {
    operator infix fun plus(x: Complex) = Complex(re + x.re, im + x.im)
    operator infix fun minus(x: Complex) = Complex(re - x.re, im - x.im)
    operator infix fun times(x: Double) = Complex(re * x, im * x)
    operator infix fun times(x: Complex) = Complex(re * x.re - im * x.im, re * x.im + im * x.re)
    operator infix fun div(x: Double) = Complex(re / x, im / x)
    val exp: Complex by lazy { Complex(cos(im), sin(im)) * (cosh(re) + sinh(re)) }

    override fun toString() = when {
        b == "0.000" -> a
        a == "0.000" -> b + 'i'
        im > 0 -> a + " + " + b + 'i'
        else -> a + " - " + b + 'i'
    }

    private val a = "%1.3f".format(re)
    private val b = "%1.3f".format(abs(im))
}

/**
 * https://rosettacode.org/wiki/Fast_Fourier_transform#Kotlin
 */
object FFTKotlin {
    fun fft(a: Array<Complex>) = _fft(a, Complex(0.0, 2.0), 1.0)
    fun rfft(a: Array<Complex>) = _fft(a, Complex(0.0, -2.0), 2.0)

    private fun _fft(a: Array<Complex>, direction: Complex, scalar: Double): Array<Complex> =
        if (a.size == 1)
            a
        else {
            val n = a.size
            require(
                n % 2 == 0,
                { "The Cooley-Tukey FFT algorithm only works when the length of the input is even." })

            var (evens, odds) = Pair(emptyArray<Complex>(), emptyArray<Complex>())
            for (i in a.indices)
                if (i % 2 == 0) evens += a[i]
                else odds += a[i]
            evens = _fft(evens, direction, scalar)
            odds = _fft(odds, direction, scalar)

            val pairs = (0 until n / 2).map {
                val offset = (direction * (java.lang.Math.PI * it / n)).exp * odds[it] / scalar
                val base = evens[it] / scalar
                Pair(base + offset, base - offset)
            }
            var (left, right) = Pair(emptyArray<Complex>(), emptyArray<Complex>())
            for ((l, r) in pairs) {
                left += l; right += r
            }
            left + right
        }
}