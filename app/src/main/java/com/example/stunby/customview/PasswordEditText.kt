package com.example.stunby.customview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import com.example.stunby.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class PasswordEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.editTextStyle
) : TextInputEditText(context, attrs, defStyleAttr) {

    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty() && s.length < 8) {
                    (parent.parent as? TextInputLayout)?.error =
                        context.getString(R.string.password_validation)
                } else {
                    (parent.parent as? TextInputLayout)?.error = null
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
}