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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    lateinit var audioRecord: AudioRecord
    val dataScope = CoroutineScope(Dispatchers.IO)

    @SuppressLint("MissingPermission")
    fun record(){
        val minBufferInByte=AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT)
        val min2=minBufferInByte/2
        Log.e("huhu",minBufferInByte.toString())
         audioRecord=  AudioRecord(MediaRecorder.AudioSource.MIC,
            8000,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            minBufferInByte)
        dataScope.launch {
            while (true){
                val sampleData = ShortArray(min2)
                val size = audioRecord.read(sampleData, 0, min2)
                if(size>0){
                    Log.e("size",size.toString())
                }
            }
        }
        audioRecord.startRecording()
    }





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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