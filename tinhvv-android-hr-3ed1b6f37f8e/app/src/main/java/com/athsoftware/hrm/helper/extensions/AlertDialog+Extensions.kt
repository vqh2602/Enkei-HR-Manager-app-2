package com.athsoftware.hrm.helper.extensions

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.SpannableStringBuilder
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import com.athsoftware.hrm.R
import com.athsoftware.hrm.model.LeaveType
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.dialog_enter_comment.view.*
import kotlinx.android.synthetic.main.dialog_search.view.*
import java.util.*

fun showConfirm(context: Context, title: String? = null, content: String, rightButtonTitle: String = "OK", leftButtonTitle: String? = null, rightButtonClickHandler: (() -> Unit)? = null, cancelListener: (() -> Unit)? = null) {
    val dialog = AlertDialog.Builder(context)
    dialog.setCancelable(true)
    dialog.setPositiveButton(rightButtonTitle) { dia, _ ->
        dia.dismiss()
        rightButtonClickHandler?.invoke()
    }
    title?.let {
        dialog.setTitle(title)
    }
    leftButtonTitle?.let {
        dialog.setNegativeButton(it) { dia, _ ->
            dia.dismiss()
        }
    }
    dialog.setOnCancelListener {
        cancelListener?.invoke()
    }
    dialog.setOnDismissListener {
        cancelListener?.invoke()
    }
    dialog.setMessage(content)
    dialog.create().show()
}


fun URLSpan.makeLinkClickable(strBuilder: SpannableStringBuilder, click: () -> Unit) {
    val start = strBuilder.getSpanStart(this)
    val end = strBuilder.getSpanEnd(this)
    val flags = strBuilder.getSpanFlags(this)
    val clickable = object : ClickableSpan() {
        override fun onClick(view: View) {
            click.invoke()
        }
    }
    strBuilder.setSpan(clickable, start, end, flags)
    strBuilder.removeSpan(this)
}

fun showSearch(context: Context, adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
               loadData: (() -> Unit) -> Disposable): Dialog? {
    val builder = AlertDialog.Builder(context)
    builder.setCancelable(true)
    var dialog: Dialog? = null
    val view = LayoutInflater.from(context).inflate(R.layout.dialog_search, null, false)
    var dis: Disposable? = null
    view.apply {
        progressBar.visible()
        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.adapter = adapter
        btnClose?.setOnClickListener {
            dialog?.dismiss()
        }
    }
    dialog?.setOnDismissListener {
        dis?.dispose()
    }
    builder.setView(view)
    dialog = builder.create()
    val lp = WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.MATCH_PARENT)
    lp.copyFrom(dialog?.window?.attributes)
    lp.height = WindowManager.LayoutParams.WRAP_CONTENT
    lp.width = WindowManager.LayoutParams.MATCH_PARENT
    dialog?.window?.attributes = lp
    dialog?.show()
    dis = loadData.invoke {
        view.progressBar?.gone()
        view.recyclerView?.adapter?.notifyDataSetChanged()
    }
    return dialog
}

fun showEnterComment(context: Context,
                     listener: ((String) -> Unit)): Dialog? {
    val builder = AlertDialog.Builder(context)
    builder.setCancelable(true)
    var dialog: Dialog? = null
    val view = LayoutInflater.from(context).inflate(R.layout.dialog_enter_comment, null, false)
    view.apply {
        btnCloseComment.setOnClickListener {
            dialog?.dismiss()
        }
        btnSaveComment.setOnClickListener {
            listener(edtComment.text.toString())
            dialog?.dismiss()
        }
    }
    dialog?.setOnDismissListener {
    }
    builder.setView(view)
    dialog = builder.create()
    val lp = WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.MATCH_PARENT)
    lp.copyFrom(dialog?.window?.attributes)
    lp.height = WindowManager.LayoutParams.WRAP_CONTENT
    lp.width = WindowManager.LayoutParams.MATCH_PARENT
    dialog?.window?.attributes = lp
    dialog?.show()
    return dialog
}

fun showSelection(context: Context, title: String? = null, listType: List<LeaveType>, selectedListener: (LeaveType) -> Unit) {
    val dialog = AlertDialog.Builder(context)
    dialog.setCancelable(true)
    dialog.setPositiveButton(R.string.cancel) { dia, _ ->
        dia.dismiss()
    }
    title?.let {
        dialog.setTitle(title)
    }

    val arrayAdapter = ArrayAdapter<LeaveType>(context, android.R.layout.select_dialog_item)
    arrayAdapter.addAll(listType)
    dialog.setAdapter(arrayAdapter) { dialogI, position ->
        selectedListener.invoke(listType[position])
        dialogI.dismiss()
    }
    dialog.create().show()
}

fun showSelectionPartOfDay(context: Context, title: String? = null, listString: List<String>, selectedListener: (Int) -> Unit) {
    val dialog = AlertDialog.Builder(context)
    dialog.setCancelable(true)
    dialog.setPositiveButton(R.string.cancel) { dia, _ ->
        dia.dismiss()
    }
    title?.let {
        dialog.setTitle(title)
    }

    val arrayAdapter = ArrayAdapter<String>(context, android.R.layout.select_dialog_item)
    listString.forEach {
        arrayAdapter.addAll(it)
    }
    dialog.setAdapter(arrayAdapter) { dialogI, position ->
        selectedListener(position)
        dialogI.dismiss()
    }
    dialog.create().show()
}

fun showSelectDate(context: Context, minDate: Calendar? = null, default: Calendar? = null, maxDate: Calendar? = null, select: (Calendar) -> Unit) {
    var builder = SpinnerDatePickerDialogBuilder()
            .context(context)
            .callback { _, year, monthOfYear, dayOfMonth ->
                val date = Calendar.getInstance()
                date.set(year, monthOfYear, dayOfMonth)
                select(date)
            }
            .spinnerTheme(R.style.NumberPickerStyle)
            .showTitle(false)
            .showDaySpinner(true)
    minDate?.let {
        builder = builder.minDate(it.year(), it.month(), it.day())
    }
    default?.let {
        builder = builder.defaultDate(it.year(), it.month(), it.day())
    }
    if (maxDate != null) {
        builder = builder.maxDate(maxDate.year(), maxDate.month(), maxDate.day())
    } else {
        val next6m = Calendar.getInstance()
        next6m.add(Calendar.MONTH, 6)
        builder = builder.maxDate(next6m.year(), next6m.month(), next6m.day())
    }

    builder.build()
            .show()
}

