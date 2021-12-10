package com.viatom.littlePu.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

import com.viatom.soundwavegraph.R


class WaveViewBackGround : View {

    interface Ga {
        fun yes(x: Int, y: Int)
    }

    fun setG(g: Ga) {
        ga = g
    }
    val co=74.283167f*2*2

    var ga: Ga? = null
    var canvas: Canvas? = null
    private val wavePaint = Paint()
    private val bgPaint = Paint()
    var currentHead = 0
    val headLen = 3
    var currentTail = 0
    val drawSize = 500
    var n1 = 0
    var n2 = 0
    private val timePaint = Paint()
    private val linePaint = Paint()
    private fun nn(k: Int): Int {
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
    var drawFra: Int = 1

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
        timePaint.apply {
            color = getColor(R.color.report_wave_time)
            textSize = 24f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

        linePaint.apply {
            color = getColor(R.color.report_wave_hint_line)
            style = Paint.Style.STROKE
            strokeWidth = 4.0f
        }

    }


    lateinit var w: Rect

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)


        canvas.drawARGB(255, 255, 255, 255)


        val xy=0.1f*co
        var nn=0f


        var km=0
        do{
            nn=km.toFloat()*xy
            canvas.drawLine(
                nn,
                0f,
                nn,
                height.toFloat(),
                bgPaint
            )
            km++
        }while (nn<=width)

        km=0
        do{
            nn=km*xy
            canvas.drawLine(
                0f,
                nn,
                width.toFloat(),
                nn,
                bgPaint
            )
            km++
        }while (nn<=height)



        val p = Path()
        val baseY = height.toFloat() * 1 / 2
//        val x1 = 25f
//        val x2 = 30f
//        p.moveTo(0f, baseY)
//        p.moveTo(30f, -co/2)
//        p.lineTo(30f, co/2)
//        p.lineTo(x1 + x2, baseY - co)
//        p.lineTo(x1 + x2, baseY)
//        p.lineTo(x1 * 2 + x2, baseY)
        canvas.drawPath(p, linePaint)
        canvas.drawLine(30f,baseY,30f,-co+baseY,linePaint)
        canvas.drawText("1mV", 35f, baseY + 35f, timePaint)


    }


    private fun drawWave(canvas: Canvas) {
        canvas.drawColor(getColor(R.color.black))
    }


    private fun getColor(resource_id: Int): Int {
        return ContextCompat.getColor(context, resource_id)
    }
}