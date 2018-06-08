package io.jim.tesserapp.ui.model

import android.content.Context
import android.widget.SeekBar
import android.widget.TextView
import io.jim.tesserapp.R

/**
 * A translation controller.
 * The current translation value is passed to [onTranslated].
 */
fun translationController(
        context: Context,
        seekBar: SeekBar,
        watch: TextView,
        onTranslated: (translation: Double) -> Unit
) = Controller(
        seekBar = seekBar,
        watch = watch,
        valueRange = -5.0..5.0,
        startValue = 0.0,
        formatString = context.getString(R.string.transform_translation_value),
        onValueUpdate = onTranslated
)