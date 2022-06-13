package app.dealux.docs_txtinputs_library.customview.uftextinput

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import app.dealux.docs_txtinputs_library.R
import app.dealux.docs_txtinputs_library.databinding.UfTextinputLayoutBinding
import kotlin.properties.Delegates

class UFItem @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding = UfTextinputLayoutBinding
        .inflate(LayoutInflater.from(context), this, true)

    var defaultMinLength = 5
    var isValid: Boolean = false

    private val ufs: Array<String> = context.resources.getStringArray(R.array.ufs_values)


    var docHintText: String by Delegates.observable(
        initialValue = context.getString(
            R.string.default_hint
        )
    ) { _, old, new ->
        if (old != new) binding.ufInputEdittext.hint = new
    }

    var docErrorText: String = getContext().getString(R.string.default_error_text)
    var docCounterMinLength: Int = defaultMinLength

    init {
        setLayout(attrs)
        listener()
    }

    private fun listener() {
        binding.ufInputEdittext.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.docInputLayout.boxStrokeColor = ContextCompat.getColor(
                    context,
                    R.color.blue
                )
                autocomplete()
            }
        }
    }

    private fun setLayout(attrs: AttributeSet?) {
        attrs?.let { attributeSet ->
            val attributes = context.obtainStyledAttributes(
                attributeSet,
                R.styleable.UFItem
            )

            docCounterMinLength =
                attributes.getInt(R.styleable.UFItem_ufCounterMinLength, defaultMinLength)
            docHintText = attributes.getString(R.styleable.UFItem_ufHintText)
                ?: context.getString(R.string.default_hint)
            val customErrorText = attributes.getString(R.styleable.UFItem_ufErrorText)
            val defaultErrorText = context.getString(R.string.default_error_text)
            docErrorText = customErrorText ?: defaultErrorText
            isValid = false

            attributes.recycle()
        }
    }

    private fun autocomplete() {
        val arrayAdapterUfs = ArrayAdapter(
            context,
            R.layout.layout_item_autocomplete,
            R.id.autoComplete_custom,
            ufs
        )
        binding.ufInputEdittext.setAdapter(arrayAdapterUfs)
    }

    fun verifyUF(): Boolean {
        val uf = binding.ufInputEdittext.text.toString()

        if (uf == "" || uf == null || uf !in ufs) {
            binding.ufInputEdittext.error = docErrorText

            return false
        } else {
            isValid = true
        }
        return true
    }

}