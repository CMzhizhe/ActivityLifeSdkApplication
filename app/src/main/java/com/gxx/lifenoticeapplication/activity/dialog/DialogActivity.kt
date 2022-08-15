package com.gxx.lifenoticeapplication.activity.dialog

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.gxx.lifenoticeapplication.R

class DialogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_activity)

        this.findViewById<Button>(R.id.btn_add_people_y).setOnClickListener {
            this.finish()
        }
    }
}