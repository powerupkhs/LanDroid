package com.sogogi.landroid;

import android.net.NetworkInfo;

import android.util.Log;
import de.robv.android.xposed.XposedHelpers;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


public class Controller
{
	public static final String MY_PACKAGE = Controller.class.getPackage().getName();

	public static Constructor<?> getCompatibleConstructor(Class<?> clazz, Class<?>[] parameterTypes)
	{
		Constructor<?>[] constructors = clazz.getConstructors();

		Log.e("msg", "컨스트럭터 들어옴");

		for (int i = 0; i < constructors.length; i++)
		{
			Log.e("msg", "컨스트럭터 포문 들어옴");

			if (constructors[i].getParameterTypes().length == (parameterTypes != null ? parameterTypes.length : 0))
			{
				Log.e("msg", "컨스트럭터 랭쓰비교 들어옴");

				// If we have the same number of parameters there is a shot that we have a compatible
				// constructor
				Class<?>[] constructorTypes = constructors[i].getParameterTypes();
				boolean isCompatible = true;
				for (int j = 0; j < (parameterTypes != null ? parameterTypes.length : 0); j++)
				{
					Log.e("msg", "컨스트럭터 두번째 포문 들어옴");
					if (!constructorTypes[j].isAssignableFrom(parameterTypes[j]))
					{
						Log.e("msg", "컨스트럭터 두번째 포문 이즈어사인에이블프롬 들어옴");
						// The type is not assignment compatible, however
						// we might be able to coerce from a basic type to a boxed type
						if (constructorTypes[j].isPrimitive())
						{
							Log.e("msg", "컨스트럭터 두번째포문 이즈프리미티브  들어옴");
							if (!isAssignablePrimitiveToBoxed(constructorTypes[j], parameterTypes[j]))
							{
								Log.e("msg", "컨스트럭터 두번째 포문 이즈어사인 박스 들어옴");
								isCompatible = false;
								break;
							}
						}
					}
				}
				if (isCompatible)
				{
					return constructors[i];
				}
			}
		}
		return null;
	}

	/**
	 * <p> Checks if a primitive type is assignable with a boxed type.</p>
	 *
	 * @param primitive a primitive class type
	 * @param boxed     a boxed class type
	 *
	 * @return true if primitive and boxed are assignment compatible
	 */
	private static boolean isAssignablePrimitiveToBoxed(Class<?> primitive, Class<?> boxed)
	{
		Log.e("msg", "이즈어사인박스 들어옴");

		if (primitive.equals(java.lang.Boolean.TYPE))
		{
			if (boxed.equals(java.lang.Boolean.class))
				return true;
			else
				return false;
		}
		else
		{
			if (primitive.equals(java.lang.Byte.TYPE))
			{
				if (boxed.equals(java.lang.Byte.class))
					return true;
				else
					return false;
			}
			else
			{
				if (primitive.equals(java.lang.Character.TYPE))
				{
					if (boxed.equals(java.lang.Character.class))
						return true;
					else
						return false;
				}
				else
				{
					if (primitive.equals(java.lang.Double.TYPE))
					{
						if (boxed.equals(java.lang.Double.class))
							return true;
						else
							return false;
					}
					else
					{
						if (primitive.equals(java.lang.Float.TYPE))
						{
							if (boxed.equals(java.lang.Float.class))
								return true;
							else
								return false;
						}
						else
						{
							if (primitive.equals(java.lang.Integer.TYPE))
							{
								if (boxed.equals(java.lang.Integer.class))
									return true;
								else
									return false;
							}
							else
							{
								if (primitive.equals(java.lang.Long.TYPE))
								{
									if (boxed.equals(java.lang.Long.class))
										return true;
									else
										return false;
								}
								else
								{
									if (primitive.equals(java.lang.Short.TYPE))
									{
										if (boxed.equals(java.lang.Short.class))
											return true;
										else
											return false;
									}
								}
							}
						}
					}
				}
			}
		}
		return false;
	}

//	public static int getHackMode()
//	{
//		Log.e("msg", "컨트롤러 겟핵모드 부름");
//		
//		File mFile;
//		mFile = new File(Environment.getDataDirectory() + "/data/uk.co.villainrom.pulser.enablecallrecording/shared_prefs", "ConnectivityManager.xml");
//		Log.e("msg", "컨트롤러 겟핵모드 부름 : " + Environment.getDataDirectory());
//		
//		return new XSharedPreferences("uk.co.villainrom.pulser.enablecallrecording", "ConnectivityManager").getInt("fakeWifiActivated", 0);
//		
//	}

//	public static int getTraceLevel()
//	{
//		return new XSharedPreferences("uk.co.villainrom.pulser.enablecallrecording", "ConnectivityManager").getInt("TraceLevel", 0);
//	}

	public static NetworkInfo newNetWorkInfo(int paramInt)
	{
		Log.e("msg", "뉴 네트워크 인포 들어옴");
		Constructor localConstructor = getCompatibleConstructor(NetworkInfo.class, new Class[] { Integer.class });
		NetworkInfo localNetworkInfo = null;
		
		if (localConstructor != null)
		{
			localConstructor.setAccessible(true);
			try
			{
				Log.e("msg", "설정 들어옴");
				Object[] arrayOfObject = new Object[1];
				arrayOfObject[0] = Integer.valueOf(paramInt);
				Log.e("msg", "설정 파람 가져오기 끝 Integer.valueof : " + Integer.valueOf(paramInt));
				localNetworkInfo = (NetworkInfo)localConstructor.newInstance(arrayOfObject);
				//						ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				//						localNetworkInfo = cm.getActiveNetworkInfo();

				Log.e("msg", "설정 네트워크인포 가져오기 끝");
				XposedHelpers.setIntField(localNetworkInfo, "mNetworkType", paramInt);
				Log.e("msg", "설정 인트필드 끝");
				XposedHelpers.setBooleanField(localNetworkInfo, "mIsAvailable", true);
				Log.e("msg", "설정 불리언필드 끝");
				XposedHelpers.setObjectField(localNetworkInfo, "mState", NetworkInfo.State.CONNECTED);
				Log.e("msg", "설정 mstate 끝");
				XposedHelpers.setObjectField(localNetworkInfo, "mDetailedState", NetworkInfo.DetailedState.CONNECTED);
				Log.e("msg", "설정 디테일스테이트 끝");
				XposedHelpers.setObjectField(localNetworkInfo, "mSubtypeName", "");
				Log.e("msg", "설정 섭타입네임 끝");
				
				if (paramInt == 1)
				{
					Log.e("msg", "와이파이 1 인거 들어옴");
					XposedHelpers.setObjectField(localNetworkInfo, "mTypeName", "WIFI");
				}
				else
					XposedHelpers.setObjectField(localNetworkInfo, "mTypeName", "MOBILE");
			}
			catch (IllegalArgumentException localIllegalArgumentException)
			{
				Log.e("msg", "IllegalArgumentException");
			}
			catch (InstantiationException localInstantiationException)
			{
				Log.e("msg", "InstantiationException");
			}
			catch (IllegalAccessException localIllegalAccessException)
			{
				Log.e("msg", "IllegalAccessException");
			}
			catch (InvocationTargetException localInvocationTargetException)
			{
				Log.e("msg", "InvocationTargetException");
			}
		}
		
		return localNetworkInfo;
	}
	
	

//	public static void setHackMode(int paramInt)
//	{
//		try
//		{
//			SharedPreferences.Editor localEditor = new XSharedPreferences("uk.co.villainrom.pulser.enablecallrecording", "ConnectivityManager").edit();
//			localEditor.putInt("fakeWifiActivated", paramInt);
//			localEditor.commit();
//			return;
//		}
//		catch (Exception localException)
//		{
//			while (true)
//				Log.d("msg", "unable to save preference");
//		}
//	}
//
//	public static void setTraceLevel(int paramInt)
//	{
//		try
//		{
//			SharedPreferences.Editor localEditor = new XSharedPreferences("uk.co.villainrom.pulser.enablecallrecording", "ConnectivityManager").edit();
//			localEditor.putInt("TraceLevel", paramInt);
//			localEditor.commit();
//			return;
//		}
//		catch (Exception localException)
//		{
//			while (true)
//				Log.d("msg", "unable to save preference");
//		}
//	}
}