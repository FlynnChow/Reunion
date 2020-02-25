package com.example.reunion.customize

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.common.FirebaseVisionPoint
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import kotlinx.android.synthetic.main.activity_camera.*
import java.nio.ByteBuffer
import java.util.concurrent.TimeUnit

class FaceAnalyzer(private val option: FirebaseVisionFaceDetectorOptions,private var listener:(ArrayList<FirebaseVisionPoint>,Bitmap)->Unit):ImageAnalysis.Analyzer {
    private var lastTime = 0L
    var cycleTime = 1L
    @SuppressLint("UnsafeExperimentalUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastTime >= TimeUnit.SECONDS.toMillis(cycleTime) ){
            val mediaImage = imageProxy.image
            Log.d("测试：","${mediaImage?.width}  ${mediaImage?.height}}")
            val rotation = imageProxy.imageInfo.rotationDegrees
            val imageRotation = rotation.degreesToFirebaseRotation()
            if (mediaImage != null){
                val image:FirebaseVisionImage = FirebaseVisionImage.fromMediaImage(mediaImage,imageRotation)
                val detector = FirebaseVision.getInstance().getVisionFaceDetector(option)
                val result = detector.detectInImage(image)
                    .addOnSuccessListener {
                        for (face in it){
                            val faceContour = face.getContour(FirebaseVisionFaceContour.FACE).points
                            listener.invoke(faceContour as ArrayList<FirebaseVisionPoint>,image.bitmap)
                            Log.d("测试：","${image.bitmap.width}  ${image.bitmap.height}}")
                        }
                    }
                    .addOnFailureListener {

                    }
            }
        }
        imageProxy.close()
    }

    private fun Int.degreesToFirebaseRotation():Int{
        return when(this){
            0->  FirebaseVisionImageMetadata.ROTATION_0
            90-> FirebaseVisionImageMetadata.ROTATION_90
            180-> FirebaseVisionImageMetadata.ROTATION_180
            270-> FirebaseVisionImageMetadata.ROTATION_270
            else->throw Exception("Rotation must be 0, 90, 180, or 270.")
        }
    }



    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()    // Rewind the buffer to zero
        val data = ByteArray(remaining())
        get(data)   // Copy the buffer into a byte array
        return data // Return the byte array
    }
}