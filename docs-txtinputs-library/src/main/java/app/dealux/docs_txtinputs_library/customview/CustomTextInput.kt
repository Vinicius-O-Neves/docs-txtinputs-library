package app.dealux.docs_txtinputs_library.customview

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import app.dealux.docs_txtinputs_library.R
import app.dealux.docs_txtinputs_library.databinding.CustomInputLayoutBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlin.properties.Delegates

class CustomTextInput @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding = CustomInputLayoutBinding
        .inflate(LayoutInflater.from(context), this, true)
    private val job = CoroutineScope(Job() + Dispatchers.Main)

    var defaultMinLength = 3
    var defaultMaxLength = 20
    var isValid: Boolean = false
    var docCounterEnable: Boolean by Delegates.observable(
        false,
    ) { _, _, new ->
        binding.docInputLayout.isCounterEnabled = new
    }
    var docCounterMaxLength: Int by Delegates.observable(
        defaultMaxLength
    ) { _, _, new ->
        binding.docInputLayout.counterMaxLength = new
    }
    var docHintText: String by Delegates.observable(
        initialValue = context.getString(
            R.string.default_hint
        )
    ) { _, old, new ->
        if (old != new) binding.docInputLayout.hint = new
    }
    var docStartDrawable: Drawable? by Delegates.observable(
        initialValue = AppCompatResources.getDrawable(
            context, R.drawable.ic_person
        )
    )
    { _, old, new ->
        if (old != new) binding.docInputLayout.startIconDrawable = new
    }
    var docErrorText: String = getContext().getString(R.string.default_error_text)
    var docCounterMinLength: Int = defaultMinLength

    init {
        setLayout(attrs)
        listener()
    }

    private fun setLayout(attrs: AttributeSet?) {
        attrs?.let { attributeSet ->
            val attributes = context.obtainStyledAttributes(
                attributeSet,
                R.styleable.CustomTextInput
            )

            docCounterEnable =
                attributes.getBoolean(R.styleable.CustomTextInput_customInputCounterEnable, false)
            docCounterMaxLength =
                attributes.getInt(R.styleable.CustomTextInput_customInputMaxLength,
                    defaultMaxLength)
            docCounterMinLength =
                attributes.getInt(R.styleable.CustomTextInput_customInputMinLength,
                    defaultMinLength)
            docHintText =
                attributes.getString(R.styleable.CustomTextInput_customInputHintText) ?: docHintText
            docErrorText =
                attributes.getString(R.styleable.CustomTextInput_customInputErrorText)
                    ?: docErrorText
            isValid = false
            docStartDrawable =
                attributes.getDrawable(R.styleable.CustomTextInput_customInputDrawableStart)

            attributes.recycle()
        }
    }

    private fun listener() {
        binding.docInputEdittext.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                job.run {
                    binding.docInputLayout.boxStrokeColor = ContextCompat.getColor(
                        context,
                        R.color.blue
                    )
                }
            } else {
                job.cancel()
            }
        }
    }

    fun verify(): Boolean {
        val input = binding.docInputEdittext.text.toString()

        if (input.length < docCounterMinLength) {
            binding.docInputLayout.isErrorEnabled = true
            binding.docInputLayout.error = docErrorText

            return false
        } else if (input.length > docCounterMaxLength) {
            binding.docInputLayout.isErrorEnabled = true
            binding.docInputLayout.error = docErrorText

            return false
        } else {
            binding.docInputLayout.isErrorEnabled = false
            isValid = true
        }
        return true
    }

}
