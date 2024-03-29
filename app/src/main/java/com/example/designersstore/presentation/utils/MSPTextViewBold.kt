package com.example.designersstore.presentation.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

/**
 * This class will be used for Custom bold text using the TextView which inherits the AppCompactTextView class.
 */

class MSPTextViewBold(context: Context,attrs: AttributeSet): AppCompatTextView(context, attrs) {
    /**
     * The init block runs every time the class is instantiated.
     */
    init {
        // Call the function to apply the font to the components.
        applyFont()
    }

    /**
     * Applies a font to a TextView.
     */
    private fun applyFont(){

        val typeface:Typeface=
            Typeface.createFromAsset(context.assets,"Montserrat-Bold.ttf")
        setTypeface(typeface)
    }
}