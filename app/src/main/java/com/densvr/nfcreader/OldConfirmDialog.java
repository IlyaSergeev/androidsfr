package com.densvr.nfcreader;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.TextView;

@Deprecated
public class OldConfirmDialog extends AlertDialog.Builder {

	private TextView view;
	OnClickListener onClickListener;
	
	
	public static interface OnClickListener {
		/**
		 * true - yes clicked, false - no clicked
		 * dialog will be finished automatically
		 * @param bResult
		 */
		public void onClick(boolean bResult);
	}
	
	protected OldConfirmDialog(Context context, String title, String text, OnClickListener ocl) {
		super(context);
		this.setTitle(title);
		if (!text.isEmpty()) {
			view  = new TextView(context);
			view.setText(text);
			this.setView(view);
		}
		this.onClickListener = ocl;
		this.setPositiveButton("да", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (onClickListener != null) {
					onClickListener.onClick(true);
				}
				dialog.cancel();
			}
		});
		this.setNegativeButton("нет", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (onClickListener != null) {
					onClickListener.onClick(false);
				}
				dialog.cancel();
			}
		});
	}

	
	
	public static void showDialog(Context context, String title, String text, OnClickListener onClickListener) {
		OldConfirmDialog d = new OldConfirmDialog(context, title, text, onClickListener);
		d.show(); 
	}
	
	
};
