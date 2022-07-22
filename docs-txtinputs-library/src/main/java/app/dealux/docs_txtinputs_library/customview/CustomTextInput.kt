package app.dealux.docs_txtinputs_library.customview

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import app.dealux.docs_txtinputs_library.R
import app.dealux.docs_txtinputs_library.databinding.CustomInputLayoutBinding
import app.dealux.view_animations_library.shake
import kotlinx.coroutines.*
import kotlin.properties.Delegates

class CustomTextInput @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding = CustomInputLayoutBinding
        .inflate(LayoutInflater.from(context), this, true)

    private val job = CoroutineScope(Job() + Dispatchers.Main)

    private var defaultMinLength = 3

    private var defaultMaxLength = 20

    var isValid: Boolean = false

    private var docMaxLines: Int by Delegates.observable(
        1
    ) { _, old, new ->
        if (old != new) binding.docInputEdittext.maxLines = new
    }

    private var docCounterEnable: Boolean by Delegates.observable(
        false,
    ) { _, _, new ->
        binding.docInputLayout.isCounterEnabled = new
    }

    private var docCounterMaxLength: Int by Delegates.observable(
        defaultMaxLength
    ) { _, _, new ->
        binding.docInputLayout.counterMaxLength = new
    }

    private var docHintText: String by Delegates.observable(
        initialValue = context.getString(
            R.string.default_hint
        )
    ) { _, old, new ->
        if (old != new) binding.docInputLayout.hint = new
    }

    private var docStartDrawable: Drawable? by Delegates.observable(
        initialValue = AppCompatResources.getDrawable(
            context, R.drawable.ic_person
        )
    )
    { _, old, new ->
        if (old != new) binding.docInputLayout.startIconDrawable = new
    }

    private var docMask: String? by Delegates.observable(
        initialValue = null
    ) { _, old, new ->
        if (old != new && new != null) maskCustomTextInput()
    }

    var docErrorText: String = getContext().getString(R.string.default_error_text)

    private var docCounterMinLength: Int = defaultMinLength

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
            docStartDrawable =
                attributes.getDrawable(R.styleable.CustomTextInput_customInputDrawableStart)
            docMask =
                attributes.getString(R.styleable.CustomTextInput_customInputMask)
            docMaxLines = attributes.getInt(R.styleable.CustomTextInput_customMaxLines, 1)

            attributes.recycle()
        }
    }

    fun text(): String {
        return binding.docInputEdittext.text.toString()
    }

    fun setText(text: String) {
        binding.docInputEdittext.setText(text)
    }

    private fun listener() {
        binding.docInputEdittext.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                job.launch {
                    binding.docInputLayout.boxStrokeColor = ContextCompat.getColor(
                        context,
                        R.color.blue
                    )
                    docStartDrawable!!.setTint(ContextCompat.getColor(context, R.color.blue))
                }
            } else {
                job.cancel()
                docStartDrawable!!.setTint(ContextCompat.getColor(context, R.color.black))
            }
        }
    }

    // Function that take off the mask to check
    fun unmask(s: String): String {
        return s.replace("-", "").replace("/", "").replace(".", "")
    }

    private fun maskCustomTextInput() {
        binding.docInputEdittext.addTextChangedListener(object : TextWatcher {
            var isUpdating: Boolean = false
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // Variáveis de strings
                val str = unmask(s.toString())
                var mascaraCpf = ""

                binding.docInputLayout.isErrorEnabled = false

                if (isUpdating) {
                    isUpdating = false
                    return
                }
                var i = 0
                for (m in docMask!!.toCharArray()) {
                    if (m != '#' && count > before) {
                        mascaraCpf += m
                        continue
                    }
                    try {
                        mascaraCpf += str[i]
                    } catch (e: Exception) {
                        break
                    }
                    i++
                }

                // Faz o update da string no EditText
                isUpdating = true
                binding.docInputEdittext.setText(mascaraCpf)
                binding.docInputEdittext.setSelection(mascaraCpf.length)
            }
        })
    }

    fun verify(): Boolean {
        val input = binding.docInputEdittext.text.toString()

        if (input.length < docCounterMinLength) {
            binding.docInputLayout.also {
                it.isErrorEnabled = true
                it.error = docErrorText
                it.shake(it)
            }
            return false
        } else if (input.length > docCounterMaxLength) {
            binding.docInputLayout.also {
                it.isErrorEnabled = true
                it.error = docErrorText
                it.shake(it)
            }
            return false
        } else {
            binding.docInputLayout.isErrorEnabled = false
            isValid = true
        }
        return true
    }

}
