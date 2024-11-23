package com.example.stunby.ui.profile

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.stunby.R
import com.example.stunby.databinding.FragmentHomeBinding
import com.example.stunby.databinding.FragmentProfileBinding
import com.example.stunby.ui.ViewModelFactory

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            Glide.with(this)
                .load(it.fotoUrl)
                .circleCrop()
                .into(binding.ivProfile)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi views
        tvSettings = view.findViewById(R.id.tvSettings)

        // Tambahkan logika untuk toggle submenu Settings
        tvSettings.setOnClickListener {
            toggleSubMenu()
        }
    }

    private fun toggleSubMenu() {
        // Tampilkan atau sembunyikan submenu Settings
        val isVisible = subMenuSettings.visibility == View.VISIBLE
        subMenuSettings.visibility = if (isVisible) View.GONE else View.VISIBLE
    }
}
