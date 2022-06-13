package app.dealux.docs_txtinputs_library.customview.birthdaytextinput

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import app.dealux.docs_txtinputs_library.R
import app.dealux.docs_txtinputs_library.utils.DatePickerFragment
import app.dealux.docs_txtinputs_library.utils.DatePickerFragment.Companion.style
import app.dealux.docs_txtinputs_library.databinding.BirthButtomLayoutBinding
import java.util.*

class BirthdayItem @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding = BirthButtomLayoutBinding
        .inflate(LayoutInflater.from(context), this, true)
    var fragmentActivity: FragmentActivity? = null
    var docErrorText: String = getContext().getString(R.string.default_error_text)
    var isValid: Boolean = false

    init {
        setLayout(attrs)
        listener()
    }

    private fun setLayout(attrs: AttributeSet?) {
        attrs?.let { attributeSet ->
            val attributes = context.obtainStyledAttributes(
                attributeSet,
                R.styleable.BirthdayItem
            )

            fragmentActivity = null
            style = R.style.datePickerTheme
            val customErrorText = attributes.getString(R.styleable.BirthdayItem_birthdayErrorText)
            docErrorText = customErrorText ?: docErrorText
            isValid = false

            attributes.recycle()
        }
    }

    private fun listener() {
        binding.buttom.setOnClickListener {
            val datePicker = DatePickerFragment { day, month, year ->
                onDateSelected(
                    day,
                    month + 1,
                    year
                )
            }
            datePicker.show(fragmentActivity!!.supportFragmentManager, "datePicker")
        }
    }

    private fun onDateSelected(day: Int, month: Int, year: Int) {
        if (day <= 9 && month <= 9) {
            "0$day/0$month/$year".also { binding.buttom.text = it }
        } else if (day <= 9) {
            "0$day/$month/$year".also { binding.buttom.text = it }
        } else if (month <= 9) {
            "$day/0$month/$year".also { binding.buttom.text = it }
        } else {
            "$day/$month/$year".also { binding.buttom.text = it }
        }
    }

    fun verifyBirthDay(): Boolean {
        val birthday = binding.buttom.text.toString()
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR).toString()
        val redColor = Color.parseColor("#FD1414")
        val colorState = ColorStateList.valueOf(redColor)

        if (birthday == "" || birthday == null || birthday.subSequence(6, 9) == year) {
            binding.buttom.strokeColor = colorState
            binding.buttom.error = docErrorText

            return false
        } else {
            binding.buttom.strokeColor = colorState
            isValid = true
        }
        return true
    }

}