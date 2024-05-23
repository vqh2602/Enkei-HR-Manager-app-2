package com.athsoftware.hrm.base

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.Toolbar
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import com.athsoftware.hrm.MainActivity
import com.athsoftware.hrm.R
import com.athsoftware.hrm.helper.extensions.*
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout
import com.lcodecore.tkrefreshlayout.footer.LoadingView
import com.lcodecore.tkrefreshlayout.header.progresslayout.ProgressLayout
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_base.view.*

abstract class BaseFragment : Fragment(), IBaseView {

    open fun getPresenter(): IBasePresenter<IBaseView>? = null
    open val isTransfStatus = false
    open val isPaddingBottomBar = true

    open val isUsingViewBase = true
    protected open fun isShowToolbar(): Boolean = false
    protected open fun isShowToolbarIcon(): Boolean = false

    protected open var onMenuSelected: ((MenuItem) -> Unit)? = null

    open lateinit var mActivity: BaseActivity
    private lateinit var rootView: View
    protected var toolbar: Toolbar? = null

    protected var canLoadmore: Boolean = false
    private var refreshView: TwinklingRefreshLayout? = null
    protected open val isRootFragment = false
    private var btnMenu: AppCompatImageView? = null
    protected val disposables: MutableSet<Disposable> = mutableSetOf()

    protected open var mTitle: String = ""
    fun setTitle(title: String) {
        mTitle = title
        toolbar?.title = mTitle
    }

    protected abstract fun getLayoutRes(): Int

    protected open fun getMenu(): Int? {
        return null
    }

    open var progressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (activity !is BaseActivity) {
            Throwable("Activity no override BaseActivity")
        }
        mActivity = activity as BaseActivity
    }

//    fun showBackButton() {
//        rootView.toolbar?.setNavigationIcon(R.drawable.bi_ic_back_white)
//        rootView.toolbar?.setNavigationOnClickListener {
//            back()
//        }
//    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (!isUsingViewBase) {
            rootView = inflater.inflate(getLayoutRes(), container, false)
            return rootView
        }
        rootView = inflater.inflate(R.layout.fragment_base, container, false)
        if (!isShowToolbar()) {
            rootView.toolbar?.gone()
        }
//        if (isPaddingBottomBar)
//            rootView.setPadding(0, 0, 0, getNavigationBarHeight())
        if (!isShowToolbarIcon()) {
            rootView.toolbar?.navigationIcon = null
            rootView.toolbar?.toolbar?.title = ""
        }

        val contentView = inflater.inflate(getLayoutRes(), container, false)
        contentView?.let {
            rootView.contentLayout?.addChild(contentView)
//            refreshView = contentView.bind(R.id.refreshView)
        }
        refreshView?.let {
            it.setHeaderView(ProgressLayout(it.context))
            it.setBottomView(LoadingView(it.context))
            it.setOnRefreshListener(object : RefreshListenerAdapter() {
                override fun onRefresh(refreshLayout: TwinklingRefreshLayout?) {
                    onRefresh()
                }

                override fun onLoadMore(refreshLayout: TwinklingRefreshLayout?) {
                    if (canLoadmore) {
                        onLoadmore()
                    } else {
                        refreshLayout?.finishLoadmore()
                    }
                }
            })
        }

        toolbar = rootView.bind(R.id.toolbar)
        progressBar = rootView.bind(R.id.progressBar)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            view.setupHiddenKeyboard(it)
        }
        getPresenter()?.attachView(this)

        savedInstanceState?.let {
            mTitle = it.getString("title")
        }

        toolbar?.setOnMenuItemClickListener { item ->
            onMenuSelected?.invoke(item)
            return@setOnMenuItemClickListener true
        }
        toolbar?.title = mTitle

//        view.findViewById<View>(R.id.btnBack)?.setOnClickListener {
//            back()
//        }
//
//        view.findViewById<View>(R.id.btnMenu)?.setOnClickListener {
//            showMenu()
//        }
//        btnMenu = view.findViewById(R.id.btnMenu)
//        (activity as? HomeActivity)?.numberNotification?.let {
//            updateMenuNotifi(it)
//        }
    }

//    protected fun showMenu() {
//        (activity as? HomeActivity)?.openMenu()
//    }



    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("title", mTitle)
        super.onSaveInstanceState(outState)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
    }

    override fun showLoading(text: String?) {
        activity?.runOnUiThread {
            rootView.progressLayout?.visible()
        }
    }

    override fun hideLoading() {
        activity?.runOnUiThread {
            rootView.progressLayout?.gone()
        }
    }

    override fun showError(message: String?, isToast: Boolean) {
        activity?.runOnUiThread {
            if (isToast) {
                toast(message?.mapError() ?: R.string.have_an_error.getString())
            } else {
                showNotice(message?.mapError() ?: R.string.have_an_error.getString())
            }
        }
    }

    fun canBackFragment(): Boolean {
        return childFragmentManager.backStackEntryCount > 1
    }

    override fun back() {
        try {
            if (activity is MainActivity) {
                (activity as MainActivity).backFragment()
            } else
            activity?.supportFragmentManager?.popBackStack()
        }catch (ex: IllegalStateException) {

        }
        hideKeyboard()
    }

    fun hideKeyboard() {
        val view = activity?.currentFocus
        if (view != null) {
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun popBackStack(name: String, flags: Int, isFromActivity: Boolean = true) {
        if (isFromActivity) {
            mActivity.popBackStack(name, flags)
        } else {
            childFragmentManager.popBackStack(name, flags)
        }
    }

    fun popBackStackImmediate(name: String, flags: Int, isFromActivity: Boolean = true): Boolean {
        if (isFromActivity) {
            return mActivity.popBackStackImmediate(name, flags)
        } else {
            return childFragmentManager.popBackStackImmediate(name, flags)
        }
    }

    fun openFragment(fragment: BaseFragment, isAddToActivity: Boolean = true, addToBackStack: Boolean = true, name: String? = null) {
        hideKeyboard()
        if (isAddToActivity) {
            mActivity.openFragment(fragment, addToBackStack, name)
        } else {
            openChildFragment(fragment, addToBackStack, name)
        }
    }

    fun replaceFragment(fragment: BaseFragment, isAddToActivity: Boolean = true) {
        if (isAddToActivity) {
            mActivity.openFragment(fragment, false)
        } else {
            openChildFragment(fragment, false)
        }
    }

    fun openChildFragment(fragment: BaseFragment, addToBackStack: Boolean = true, name: String? = null) {
        if (!isRootFragment && parentFragment is BaseFragment) {
            (parentFragment as? BaseFragment)?.openChildFragment(fragment)
        } else {
            val transaction = childFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,
                            R.anim.enter_from_left, R.anim.exit_to_right)
            transaction.replace(R.id.container, fragment)
            if (addToBackStack) {
                transaction.addToBackStack(name)
            }
            transaction.commitAllowingStateLoss()
        }
    }

    override fun onLoadmore() {

    }

    override fun onRefresh() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposables.forEach {
            it.dispose()
        }
        getPresenter()?.detachView()
    }

    fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    private fun getNavigationBarHeight(): Int {
        val hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)
        val hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME)
        if (!hasBackKey || !hasHomeKey) return 0
        if (Build.VERSION.SDK_INT >= 21) {
            var result = 0
            val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = resources.getDimensionPixelSize(resourceId)
            }
            return result
        } else return 0
    }

    fun backStack(): Boolean {
        val canBackStack = canBackFragment()
        if (canBackStack) {
            childFragmentManager.popBackStack()
        }
        return canBackStack
    }

    fun hasNavBar(): Boolean {
        val id = resources.getIdentifier("config_showNavigationBar", "bool", "android")
        return id > 0 && resources.getBoolean(id)
    }

    protected fun showNotice(message: String, handler: (() -> Unit)? = null) {
        activity?.runOnUiThread {
            context?.let {
                showConfirm(it, null, message,"OK", rightButtonClickHandler = handler)
            }
        }
    }
}