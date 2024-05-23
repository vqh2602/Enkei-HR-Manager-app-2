package com.athsoftware.hrm.helper
import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager

class KeyboardHelper {

    companion object {
        val shared = KeyboardHelper()
    }

    private val TAG_LISTENER_ID = -10010

    /**
     * Hides keyboard using currently focused view.<br></br>
     * Shortcut for [ hideSoftKeyboard(activity, activity.getCurrentFocus())][.hideSoftKeyboard].
     */
    fun hideSoftKeyboard(activity: Activity) {
        hideSoftKeyboard(activity, activity.currentFocus)
    }

    /**
     * Uses given views to hide soft keyboard and to clear current focus.
     *
     * @param context Context
     * @param focusedView Currently focused view
     */
    fun hideSoftKeyboard(context: Context, focusedView: View?) {
        if (focusedView == null) {
            return
        }

        val manager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        manager.hideSoftInputFromWindow(focusedView.windowToken, 0)
        focusedView.clearFocus()
    }

    /**
     * Shows soft keyboard and requests focus for given view.
     */
    fun showSoftKeyboard(context: Context, view: View?) {
        if (view == null) {
            return
        }

        val manager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        view.requestFocus()
        manager.showSoftInput(view, 0)
    }

    /**
     * Registers listener for soft keyboard state changes.<br></br>
     * The state is computed based on rootView height changes.
     *
     * @param rootView Should be deepest full screen view, i.e. root of the layout passed to
     * Activity.setContentView(...) or view returned by Fragment.onCreateView(...)
     * @param listener Keyboard state listener
     */
    fun addKeyboardListener(rootView: View,
                            listener: OnKeyboardShowListener) {

        val layoutListener = object : ViewTreeObserver.OnGlobalLayoutListener {
            private var isKeyboardShown: Boolean = false
            private var initialHeightsDiff = -1

            override fun onGlobalLayout() {
                val frame = Rect()
                rootView.getWindowVisibleDisplayFrame(frame)

                var heightDiff = rootView.rootView.height - (frame.bottom - frame.top)
                if (initialHeightsDiff == -1) {
                    initialHeightsDiff = heightDiff
                }
                heightDiff -= initialHeightsDiff

                if (heightDiff > 100) { // If more than 100 pixels, its probably a keyboard...
                    if (!isKeyboardShown) {
                        isKeyboardShown = true
                        listener.onKeyboardShow(true)
                    }
                } else if (heightDiff < 50) {
                    if (isKeyboardShown) {
                        isKeyboardShown = false
                        listener.onKeyboardShow(false)
                    }
                }
            }
        }

        rootView.viewTreeObserver.addOnGlobalLayoutListener(layoutListener)
        rootView.setTag(TAG_LISTENER_ID, layoutListener)
    }

    fun removeKeyboardListener(rootView: View) {
        val layoutListener = rootView.getTag(TAG_LISTENER_ID) as ViewTreeObserver.OnGlobalLayoutListener
        rootView.viewTreeObserver.removeOnGlobalLayoutListener(layoutListener)
    }


    interface OnKeyboardShowListener {
        fun onKeyboardShow(show: Boolean)
    }
}