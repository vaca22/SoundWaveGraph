package com.viatom.soundwavegraph

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import com.viatom.ecgfilter.EcgFilter
import com.viatom.soundwavegraph.bean.Er2Draw
import com.viatom.soundwavegraph.view.WaveView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class MainActivity : AppCompatActivity() {
    lateinit var waveView: WaveView
    lateinit var audioRecord: AudioRecord
    var currentUpdateIndex = 0
    val dataScope = CoroutineScope(Dispatchers.IO)
    val sampleHz=8000
    val ff=FloatArray(4){
        0f
    }
    val g = FloatArray(4)
    var gIndex = 0;
    var nn=0
    val waveDataX = LinkedList<Float>()





    val er2Graph = MutableLiveData<Er2Draw>()




    inner class DrawTask() : TimerTask() {
        override fun run() {

            try {
                do {
                    val gx = waveDataX.poll()
                    if (gx == null) {
                        return
                    } else {
                        g[gIndex] = gx
                    }
                    gIndex++
                } while (gIndex < 4);
                gIndex = 0;
                er2Graph.postValue(Er2Draw(g))
            }catch (e:java.lang.Exception){
                waveDataX.clear()
                gIndex=0;
            }


        }
    }
    val drawTask=DrawTask()

    @SuppressLint("MissingPermission")
    fun record(){
        val minBufferInByte=AudioRecord.getMinBufferSize(sampleHz, AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT)
        val min2=minBufferInByte/2
        Log.e("huhu",minBufferInByte.toString())
         audioRecord=  AudioRecord(MediaRecorder.AudioSource.MIC,
            sampleHz,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            minBufferInByte)
        dataScope.launch {
            while (true){
                val sampleData = ShortArray(min2)
                val size = audioRecord.read(sampleData, 0, min2)



                if(size>0){
                    do {
                        val dd=sampleData[nn].toFloat()/5000
                        val doubleArray = EcgFilter.filter(dd.toDouble(), reset = false)
                        doubleArray?.let { ga ->
                            if (ga.isNotEmpty()) {

                                for (j in ga) {
                                    val xcv = j.toFloat()
                                    waveDataX.offer(xcv)
                                }
                            }
                        }
                        nn+=64
                    }while (nn<size)
                    nn=nn-size
                }



            }
        }
        audioRecord.startRecording()
    }





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        waveView=findViewById(R.id.fukc)
        waveView.disp=true
        val requestPhotoPermission= registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            val grantedList = it.filterValues { it }.mapNotNull { it.key }
            val allGranted = grantedList.size == it.size
            if(allGranted){
               record()
            }
        }


       if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
           requestPhotoPermission.launch( arrayOf(Manifest.permission.RECORD_AUDIO))
        }else{
            record()
       }



        er2Graph.observe(this, {

            for (k in 0 until 4) {
                waveView.data[currentUpdateIndex] = (it.data[k] * 74.283167f*2).toInt()
                currentUpdateIndex++
                if (currentUpdateIndex >= 500) {
                    currentUpdateIndex -= 500
                }
            }

           waveView.currentHead = currentUpdateIndex - 1
            var t = currentUpdateIndex +waveView.headLen
            if (t > waveView.drawSize - 1) {
                t -= waveView.drawSize
            }
            waveView.currentTail = t
            waveView.invalidate()


        })


        Timer().schedule(drawTask,Date(),32)

    }
}