# 아이템23 : 태그 달린 클래스보다는 클래스 계층구조를 활용

#### 태그 달린 클래스 예시
```java 
class Figure {
    enum Shape {RECTANGLE, CIRCLE};
    
    // 태그 필드 - 현재 모양을 나타냄
    final Shape shape;

    // 사각형 전용 필드
    double length;
    double width;

    // 원 전용 필드
    dobule radius;

    //사각형용 생성자
    Figure(double length, double width) {
        shape = Shape.RECTANGLE;
        this.length = length;
        this.width = width;
    }

    //원용 생성자
    Figure (double radius) {
        shape = Shape.CIRCLE;
        this.radius = radius;
    }
    
    double area() {
        switch(Shape) {
            case RECTANGLE:
                return length * width;
            case CIRCLE:
                return Math.PI * (radius * radius);
            default:
                throw new AssertionError(shape);
        }
    }
}
```

태그 달린 클래스 단점

* 여러 구현이 한 클래스에 혼합돼 있어 가독성이 나쁘다.
* 필드들을 final로 선언하려면 쓰이지 않는 필드까지 생성자에서 초기화 해야한다.
    * ~~태그가 클래스의 정체성을 나타내서 변하면 안되므로 태그를 final을 쓴것같다.~~
* 또 다른 의미를 추가하려면 코드를 수정해야 한다.
* 인스턴스 타입만으로 현재 나타내는 의미를 알길이 없다.

> 태그 달린 클래스는 장황하고, 오류를 내기 쉽고, 비효율적이다.

---

클래스를 계층구조로 바꾸는것으로 대그 달린 클래스의 문제를 해결할 수 있다.  
태그 달린 클래스를 계층구조로 바꾸는 방법을 알아보자.
1. 계층구조의 루트(root)가 될 추상클래스를 정의하고, 태그 값에 따라 동작이 달라지는 메서드들을 루트 클래스의 추상 메서드로 선언한다.
2. 태그 값에 상관없이 동작이 일정한 메서드들을 루트 클래스의 일반 메서드로 추가한다.  
    2-1. 모든 하위 클래스에서 공통으로 사용하는 데이터 필드들도 전부 루트 클래스로 올린다.
3. 루트 클래스를 확장한 구체 클래스를 의미별로 하나씩 정의한다.

다음과 같이 계층구조 클래스를 나타낼 수 있다.

```java
abstract class Figure {
    abstract double area();
}
```
```java
class Circle extends Figure {
    final double radius;

    Circle(double radius) {
        this.radius = radius;
    }

    @Override
    double area() {
        return Math.PI * (radius * radius);
    }
}
```
```java
class Rectangle extends Figure {
    final double length;
    final double width;

    Rectangle(double length, double width) {
        this.length = length;
        this.width = width;
    }

    @Override
    double area() {
        return length * width;
    }
}
```  
클래스 계층구조는 태그 달린 클래스의 단점을 모두 날려버렸다. 간결하고 명확하다.  
  각 클래스의 생성자가 모든 필드를 남김없이 초기화하고 추상 메서드를 모두 구현 했는지 컴파일러가 확인해준다.  
  독립적으로 계층구조를 확장하고 함께 사용할 수 있다.  
  타입이 의미별로 따로 존재한다.  
  만약 정사각형이 추가가 되었다면 아주 간단하게 반영 할 수 있다.
  ```java
 class Square extends Rectangle {
     Square(double side) {
         super(side, side);
     }
 }
 ```
 ---
 > 요약
 ## 일반적으로 태그 달린 필드가 등장한다면 없애고 계층구조로 대체 해보자.
