package app.dealux.docs_txtinputs_library.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import app.dealux.docs_txtinputs_library.R
import app.dealux.docs_txtinputs_library.databinding.StateTextinputLayoutBinding
import app.dealux.view_animations_library.shake
import kotlin.properties.Delegates

class StateItem @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding = StateTextinputLayoutBinding
        .inflate(LayoutInflater.from(context), this, true)

    private var defaultMinLength = 5

    var isValid: Boolean = false

    private val states: Array<String> = context.resources.getStringArray(R.array.states_values)

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
        return binding.stateInputEdittext.text.toString()
    }

    fun setText(text: String) {
        binding.stateInputEdittext.setText(text)
    }

    init {
        setLayout(attrs)
        listener()
    }

    private fun setLayout(attrs: AttributeSet?) {
        attrs.let { attributeSet ->
            val attributes = context.obtainStyledAttributes(
                attributeSet,
                R.styleable.StateItem
            )

            docCounterMinLength =
                attributes.getInt(R.styleable.StateItem_stateCounterMinLength, defaultMinLength)
            docHintText = attributes.getString(R.styleable.StateItem_stateHintText)
                ?: context.getString(R.string.default_hint)
            docErrorText = attributes.getString(R.styleable.StateItem_stateErrorText)
                ?: context.resources.getString(R.string.default_error_text)

            attributes.recycle()
        }
    }

    private fun listener() {
        binding.stateInputEdittext.setOnFocusChangeListener { _, hasFocus ->
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
        val arrayAdapterStates = ArrayAdapter(
            context,
            R.layout.layout_item_autocomplete,
            R.id.autoComplete_custom,
            states
        )
        binding.stateInputEdittext.setAdapter(arrayAdapterStates)
    }

    fun verify(): Boolean {
        val state = binding.stateInputEdittext.text.toString()

        if (state == "" ||
            state == null ||
            state !in states ||
            state.length < docCounterMinLength
        ) {
            binding.docInputLayout.also {
                it.isErrorEnabled = true
                it.error = docErrorText
                it.shake(it)
            }

            return false
        }
        isValid = true
        return true
    }

}