package com.snaptall.app

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private lateinit var urlInput: EditText
    private lateinit var statusText: TextView
    private lateinit var linksList: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestNotificationPermission()
        buildUi()
    }

    private fun buildUi() {
        val scroll = ScrollView(this)
        val page = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(24), dp(20), dp(28))
            setBackgroundColor(0xFF07111F.toInt())
            layoutDirection = View.LAYOUT_DIRECTION_RTL
        }
        scroll.addView(page)

        val logo = TextView(this).apply {
            text = "⬇ SnaptAll"
            textSize = 32f
            gravity = Gravity.CENTER
            setTextColor(0xFF00C7F2.toInt())
            setPadding(0, dp(8), 0, dp(8))
        }
        page.addView(logo, matchWrap())

        page.addView(label("حمّل الفيديوهات والملفات المسموح بها عبر الرابط", 16f))
        urlInput = EditText(this).apply {
            hint = "الصق رابط الفيديو هنا"
            textSize = 15f
            setSingleLine(false)
            minLines = 2
            setTextColor(0xFFFFFFFF.toInt())
            setHintTextColor(0xFF9AA8BD.toInt())
            setPadding(dp(12), dp(12), dp(12), dp(12))
            setBackgroundColor(0xFF17243A.toInt())
        }
        page.addView(urlInput, matchWrap())

        val buttons = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        buttons.addView(button("لصق الرابط") {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip: ClipData? = clipboard.primaryClip
            if (clip != null && clip.itemCount > 0) urlInput.setText(clip.getItemAt(0).coerceToText(this@MainActivity))
            else toast("الحافظة فارغة")
        }, LinearLayout.LayoutParams(0, dp(52), 1f))
        buttons.addView(button("إضافة للقائمة") { addLinks() }, LinearLayout.LayoutParams(0, dp(52), 1f))
        page.addView(buttons)

        page.addView(label("روابط التنزيل", 20f))
        linksList = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        page.addView(linksList, matchWrap())

        page.addView(button("بدء التنزيل") {
            statusText.text = "هذه النسخة هي واجهة مشروع أساسية. يلزم ربط مصدر تنزيل مصرح به أو API رسمي قبل بدء تنزيل فعلي."
        }, matchWrap())

        statusText = label("جاهز لإضافة الروابط. يدعم إدخال عدة روابط، رابطاً في كل سطر.", 14f)
        statusText.setTextColor(0xFFB9C8DC.toInt())
        page.addView(statusText, matchWrap())

        page.addView(label("ملاحظة: لا يمكن ضمان تنزيل كل منصة أو تنزيل فيديوهات الحسابات الخاصة. يجب احترام حقوق النشر وشروط المنصات.", 12f))
        setContentView(scroll)
    }

    private fun addLinks() {
        val raw = urlInput.text.toString()
        val links = raw.split(Regex("[\\n\\r\\s]+")).map { it.trim() }
            .filter { it.startsWith("https://") || it.startsWith("http://") }
            .distinct()
        if (links.isEmpty()) {
            toast("الصق رابطاً صحيحاً أو عدة روابط، كل رابط في سطر")
            return
        }
        links.forEach { link ->
            val row = TextView(this).apply {
                text = "• $link"
                textSize = 13f
                setTextColor(0xFFE6EDF7.toInt())
                setPadding(dp(10), dp(10), dp(10), dp(10))
                setBackgroundColor(0xFF142137.toInt())
            }
            val params = LinearLayout.LayoutParams(-1, -2)
            params.setMargins(0, dp(4), 0, dp(4))
            linksList.addView(row, params)
        }
        statusText.text = "تمت إضافة ${links.size} رابط/روابط. هذه الروابط لم تُنزّل بعد."
        urlInput.text.clear()
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 100)
        }
    }

    private fun label(text: String, size: Float) = TextView(this).apply {
        this.text = text
        textSize = size
        setTextColor(0xFFF5F7FB.toInt())
        setPadding(0, dp(12), 0, dp(10))
    }

    private fun button(text: String, action: () -> Unit) = Button(this).apply {
        this.text = text
        textSize = 14f
        isAllCaps = false
        setOnClickListener { action() }
    }

    private fun matchWrap() = LinearLayout.LayoutParams(-1, -2).apply {
        bottomMargin = dp(10)
    }

    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()
    private fun toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}
