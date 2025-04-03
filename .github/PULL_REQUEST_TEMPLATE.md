## 요구사항

### Domain

- [ ] User
    - [ ] id: 객체를 식별하기 위한 id로 UUID 타입으로 선언
    - [ ] userName: 사용자 이름 String 타입으로 선언
    - [ ] createdAt, updatedAt: 각각 객체의 생성, 수정 시간을 유닉스 타임스탬프로 나타내기 위한 필드로 Long 타입으로 선언
    - [ ] updateUserName: 입력받은 이름으로 UserName 수정
- [ ] Channel
    - [ ] id: 객체를 식별하기 위한 id로 UUID 타입으로 선언
    - [ ] channelName: 채널 이름 String 타입으로 선언
    - [ ] createdAt, updatedAt: 각각 객체의 생성, 수정 시간을 유닉스 타임스탬프로 나타내기 위한 필드로 Long 타입으로 선언
    - [ ] updateChannelName: 입력받은 이름으로 channelName 수정
- [ ] Message
    - [ ] id: 객체를 식별하기 위한 id로 UUID 타입으로 선언
    - [ ] message: 메시지 내용 String 타입으로 선언
    - [ ] createdAt, updatedAt: 각각 객체의 생성, 수정 시간을 유닉스 타임스탬프로 나타내기 위한 필드로 Long 타입으로 선언
    - [ ] updateMessage: 입력받은 메세지로 message 수정

### Service

- [ ] UserService
    - [ ] 사용자 입력 후 정보 저장 기능
    - [ ] 사용자의 정보 추력 기능
        - [ ] 모든 사용자 출력
        - [ ] 해당하는 사용자만 출력
            - [ ] 단건 조회
            - [ ] 다건 조회
    - [ ] 사용자 이름 입력 기능
    - [ ] 사용자 이름 수정 기능
    - [ ] 사용자 이름 입력 후 삭제 기능

- [ ] ChannelService
    - [ ] 채널 입력 후 정보 저장 기능
    - [ ] 채널 정보 출력 기능
    - [ ] 채널 이름 수정 기능
    - [ ] 채널 이름 입력 후 삭제 기능

- [ ] MessageService
    - [ ] 사용자 입력 후 사용자가 원하는 기능 입력 받아 기능 수행
        - [ ] 메시지 작성 기능
        - [ ] 사용자가 작성 한 모든 메시지 출력
        - [ ] 메시지 수정 기능
        - [ ] 메시지 삭제 기능

### Application

- [ ] JavaApplication
    -  [ ] 추후에 작성

## 멘토에게

- 코드 작성 완료 후 작성