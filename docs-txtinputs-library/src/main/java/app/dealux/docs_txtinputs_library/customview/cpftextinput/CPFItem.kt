package app.dealux.docs_txtinputs_library.customview.cpftextinput

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import app.dealux.docs_txtinputs_library.R
import app.dealux.docs_txtinputs_library.databinding.CpfTextinputLayoutBinding
import kotlinx.coroutines.*

import kotlin.properties.Delegates

class CPFItem @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding = CpfTextinputLayoutBinding
        .inflate(LayoutInflater.from(context), this, true)

    var defaultMinLength = 14
    var defaultMaxLength = 14
    var isValid: Boolean = false
    private val jobMask = CoroutineScope(Job() + Dispatchers.Main)
    var docCounterEnable: Boolean by Delegates.observable(
        false
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
                R.styleable.CPFItem
            )

            docCounterEnable = attributes.getBoolean(R.styleable.CPFItem_docCounterEnable, false)
            docCounterMaxLength =
                attributes.getInt(R.styleable.CPFItem_docCounterMaxLength, defaultMaxLength)
            docCounterMinLength =
                attributes.getInt(R.styleable.CPFItem_docCounterMinLength, defaultMinLength)
            docHintText = attributes.getString(R.styleable.CPFItem_docHintText)
                ?: context.getString(R.string.default_hint)
            val customErrorText = attributes.getString(R.styleable.CPFItem_docErrorText)
            val defaultErrorText = context.getString(R.string.default_error_text)
            docErrorText = customErrorText ?: defaultErrorText
            isValid = false

            attributes.recycle()
        }
    }

    private fun listener() {
        binding.docInputEdittext.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                jobMask.run {
                    jobMask.launch {
                        binding.docInputLayout.boxStrokeColor = ContextCompat.getColor(
                            context,
                            R.color.blue
                        )
                        maskCPF()
                    }
                }
            } else {
                jobMask.cancel()
            }
        }
    }

    // Function that take off the mask to check
    fun unmask(s: String): String {
        return s.replace("-", "").replace("/", "").replace(".", "")
    }

    // Create mask for CPF
    private fun maskCPF() {
        binding.docInputEdittext.addTextChangedListener(object : TextWatcher {
            var isUpdating: Boolean = false
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // Variáveis de strings
                val strCpf = unmask(s.toString())
                var mascaraCpf = ""
                val maskCpf = "###.###.###-##"

                binding.docInputLayout.isErrorEnabled = false

                if (isUpdating) {
                    isUpdating = false
                    return
                }
                var i = 0
                for (m in maskCpf.toCharArray()) {
                    if (m != '#' && count > before) {
                        mascaraCpf += m
                        continue
                    }
                    try {
                        mascaraCpf += strCpf[i]
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

    private fun isCpfValid(document: String): Boolean {
        if (document.isEmpty()) return false

        val numbers = document.filter { it.isDigit() }.map {
            it.toString().toInt()
        }

        //repeticao
        if (numbers.all { it == numbers[0] }) return false

        if (document.isEmpty()) return false

        if (binding.docInputEdittext.text!!.length < docCounterMaxLength) return false

        //digito 1
        val dv1 = ((0..8).sumOf { (it + 1) * numbers[it] }).rem(11).let {
            if (it >= 10) 0 else it
        }

        val dv2 = ((0..8).sumOf { it * numbers[it] }.let { (it + (dv1 * 9)).rem(11) }).let {
            if (it >= 10) 0 else it
        }

        return numbers[9] == dv1 && numbers[10] == dv2
    }

    fun verifyCpf(): Boolean {
        val cpf = unmask(binding.docInputEdittext.text.toString())
        if (!isCpfValid(cpf)) {
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