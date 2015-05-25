# LanDroid
This is : 
* RJ45를 이용한 안드로이드 스마트폰용 이더넷 모듈 LAN Droid
* 무선 네트워크의 제약을 해결하기 위해 라즈베리파이를 이용하여 안드로이드 스마트폰에서 유선 네트워트를 사용할 수 있도록 만들어주는 프로젝트

# Feature
* 라즈베리파이와 안드로이드를 연결하면 IP/Port forwarding을 통해 유선 네트워크를 안드로이드로 redirect 해줍니다. 안드로이드에서는 Xposed Framework를 통해 네트워크 메소드를 후킹하고 와이파이를 사용 중인 것처럼 결과를 리턴하여 유선 네트워크를 사용할 수 있게 만들어줍니다. 또한, Serveroid 어플리케이션을 통해 안드로이드를 Web/File Server로 사용할 수 있습니다.

# My Part
* 이 프로젝트는 4명의 팀으로 진행하였고 저는 안드로이드에서 IP/Port forwarding과 Xposed Framework를 통해 네트워크 메소드를 후킹 하는 부분을 담당하였습니다.

# Composition
Landroid(android)
* 

Server
* 1. get Bitmap using getDrawingCache(). 
* 2. Bitmap to Byte[]
* 3. Byte[] to Encoder
* 4. get Byte[] (encoded data) from Encoder
* 5. Transfer Byte[] to Client

Client
* 1. Receive data from Server
* 2. Received data to Byte[]
* 3. Byte[] to Decoder
* 4. Decoder to Surface
* 5. Rendering with the Surface

However :
* It's not implemented layout.
* It's not encoded with the surface. So, need to RGB2YUV and NV21->NV12. Results in a low performance. 

# Feature
* Application Mirroring
* H.264 Encoding, Decoding
* It's not used MediaExtractor.

# Requirements
* Requires minimum API 16 (Android 4.1)


# Android API
* MediaCodec : http://developer.android.com/reference/android/media/MediaCodec.html

# Reference
* Encoder : http://bigflake.com/mediacodec/
 
# Image
![alt tag](https://github.com/zerstyu/MediaCodec_Mirroring/blob/master/mediacodec.PNG)
