package ru.npf_paker.wms;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import data.Act;
import data.ActItem;

public class QntDialogFragment extends DialogFragment {
    private static QntDialogFragment fragment = null;
    Act act;
    Integer quantity;

    public static QntDialogFragment newInstance(Act act) {
        if (fragment == null)
            fragment = new QntDialogFragment();
        fragment.act = act;
        fragment.quantity = 0;
        return fragment;
    }

    @Override
    public void onStart() {
        getDialog().getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        super.onStart();
    }

    //тэг для передачи результата обратно
    public static final String TAG_QNT_SELECTED = "qnt";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_qnt_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        EditText qntText = (EditText) view.findViewById(R.id.qntText);
        builder.setView(view)
                .setPositiveButton("Ok", (dialog, which) -> {
                    int i = 0;
                    try {
                        i = Integer.parseInt(qntText.getText().toString());
                    } catch (NumberFormatException e) {
                    }
                    if (i > 0 && act != null) {
                        quantity = i;
                        for (ActItem item : act.items) {
                            item.quantity = quantity;
                        }
                        if (act.income) {

                        } else
                            ((MainActivity) getActivity()).OutcomeMode1SubmitForm(act);
                        //отправляем результат обратно
//                            Intent intent = new Intent();
//                            intent.putExtra(TAG_QNT_SELECTED, i);
//                            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                    }
                });
        return builder.create();
    }

}
