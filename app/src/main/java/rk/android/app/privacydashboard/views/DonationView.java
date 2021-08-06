package rk.android.app.privacydashboard.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import rk.android.app.privacydashboard.R;
import rk.android.app.privacydashboard.util.Utils;

public class DonationView extends RelativeLayout {

    Context context;
    AttributeSet attrs;
    int styleAttr;

    TextView textTitle, textInfo;
    ImageView imageIcon;

    public DonationView(Context context) {
        super(context);
        this.context=context;
        initView();
    }
    public DonationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        this.attrs=attrs;
        initView();
    }
    public DonationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        this.attrs=attrs;
        this.styleAttr=defStyleAttr;
        initView();
    }

    private void initView() {

        inflate(context, R.layout.object_donation,this);
        TypedArray arr = context.obtainStyledAttributes(attrs,R.styleable.DonationView,
                styleAttr,0);

        imageIcon = findViewById(R.id.image_icon);
        textTitle = findViewById(R.id.text_title);
        textInfo = findViewById(R.id.text_info);

        setAttrsToView(arr);
        arr.recycle();
    }

    public void setAttrsToView(TypedArray arr){
        textTitle.setText(arr.getText(R.styleable.DonationView_donation_price));
        textInfo.setText(arr.getText(R.styleable.DonationView_donation_info));
        imageIcon.setImageDrawable(arr.getDrawable(R.styleable.DonationView_donation_icon));
        imageIcon.setImageTintList(ColorStateList.valueOf(arr.getColor(R.styleable.DonationView_donation_color, Utils.getAttrColor(context,R.attr.colorIcon))));
    }

    public void setPrice(String info) {
        this.textTitle.setText(info);
    }

}
