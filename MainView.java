package com.example.k.carapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainView extends AppCompatActivity {

    int MAX_PAGE=3;
    Fragment cur_fragment=new Fragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_view);

        ViewPager viewPager=(ViewPager)findViewById(R.id.viewpager);        //Viewpager 선언 및 초기화
        viewPager.setAdapter(new adapter(getSupportFragmentManager()));     //선언한 viewpager에 adapter를 연결
    }
    private class adapter extends FragmentPagerAdapter {                    //adapter클래스
        public adapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position<0 || MAX_PAGE<=position)        //가리키는 페이지가 0 이하거나 MAX_PAGE보다 많을 시 null로 리턴
                return null;
            switch (position){              //포지션에 맞는 Fragment찾아서 cur_fragment변수에 대입
                case 0:
                    cur_fragment=new page_1();
                    break;

                case 1:
                    cur_fragment=new page_2();
                    break;

                case 2:
                    cur_fragment=new page_3();
                    break;
            }

            return cur_fragment;
        }

        @Override
        public int getCount() {
            return MAX_PAGE;
        }

    }
}
