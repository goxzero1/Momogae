package com.example.momogae.Abandoned;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.momogae.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class AbandonedActivity extends AppCompatActivity {

    public String dataKey ="95zOfUKYctzSJ7y9tqQq6QGtBjTeOfIf9aJOcbGtdctl4q2d3iB77Ov5WRkqERsaabfkluvGV%2B7U4BcPMmfN3A%3D%3D";
    //공공데이터에서 받은 서비스 키
    private String requestUrl;
    ArrayList<AbandonedItem> list = null;
    AbandonedItem abandonedItem = null; //데이터를 받아올 리스트 초기화
    RecyclerView recyclerView;
    AbandonedAdapter adapter;
    TextView no_result;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abandoned);

        no_result = findViewById(R.id.no_result);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        //리사이클러뷰의 사이즈 고정

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);


        MyAsyncTask myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute(); //Asynctask 수행 시작 - background에서는 데이터 파싱이 이루어짐
    }

    public class MyAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {  //background에서 데이터 파싱이 이루어지는 코드

            requestUrl = "http://openapi.animal.go.kr/openapi/service/rest/" +
                    "abandonmentPublicSrvc/abandonmentPublic?numOfRows=500&serviceKey=" +dataKey;
            //공공데이터포털 사이트에서 신청했던 api키와 정보를 제공해주는 URL을 연결함
            try {
                boolean popfile = false; //유기동물 사진
                boolean kindCd =false; // 품종
                boolean sexCd = false; //성별
                boolean specialMark = false; // 특징
                boolean careNm = false; //보호소 이름
                boolean careTel = false; //보호소 전화번호 , 이 정보는 공공데이터포털에서 정해준 데이터 변수


                URL url = new URL(requestUrl);
                InputStream is = url.openStream(); //url 주소를 넣어 그 안의 주소를 읽어옴
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser parser = factory.newPullParser();
                parser.setInput(new InputStreamReader(is, "UTF-8"));
                // XmlPullParser를 이용해 데이터를 파싱하고 데이터를 한글형식으로 받아옴

                String tag;
                int eventType = parser.getEventType();

                while(eventType != XmlPullParser.END_DOCUMENT){
                    //반복문을 통한 XML파일의 끝에 도달했을때 반환값
                    switch (eventType){
                        case XmlPullParser.START_DOCUMENT:
                            //XML 파일의 맨 처음의 반환값
                            list = new ArrayList<AbandonedItem>();
                            break;
                        case XmlPullParser.END_DOCUMENT:
                            break;
                        case XmlPullParser.END_TAG:
                            //요소의 종료태그를 만났을때 반환값
                            if(parser.getName().equals("item") && abandonedItem != null) {
                                list.add(abandonedItem);
                            }
                            break;
                        case XmlPullParser.START_TAG:
                            //요소의 처음태그를 만났을때 반환값
                            if(parser.getName().equals("item")){
                                abandonedItem = new AbandonedItem();
                            }
                            if (parser.getName().equals("popfile")) popfile = true; //일치할시 접근가능
                            if (parser.getName().equals("kindCd")) kindCd = true;
                            if (parser.getName().equals("sexCd")) sexCd = true;
                            if (parser.getName().equals("specialMark")) specialMark = true;
                            if (parser.getName().equals("careNm")) careNm = true;
                            if (parser.getName().equals("careTel")) careTel = true;
                            break;
                        case XmlPullParser.TEXT:
                            //파서가 내용에 접근했을때 반환값
                            if(popfile){
                                abandonedItem.setPopfile(parser.getText()); //true일때 내용을 저장함
                                popfile = false;
                            } else if(kindCd) {
                                abandonedItem.setKindCd(parser.getText());
                                kindCd = false;
                            } else if (sexCd) {
                                abandonedItem.setSexCd(parser.getText());
                                sexCd = false;
                            } else if (specialMark) {
                                abandonedItem.setSpecialMark(parser.getText());
                                specialMark = false;
                            } else if (careNm) {
                                abandonedItem.setCareNm(parser.getText());
                                careNm = false;
                            } else if (careTel) {
                                abandonedItem.setCareTel(parser.getText());
                                careTel = false;
                            }
                            break;
                    }
                    eventType = parser.next();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            //AsyncTask의 결과값을 리사이클러뷰에 저장
            super.onPostExecute(s);
            dialog= new ProgressDialog(AbandonedActivity.this);
            dialog.setTitle("검색중");
            dialog.setMessage("유기동물 검색중 입니다..");
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);  // progress dialog 설정
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    adapter = new AbandonedAdapter(getApplicationContext(), list); //정보를 어댑터와 연결
                    recyclerView.setAdapter(adapter);
                    dialog.dismiss();
                    dialog = null;
                }
            };
            handler.sendEmptyMessageDelayed(0, 3000);
        }
    }



    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}