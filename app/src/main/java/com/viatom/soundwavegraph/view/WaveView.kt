package com.viatom.soundwavegraph.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.viatom.soundwavegraph.R


class WaveView : View {

    var canvas: Canvas? = null
    private val wavePaint = Paint()
    private val bgPaint = Paint()
    var currentHead = 0
    val headLen = 3
    var currentTail = 0
    val drawSize = 500
    var n1 = 0
    var n2 = 0

    private fun judgePoint(k: Int): Int {
        if (currentHead < currentTail) {
            if ((k > currentHead) && (k <= currentTail)) {
                return 0
            } else {
                return 1
            }
        } else {
            if ((k > currentHead) || (k < currentTail)) {
                return 0
            } else {
                return 1
            }
        }
    }

    var disp = false
    val data = IntArray(drawSize) {
        0
    }

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init()
    }

    private fun init() {
        wavePaint.apply {
            color = getColor(R.color.wave_color)
            style = Paint.Style.STROKE
            strokeWidth = 5.0f
        }

        bgPaint.apply {
            color = getColor(R.color.gray)
            style = Paint.Style.STROKE
            strokeWidth = 2.0f
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val step=width.toFloat()/500
        canvas.drawARGB(0, 0, 0, 0)
        if (disp) {
            var wavePath = Path()
            for ((index, h) in data.withIndex()) {
                n2 = judgePoint(index)
                if ((n2 == 1) && (index == data.size - 1)) {
                    canvas.drawPath(wavePath, wavePaint)
                    n1 = 0
                    break
                }
                if (n2 != n1) {
                    if (n1 > n2) {
                        canvas.drawPath(wavePath, wavePaint)
                        n1 = 0
                    } else {
                        wavePath = Path()
                        wavePath.moveTo(
                            step * index.toFloat(),
                            height / 2 - h.toFloat()
                        )
                        n1 = 1
                    }
                } else {
                    wavePath.lineTo(
                        step * index.toFloat(),
                        height / 2 - h.toFloat()
                    )
                }

            }
        }

    }


    private fun getColor(resource_id: Int): Int {
        return ContextCompat.getColor(context, resource_id)
    }
}