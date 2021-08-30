package com.hackyeon.wormgame

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class EndGameDialogFragment : DialogFragment() {
    internal lateinit var listener: NoticeDialogListener

    interface NoticeDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val view = requireActivity().layoutInflater.inflate(R.layout.end_game_dialog, null)

            view.findViewById<Button>(R.id.endButton).setOnClickListener {
                listener.onDialogPositiveClick(this)
            }
            view.findViewById<Button>(R.id.replayButton).setOnClickListener {
                listener.onDialogNegativeClick(this)
            }

            builder.setView(view)
            builder.create()

        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as NoticeDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                (context.toString() +
                        "must implement NoticeDialogListener")
            )
        }

    }


}