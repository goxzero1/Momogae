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
    AbandonedItem abandonedItem = null;
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
        myAsyncTask.execute();
        //AsyncTask로 비동기 방식으로 데이터 파싱
    }

    public class MyAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            requestUrl = "http://openapi.animal.go.kr/openapi/service/rest/abandonmentPublicSrvc/abandonmentPublic?numOfRows=500&serviceKey=" +dataKey;
            //url + 서비스키 + 검색할텍스트 일치 여부
            try {
                boolean popfile = false;
                boolean kindCd =false;
                boolean sexCd = false;
                boolean specialMark = false;
                boolean careNm = false;
                boolean careTel = false;


                URL url = new URL(requestUrl);
                InputStream is = url.openStream();
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser parser = factory.newPullParser();
                parser.setInput(new InputStreamReader(is, "UTF-8"));
                // XmlPullParser를 이용해 데이터 파싱 스트림리더로 한글형식 받아옴

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
                            if (parser.getName().equals("popfile")) popfile = true;
                            if (parser.getName().equals("kindCd")) kindCd = true;
                            if (parser.getName().equals("sexCd")) sexCd = true;
                            if (parser.getName().equals("specialMark")) specialMark = true;
                            if (parser.getName().equals("careNm")) careNm = true;
                            if (parser.getName().equals("careTel")) careTel = true;
                            break;
                        case XmlPullParser.TEXT:
                            //요소의 텍스트를 만났을때 반환값
                            if(popfile){
                                abandonedItem.setPopfile(parser.getText());
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
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    adapter = new AbandonedAdapter(getApplicationContext(), list);
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