/*
 *  Copyright (C) 2021 The OmniROM Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.omnirom.deviceregistration

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    val GOOGLE_REGISTER_URL = "https://www.google.com/android/uncertified"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<TextView>(R.id.open_browser).isEnabled = false

        val res = getGsfAndroidId()
        if (res != null) {
            findViewById<TextView>(R.id.gsf_id).text = res
            findViewById<TextView>(R.id.copy_gsf_id).isEnabled = true
        } else {
            findViewById<TextView>(R.id.gsf_id).text = "error"
            findViewById<TextView>(R.id.copy_gsf_id).isEnabled = false
        }

        findViewById<TextView>(R.id.copy_gsf_id).setOnClickListener {
            val clipboard: ClipboardManager =
                getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("GSF id", findViewById<TextView>(R.id.gsf_id).text)
            clipboard.setPrimaryClip(clip)
            findViewById<TextView>(R.id.open_browser).isEnabled = true
        }

        findViewById<TextView>(R.id.open_browser).setOnClickListener {
            try {
                val openURL = Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_REGISTER_URL))
                startActivity(openURL)
            } catch (e: Exception) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle(getString(R.string.browser_dialog_title))
                builder.setPositiveButton(android.R.string.ok, null)
                builder.setMessage(resources.getString(R.string.browser_dialog_text))
                builder.create().show()
            }
        }
    }

    private fun getGsfAndroidId(): String? {
        val URI: Uri = Uri.parse("content://com.google.android.gsf.gservices")
        val ID_KEY = "android_id"
        val params = arrayOf(ID_KEY)
        var c: Cursor? = contentResolver.query(URI, null, null, params, null)
        if (c == null) return null
        try {
            if (!c.moveToFirst() || c.columnCount < 2) {
                return null
            }
            return c.getString(1)
        } finally {
            if (!c.isClosed) c.close()
        }
    }
}