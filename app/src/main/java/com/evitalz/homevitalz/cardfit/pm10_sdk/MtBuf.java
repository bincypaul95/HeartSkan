package com.evitalz.homevitalz.cardfit.pm10_sdk;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Vector;
import com.contec.jar.pm10.DeviceCommand;
import com.contec.jar.pm10.DevicePackManager;
import com.contec.jar.pm10.PrintBytes;



import static com.evitalz.homevitalz.cardfit.pm10_sdk.BluetoothChatService.ACTION_DATA_NOT_AVAILABLE;
import static com.evitalz.homevitalz.cardfit.pm10_sdk.BluetoothChatService.ACTION_FETCHING_DATA;


public class  MtBuf {
	private static final String TAG = "lz";
	public static Vector<Integer> m_buf = null;
	private boolean mReceiveDataFailed = true;

	private boolean  mFlage = false;

	public static final byte e_back_settime = (byte) 0xF2;
	public static final byte e_back_deletedata = (byte) 0xC0;
	public static final byte e_back_caseinfo = (byte) 0xE0;
	public static final byte e_back_single_caseinfo = (byte) 0xE1;
	public static final byte e_back_single_data = (byte) 0xD0;
	public static final byte e_back_stop_transfer = (byte) 0xF6;
	public static final byte e_back_dateresponse = (byte) 0xA0;// 获取病例数据命令及应答包

	Context context;
	private String mAllData="";
	boolean flag=false;
	public MtBuf(Context context) {
		m_buf = new Vector<Integer>();
		this.context=context;
		flag=false;
	}

	public synchronized int Count() {
		return m_buf.size();
	}

	DevicePackManager mPackManager = new DevicePackManager();
	int mSettimeCount = 0;
	int mCount = 0;
	int _receCount = 1;
	int _dataCount = 0;
	int _caseLen = 0;
	int _recedataCount = 0;
	int mCaseCount = 1;
	int case_val=0;
	public synchronized void write(byte[] buf, int count,
								   OutputStream pOutputStream) throws Exception {
//		PrintBytes.printData(buf, count);
		try
		{
			byte[] pack = mPackManager.arrangeMessage(buf, count);
			if (pack != null) {
				Log.d("bluetooth_test", "command: "+pack[0]);
				switch (pack[0]) {
					case e_back_settime:// 校时成功
						pOutputStream.write(DeviceCommand.GET_DATA_INFO(1, 0));

						break;
					case e_back_caseinfo:// 返回病例信息
						mCount = mPackManager.mCount;
						if (mCount > 0) {
							Log.e("病例信息个数", "---------------------->>::" + mCount);
							Log.e("病例索引", "---------------------->>::" + _receCount);
							pOutputStream.write(DeviceCommand.GET_DATA_INFO(2, _receCount));
						} else {
							Intent i=new Intent(ACTION_DATA_NOT_AVAILABLE);
							context.sendBroadcast(i);
							pOutputStream.write(DeviceCommand.DELETE_DATA(0, 0));

							Log.e("无新病例", "---------------------->>::" + mCount);
							Log.e("病例索引", "---------------------->>::" + _receCount);
						}
						break;
					case e_back_single_caseinfo:// 返回单个病例信息
						_receCount++;
//				mData = new DeviceData();
						PrintBytes.printData(pack);
						// if (pack[21] == 0) {
						int _year = ((pack[3] << 7) | (pack[4] & 0xff)) & 0xffff;

						Log.e("8888888888888888851ssss", "---------------->>>>>" + _year);

						_caseLen = (pack[10] << 21) | (pack[11] << 14) | (pack[12] << 7)
								| pack[13];
						_dataCount = _caseLen / 25;
						Log.d(TAG, "write: caselength ->"+_caseLen);
						Log.d(TAG, "write: casevalue ->"+case_val);
						_recedataCount = 0;
						case_val=pack[16];
						pOutputStream.write(DeviceCommand.GET_DATA(1));
						break;
					case (byte)0xff:
						Log.d("bluetooth_test", "command => oxff");
						try
						{
//					if ((_receCount - 1) == mCount) {
							pOutputStream.write(DeviceCommand.GET_DATA_RE(0));
							try {
								Thread.sleep(300);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							pOutputStream.write(DeviceCommand.DELETE_DATA(0, 0));
//					}
//					else
//					{
//						pOutputStream.write(DeviceCommand.GET_DATA_INFO(2, _receCount));
//						Log.e("88888888888888888888",
//								"---------------->>>>>_receCount");
//					}
						}
						catch (Exception e)
						{
							System.out.println("@@@"+e.toString());
						}
						if(!flag)
						{
							flag=true;
							Intent i=new Intent(ACTION_FETCHING_DATA);
							context.sendBroadcast(i);
//				Log.e("===========一条波形数据=============", "--------接收完毕--------");

							String case_value= Arrays.toString(mPackManager.mDeviceData.CaseData);
							case_value=case_value.replace(" ","");
							case_value=case_value.replace("[","");
							case_value=case_value.replace("]","");
//				System.out.println("@@@" + Arrays.toString(_back));
							System.out.println("@@@" + mPackManager.mDeviceData.mYear);
							System.out.println("@@@" + mPackManager.mDeviceData.mMonth);
							System.out.println("@@@" + mPackManager.mDeviceData.mDay);
							System.out.println("@@@" + mPackManager.mDeviceData.mHour);
							System.out.println("@@@" + mPackManager.mDeviceData.mMin);
							System.out.println("@@@" + mPackManager.mDeviceData.mSec);
							System.out.println("@@@" + mPackManager.mDeviceData.Plus);
							System.out.println("@@@" + Arrays.toString(mPackManager.mDeviceData.mResult));
							String cases = "";
							String s = mPackManager.mDeviceData.mYear + "-" + checkDigit(mPackManager.mDeviceData.mMonth) + "-" + checkDigit(mPackManager.mDeviceData.mDay)
									+ " " + checkDigit(mPackManager.mDeviceData.mHour) + ":" +checkDigit( mPackManager.mDeviceData.mMin) + ":" + checkDigit(mPackManager.mDeviceData.mSec);
							int bpm = mPackManager.mDeviceData.Plus;
							switch (case_val) {
								case 0:
									cases = "No abnormalities";
									break;
								case 1:
									cases = "Missed Beat";
									break;
								case 2:
									cases = "Accidental VPB";
									break;
								case 3:
									cases = "VPB Trigeminy";
									break;
								case 4:
									cases = "VPB Bigeminy";
									break;
								case 5:
									cases = "VPB Couple";
									break;
								case 6:
									cases = "VPB runs of 3";
									break;
								case 7:
									cases = "VPB runs of 4";
									break;
								case 8:
									cases = "VPB RonT";
									break;
								case 9:
									cases = "Bradycardia";
									break;
								case 10:
									cases = "Tachycardia";
									break;
								case 11:
									cases = "Arrhythmia";
									break;
								case 12:
									cases = "ST elevation";
									break;
								case 13:
									cases = "ST depression";
									break;
							}

							final Intent i1 = new Intent("Receiving_Data");
							SimpleDateFormat f2=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							i1.putExtra("DATE",s);
							i1.putExtra("TYPE","ECG");
							i1.putExtra("VAL1",cases);
							i1.putExtra("VAL2",Integer.toString(bpm));
							i1.putExtra("VAL3","");
							i1.putExtra("VAL4","");
							i1.putExtra("ECG_VALUE",case_value);
							i1.putExtra("MANUF","CONTEC");
							context.sendBroadcast(i1);
						}



//			    saveAsString(_CaseData.toString());
//
//			    if ((_receCount - 1) == mCount) {


//				} else {
//					pOutputStream.write(DeviceCommand.GET_DATA_INFO(2, _receCount));
//					Log.e("88888888888888888888",
//							"---------------->>>>>_receCount");
//				}


						break;
					case e_back_single_data:

						_recedataCount++;
						_dataCount--;
//						if (_dataCount <= 50) {
////							if ((_receCount - 1) == mCount) {
//
//								pOutputStream.write(DeviceCommand.GET_DATA_RE(0));
//								try {
//									Thread.sleep(300);
//								} catch (InterruptedException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}
//								pOutputStream.write(DeviceCommand.DELETE_DATA(0, 0));
////							} else {
////								pOutputStream.write(DeviceCommand.GET_DATA_INFO(2, 1));
////								Log.e("88888888888888888888",
////										"---------------->>>>>_receCount");
////							}
//						} else {
//							// SendCommand.send(DeviceCommand.CONFIRM);
//						}
//						String case_value= Arrays.toString(mPackManager.mDeviceData.CaseData);
//						case_value=case_value.replace(" ","");
//						case_value=case_value.replace("[","");
//						case_value=case_value.replace("]","");
////				System.out.println("@@@" + Arrays.toString(_back));
//						System.out.println("@@@" + mPackManager.mDeviceData.mYear);
//						System.out.println("@@@" + mPackManager.mDeviceData.mMonth);
//						System.out.println("@@@" + mPackManager.mDeviceData.mDay);
//						System.out.println("@@@" + mPackManager.mDeviceData.mHour);
//						System.out.println("@@@" + mPackManager.mDeviceData.mMin);
//						System.out.println("@@@" + mPackManager.mDeviceData.mSec);
//						System.out.println("@@@" + mPackManager.mDeviceData.Plus);
//						System.out.println("@@@" + Arrays.toString(mPackManager.mDeviceData.mResult));
//						String cases = "";
//						String s = mPackManager.mDeviceData.mYear + "-" + checkDigit(mPackManager.mDeviceData.mMonth) + "-" + checkDigit(mPackManager.mDeviceData.mDay)
//								+ " " + checkDigit(mPackManager.mDeviceData.mHour) + ":" +checkDigit( mPackManager.mDeviceData.mMin) + ":" + checkDigit(mPackManager.mDeviceData.mSec);
//						int bpm = mPackManager.mDeviceData.Plus;
//						switch (case_val) {
//							case 0:
//								cases = "No abnormalities";
//								break;
//							case 1:
//								cases = "Missed Beat";
//								break;
//							case 2:
//								cases = "Accidental VPB";
//								break;
//							case 3:
//								cases = "VPB Trigeminy";
//								break;
//							case 4:
//								cases = "VPB Bigeminy";
//								break;
//							case 5:
//								cases = "VPB Couple";
//								break;
//							case 6:
//								cases = "VPB runs of 3";
//								break;
//							case 7:
//								cases = "VPB runs of 4";
//								break;
//							case 8:
//								cases = "VPB RonT";
//								break;
//							case 9:
//								cases = "Bradycardia";
//								break;
//							case 10:
//								cases = "Tachycardia";
//								break;
//							case 11:
//								cases = "Arrhythmia";
//								break;
//							case 12:
//								cases = "ST elevation";
//								break;
//							case 13:
//								cases = "ST depression";
//								break;
//						}
//
//						final Intent i1 = new Intent("Receiving_Data");
//						SimpleDateFormat f2=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//						i1.putExtra("DATE",s);
//						i1.putExtra("TYPE","ECG");
//						i1.putExtra("VAL1",cases);
//						i1.putExtra("VAL2",Integer.toString(bpm));
//						i1.putExtra("VAL3","");
//						i1.putExtra("VAL4","");
//						i1.putExtra("ECG_VALUE",case_value);
//						i1.putExtra("MANUF","CONTEC");
//						context.sendBroadcast(i1);
////						pOutputStream.write(DeviceCommand.GET_DATA_RE(0));
////						try {
////							Thread.sleep(500);
////						} catch (InterruptedException e) {
////							// TODO Auto-generated catch block
////							e.printStackTrace();
////						}
////						Log.d("bluetooth_test", "write: deleting");
//						pOutputStream.write(DeviceCommand.DELETE_DATA(0, 1));
////						pOutputStream.write(DeviceCommand.GET_DATA_INFO(2, 1));
						break;
					case e_back_deletedata:// 删除成功


						Log.e("==============",
								"---------------删除成功共-----------------");
						//
						break;
					default:
						break;
				}
			}
		}
		catch(Exception e)
		{
			System.out.println("@@@"+e.toString());
		}

	}
	public String checkDigit(int number)
	{
		return number<=9?"0"+number: String.valueOf(number);
	}

	String PATH_BASE = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/contec";
	/**
	 * 接收到的数据存数到文件中
	 * @param pContent
	 */
	public void saveAsString(String pContent){

		Log.e("88888888888888888888","---------------->>>>>"+PATH_BASE);
		File _file=new File(PATH_BASE,"PM10_CASE_DAtA.txt");
		if(!_file.exists()){
			try {
				_file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		byte[] _data = pContent.getBytes();
		System.out.println(_data[0]);
		try {
			OutputStreamWriter os=new OutputStreamWriter(new FileOutputStream(_file));
			os.write(pContent);
			os.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public synchronized int read(int[] buf) {
		int len = 0;
		if (buf.length <= m_buf.size()) {
			for (int i = 0; i < buf.length; i++) {
				buf[i] = (int) (m_buf.get(i));
			}
			len = buf.length;
			for (int j = 0; j < len; j++) {
				m_buf.remove(0);
			}

		} else if (buf.length > m_buf.size()) {
			for (int i = 0; i < m_buf.size(); i++) {
				buf[i] = m_buf.get(i);
			}
			len = m_buf.size();
			for (int j = 0; j < len; j++) {
				m_buf.remove(0);
			}

		}
		return len;
	}

}
