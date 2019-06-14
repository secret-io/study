# 아이템4:인스턴스화를 막으려거든 private 생성자를 사용하라
1.  추상 클래스를 만드는 것으로 인스턴스화를 막을 수 없다.(아이템19)
2. private 생성자를 추가하면 클래스의 인스턴스활를 막을 수 있다.
---
##### 1.
하위 클래스를 만들어 인스턴스화 하면 그만이다.

```java
public abstract class Figure implements FigureMessage , FigureProperty{
    private List<Point> points;

    public Figure(List<Point> points) {
        this.points = points;
    }
```


```java
 public class Line extends Figure {
    public Line(List<Point> points) {
        super(points);
    }   

```



## 참고 
###### 상속을 금지하려면  (129p 참고)
* 클래스를 *final* 로 선언한다. 
* 생성자 모두를 외부에서 접근할 수 없도록 만들면 된다.




##### 2.
```java
public class UtilClass {

    private UtilClass() {
       throw new AssertionError();
    }

    public static void main(String[] args) {

    }

}
```


이 코드는 어떤 환경에서도 클래스가 인스턴스화 되는 것을 막아 준다.

꼭 AssertionError를 던질 필요는 없지만, 클래스 안에서 실수로라도 생성자를 호출하지 않도록 막아준다.

이 방식은 상속을 불가능하게 하는 효과도 있다.
> 모든 생성자는 상위 클래스의 생성자를 호출한다.

근데 생성자 private으로 했으니 하위 클래스가 상위 클래스의 생성자에 접근할 길이 막혀버린것 이다.
