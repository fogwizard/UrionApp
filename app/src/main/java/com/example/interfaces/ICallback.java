package com.example.interfaces;

import com.example.urionbean.Error;
import com.example.urionbean.IBean;
import com.example.urionbean.Msg;

public interface ICallback {
	public void onReceive(IBean bean);

	public void onMessage(Msg message);

	public void onError(Error error);
}
