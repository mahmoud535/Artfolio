package com.example.designersstore.presentation.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.designersstore.R
import java.io.IOException


// TODO Step 2: Create a custom object to create a common functions for Glide which can be used in whole application. You can also add the piece of code directly at the same place where you want to load the image with all the parameters.
// START
/**
 * A custom object to create a common functions for Glide which can be used in whole application.
 */
class GlideLoader (val  context: Context){

    // TODO Step 3: Create a function to load image from URI for the user profile picture.
    // START
    /**
     * A function to load image from URI for the user profile picture.
     */
      fun loadUserPicture(image: Any,imageView: ImageView){
          try {
              // Load the user image in the ImageView.
              Glide
                  .with(context)
                  .load(image)// URI of the image
                  .centerCrop()// Scale type of the image.
                  .placeholder(R.drawable.ic_user_placeholder)// A default place holder if image is failed to load.
                  .into(imageView)// the view in which the image will be loaded.
          }catch (e:IOException){
              e.printStackTrace()
          }
      }
    //END

    fun loadProductPicture(image: Any, imageView: ImageView) {
        try {
            // Load the user image in the ImageView.
            Glide
                .with(context)
                .load(image) // URI of the image
                .centerCrop() // Scale type of the image.
                .into(imageView) // the view in which the image will be loaded.
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


}