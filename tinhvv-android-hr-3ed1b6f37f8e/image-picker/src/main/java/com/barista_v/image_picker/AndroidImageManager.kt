package com.barista_v.image_picker

import android.Manifest.permission.*
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import com.barista_v.image_picker.extensions.saveInFile
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import java.io.File
import java.lang.ref.WeakReference


/**
 * Sample (change quality and size) images obtained from the gallery/camera intent.
 *
 * You need to call [handleOnActivityResult] from the fragment.
 */
open class AndroidImageManager(fragment: Fragment, val applicationPackage: String, val isDialog: Boolean = false, val isCrop: Boolean = false) {
    private var weakActivity = WeakReference(fragment)
    private val permissionOwner = PermissionOwner(fragment)
    private val storageDir: File? by lazy {
        fragment.activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    }

    private val isExternalStorageWritable: Boolean
        get() = Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()

    var results = BehaviorSubject.create<String>()
    var format = Bitmap.CompressFormat.JPEG
    var quality = 80
    val CROP_RESULT = 12555

    /**
     * From SDK 18 (kitkat) you dont need to ask user permissions for
     * #android.Manifest.permission.WRITE_EXTERNAL_STORAGE
     */
    val isCameraPermissionsNeeded: Boolean
        get() = Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT

    /**q
     * Added camera permission because it creates a conflict if the app have camera permission
     * even if its in other place of the app.
     *
     * "Note: if you app targets M and above and declares as using the CAMERA permission
     * which is not granted, then try to use this action will result in a SecurityException."
     */
    val permissions = arrayOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE)
    val cameraPermissions = arrayOf(CAMERA)

    /**
     * Use #shouldAskForCameraPermissions to check if this method really needs a permission or not
     *
     * @return Observable with the result file path
     */
    //  @RequiresPermission(WRITE_EXTERNAL_STORAGE)
    open fun requestImageFromCamera(resultImageName: String, requestCode: Int): Observable<String> {
        if (isExternalStorageWritable) {
            weakActivity.get()?.let {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                if (takePictureIntent.resolveActivity(it.activity?.packageManager) != null) {
                    val imageUri = getCameraImageUri(it.context ?: return@let, resultImageName)

                    it.activity?.grantUriPermission(applicationPackage, imageUri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                    takePictureIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    if (isDialog) {
                        it.targetFragment?.startActivityForResult(takePictureIntent, requestCode)
                    } else
                        it.startActivityForResult(takePictureIntent, requestCode)
                }
            }
        } else {
            results.onError(Throwable("External storage is not available at the moment."))
            completeResults()
        }

        return results.hide()
    }

    //  @RequiresPermission(allOf = arrayOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE))
    /**
     * @return Observable with the result file path
     */
    fun requestImageFromGallery(requestCode: Int): Observable<String> {
        if (isExternalStorageWritable) {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            if (isDialog) {
                weakActivity.get()?.targetFragment?.startActivityForResult(intent, requestCode)
            } else
                weakActivity.get()?.startActivityForResult(intent, requestCode)
        } else {
            results.onError(Throwable("External storage is not available at the moment."))
        }

        return results.hide()
    }

    fun cropImage(path: String, imageUri: Uri) {
        val builder = CropImage.activity(imageUri)
                .setCropShape(CropImageView.CropShape.OVAL)
                .setFixAspectRatio(true)

//        val cropIntent = Intent("com.android.camera.action.CROP")
//
//        cropIntent.setDataAndType(Uri.parse(path), "image/*")
//        cropIntent.putExtra("crop", "true")
//        cropIntent.putExtra("aspectX", 1)
//        cropIntent.putExtra("aspectY", 1)
//        cropIntent.putExtra("return-data", true)
//        try {
//
        if (isDialog) {
            builder.start(weakActivity.get()?.targetFragment?.activity
                    ?: return, weakActivity.get()?.targetFragment ?: return)
        } else
            builder.start(weakActivity.get()?.activity ?: return, weakActivity.get() ?: return)
//        } catch (_: Exception) {
//            results.onNext(path)
//            completeResults()
//            weakActivity.get()?.activity?.revokeUriPermission(imageUri,
//                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
//        }
    }

    fun isPermissionGranted(): Boolean = permissionOwner.isPermissionGranted(permissions)
    fun isCameraPermissionGranted(): Boolean = permissionOwner.isPermissionGranted(cameraPermissions)

    fun shouldShowPermissionRationale(): Boolean = permissionOwner.shouldShowPermissionRationale(permissions)

    fun requestPermission(requestCodeGalleryPermissions: Int) =
            permissionOwner.requestPermission(permissions, requestCodeGalleryPermissions)

    fun requestCameraPermission(requestCodeCameraPermissions: Int) =
            permissionOwner.requestPermission(cameraPermissions, requestCodeCameraPermissions)

    open fun handleOnActivityResult(result: ActivityResult, imageName: String, width: Int, height: Int): Int {
        if (!isExternalStorageWritable) {
            results.onError(Throwable("External storage is not available at the moment."))
            completeResults()
            return 1
        }

        weakActivity.get()?.let { activity ->
            try {
                val imageUri = getCameraImageUri(activity.context ?: return@let, imageName)
                if (isCrop && result.requestCode == CROP_RESULT) {
                    val bitmap = result.data?.extras?.getParcelable<Bitmap>("data")
                    val destinationFile = createInternalFile("$imageName.${format.name}")
                    bitmap?.saveInFile(destinationFile, format, quality)?.let {
                        results.onNext(it.absolutePath)
                        completeResults()
                        activity.activity?.revokeUriPermission(imageUri,
                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        return 1
                    }
                    if (bitmap == null) {
                        val inf = createInternalFile("$imageName.${format.name}")
                        results.onNext(inf.absolutePath)
                        completeResults()
                        activity.activity?.revokeUriPermission(imageUri,
                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    return 1
                }

                if (isCrop && (result.requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)) {
                    val resultCrop = CropImage.getActivityResult(result.data)
                    val resultUri = resultCrop.getUri()
                    val bitmap = MediaStore.Images.Media
                            .getBitmap(activity.activity?.contentResolver, resultUri)
                    val destinationFile = createInternalFile("$imageName.${format.name}")
                    bitmap?.saveInFile(destinationFile, format, quality)?.let {
                        results.onNext(it.absolutePath)
                        completeResults()
                        activity.activity?.revokeUriPermission(imageUri,
                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        return 1
                    }
                    if (bitmap == null) {
                        val inf = createInternalFile("$imageName.${format.name}")
                        results.onNext(inf.absolutePath)
                        completeResults()
                        activity.activity?.revokeUriPermission(imageUri,
                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    return 1
                }
                val sourceImage = readImageFileFromGallery(result.data)
                        ?: readImageFileFromCamera(imageUri)

                val destinationFile = createInternalFile("$imageName.${format.name}")

                val bitmap = sourceImage.resizeRotatedBitmap(width, height)
                bitmap?.saveInFile(destinationFile, format, quality)?.let {
                    if (isCrop && result.requestCode != CROP_RESULT) {
                        cropImage(it.absolutePath, imageUri)
                    } else {
                        results.onNext(it.absolutePath)
                        completeResults()
                        activity.activity?.revokeUriPermission(imageUri,
                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    return 1
                }

                if (bitmap == null) {
                    results.onError(Throwable("Image  $imageName.${format.name} could not be saved."))
                    completeResults()
                }
            } catch (e: Exception) {
                results.onError(e)
                completeResults()
            }
        } ?: completeResults()
        return 0
    }

    /**
     * Complete and init the observer so it can handle more items.
     */
    private fun completeResults() {
        results = BehaviorSubject.create<String>()
    }

    /**
     * Find the shareable Uri for an image with name.
     */
    private fun getCameraImageUri(context: Context, imageName: String): Uri {
        val cameraFile = createCameraFile("$imageName.${format.name}")

        return FileProvider.getUriForFile(context, "$applicationPackage.provider", cameraFile)
    }

    /**
     * Get the image path from an [Intent.ACTION_PICK] intent action created with
     * [MediaStore.Images.Media.EXTERNAL_CONTENT_URI]

     * @param data intent received in [Activity.onActivityResult]
     * *
     * @return image path
     * *
     * @see MediaStore.ACTION_IMAGE_CAPTURE
     * @see MediaStore.Images.Media.EXTERNAL_CONTENT_URI
     */
    private fun readImageFileFromGallery(resultIntent: Intent?): InternalImage? {
        return weakActivity.get()?.activity?.contentResolver?.let { contentResolver ->
            val columns = arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.ImageColumns.ORIENTATION)
            var path: String? = null
            var orientation: Int = 0

            resultIntent?.data?.let { uri ->
                contentResolver.query(uri, columns, null, null, null)?.apply {
                    moveToFirst()

                    path = getString(getColumnIndex(columns[0]))
                    orientation = getInt(getColumnIndex(columns[1]))

                    close()
                }

                return InternalImage(path, orientation)
            }
        }
    }

    private fun readImageFileFromCamera(imageUri: Uri): InternalImage {
        return InternalImage(createCameraFile(imageUri.lastPathSegment).absolutePath)
    }

    private fun createCameraFile(fullFileName: String) = File(storageDir, fullFileName)

    private fun createInternalFile(fileName: String) = File(storageDir, fileName).apply {
        mkdirs()
    }
}

