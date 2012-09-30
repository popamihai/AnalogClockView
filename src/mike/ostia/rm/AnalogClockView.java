package mike.ostia.rm;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;

public class AnalogClockView extends View {
	private Paint hourPaint = new Paint();
	private Paint minutePaint = new Paint();
	private Paint framePaint = new Paint();
	private Paint amTextPaint = new Paint();
	private Paint pmTextPaint = new Paint();
	private Paint hourMinuteSelectionPaint = new Paint();
	private float radius;
	private float centerX;
	private float centerY;
	private PointF[] hourCoordinates = new PointF[12];
	private PointF[] minuteCoordinates = new PointF[12];
	private float selectedRadius;
	private int selectedHour;
	private int selectedMinute;
	private OnTimeChangedListener listener;
	private Path AMPath;
	private Path PMPath;
	private Path hourMinuteSelectionPath;

	private boolean am = true;
	private boolean initialSetup = true;
	private boolean hourSelection = false;

	/**
	 * Interface used to send selected hour, minute data to other classes
	 * 
	 */
	public interface OnTimeChangedListener {
		void onTimeChanged(View v, int hour, int minute);
	}

	/**
	 * View constructor - used for the initial setup
	 * 
	 * @param context
	 * @param attrs
	 */
	public AnalogClockView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialSetup = true;
	}

	/**
	 * Draws on the canvas
	 */
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (initialSetup) {
			setupFrame();
			setupHours();
			setupMinutes();
			setTextCoordinates();
			initialSetup = false;
		}

		canvas.drawCircle(centerX, centerY, radius, framePaint);
		if (hourSelection) {
			canvas.drawTextOnPath("AM", AMPath, 0, 0, amTextPaint);
			canvas.drawTextOnPath("PM", PMPath, 0, 0, pmTextPaint);
			canvas.drawTextOnPath("HOUR", hourMinuteSelectionPath, 0, 0,
					hourMinuteSelectionPaint);

			canvas.drawCircle(hourCoordinates[selectedHour].x,
					hourCoordinates[selectedHour].y, selectedRadius, hourPaint);

			for (int i = 0; i < hourCoordinates.length; i++) {
				if (i == 0) {
					if (am) {
						canvas.drawText("0", hourCoordinates[i].x,
								hourCoordinates[i].y, hourPaint);
					} else {
						canvas.drawText(i + 12 + "", hourCoordinates[i].x,
								hourCoordinates[i].y, hourPaint);
					}

				} else {
					canvas.drawText("" + i, hourCoordinates[i].x,
							hourCoordinates[i].y, hourPaint);
				}

			}

		} else {

			canvas.drawCircle(minuteCoordinates[selectedMinute].x,
					minuteCoordinates[selectedMinute].y, selectedRadius,
					minutePaint);

			canvas.drawTextOnPath("MINUTE", hourMinuteSelectionPath, 0, 0,
					hourMinuteSelectionPaint);

			for (int i = 0; i < minuteCoordinates.length; i++) {
				canvas.drawText(i * 5 + "", minuteCoordinates[i].x,
						minuteCoordinates[i].y, minutePaint);
			}
		}
	}

	/**
	 * Sets the paint and the coordinates of the am/pm and type labels
	 */
	private void setTextCoordinates() {
		amTextPaint.setAntiAlias(true);
		amTextPaint.setColor(Color.GREEN);
		amTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		amTextPaint.setStrokeJoin(Paint.Join.ROUND);
		amTextPaint.setTextSize(radius * 0.2f);
		amTextPaint.setTextAlign(Align.CENTER);

		pmTextPaint.setAntiAlias(true);
		pmTextPaint.setColor(Color.BLACK);
		pmTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		pmTextPaint.setStrokeJoin(Paint.Join.ROUND);
		pmTextPaint.setTextSize(radius * 0.2f);
		pmTextPaint.setTextAlign(Align.CENTER);

		hourMinuteSelectionPaint.setAntiAlias(true);
		hourMinuteSelectionPaint.setColor(Color.BLACK);
		hourMinuteSelectionPaint.setStyle(Paint.Style.STROKE);
		hourMinuteSelectionPaint.setStrokeJoin(Paint.Join.ROUND);
		hourMinuteSelectionPaint.setTextSize(radius * 0.2f);
		hourMinuteSelectionPaint.setTextAlign(Align.CENTER);

		hourMinuteSelectionPath = new Path();
		hourMinuteSelectionPath.moveTo(centerX - radius * 0.5f, centerY);
		hourMinuteSelectionPath.lineTo(centerX + radius * 0.5f, centerY);

		AMPath = new Path();
		AMPath.moveTo(centerX - radius, centerY - radius * 0.8f);
		AMPath.lineTo(centerX - radius + radius * 0.4f, centerY - radius * 0.8f);

		PMPath = new Path();
		PMPath.moveTo(centerX + radius - radius * 0.4f, centerY - radius * 0.8f);
		PMPath.lineTo(centerX + radius, centerY - radius * 0.8f);
	}

	/**
	 * Set the paint and the coordinates needed to draw the frame of the clock
	 */
	private void setupFrame() {

		framePaint.setAntiAlias(true);
		framePaint.setColor(Color.BLACK);
		framePaint.setStyle(Paint.Style.STROKE);
		framePaint.setStrokeJoin(Paint.Join.ROUND);
		framePaint.setStrokeWidth(radius * 0.2f);

		centerX = this.getWidth() / 2;
		centerY = this.getHeight() / 2;
		if (this.getWidth() > this.getHeight()) {
			radius = this.getHeight() / 2;
		} else {
			radius = this.getWidth() / 2;
		}
		selectedRadius = radius / 10;
	}

	/**
	 * Set the paint and the coordinates of the hour labels
	 */
	private void setupHours() {
		hourPaint.setAntiAlias(true);
		hourPaint.setColor(Color.RED);
		hourPaint.setStyle(Paint.Style.STROKE);
		hourPaint.setStrokeJoin(Paint.Join.ROUND);
		hourPaint.setStrokeWidth(radius * 0.01f);
		hourPaint.setTextSize(radius * 0.1f);
		hourPaint.setTextAlign(Align.CENTER);

		for (int i = 0; i < 12; i++) {
			hourCoordinates[i] = new PointF();
			float angle = (i * 30) * (float) Math.PI / 180;
			hourCoordinates[i].x = centerX + radius * 0.8f
					* FloatMath.sin(angle);
			hourCoordinates[i].y = centerY - radius * 0.8f
					* FloatMath.cos(angle);
		}
	}

	/**
	 * Set the paint and the coordinates of the minute labels
	 */
	private void setupMinutes() {

		minutePaint.setAntiAlias(true);
		minutePaint.setColor(Color.BLUE);
		minutePaint.setStyle(Paint.Style.STROKE);
		minutePaint.setStrokeJoin(Paint.Join.ROUND);
		minutePaint.setStrokeWidth(radius * 0.01f);
		minutePaint.setTextSize(radius * 0.1f);
		minutePaint.setTextAlign(Align.CENTER);

		for (int i = 0; i < 12; i++) {
			minuteCoordinates[i] = new PointF();
			float angle = (i * 30) * (float) Math.PI / 180;
			minuteCoordinates[i].x = centerX + radius * 0.8f
					* FloatMath.sin(angle);
			minuteCoordinates[i].y = centerY - radius * 0.8f
					* FloatMath.cos(angle);
		}
	}

	/**
	 * Update the coordinates of the selected minute
	 * 
	 * @param x
	 *            - the x coordinate of the selected minute
	 * @param y
	 *            - the y coordinate of the selected minute
	 */
	private void selectMinute(float x, float y) {
		for (int i = 0; i < 12; i++) {
			if (x > (minuteCoordinates[i].x - radius / 8)
					&& x < (minuteCoordinates[i].x + radius / 8)
					&& y < (minuteCoordinates[i].y + radius / 8)
					&& y > (minuteCoordinates[i].y - radius / 8)) {
				selectedMinute = i;
			}
		}
	}

	/**
	 * Update the coordinates of the selected hour (when tocuhed)
	 * 
	 * @param x
	 *            - x coordinate of the touch
	 * @param y
	 *            - y coordinate of the touch
	 */
	private void selectHour(float x, float y) {
		for (int i = 0; i < 12; i++) {
			if (x > (hourCoordinates[i].x - radius / 6)
					&& x < (hourCoordinates[i].x + radius / 6)
					&& y < (hourCoordinates[i].y + radius / 6)
					&& y > (hourCoordinates[i].y - radius / 6)) {
				selectedHour = i;
			}
		}
	}

	/**
	 * Checks if the touch position is on one of the two AM/PM buttons Updates
	 * the paint and the meridian variable accordingly
	 * 
	 * @param x
	 * @param y
	 */
	private void selectAMPM(float x, float y) {
		if (x > centerX - radius && x < centerX - radius + radius * 0.4f
				&& y > centerY - radius && y < centerY - radius * 0.8f) {
			am = true;
			amTextPaint.setColor(Color.GREEN);
			pmTextPaint.setColor(Color.BLACK);
		}
		if (x > centerX + radius - radius * 0.4f && x < centerX + radius
				&& y > centerY - radius && y < centerY - radius * 0.8f) {
			am = false;
			amTextPaint.setColor(Color.BLACK);
			pmTextPaint.setColor(Color.GREEN);
		}
	}

	/**
	 * Checks if the touch position is in the center of the clock and changes
	 * the selection type accordingly (hour/minute)
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean selectSelectionType(float x, float y) {
		if (x < (centerX + radius / 3) && x > (centerX - radius / 3)
				&& y < (centerY + radius / 3) && y > (centerY - radius / 3)) {
			hourSelection = !hourSelection;
			return true;
		}
		return false;
	}

	/**
	 * Touch listener method Retrieves the x,y position and updates the view
	 * accordingly. Used to: change the selection type (hour, minute), select
	 * minute, select hour, update the data sent to the registered timechanged
	 * listener.
	 */
	public boolean onTouchEvent(MotionEvent event) {
		float eventX = event.getX();
		float eventY = event.getY();
		selectAMPM(eventX, eventY);
		if (MotionEvent.ACTION_DOWN == event.getAction()
				&& selectSelectionType(eventX, eventY) == true) {
			invalidate();
			return true;
		} else {
			if (hourSelection) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					selectHour(eventX, eventY);
					break;
				case MotionEvent.ACTION_MOVE:
					selectHour(eventX, eventY);
					break;
				default:
					return false;
				}
			} else {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					selectMinute(eventX, eventY);
					break;
				case MotionEvent.ACTION_MOVE:
					selectMinute(eventX, eventY);
					break;
				default:
					return false;
				}

			}
			if (listener != null) {
				if (am) {
					listener.onTimeChanged(this, selectedHour,
							selectedMinute * 5);
				} else {
					if (selectedHour == 0) {
						listener.onTimeChanged(this, selectedHour + 24,
								selectedMinute * 5);
					} else {
						listener.onTimeChanged(this, selectedHour + 12,
								selectedMinute * 5);
					}

				}
			}
			invalidate();
			return true;
		}
	}

	/**
	 * Listener registration. Needed to send hour, minute data to the classes
	 * that implement the time changed interface
	 * 
	 * @param l
	 */
	public void setOnTimeChangedListener(OnTimeChangedListener l) {
		listener = l;
	}

}
