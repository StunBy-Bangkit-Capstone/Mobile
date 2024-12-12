package com.example.stunby.ui.scan

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import android.widget.Toast
import com.example.stunby.R
import com.example.stunby.data.remote.response.MeasureResponse
import com.example.stunby.databinding.FragmentScanBinding
import com.example.stunby.ui.ViewModelFactory
import com.example.stunby.ui.detail.DetailActivity
import com.example.stunby.utils.getImageUri
import com.example.stunby.utils.reduceFileImage
import com.example.stunby.utils.uriToFile
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.util.Calendar

class ScanFragment : Fragment() {

    private var _binding: FragmentScanBinding? = null
    private val binding get() = _binding!!
    private var currentImageUri: Uri? = null

    private val viewModel by viewModels<ScanViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScanBinding.inflate(inflater, container, false)

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCamera() }

        binding.edDate.setOnClickListener {
            showDatePicker()
        }

        binding.measurementButton.setOnClickListener { addMeasure() }


        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun addMeasure() {

        viewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, requireContext()).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")
            val levelActivity = binding.spinnerAktivitasLevel.selectedItem.toString()
            val statusAsi = if (binding.spinnerStatusAsi.selectedItem.toString()=="ASI Eksklusif") "ASI_Eksklusif" else  binding.spinnerStatusAsi.selectedItem.toString()
            val age = binding.edAge.text.toString()
            val weight = binding.edWeight.text.toString()
            val date = binding.edDate.text.toString()

            val requestLevelActivity = levelActivity.toRequestBody("text/plain".toMediaType())
            val requestStatusAsi = statusAsi.toRequestBody("text/plain".toMediaType())
            val requestAge = age.toRequestBody("text/plain".toMediaType())
            val requestWeight = weight.toRequestBody("text/plain".toMediaType())
            val requestDate = date.toRequestBody("text/plain".toMediaType())

            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "baby_photo_url",
                imageFile.name,
                requestImageFile,
            )

            lifecycleScope.launch {
                try {
                    val successResponse = viewModel.addMeasure(
                        multipartBody,
                        requestLevelActivity,
                        requestStatusAsi,
                        requestAge,
                        requestWeight,
                        requestDate
                    )
                    showToast(successResponse.message)
                    val measureId = successResponse.data.measurement?.id
                    val intent = Intent(requireContext(), DetailActivity::class.java).apply {
                        putExtra("key_id", measureId)
                        Log.d("ScanFragment", "key id: $measureId")
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    startActivity(intent)
                } catch (e: HttpException) {
                    val errorBody = e.response()?.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, MeasureResponse::class.java)
                    showToast(errorResponse.message)
                }
            }
        } ?: showToast(getString(R.string.empty_image_warning))
    }

    private fun showLoading(loading: Boolean?): Boolean? {

        binding.measurementButton.isEnabled = !loading!!
        binding.galleryButton.isEnabled = !loading
        binding.cameraButton.isEnabled = !loading
        binding.spinnerAktivitasLevel.isEnabled = !loading
        binding.spinnerStatusAsi.isEnabled = !loading
        binding.edAge.isEnabled = !loading
        binding.edWeight.isEnabled = !loading
        binding.edDate.isEnabled = !loading
        binding.progressBar.visibility = if (loading) {
            View.VISIBLE
        } else {
            View.GONE
        }
        return loading

    }


    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            // Format tanggal menjadi yyyy-MM-dd
            val formattedDate = "${selectedYear}-${(selectedMonth + 1).toString().padStart(2, '0')}-${selectedDay.toString().padStart(2, '0')}"
            binding.edDate.setText(formattedDate)

            lifecycleScope.launch {
                val age = withContext(Dispatchers.IO) {
                    viewModel.calculateAge(formattedDate)
                }
                binding.edAge.setText(age)
            }
        }, year, month, day).show()
    }


    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(requireContext())
        launcherIntentCamera.launch(currentImageUri!!)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        } else {
            currentImageUri = null
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Glide.with(this)
                .load(it)
                .into(binding.ivPreview)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
