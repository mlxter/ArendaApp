package com.example.arendainstrumenta.fragment.shopping

import android.graphics.BitmapFactory
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.util.Base64
import android.util.Log
import android.widget.TextView
import androidx.core.content.FileProvider
import com.example.arendainstrumenta.R
import com.google.firebase.ktx.Firebase
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.io.File
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.tensorflow.lite.Interpreter
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

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



//class SearchFragment : Fragment() {
//
//    private lateinit var captureIV: ImageView
//    private lateinit var imageUrl: Uri
//    private lateinit var classifier: Interpreter
//    private lateinit var corrosionClassifier: Interpreter // Добавлено для второй модели
//
//    private val captureImage = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
//        if (success) {
//            captureIV.setImageURI(imageUrl)
//            classifyImage(imageUrl)
//            classifyWithCorrosionModel(imageUrl)
//        }
//    }
//
//    private val selectImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
//        uri?.let {
//            captureIV.setImageURI(it)
//            classifyImage(it)
//            classifyWithCorrosionModel(it)
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
//        // Инициализация интерпретаторов TensorFlow Lite
//        val (classifier, corrosionClassifier) = initializeClassifiers() // Инициализация обеих моделей
//        this.classifier = classifier
//        this.corrosionClassifier = corrosionClassifier
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
//    private fun initializeClassifiers(): Pair<Interpreter, Interpreter> {
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
//        val classifier = Interpreter(byteBuffer)
//
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
//        val corrosionClassifier = Interpreter(corrosionByteBuffer)
//
//        return Pair(classifier, corrosionClassifier)
//    }
//
//    private fun updateResultTextView(className: String, errorMessage: String? = null) {
//        val resultTextView = view?.findViewById<TextView>(R.id.resultTextView)
//        if (className == "Неизвестно") {
//            resultTextView?.text = "Результат: Неизвестно"
//        } else {
//            resultTextView?.text = "Результат: $className"
//        }
//        errorMessage?.let {
//            resultTextView?.text = it
//        }
//    }
//
//    private fun classifyImage(uri: Uri) {
//        val bitmap = try {
//            val inputStream = context?.contentResolver?.openInputStream(uri)
//            BitmapFactory.decodeStream(inputStream)
//        } catch (e: FileNotFoundException) {
//            e.printStackTrace()
//            updateResultTextView("", "Ошибка при чтении изображения")
//            return
//        }
//
//        if (bitmap == null) {
//            updateResultTextView("", "Не удалось загрузить изображение")
//            return
//        }
//
//        val byteBuffer = convertBitmapToByteBuffer(bitmap)
//        val output = Array(1) { FloatArray(13) }
//        classifier.run(byteBuffer, output)
//        val maxIndex = output[0].indices.maxByOrNull { output[0][it] }
//        if (maxIndex != null) {
//            val confidence = output[0][maxIndex]
//            if (confidence >= 0.95f) {
//                val className = getClassName(maxIndex)
//                updateResultTextView(className)
//            } else {
//                updateResultTextView("", "Не удалось классифицировать изображение")
//            }
//        } else {
//            updateResultTextView("", "Не удалось классифицировать изображение")
//        }
//    }
//
//    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
//        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
//        val byteBuffer = ByteBuffer.allocateDirect(4 * 224 * 224 * 3)
//        byteBuffer.order(ByteOrder.nativeOrder())
//        val intValues = IntArray(224 * 224)
//        scaledBitmap.getPixels(intValues, 0, scaledBitmap.width, 0, 0, scaledBitmap.width, scaledBitmap.height)
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
//    private fun classifyWithCorrosionModel(uri: Uri) {
//        val bitmap = try {
//            val inputStream = context?.contentResolver?.openInputStream(uri)
//            BitmapFactory.decodeStream(inputStream)
//        } catch (e: FileNotFoundException) {
//            e.printStackTrace()
//            updateResultTextView("", "Ошибка при чтении изображения")
//            return
//        }
//
//        if (bitmap == null) {
//            updateResultTextView("", "Не удалось загрузить изображение")
//            return
//        }
//
//        val byteBuffer = convertBitmapToByteBufferForCorrosion(bitmap)
//        val output = Array(1) { Array(224) { Array(224) { FloatArray(1) } } }
//        corrosionClassifier.run(byteBuffer, output)
//
//        // Преобразование выходных данных модели в Bitmap с выделенной ржавчиной
//        val corrosionBitmap = processCorrosionOutput(output)
//
//        // Отображение результата
//        displayCorrosionResult(corrosionBitmap)
//    }
//
//    private fun convertBitmapToByteBufferForCorrosion(bitmap: Bitmap): ByteBuffer {
//        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
//        val byteBuffer = ByteBuffer.allocateDirect(4 * 224 * 224 * 3)
//        byteBuffer.order(ByteOrder.nativeOrder())
//        val intValues = IntArray(224 * 224)
//        scaledBitmap.getPixels(intValues, 0, scaledBitmap.width, 0, 0, scaledBitmap.width, scaledBitmap.height)
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
//    private fun processCorrosionOutput(output: Array<Array<Array<FloatArray>>>): Bitmap {
//        val mask = output[0]
//        val width = 224
//        val height = 224
//        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
//
//        for (i in 0 until height) {
//            for (j in 0 until width) {
//                // Предполагаем, что оценка уверенности не предоставляется моделью,
//                // поэтому мы просто проверяем наличие ржавчины
//                val color = if (mask[i][j][0] >= 0.95f) {
//                    Color.RED // Наличие ржавчины
//                } else {
//                    Color.TRANSPARENT // Прозрачный фон, если ржавчины нет
//                }
//                bitmap.setPixel(j, i, color)
//            }
//        }
//
//        return bitmap
//    }
//
//
//    private fun displayCorrosionResult(bitmap: Bitmap) {
//        val resultImageView = view?.findViewById<ImageView>(R.id.resultImageView)
//        if (resultImageView != null) {
//            resultImageView.setImageBitmap(bitmap)
//        } else {
//            // Обработка случая, когда ImageView не найден
//            Log.e("SearchFragment", "ImageView для отображения результата не найден")
//        }
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
//
//    // Ваш существующий код
//
//    private val captureImage = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
//        if (success) {
//            captureIV.setImageURI(imageUrl)
//            // Отправляем изображение на сервер сразу после его загрузки
//            sendImageToServer(imageUrl)
//        }
//    }
//
//    private val selectImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
//        uri?.let {
//            captureIV.setImageURI(it)
//            // Отправляем изображение на сервер сразу после его выбора
//            sendImageToServer(it)
//        }
//    }
//
//    // Ваш существующий код
//
//    private fun sendImageToServer(uri: Uri) {
//        // Создание экземпляра Retrofit
//        val retrofit = Retrofit.Builder()
//            .baseUrl("http://127.0.0.1:8000/")
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//
//        // Создание экземпляра API
//        val apiService = retrofit.create(ApiService::class.java)
//
//        // Создание MultipartBody.Part из Uri
//        val file = File(uri.path)
//        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
//        val body = MultipartBody.Part.createFormData("photo_url", file.name, requestFile)
//
//        // Отправка изображения на сервер
//        apiService.uploadImage(body).enqueue(object : Callback<ResponseBody> {
//            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                if (response.isSuccessful) {
//                    // Обработка успешного ответа
//                    Log.d("SearchFragment", "Изображение успешно отправлено")
//                } else {
//                    // Обработка ошибки
//                    Log.e("SearchFragment", "Ошибка при отправке изображения: ${response.errorBody()}")
//                }
//            }
//
//            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                // Обработка ошибки сети
//                Log.e("SearchFragment", "Ошибка сети: ${t.message}")
//            }
//        })
//    }
//}





//class SearchFragment : Fragment() {
//
//    private lateinit var captureIV: ImageView
//    private lateinit var imageUrl: Uri
//
//    private val captureImage = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
//        if (success) {
//            captureIV.setImageURI(imageUrl)
//            sendImageToServer(imageUrl)
//        }
//    }
//
//    private val selectImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
//        uri?.let {
//            captureIV.setImageURI(it)
//            sendImageToServer(it)
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
//
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
//    private fun sendImageToServer(uri: Uri) {
//        val tempFile = uriToFile(uri) ?: return
//
//        // Кодирование изображения в base64
//        val encodedImage = Base64.encodeToString(tempFile.readBytes(), Base64.DEFAULT)
//
//        // Формирование URL с base64 изображением
//        val photoUrl = "data:image/jpeg;base64,$encodedImage"
//
//        // Логирование формируемого URL для отладки
//        Log.d("SearchFragment", "Формируемый URL: $photoUrl")
//
//        // Отправка запроса на сервер
//        val retrofit = Retrofit.Builder()
//            .baseUrl("http://1caf-95-54-230-244.ngrok-free.app/")
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//
//        val apiService = retrofit.create(ApiService::class.java)
//
//        apiService.uploadImage(photoUrl).enqueue(object : Callback<ResponseBody> {
//            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                if (response.isSuccessful) {
//                    Log.d("SearchFragment", "Изображение успешно отправлено")
//                } else {
//                    // Попытка преобразовать errorBody в строку для вывода
//                    val errorBody = response.errorBody()?.string()
//                    Log.e("SearchFragment", "Ошибка при отправке изображения: $errorBody")
//                }
//            }
//
//            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                Log.e("SearchFragment", "Ошибка сети: ${t.message}")
//            }
//        })
//    }
//
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
//    private fun uriToFile(uri: Uri): File? {
//        var inputStream: InputStream? = null
//        var outputStream: FileOutputStream? = null
//        try {
//            inputStream = requireContext().contentResolver.openInputStream(uri)
//            val tempFile = File.createTempFile("tempImage", ".jpg", requireContext().cacheDir)
//            outputStream = FileOutputStream(tempFile)
//            inputStream?.copyTo(outputStream)
//            return tempFile
//        } catch (e: Exception) {
//            e.printStackTrace()
//        } finally {
//            inputStream?.close()
//            outputStream?.close()
//        }
//        return null
//    }
//
//    interface ApiService {
//        @GET("predict")
//        fun uploadImage(@Query("photo_url") photoUrl: String): Call<ResponseBody>
//    }
//
//}

/////////////РАБОЧИЙ
//class SearchFragment : Fragment() {
//
//    private lateinit var captureIV: ImageView
//    private lateinit var imageUrl: Uri
//
//    private val captureImage = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
//        if (success) {
//            captureIV.setImageURI(imageUrl)
//            sendImageToServer(imageUrl)
//        }
//    }
//
//    private val selectImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
//        uri?.let {
//            captureIV.setImageURI(it)
//            sendImageToServer(it)
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
//
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
//    private fun sendImageToServer(uri: Uri) {
//        val storageRef = Firebase.storage.reference
//        val imageRef = storageRef.child("images/${uri.lastPathSegment}")
//
//        imageRef.putFile(uri).addOnSuccessListener {
//            imageRef.downloadUrl.addOnSuccessListener { uri ->
//                val photoUrl = uri.toString()
//                Log.d("SearchFragment", "URL изображения: $photoUrl")
//                sendPhotoUrlToServer(photoUrl)
//            }.addOnFailureListener {
//                Log.e("SearchFragment", "Ошибка при получении URL изображения", it)
//            }
//        }.addOnFailureListener {
//            Log.e("SearchFragment", "Ошибка при загрузке изображения", it)
//        }
//    }
//
//    private fun sendPhotoUrlToServer(photoUrl: String) {
//        // Отправка запроса на сервер
//        val retrofit = Retrofit.Builder()
//            .baseUrl("http://f185-95-54-231-188.ngrok-free.app/")
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//
//        val apiService = retrofit.create(ApiService::class.java)
//
//        apiService.uploadImage(photoUrl).enqueue(object : Callback<ResponseBody> {
//            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                if (response.isSuccessful) {
//                    Log.d("SearchFragment", "Изображение успешно отправлено")
//                } else {
//                    val errorBody = response.errorBody()?.string()
//                    Log.e("SearchFragment", "Ошибка при отправке изображения: $errorBody")
//                }
//            }
//
//            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                Log.e("SearchFragment", "Ошибка сети: ${t.message}")
//            }
//        })
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
//    interface ApiService {
//        @GET("predict")
//        fun uploadImage(@Query("photo_url") photoUrl: String): Call<ResponseBody>
//    }
//}



import khttp.post
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream


import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException


class SearchFragment : Fragment() {

    private lateinit var captureIV: ImageView
    private lateinit var imageUrl: Uri
    private lateinit var resultTextView: TextView // Объявление переменной resultTextView
    private lateinit var imageMiniaturesContainer: LinearLayout

    private val captureImage = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            captureIV.setImageURI(imageUrl)
            addImageToMiniatures(imageUrl)
            sendImageToServer(imageUrl)
        }
    }

    private val selectImages = registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris: List<Uri>? ->
        uris?.forEach { uri ->
            captureIV.setImageURI(uri)
            addImageToMiniatures(uri)
            sendImageToServer(uri)
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
        resultTextView = view.findViewById(R.id.resultTextView) // Инициализация resultTextView
        imageMiniaturesContainer = view.findViewById(R.id.imageMiniaturesContainer)

        val captureImgBtn = view.findViewById<Button>(R.id.captureImgBtn)
        captureImgBtn.setOnClickListener {
            captureImage.launch(imageUrl)
        }


        val selectImgBtn = view.findViewById<Button>(R.id.selectImgBtn)
        selectImgBtn.setOnClickListener {
            selectImages.launch(arrayOf("image/*"))
        }
    }


    private fun addImageToMiniatures(imageUri: Uri) {
        val imageView = ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(130, 130)
            setPadding(8, 8, 8, 8)
            setImageURI(imageUri)
        }
        imageMiniaturesContainer.addView(imageView)
    }
    private fun sendImageToServer(uri: Uri) {
        GlobalScope.launch(Dispatchers.IO) {
            val bitmap = BitmapFactory.decodeStream(requireContext().contentResolver.openInputStream(uri))
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()

            // Создаем тело запроса с изображением
            val requestBody = byteArray.toRequestBody("image/png".toMediaTypeOrNull())

            // Создаем multipart body с изображением
            val body = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "image.png", requestBody)
                .build()

            // Отправляем запрос на сервер
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("http://192.168.0.3:5000/upload")
                .post(body)
                .build()

            client.newCall(request).enqueue(object : okhttp3.Callback {
                override fun onFailure(call: okhttp3.Call, e: IOException) {
                    Log.e("SearchFragment", "Ошибка при отправке изображения: ${e.message}")
                }

                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        val jsonObject = JSONObject(responseBody)

                        // Логирование ответа сервера для диагностики
                        Log.d("SearchFragment", "Response Body: $responseBody")

                        // Проверяем, было ли обнаружено царапин
                        val scratchesFound = jsonObject.getBoolean("scratchesFound")

//                        val dustFound = jsonObject.optString("prediction", "") == "dust"

                        val corrosionFound = jsonObject.optBoolean("corrosionFound", false)
                        var resultText = ""

                        // Извлекаем информацию о брендах как строку, а не как JSONArray
                        var texts = jsonObject.optString("texts", "")
                        texts = texts.replace("[", "")
                            .replace("]", "")
                            .replace("'", "")
                            .replace("\"", "")

                        var className = jsonObject.optString("className", "") // Извлекаем className

                        // Переводим className на русский
                        className = when (className) {
                            "Chain saw" -> "Цепная пила"
                            "Circular saw" -> "Дисковая пила"
                            "Drill" -> "Дрель"
                            "Screwdriver" -> "Шуруповерт"
                            else -> className
                        }

                        // Всегда устанавливаем текст о бренде
                        resultText = "Бренд: $texts"

                        // Если обнаружены царапины, добавляем соответствующее сообщение
                        if (scratchesFound) {
                            resultText += "\nОбнаружены царапины"
                        }

                        if (corrosionFound) {
                            resultText += "\nОбнаружена коррозия"
                        }

//                        if (dustFound) {
//                            resultText += "\nОбнаружены загрязнения"
//                        }

                        // Если className не пуст, добавляем его в результат
                        if (className.isNotEmpty()) {
                            resultText += "\nИнструмент: $className"
                        }

                        activity?.runOnUiThread {
                            resultTextView.text = resultText // Используем resultTextView здесь
                            // Логирование обновленного текста TextView для диагностики
                            Log.d("SearchFragment", "Updated TextView: $resultText")
                        }
                    } else {
                        Log.e("SearchFragment", "Ошибка при отправке изображения: ${response.message}")
                    }
                }

            })
        }
    }

    private fun createImageUri(): Uri {
        val image = File(requireContext().filesDir, "camera_photos.png")
        return FileProvider.getUriForFile(
            requireContext(),
            "com.example.arendainstrumenta.fragment.shopping.FileProvider",
            image
        )
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














