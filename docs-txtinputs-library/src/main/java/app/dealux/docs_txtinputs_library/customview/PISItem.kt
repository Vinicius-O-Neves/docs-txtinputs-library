package app.dealux.docs_txtinputs_library.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import app.dealux.docs_txtinputs_library.R
import app.dealux.docs_txtinputs_library.databinding.PisTextinputLayoutBinding
import kotlin.properties.Delegates

open class PISItem @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding = PisTextinputLayoutBinding
        .inflate(LayoutInflater.from(context), this, true)

    var defaultMinLength = 11
    var defaultMaxLength = 11

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
    }

    private fun setLayout(attrs: AttributeSet?) {
        attrs?.let { attributeSet ->
            val attributes = context.obtainStyledAttributes(
                attributeSet,
                R.styleable.PISItem
            )

            docCounterEnable = attributes.getBoolean(R.styleable.PISItem_pisCounterEnable, false)
            docCounterMaxLength =
                attributes.getInt(R.styleable.PISItem_pisCounterMaxLength, defaultMaxLength)
            docCounterMinLength =
                attributes.getInt(R.styleable.PISItem_pisCounterMinLength, defaultMinLength)
            docHintText = attributes.getString(R.styleable.PISItem_pisHintText)
                ?: context.getString(R.string.default_hint)
            val customErrorText = attributes.getString(R.styleable.PISItem_pisErrorText)
            val defaultErrorText = context.getString(R.string.default_error_text)
            docErrorText = customErrorText ?: defaultErrorText

            attributes.recycle()
        }
    }

    // Function that take off the mask to check
    fun unmask(s: String): String {
        return s.replace("-", "").replace("/", "").replace(".", "")
    }

    private fun isPisValid(document: String): Boolean {
        val numbers = document.filter { it.isDigit() }.map {
            it.toString().toInt()
        }

        val pis = mutableListOf(document.split(""))
        val list: MutableList<String> = ArrayList()
        repeat(11) {
            list += pis[0]
        }
        if (pis == list) return false

        if (document.isEmpty()) return false

        if (document.length < docCounterMaxLength) return false

        val n1 = numbers[0] * 3
        val n2 = numbers[1] * 2
        val n3 = numbers[2] * 9
        val n4 = numbers[3] * 8
        val n5 = numbers[4] * 7
        val n6 = numbers[5] * 6
        val n7 = numbers[6] * 5
        val n8 = numbers[7] * 4
        val n9 = numbers[8] * 3
        val n10 = numbers[9] * 2

        val digv = (n1 + n2 + n3 + n4 + n5 + n6 + n7 + n8 + n9 + n10).rem(11).let {
            if (it >= 10) 0 else it
        }
        val result = 11 - digv

        return numbers[10] == result
    }

    fun verifyPis() {
        val pis = unmask(binding.docInputEdittext.text.toString())
        if (!isPisValid(pis)) {
            binding.docInputLayout.isErrorEnabled = true
            binding.docInputLayout.error = docErrorText
        } else {
            binding.docInputLayout.isErrorEnabled = false
        }
    }

}