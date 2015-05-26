package com.sogogi.landroid.networkmodule;

import java.lang.reflect.Constructor;

import android.net.NetworkInfo;
import android.util.Log;
import de.robv.android.xposed.XposedHelpers;

public class Controller {
	public static final String MY_PACKAGE = Controller.class.getPackage().getName();

	public static Constructor<?> getCompatibleConstructor(Class<?> clazz, Class<?>[] parameterTypes) {
		Log.e("msg", "getCompatibleConstructor 들어옴");
		Constructor<?>[] constructors = clazz.getConstructors();

		for (int i = 0; i < constructors.length; i++) {
			if (constructors[i].getParameterTypes().length == (parameterTypes != null ? parameterTypes.length : 0)) {
				// If we have the same number of parameters there is a shot that we have a compatible
				Class<?>[] constructorTypes = constructors[i].getParameterTypes();
				boolean isCompatible = true;

				for (int j = 0; j < (parameterTypes != null ? parameterTypes.length : 0); j++) {
					if (!constructorTypes[j].isAssignableFrom(parameterTypes[j])) {
						// The type is not assignment compatible, however
						// we might be able to coerce from a basic type to a boxed type
						if (constructorTypes[j].isPrimitive()) {
							if (!isAssignablePrimitiveToBoxed(constructorTypes[j], parameterTypes[j])) {
								isCompatible = false;
								break;
							}
						}
					}
				}

				if (isCompatible) {
					return constructors[i];
				}
			}
		}
		return null;
	}

	/**
	 * Checks if a primitive type is assignable with a boxed type.
	 *
	 * @param primitive
	 *            a primitive class type
	 * @param boxed
	 *            a boxed class type
	 *
	 * @return true if primitive and boxed are assignment compatible
	 */
	private static boolean isAssignablePrimitiveToBoxed(Class<?> primitive, Class<?> boxed) {
		Log.e("msg", "isAssignablePrimitiveToBoxed 들어옴");

		if (primitive.equals(java.lang.Boolean.TYPE)) {
			if (boxed.equals(java.lang.Boolean.class))
				return true;
			else
				return false;
		} else {
			if (primitive.equals(java.lang.Byte.TYPE)) {
				if (boxed.equals(java.lang.Byte.class))
					return true;
				else
					return false;
			} else {
				if (primitive.equals(java.lang.Character.TYPE)) {
					if (boxed.equals(java.lang.Character.class))
						return true;
					else
						return false;
				} else {
					if (primitive.equals(java.lang.Double.TYPE)) {
						if (boxed.equals(java.lang.Double.class))
							return true;
						else
							return false;
					} else {
						if (primitive.equals(java.lang.Float.TYPE)) {
							if (boxed.equals(java.lang.Float.class))
								return true;
							else
								return false;
						} else {
							if (primitive.equals(java.lang.Integer.TYPE)) {
								if (boxed.equals(java.lang.Integer.class))
									return true;
								else
									return false;
							} else {
								if (primitive.equals(java.lang.Long.TYPE)) {
									if (boxed.equals(java.lang.Long.class))
										return true;
									else
										return false;
								} else {
									if (primitive.equals(java.lang.Short.TYPE)) {
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

	public static NetworkInfo newNetWorkInfo(int paramInt) {
		Log.e("msg", "newNetWorkInfo 들어옴");
		Constructor<?> localConstructor = getCompatibleConstructor(NetworkInfo.class, new Class[] { Integer.class });
		NetworkInfo localNetworkInfo = null;

		if (localConstructor != null) {
			localConstructor.setAccessible(true);
			try {
				Object[] arrayOfObject = new Object[1];
				arrayOfObject[0] = Integer.valueOf(paramInt);
				localNetworkInfo = (NetworkInfo) localConstructor.newInstance(arrayOfObject);

				XposedHelpers.setIntField(localNetworkInfo, "mNetworkType", paramInt);
				XposedHelpers.setBooleanField(localNetworkInfo, "mIsAvailable", true);
				XposedHelpers.setObjectField(localNetworkInfo, "mState", NetworkInfo.State.CONNECTED);
				XposedHelpers.setObjectField(localNetworkInfo, "mDetailedState", NetworkInfo.DetailedState.CONNECTED);
				XposedHelpers.setObjectField(localNetworkInfo, "mSubtypeName", "");

				if (paramInt == 1) {
					XposedHelpers.setObjectField(localNetworkInfo, "mTypeName", "WIFI");
				} else {
					XposedHelpers.setObjectField(localNetworkInfo, "mTypeName", "MOBILE");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return localNetworkInfo;
	}
}