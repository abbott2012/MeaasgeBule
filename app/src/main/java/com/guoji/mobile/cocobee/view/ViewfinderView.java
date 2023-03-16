package com.guoji.mobile.cocobee.view;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public final class ViewfinderView extends View {
	private int checkLeftFrame = 0;// ���֤������Ƿ���ڻ��߶���
	private int checkTopFrame = 0;// ���֤���ϱ��Ƿ���ڻ��߶���
	private int checkRightFrame = 0;// ���֤���ұ��Ƿ���ڻ��߶���
	private int checkBottomFrame = 0;// ���֤���±��Ƿ���ڻ��߶���
	private static final int[] SCANNER_ALPHA = { 0, 64, 128, 192, 255, 192,
			128, 64 };
	/**
	 * ˢ�½����ʱ��
	 */
	private static final long ANIMATION_DELAY = 50L;

	public int getCheckLeftFrame() {
		return checkLeftFrame;
	}

	public void setCheckLeftFrame(int checkLeftFrame) {
		this.checkLeftFrame = checkLeftFrame;
	}

	public int getCheckTopFrame() {
		return checkTopFrame;
	}

	public void setCheckTopFrame(int checkTopFrame) {
		this.checkTopFrame = checkTopFrame;
	}

	public int getCheckRightFrame() {
		return checkRightFrame;
	}

	public void setCheckRightFrame(int checkRightFrame) {
		this.checkRightFrame = checkRightFrame;
	}

	public int getCheckBottomFrame() {
		return checkBottomFrame;
	}

	public void setCheckBottomFrame(int checkBottomFrame) {
		this.checkBottomFrame = checkBottomFrame;
	}

	private static final int OPAQUE = 0xFF;
	/**
	 * �ж���Ļ����ת�Ķ����Ӧ�ķ���ֵ�磺0,1,2,3
	 */
	private static int directtion = 0;
	/**
	 * ��idcardType==0ʱ������MRZʶ�𣬵�idcardType==1ʱ������ȫ����ʶ��
	 */
	private static int idcardType = 0;

	public static int getIdcardType() {
		return idcardType;
	}

	public static void setIdcardType(int idcardType) {
		ViewfinderView.idcardType = idcardType;
	}

	public int getDirecttion() {
		return directtion;
	}

	public void setDirecttion(int directtion) {
		this.directtion = directtion;
	}

	private final Paint paint;
	private Bitmap resultBitmap;
	// private final int maskColor;
	// private final int resultColor;
	// private final int frameColor;
	// private final int laserColor;
	private int scannerAlpha;
	/**
	 * �м们���ߵ����λ��
	 */
	private int slideTop;
	private int slideTop1;

	/**
	 * �м们���ߵ���׶�λ��
	 */
	private int slideBottom;
	/**
	 * �м�������ÿ��ˢ���ƶ��ľ���
	 */
	private static final int SPEEN_DISTANCE = 10;
	/**
	 * ɨ����е��ߵĿ��
	 */
	private static final int MIDDLE_LINE_WIDTH = 6;
	private boolean isFirst = false;
	/**
	 * ���ܱ߿�Ŀ��
	 */
	private static final int FRAME_LINE_WIDTH = 4;
	private Rect frame;
	private int width;
	private int height;

	public ViewfinderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		paint = new Paint();
		scannerAlpha = 0;
	}

	@Override
	public void onDraw(Canvas canvas) {
		width = canvas.getWidth();
		height = canvas.getHeight();
		// һ����豸���ǵ����С�ڸ߶ȵ�ʱ���ֻ���ת�ĽǶ�Ϊ0��2�������ǵ��豸����ȴ��ڸ߶ȵ�ʱ���ֻ���ת�ĽǶ�Ϊ0��2
		if (directtion == 0 || directtion == 2) {
			if (width > height) {
				if (!Build.MODEL.equals("GT-P7500")
						&& !Build.MODEL.equals("SM-T520")) {
					width = 3 * width / 4;
				}

			}

			if (idcardType == 3000) {
				// MRZʶ��
				/**
				 * ������ξ����м���ʾ���Ǹ����
				 */
				frame = new Rect((int) (width * 0.15), height / 3,
						(int) (width * 0.8), (2 * height) / 3);
			} else if (idcardType == 2 || idcardType == 22
					|| idcardType == 1030 || idcardType == 1031
					|| idcardType == 1032 || idcardType == 1005
					|| idcardType == 1001 || idcardType == 2001
					|| idcardType == 2004 || idcardType == 2002
					|| idcardType == 2003 || idcardType == 14
					|| idcardType == 15 || idcardType == 25 || idcardType == 26) {

				frame = new Rect((int) (width * 0.025),
						(int) (height - 0.59375 * width) / 2,
						(int) (width * 0.975),
						(int) (height + 0.59375 * width) / 2);
			}

			else if (idcardType == 5 || idcardType == 6) {
				frame = new Rect((int) (width * 0.025),
						(int) (height - 0.64 * width) / 2,
						(int) (width * 0.975),
						(int) (height + 0.64 * width) / 2);
			} else {
				// for page
				frame = new Rect((int) (width * 0.025),
						(int) (height - 0.659 * width) / 2,
						(int) (width * 0.975),
						(int) (height + 0.659* width) / 2);
			}
			// }
			if (frame == null) {
				return;
			}
			// ��ʼ���м��߻��������ϱߺ����±�
			if (!isFirst) {
				isFirst = true;
				slideTop = height / 3;
				slideBottom = 2 * height / 3;
				slideTop1 = width / 2;
			}
			// ����ɨ����������Ӱ���֣����ĸ����֣�ɨ�������浽��Ļ���棬ɨ�������浽��Ļ����
			// ɨ��������浽��Ļ��ߣ�ɨ�����ұߵ���Ļ�ұ�
			paint.setColor(Color.argb(48, 0, 0, 0));
			canvas.drawRect(0, 0, width, frame.top, paint);
			canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
			canvas.drawRect(frame.right + 1, frame.top, width,
					frame.bottom + 1, paint);
			canvas.drawRect(0, frame.bottom + 1, width, height, paint);

			// �����������ر߿����ɫ�߿�
			paint.setColor(Color.rgb(243, 153, 18));
			if (idcardType == 3000) {
				canvas.drawRect(frame.left + FRAME_LINE_WIDTH - 2, frame.top,
						frame.right - FRAME_LINE_WIDTH + 2, frame.top
								+ FRAME_LINE_WIDTH, paint);// �ϱ�
				canvas.drawRect(frame.left + FRAME_LINE_WIDTH - 2, frame.top,
						frame.left + FRAME_LINE_WIDTH + 2, frame.bottom
								+ FRAME_LINE_WIDTH, paint);// ���
				canvas.drawRect(frame.right - FRAME_LINE_WIDTH - 2, frame.top,
						frame.right - FRAME_LINE_WIDTH + 2, frame.bottom
								+ FRAME_LINE_WIDTH, paint);// �ұ�
				canvas.drawRect(frame.left + FRAME_LINE_WIDTH - 2,
						frame.bottom, frame.right - FRAME_LINE_WIDTH + 2,
						frame.bottom + FRAME_LINE_WIDTH, paint);// �ױ�
				// ����һ������ɨ��ĺ���
				// paint.setColor(laserColor);
				paint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
				scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
			}

			{
				canvas.drawRect(frame.left + FRAME_LINE_WIDTH - 2, frame.top,
						frame.left + FRAME_LINE_WIDTH - 2 + 50, frame.top
								+ FRAME_LINE_WIDTH, paint);
				canvas.drawRect(frame.left + FRAME_LINE_WIDTH - 2, frame.top,
						frame.left + FRAME_LINE_WIDTH + 2, frame.top + 50,
						paint);// ���Ͻ�
				canvas.drawRect(frame.right - FRAME_LINE_WIDTH - 2, frame.top,
						frame.right - FRAME_LINE_WIDTH + 2, frame.top + 50,
						paint);
				canvas.drawRect(frame.right - FRAME_LINE_WIDTH - 2 - 50,
						frame.top, frame.right - FRAME_LINE_WIDTH + 2,
						frame.top + FRAME_LINE_WIDTH, paint);// ���Ͻ�
				canvas.drawRect(frame.left + FRAME_LINE_WIDTH - 2,
						frame.bottom - 50, frame.left + FRAME_LINE_WIDTH + 2,
						frame.bottom, paint);
				canvas.drawRect(frame.left + FRAME_LINE_WIDTH - 2, frame.bottom
						- FRAME_LINE_WIDTH, frame.left + FRAME_LINE_WIDTH - 2
						+ 50, frame.bottom, paint); // ���½�
				canvas.drawRect(frame.right - FRAME_LINE_WIDTH - 2,
						frame.bottom - 50, frame.right - FRAME_LINE_WIDTH + 2,
						frame.bottom, paint);
				canvas.drawRect(frame.right - FRAME_LINE_WIDTH - 2 - 50,
						frame.bottom - FRAME_LINE_WIDTH, frame.right
								- FRAME_LINE_WIDTH - 2, frame.bottom, paint); // ���½�
				// ����⵽֤����߾ͻử����ߵ���ʾ��
				if (checkLeftFrame == 1)
					canvas.drawRect(frame.left + FRAME_LINE_WIDTH - 2,
							frame.top, frame.left + FRAME_LINE_WIDTH + 2,
							frame.bottom, paint);// ���
				// ����⵽֤���ϱ߾ͻử����ߵ���ʾ��
				if (checkTopFrame == 1)
					canvas.drawRect(frame.left + FRAME_LINE_WIDTH - 2,
							frame.top, frame.right - FRAME_LINE_WIDTH + 2,
							frame.top + FRAME_LINE_WIDTH, paint);// �ϱ�
				// ����⵽֤���ұ߾ͻử����ߵ���ʾ��
				if (checkRightFrame == 1)
					canvas.drawRect(frame.right - FRAME_LINE_WIDTH - 2,
							frame.top, frame.right - FRAME_LINE_WIDTH + 2,
							frame.bottom, paint);// �ұ�
				// ����⵽֤���ױ߾ͻử����ߵ���ʾ��
				if (checkRightFrame == 1)
					canvas.drawRect(frame.left + FRAME_LINE_WIDTH - 2,
							frame.bottom - FRAME_LINE_WIDTH, frame.right
									- FRAME_LINE_WIDTH - 2, frame.bottom, paint); // ���½�
			}

			// һ����豸���ǵ���ȴ��ڸ߶ȵ�ʱ���ֻ���ת�ĽǶ�Ϊ1��3�������ǵ��豸�����С�ڸ߶ȵ�ʱ���ֻ���ת�ĽǶ�Ϊ1��3
		} else if (directtion == 1 || directtion == 3) {
			if (width < height) {
				width = 4 * width / 3;
				height = 3 * height / 4;

				/**
				 * ������ξ����м���ʾ���Ǹ����
				 */
				frame = new Rect(0, height / 2, 3 * width / 4, 2 * height / 3);

				if (frame == null) {
					return;
				}
				// ��ʼ���м��߻��������ϱߺ����±�
				if (!isFirst) {
					isFirst = true;
					slideTop = width / 3;
					slideBottom = 2 * width / 3;
					slideTop1 = height / 2;
				}
				// ����ɨ����������Ӱ���֣����ĸ����֣�ɨ�������浽��Ļ���棬ɨ�������浽��Ļ����
				// ɨ��������浽��Ļ��ߣ�ɨ�����ұߵ���Ļ�ұ�
				paint.setColor(Color.argb(48, 0, 0, 0));
				canvas.drawRect(0, 0, width, frame.top, paint);
				canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1,
						paint);
				canvas.drawRect(frame.right + 1, frame.top, width,
						frame.bottom + 1, paint);
				canvas.drawRect(0, frame.bottom + 1, width, height, paint);

				// �����������ر߿����ɫ�߿�
				paint.setColor(Color.rgb(243, 153, 18));
				canvas.drawRect(frame.left + FRAME_LINE_WIDTH - 2, frame.top,
						frame.right - FRAME_LINE_WIDTH + 2, frame.top
								+ FRAME_LINE_WIDTH, paint);// �ϱ�
				canvas.drawRect(frame.left + FRAME_LINE_WIDTH - 2, frame.top,
						frame.left + FRAME_LINE_WIDTH + 2, frame.bottom
								+ FRAME_LINE_WIDTH, paint);// ���
				canvas.drawRect(frame.right - FRAME_LINE_WIDTH - 2, frame.top,
						frame.right - FRAME_LINE_WIDTH + 2, frame.bottom
								+ FRAME_LINE_WIDTH, paint);// �ұ�
				canvas.drawRect(frame.left + FRAME_LINE_WIDTH - 2,
						frame.bottom, frame.right - FRAME_LINE_WIDTH + 2,
						frame.bottom + FRAME_LINE_WIDTH, paint);// �ױ�
				// ����һ������ɨ��ĺ���
				// paint.setColor(laserColor);
				paint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
				scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;

			} else {

				if (idcardType == 3000) {
					// MRZʶ��
					/**
					 * ������ξ����м���ʾ���Ǹ����
					 */
					frame = new Rect((int) (width * 0.2), height / 3,
							(int) (width * 0.85), 2 * height / 3);
				} else if (idcardType == 2 || idcardType == 22
						|| idcardType == 1030 || idcardType == 1031
						|| idcardType == 1032 || idcardType == 1005
						|| idcardType == 1001 || idcardType == 2001
						|| idcardType == 2004 || idcardType == 2002
						|| idcardType == 2003 || idcardType == 14
						|| idcardType == 15 || idcardType == 25
						|| idcardType == 26) {
					frame = new Rect((int) (width * 0.2),
							(int) (height - 0.41004673 * width) / 2,
							(int) (width * 0.85),
							(int) (height + 0.41004673 * width) / 2);

				} else if (idcardType == 5 || idcardType == 6) {
					frame = new Rect((int) (width * 0.24),
							(int) (height - 0.41004673 * width) / 2,
							(int) (width * 0.81),
							(int) (height + 0.41004673 * width) / 2);
				} else {
					// for page
					frame = new Rect((int) (width * 0.2),
							(int) (height - 0.45 * width) / 2,
							(int) (width * 0.85),
							(int) (height + 0.45 * width) / 2);
				}
				if (frame == null) {
					return;
				}
				// ��ʼ���м��߻��������ϱߺ����±�
				if (!isFirst) {
					isFirst = true;
					slideTop = width / 3;
					slideBottom = 2 * width / 3;
					slideTop1 = height / 3;
				}

				// ����ɨ����������Ӱ���֣����ĸ����֣�ɨ�������浽��Ļ���棬ɨ�������浽��Ļ����
				// ɨ��������浽��Ļ��ߣ�ɨ�����ұߵ���Ļ�ұ�
				paint.setColor(Color.argb(48, 0, 0, 0));
				canvas.drawRect(0, 0, width, frame.top, paint);
				canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1,
						paint);
				canvas.drawRect(frame.right + 1, frame.top, width,
						frame.bottom + 1, paint);
				canvas.drawRect(0, frame.bottom + 1, width, height, paint);

				// �����������ر߿����ɫ�߿�
				paint.setColor(Color.rgb(243, 153, 18));
				if (idcardType == 3000) {
					canvas.drawRect(frame.left + FRAME_LINE_WIDTH - 2,
							frame.top, frame.right - FRAME_LINE_WIDTH + 2,
							frame.top + FRAME_LINE_WIDTH, paint);// �ϱ�
					canvas.drawRect(frame.left + FRAME_LINE_WIDTH - 2,
							frame.top, frame.left + FRAME_LINE_WIDTH + 2,
							frame.bottom + FRAME_LINE_WIDTH, paint);// ���
					canvas.drawRect(frame.right - FRAME_LINE_WIDTH - 2,
							frame.top, frame.right - FRAME_LINE_WIDTH + 2,
							frame.bottom + FRAME_LINE_WIDTH, paint);// �ұ�
					canvas.drawRect(frame.left + FRAME_LINE_WIDTH - 2,
							frame.bottom, frame.right - FRAME_LINE_WIDTH + 2,
							frame.bottom + FRAME_LINE_WIDTH, paint);// �ױ�
				} else {

					canvas.drawRect(frame.left + FRAME_LINE_WIDTH - 2,
							frame.top, frame.left + FRAME_LINE_WIDTH - 2 + 50,
							frame.top + FRAME_LINE_WIDTH, paint);
					canvas.drawRect(frame.left + FRAME_LINE_WIDTH - 2,
							frame.top, frame.left + FRAME_LINE_WIDTH + 2,
							frame.top + 50, paint);// ���Ͻ�
					canvas.drawRect(frame.right - FRAME_LINE_WIDTH - 2,
							frame.top, frame.right - FRAME_LINE_WIDTH + 2,
							frame.top + 50, paint);
					canvas.drawRect(frame.right - FRAME_LINE_WIDTH - 2 - 50,
							frame.top, frame.right - FRAME_LINE_WIDTH + 2,
							frame.top + FRAME_LINE_WIDTH, paint);// ���Ͻ�
					canvas.drawRect(frame.left + FRAME_LINE_WIDTH - 2,
							frame.bottom - 50, frame.left + FRAME_LINE_WIDTH
									+ 2, frame.bottom, paint);
					canvas.drawRect(frame.left + FRAME_LINE_WIDTH - 2,
							frame.bottom - FRAME_LINE_WIDTH, frame.left
									+ FRAME_LINE_WIDTH - 2 + 50, frame.bottom,
							paint); // ���½�
					canvas.drawRect(frame.right - FRAME_LINE_WIDTH - 2,
							frame.bottom - 50, frame.right - FRAME_LINE_WIDTH
									+ 2, frame.bottom, paint);
					canvas.drawRect(frame.right - FRAME_LINE_WIDTH - 2 - 50,
							frame.bottom - FRAME_LINE_WIDTH, frame.right
									- FRAME_LINE_WIDTH - 2, frame.bottom, paint); // ���½�
					// ����⵽֤����߾ͻử����ߵ���ʾ��
					if (checkLeftFrame == 1)
						canvas.drawRect(frame.left + FRAME_LINE_WIDTH - 2,
								frame.top, frame.left + FRAME_LINE_WIDTH + 2,
								frame.bottom, paint);// ���
					// ����⵽֤���ϱ߾ͻử����ߵ���ʾ��
					if (checkTopFrame == 1)
						canvas.drawRect(frame.left + FRAME_LINE_WIDTH - 2,
								frame.top, frame.right - FRAME_LINE_WIDTH + 2,
								frame.top + FRAME_LINE_WIDTH, paint);// �ϱ�
					// ����⵽֤���ұ߾ͻử����ߵ���ʾ��
					if (checkRightFrame == 1)
						canvas.drawRect(frame.right - FRAME_LINE_WIDTH - 2,
								frame.top, frame.right - FRAME_LINE_WIDTH + 2,
								frame.bottom, paint);// �ұ�
					// ����⵽֤���ױ߾ͻử����ߵ���ʾ��
					if (checkBottomFrame == 1)
						canvas.drawRect(frame.left + FRAME_LINE_WIDTH - 2,
								frame.bottom - FRAME_LINE_WIDTH, frame.right
										- FRAME_LINE_WIDTH - 2, frame.bottom,
								paint); // ���½�
				}

			}
		}

		/**
		 * �����ǻ�ý���ʱ�����Ǹ��������Ļ������
		 */

		postInvalidateDelayed(ANIMATION_DELAY, 0, 0, width, height);

	}
}
