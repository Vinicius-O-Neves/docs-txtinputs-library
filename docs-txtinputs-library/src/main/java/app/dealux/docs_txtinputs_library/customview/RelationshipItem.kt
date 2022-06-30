package app.dealux.docs_txtinputs_library.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import app.dealux.docs_txtinputs_library.R
import app.dealux.docs_txtinputs_library.databinding.RelationshipTextinputLayoutBinding
import app.dealux.view_animations_library.shake
import kotlin.properties.Delegates

class RelationshipItem @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding = RelationshipTextinputLayoutBinding
        .inflate(LayoutInflater.from(context), this, true)

    private var defaultMinLength = 5

    var isValid: Boolean = false

    private val relationshipStatus: Array<String> =
        context.resources.getStringArray(R.array.relationship)


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
        return binding.relationshipInputEdittext.text.toString()
    }

    init {
        setLayout(attrs)
        listener()
    }

    private fun setLayout(attrs: AttributeSet?) {
        attrs?.let { attributeSet ->
            val attributes = context.obtainStyledAttributes(
                attributeSet,
                R.styleable.RelationshipItem
            )

            docCounterMinLength =
                attributes.getInt(R.styleable.RelationshipItem_relationCounterMinLength,
                    defaultMinLength)
            docHintText = attributes.getString(R.styleable.RelationshipItem_relationHintText)
                ?: context.getString(R.string.default_hint)
            docErrorText = attributes.getString(R.styleable.RelationshipItem_relationErrorText)
                ?: context.resources.getString(R.string.default_error_text)
            isValid = false

            attributes.recycle()
        }
    }

    private fun listener() {
        binding.relationshipInputEdittext.setOnFocusChangeListener { _, hasFocus ->
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
        val arrayAdapterRelations = ArrayAdapter(
            context,
            R.layout.layout_item_autocomplete,
            R.id.autoComplete_custom,
            relationshipStatus
        )
        binding.relationshipInputEdittext.setAdapter(arrayAdapterRelations)
    }

    fun verify(): Boolean {
        val relation = binding.relationshipInputEdittext.text.toString()

        if (relation == "" ||
            relation == null ||
            relation !in relationshipStatus ||
            relation.length < docCounterMinLength
        ) {
            binding.docInputLayout.also {
                it.error = docErrorText
                it.isErrorEnabled = true
                it.shake(it)
            }

            return false
        }
        binding.docInputLayout.isErrorEnabled = false
        isValid = true
        return true
    }

}