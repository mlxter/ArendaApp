package com.example.arendainstrumenta.fragment.shopping

import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
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
