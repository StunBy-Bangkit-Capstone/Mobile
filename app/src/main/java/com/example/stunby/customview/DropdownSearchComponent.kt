package com.example.stunby.customview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.textfield.TextInputLayout

class DropdownSearchComponent(
    private val context: Context,
    private val textInputLayout: TextInputLayout,
    private val items: List<String>
) {
    private val autoCompleteTextView: AppCompatAutoCompleteTextView =
        textInputLayout.editText as AppCompatAutoCompleteTextView

    fun setup() {
        // Create a custom adapter with filtering capabilities
        val adapter = CustomDropdownAdapter(context, items)
        autoCompleteTextView.setAdapter(adapter)

        // Configure dropdown behavior
        autoCompleteTextView.apply {
            // Show dropdown on focus
            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    showDropDown()
                }
            }

            // Perform filtering as user types
            doAfterTextChanged { text ->
                adapter.filter.filter(text.toString())
            }

            // Handle item selection
            setOnItemClickListener { parent, _, position, _ ->
                val selectedItem = parent.getItemAtPosition(position) as String
                setText(selectedItem)
                clearFocus()
            }
        }
    }

    // Custom adapter to handle filtering
    private class CustomDropdownAdapter(
        context: Context,
        private val originalItems: List<String>
    ) : ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, originalItems),
        Filterable {

        private var filteredItems: List<String> = originalItems

        override fun getCount(): Int = filteredItems.size

        override fun getItem(position: Int): String = filteredItems[position]

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context)
                .inflate(android.R.layout.simple_dropdown_item_1line, parent, false)

            val textView = view.findViewById<TextView>(android.R.id.text1)
            textView.text = getItem(position)

            return view
        }

        override fun getFilter(): Filter {
            return object : Filter() {
                override fun performFiltering(constraint: CharSequence?): FilterResults {
                    val results = FilterResults()
                    val filtered = if (constraint.isNullOrBlank()) {
                        originalItems
                    } else {
                        originalItems.filter {
                            it.contains(constraint.toString(), ignoreCase = true)
                        }
                    }

                    results.values = filtered
                    results.count = filtered.size
                    return results
                }

                @Suppress("UNCHECKED_CAST")
                override fun publishResults(constraint: CharSequence?, results: FilterResults) {
                    filteredItems = results.values as List<String>
                    notifyDataSetChanged()
                }
            }
        }
    }
}