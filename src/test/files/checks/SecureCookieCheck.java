import javax.servlet.http.Cookie;
import java.net.HttpCookie;

class A {
  HttpCookie cookie1 = new HttpCookie("name", "value");

  void foo(Cookie cookie) {
    int age = cookie.getMaxAge();
  }

  void bar() {
    HttpCookie cookie = new HttpCookie("name", "value");
    cookie.setSecure(true);
  }
  void baz() {
    HttpCookie cookie = new HttpCookie("name", "value"); // Noncompliant {{}}
  }
  void qix() {
    HttpCookie cookie = new HttpCookie("name", "value"); // Noncompliant {{}}
    cookie.setSecure(false);
  }

}