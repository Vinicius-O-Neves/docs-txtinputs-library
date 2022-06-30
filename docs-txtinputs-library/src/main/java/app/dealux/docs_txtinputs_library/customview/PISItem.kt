package app.dealux.docs_txtinputs_library.customview

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import app.dealux.docs_txtinputs_library.R
import app.dealux.docs_txtinputs_library.databinding.PisTextinputLayoutBinding
import app.dealux.view_animations_library.shake
import kotlinx.coroutines.*
import kotlin.properties.Delegates

class PISItem @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding = PisTextinputLayoutBinding
        .inflate(LayoutInflater.from(context), this, true)
    private val job = CoroutineScope(Job() + Dispatchers.Main)

    private var defaultMinLength = 11

    private var defaultMaxLength = 11

    var isValid: Boolean = false

    private var docCounterEnable: Boolean by Delegates.observable(
        false
    ) { _, _, new ->
        binding.pisInputLayout.isCounterEnabled = new
    }

    private var docCounterMaxLength: Int by Delegates.observable(
        defaultMaxLength
    ) { _, _, new ->
        binding.pisInputLayout.counterMaxLength = new
    }

    private var docHintText: String by Delegates.observable(
        initialValue = context.getString(
            R.string.default_hint
        )
    ) { _, old, new ->
        if (old != new) binding.pisInputLayout.hint = new
    }

    var docErrorText: String = getContext().getString(R.string.default_error_text)

    private var docCounterMinLength: Int = defaultMinLength

    fun text(): String {
        return binding.pisInputEdittext.text.toString()
    }

    init {
        setLayout(attrs)
        listener()
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
            isValid = false

            attributes.recycle()
        }
    }

    private fun listener() {
        binding.pisInputEdittext.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                job.launch {
                    binding.pisInputLayout.boxStrokeColor = ContextCompat.getColor(
                        context,
                        R.color.blue
                    )
                    binding.pisInputLayout.startIconDrawable!!
                        .setTint(ContextCompat.getColor(context, R.color.blue))
                }
            } else {
                binding.pisInputLayout.startIconDrawable!!
                    .setTint(ContextCompat.getColor(context, R.color.black))
                job.cancel()
            }
        }
    }

    // Function that take off the mask to check
    private fun unmask(s: String): String {
        return s.replace("-", "").replace("/", "").replace(".", "")
    }

    private fun isPisValid(document: String): Boolean {
        if (document == "" || document.length > 11 || document.length < 11) {
            return false
        }

        val numbers = document.filter { it.isDigit() }.map {
            it.toString().toInt()
        }

        val pis = mutableListOf(document.split(""))
        val list: MutableList<String> = ArrayList()
        repeat(11) {
            list += pis[0]
        }

        if (pis == list) {
            return false
        }

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
        Log.d("pis", result.toString())
        Log.d("pis", numbers[10].toString())
        if (numbers[10] == result || (numbers[10] == 0 && result == 10 || result == 11)) {
            return true
        }
        return false
    }

    fun verify(): Boolean {
        val pis = unmask(binding.pisInputEdittext.text.toString())
        if (!isPisValid(pis)) {
            binding.pisInputLayout.also {
                it.isErrorEnabled = true
                it.error = docErrorText
                it.shake(it)
            }
            return false
        } else {
            binding.pisInputLayout.isErrorEnabled = false
            isValid = true
        }
        return true
    }

}