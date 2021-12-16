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
import com.viatom.soundwavegraph.view.WaveView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    lateinit var waveView: WaveView
    lateinit var audioRecord: AudioRecord
    var currentUpdateIndex = 0
    val dataScope = CoroutineScope(Dispatchers.IO)
    val sampleHz=44100
    val ff=FloatArray(4){
        0f
    }
    var nn=0

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
                        val dd=sampleData[nn]
                        waveView.data[currentUpdateIndex] = (dd.toFloat() /30).toInt()
                        currentUpdateIndex++
                        if (currentUpdateIndex >= 500) {
                            currentUpdateIndex -= 500
                        }
                        nn+=64
                    }while (nn<size)
                    nn=nn-size


                    waveView.currentHead = currentUpdateIndex - 1
                    var t = currentUpdateIndex + waveView.headLen
                    if (t > waveView.drawSize - 1) {
                        t -=waveView.drawSize
                    }
                    waveView.currentTail = t
                    waveView.disp=true
                    waveView.invalidate()
                }



            }
        }
        audioRecord.startRecording()
    }





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        waveView=findViewById(R.id.fukc)
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






    }
}