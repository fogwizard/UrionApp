package com.example.urionservice;

import com.example.urionbean.IBean;
import com.example.urionbean.Msg;
import com.example.urionbean.Error;

public interface ICallback {
	public void onReceive(IBean bean);

	public void onMessage(Msg message);

	public void onError(Error error);
}
