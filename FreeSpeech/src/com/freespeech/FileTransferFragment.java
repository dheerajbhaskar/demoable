package com.freespeech;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kaloer.filepicker.FilePickerActivity;

public class FileTransferFragment extends Fragment {

	private TextView mFilePathTextView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.file_transfer, container, false);
		mFilePathTextView = (TextView) view.findViewById(R.id.lbl_file_name);
		return view;

	}

	public void onClickChooseFile_actual(View view) {
		System.out.println("actual listener clicked");

		switch (view.getId()) {
		case R.id.btn_file_chooser:
			// Create a new Intent for the file picker activity
			Intent intent = new Intent(getActivity(), FilePickerActivity.class);

			// Set the initial directory to be the sdcard
			// intent.putExtra(FilePickerActivity.EXTRA_FILE_PATH,
			// Environment.getExternalStorageDirectory());

			// Show hidden files
			// intent.putExtra(FilePickerActivity.EXTRA_SHOW_HIDDEN_FILES,
			// true);

			// Only make .png files visible
			// ArrayList<String> extensions = new ArrayList<String>();
			// extensions.add(".png");
			// intent.putExtra(FilePickerActivity.EXTRA_ACCEPTED_FILE_EXTENSIONS,
			// extensions);

			// Start the activity
			startActivityForResult(intent, CONSTANTS.REQUEST_PICK_FILE);
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case CONSTANTS.REQUEST_PICK_FILE:
				if (data.hasExtra(FilePickerActivity.EXTRA_FILE_PATH)) {
					// Get the file path
					File f = new File(
							data.getStringExtra(FilePickerActivity.EXTRA_FILE_PATH));

					// Set the file path text view
					mFilePathTextView.setText(f.getName());
				}
			}
		}
	}
}
