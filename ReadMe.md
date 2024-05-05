## 개요
주로 모놀리식 구조 애플리케이션을 개발하였고, 단일 데이터베이스 환경에서만 주로 개발을 했었습니다. 요즘 업무를 하면서 MSA 환경 및 다중 데이터베이스를 이용한 환경들을 고민을 해야되는 경우가 발생하였습니다.
보통 한 개의 데이터베이스의 하나의 트랜잭션을 로컬 트랜잭션이라고 하며, 여러 개의 데이터베이스 또는 데이터베이스와 메시지 큐 등의 두 개 이상의 자원을 트랜잭션 처리 하기 위해서는 글로벌 트랜잭션을 사용해야 합니다.

## XA와 2PC
### XA(eXtended Architecture) 란
  - 동일한 전역 트랜잭션 내에서 몇 개의 리소스에 접근하기 위한 X/Open 제정한 표준
  - XA 표준 규격은 하나의 트랜잭션 매니저가 어떤 트랜잭션의 한 부분으로 어떤 작업이 수행되고 있는지를 데이터베이스에 통보하는 방식과, 각 트랜잭션이 완료 될 때 2PC(2 Phase Commit) 을 수행하는 방식 권장
  - 하나의 표준이기 때문에, 모든 호환되는 리소스 혹은 드라이버들이 분산 트랜잭션의 일부로서 트랜잭션 매니저와 연동, 2PC 고려되어야 하는 상황이라면 XA는 트랜잭션 매니저와 리소스를 연결해주는 역할

### 2PC 란
  - 2 Phase Commit의 약자로, 분산 환경에서의 트랜잭션 처리를 위한 프로토콜
  - 원자적 커밋 프로토콜(Atomic Commit Protocol) 의 일종으로, 트랜잭션을 커밋할지 롤백할지에 대한 분산 원자적 트랜잭션(Distributed Atomic Transaction) 에 관여하는 분산 알고리즘 중 하나

![2PC.png](2PC.png)
  - 위 그림처럼 서로 다른 리소스에 대한 요청을 원자적으로 처리하기 위해 1. Prepare Phase, 2. Commit Phase 두 단계로 나누어 처리하여 서로 다른 DB 간 정합성을 보장
  - 각각 서로 다른 리소스에 데이터를 추가/변경 등의 작업을 한 뒤 phase1 에서 모든 리소스에 반영할 수 있을지 물어보고 합의가 되지 않았을 경우 롤백을 하고, 합의가 되었을 경우 phase2 에서 커밋을 한다.
  - 하지만 그림을 통해서 알 수 있듯이 데이터를 추가/변경 등의 작업을 시작핼 때부터 커밋될 때까지 Lock 이 걸림, 이로인해서 양 쪽 리소스를 함께 조회하더라도 커밋되지 않은 쪽은 락이 잡혀 때문에 두 리소스가 불일치된 상태로 조회되지 않는다. 락을 이용해서 정합성 보장
  - 하지만 이러한 2PC 방식은 성능 이슈(Lock)가 있음

### JTA(Java Transaction API) 란
  - 플랫폼마다 상이한 트랜잭션 매니저들과 어플리케이션들이 상호작용할 수 있는 인터페이스를 정의, 트랜잭션 처리가 필요한 어플리케이션이 특정 벤더의 트랜잭션 매니저에 의존할 필요 없음
  - JTA 는 JTS(Java Transaction Service) 와 JTA(Java Transaction API) 로 구성, JTS 는 트랜잭션 관리자를 제공하고, JTA 는 트랜잭션 관리자와 트랜잭션을 처리하는 애플리케이션 간의 통신을 위한 API 를 제공한다.
  - Spring Boot 엣는 Atomikos, Bitronix JTA 구현체를 지원

### Atomikos 를 이용하여 트랜잭션 처리 구현
  1. Atomikos 라이브러리 추가 (Spring Boot 3 기준)
```groovy
    implementation 'com.atomikos:transactions-spring-boot-starter:6.0.0'
    implementation 'jakarta.transaction:jakarta.transaction-api:2.0.1'
```

## Saga 패턴
### Camel + LRA 이용한 MicroService 간 보상 거래 구현
### 1. Docker를 이용한 LRA Coordinator 설치
```shell script
docker run -i -p 8088:8080 quay.io/jbosstm/lra-coordinator
```

### 2. build.gradle 에 Camel 의존성 추가
Gradle 의존성 추가를 하였으며, Tomcat 을 사용하지 않고, Undertow를 사용하도록 설정을 변경하였습니다.
```groovy
implementation ('org.springframework.boot:spring-boot-starter-web') {
    //톰캣 제거
    exclude module: 'spring-boot-starter-tomcat'
}
compileOnly 'org.apache.camel.springboot:camel-spring-boot-dependencies:4.5.0'
implementation 'org.apache.camel:camel-netty-http:4.5.0'
implementation 'org.apache.camel.springboot:camel-spring-boot-starter:4.5.0'
implementation 'org.apache.camel.springboot:camel-undertow-starter:4.5.0'
implementation 'org.apache.camel.springboot:camel-lra-starter:4.5.0'
implementation 'org.apache.camel:camel-http-common:4.5.0'
implementation 'org.apache.camel.springboot:camel-http-starter:4.5.0'
implementation 'org.apache.camel.springboot:camel-servlet-starter:4.5.0'
```

### 3. Camel LRA 설정
```yml
camel:
  servlet:
    mapping:
      enabled: true
      context-path: /*
  lra:
    enabled: true
    coordinator-url: http://localhost:8088
    local-participant-url: http://host.docker.internal:8080
    local-participant-context-path: /lra-participant
```

### 4. Router 설정
RouteBuilder 상속을 받아서, configure 메서드를 오버라이딩하여 라우터를 설정

### 개발을 진행하면서 특이 사항
compensation 을 이용해서 실패 시, 보상 처리를 테스트를 진행을 해보고 었습니다. 그런데 보상 거래가 정상적으로 실행이 안되고, Docker 를 이용하여 실행 한 LRA Coordinator 에서 아래 로그가 계속 찍히는 현상이 발생 하였습니다.
```
2024-05-05 15:54:32,146 INFO  [io.nar.lra] (Periodic Recovery) LRAParticipantRecord.doEnd(compensate) HTTP PUT at http://localhost:8001/lra-participant/compensate?id=SAGA+Simple+Test&Camel-Saga-Compensate=direct://cancelPayment failed for LRA http://localhost:8088/lra-coordinator/0_ffffac110002_8f7d_66379dff_12 (reason: jakarta.ws.rs.ProcessingException: io.netty.channel.AbstractChannel$AnnotatedConnectException: Connection refused: localhost/127.0.0.1:8001)
```
해당 부분은 Docker 네트워크에 대한 이해가 필요합니다. Docker 컨테이너는 기본적으로 격리된 환경을 제공하며 이 때문에 컨테이너는 자체적인 로컬 호스트를 가지고, 호스트 메신의 로컬 호스트와 다릅니다. 따라서 컨테이너 내부에서 localhost 또는 127.0.0.1를 사용하여 호스트 머신에 접근을 하려고 하면 안됩니다.
```
변경 전 : camel.lra.local-participant-url=http://localhost:8001
변경 후 : camel.lra.local-participant-url=http://host.docker.internal:8001
```
그래서 현재는 간단하게 Saga 패턴을 구현을 해서 테스트를 하기 위한 목적이기 때문에 위와 같이 설정을 변경하여 테스트를 진행을 하였습니다.
Docker 18.03 버전 이후부터는 host.docker.internal 이라는 호스트명을 사용하여 호스트 머신에 접근을 할 수 있습니다. Docker가 자동으로 호스트 머신의 IP주소로 해석을 해줍니다.
