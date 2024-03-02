package com.example.arendainstrumenta.fragment.shopping

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.arendainstrumenta.R
import java.io.File

//class SearchFragment : Fragment() {
//
//    private lateinit var captureIV: ImageView
//    private lateinit var imageUrl: Uri
//
//    private val captureImage = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
//        if (success) {
//            captureIV.setImageURI(imageUrl)
//        }
//    }
//
//    private val selectImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
//        uri?.let {
//            captureIV.setImageURI(it)
//        }
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return inflater.inflate(R.layout.fragment_search, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        imageUrl = createImageUri()
//        captureIV = view.findViewById(R.id.captureImageView)
//        val captureImgBtn = view.findViewById<Button>(R.id.captureImgBtn)
//        captureImgBtn.setOnClickListener {
//            captureImage.launch(imageUrl)
//        }
//
//        val selectImgBtn = view.findViewById<Button>(R.id.selectImgBtn)
//        selectImgBtn.setOnClickListener {
//            selectImage.launch("image/*")
//        }
//    }
//
//    private fun createImageUri(): Uri {
//        val image = File(requireContext().filesDir, "camera_photos.png")
//        return FileProvider.getUriForFile(
//            requireContext(),
//            "com.example.arendainstrumenta.fragment.shopping.FileProvider",
//            image
//        )
//    }
//
//}


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.StandardOpenOption

class SearchFragment : Fragment() {

    private lateinit var captureIV: ImageView
    private lateinit var imageUrl: Uri
    private lateinit var classifier: Interpreter

    private val captureImage = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            captureIV.setImageURI(imageUrl)
            classifyImage(imageUrl)
        }
    }

    private val selectImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            captureIV.setImageURI(it)
            classifyImage(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageUrl = createImageUri()
        captureIV = view.findViewById(R.id.captureImageView)
        val captureImgBtn = view.findViewById<Button>(R.id.captureImgBtn)
        captureImgBtn.setOnClickListener {
            captureImage.launch(imageUrl)
        }

        val selectImgBtn = view.findViewById<Button>(R.id.selectImgBtn)
        selectImgBtn.setOnClickListener {
            selectImage.launch("image/*")
        }

        // Инициализация интерпретатора TensorFlow Lite
        classifier = initializeClassifier()
    }

    private fun createImageUri(): Uri {
        val image = File(requireContext().filesDir, "camera_photos.png")
        return FileProvider.getUriForFile(
            requireContext(),
            "com.example.arendainstrumenta.fragment.shopping.FileProvider",
            image
        )
    }

    private fun initializeClassifier(): Interpreter {
        val assetManager = context?.assets
        val modelFile = assetManager?.open("converted_model.tflite")
        val byteBuffer = modelFile?.let {
            val bytes = ByteArray(it.available())
            it.read(bytes)
            ByteBuffer.allocateDirect(bytes.size).apply {
                order(ByteOrder.nativeOrder())
                put(bytes)
                position(0)
            }
        } ?: throw IllegalStateException("Model file not found or could not be read.")
        return Interpreter(byteBuffer)
    }


    private fun classifyImage(uri: Uri) {
        // Получение Bitmap из URI
        val bitmap = try {
            val inputStream = context?.contentResolver?.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            Toast.makeText(context, "Ошибка при чтении изображения", Toast.LENGTH_SHORT).show()
            return
        }

        // Проверка, что Bitmap не null
        if (bitmap == null) {
            Toast.makeText(context, "Не удалось загрузить изображение", Toast.LENGTH_SHORT).show()
            return
        }

        // Классификация изображения
        val byteBuffer = convertBitmapToByteBuffer(bitmap)
        val output = Array(1) { FloatArray(13) } // Предполагается, что модель возвращает 13 классов
        classifier.run(byteBuffer, output)
        // Обработка результатов классификации
        val maxIndex = output[0].indices.maxByOrNull { output[0][it] }
        if (maxIndex != null) {
            // Вывод результата классификации
            val className = getClassName(maxIndex)
            updateResultTextView(className)
        } else {
            Toast.makeText(context, "Не удалось классифицировать изображение", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateResultTextView(className: String) {
        val resultTextView = view?.findViewById<TextView>(R.id.resultTextView)
        resultTextView?.text = "Результат: $className"
    }



    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        // Масштабирование Bitmap до нужного размера
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)

        // Преобразование Bitmap в ByteBuffer
        val byteBuffer = ByteBuffer.allocateDirect(4 * 224 * 224 * 3)
        byteBuffer.order(ByteOrder.nativeOrder())

        val intValues = IntArray(224 * 224)
        scaledBitmap.getPixels(intValues, 0, scaledBitmap.width, 0, 0, scaledBitmap.width, scaledBitmap.height)

        var pixel = 0
        for (i in 0 until 224) {
            for (j in 0 until 224) {
                val `val` = intValues[pixel++]
                byteBuffer.putFloat(((`val` shr 16 and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                byteBuffer.putFloat(((`val` shr 8 and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                byteBuffer.putFloat(((`val` and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
            }
        }
        return byteBuffer
    }

    companion object {
        private const val IMAGE_MEAN = 128.0f
        private const val IMAGE_STD = 128.0f
    }


    private fun getClassName(index: Int?): String {
        return when (index) {
            0 -> "Дрель AEG"
            1 -> "Перфоратор AEG"
            2 -> "Дрель BOSCH"
            3 -> "Перфоратор BOSCH"
            4 -> "Пила BOSCH"
            5 -> "Дрель DeWalt"
            6 -> "Перфоратор DeWalt"
            7 -> "Дрель Makita"
            8 -> "Перфоратор Makita"
            9 -> "Пила Makita"
            10 -> "Дрель Ryobi"
            11 -> "Перфоратор Ryobi"
            12 -> "Пила Ryobi"
            else -> "Неизвестно"
        }
    }

}



//class SearchFragment : Fragment() {
//
//    private lateinit var captureIV: ImageView
//    private lateinit var imageUrl: Uri
//    private lateinit var classifier: Interpreter
//    private lateinit var rustDetector: Interpreter
//
//    private val captureImage = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
//        if (success) {
//            captureIV.setImageURI(imageUrl)
//            classifyImage(imageUrl)
//        }
//    }
//
//    private val selectImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
//        uri?.let {
//            captureIV.setImageURI(it)
//            classifyImage(it)
//        }
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return inflater.inflate(R.layout.fragment_search, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        imageUrl = createImageUri()
//        captureIV = view.findViewById(R.id.captureImageView)
//        val captureImgBtn = view.findViewById<Button>(R.id.captureImgBtn)
//        captureImgBtn.setOnClickListener {
//            captureImage.launch(imageUrl)
//        }
//
//        val selectImgBtn = view.findViewById<Button>(R.id.selectImgBtn)
//        selectImgBtn.setOnClickListener {
//            selectImage.launch("image/*")
//        }
//
//        // Инициализация интерпретатора TensorFlow Lite для классификации
//        classifier = initializeClassifier()
//
//        // Инициализация интерпретатора TensorFlow Lite для обнаружения ржавчины
//        rustDetector = initializeRustDetector()
//    }
//
//    private fun createImageUri(): Uri {
//        val image = File(requireContext().filesDir, "camera_photos.png")
//        return FileProvider.getUriForFile(
//            requireContext(),
//            "com.example.arendainstrumenta.fragment.shopping.FileProvider",
//            image
//        )
//    }
//
//    private fun initializeClassifier(): Interpreter {
//        val assetManager = context?.assets
//        val modelFile = assetManager?.open("converted_model.tflite")
//        val byteBuffer = modelFile?.let {
//            val bytes = ByteArray(it.available())
//            it.read(bytes)
//            ByteBuffer.allocateDirect(bytes.size).apply {
//                order(ByteOrder.nativeOrder())
//                put(bytes)
//                position(0)
//            }
//        } ?: throw IllegalStateException("Model file not found or could not be read.")
//        return Interpreter(byteBuffer)
//    }
//
//    private fun initializeRustDetector(): Interpreter {
//        val assetManager = context?.assets
//        val modelFile = assetManager?.open("rust.tflite")
//        val byteBuffer = modelFile?.let {
//            val bytes = ByteArray(it.available())
//            it.read(bytes)
//            ByteBuffer.allocateDirect(bytes.size).apply {
//                order(ByteOrder.nativeOrder())
//                put(bytes)
//                position(0)
//            }
//        } ?: throw IllegalStateException("Model file not found or could not be read.")
//        return Interpreter(byteBuffer)
//    }
//
//    private fun classifyImage(uri: Uri) {
//        // Получение Bitmap из URI
//        val bitmap = try {
//            val inputStream = context?.contentResolver?.openInputStream(uri)
//            BitmapFactory.decodeStream(inputStream)
//        } catch (e: FileNotFoundException) {
//            e.printStackTrace()
//            Toast.makeText(context, "Ошибка при чтении изображения", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        // Проверка, что Bitmap не null
//        if (bitmap == null) {
//            Toast.makeText(context, "Не удалось загрузить изображение", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        // Классификация изображения
//        val byteBuffer = convertBitmapToByteBuffer(bitmap)
//        val output = Array(1) { FloatArray(13) } // Предполагается, что модель возвращает 13 классов
//        classifier.run(byteBuffer, output)
//        // Обработка результатов классификации
//        val maxIndex = output[0].indices.maxByOrNull { output[0][it] }
//        if (maxIndex != null) {
//            // Вывод результата классификации
//            val className = getClassName(maxIndex)
//            updateResultTextView(className)
//        } else {
//            Toast.makeText(context, "Не удалось классифицировать изображение", Toast.LENGTH_SHORT).show()
//        }
//
//        // Обнаружение ржавчины
//        val rustOutput = detectRust(bitmap)
//        drawBoundingBoxes(bitmap, rustOutput)
//    }
//
//    private fun detectRust(bitmap: Bitmap): Array<FloatArray> {
//        // Преобразование Bitmap в ByteBuffer для модели обнаружения ржавчины
//        val byteBuffer = convertBitmapToByteBufferForRustDetection(bitmap)
//
//        // Предполагаем, что модель возвращает массив координат bounding box для каждого обнаруженного объекта
//        val output = Array(1) { FloatArray(4) } // Пример возвращаемого значения
//        rustDetector.run(byteBuffer, output)
//
//        return output
//    }
//
//    private fun convertBitmapToByteBufferForRustDetection(bitmap: Bitmap): TensorBuffer {
//        // Масштабирование Bitmap до нужного размера
//        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 1280, 1280, true)
//
//        // Создание TensorBuffer для хранения преобразованных данных
//        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 3, 1280, 1280), DataType.FLOAT32)
//
//        // Преобразование Bitmap в TensorBuffer
//        val intValues = IntArray(1280 * 1280)
//        scaledBitmap.getPixels(intValues, 0, scaledBitmap.width, 0, 0, scaledBitmap.width, scaledBitmap.height)
//        var pixel = 0
//        for (i in 0 until 1280) {
//            for (j in 0 until 1280) {
//                val `val` = intValues[pixel++]
//                inputFeature0.loadArray(floatArrayOf(((`val` shr 16 and 0xFF) - IMAGE_MEAN) / IMAGE_STD.toFloat(),
//                    ((`val` shr 8 and 0xFF) - IMAGE_MEAN) / IMAGE_STD.toFloat(),
//                    ((`val` and 0xFF) - IMAGE_MEAN) / IMAGE_STD.toFloat()),
//                    intArrayOf(0, 0, i, j))
//            }
//        }
//
//        return inputFeature0
//    }
//
//    private fun drawBoundingBoxes(bitmap: Bitmap, boundingBoxes: Array<FloatArray>) {
//        val canvas = Canvas(bitmap)
//        val paint = Paint()
//        paint.color = Color.RED
//        paint.style = Paint.Style.STROKE
//        paint.strokeWidth = 5f
//
//        for (box in boundingBoxes) {
//            val x = box[0]
//            val y = box[1]
//            val width = box[2]
//            val height = box[3]
//            canvas.drawRect(x, y, x + width, y + height, paint)
//        }
//
//        // Обновление ImageView с обводкой bounding box
//        captureIV.setImageBitmap(bitmap)
//    }
//
//
//    private fun updateResultTextView(className: String) {
//        val resultTextView = view?.findViewById<TextView>(R.id.resultTextView)
//        resultTextView?.text = "Результат: $className"
//    }
//
//    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
//        // Масштабирование Bitmap до нужного размера
//        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
//
//        // Преобразование Bitmap в ByteBuffer
//        val byteBuffer = ByteBuffer.allocateDirect(4 * 224 * 224 * 3)
//        byteBuffer.order(ByteOrder.nativeOrder())
//
//        val intValues = IntArray(224 * 224)
//        scaledBitmap.getPixels(intValues, 0, scaledBitmap.width, 0, 0, scaledBitmap.width, scaledBitmap.height)
//
//        var pixel = 0
//        for (i in 0 until 224) {
//            for (j in 0 until 224) {
//                val `val` = intValues[pixel++]
//                byteBuffer.putFloat(((`val` shr 16 and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
//                byteBuffer.putFloat(((`val` shr 8 and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
//                byteBuffer.putFloat(((`val` and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
//            }
//        }
//        return byteBuffer
//    }
//
//    companion object {
//        private const val IMAGE_MEAN = 128.0f
//        private const val IMAGE_STD = 128.0f
//    }
//
//    private fun getClassName(index: Int?): String {
//        return when (index) {
//            0 -> "Дрель AEG"
//            1 -> "Перфоратор AEG"
//            2 -> "Дрель BOSCH"
//            3 -> "Перфоратор BOSCH"
//            4 -> "Пила BOSCH"
//            5 -> "Дрель DeWalt"
//            6 -> "Перфоратор DeWalt"
//            7 -> "Дрель Makita"
//            8 -> "Перфоратор Makita"
//            9 -> "Пила Makita"
//            10 -> "Дрель Ryobi"
//            11 -> "Перфоратор Ryobi"
//            12 -> "Пила Ryobi"
//            else -> "Неизвестно"
//        }
//    }
//}



//class SearchFragment : Fragment() {
//
//    private lateinit var captureIV: ImageView
//    private lateinit var imageUrl: Uri
//    private lateinit var classifier: Interpreter
//    private lateinit var rustDetector: Interpreter
//
//    private val captureImage = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
//        if (success) {
//            captureIV.setImageURI(imageUrl)
//            onImageLoaded(imageUrl)
//        }
//    }
//
//    private val selectImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
//        uri?.let {
//            captureIV.setImageURI(it)
//            onImageLoaded(it)
//        }
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return inflater.inflate(R.layout.fragment_search, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        imageUrl = createImageUri()
//        captureIV = view.findViewById(R.id.captureImageView)
//        val captureImgBtn = view.findViewById<Button>(R.id.captureImgBtn)
//        captureImgBtn.setOnClickListener {
//            captureImage.launch(imageUrl)
//        }
//
//        val selectImgBtn = view.findViewById<Button>(R.id.selectImgBtn)
//        selectImgBtn.setOnClickListener {
//            selectImage.launch("image/*")
//        }
//
//        // Инициализация интерпретатора TensorFlow Lite для классификации
//        classifier = initializeClassifier()
//
//        // Инициализация интерпретатора TensorFlow Lite для обнаружения ржавчины
//        rustDetector = initializeRustDetector()
//    }
//
//    private fun createImageUri(): Uri {
//        val image = File(requireContext().filesDir, "camera_photos.png")
//        return FileProvider.getUriForFile(
//            requireContext(),
//            "com.example.arendainstrumenta.fragment.shopping.FileProvider",
//            image
//        )
//    }
//
//    private fun initializeClassifier(): Interpreter {
//        val assetManager = context?.assets
//        val modelFile = assetManager?.open("converted_model.tflite")
//        val byteBuffer = modelFile?.let {
//            val bytes = ByteArray(it.available())
//            it.read(bytes)
//            ByteBuffer.allocateDirect(bytes.size).apply {
//                order(ByteOrder.nativeOrder())
//                put(bytes)
//                position(0)
//            }
//        } ?: throw IllegalStateException("Model file not found or could not be read.")
//        return Interpreter(byteBuffer)
//    }
//
//    private fun initializeRustDetector(): Interpreter {
//        val assetManager = context?.assets
//        val modelFile = assetManager?.open("rust.tflite")
//        val byteBuffer = modelFile?.let {
//            val bytes = ByteArray(it.available())
//            it.read(bytes)
//            ByteBuffer.allocateDirect(bytes.size).apply {
//                order(ByteOrder.nativeOrder())
//                put(bytes)
//                position(0)
//            }
//        } ?: throw IllegalStateException("Model file not found or could not be read.")
//        return Interpreter(byteBuffer)
//    }
//
//    private fun onImageLoaded(uri: Uri) {
//        CoroutineScope(Dispatchers.IO).launch {
//            val bitmap = loadBitmapFromUri(uri)
//            val result1 = classifyImageWithModel1(bitmap)
//            val result2 = classifyImageWithModel2(bitmap)
//            withContext(Dispatchers.Main) {
//                updateUIWithResults(result1, result2)
//            }
//        }
//    }
//
//    private fun loadBitmapFromUri(uri: Uri): Bitmap {
//        return try {
//            val inputStream = context?.contentResolver?.openInputStream(uri)
//            BitmapFactory.decodeStream(inputStream)
//        } catch (e: FileNotFoundException) {
//            e.printStackTrace()
//            Toast.makeText(context, "Ошибка при чтении изображения", Toast.LENGTH_SHORT).show()
//            Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888) // Возвращаем пустое изображение в случае ошибки
//        }
//    }
//
//    private fun classifyImageWithModel1(bitmap: Bitmap): ResultModel1 {
//        // Здесь должен быть код для обработки изображения с помощью первой модели
//        // и возврата результата
//        return ResultModel1() // Пример возвращаемого значения
//    }
//
//    private fun classifyImageWithModel2(bitmap: Bitmap): ResultModel2 {
//        // Здесь должен быть код для обработки изображения с помощью второй модели
//        // и возврата результата
//        return ResultModel2() // Пример возвращаемого значения
//    }
//
//    private fun updateUIWithResults(result1: ResultModel1, result2: ResultModel2) {
//        // Здесь должен быть код для обновления пользовательского интерфейса
//        // на основе результатов обработки изображения
//    }
//
//    companion object {
//        private const val IMAGE_MEAN = 128.0f
//        private const val IMAGE_STD = 128.0f
//    }
//}









