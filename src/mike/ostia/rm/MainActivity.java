package mike.ostia.rm;

import mike.ostia.rm.AnalogClockView.OnTimeChangedListener;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity implements OnTimeChangedListener {

	private AnalogClockView analogClockView;
	private TextView clockText;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initializeViews();
	}

	private void initializeViews() {
		analogClockView = (AnalogClockView) findViewById(R.id.cvClock);
		clockText = (TextView) findViewById(R.id.tvClock);
		analogClockView.setOnTimeChangedListener(this);
	}

	public void onTimeChanged(View v, int hour, int minute) {
		clockText.setText(hour + " : " + minute);
	}

}
