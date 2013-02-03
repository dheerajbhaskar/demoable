package com.freespeech;

import java.util.List;

import org.alljoyn.bus.sample.chat.AllJoynService;
import org.alljoyn.bus.sample.chat.ChatApplication;
import org.alljoyn.bus.sample.chat.Observable;
import org.alljoyn.bus.sample.chat.Observer;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class ChatFragment extends Fragment implements Observer {

	protected static final String TAG = "ChatFragment";

	private ArrayAdapter<String> mHistoryList;

	private ChatApplication mChatApplication = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.chat, container, false);

		mHistoryList = new ArrayAdapter<String>(getActivity(),
				android.R.layout.test_list_item);

		ListView hlv = (ListView) view.findViewById(R.id.listView1);
		hlv.setAdapter(mHistoryList);

		EditText messageBox = (EditText) view.findViewById(R.id.editText1);
		messageBox
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView view, int actionId,
							KeyEvent event) {
						if (actionId == EditorInfo.IME_NULL
								&& event.getAction() == KeyEvent.ACTION_UP) {
							String message = view.getText().toString();
							Log.i(TAG,
									"useMessage.onEditorAction(): got message "
											+ message + ")");
							mChatApplication.newLocalUserMessage(message);
							view.setText("");
						}
						return true;
					}
				});

		mChatApplication = (ChatApplication) getActivity().getApplication();
		mChatApplication.checkin();

		updateChannelState();
		updateHistory();

		mChatApplication.addObserver(this);

		return view;
	}

	public static final int DIALOG_JOIN_ID = 0;
	public static final int DIALOG_LEAVE_ID = 1;
	public static final int DIALOG_ALLJOYN_ERROR_ID = 2;

	@Override
	public synchronized void update(Observable o, Object arg) {
		Log.i(TAG, "update(" + arg + ")");
		String qualifier = (String) arg;

		if (qualifier.equals(ChatApplication.APPLICATION_QUIT_EVENT)) {
			Message message = mHandler
					.obtainMessage(HANDLE_APPLICATION_QUIT_EVENT);
			mHandler.sendMessage(message);
		}

		if (qualifier.equals(ChatApplication.HISTORY_CHANGED_EVENT)) {
			Message message = mHandler
					.obtainMessage(HANDLE_HISTORY_CHANGED_EVENT);
			mHandler.sendMessage(message);
		}

		if (qualifier.equals(ChatApplication.USE_CHANNEL_STATE_CHANGED_EVENT)) {
			Message message = mHandler
					.obtainMessage(HANDLE_CHANNEL_STATE_CHANGED_EVENT);
			mHandler.sendMessage(message);
		}

		if (qualifier.equals(ChatApplication.ALLJOYN_ERROR_EVENT)) {
			Message message = mHandler
					.obtainMessage(HANDLE_ALLJOYN_ERROR_EVENT);
			mHandler.sendMessage(message);
		}
	}

	private void updateHistory() {
		Log.i(TAG, "updateHistory()");
		mHistoryList.clear();
		List<String> messages = mChatApplication.getHistory();
		for (String message : messages) {
			mHistoryList.add(message);
		}
		mHistoryList.notifyDataSetChanged();
	}

	private void updateChannelState() {
		Log.i(TAG, "updateHistory()");
		AllJoynService.UseChannelState channelState = mChatApplication
				.useGetChannelState();
		String name = mChatApplication.useGetChannelName();
	}

	/**
	 * An AllJoyn error has happened. Since this activity pops up first we
	 * handle the general errors. We also handle our own errors.
	 */
	private void alljoynError() {
		// if (mChatApplication.getErrorModule() ==
		// ChatApplication.Module.GENERAL
		// || mChatApplication.getErrorModule() == ChatApplication.Module.USE) {
		// showDialog(DIALOG_ALLJOYN_ERROR_ID);
		// }
	}

	private static final int HANDLE_APPLICATION_QUIT_EVENT = 0;
	private static final int HANDLE_HISTORY_CHANGED_EVENT = 1;
	private static final int HANDLE_CHANNEL_STATE_CHANGED_EVENT = 2;
	private static final int HANDLE_ALLJOYN_ERROR_EVENT = 3;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLE_APPLICATION_QUIT_EVENT: {
				Log.i(TAG,
						"mHandler.handleMessage(): HANDLE_APPLICATION_QUIT_EVENT");
				getActivity().finish();
			}
				break;
			case HANDLE_HISTORY_CHANGED_EVENT: {
				Log.i(TAG,
						"mHandler.handleMessage(): HANDLE_HISTORY_CHANGED_EVENT");
				updateHistory();
				break;
			}
			case HANDLE_CHANNEL_STATE_CHANGED_EVENT: {
				Log.i(TAG,
						"mHandler.handleMessage(): HANDLE_CHANNEL_STATE_CHANGED_EVENT");
				updateChannelState();
				break;
			}
			case HANDLE_ALLJOYN_ERROR_EVENT: {
				Log.i(TAG,
						"mHandler.handleMessage(): HANDLE_ALLJOYN_ERROR_EVENT");
				alljoynError();
				break;
			}
			default:
				break;
			}
		}
	};

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy()");
		mChatApplication = (ChatApplication) getActivity().getApplication();
		mChatApplication.deleteObserver(this);
		super.onDestroy();
	}
}
