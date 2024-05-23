package com.athsoftware.hrm.helper.support

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.athsoftware.hrm.BuildConfig
import com.athsoftware.hrm.R
import com.athsoftware.hrm.helper.extensions.gone
import com.barista_v.image_picker.ActivityResult
import com.barista_v.image_picker.AndroidImageManager
import com.lcodecore.tkrefreshlayout.utils.LogUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.layout_image_picker.*
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by vinhdn on 16-Jan-18.
 */
class PhotoPickerDialog : BottomSheetDialogFragment() {

    companion object {
        fun getInstance(isCrop: Boolean = false, delegateRetry:(() -> Unit)? = null, delegateGetImage: ((String) -> Unit)?): PhotoPickerDialog {
            val dialg = PhotoPickerDialog()
            dialg.isCrop = isCrop
            dialg.delegateRetry = delegateRetry
            dialg.delegateGetImage = delegateGetImage
            return dialg
        }
    }

    val requestCodeCameraPermissions = 1
    val requestCodeGalleryPermissions = 2
    val requestCodeCameraPhoto = 3
    val requestCodeGalleryPhoto = 4

    private var imageName = ""
    private var isCrop = false
    private var delegateRetry: (() -> Unit)? = null
    private lateinit var fragment: WeakReference<Fragment>
    var androidImageManager: AndroidImageManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        androidImageManager = AndroidImageManager(this, BuildConfig.APPLICATION_ID, isCrop = isCrop)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.layout_image_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btCancel?.setOnClickListener {
            dismiss()
        }

        btGallery?.setOnClickListener {
            onGalleryClick()
        }

        btTakeImage?.setOnClickListener {
            onCameraClick()
        }

        btRetry?.setOnClickListener {
            delegateRetry?.invoke()
            dismiss()
        }
        if (delegateRetry == null) {
            btRetry.gone()
            bottomLineRetry.gone()
        }
    }

    protected fun onGalleryClick() {
        imageName = "Image-${SimpleDateFormat("yyyyMMdd-hhmmss-SSS").format(Date())}"
        if (androidImageManager?.isPermissionGranted() == true) {
            handleObservable(androidImageManager?.requestImageFromGallery(requestCodeGalleryPhoto))
//        } else if (androidImageManager?.shouldShowPermissionRationale() == true) {
//            showImagePermissionRationale()
        } else {
            androidImageManager?.requestPermission(requestCodeGalleryPermissions)
        }
    }

    protected fun onCameraClick() {
        imageName = "Image-${SimpleDateFormat("yyyyMMdd-hhmmss-SSS").format(Date())}"
        if (androidImageManager?.isCameraPermissionGranted() == true) {
            handleObservable(androidImageManager?.requestImageFromCamera(imageName, requestCodeCameraPhoto))
//        } else if (androidImageManager?.shouldShowPermissionRationale() == true) {
//            showImagePermissionRationale()
        } else {
            androidImageManager?.requestCameraPermission(requestCodeCameraPermissions)
        }
    }

    /**
     * Handle both [AndroidImageManager.requestImageFromCamera] and
     * [AndroidImageManager.requestImageFromGallery] responses with the same function, it doesnt matter
     * if it comes from gallery or camera, we need to set it to the view (or do something with the path).
     */
    private fun handleObservable(observable: Observable<String>?) {
        observable?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({ decodedImagePath ->
                    delegateGetImage?.invoke(decodedImagePath)
                    dismiss()
                }, {
                    manageError(it)
                    dismiss()
                })
    }

    private var delegateGetImage: ((String) -> Unit)? = null

    fun manageError(throwable: Throwable?) {
        throwable?.printStackTrace()
        LogUtil.i("Error getting image")
    }

    fun showImagePermissionRationale() {
//        toast("")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        androidImageManager?.handleOnActivityResult(ActivityResult(requestCode, data), imageName, 1024, 1024)
    }
}