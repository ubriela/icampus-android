package edu.usc.imsc.services;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Controller extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		startService(new Intent(Controller.this, MessengerService.class));
//		stopService(new Intent(Controller.this, MessengerService.class));
	}
}