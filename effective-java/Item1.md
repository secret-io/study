# 아이템1 : 생성자 대신 정적 팩터리 메서드를 고려하라

* 해당 클래스의 인스턴스를 반환하는 단순한 정적 메서드
* 전통적인 수단은 public 생성자이지만, 꼭 알아둬야 할 기법

---

### 장점
1. 이름을 가질 수 있다
2. 호출될 때마다 인스턴스를 새로 생성하지는 않아도 된다
3. 반환 타입의 하위 타입 객체를 반환할 수 있는 능력이 있다
4. 입력 매개변수에 따라 매번 다른 클래스의 객체를 반환할 수 있다
5. 정적 팩터리 메서드를 작성하는 시점에는 반환할 객체의 클래스가 존재하지 않아도 된다

### 단점
1. 상속을 하려면 public이나 protected 생성자가 필요하니 정적 팩터리 메서드만 제공하면 하위 클래스를 만들 수 없다
2. 정적 팩터리 메서드는 프로그래머가 찾기 어렵다

---

#### 1. 이름을 가질 수 있다

```java
public Person(String name) {
    this.name = name;
}
```
보단

```java
public Person withName(String name) {
    return new Person(name);
}
```
이름을 가지면 명시적이다

---

```java
public Person(String name) {
    this.name = name;
}

public Person(String address) {
    this.address = address);
}
```
불가능하지만

```java
public Person withName(String name) {
    return new Person(name);
}

public Person withAddress(String address) {
    Person person = new Person();
    person.address = address;
    return person;
}
```
가능해진다

#### 5. 정적 팩터리 메서드를 작성하는 시점에는 반환할 객체의 클래스가 존재하지 않아도 된다
서비스 제공자 프레임워크의 근간 e.g. JDBC(Java Database Connectivity)

클라이언트 - 서비스 접근 API - 서비스 인터페이스 - (서비스 제공자 인터페이스) - 제공자 등록 API - 제공자

**JDBC**
* Connection - 서비스 인터페이스
* DriverManager.registerDriver - 제공자 등록 API
* DriverManager.getConnection - 서비스 접근 API
* Driver - 서비스 제공자 인터페이스

---

### 정리
정적 팩터리 메서드와 public 생성자는 각자의 쓰임새에 맞게 이해하고 쓰자  
보통 정적 팩터리 메서드가 유리한 경우가 더 많으므로, 무의식적으로 public 생성자를 제공하는 습관은 고쳐보자