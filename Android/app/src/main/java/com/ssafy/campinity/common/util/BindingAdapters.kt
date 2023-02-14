package com.ssafy.campinity.common.util

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.ssafy.campinity.R

object BindingAdapters {

    @JvmStatic
    @BindingAdapter("android:profileImgUri")
    fun ImageView.setProfileImg(imgUri: Uri?) {
        Glide.with(this.context)
            .load(imgUri)
            .placeholder(R.drawable.ic_profile_image)
            .error(R.drawable.ic_profile_image)
            .circleCrop()
            .into(this)
    }

    @JvmStatic
    @BindingAdapter("android:collectionImgUri")
    fun ImageView.setCollectionImgUri(imgUri: Uri?) {
        Glide.with(this.context)
            .load(imgUri)
            .into(this)
    }

    @JvmStatic
    @BindingAdapter("android:profileImgString")
    fun ImageView.setProfileImgString(imgUri: String?) {
        Glide.with(this.context)
            .load("http://i8d101.p.ssafy.io:8003/images$imgUri")
            .placeholder(R.drawable.ic_profile_default)
            .error(R.drawable.ic_profile_default)
            .circleCrop()
            .into(this)
    }

    @JvmStatic
    @BindingAdapter("android:normalImgUri")
    fun ImageView.setNormalImg(imgUri: String?) {
        Glide.with(this.context)
            .load("http://i8d101.p.ssafy.io:8003/images$imgUri")
            .placeholder(R.drawable.bg_image_not_found)
            .error(R.drawable.bg_image_not_found)
            .into(this)
    }

    @JvmStatic
    @BindingAdapter("android:rankingImgUri")
    fun ImageView.setRankingImg(imgUri: String?) {
        Glide.with(this.context)
            .load(imgUri)
            .placeholder(R.drawable.bg_image_not_found)
            .error(R.drawable.bg_image_not_found)
            .into(this)
    }

    @JvmStatic
    @BindingAdapter("android:roundedImgUri")
    fun ImageView.setRoundedImg(imgUri: String?) {
        Glide.with(this.context)
            .load("http://i8d101.p.ssafy.io:8003/images$imgUri")
            .placeholder(R.drawable.bg_image_not_found)
            .error(R.drawable.bg_image_not_found)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(10.px(context))))
            .into(this)
    }
}