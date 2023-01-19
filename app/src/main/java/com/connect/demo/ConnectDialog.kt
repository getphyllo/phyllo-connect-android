package com.connect.demo

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView

class ConnectDialog(private val mContext: Context) : Dialog(mContext) {

    var descView:TextView?= null
    var titleView:TextView?= null
    var okBtn:Button?= null

    fun showDialog(title: String?, desc: String?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.layout_connect_dialog)

        descView = findViewById(R.id.desc)
        titleView = findViewById(R.id.title)
        okBtn = findViewById(R.id.ok_btn)
        if (TextUtils.isEmpty(desc)) {
            descView?.visibility = View.GONE
        } else {
            descView?.text = desc
        }

        if (TextUtils.isEmpty(title)) {
            titleView?.visibility = View.GONE
        } else {
            titleView?.text = title
        }

        okBtn?.setOnClickListener(object:View.OnClickListener{
            override fun onClick(v: View?) {
                this@ConnectDialog.dismiss()
            }
        })
        setCanceledOnTouchOutside(true)
        window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
        window!!.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL)
        window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        show()
    }
}