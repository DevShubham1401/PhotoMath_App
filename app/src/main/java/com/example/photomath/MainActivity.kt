    //package com.example.photomath
    //
    //import android.Manifest
    //import android.content.pm.PackageManager
    //import android.os.Bundle
    //import android.speech.tts.TextToSpeech
    //import android.util.Log
    //import android.webkit.WebView
    //import android.widget.Button
    //import android.widget.TextView
    //import android.widget.Toast
    //import androidx.activity.result.contract.ActivityResultContracts
    //import androidx.appcompat.app.AppCompatActivity
    //import androidx.camera.core.*
    //import androidx.camera.lifecycle.ProcessCameraProvider
    //import androidx.camera.view.PreviewView
    //import androidx.core.content.ContextCompat
    //import androidx.core.net.toUri
    //import com.google.mlkit.vision.common.InputImage
    //import com.google.mlkit.vision.text.TextRecognition
    //import com.google.mlkit.vision.text.latin.TextRecognizerOptions
    //import kotlinx.coroutines.CoroutineScope
    //import kotlinx.coroutines.Dispatchers
    //import kotlinx.coroutines.launch
    //import okhttp3.OkHttpClient
    //import okhttp3.Request
    //import org.json.JSONArray
    //import org.json.JSONObject
    //import java.io.File
    //import java.util.Locale
    //
    //class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    //
    //    private lateinit var cameraPreview: PreviewView
    //    private lateinit var captureButton: Button
    //    private lateinit var extractedEquation: TextView
    //    private lateinit var solutionView: WebView
    //    private lateinit var voiceButton: Button
    //
    //    private var imageCapture: ImageCapture? = null
    //    private lateinit var textToSpeech: TextToSpeech
    //
    //    // Wolfram Alpha API
    //    private val WOLFRAM_APP_ID = "WGUQY3-55PXQ32W8R"
    //    private val WOLFRAM_BASE_URL = "http://api.wolframalpha.com/v2/query"
    //
    //    override fun onCreate(savedInstanceState: Bundle?) {
    //        super.onCreate(savedInstanceState)
    //        setContentView(R.layout.activity_main)
    //
    //        cameraPreview = findViewById(R.id.camera_preview)
    //        captureButton = findViewById(R.id.capture_button)
    //        extractedEquation = findViewById(R.id.extracted_equation) as TextView
    //        solutionView = findViewById(R.id.solution_view)
    //        voiceButton = findViewById(R.id.voice_button)
    //
    //        textToSpeech = TextToSpeech(this, this)
    //
    //        solutionView.evaluateJavascript("(function() { return document.body.innerText; })();") { text ->
    //            extractedEquation.text = text
    //        }
    //
    //
    //        if (allPermissionsGranted()) {
    //            startCamera()
    //        } else {
    //            requestPermissions.launch(arrayOf(Manifest.permission.CAMERA))
    //        }
    //
    //        captureButton.setOnClickListener { takePhoto() }
    //
    //        voiceButton.setOnClickListener {
    //            solutionView.evaluateJavascript("(function() { return document.body.innerText; })();") { solutionText ->
    //                textToSpeech.speak(solutionText, TextToSpeech.QUEUE_FLUSH, null, null)
    //            }
    //        }
    //    }
    //
    //    private fun startCamera() {
    //        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
    //        cameraProviderFuture.addListener({
    //            val cameraProvider = cameraProviderFuture.get()
    //            val preview = Preview.Builder().build().also {
    //                it.setSurfaceProvider(cameraPreview.surfaceProvider)
    //            }
    //            imageCapture = ImageCapture.Builder().build()
    //            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    //
    //            try {
    //                cameraProvider.unbindAll()
    //                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
    //            } catch (e: Exception) {
    //                Log.e("CameraX", "Use case binding failed", e)
    //            }
    //        }, ContextCompat.getMainExecutor(this))
    //    }
    //
    //    private fun takePhoto() {
    //        val imageCapture = imageCapture ?: return
    //        val photoFile = File(externalMediaDirs.firstOrNull(), "${System.currentTimeMillis()}.jpg")
    //        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
    //
    //        imageCapture.takePicture(
    //            outputOptions,
    //            ContextCompat.getMainExecutor(this),
    //            object : ImageCapture.OnImageSavedCallback {
    //                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
    //                    val image = InputImage.fromFilePath(this@MainActivity, photoFile.toUri())
    //                    recognizeTextFromImage(image)
    //                }
    //
    //                override fun onError(exc: ImageCaptureException) {
    //                    Log.e("CameraX", "Photo capture failed: ${exc.message}", exc)
    //                }
    //            }
    //        )
    //    }
    //
    //    private fun recognizeTextFromImage(image: InputImage) {
    //        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    //        recognizer.process(image)
    //            .addOnSuccessListener { visionText ->
    //                val extractedText = visionText.text
    //                extractedEquation.text = "Extracted Equation: $extractedText"
    //                solveMathProblem(extractedText)
    //            }
    //            .addOnFailureListener { e -> Log.e("OCR", "Text recognition failed", e) }
    //    }
    //
    //    private fun solveMathProblem(equation: String) {
    //        CoroutineScope(Dispatchers.IO).launch {
    //            try {
    //                Log.d("WolframAPI", "Solving equation: $equation")
    //                val solution = fetchSolutionFromWolframAlpha(equation)
    //                Log.d("WolframAPI", "Solution: $solution")
    //                runOnUiThread { displaySolution(solution) }
    //            } catch (e: Exception) {
    //                Log.e("WolframAPI", "Failed to solve equation", e)
    //                runOnUiThread { displaySolution("Error: Could not retrieve solution") }
    //            }
    //        }
    //    }
    //
    //    private fun fetchSolutionFromWolframAlpha(query: String): String {
    //        val client = OkHttpClient()
    //        val url = "$WOLFRAM_BASE_URL?input=${query.replace(" ", "+")}&format=plaintext&output=JSON&appid=$WOLFRAM_APP_ID&podstate=Step-by-step%20solution"
    //
    //        val request = Request.Builder().url(url).build()
    //
    //        try {
    //            client.newCall(request).execute().use { response ->
    //                if (!response.isSuccessful || response.body == null) {
    //                    Log.e("WolframAPI", "Unsuccessful response: ${response.code}")
    //                    return "Error: Unable to get a response"
    //                }
    //
    //                val jsonResponse = response.body!!.string()
    //                Log.d("WolframAPI", "Response: $jsonResponse")
    //                return parseSolution(jsonResponse)
    //            }
    //        } catch (e: Exception) {
    //            Log.e("WolframAPI", "Network error", e)
    //            return "Error: Network error"
    //        }
    //    }
    //
    //    private fun parseSolution(jsonResponse: String): String {
    //        return try {
    //            val jsonObject = JSONObject(jsonResponse)
    //            val queryResult = jsonObject.getJSONObject("queryresult")
    //            if (!queryResult.getBoolean("success")) {
    //                return "Error: Query not successful"
    //            }
    //
    //            val pods: JSONArray = queryResult.getJSONArray("pods")
    //            for (i in 0 until pods.length()) {
    //                val pod = pods.getJSONObject(i)
    //                if (pod.getString("title").contains("Solution")) {
    //                    return pod.getJSONArray("subpods").getJSONObject(0).getString("plaintext")
    //                }
    //            }
    //            "Solution not found"
    //        } catch (e: Exception) {
    //            Log.e("WolframAPI", "Parsing error", e)
    //            "Error: Parsing failed"
    //        }
    //    }
    //
    //    private fun displaySolution(solution: String) {
    //        solutionView.settings.javaScriptEnabled = true
    //        solutionView.loadDataWithBaseURL(
    //            null,
    //            "<html><head><script type='text/javascript' async src='https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.7/MathJax.js?config=TeX-MML-AM_CHTML'></script></head><body>$solution</body></html>",
    //            "text/html",
    //            "UTF-8",
    //            null
    //        )
    //    }
    //
    //    private fun allPermissionsGranted() = arrayOf(Manifest.permission.CAMERA).all {
    //        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    //    }
    //
    //    private val requestPermissions =
    //        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
    //            if (permissions[Manifest.permission.CAMERA] == true) {
    //                startCamera()
    //            } else {
    //                Toast.makeText(this, "Camera permission denied. Enable it in settings.", Toast.LENGTH_LONG).show()
    //            }
    //        }
    //
    //    override fun onInit(status: Int) {
    //        if (status == TextToSpeech.SUCCESS) {
    //            textToSpeech.language = Locale.US
    //        } else {
    //            Log.e("TTS", "Initialization failed")
    //        }
    //    }
    //
    //    override fun onDestroy() {
    //        super.onDestroy()
    //        textToSpeech.stop()
    //        textToSpeech.shutdown()
    //    }
    //}




        package com.example.photomath

        import android.Manifest
        import android.content.pm.PackageManager
        import android.os.Bundle
        import android.speech.tts.TextToSpeech
        import android.util.Log
        import android.webkit.WebView
        import android.widget.Button
        import android.widget.TextView
        import android.widget.Toast
        import androidx.activity.result.contract.ActivityResultContracts
        import androidx.appcompat.app.AppCompatActivity
        import androidx.camera.core.*
        import androidx.camera.lifecycle.ProcessCameraProvider
        import androidx.camera.view.PreviewView
        import androidx.core.content.ContextCompat
        import androidx.core.net.toUri
        import com.google.mlkit.vision.common.InputImage
        import com.google.mlkit.vision.text.TextRecognition
        import com.google.mlkit.vision.text.latin.TextRecognizerOptions
        import kotlinx.coroutines.CoroutineScope
        import kotlinx.coroutines.Dispatchers
        import kotlinx.coroutines.launch
        import okhttp3.OkHttpClient
        import okhttp3.Request
        import org.json.JSONArray
        import org.json.JSONObject
        import java.io.File
        import java.util.Locale

        class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

            private lateinit var cameraPreview: PreviewView
            private lateinit var captureButton: Button
            private lateinit var extractedEquation: TextView
            private lateinit var solutionView: WebView
            private lateinit var voiceButton: Button

            private var imageCapture: ImageCapture? = null
            private lateinit var textToSpeech: TextToSpeech

            // Wolfram Alpha API
            private val WOLFRAM_APP_ID = "WGUQY3-55PXQ32W8R"
            private val WOLFRAM_BASE_URL = "http://api.wolframalpha.com/v2/query"

            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_main)

                cameraPreview = findViewById(R.id.camera_preview)
                captureButton = findViewById(R.id.capture_button)
                extractedEquation = findViewById(R.id.extracted_equation)
                solutionView = findViewById(R.id.solution_view)
                voiceButton = findViewById(R.id.voice_button)

                textToSpeech = TextToSpeech(this, this)

                if (allPermissionsGranted()) {
                    startCamera()
                } else {
                    requestPermissions.launch(arrayOf(Manifest.permission.CAMERA))
                }

                captureButton.setOnClickListener { takePhoto() }

                voiceButton.setOnClickListener {
                    solutionView.evaluateJavascript("(function() { return document.body.innerText; })();") { solutionText ->
                        textToSpeech.speak(solutionText, TextToSpeech.QUEUE_FLUSH, null, null)
                    }
                }
            }

            private fun startCamera() {
                val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(cameraPreview.surfaceProvider)
                    }
                    imageCapture = ImageCapture.Builder().build()
                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
                    } catch (e: Exception) {
                        Log.e("CameraX", "Use case binding failed", e)
                    }
                }, ContextCompat.getMainExecutor(this))
            }

            private fun takePhoto() {
                val imageCapture = imageCapture ?: return
                val photoFile = File(externalMediaDirs.firstOrNull(), "${System.currentTimeMillis()}.jpg")
                val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                imageCapture.takePicture(
                    outputOptions,
                    ContextCompat.getMainExecutor(this),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                            val image = InputImage.fromFilePath(this@MainActivity, photoFile.toUri())
                            recognizeTextFromImage(image)
                        }

                        override fun onError(exc: ImageCaptureException) {
                            Log.e("CameraX", "Photo capture failed: ${exc.message}", exc)
                        }
                    }
                )
            }

            private fun recognizeTextFromImage(image: InputImage) {
                val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                recognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        val extractedText = visionText.text
                        extractedEquation.text = "Extracted Equation: $extractedText"
                        solveMathProblem(extractedText)
                    }
                    .addOnFailureListener { e -> Log.e("OCR", "Text recognition failed", e) }
            }

            private fun solveMathProblem(equation: String) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        Log.d("WolframAPI", "Solving equation: $equation")
                        val solution = fetchSolutionFromWolframAlpha(equation)
                        Log.d("WolframAPI", "Solution: $solution")
                        runOnUiThread { displaySolution(solution) }
                    } catch (e: Exception) {
                        Log.e("WolframAPI", "Failed to solve equation", e)
                        runOnUiThread { displaySolution("Error: Could not retrieve solution") }
                    }
                }
            }

            private fun fetchSolutionFromWolframAlpha(query: String): String {
                val client = OkHttpClient()
                val url = "$WOLFRAM_BASE_URL?input=${query.replace(" ", "+")}&format=plaintext&output=JSON&appid=$WOLFRAM_APP_ID&podstate=Step-by-step%20solution"

                val request = Request.Builder().url(url).build()

                try {
                    client.newCall(request).execute().use { response ->
                        if (!response.isSuccessful || response.body == null) {
                            Log.e("WolframAPI", "Unsuccessful response: ${response.code}")
                            return "Error: Unable to get a response"
                        }

                        val jsonResponse = response.body!!.string()
                        Log.d("WolframAPI", "Response: $jsonResponse")
                        return parseSolution(jsonResponse)
                    }
                } catch (e: Exception) {
                    Log.e("WolframAPI", "Network error", e)
                    return "Error: Network error"
                }
            }

            private fun parseSolution(jsonResponse: String): String {
                return try {
                    val jsonObject = JSONObject(jsonResponse)
                    val queryResult = jsonObject.getJSONObject("queryresult")
                    if (!queryResult.getBoolean("success")) {
                        return "Error: Query not successful"
                    }

                    val pods: JSONArray = queryResult.getJSONArray("pods")
                    for (i in 0 until pods.length()) {
                        val pod = pods.getJSONObject(i)
                        if (pod.getString("title").contains("Solution")) {
                            return pod.getJSONArray("subpods").getJSONObject(0).getString("plaintext")
                        }
                    }
                    "Solution not found"
                } catch (e: Exception) {
                    Log.e("WolframAPI", "Parsing error", e)
                    "Error: Parsing failed"
                }
            }

            private fun displaySolution(solution: String) {
                solutionView.settings.javaScriptEnabled = true
                solutionView.loadDataWithBaseURL(
                    null,
                    "<html><head><script type='text/javascript' async src='https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.7/MathJax.js?config=TeX-MML-AM_CHTML'></script></head><body>$solution</body></html>",
                    "text/html",
                    "UTF-8",
                    null
                )
            }

            private fun allPermissionsGranted() = arrayOf(Manifest.permission.CAMERA).all {
                ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
            }

            private val requestPermissions =
                registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                    if (permissions[Manifest.permission.CAMERA] == true) {
                        startCamera()
                    } else {
                        Toast.makeText(
                            this,
                            "Camera permission denied. Enable it in settings.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

            override fun onInit(status: Int) {
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeech.language = Locale.US
                } else {
                    Log.e("TTS", "Initialization failed")
                }
            }

            override fun onDestroy() {
                super.onDestroy()
                textToSpeech.stop()
                textToSpeech.shutdown()
            }
        }