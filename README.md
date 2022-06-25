삼색냥 스토어 - 개인 프로젝트
=============

커머스 웹 애플리케이션
[삼색냥 스토어](http://ec2-52-78-33-172.ap-northeast-2.compute.amazonaws.com/) 보러가기  
### 테스트 어드민 계정  
> ID: admin   
> PW: qwerqwer


## 🗓 프로젝트 일정
> 2022-03-09 ~ 현재 진행 중  
> 2022년 8월부터 1개월동안 테스트 배포 후 출시 예정

<br/>
<br/>

## 🌿 기획 의도
판매업을 하는 친구와 함께 추후 확장할 수 있는 `상품 판매용 홈페이지`를 제작해보고자 
이번 프로젝트를 시작하게 되었다.  

또한 진행 도중 팀원의 이탈로 프로젝트가 중단되는 것을 우려하여 `개인 프로젝트`로
진행하였고,   
내가 잡아놓은 일정을 맞추기 위해 노력은 많이 들어가지만 비교적 중요하지 
않은 기능들은 프레임만 잡아놓고 간소화해서 작업하였다.  

## 📝 시스템 설계도
<img width="833" alt="시스템 아키텍처" src="https://user-images.githubusercontent.com/78669797/173209283-6170f06b-2bbd-4fa8-b948-e3331833bed1.png">


## 🛠 기능 구조
> 구조도는 모든 기능이 포함한 것은 아니며 기능들의 대략적인 흐름을 나타냅니다.

<img width="850" alt="스크린샷 2022-06-11 오후 4 56 43" src="https://user-images.githubusercontent.com/78669797/173179080-afca8aab-f3c9-480f-9188-04b3e2af0481.png">

### 로그인
- spring security를 사용하여 filter에서 아이디/비밀번호/SESSIONID를 검증하는 방식을 사용한다.
- 세션에 `회원` 객체를 저장하고 `세션ID`와 선택적으로 `remember-me` 값을 쿠키에 담아 반환한다.
- 비밀번호 암호화는 `BCrypt` 해시 함수를 사용한다. 
- 추후 서버 메모리를 아끼기 위해 회원 객체의 필요한 부분(*회원번호, 이름 등*)만을 뽑아서 세션에 저장할 예정이다.

### 채팅
- 처음에는 단순 WebSocket을 구현하여 사용하였다.
- 이후 구독/발행 형식의 stomp로 변경하였다.


## 🧑‍💻 기술 스택
<img width="850" alt="기술 스택" src="https://user-images.githubusercontent.com/78669797/173209293-98abb4a0-48bb-40db-bcd6-8455e1431965.png">





---
## DB 다이어그램
<img width="1157" alt="스크린샷 2022-06-12 오전 9 34 36" src="https://user-images.githubusercontent.com/78669797/173209464-f7c2fd09-b3ed-4219-9043-3b3d9d3f9ef0.png">




---
<br/>

## ⭐⭐️️️️️️️️️️️️ Cart 시스템 (주문)
`주문` 시스템은 커머스에서 가장 중요한 기능이라고 생각한다.  
`회원`, `상품`, `결제`, `배송` 시스템과 정확히 맞물려 돌아가야 하고 이들 중 하나라도 오류가 발생한다면  
주문에 대한 **정상적인 처리가 불가능**하다.  

이러한 복잡한 주문 시스템을 단순화하기 위해 `Cart`라는 엔티티를 도입했다.


<img width="600" alt="스크린샷 2022-06-12 오전 11 36 17" src="https://user-images.githubusercontent.com/78669797/173211924-25a3bfac-01fe-406b-ace5-609aad792354.png"><br/>

`Cart` 엔티티는 상품의 주문 시스템을 단순화하여 독립성을 지키기 위해 만들어진
모듈이라고 할 수 있고,  
**장바구니/주문/정산 등 시스템 전반에 사용된다**.

### 방식은 다음과 같다.  
>`Cart` 객체가 "`Account` 객체의 소유인가?" 혹은 "`Order` 객체의 소유인가?"에 따라서  
> 해당 Cart가 단순히 "장바구니에 올라간 상품"인지
> "결제가 완료되었으니 배송을 준비해야 할 상품"인지를 판단하게 된다.  
> 여기서 "**소유한다**" 라는 개념은 "**연관관계(FK)가 맺어져 있다**" 의 개념과 같다.


### Cart 엔티티를 활용한 주문 순서
>1. 고객이 상품을 장바구니에 담으면 <U>Cart 엔티티가 생성</U>되고 이것은 <U>"고객" 엔티티의 소유</U>가 된다.
>2. 고객이 소유한 Cart들 중 원하는 Cart들을 선택하여 주문한다.
>3. <U>"주문" 엔티티가 생성</U>된다.
>4. 선택된 Cart 객체들은 <U>"주문" 엔티티의 소유</U>가 되고 <U>"회원" 엔티티의 소유는 박탈</U>된다.

### Cart 시스템의 장점
>- 주문할 때 어떠한 상품의 이름/수량/옵션 등의 명시 없이 Cart 객체의 id(PK)값으로 주문할 수 있다.
>- 상품에 대한 다른 변수나 추가적인 작업이 필요 없어서 `안전성`과 `유지보수성`이 좋아진다.
>- HTTP Request 에서 상품의 이름/수량/옵션 등이 노출되지 않는다.
>- 주문 시스템에 문제가 발생하면 Cart 를 개별적으로 추적 조회할 수 있다.


<br/>


## 트러블 슈팅 및 문제 해결

### 문제1
> ### 내용
> 상품 List를 조회한 결과 `간헐적`으로 로직과 상관없는, 모든 상품에 대한 OptionTitle를 조회하는 쿼리가 나간다.
> - `디버깅`을 시도해본 결과 **Controller 로직이 끝난 후** 문제가 발생하는 것을 알 수 있었다.
  >   - Controller, Service, Repository 에는 문제가 발생하는 코드가 존재하지 않았다.
> - `템플릿 엔진`에서 로딩되지 않은 값을 불러오는 `지연로딩`을 시도한 것으로 추측하고  
>   문제를 해결해보려 했으나 템플릿 엔진에서는 지연로딩을 시도하는 코드를 찾을 수 없었다.
> ### 해결방법
> 지연로딩 자체를 불가능하게 하기 위해서 `dto`와 `Projection`을 사용하였다.
> - querydsl로 객체를 조회하면 해당 객체는 영속성 컨텍스트에 `영속` 상태로 남고 연관관계가 있는 객체는 `지연로딩`과 `dirty checking`이 가능해진다.
> - 지연로딩을 불가능하게 하기 위해서 `Projection`을 사용했고 `dto`로 값을 받와오면서 문제를 **해결**했다.




### 문제2
> ### 내용
> `@ManyToMany`관계의 관심 태그 추가 시 태그 <U>연관관계가 삭제된 후 다시 등록되는</U> 문제가 발생했다.  
> Dynamic Array의 `Resize` 과정과 동일했다. 
> 순서가 보장되지않는 `Set`자료 구조를 사용했을 때 문제가 발생하지 않는 것을  
> 보면`ArrayList`의 자료구조 특징 때문인것으로 추측된다.
> - 회원의 관심 태그가 6개가 있을 때 1개를 추가하면 6개가 모두 삭제된 후 7개가 등록된다.
> - <U>기능상 문제는 없다</U>.
> ### 해결방법(해결X)
>- `N+1` 문제다. `Resize` 과정이 없는 `LinkedList`를 사용해보려 했으나 `JPA`에서 지원하지 않는다.
>- 태그를 추가하는 빈도는 태그를 조회하는 빈도에 비해서 매우 적기 때문에 기능을 유지하되, 태그 추가를 `10개로 제한`하였다.
>- 수정할 시간이 있다면 `@ManyToMany` 연관관계를 `@ManyToOne` `@OneToMany` 로 매핑 테이블을 만들어서 풀어낼 것이다.
>- 또한 `@ManyToMany`매핑 방식은 확장성이 많이 떨어진다.





### 문제3
> ### 내용
> 채팅을 구현하면서 웹 소켓 통신을 사용했다. `sender`, `receiver`, `content` 를 필드 값으로 사용했는데   
> 채팅 내용을 저장하고 조회하는데 쿼리문이 복잡해지는 문제가 발생했다.
> - A 회원이 B와 채팅한 내용을 불러오려면 sender = A, receiver = B 또는  
>   sender = B, receiver = A인 채팅내용을 불러와야 했다.
> ### 해결방법
>- `채팅방` 필드를 추가해서 채팅방에 대화 내용을 저장하고, `채팅방`을 이용해서 대화 내용을 조회하게 만들었다.
>- 쿼리문이 상당히 단순해졌다.




### 문제4
> ### 내용
> 상품에 옵션(예: 색상)이 있을 때 옵션의 종류와 항목들 그리고 이 옵션이 필수 옵션인지도
> 선택할 수 있게 만들었다. 다음과 같은 로직의 추가가 필요했다.
>- 필수 옵션을 선택하지 않으면 주문이 실패해야 한다.
>- 옵션으로 인해서 주문이 실패할 경우 사용자에게 해결 방법이 담긴 경고 메시지를 띄워야 한다.
> 
> ### 해결방법
>- `종류`를 없애고 `항목`들로만 채워서 옵션으로 인한 주문 실패 경우를 없앴다.
>- 복잡한 로직들을 많이 삭제할 수 있었고 기능이 단순해졌다.
>> 예)  
> 색상: 빨강, 노랑, 파랑 --> 빨강, 노랑, 파랑
> #### 추가 : DB에서 option_title, option_content 테이블이라는 이름이 붙은 이유이다.




### 문제5
> ### 내용
> 테스트 코드 init data 생성 시 복잡한 연관관계의 데이터들은 만들기 어려웠다.  
> 예) 배송 객체를 만들기 위해서는 주문 객체가 필요하고, 주문 객체를 만들기 위해서는 회원 객체가 필요하다.
> ### 해결방법
>- 회원, 상품, 주문, 결제에 대한 생성을 `static` 메서드로 정리해놓았다.
> ``` JAVA
> // 메서드 예시
> @BeforeEach
> void init() {
>     Account account = TestUtils.createAccount();
>     Order order = TestUtils.createOrder(account);
>     Delivery delivery = TestUtils.createDelivery(order); 
> }
> ```



### 이 외의 트러블 슈팅 방법
>- 예외가 발생하면 예외 `Class`와 `message`를 읽어보고 빠르게 해결되지 않는다면
   `cause by` 주변을 읽어서 문제가 될만한 지점들을 수정한다.
>- `정지점`을 찍어놓고 `디버깅`을 실행한다. 언제 쿼리가 나가고, 객체에 값이 있는지 확인한다.
>- Chrome `개발자도구 콘솔`에 값을 찍어본다.
>- **한 번에 여러 계층의 코드를 수정하지 않는다**. 여러 계층의 코드를 수정하면 오류 발생 시 오류의 원인을 특정하기 어려워진다.


### 간단한 쿼리 튜닝
- querydsl에서 paging을 위한 count 쿼리를 작성할 때 "조회한 튜플의 개수" 를 구하는 것에서 "count(PK)의 개수" 로 변경하였다.

``` JAVA
// 변경 전
queryFactory.selectFrom(item)
            .leftJoin(item.tags, tag)
            .where( ... )
            .fetch().size();
// 변경 후
queryFactory.select(item.id.count())
            .from(item)
            .leftJoin(item.tags, tag)
            .where( ... )
            .fetchOne();
```
