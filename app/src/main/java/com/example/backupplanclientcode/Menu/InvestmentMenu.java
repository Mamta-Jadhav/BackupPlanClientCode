package com.example.backupplanclientcode.Menu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.backupplanclientcode.Asyntask.GeneralTask;
import com.example.backupplanclientcode.Asyntask.GeneralTask.ResponseListener_General;
import com.example.backupplanclientcode.Asyntask.SaveProfileAsytask;
import com.example.backupplanclientcode.Asyntask.SaveProfileAsytask.ResponseListerProfile;
import com.example.backupplanclientcode.Bugsense.Bugsense;
import com.example.backupplanclientcode.ConnectionDetector;
import com.example.backupplanclientcode.Constant.Constant;
import com.example.backupplanclientcode.LogOutTimerUtil;
import com.example.backupplanclientcode.Preference.SettingPreference;
import com.example.backupplanclientcode.R;
import com.example.backupplanclientcode.ServiceUrl.ServiceUrl;
import com.example.backupplanclientcode.Utility.CompressImage;
import com.example.backupplanclientcode.loginActivity;
import com.google.gson.JsonObject;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import static com.example.backupplanclientcode.LogOutTimerUtil.foreGround;
import static com.example.backupplanclientcode.LogOutTimerUtil.logout;

public class InvestmentMenu extends Activity implements OnClickListener, ResponseListerProfile, ResponseListener_General, LogOutTimerUtil.LogOutListener {
    private static final int SELECT_PICTURE = 1;
    TextView actionBarTittle;
    ImageView addIcon;
    Button btn_back;
    Button btn_save;
    CompressImage compress;
    ConnectionDetector connection;
    ImageView currentImageVew = null;
    String delete_investment = "";
    LinearLayout layout_investment;
    SettingPreference pref;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_investment);
        this.pref = new SettingPreference(getApplicationContext());
        this.compress = new CompressImage(getApplicationContext());
        this.connection = new ConnectionDetector(getApplicationContext());
        new Bugsense().startBugsense(getApplicationContext());
        findViewId();
        checkAlreadySaveinvestment();
    }

    private void checkAlreadySaveinvestment() {
        if (this.pref.getStringValue(Constant.investmentFlag, "").equalsIgnoreCase("1")) {
            this.btn_save.setText("Edit");
            if (!this.pref.getBooleanValue(Constant.isGuestLogin, false)) {
                this.actionBarTittle.setText("Edit " + getResources().getString(R.string.menu_investments));
            }
            if (this.connection.isConnectingToInternet()) {
                try {
                    JSONObject nameValuePair = new JSONObject();
                    nameValuePair.put("user_id", "23");//this.pref.getStringValue(Constant.user_id, ""));
                    nameValuePair.put("token", this.pref.getStringValue(Constant.jwttoken, ""));
                    new GeneralTask(this, ServiceUrl.get_investment_detail, nameValuePair, 1, "post").execute(new Void[0]);
                } catch (Exception e) {
                }
                return;
            }
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.connectionFailMessage), Toast.LENGTH_SHORT).show();
            return;
        }
        this.actionBarTittle.setText(getResources().getString(R.string.menu_investments));
        addInvestmentLayout();
    }

    private void findViewId() {
        this.layout_investment = (LinearLayout) findViewById(R.id.layout_investment);
        this.btn_save = (Button) findViewById(R.id.btn_save);
        this.btn_back = (Button) findViewById(R.id.btn_back);
        this.addIcon = (ImageView) findViewById(R.id.addIcon);
        this.actionBarTittle = (TextView) findViewById(R.id.actionBarTittle);
        this.btn_save.setOnClickListener(this);
        this.btn_back.setOnClickListener(this);
        this.addIcon.setOnClickListener(this);
        if (this.pref.getBooleanValue(Constant.isGuestLogin, false)) {
            setControlEnable();
        }
    }

    private void setControlEnable() {
        this.actionBarTittle.setText(getResources().getString(R.string.menu_investments));
        this.btn_save.setVisibility(View.GONE);
        this.addIcon.setEnabled(false);
    }

    @SuppressLint({"InflateParams"})
    private void addInvestmentLayout() {
        final View walletView = LayoutInflater.from(this).inflate(R.layout.layout_investment, null);
        ImageView removeIcon = (ImageView) walletView.findViewById(R.id.removeIcon);
        removeIcon.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                InvestmentMenu.this.layout_investment.removeView(walletView);
            }
        });
        final ImageView imgInvestment = (ImageView) walletView.findViewById(R.id.imgInvestment);
        imgInvestment.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                InvestmentMenu.this.currentImageVew = imgInvestment;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction("android.intent.action.GET_CONTENT");
                InvestmentMenu.this.startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            }
        });
        EditText edit_investment_id = (EditText) walletView.findViewById(R.id.edit_investment_id);
        EditText edit_name = (EditText) walletView.findViewById(R.id.edit_name);
        EditText edit_typeOfInvest = (EditText) walletView.findViewById(R.id.edit_typeOfInvest);
        EditText edit_Company = (EditText) walletView.findViewById(R.id.edit_Company);
        EditText edit_OwnerRegister = (EditText) walletView.findViewById(R.id.edit_OwnerRegister);
        EditText edit_Benificery = (EditText) walletView.findViewById(R.id.edit_Benificery);
        EditText edit_yearPurchase = (EditText) walletView.findViewById(R.id.edit_yearPurchase);
        if (this.pref.getBooleanValue(Constant.isGuestLogin, false)) {
            imgInvestment.setEnabled(false);
            removeIcon.setVisibility(View.GONE);
            edit_investment_id.setEnabled(false);
            edit_name.setEnabled(false);
            edit_typeOfInvest.setEnabled(false);
            edit_Company.setEnabled(false);
            edit_OwnerRegister.setEnabled(false);
            edit_Benificery.setEnabled(false);
            edit_yearPurchase.setEnabled(false);
        }
        this.layout_investment.addView(walletView);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save /*2131558589*/:
                PrepareJson();
                return;
            case R.id.btn_back /*2131558590*/:
                finish();
                return;
            case R.id.addIcon /*2131558955*/:
                addInvestmentLayout();
                return;
            default:
                return;
        }
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1 && requestCode == 1) {
            Uri selectedImageUri = data.getData();
            String[] filePathColumn = {"_data"};
            Cursor cursor = getContentResolver().query(selectedImageUri, filePathColumn, null, null, null);
            cursor.moveToFirst();
            String picturePath = cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
            cursor.close();
            this.currentImageVew.setImageBitmap(this.compress.compressImage(selectedImageUri.toString(), picturePath));
            this.currentImageVew.setContentDescription(picturePath.toString());
        }
    }

    private void PrepareJson() {
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

            MultipartEntity entity = new MultipartEntity();//HttpMultipartMode.BROWSER_COMPATIBLE);
            JSONArray jsonArray = new JSONArray();
            JSONObject investmentJson = new JSONObject();
            investmentJson.put("user_id", "23");//this.pref.getStringValue(Constant.user_id, ""));
            investmentJson.put("delete_investment", this.delete_investment);
            for (int i = 0; i < this.layout_investment.getChildCount(); i++) {
                ViewGroup view = (ViewGroup) this.layout_investment.getChildAt(i);
                EditText edit_investment_id = (EditText) view.findViewById(R.id.edit_investment_id);
                EditText edit_name = (EditText) view.findViewById(R.id.edit_name);
                EditText edit_typeOfInvest = (EditText) view.findViewById(R.id.edit_typeOfInvest);
                EditText edit_Company = (EditText) view.findViewById(R.id.edit_Company);
                EditText edit_OwnerRegister = (EditText) view.findViewById(R.id.edit_OwnerRegister);
                EditText edit_Benificery = (EditText) view.findViewById(R.id.edit_Benificery);
                EditText edit_yearPurchase = (EditText) view.findViewById(R.id.edit_yearPurchase);
                ImageView imgInvestment = (ImageView) view.findViewById(R.id.imgInvestment);
                JSONObject json = new JSONObject();
                json.put("investment_id", edit_investment_id.getText().toString().trim());
                json.put("name", edit_name.getText().toString().trim());
                json.put("type", edit_typeOfInvest.getText().toString().trim());
                json.put("company", edit_Company.getText().toString().trim());
                json.put("owner", edit_OwnerRegister.getText().toString().trim());
                json.put("benificial", edit_Benificery.getText().toString().trim());
                json.put("year_purchase", edit_yearPurchase.getText().toString().trim());
                if (!imgInvestment.getContentDescription().toString().trim().isEmpty()) {
                    json.put("photo", "photo" + i);
                    entity.addPart("photo" + i, new FileBody(new File(imgInvestment.getContentDescription().toString()), "image/jpeg"));
                    nameValuePairs.add(new BasicNameValuePair("photo" + i, new FileBody(new File(imgInvestment.getContentDescription().toString()), "image/jpeg").getFilename()));
                    Log.i("imgInvestment", imgInvestment.getContentDescription().toString());
                } else {
                    json.put("photo", "");
                }
                jsonArray.put(json);
            }
            investmentJson.put("investment", jsonArray);
            JSONObject investment_data = new JSONObject();
            investment_data.put("investment_data", investmentJson);
            Log.i("send json", investment_data.toString());
            if (!this.connection.isConnectingToInternet()) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.connectionFailMessage), Toast.LENGTH_SHORT).show();
            } else if (this.btn_save.getText().toString().trim().equalsIgnoreCase("edit")) {
                nameValuePairs.add(new BasicNameValuePair("json_data", investment_data.toString()));
                entity.addPart("json_data", new StringBody(investment_data.toString()));
                SaveProfileAsytask saveProfileAsytask = new SaveProfileAsytask(this, ServiceUrl.edit_investment, nameValuePairs);
                saveProfileAsytask.execute(new Void[0]);
            } else {
                nameValuePairs.add(new BasicNameValuePair("json_data", investment_data.toString()));
                SaveProfileAsytask saveProfileAsytask2 = new SaveProfileAsytask(this, ServiceUrl.save_investment, nameValuePairs);
                saveProfileAsytask2.execute(new Void[0]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void on_ProfileSuccess(JSONObject response) {
        try {
            if (response.has("message")) {
                Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
            }
            if (response.has("success")) {
                this.pref.setStringValue(Constant.investmentFlag, response.getString("success"));
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void on_GeneralSuccess(JSONObject response, int responseCode) {
        if (response == null) {
            addInvestmentLayout();
        } else {
            show_investment_detail(response);
        }
    }

    @SuppressLint({"InflateParams"})
    private void show_investment_detail(JSONObject response) {
        try {
            JSONArray investmentArray = response.getJSONObject("investment").getJSONArray("investment");
            for (int i = 0; i < investmentArray.length(); i++) {
                JSONObject json = investmentArray.getJSONObject(i);
                final View investment = LayoutInflater.from(this).inflate(R.layout.layout_investment, null);
                EditText edit_investment_id = (EditText) investment.findViewById(R.id.edit_investment_id);
                EditText edit_name = (EditText) investment.findViewById(R.id.edit_name);
                EditText edit_typeOfInvest = (EditText) investment.findViewById(R.id.edit_typeOfInvest);
                EditText edit_Company = (EditText) investment.findViewById(R.id.edit_Company);
                EditText edit_OwnerRegister = (EditText) investment.findViewById(R.id.edit_OwnerRegister);
                EditText edit_Benificery = (EditText) investment.findViewById(R.id.edit_Benificery);
                EditText edit_yearPurchase = (EditText) investment.findViewById(R.id.edit_yearPurchase);
                if (json.has("investment_id")) {
                    edit_investment_id.setText(json.getString("investment_id"));
                }
                if (json.has("name")) {
                    edit_name.setText(json.getString("name"));
                }
                if (json.has("type")) {
                    edit_typeOfInvest.setText(json.getString("type"));
                }
                if (json.has("company")) {
                    edit_Company.setText(json.getString("company"));
                }
                if (json.has("owner")) {
                    edit_OwnerRegister.setText(json.getString("owner"));
                }
                if (json.has("benificial")) {
                    edit_Benificery.setText(json.getString("benificial"));
                }
                if (json.has("year_purchase")) {
                    edit_yearPurchase.setText(json.getString("year_purchase"));
                }
                ImageView removeIcon = (ImageView) investment.findViewById(R.id.removeIcon);
                if (json.has("investment_id")) {
                    removeIcon.setTag(json.getString("investment_id"));
                    final ImageView imageView = removeIcon;
                    removeIcon.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            InvestmentMenu.this.layout_investment.removeView(investment);
                            if (InvestmentMenu.this.delete_investment.equalsIgnoreCase("")) {
                                InvestmentMenu.this.delete_investment = InvestmentMenu.this.delete_investment.concat(imageView.getTag().toString());
                                return;
                            }
                            InvestmentMenu.this.delete_investment = InvestmentMenu.this.delete_investment.concat("," + imageView.getTag().toString());
                        }
                    });
                }
                final ImageView imgInvestment = (ImageView) investment.findViewById(R.id.imgInvestment);
                if (json.has("photo")) {
                    UrlImageViewHelper.setUrlDrawable(imgInvestment, json.getString("photo").toString().trim(), (int) R.drawable.img);
                    imgInvestment.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            InvestmentMenu.this.currentImageVew = imgInvestment;
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction("android.intent.action.GET_CONTENT");
                            InvestmentMenu.this.startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
                        }
                    });
                }
                if (this.pref.getBooleanValue(Constant.isGuestLogin, false)) {
                    imgInvestment.setEnabled(false);
                    removeIcon.setVisibility(View.GONE);
                    edit_name.setEnabled(false);
                    edit_typeOfInvest.setEnabled(false);
                    edit_Company.setEnabled(false);
                    edit_OwnerRegister.setEnabled(false);
                    edit_Benificery.setEnabled(false);
                    edit_yearPurchase.setEnabled(false);
                }
                this.layout_investment.addView(investment);
            }
        } catch (Exception e) {
            addInvestmentLayout();
            e.printStackTrace();
        }
    }

    @Override
    public void doLogout() {

        if(foreGround){

            pref.setBooleanValue(Constant.isLogin, false);
            pref.setBooleanValue(Constant.isGuestLogin, false);
            startActivity(new Intent(getApplicationContext(), loginActivity.class));
            finish();

        }else {
            logout = "true";
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        LogOutTimerUtil.startLogoutTimer(this, this);
        Log.e("TAG", "OnStart () &&& Starting timer");

        if(logout.equals("true")){

            logout = "false";

            //redirect user to login screen

            pref.setBooleanValue(Constant.isLogin, false);
            pref.setBooleanValue(Constant.isGuestLogin, false);
            startActivity(new Intent(getApplicationContext(), loginActivity.class));
            finish();
        }
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        LogOutTimerUtil.startLogoutTimer(this, this);
        Log.e("TAG", "User interacting with screen");
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.e("TAG", "onPause()");
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.e("TAG", "onResume()");

        if(logout.equals("true")){

            logout = "false";

            //redirect user to login screen
            pref.setBooleanValue(Constant.isLogin, false);
            pref.setBooleanValue(Constant.isGuestLogin, false);
            startActivity(new Intent(getApplicationContext(), loginActivity.class));
            finish();
        }
    }
}
