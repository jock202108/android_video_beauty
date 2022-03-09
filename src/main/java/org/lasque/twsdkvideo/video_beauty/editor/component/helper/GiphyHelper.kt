package org.lasque.twsdkvideo.video_beauty.editor.component.helper

import android.util.Log
import com.giphy.sdk.ui.Giphy.configure

import com.giphy.sdk.ui.views.GiphyDialogFragment.Companion.newInstance
import androidx.fragment.app.FragmentActivity
import com.giphy.sdk.core.models.Media
import com.giphy.sdk.ui.Giphy
import com.giphy.sdk.ui.GPHSettings
import com.giphy.sdk.ui.themes.GridType
import com.giphy.sdk.ui.themes.GPHTheme
import com.giphy.sdk.ui.GPHContentType
import com.giphy.sdk.core.models.enums.RatingType
import com.giphy.sdk.ui.views.GiphyDialogFragment
import com.google.gson.Gson
import java.util.ArrayList

object GiphyHelper {
    @JvmStatic
    fun openGiphy(context: FragmentActivity,gifSelectionListener : GiphyDialogFragment.GifSelectionListener) {
        val YOUR_API_KEY = "RWVPqbvg85MRkmKOSrZ9R9FtwwdtWVSK" //prod key
        configure(context, YOUR_API_KEY, true, null)
        val gphSettings = GPHSettings()
        gphSettings.gridType = GridType.waterfall
        gphSettings.useBlurredBackground = false
        gphSettings.theme = GPHTheme.Dark
        gphSettings.stickerColumnCount = 3
        val contentTypes = ArrayList<GPHContentType>()
        contentTypes.add(GPHContentType.recents)
        contentTypes.add(GPHContentType.gif)
        contentTypes.add(GPHContentType.sticker)
         contentTypes.add(GPHContentType.text)
        contentTypes.add(GPHContentType.emoji)
        gphSettings.mediaTypeConfig = contentTypes.toTypedArray()
        gphSettings.showAttribution = true
        gphSettings.rating = RatingType.pg
        gphSettings.showConfirmationScreen = false
        gphSettings.showCheckeredBackground = true
        val dialog = newInstance(gphSettings, null, null)
        dialog.onResume()
        dialog.gifSelectionListener = gifSelectionListener
//        dialog.gifSelectionListener =  object : GiphyDialogFragment.GifSelectionListener {
//            override fun onGifSelected(
//                media: Media,
//                searchTerm: String?,
//                selectedContentType: GPHContentType
//            ) {
//                val toJson = Gson().toJson(media)
//                Log.d("GIPHY", "onGifSelected$toJson")
//
//
//            }
//
//            override fun onDismissed(selectedContentType: GPHContentType) {
//            }
//
//            override fun didSearchTerm(term: String) {
//            }
//        }
        dialog.show(context.supportFragmentManager, "gifs_dialog")
    }
}