package com.example.zad2

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.zad2.database.GalleryCellDao
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class DialogEdit(private val desc: TextView,
                 private val key: String,
                 private val galleryCellDao: GalleryCellDao?
                 ) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var inflater = activity?.layoutInflater
        var view = inflater?.inflate(R.layout.edit_dialog, null)
        val input = view!!.findViewById<EditText>(R.id.textDes)
        input.inputType = InputType.TYPE_TEXT_FLAG_MULTI_LINE
        input.isSingleLine = false
        input.setText(desc.text.toString())
        input.setLines(5)
        return AlertDialog.Builder(requireContext())
                .setMessage(R.string.mes_edit)
                .setPositiveButton(R.string.ok,
                        DialogInterface.OnClickListener { dialog, id ->
                            // User clicked OK button
                            doAsync {
                                galleryCellDao?.setDesById(input.text.toString(), key)
                                uiThread {
                                    desc.text = input.text.toString();
                                }
                            }

                        })
                .setNegativeButton(R.string.cancel,
                        DialogInterface.OnClickListener { dialog, id ->
                        })
                .setView(view)
                .create()
    }
        companion object {
            const val TAG = "EditDialog"
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }
}
