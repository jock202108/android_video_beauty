
package org.lasque.twsdkvideo.video_beauty.effectcamera.album

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class AlbumInfo(var path :String,var type : AlbumItemType,var duration : Int,var createDate : Long,val md5Key : String,val maxSize : Int = 0,var audioPath : String = path) : Serializable

enum class AlbumItemType : Serializable{
    Image,Video;
}