package app.dealux.docs_txtinputs_library.utils

import android.view.View
import android.view.animation.Animation
import com.google.android.material.textfield.TextInputLayout

fun TextInputLayout.shake(view: View) {
    val startX = 0f
    val translationX = 35f
    val duration = 100L

    view.animate()
        .translationX(translationX)
        .setDuration(duration)
        .withEndAction {
            view.animate()
                .translationX(startX)
                .duration = duration
        }
        .start()
}