package com.athsoftware.hrm

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.athsoftware.hrm.base.BaseActivity
import com.athsoftware.hrm.helper.extensions.getColor
import com.athsoftware.hrm.helper.extensions.inflate
import com.athsoftware.hrm.helper.extensions.load
import com.athsoftware.hrm.views.authen.LoginActivity
import kotlinx.android.synthetic.main.activity_select_service.*
import kotlinx.android.synthetic.main.item_service.view.*

class SelectServiceActivity : BaseActivity() {
    override fun isUsingBaseContent(): Boolean {
        return false
    }

    private val listService = listOf("Leave Management", "Employee Satisfaction Survey", "Employee Evaluation", "Employee Registration")
    private val listServiceIcon = listOf(R.drawable.ic_group_1, R.drawable.ic_group_2, R.drawable.ic_group_3, R.drawable.ic_group_4)
    private val listServiceColor = listOf("#2e80f9", "#f9ac2e", "#ce37fa", "#21d5f2")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.extras != null && (intent.getStringExtra("data") != null || intent.getStringExtra("id") != null)) {
            val intentH = Intent(this, MainActivity::class.java)
            var data = intent.getStringExtra("data")
            if (intent.getStringExtra("data") == null) {
                data = "{\n" +
                        "     \"id\": \"${intent.getStringExtra("id")}\"\n" +
                        " }"
            }
            intentH.putExtra("data", data)
            startActivity(intentH)
            finish()
            return
        }
        setContentView(R.layout.activity_select_service)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = Adapter()
    }

    inner class Adapter : RecyclerView.Adapter<Holder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            val view = parent.inflate(R.layout.item_service)
            return Holder(view)
        }

        override fun getItemViewType(position: Int): Int {
            return position
        }

        override fun getItemCount(): Int {
            return listService.size
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            holder.itemView.apply {
                tvTitle.text = listService[position]
                tvTitle.setTextColor(Color.parseColor(listServiceColor[position]))
                ivType.load(listServiceIcon[position])
                rootView.setOnClickListener {
                    if (position == 0) {
                        startActivity(Intent(this@SelectServiceActivity, LoginActivity::class.java))
                        finish()
                    }
                }
            }
        }

    }

    inner class Holder(view: View) : RecyclerView.ViewHolder(view)
}