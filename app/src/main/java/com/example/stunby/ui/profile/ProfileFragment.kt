package com.example.stunby.ui.profile

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.stunby.R
import com.example.stunby.databinding.FragmentHomeBinding
import com.example.stunby.databinding.FragmentProfileBinding
import com.example.stunby.ui.ViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<ProfileViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    private lateinit var tvSettings: TextView
    private lateinit var subMenuSettings: LinearLayout

    companion object {
        fun newInstance() = ProfileFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        setupUser()



        // Inflate layout untuk fragment
        return binding.root
    }

    private fun setupUser() {

        viewModel.user.observe(viewLifecycleOwner) {
            binding.tvName.text = it.fullName
            val birthDate = it.birthDay
            val dateNow = LocalDate.now().toString()
            calculateAge(birthDate, dateNow)

            lifecycleScope.launch {
                val fotoUrl = withContext(Dispatchers.IO) {
                    it.fotoUrl
                }
                Glide.with(this@ProfileFragment)
                    .load(fotoUrl)
                    .circleCrop()
                    .into(binding.ivProfile)
            }
        }
    }

    private fun calculateAge(birthDate: String?, dateMeasure: String?) {
        if (birthDate == null || dateMeasure == null) {
            binding.tvAge.text = "Tanggal tidak valid"
            return
        }

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val birthLocalDate = LocalDate.parse(birthDate, formatter)
        val measureLocalDate = LocalDate.parse(dateMeasure, formatter)

        val years = ChronoUnit.YEARS.between(birthLocalDate, measureLocalDate).toInt()
        val months = ChronoUnit.MONTHS.between(birthLocalDate.plusYears(years.toLong()), measureLocalDate).toInt()
        val days = ChronoUnit.DAYS.between(birthLocalDate.plusYears(years.toLong()).plusMonths(months.toLong()), measureLocalDate).toInt()

        val ageString = "$years Tahun $months Bulan $days Hari"
        binding.tvAge.text = ageString
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi views
        tvSettings = view.findViewById(R.id.tvSettings)

        // Tambahkan logika untuk toggle submenu Settings
        tvSettings.setOnClickListener {
            toggleSubMenu()
        }

        binding.logout.setOnClickListener {
            viewModel.logout()
        }
    }

    private fun toggleSubMenu() {
        // Tampilkan atau sembunyikan submenu Settings
        val isVisible = subMenuSettings.visibility == View.VISIBLE
        subMenuSettings.visibility = if (isVisible) View.GONE else View.VISIBLE
    }
}
