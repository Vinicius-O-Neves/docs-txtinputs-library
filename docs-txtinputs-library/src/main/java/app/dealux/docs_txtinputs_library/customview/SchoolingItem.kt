package app.dealux.docs_txtinputs_library.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import app.dealux.docs_txtinputs_library.R
import app.dealux.docs_txtinputs_library.databinding.SchoolingTextinputLayoutBinding
import app.dealux.view_animations_library.shake
import kotlin.properties.Delegates

class SchoolingItem @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding = SchoolingTextinputLayoutBinding
        .inflate(LayoutInflater.from(context), this, true)

    private var defaultMinLength = 5

    var isValid: Boolean = false

    private val schooling: Array<String> = context.resources.getStringArray(R.array.schooling)

    private var docHintText: String by Delegates.observable(
        initialValue = context.getString(
            R.string.default_hint
        )
    ) { _, old, new ->
        if (old != new) binding.docInputLayout.hint = new
    }

    var docErrorText: String = getContext().getString(R.string.default_error_text)

    private var docCounterMinLength: Int = defaultMinLength

    init {
        setLayout(attrs)
        listener()
    }

    fun text(): String {
        return binding.schoolingInputEdittext.text.toString()
    }

    fun setText(text: String) {
        binding.schoolingInputEdittext.setText(text)
    }

    private fun setLayout(attrs: AttributeSet?) {
        attrs?.let { attributeSet ->
            val attributes = context.obtainStyledAttributes(
                attributeSet,
                R.styleable.SchoolingItem
            )

            docCounterMinLength =
                attributes.getInt(R.styleable.SchoolingItem_schoolingCounterMinLength,
                    defaultMinLength)
            docHintText = attributes.getString(R.styleable.SchoolingItem_schoolingHintText)
                ?: context.getString(R.string.default_hint)
            docErrorText = attributes.getString(R.styleable.SchoolingItem_schoolingErrorText)
                ?: context.resources.getString(R.string.default_error_text)
            isValid = false

            attributes.recycle()
        }
    }

    private fun listener() {
        binding.schoolingInputEdittext.setOnFocusChangeListener { _, hasFocus ->
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
        val arrayAdapterSchooling = ArrayAdapter(
            context,
            R.layout.layout_item_autocomplete,
            R.id.autoComplete_custom,
            schooling
        )
        binding.schoolingInputEdittext.setAdapter(arrayAdapterSchooling)
    }

    fun verify(): Boolean {
        val edSchooling = binding.schoolingInputEdittext.text.toString()

        if (edSchooling == "" ||
            edSchooling == null ||
            edSchooling !in schooling ||
            edSchooling.length < docCounterMinLength
        ) {
            binding.docInputLayout.also {
                it.isErrorEnabled = true
                it.error = docErrorText
                it.shake(it)
            }
            return false
        }
        binding.docInputLayout.isErrorEnabled = false
        isValid = true
        return true
    }

}