package com.guoji.mobile.cocobee.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Build;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import java.util.List;

@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
public class CameraParametersUtils {
	Camera.Parameters parameters;
	public int srcWidth, srcHeight;
	public int preWidth, preHeight;
	public int picWidth, picHeight;
	public int surfaceWidth, surfaceHeight;
	List<Size> list;
	private boolean isShowBorder = false;
	private Context context;

	public CameraParametersUtils(Context context) {
		this.context = context;
		setScreenSize(context);
	}

	/**
	 * ��ȡ�豸�����շֱ��ʵĿ�͸�
	 * 
	 * @param camera
	 */
	public void getCameraPicParameters(Camera camera) {
		isShowBorder = false;
		parameters = camera.getParameters();
		list = parameters.getSupportedPictureSizes();
		float ratioScreen = (float) srcWidth / srcHeight;
		for (int i = 0; i < list.size(); i++) {
			float ratioPicture = (float) list.get(i).width / list.get(i).height;
			if (ratioScreen == ratioPicture) {// �ж���Ļ��߱��Ƿ������տ�߱�һ�����һ��ִ�����´���
				if (list.get(i).width >= 1600 || list.get(i).height >= 1200) {// Ĭ��Ԥ����1600*1200Ϊ��׼
					if (picWidth == 0 && picHeight == 0) {// ��ʼֵ
						picWidth = list.get(i).width;
						picHeight = list.get(i).height;
					}
					if (list.get(0).width > list.get(list.size() - 1).width) {
						// ����һ��ֵ�������һ��ֵ
						if (picWidth > list.get(i).width
								|| picHeight > list.get(i).height) {
							// ���д���1600*1200�ķֱ��ʵ���С��֮ǰ���صķֱ��ʣ�����ȡ�м�ķֱ���
							picWidth = list.get(i).width;
							picHeight = list.get(i).height;
						}
					} else {
						// ����һ��ֵС�����һ��ֵ
						if (picWidth < list.get(i).width
								|| picHeight < list.get(i).height) {
							// ���֮ǰ�Ŀ�Ⱥ͸߶ȴ��ڵ���1600*1200���Ͳ���Ҫ��ɸѡ��
							if (picWidth >= 1600 || picHeight >= 1200) {

							} else {
								// Ϊ���ҵ����ʵķֱ��ʣ����picWidth��picHeightû�б�1600*1200��ľͼ������
								picWidth = list.get(i).width;
								picHeight = list.get(i).height;
							}
						}
					}
				}
			}
		}
		// ˵��û���ҵ�������Ҫ�ķֱ���
		if (picWidth == 0 || picHeight == 0) {
			isShowBorder = true;
			picWidth = list.get(0).width;
			picHeight = list.get(0).height;
			for (int i = 0; i < list.size(); i++) {

				if (list.get(0).width > list.get(list.size() - 1).width) {
					// ����һ��ֵ�������һ��ֵ
					if (picWidth >= list.get(i).width
							|| picHeight >= list.get(i).height) {
						// ����һ��ѡ������շֱ��ʿ���߸߶ȴ��ڱ��εĿ�Ⱥ͸߶�ʱ��ִ�����´���:
						if (list.get(i).width >= 1600) {
							// �����ε�Ԥ����Ⱥ͸߶ȴ���1280*720ʱִ�����´���
							picWidth = list.get(i).width;
							picHeight = list.get(i).height;

						}
					}
				} else {
					if (picWidth <= list.get(i).width
							|| picHeight <= list.get(i).height) {
						if (picWidth >= 1600 || picHeight >= 1200) {

						} else {
							// ����һ��ѡ���Ԥ���ֱ��ʿ���߸߶ȴ��ڱ��εĿ�Ⱥ͸߶�ʱ��ִ�����´���:
							if (list.get(i).width >= 1600) {
								// �����ε�Ԥ����Ⱥ͸߶ȴ���1280*720ʱִ�����´���
								picWidth = list.get(i).width;
								picHeight = list.get(i).height;

							}
						}

					}
				}
			}
		}
		// ���û���ҵ�����1280*720�ķֱ��ʵĻ���ȡ�����е����ֵ����ƥ��
		if (picWidth == 0 || picHeight == 0) {
			isShowBorder = true;
			if (list.get(0).width > list.get(list.size() - 1).width) {
				picWidth = list.get(0).width;
				picHeight = list.get(0).height;
			} else {
				picWidth = list.get(list.size() - 1).width;
				picHeight = list.get(list.size() - 1).height;
			}
		}
		if (isShowBorder) {
			if (ratioScreen > (float) picWidth / picHeight) {
				float rp = ratioScreen - ((float) preWidth / preHeight);
				// ��������0.02֮�ڣ����Ժ���
				if (rp <= 0.02) {
					surfaceWidth = srcWidth;
					surfaceHeight = srcHeight;
				} else {
					surfaceWidth = (int) (((float) preWidth / preHeight) * srcHeight);
					surfaceHeight = srcHeight;
				}
			} else {
				surfaceWidth = srcWidth;
				surfaceHeight = (int) (((float) picWidth / picHeight) * srcHeight);
			}
		} else {
			surfaceWidth = srcWidth;
			surfaceHeight = srcHeight;
		}
		System.out.println("surfaceWidth:" + surfaceWidth + "--surfaceHeight:"
				+ surfaceHeight);
	}

	/**
	 * ��ȡ�豸��Ԥ���ֱ��ʵĿ�͸�
	 * 
	 * @param camera
	 */
	public void getCameraPreParameters(Camera camera, int rotation,List<Size> list)

	{
		isShowBorder = false;
		// 荣耀七设备
		if ("PLK-TL01H".equals(Build.MODEL)) {
			preWidth = 1920;
			preHeight = 1080;
			surfaceWidth = srcWidth;
			surfaceHeight = srcHeight;
			return;
		}
		preWidth = 0;
		preHeight = 0;
		// �����豸
		float ratioScreen = 0;
		if (rotation == 0 || rotation == 180) {
			ratioScreen = (float) srcWidth / srcHeight;
		} else if (rotation == 90 || rotation == 270) {
			ratioScreen = (float) srcHeight / srcWidth;
		}

		for (int i = 0; i < list.size(); i++) {
			float ratioPreview = (float) list.get(i).width / list.get(i).height;
			if (ratioScreen == ratioPreview) {// �ж���Ļ��߱��Ƿ���Ԥ����߱�һ�����һ��ִ�����´���
				if (list.get(i).width >= 1280 || list.get(i).height >= 720) {// Ĭ��Ԥ����1280*720Ϊ��׼
					if (preWidth == 0 && preHeight == 0) {// ��ʼֵ
						preWidth = list.get(i).width;
						preHeight = list.get(i).height;
					}
					if (list.get(0).width > list.get(list.size() - 1).width) {
						// ����һ��ֵ�������һ��ֵ
						if (preWidth > list.get(i).width
								|| preHeight > list.get(i).height) {
							// ���д���1280*720�ķֱ��ʵ���С��֮ǰ���صķֱ��ʣ�����ȡ�м�ķֱ���
							preWidth = list.get(i).width;
							preHeight = list.get(i).height;
						}
					} else {
						// ����һ��ֵС�����һ��ֵ
						if (preWidth < list.get(i).width
								|| preHeight < list.get(i).height) {
							// ���֮ǰ�Ŀ�Ⱥ͸߶ȴ��ڵ���1280*720���Ͳ���Ҫ��ɸѡ��
							if (preWidth >= 1280 || preHeight >= 720) {

							} else {
								// Ϊ���ҵ����ʵķֱ��ʣ����preWidth��preHeightû�б�1280*720��ľͼ������
								preWidth = list.get(i).width;
								preHeight = list.get(i).height;
							}
						}
					}
				}
			}
		}
		// ˵��û���ҵ�������Ҫ�ķֱ���
		if (preWidth == 0 || preHeight == 0) {
			isShowBorder = true;
			preWidth = list.get(0).width;
			preHeight = list.get(0).height;
			for (int i = 0; i < list.size(); i++) {

				if (list.get(0).width > list.get(list.size() - 1).width) {
					// ����һ��ֵ�������һ��ֵ
					if (preWidth >= list.get(i).width
							|| preHeight >= list.get(i).height) {
						// ����һ��ѡ���Ԥ���ֱ��ʿ���߸߶ȴ��ڱ��εĿ�Ⱥ͸߶�ʱ��ִ�����´���:
						if (list.get(i).width >= 1280) {
							// �����ε�Ԥ����Ⱥ͸߶ȴ���1280*720ʱִ�����´���
							preWidth = list.get(i).width;
							preHeight = list.get(i).height;

						}
					}
				} else {
					if (preWidth <= list.get(i).width
							|| preHeight <= list.get(i).height) {
						if (preWidth >= 1280 || preHeight >= 720) {

						} else {
							// ����һ��ѡ���Ԥ���ֱ��ʿ���߸߶ȴ��ڱ��εĿ�Ⱥ͸߶�ʱ��ִ�����´���:
							if (list.get(i).width >= 1280) {
								// �����ε�Ԥ����Ⱥ͸߶ȴ���1280*720ʱִ�����´���
								preWidth = list.get(i).width;
								preHeight = list.get(i).height;

							}
						}

					}
				}
			}
		}

		// ���û���ҵ�����1280*720�ķֱ��ʵĻ���ȡ�����е����ֵ����ƥ��
		if (preWidth <= 640 || preHeight <= 480) {
			isShowBorder = true;
			if (list.get(0).width > list.get(list.size() - 1).width) {
				preWidth = list.get(0).width;
				preHeight = list.get(0).height;
			} else {
				preWidth = list.get(list.size() - 1).width;
				preHeight = list.get(list.size() - 1).height;
			}
		}
		if (isShowBorder) {
			if (ratioScreen > (float) preWidth / preHeight) {
				surfaceWidth = (int) (((float) preWidth / preHeight) * srcHeight);
				surfaceHeight = srcHeight;
			} else {
				surfaceWidth = srcWidth;
				surfaceHeight = (int) (((float) preHeight / preWidth) * srcWidth);
			}
		} else {
			surfaceWidth = srcWidth;
			surfaceHeight = srcHeight;
		}

		System.out.println("surfaceWidth1:" + surfaceWidth
				+ "--surfaceHeight1:" + surfaceHeight);
	}

	@SuppressLint("NewApi")
	public void setScreenSize(Context context) {
		int x, y;
		WindowManager wm = ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE));
		Display display = wm.getDefaultDisplay();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			Point screenSize = new Point();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
				display.getRealSize(screenSize);
				x = screenSize.x;
				y = screenSize.y;
			} else {
				display.getSize(screenSize);
				x = screenSize.x;
				y = screenSize.y;
			}
		} else {
			x = display.getWidth();
			y = display.getHeight();
		}

		srcWidth = x;
		srcHeight = y;
	}

	/**
	 * @param mDecorView
	 *            {tags} �趨�ļ�
	 * @return ${return_type} ��������
	 * @throws
	 * @Title: ����ģʽ
	 * @Description: �������ⰴ��
	 */
	@TargetApi(19)
	public void hiddenVirtualButtons(View mDecorView) {
		if (Build.VERSION.SDK_INT >= 19) {
			mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
					| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
					| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
					| View.SYSTEM_UI_FLAG_FULLSCREEN
					| View.SYSTEM_UI_FLAG_IMMERSIVE);
		}
	}

	public static int setRotation(int width, int height, int uiRot, int rotation) {
		if (width >= height) {
			if (uiRot == 1 || uiRot == 3) {
				switch (uiRot) {
				case 1:
					rotation = 0;

					break;
				case 3:
					rotation = 180;

					break;
				}

			} else {
				switch (uiRot) {
				case 0:
					rotation = 0;

					break;

				case 2:
					rotation = 180;

					break;

				}
			}
		} else if (height >= width) {
			if (uiRot == 0 || uiRot == 2) {

				switch (uiRot) {
				case 0:
					rotation = 90;

					break;

				case 2:
					rotation = 270;

					break;

				}
			} else {
				switch (uiRot) {
				case 1:
					rotation = 270;

					break;
				case 3:
					rotation = 90;

					break;
				}
			}
		}
		return rotation;
	}
}
