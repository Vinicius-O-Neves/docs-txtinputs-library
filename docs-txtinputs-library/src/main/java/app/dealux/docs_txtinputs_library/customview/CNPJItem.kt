package app.dealux.docs_txtinputs_library.customview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import app.dealux.docs_txtinputs_library.R
import app.dealux.docs_txtinputs_library.databinding.CnpjTextinputLayoutBinding
import app.dealux.view_animations_library.shake
import kotlinx.coroutines.*
import kotlin.properties.Delegates

class CNPJItem @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding = CnpjTextinputLayoutBinding
        .inflate(LayoutInflater.from(context), this, true)

    private var defaultMinLength = 14

    private var defaultMaxLength = 14

    var isValid: Boolean = false

    private val jobMask = CoroutineScope(Job() + Dispatchers.Main)

    private var docCounterEnable: Boolean by Delegates.observable(
        false
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

    var docErrorText: String = getContext().getString(R.string.default_error_text)

    private var docCounterMinLength: Int = defaultMinLength

    fun text(): String {
        return binding.cnpjInputEdittext.text.toString()
    }

    init {
        setLayout(attrs)
        listener()
    }

    private fun setLayout(attrs: AttributeSet?) {
        attrs?.let { attributeSet ->
            val attributes = context.obtainStyledAttributes(
                attributeSet,
                R.styleable.CNPJItem
            )

            docCounterEnable = attributes.getBoolean(R.styleable.CNPJItem_cnpjCounterEnable, false)
            docCounterMaxLength =
                attributes.getInt(R.styleable.CNPJItem_cnpjCounterMaxLength, defaultMaxLength)
            docCounterMinLength =
                attributes.getInt(R.styleable.CNPJItem_cnpjCounterMinLength, defaultMinLength)
            docHintText = attributes.getString(R.styleable.CNPJItem_cnpjHintText)
                ?: context.getString(R.string.default_hint)
            val customErrorText = attributes.getString(R.styleable.CNPJItem_cnpjErrorText)
            val defaultErrorText = context.getString(R.string.default_error_text)
            docErrorText = customErrorText ?: defaultErrorText
            isValid = false

            attributes.recycle()
        }
    }

    private fun listener() {
        binding.cnpjInputEdittext.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                jobMask.launch {
                    binding.docInputLayout.boxStrokeColor = ContextCompat.getColor(
                        context,
                        R.color.blue
                    )
                    binding.docInputLayout.startIconDrawable!!
                        .setTint(ContextCompat.getColor(context, R.color.blue))
                    maskCnpj()
                }
            } else {
                binding.docInputLayout.startIconDrawable!!
                    .setTint(ContextCompat.getColor(context, R.color.black))
                jobMask.cancel()
            }
        }
    }

    // Function that take off the mask to check
    fun unmask(s: String): String {
        return s.replace("-", "").replace("/", "").replace(".", "")
    }

    private fun maskCnpj() {
        binding.cnpjInputEdittext.addTextChangedListener(object : TextWatcher {
            var isUpdating: Boolean = false
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // Variáveis de strings
                val strCnpj = unmask(s.toString())
                var mascaraCnpj = ""
                val maskCnpj = "##.###.###/####-##"

                if (isUpdating) {
                    isUpdating = false
                    return
                }
                var i = 0
                for (m in maskCnpj.toCharArray()) {
                    if (m != '#' && count > before) {
                        mascaraCnpj += m
                        continue
                    }
                    try {
                        mascaraCnpj += strCnpj[i]
                    } catch (e: Exception) {
                        break
                    }
                    i++
                }

                // Faz o update da string no EditText
                isUpdating = true
                binding.cnpjInputEdittext.setText(mascaraCnpj)
                binding.cnpjInputEdittext.setSelection(mascaraCnpj.length)
            }
        })
    }

    private fun isCnpjValid(): Boolean {
        val cnpj = binding.cnpjInputEdittext.text.toString()
        val strCnpj = unmask(cnpj)

        return if (strCnpj == "" || strCnpj == null) {
            false
        } else {
            validateCNPJVerificationDigit(true, strCnpj) && validateCNPJVerificationDigit(
                false,
                strCnpj)
        }
    }

    private fun validateCNPJVerificationDigit(firstDigit: Boolean, cnpj: String): Boolean {
        val startPos = when (firstDigit) {
            true -> 11
            else -> 12
        }
        val weightOffset = when (firstDigit) {
            true -> 0
            false -> 1
        }
        val sum = (startPos downTo 0).fold(0) { acc, pos ->
            val weight = 2 + ((11 + weightOffset - pos) % 8)
            val num = cnpj[pos].toString().toInt()
            val sum = acc + (num * weight)
            sum
        }
        val expectedDigit = when (val result = sum % 11) {
            0, 1 -> 0
            else -> 11 - result
        }

        val actualDigit = cnpj[startPos + 1].toString().toInt()

        return expectedDigit == actualDigit
    }

    fun verify(): Boolean {
        if (!isCnpjValid()) {
            binding.docInputLayout.let {
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