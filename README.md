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
* 유선 네트워크를 사용할 클라이언트
* 네트워크 활성/비활성 기능
* USB 테더링 간편설정 기능
* 포트 포워딩 기능
* 핫스팟을 통해 유선네트워크를 와이파이처럼 사용하는 기능
* IP 설정 기능
* 유선 네트워크 리다이렉트를 담당하는 라즈베리 파이의 상태 확인 기능

Landroid(linux)
* RJ45를 입력받아 유선 네트워크를 안드로이드 스마트폰으로 리다이렉트
* 스마트폰이 연결되면 자동으로 인식하여 필요한 소프트웨어를 설치
* 리다이렉트에 필요한 네트워크 설정
* 여러 대의 안드로이드 스마트폰 연결 가능
* 연결된 안드로이드 스마트폰에서 설정 변경 기능

Serveroid(android)
* 스마트폰을 웹/파일 서버로 사용가능하게 하는 어플리케이션
* IP/Port 설정 기능
* 요청에 따른 응답 생성 및 전송을 통해 웹/파일 서버로 사용 가능
* LanDroid와 Serveroid를 함께 사용 함으로서 공기계를 실제 서버처럼 사용 가능

# Demonstration Video
* https://www.youtube.com/watch?v=oiBMFBLF4RY
