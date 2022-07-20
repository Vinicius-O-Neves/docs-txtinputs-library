package app.dealux.docs_txtinputs_library.customview

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import app.dealux.docs_txtinputs_library.R
import app.dealux.docs_txtinputs_library.databinding.DispatchingAgencyTextinputLayoutBinding
import app.dealux.view_animations_library.shake
import kotlin.properties.Delegates

class DispatchingAgencyItem @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding = DispatchingAgencyTextinputLayoutBinding
        .inflate(LayoutInflater.from(context), this, true)

    private var isValid: Boolean = false

    private val dispatchingAgencys: Array<String> =
        context.resources.getStringArray(R.array.dispatching_agency_values)

    private var docHintText: String by Delegates.observable(
        initialValue = context.getString(
            R.string.default_hint
        )
    ) { _, old, new ->
        if (old != new) binding.docInputLayout.hint = new
    }

    var docErrorText: String = getContext().getString(R.string.default_error_text)

    private var docCounterMinLength: Int = 5

    init {
        setLayout(attrs)
        listener()
    }

    private fun setLayout(attrs: AttributeSet?) {
        attrs?.let { attributeSet ->
            val attributes = context.obtainStyledAttributes(
                attributeSet,
                R.styleable.DispatchingAgencyItem
            )

            docCounterMinLength =
                attributes.getInt(R.styleable.DispatchingAgencyItem_customCounterMinLength, 2)
            docHintText = attributes.getString(R.styleable.DispatchingAgencyItem_customHintText)
                ?: context.resources.getString(R.string.default_hint)
            docErrorText = attributes.getString(R.styleable.DispatchingAgencyItem_customErrorText)
                ?: context.resources.getString(R.string.default_error_text)
            isValid = false

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
                binding.docInputLayout.boxStrokeColor = ContextCompat.getColor(
                    context,
                    R.color.blue
                )
                autocomplete()
            }
        }
    }

    private fun autocomplete() {
        val arrayAdapterDispatchingAgencys = ArrayAdapter(
            context,
            R.layout.layout_item_autocomplete,
            R.id.autoComplete_custom,
            dispatchingAgencys
        )
        binding.docInputEdittext.setAdapter(arrayAdapterDispatchingAgencys)
    }

    fun verify() {
        val dispatchingAgency = binding.docInputEdittext.text.toString()

        if (dispatchingAgency == "" ||
            dispatchingAgency == null ||
            dispatchingAgency !in dispatchingAgencys ||
            dispatchingAgency.length < docCounterMinLength
        ) {
            binding.docInputLayout.also {
                it.isErrorEnabled = true
                it.error = docErrorText
                it.shake(it)
            }
        }
        binding.docInputLayout.isErrorEnabled = false
        Log.d("click", dispatchingAgency)
        isValid = true
    }

}