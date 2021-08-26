package com.connect.demo


import android.app.Dialog
import android.content.Context
import android.text.TextUtils
import android.view.*
import android.widget.TextView

class ProgressDialog(private val mContext: Context) : Dialog(mContext) {
    fun showDialog(desc: String?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.layout_progress_dialog)
        val descView: TextView = findViewById(R.id.loading_desc)
        if (TextUtils.isEmpty(desc)) {
            descView.visibility = View.GONE
        } else {
            descView.text = desc
        }
        setCanceledOnTouchOutside(false)
        window!!.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL)
        window!!.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT)
        show()
    }
}
