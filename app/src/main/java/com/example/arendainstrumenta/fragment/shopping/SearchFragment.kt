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

class SearchFragment : Fragment() {

    private lateinit var captureIV: ImageView
    private lateinit var imageUrl: Uri

    private val captureImage = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            captureIV.setImageURI(imageUrl)
        }
    }

    private val selectImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            captureIV.setImageURI(it)
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


//class SearchFragment : Fragment() {
//
//    private lateinit var captureIV: ImageView
//    private lateinit var imageUrl: Uri
//    private lateinit var imageClassifier: ActivityResultLauncher<Intent>
//    private lateinit var model: model.tflite // Замените на ваш класс модели
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
//        imageClassifier = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            if (result.resultCode == Activity.RESULT_OK) {
//                val imageUri = result.data?.data
//                imageUri?.let {
//                    val inputImage = InputImage.fromFilePath(requireContext(), it)
//                    val outputs = model.tflite.process(inputImage)
//                    val result = outputs.outputFeature0AsTensorBuffer.floatArray
//                    val maxIndex = result.indexOf(result.maxOrNull())
//                    val resultTextView = view.findViewById<TextView>(R.id.resultTextView)
//                    resultTextView?.text = "Результат: ${model.labels[maxIndex]}"
//                }
//            }
//        }
//
//        val captureImgBtn = view.findViewById<Button>(R.id.captureImgBtn)
//        captureImgBtn.setOnClickListener {
//            captureImage()
//        }
//
//        val selectImgBtn = view.findViewById<Button>(R.id.selectImgBtn)
//        selectImgBtn.setOnClickListener {
//            selectImage()
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
//    private fun captureImage() {
//        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        if (intent.resolveActivity(requireContext().packageManager) != null) {
//            val values = ContentValues()
//            values.put(MediaStore.Images.Media.TITLE, "New Picture")
//            values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera")
//            imageUrl = requireContext().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUrl)
//            imageClassifier.launch(intent)
//        }
//    }
//
//    private fun selectImage() {
//        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        imageClassifier.launch(intent)
//    }
//}




