package app.dealux.docs_txtinputs_library.customview

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

    private var defaultMinLength = 5

    var isValid: Boolean = false

    private val ufs: Array<String> = context.resources.getStringArray(R.array.ufs_values)

    private var docHintText: String by Delegates.observable(
        initialValue = context.getString(
            R.string.default_hint
        )
    ) { _, old, new ->
        if (old != new) binding.docInputLayout.hint = new
    }

    var docErrorText: String = getContext().getString(R.string.default_error_text)

    private var docCounterMinLength: Int = defaultMinLength

    fun text(): String {
        return binding.ufInputEdittext.text.toString()
    }

    init {
        setLayout(attrs)
        listener()
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
            docErrorText = attributes.getString(R.styleable.UFItem_ufErrorText)
                ?: context.resources.getString(R.string.default_error_text)
            isValid = false

            attributes.recycle()
        }
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

    private fun autocomplete() {
        val arrayAdapterUfs = ArrayAdapter(
            context,
            R.layout.layout_item_autocomplete,
            R.id.autoComplete_custom,
            ufs
        )
        binding.ufInputEdittext.setAdapter(arrayAdapterUfs)
    }

    fun verify(): Boolean {
        val uf = binding.ufInputEdittext.text.toString()

        if (uf == "" ||
            uf == null ||
            uf !in ufs ||
            uf.length < docCounterMinLength
        ) {
            binding.ufInputEdittext.error = docErrorText

            return false
        }
        isValid = true
        return true
    }

}