package com.milkywaylhy.ex32thread;




import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView tv;
    int num=0;

    Handler handler= new Handler(){
        //이 Handler 객체에게 sendEmptyMessage()를 통해
        //메세지가 MainThread에게 전달되면 자동을 발동하는 메소드
        @Override
        public void handleMessage(@NonNull Message msg) {
            //이 영역안에서는 UI 변경작업 가능
            tv.setText(num+"");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv= findViewById(R.id.tv);
    }

    public void clickBtn(View view) {
        //오래 걸리는 작업 [ ex. 네트워크작업, 파일IO 작업 등..]
        //별도의 Thread를 사용하지 않았으므로 Main Thread가 처리함
        for(int i=0; i<20; i++){

            num++;
            tv.setText(num+"");

            //0.5초간 대기
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void clickBtn2(View view) {
        //오래 걸리는 작업수행...
        //하는 직원객체(MyThread) 생성 및 실행
        MyThread t= new MyThread();
        t.start(); //자동 run()메소드가 발동!!
    }

    //inner class [아웃터클래스의 멤버를 내것인양 사용가능]
    class MyThread extends Thread{
        // Main Thread와 별도로 동작하는 코드는
        //이 run()메소드 안에 작성해야 만 함.
        @Override
        public void run() {
            //오래 걸리는 작업..[ex. network 작업]
            for(int i=0; i<20; i++){
                num++;

                //화면에 num값 출력
                //tv.setText(num+""); //error
                //UI변경작업은 반드시
                //UI Thread(Main Thread)만이 수행가능

                // Main Thread에게 UI 변경작업 요청!!

                // 방법1. Handler 를 이용하는 방법
                handler.sendEmptyMessage(0);
                // 방법2. Activity 클래스의 메소드인
                //runOnUiThread 라는 메소드 이용
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       //이 영역안에서는 UI변경 가능
                       //메인 Thread능력을 위임 받은 것임
                       tv.setText(num+"");
                   }
               });

                //0.5초 대기
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}//MainActivity class..
      