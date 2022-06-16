package app.dealux.docs_txtinputs_library.customview.nationality

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import app.dealux.docs_txtinputs_library.R
import app.dealux.docs_txtinputs_library.databinding.NationalityInputLayoutBinding
import kotlin.properties.Delegates

class NationalityItem @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding = NationalityInputLayoutBinding
        .inflate(LayoutInflater.from(context), this, true)


    var isValid: Boolean = false
    private val nationalities: Array<String> = context.resources.getStringArray(R.array.nationality)
    var docHintText: String by Delegates.observable(
        initialValue = context.getString(
            R.string.default_hint
        )
    ) { _, old, new ->
        if (old != new) binding.docInputLayout.hint = new
    }
    var docErrorText: String = getContext().getString(R.string.default_error_text)
    var docCounterMinLength: Int = 5

    init {
        setLayout(attrs)
        listener()
    }

    private fun setLayout(attrs: AttributeSet?) {
        attrs?.let { attributeSet ->
            val attributes = context.obtainStyledAttributes(
                attributeSet,
                R.styleable.NationalityItem
            )

            docCounterMinLength =
                attributes.getInt(R.styleable.NationalityItem_nationalityCounterMinLength, 5)
            docHintText = attributes.getString(R.styleable.NationalityItem_nationalityHintText)
                ?: context.resources.getString(R.string.default_hint)
            docErrorText = attributes.getString(R.styleable.NationalityItem_nationalityErrorText)
                ?: context.resources.getString(R.string.default_error_text)
            isValid = false

            attributes.recycle()
        }
    }

    private fun listener() {
        binding.nationalityInputEdittext.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.docInputLayout.boxStrokeColor = ContextCompat.getColor(
                    context,
                    R.color.blue
                )
                autocomplete()
            }
        }
    }

    private fun autocomplete() {
        val arrayAdapterNationalities = ArrayAdapter(
            context,
            R.layout.layout_item_autocomplete,
            R.id.autoComplete_custom,
            nationalities
        )
        binding.nationalityInputEdittext.setAdapter(arrayAdapterNationalities)
    }

    fun verifyNationalities(): Boolean {
        val nationality = binding.nationalityInputEdittext.text.toString()

        if (nationality == "" ||
            nationality == null ||
            nationality !in nationalities ||
            nationality.length < docCounterMinLength
        ) {
            binding.docInputLayout.isErrorEnabled = true
            binding.docInputLayout.error = docErrorText

            return false
        }
        isValid = true
        return true
    }

}