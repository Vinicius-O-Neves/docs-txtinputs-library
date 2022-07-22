package app.dealux.docs_txtinputs_library.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import app.dealux.docs_txtinputs_library.R
import app.dealux.docs_txtinputs_library.databinding.CityTextinputLayoutBinding
import app.dealux.view_animations_library.shake
import kotlin.properties.Delegates

class CityItem @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding = CityTextinputLayoutBinding
        .inflate(LayoutInflater.from(context), this, true)

    private var defaultMinLength = 3

    var isValid: Boolean = false

    private val citys: Array<String> = context.resources.getStringArray(R.array.citys_values)

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
        return binding.cityInputEdittext.text.toString()
    }

    fun setText(text: String) {
        binding.cityInputEdittext.setText(text)
    }

    init {
        setLayout(attrs)
        listener()
    }

    private fun setLayout(attrs: AttributeSet?) {
        attrs.let { attributeSet ->
            val attributes = context.obtainStyledAttributes(
                attributeSet,
                R.styleable.CityItem
            )

            docCounterMinLength =
                attributes.getInt(R.styleable.CityItem_cityCounterMinLength, defaultMinLength)
            docHintText = attributes.getString(R.styleable.CityItem_cityHintText)
                ?: context.getString(R.string.default_hint)
            docErrorText = attributes.getString(R.styleable.CityItem_cityErrorText)
                ?: context.resources.getString(R.string.default_error_text)

            attributes.recycle()
        }
    }

    private fun listener() {
        binding.cityInputEdittext.setOnFocusChangeListener { _, hasFocus ->
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
        val arrayAdapterCitys = ArrayAdapter(
            context,
            R.layout.layout_item_autocomplete,
            R.id.autoComplete_custom,
            citys
        )
        binding.cityInputEdittext.setAdapter(arrayAdapterCitys)
    }

    fun verify(): Boolean {
        val city = binding.cityInputEdittext.text.toString()

        if (city == "" ||
            city == null ||
            city !in citys ||
            city.length < docCounterMinLength
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