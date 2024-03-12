package com.example.arendainstrumenta.fragment.shopping

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
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
import android.graphics.Color
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.FileNotFoundException
import java.nio.ByteBuffer
import java.nio.ByteOrder

//class SearchFragment : Fragment() {
//
//    private lateinit var captureIV: ImageView
//    private lateinit var imageUrl: Uri
//    private lateinit var classifier: Interpreter
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
//        // Инициализация интерпретатора TensorFlow Lite
//        classifier = initializeClassifier()
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
//        val modelFile = assetManager?.open("maximhyesos.tflite")
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
//            return
//        }
//
//        // Проверка, что Bitmap не null
//        if (bitmap == null) {
//            return
//        }
//
//        fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
//            // Масштабирование Bitmap до нужного размера
//            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
//
//            // Преобразование Bitmap в ByteBuffer
//            val byteBuffer = ByteBuffer.allocateDirect(4 * 224 * 224 * 3)
//            byteBuffer.order(ByteOrder.nativeOrder())
//
//            val intValues = IntArray(224 * 224)
//            scaledBitmap.getPixels(intValues, 0, scaledBitmap.width, 0, 0, scaledBitmap.width, scaledBitmap.height)
//
//            var pixel = 0
//            for (i in 0 until 224) {
//                for (j in 0 until 224) {
//                    val `val` = intValues[pixel++]
//                    byteBuffer.putFloat(((`val` shr 16 and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
//                    byteBuffer.putFloat(((`val` shr 8 and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
//                    byteBuffer.putFloat(((`val` and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
//                }
//            }
//            return byteBuffer
//        }
//
//        // Сегментация изображения
//        val byteBuffer = convertBitmapToByteBuffer(bitmap)
//        val output = Array(1) { FloatArray(224 * 224) } // Скорректировано, чтобы соответствовать форме выходных данных модели
//        classifier.run(byteBuffer, output)
//
//        // Обработка результатов сегментации
//        val mask = output[0].map { if (it >= 0.5) 255.0f else 0.0f }.toFloatArray()
//        val maskBitmap = Bitmap.createBitmap(224, 224, Bitmap.Config.ARGB_8888)
//        maskBitmap.setPixels(mask.map { (it * 255).toInt() }.toIntArray(), 0, 224, 0, 0, 224, 224)
//
//        // Накладываем маску коррозии поверх исходного изображения
//        val resultBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
//        val canvas = Canvas(resultBitmap)
//        canvas.drawBitmap(bitmap, 0f, 0f, null)
//        canvas.drawBitmap(maskBitmap, 0f, 0f, Paint().apply { colorFilter = PorterDuffColorFilter(Color.RED, PorterDuff.Mode.SRC_IN) })
//
//        // Отображаем результат
//        captureIV.post {
//            captureIV.setImageBitmap(resultBitmap)
//        }
//    }
//
//    companion object {
//        private const val IMAGE_MEAN = 128.0f
//        private const val IMAGE_STD = 128.0f
//    }
//}



class SearchFragment : Fragment() {

    private lateinit var captureIV: ImageView
    private lateinit var imageUrl: Uri
    private lateinit var classifier: Interpreter
    private lateinit var corrosionClassifier: Interpreter // Добавлено для второй модели

    private val captureImage = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            captureIV.setImageURI(imageUrl)
            classifyImage(imageUrl)
            classifyWithCorrosionModel(imageUrl)
        }
    }

    private val selectImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            captureIV.setImageURI(it)
            classifyImage(it)
            classifyWithCorrosionModel(it)
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

        // Инициализация интерпретаторов TensorFlow Lite
        val (classifier, corrosionClassifier) = initializeClassifiers() // Инициализация обеих моделей
        this.classifier = classifier
        this.corrosionClassifier = corrosionClassifier
    }

    private fun createImageUri(): Uri {
        val image = File(requireContext().filesDir, "camera_photos.png")
        return FileProvider.getUriForFile(
            requireContext(),
            "com.example.arendainstrumenta.fragment.shopping.FileProvider",
            image
        )
    }

    private fun initializeClassifiers(): Pair<Interpreter, Interpreter> {
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
        val classifier = Interpreter(byteBuffer)

        val corrosionModelFile = assetManager?.open("corrosion.tflite")
        val corrosionByteBuffer = corrosionModelFile?.let {
            val bytes = ByteArray(it.available())
            it.read(bytes)
            ByteBuffer.allocateDirect(bytes.size).apply {
                order(ByteOrder.nativeOrder())
                put(bytes)
                position(0)
            }
        } ?: throw IllegalStateException("Corrosion model file not found or could not be read.")
        val corrosionClassifier = Interpreter(corrosionByteBuffer)

        return Pair(classifier, corrosionClassifier)
    }

    private fun updateResultTextView(className: String, errorMessage: String? = null) {
        val resultTextView = view?.findViewById<TextView>(R.id.resultTextView)
        if (className == "Неизвестно") {
            resultTextView?.text = "Результат: Неизвестно"
        } else {
            resultTextView?.text = "Результат: $className"
        }
        errorMessage?.let {
            resultTextView?.text = it
        }
    }

    private fun classifyImage(uri: Uri) {
        val bitmap = try {
            val inputStream = context?.contentResolver?.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            updateResultTextView("", "Ошибка при чтении изображения")
            return
        }

        if (bitmap == null) {
            updateResultTextView("", "Не удалось загрузить изображение")
            return
        }

        val byteBuffer = convertBitmapToByteBuffer(bitmap)
        val output = Array(1) { FloatArray(13) }
        classifier.run(byteBuffer, output)
        val maxIndex = output[0].indices.maxByOrNull { output[0][it] }
        if (maxIndex != null) {
            val confidence = output[0][maxIndex]
            if (confidence >= 0.95f) {
                val className = getClassName(maxIndex)
                updateResultTextView(className)
            } else {
                updateResultTextView("", "Не удалось классифицировать изображение")
            }
        } else {
            updateResultTextView("", "Не удалось классифицировать изображение")
        }
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
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

    private fun classifyWithCorrosionModel(uri: Uri) {
        val bitmap = try {
            val inputStream = context?.contentResolver?.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            updateResultTextView("", "Ошибка при чтении изображения")
            return
        }

        if (bitmap == null) {
            updateResultTextView("", "Не удалось загрузить изображение")
            return
        }

        val byteBuffer = convertBitmapToByteBufferForCorrosion(bitmap)
        val output = Array(1) { Array(224) { Array(224) { FloatArray(1) } } }
        corrosionClassifier.run(byteBuffer, output)

        // Преобразование выходных данных модели в Bitmap с выделенной ржавчиной
        val corrosionBitmap = processCorrosionOutput(output)

        // Отображение результата
        displayCorrosionResult(corrosionBitmap)
    }

    private fun convertBitmapToByteBufferForCorrosion(bitmap: Bitmap): ByteBuffer {
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
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

    private fun processCorrosionOutput(output: Array<Array<Array<FloatArray>>>): Bitmap {
        val mask = output[0]
        val width = 224
        val height = 224
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (i in 0 until height) {
            for (j in 0 until width) {
                // Предполагаем, что оценка уверенности не предоставляется моделью,
                // поэтому мы просто проверяем наличие ржавчины
                val color = if (mask[i][j][0] >= 0.95f) {
                    Color.RED // Наличие ржавчины
                } else {
                    Color.TRANSPARENT // Прозрачный фон, если ржавчины нет
                }
                bitmap.setPixel(j, i, color)
            }
        }

        return bitmap
    }

//    private fun processCorrosionOutput(output: Array<Array<Array<FloatArray>>>): Bitmap {
//        val mask = output[0]
//        val width = 224
//        val height = 224
//        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
//
//        for (i in 0 until height) {
//            for (j in 0 until width) {
//                // теперь мы обращаемся к конкретному элементу массива
//                val color = if (mask[i][j][0] >= 0.5f) {
//                    Color.RED
//                } else {
//                    Color.TRANSPARENT // прозрачный фон
//                }
//                bitmap.setPixel(j, i, color)
//            }
//        }
//
//        return bitmap
//    }


    private fun displayCorrosionResult(bitmap: Bitmap) {
        val resultImageView = view?.findViewById<ImageView>(R.id.resultImageView)
        if (resultImageView != null) {
            resultImageView.setImageBitmap(bitmap)
        } else {
            // Обработка случая, когда ImageView не найден
            Log.e("SearchFragment", "ImageView для отображения результата не найден")
        }
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


//import android.graphics.Matrix
//import android.graphics.PorterDuff
//import android.graphics.PorterDuffXfermode


//class SearchFragment : Fragment() {
//
//    private lateinit var captureIV: ImageView
//    private lateinit var imageUrl: Uri
//    private lateinit var classifier: Interpreter
//    private lateinit var corrosionClassifier: Interpreter
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
//        // Инициализация интерпретатора TensorFlow Lite
//        initializeClassifier()
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
//    private fun initializeClassifier() {
//        // Инициализация первой модели
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
//        classifier = Interpreter(byteBuffer)
//
//        // Инициализация второй модели для сегментации ржавчины
//        val corrosionModelFile = assetManager?.open("corrosion.tflite")
//        val corrosionByteBuffer = corrosionModelFile?.let {
//            val bytes = ByteArray(it.available())
//            it.read(bytes)
//            ByteBuffer.allocateDirect(bytes.size).apply {
//                order(ByteOrder.nativeOrder())
//                put(bytes)
//                position(0)
//            }
//        } ?: throw IllegalStateException("Corrosion model file not found or could not be read.")
//        corrosionClassifier = Interpreter(corrosionByteBuffer)
//    }
//
//    private fun classifyImage(uri: Uri) {
//        // Получение Bitmap из URI
//        val bitmap = try {
//            val inputStream = context?.contentResolver?.openInputStream(uri)
//            BitmapFactory.decodeStream(inputStream)
//        } catch (e: FileNotFoundException) {
//            e.printStackTrace()
//            updateResultTextView("", "Ошибка при чтении изображения")
//            return
//        }
//
//        // Проверка, что Bitmap не null
//        if (bitmap == null) {
//            updateResultTextView("", "Не удалось загрузить изображение")
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
//            // Проверка уверенности классификации
//            val confidence = output[0][maxIndex]
//            if (confidence >= 0.95f) {
//                // Вывод результата классификации
//                val className = getClassName(maxIndex)
//                updateResultTextView(className)
//            } else {
//                updateResultTextView("", "Не удалось классифицировать изображение")
//            }
//        } else {
//            updateResultTextView("", "Не удалось классифицировать изображение")
//        }
//
//        // Обработка изображения с помощью второй модели для сегментации ржавчины
//        val corrosionMask = classifyCorrosion(bitmap)
//        val overlayBitmap = overlayCorrosionMask(bitmap, corrosionMask)
//        captureIV.setImageBitmap(overlayBitmap)
//    }
//
//    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
//        // Масштабирование Bitmap до нужного размера
//        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 320, 320, true)
//
//        // Преобразование Bitmap в ByteBuffer
//        val byteBuffer = ByteBuffer.allocateDirect(4 * 320 * 320 * 3)
//        byteBuffer.order(ByteOrder.nativeOrder())
//
//        val intValues = IntArray(320 * 320)
//        scaledBitmap.getPixels(intValues, 0, scaledBitmap.width, 0, 0, scaledBitmap.width, scaledBitmap.height)
//
//        var pixel = 0
//        for (i in 0 until 320) {
//            for (j in 0 until 320) {
//                val `val` = intValues[pixel++]
//                byteBuffer.putFloat(((`val` shr 16 and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
////                byteBuffer.putFloat(((`val` shr 8 and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
////                byteBuffer.putFloat(((`val` and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
//            }
//        }
//        return byteBuffer
//    }
//
//    private fun classifyCorrosion(bitmap: Bitmap): Bitmap {
//        // Преобразование Bitmap в ByteBuffer для второй модели
//        val byteBuffer = convertBitmapToByteBuffer(bitmap)
//        val output = Array(1) { Array(bitmap.height) { FloatArray(bitmap.width) } }
//        corrosionClassifier.run(byteBuffer, output)
//
//        // Преобразование выходных данных в Bitmap маски
//        val maskBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
//        for (i in 0 until bitmap.height) {
//            for (j in 0 until bitmap.width) {
//                val color = if (output[0][i][j] > 0.5f) Color.RED else Color.TRANSPARENT
//                maskBitmap.setPixel(j, i, color)
//            }
//        }
//        return maskBitmap
//    }
//
//
//    private fun overlayCorrosionMask(originalBitmap: Bitmap, maskBitmap: Bitmap): Bitmap {
//        // Создание нового Bitmap с такими же размерами, как и исходное изображение
//        val resultBitmap = Bitmap.createBitmap(originalBitmap.width, originalBitmap.height, Bitmap.Config.ARGB_8888)
//
//        // Создание Canvas для рисования на новом Bitmap
//        val canvas = Canvas(resultBitmap)
//
//        // Рисование исходного изображения на Canvas
//        canvas.drawBitmap(originalBitmap, 0f, 0f, null)
//
//        // Рисование маски ржавчины на Canvas поверх исходного изображения
//        // Предполагается, что маска ржавчины представлена в виде черного цвета на белом фоне
//        // и должна быть прозрачной, где нет ржавчины
//        val paint = Paint()
//        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
//        canvas.drawBitmap(maskBitmap, 0f, 0f, paint)
//
//        // Возвращение результата
//        return resultBitmap
//    }
//
//    private fun updateResultTextView(className: String, errorMessage: String? = null) {
//        val resultTextView = view?.findViewById<TextView>(R.id.resultTextView)
//        // Проверяем, является ли класс неопределенным
//        if (className == "Неизвестно") {
//            // Если класс неопределен, устанавливаем соответствующее сообщение
//            resultTextView?.text = "Результат: Неизвестно"
//        } else {
//            // Если класс определен, устанавливаем его имя
//            resultTextView?.text = "Результат: $className"
//        }
//        // Если есть сообщение об ошибке, отображаем его
//        errorMessage?.let {
//            resultTextView?.text = it
//        }
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
//
//    companion object {
//        private const val IMAGE_MEAN = 128.0f
//        private const val IMAGE_STD = 128.0f
//    }
//}














