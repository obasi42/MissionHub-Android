package com.missionhub.fragment.dialog;

import android.content.DialogInterface;
import android.os.Bundle;

import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.Dialog;

public class CheckAllDialog extends BaseDialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getSupportActivity())
                .setTitle("Select All?")
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                setCheckAll(true);
                            }
                        }
                )
                .setNegativeButton("No, only select items in list",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                setCheckAll(false);
                            }
                        }
                )
                .create();
    }

    private void setCheckAll(boolean all) {
        ((CheckAllDialogListener) getParentFragment()).setAllChecked(all);
    }

    public interface CheckAllDialogListener {
        public void setAllChecked(boolean all);
    }

}