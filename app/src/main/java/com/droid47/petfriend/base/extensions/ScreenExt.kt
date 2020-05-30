package com.droid47.petfriend.base.extensions

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.res.Resources
import android.graphics.Point
import android.os.Build
import android.os.IBinder
import android.view.*
import java.lang.reflect.Method


private var screenHeight = 0
private var screenWidth = 0

fun Context.getScreenHeight(): Int {
    if (screenHeight == 0) {
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)
        screenHeight = size.y
    }
    return screenHeight
}


fun Context.getScreenWidth(): Int {
    if (screenWidth == 0) {
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)
        screenWidth = size.x
    }
    return screenWidth
}

fun Context.getDimen(resId: Int): Int {
    return resources.getDimension(resId).toInt()
}

fun Context.getDimension(dimenId: Int): Int {
    return resources.getDimension(dimenId).toInt()
}

//fun getBlurImage(context: Context?, image: Bitmap): Bitmap? {
//    var bitmap = image
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//        try {
//            val pixels = IntArray(image.width * image.height)
//            //Get JPEG pixels.  Each int is the color values for one pixel.
//            image.getPixels(
//                pixels, 0, image.width, 0, 0, image.width,
//                image.height
//            )
//            //Create a Bitmap of the appropriate format.
//            bitmap = Bitmap.createBitmap(
//                image.width, image.height,
//                Bitmap.Config.ARGB_8888
//            )
//            //Set RGB pixels.
//            bitmap.setPixels(
//                pixels, 0, bitmap.width, 0, 0, bitmap.width,
//                bitmap.height
//            )
//            bitmap = Bitmap.createBitmap(
//                bitmap.width, bitmap.height,
//                Bitmap.Config.ARGB_8888
//            )
//            val renderScript = RenderScript.create(context)
//            val blurInput = Allocation.createFromBitmap(renderScript, image)
//            val blurOutput = Allocation.createFromBitmap(renderScript, bitmap)
//            val blur = ScriptIntrinsicBlur.create(
//                renderScript,
//                Element.U8_4(renderScript)
//            )
//            blur.setInput(blurInput)
//            //Radius must be 0 < r <= 25
//            blur.setRadius(5f)
//            blur.forEach(blurOutput)
//            blurOutput.copyTo(bitmap)
//            renderScript.destroy()
//        } catch (e: Exception) {
//            AppUtility.logException(e)
//        }
//    }
//    return bitmap
//}

fun getPositionInWindow(view: View): IntArray? {
    val location = intArrayOf(0, 0)
    view.getLocationInWindow(location)
    return location
}

fun getPositionOnScreen(view: View): IntArray {
    val location = intArrayOf(0, 0)
    view.getLocationOnScreen(location)
    return location
}

fun isPointInsideView(
    x: Float,
    y: Float,
    view: View
): Boolean {
    val location = IntArray(2)
    view.getLocationOnScreen(location)
    val viewX = location[0]
    val viewY = location[1]

    //point is inside view bounds
    return x > viewX && x < viewX + view.width &&
            y > viewY && y < viewY + view.height
}

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
@SuppressLint("PrivateApi")
fun hasNavigationBar(): Boolean {
    return try {
        val serviceManager =
            Class.forName("android.os.ServiceManager")
        val serviceBinder =
            serviceManager.getMethod("getService", String::class.java)
                .invoke(serviceManager, "window") as IBinder
        val stub = Class.forName("android.view.IWindowManager\$Stub")
        val windowManagerService =
            stub.getMethod("asInterface", IBinder::class.java).invoke(stub, serviceBinder)
        val hasNavigationBar: Method = windowManagerService.javaClass.getMethod("hasNavigationBar")
        return hasNavigationBar.invoke(windowManagerService) as Boolean
    } catch (e: Exception) {
        false
    }
}

fun hasNavBar(context: Context): Boolean {
    val resources: Resources = context.resources
    val id: Int = resources.getIdentifier("config_showNavigationBar", "bool", "android")
    return if (id > 0) {
        resources.getBoolean(id)
    } else {
        val hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey()
        val hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)
        !hasMenuKey && !hasBackKey
    }
}